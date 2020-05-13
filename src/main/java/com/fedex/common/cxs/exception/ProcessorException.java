package com.fedex.common.cxs.exception;

/**
 * Base exception type for exceptions emitted from a processor layer of the application
 */
@SuppressWarnings("WeakerAccess")
public class ProcessorException extends AbstractBaseException {

    private static final long serialVersionUID = 2L;

    public ProcessorException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public ProcessorException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ProcessorException(final Throwable cause, final ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public ProcessorException(final String message, final Throwable cause, final ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
    
    public ProcessorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ProcessorException(final String message) {
        super(message);
    }

    public ProcessorException set(final String name, final Object value) {
        return (ProcessorException) super.set(name, value);
    }

    public ProcessorException copyProperties(final AbstractBaseException caughtEx) {
        return (ProcessorException) super.copyProperties(caughtEx);
    }

    public ProcessorException setErrorCode(final ErrorCode errorCode) {
        super.setErrorCode(errorCode);
        return this;
    }

}
