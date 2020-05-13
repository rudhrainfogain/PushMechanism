package com.fedex.common.cxs.exception;

/**
 * A non-retryable exception emitted from the processor layer of our application
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class TerminalProcessorException extends ProcessorException {

    public TerminalProcessorException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public TerminalProcessorException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }

    public TerminalProcessorException(final Throwable cause, final ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public TerminalProcessorException(final String message, final Throwable cause, final ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public TerminalProcessorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TerminalProcessorException(final String message) {
        super(message);
    }

}
