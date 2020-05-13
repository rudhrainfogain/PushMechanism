package com.fedex.common.cxs.exception;

/**
 * A retryable exception emitted from the processor layer of our application
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class TransientProcessorException extends ProcessorException {

    public TransientProcessorException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public TransientProcessorException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }

    public TransientProcessorException(final Throwable cause, final ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public TransientProcessorException(final String message, final Throwable cause, final ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public TransientProcessorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TransientProcessorException(final String message) {
        super(message);
    }

}
