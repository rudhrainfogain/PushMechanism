package com.fedex.common.cxs.exception;

import java.util.Map;

/**
 * Base exception type for exception types thrown from a service layer
 */
@SuppressWarnings("unused")
public class ServiceException extends AbstractBaseException {

    private static final long serialVersionUID = 2L;

    public ServiceException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public ServiceException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ServiceException(final Throwable cause, final ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public ServiceException(final String message, final Throwable cause, final ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public ServiceException(final String message) {
        super(message);
    }


    public ServiceException setErrorCode(final ErrorCode errorCode) {
        super.setErrorCode(errorCode);
        return this;
    }

    public ServiceException set(final String name, final Object value) {
        return (ServiceException) super.set(name, value);
    }

    public ServiceException set(final ErrorCode errorCode) {
        return (ServiceException) super.set(errorCode);
    }

    public ServiceException copyProperties(final AbstractBaseException caughtEx) {
        return (ServiceException) super.copyProperties(caughtEx);
    }

    public ServiceException set(final Map<String, Object> amap) {
        return (ServiceException) super.set(amap);
    }

}
