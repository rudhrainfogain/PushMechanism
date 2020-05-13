package com.fedex.common.cxs.exception;

/**
 * Base exception for all exceptions that occur in a repo layer.  A spring DataAccessException will typically be
 * mapped to a FedEx specific subtype that extends this exception.
 */
@SuppressWarnings("WeakerAccess")
public class DbAccessException extends AbstractBaseException {

    private static final long serialVersionUID = 2L;

    public DbAccessException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public DbAccessException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }

    public DbAccessException(final Throwable cause, final ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public DbAccessException(final String message, final Throwable cause, final ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public DbAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DbAccessException(final String message) {
        super(message);
    }

    public DbAccessException set(final String name, final Object value) {
        return (DbAccessException) super.set(name, value);
    }

    public DbAccessException copyProperties(final AbstractBaseException caughtEx) {
        return (DbAccessException) super.copyProperties(caughtEx);
    }

    public DbAccessException setErrorCode(final ErrorCode errorCode) {
        super.setErrorCode(errorCode);
        return this;
    }

}
