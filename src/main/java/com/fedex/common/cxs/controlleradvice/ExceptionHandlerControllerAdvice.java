package com.fedex.common.cxs.controlleradvice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.metadata.ConstraintDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import brave.Span;
import brave.Tracer;
import brave.propagation.ExtraFieldPropagation;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import com.fedex.common.cxs.dto.CXSEnvelope;
import com.fedex.common.cxs.dto.CXSError;
import com.fedex.common.cxs.exception.AbstractBaseException;
import com.fedex.common.cxs.exception.InputValidationErrorCode;
import com.fedex.common.cxs.exception.InputValidationException;
import com.fedex.common.cxs.exception.TerminalProcessorException;
import com.fedex.common.cxs.exception.TransientProcessorException;
import com.fedex.common.cxs.validation.CXSErrorPayload;

/**
 * The controller advice serves as a global exception handler.  It will map java exception types to the
 * HTTP response message that are sent back to consumers
 */
@SuppressWarnings({"NullableProblems", "unused"})
@ControllerAdvice
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerControllerAdvice.class);

    private final Tracer tracer;
    private final ErrorResponseBuilder errorResponseBuilder;
    private final BeanValidationParser beanValidationParser;
    private final InputValidationExParser inputValidationExParser;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;

    @Autowired
    public ExceptionHandlerControllerAdvice(
            final BeanValidationParser beanValidationParser,
            final InputValidationExParser inputValidationExParser,
            final ErrorResponseBuilder errorResponseBuilder,
            final Tracer tracer,
            final AutowireCapableBeanFactory autowireCapableBeanFactory) {
        this.errorResponseBuilder = errorResponseBuilder;
        this.tracer = tracer;
        this.beanValidationParser = beanValidationParser;
        this.inputValidationExParser = inputValidationExParser;
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
    }


    //region Exception Handlers
    /**
     * While the {@link ExceptionHandler} methods below are great at handling exceptions that occur while our
     * own code is running, what about exceptions that occur before our code is executed?  Errors such as
     * authorization, deserialization, etc?  With CXS, we need to return error codes that occur within
     * the libraries before our code is ever touched.  This method provides that opportunity.  We can catch
     * Jackson exceptions (etc) and have them return exceptions that follow the CXSEnvelope format.
     *
     * @param ex The exception that was thrown (the original cause was almost certainly wrapped)
     * @param body The response body that would be returned if we didn't intercept it
     * @param headers The headers that would be returned if we don't change them
     * @param status The {@link HttpStatus} that would be returned if we don't change it
     * @param request The original request that was received
     * @return The {@link ResponseEntity} that will be returned to the client
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            final Exception ex,
            final Object body,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request) {


        if (ex instanceof HttpMessageNotReadableException) {

            CXSError cxsError = null;

            HttpMessageNotReadableException httpNotReadableEx = (HttpMessageNotReadableException)ex;

            if (httpNotReadableEx.getCause() instanceof UnrecognizedPropertyException) {
                // a property was included in the request bean that isn't defined on the bean we're deserializing to
                final UnrecognizedPropertyException upe = (UnrecognizedPropertyException) httpNotReadableEx.getCause();
                cxsError = errorResponseBuilder.buildUnrecognizedPropertyError(upe);
            } else if (httpNotReadableEx.getCause() instanceof JsonProcessingException) {
                // What we're after is a JsonParseException.  This is the exception that contains the detail
                // regarding what has gone wrong.  If the location of error is part of a child object on the
                // request, it is possible that the JsonParseException is wrapped inside of a JsonMappingException,
                // at which point we need to unwrap till we get to the JsonParseException.  Also of note is
                // that JsonParseException is used for many different error conditions, forcing us to rely on
                // String parsing of the exception message to determine the necessary detail of what actually happened.
                try {
                    // the original exception message (JsonProcessingException is base exception class)
                    final JsonProcessingException jsonProcessingEx =
                            (JsonProcessingException) httpNotReadableEx.getCause();

                    // recursively unwind the message (if needed) to find the JsonParseException
                    final JsonParseException jsonParseEx = getJsonParseException(jsonProcessingEx, 1);

                    // note that many errors in Jackson fall under a JsonParseException.  We need to do
                    // string parsing to map it to some of the more fine grained CXSError messages
                    if (jsonParseEx.getMessage().contains("Duplicate field")) {
                        cxsError = errorResponseBuilder.buildDuplicatePropertyError(jsonParseEx);
                    } else {
                        logger.info("Unknown JsonParseException encountered {}", jsonParseEx.getMessage());
                    }
                } catch (final IOException ioEx) {
                    logger.error("Failed to parse json request", ioEx);
                }
            }

            logger.info("Original exception received", ex);

            if (null  != cxsError) {
                return ResponseEntity.badRequest().body( CXSEnvelope.error(cxsError) );
            }

        }

        // We need to fall into an approved CXS Error message.  If we haven't found an appropriate mapping by this
        // point, we need to potentially drop detail and use a generic "something has gone wrong" error message.

        //noinspection unchecked
        return (ResponseEntity)buildTerminalError();
    }

    /**
     * Map an {@link ConstraintViolationException} to a {@link HttpStatus#BAD_REQUEST}
     * @param e The java bean validation {@link ConstraintViolationException} that was thrown
     * @param request The original request received
     *
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CXSEnvelope> constraintViolation(
            final ConstraintViolationException e,
            final HttpServletRequest request) {

        logErrorContext(e, request);

        final List<ConstraintViolation> violations = new ArrayList<>(e.getConstraintViolations());

        final List<CXSError> cxsErrors = new ArrayList<>();

        for (final ConstraintViolation violation : violations) {
            try {
                cxsErrors.add( getConstraintViolationError(violation) );
            } catch (final Exception ex) {
                // create a terminal response if we fail to parse the violation and build an input validation request
                logger.error("Failed to parse ConstraintViolation", ex);
                return buildTerminalError();
            }
        }

        return ResponseEntity.badRequest().body( CXSEnvelope.error(cxsErrors) );
    }

    /**
     * Map an {@link InputValidationException} to a {@link HttpStatus#BAD_REQUEST}
     * @param e The exception thrown
     * @param request The message that initiated the request
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(InputValidationException.class)
    public ResponseEntity<?> badInput(
            final InputValidationException e,
            final HttpServletRequest request) {

        logErrorContext(e, request);

        try {
            final ValidationErrorType errorType = inputValidationExParser.getErrorType(
                    (InputValidationErrorCode) e.getErrorCode());

            final PropertyPathNames properties = inputValidationExParser.getPropertyPathNames(e);

            final CXSError cxsError = errorResponseBuilder.buildInputValidationError(errorType, properties);

            return ResponseEntity.badRequest().body( CXSEnvelope.error(cxsError) );

        } catch (final Exception ex) {
            logger.error("Failed to parse InputValidationException", ex);
            return buildTerminalError();
        }
    }

    /**
     * Map a {@link TerminalProcessorException} to a {@link HttpStatus#INTERNAL_SERVER_ERROR}
     * @param e The exception thrown
     * @param request The message that initiated the request
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(TerminalProcessorException.class)
    public ResponseEntity<?> terminalProcessorError(
            final TerminalProcessorException e,
            final HttpServletRequest request) {
        logErrorContext(e, request);
        return buildTerminalError();
    }

    /**
     * Map a {@link TransientProcessorException} to an {@link HttpStatus#SERVICE_UNAVAILABLE}
     * @param e The exception thrown
     * @param request The message that initiated the request
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(TransientProcessorException.class)
    public ResponseEntity<?> transientProcessorError(
            final TransientProcessorException e,
            final HttpServletRequest request) {

        logErrorContext(e,request);
        return buildTransientError();
    }

    /**
     * If a more specific ExceptionHandler does not exist, this base method will generically map all remaining
     * exception types that extend {@link AbstractBaseException} to an {@link HttpStatus#INTERNAL_SERVER_ERROR}.
     * @param e The exception thrown
     * @param request The message that initiated the request
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(AbstractBaseException.class)
    public ResponseEntity<?> unknownBaseError(final AbstractBaseException e, final HttpServletRequest request) {

        logErrorContext(e,request);
        return buildTerminalError();
    }

    /**
     * Provide a safety net for common java.lang exceptions that may have been missed being map.  These will
     * be mapped to {@link HttpStatus#INTERNAL_SERVER_ERROR}.
     * @param e The exception thrown
     * @param request The message that initiated the request
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler({NullPointerException.class,IllegalArgumentException.class})
    public ResponseEntity<?> unknownTerminalError(final Exception e, final HttpServletRequest request) {

        logErrorContext(e,request);
        return buildTerminalError();
    }

    //end region

    /**
     * Convenience method to write the content of the exception to the logger
     * @param e The exception thrown by the application
     * @param request The original HTTP request message
     */
    private void logErrorContext(final Exception e, final HttpServletRequest request) {

        final Span span = tracer.currentSpan();
        final StringBuilder sb = new StringBuilder();

        try {

            if(span!=null) {
                final Map<String,String> extraMap = ExtraFieldPropagation.getAll(span.context());

                if(extraMap != null) {
                    for(final Map.Entry<String,String> entry : extraMap.entrySet()) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
                    }
                }

                // This was broken by Brave. Still searching for a replacement
                //for(Map.Entry<String,String> entry:span.tags().entrySet()) {
                //  sb.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
                //}
                if(sb.length()>0) {
                    sb.setLength(sb.length()-1);
                }
            }

        } catch(final Exception ex) {
            logger.warn("Caught unexpected exception logging error context", ex);
        }

        logger.error(
                "[{} {}] threw an exception. Context: [{}]",
                request.getMethod(),
                getPath(request),
                sb.toString(),
                e);
    }

    /**
     * Build a generic error to indicate that a retryable exception has occurred
     */
    private ResponseEntity<CXSEnvelope> buildTransientError() {
        return buildCxsError(HttpStatus.SERVICE_UNAVAILABLE, errorResponseBuilder.buildSystemUnavailableError());
    }

    /**
     * Build a generic error to indicate that a non-retryable exception has occurred
     */
    private ResponseEntity<CXSEnvelope> buildTerminalError() {
        return buildCxsError(HttpStatus.INTERNAL_SERVER_ERROR, errorResponseBuilder.buildInternalServerError());
    }

    /**
     * Helper method to construct a {@link CXSEnvelope} containing a [list of] errors
     */
    private ResponseEntity<CXSEnvelope> buildCxsError(final HttpStatus httpStatus, CXSError ... cxsErrors) {
        return buildCxsError(httpStatus, Arrays.asList(cxsErrors));
    }

    /**
     * Helper method to construct a {@link CXSEnvelope} containing a [list of] errors
     */
    private ResponseEntity<CXSEnvelope> buildCxsError(final HttpStatus httpStatus, List<CXSError> cxsErrors) {
        final CXSEnvelope cxs = CXSEnvelope.error(cxsErrors);
        return ResponseEntity.status(httpStatus).body(cxs);
    }


    /**
     * Determine what the {@link CXSError} should be given a {@link ConstraintViolation}.  In most cases, this is
     * derived based on the type of annotation that triggered the violation.  However, it is possible that the
     * triggering violation has included a payload of type {@link CXSErrorPayload}, in which case we would use
     * the code and message contained there as an override.  For some violations, such as a class level validation,
     * a payload is required, because we are otherwise unable to detect what the error should be.
     * @param violation The violation that caused the {@link ConstraintViolationException}
     * @return {@link CXSError} with properly populated code and message
     * @throws NoSuchFieldException Unable to find bean property by name
     */
    @SuppressWarnings("unchecked")
    private CXSError getConstraintViolationError(final ConstraintViolation violation) throws NoSuchFieldException {

        final ConstraintDescriptor descriptor = violation.getConstraintDescriptor();

        final ValidationErrorType errorType = beanValidationParser.getErrorType(violation);

        // If the violation included a javax.validation.Payload that implements CXSErrorPayload, then use
        // the code and message found there instead of assigning a code/value based on the type of
        // annotation.  Note that while this can serve as a mechanism to override the default code/message, it is
        // required for annotations where a code/message can be derived, such as AssertTrue & AssertFalse.
        if (null != descriptor) {

            final Set<Class> payloadClazzes = (Set<Class>)descriptor.getPayload();

            for (final Class payloadClazz : payloadClazzes) {

                if (CXSErrorPayload.class.isAssignableFrom(payloadClazz)) {

                    final CXSErrorPayload cxsErrorPayload =
                            (CXSErrorPayload)autowireCapableBeanFactory.createBean(payloadClazz);

                    return new CXSError(cxsErrorPayload.getCode(), cxsErrorPayload.getMessage());
                }
            }
        }

        final PropertyPathNames properties = beanValidationParser.getPropertyPathNames(violation);

        return errorResponseBuilder.buildInputValidationError(errorType, properties);
    }

    /**
     * Recursively walk the JsonProcessingException looking for the JsonParseException
     * @param jsonProcessingEx The exception that is wrapping our JsonParseException
     * @param currentDepth The current level of nesting
     * @throws IOException If an unknown exception type found in stream or max nesting depth achieved
     */
    private JsonParseException getJsonParseException(
            final JsonProcessingException jsonProcessingEx,
            final int currentDepth) throws IOException {

        if (jsonProcessingEx instanceof JsonParseException) {
            return (JsonParseException)jsonProcessingEx;
        } else if (jsonProcessingEx.getCause() instanceof JsonParseException) {
            return (JsonParseException) jsonProcessingEx.getCause();
        } else if (currentDepth > 20) {
            throw new IOException("Request message is too heavily nested to parse");
        } else if (jsonProcessingEx.getCause() instanceof JsonProcessingException) {
            return getJsonParseException((JsonProcessingException)jsonProcessingEx.getCause(), currentDepth + 1);
        } else {
            throw new IOException("Unexpected exception type discovered walking JSON Error");
        }
    }

    /**
     * Generate the original URL that was invoked, including any query parameters
     */
    private String getPath(final HttpServletRequest request) {
        StringBuilder reqUrl = new StringBuilder(request.getRequestURI());
        if (request.getQueryString() != null) {
            reqUrl.append("?").append(request.getQueryString());
        }
        return reqUrl.toString();
    }

}
