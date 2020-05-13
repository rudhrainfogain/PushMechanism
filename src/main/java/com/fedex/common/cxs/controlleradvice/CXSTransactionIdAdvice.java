package com.fedex.common.cxs.controlleradvice;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fedex.common.cxs.dto.CXSEnvelope;

/**
 * This controller advice is responsible for setting the <code>transactionId</code> in a
 * {@link CXSEnvelope} object, regardless of whether the request was a success or failure.
 *
 * With this logic here, developers no longer need to:
 * <ol>
 *     <li>Set a <code>transactionId</code> as part of a controller method</li>
 *     <li>Set a <code>transactionId</code> as part of error handling, including the error controller advice</li>
 * </ol>
 *
 * @author Ben Bays <ben.bays@projekt202.com>
 */
@ControllerAdvice
@SuppressWarnings({"NullableProblems", "unused"})
public class CXSTransactionIdAdvice implements ResponseBodyAdvice {

    private final static Logger logger = LoggerFactory.getLogger( CXSTransactionIdAdvice.class );

    /*
     * (non-javadoc)
     * Note:  There is no standard way to get a transaction id from a CXS request message.  The existing
     * swagger documentation found at https://gitlab.prod.fedex.com/APP3531615/fxoapi have no example of
     * a request message with a transaction id.  Looking at existing code, the only way observed is
     * through use of a X-transaction-id header.
     */
    private final static String TRANSACTION_ID_HEADER_NAME = "X-transaction-id";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(final MethodParameter returnType, final Class converterType) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object beforeBodyWrite(
            final Object body,
            final MethodParameter returnType,
            final MediaType selectedContentType,
            final Class selectedConverterType,
            final ServerHttpRequest request,
            final ServerHttpResponse response) {

        final List<String> transactionIdHeaderValues = request.getHeaders().get(TRANSACTION_ID_HEADER_NAME);

        if (null == transactionIdHeaderValues || transactionIdHeaderValues.isEmpty()) {
            return body;
        } else {
            if (transactionIdHeaderValues.size() > 1) {
                logger.warn("More than one header of name {} was received as part of request:\n{}",
                        TRANSACTION_ID_HEADER_NAME,
                        transactionIdHeaderValues);
            }

            final String transactionId = transactionIdHeaderValues.get(0);

            if (body instanceof CXSEnvelope) {
                CXSEnvelope envelopeResponse = (CXSEnvelope)body;
                envelopeResponse.setTransactionId(transactionId);
                return envelopeResponse;
            } else {
                logger.warn("A transaction id header was provided, but return type was not a CXSEnvelope");
            }

            return body;
        }
    }
}
