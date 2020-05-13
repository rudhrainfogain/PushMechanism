package com.fedex.common.cxs.exception;

/**
 * A non-retryable exception thrown from a repository layer of our application
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class TerminalDbAccessException extends DbAccessException {

    public TerminalDbAccessException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public TerminalDbAccessException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }

    public TerminalDbAccessException(final Throwable cause, final ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public TerminalDbAccessException(final String message, final Throwable cause, final ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public TerminalDbAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TerminalDbAccessException(final String message) {
        super(message);
    }

}
