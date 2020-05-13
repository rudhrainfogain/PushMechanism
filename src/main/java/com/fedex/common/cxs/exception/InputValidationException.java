package com.fedex.common.cxs.exception;

/**
 * Used to indicate that the value from a request is invalid.  {@link InputValidationErrorCode}
 * helps ot specify why something is invalid.
 */
@SuppressWarnings("unused")
public class InputValidationException extends AbstractBaseException implements RequestPathAware {


    private final String sectionName;
    private final String fieldName;


    public InputValidationException(
            final String sectionName,
            final String fieldName,
            final ErrorCode errorCode) {

        super(errorCode);
        this.sectionName = sectionName;
        this.fieldName = fieldName;
    }

    public InputValidationException(
            final String sectionName,
            final String fieldName,
            final String message,
            final ErrorCode errorCode) {

        super(message, errorCode);
        this.sectionName = sectionName;
        this.fieldName = fieldName;
    }

    public InputValidationException(
            final String sectionName,
            final String fieldName,
            final Throwable cause,
            final ErrorCode errorCode) {

        super(cause, errorCode);
        this.sectionName = sectionName;
        this.fieldName = fieldName;
    }

    public InputValidationException(
            final String sectionName,
            final String fieldName,
            final String message,
            final Throwable cause,
            final ErrorCode errorCode) {

        super(message, cause, errorCode);
        this.sectionName = sectionName;
        this.fieldName = fieldName;
    }

    public InputValidationException(
            final String sectionName,
            final String fieldName,
            final String message,
            final Throwable cause) {
        super(message, cause);
        this.sectionName = sectionName;
        this.fieldName = fieldName;
    }

    public InputValidationException(
            final String sectionName,
            final String fieldName,
            final String message) {

        super(message);
        this.sectionName = sectionName;
        this.fieldName = fieldName;
    }

    @Override
    public InputValidationException set(final String name, final Object value) {
        return (InputValidationException)super.set(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSectionName() {
        return sectionName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasSectionName() {
        return (sectionName != null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFieldName() {
        return fieldName;
    }

}
