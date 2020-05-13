package com.fedex.common.cxs.controlleradvice;

import org.springframework.stereotype.Component;

import com.fedex.common.cxs.exception.InputValidationErrorCode;
import com.fedex.common.cxs.exception.InputValidationException;

/**
 * Convenience class to help parse {@link InputValidationException}
 */
@Component
@SuppressWarnings("WeakerAccess")
public class InputValidationExParser {


    /**
     * Map an error code from an {@link InputValidationErrorCode} to the common classes of
     * {@link ValidationErrorType} used by the exception handler
     *
     * @param exErrorCode {@link com.fedex.common.cxs.exception.InputValidationErrorCode}
     * @return {@link ValidationErrorType}
     */
    public ValidationErrorType getErrorType(final InputValidationErrorCode exErrorCode) {

        switch (exErrorCode) {
            case VALUE_INVALID:
            case VALUE_BAD_FORMAT:
            case VALUE_LENGTH_NOT_IN_RANGE:
            case VALUE_NOT_ALLOWED:
                return ValidationErrorType.INVALID_VALUE;
            case VALUE_LENGTH_TOO_LONG:
            case VALUE_LENGTH_TOO_SHORT:
                return ValidationErrorType.INVALID_LENGTH;
            case VALUE_MISSING:
                return ValidationErrorType.MISSING_PROPERTY;
            case VALUE_DUPLICATED:
                return ValidationErrorType.DUPLICATE_VALUE;
            default:
                throw new IllegalArgumentException("Unknown InputValidationErrorCode " + exErrorCode);
        }

    }

    /**
     * Parse an {@link InputValidationException} to determine the field and potentially section
     * name that the exception occurred at.
     */
    public PropertyPathNames getPropertyPathNames(final InputValidationException rpa) {
        return (rpa.hasSectionName()) ?
                new PropertyPathNames(rpa.getSectionName(), rpa.getFieldName()) :
                new PropertyPathNames(rpa.getFieldName());
    }

}
