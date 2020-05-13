package com.fedex.common.cxs.exception;

/**
 * A retryable exception emitted from the repository layer of our application
 */
@SuppressWarnings("unused")
public class TransientDbAccessException extends DbAccessException {

    public TransientDbAccessException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public TransientDbAccessException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }

    public TransientDbAccessException(final Throwable cause, final ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public TransientDbAccessException(final String message, final Throwable cause, final ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public TransientDbAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TransientDbAccessException(final String message) {
        super(message);
    }

}
