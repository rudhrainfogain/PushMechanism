package com.fedex.common.cxs.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class is base class for Exception handling.
 * This is an abstract class only.
 * So, please use the instance classes.
 *
 * @see "https://northconcepts.com/blog/2013/01/18/6-tips-to-improve-your-exception-handling/"
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractBaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    protected ErrorCode errorCode;
    private final Map<String, Object> properties = new TreeMap<>();

    protected AbstractBaseException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    protected AbstractBaseException(final String message, final ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected AbstractBaseException(final Throwable cause, final ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    protected AbstractBaseException(final String message, final Throwable cause, final ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    protected AbstractBaseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    protected AbstractBaseException(final String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public AbstractBaseException setErrorCode(final ErrorCode errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final String name) {
        return (T) properties.get(name);
    }

    public AbstractBaseException set(final String name, final Object value) {
        properties.put(name, value);
        return this;
    }

    public AbstractBaseException set(final Map<String, Object> amap) {
        properties.putAll(amap);
        return this;
    }

    public AbstractBaseException set(final ErrorCode errorCode) {
        final String name = errorCode.getClass().getSimpleName() + ":" + errorCode.toString();
        properties.put(name, errorCode.getNumber());
        return this;
    }

    public void printStackTrace(PrintStream s) {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (s) {
            printStackTrace(new PrintWriter(s));
        }
    }

    public void printStackTrace(PrintWriter s) {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (s) {
            s.println(this);
            s.println("\t-------------------------------");
            if (errorCode != null) {
                s.println("\t" + errorCode + ":" + errorCode.getClass().getName());
            }
            for (String key : properties.keySet()) {
                s.println("\t" + key + "=[" + properties.get(key) + "]");
            }
            s.println("\t-------------------------------");
            StackTraceElement[] trace = getStackTrace();
            for (StackTraceElement aTrace : trace) {
                s.println("\tat " + aTrace);
            }

            Throwable ourCause = getCause();
            if (ourCause != null) {
                ourCause.printStackTrace(s);
            }
            s.flush();
        }
    }

    /**
     * If the input <pre>cause</pre> extends {@link AbstractBaseException}, copy its properties.  Otherwise do nothing.
     * @param cause The {@link Throwable} to attempt a property copy from
     */
    protected AbstractBaseException copyProperties(final Throwable cause) {
        if (cause instanceof AbstractBaseException) {
            this.getProperties().putAll(((AbstractBaseException) cause).getProperties());
        }
        return this;
    }

}