package com.fedex.common.cxs.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Helper class containing utility methods for exceptions handling.  These method serve primarily to assist in
 * mapping from framework generated exception types ot layer specific ones.
 */
public class ExceptionsHelper {

    private final static Logger logger = LoggerFactory.getLogger(ExceptionsHelper.class);

    //region Http Exception Handlers

    /**
     * Map an {@link HttpClientErrorException} to a layer specific {@link TerminalDbAccessException} or
     * {@link TransientDbAccessException}.  The type of exception is selected based on the HTTP error code.
     * 4xx status codes are considered a terminal condition, while all other status are considered transient ones
     *
     * @param msg Additional text based content to add to the exception message
     * @param context The URL being invoked
     * @param ex The exception that was caught
     * @param code {@link ErrorCode} if known
     * @param ignore404 Set to true if 404 Http Status codes should be ignored, false to throw terminal exception.
     *
     * @throws TerminalDbAccessException An error has occurred that will not succeed if retried
     * @throws TransientDbAccessException An error has occurred that may succeed if retried
     */
    public static void handleHttpClientException(
            final String msg,
            final String context,
            final HttpClientErrorException ex,
            final ErrorCode code,
            final boolean ignore404) throws TerminalDbAccessException, TransientDbAccessException {

        final String formattedExMsg = generateHttpServerExMessage(msg, context, ex);

        if (ex.getStatusCode() == HttpStatus.NOT_FOUND && ignore404) {
            return;
        }

        throw (ex.getStatusCode().is4xxClientError()) ?
                new TerminalDbAccessException(formattedExMsg, ex, code) :
                new TransientDbAccessException(formattedExMsg, ex, code);
    }

    /**
     * Map an {@link HttpServerErrorException} to a {@link TransientDbAccessException}.  spring-web will only
     * use this exception class for 5xx status code messages, meaning that this method will only concern
     * itself with transient errors.  The exception message will take the form
     *
     * @param msg Additional text to add to the exception message
     * @param context The URL that was invoked
     * @param ex The {@link HttpServerErrorException} that was thrown
     * @param code Optional {@link ErrorCode}
     */
    public static void handleHttpServerException(
            final String msg,
            final String context,
            final HttpServerErrorException ex,
            final ErrorCode code) throws TransientDbAccessException {

        throw new TransientDbAccessException(
                generateHttpServerExMessage(msg, context, ex),
                ex,
                code);
    }

    //endregion

    //region FedExDataAccess to Processor

    /**
     * Map a FedEx {@link DbAccessException} to a FedEx variant of
     * {@link com.fedex.common.cxs.exception.ProcessorException}.  This method will automatically handle the mapping of
     * transient and terminal exceptions appropriately.
     *
     * @param ex The original {@link {@link DbAccessException }}
     */
    public static void handleFedExProcessorException(final DbAccessException ex)
            throws com.fedex.common.cxs.exception.ProcessorException {

        final ProcessorException newEx =  (ex instanceof TransientDbAccessException) ?
                new TransientProcessorException(ex.getMessage(), ex) :
                new TerminalProcessorException(ex.getMessage(), ex);

        logger.trace("Mapping {} to {}", ex.getClass(), newEx.getClass());

        throw newEx;
    }

    //endregion

    //region helper methods

    /**
     * Generate a formatted {@link String} to use as an exception message.  It will take the form:
     *
     * [<pre>context</pre>] <code>msg</code>: <code>ex.getMessage()</code>: <code>ex.getResponseBodyAsString()</code>
     *
     * @param msg Additional text to add to the exception message
     * @param context The URL being invoked
     * @param ex The original exception thrown
     */
    private static String generateHttpServerExMessage(
            final String msg,
            final String context,
            final HttpStatusCodeException ex) {

        return "[" + context + "] " + msg + ": " + ex.getMessage() + ": " + ex.getResponseBodyAsString();
    }

    //endregion
}