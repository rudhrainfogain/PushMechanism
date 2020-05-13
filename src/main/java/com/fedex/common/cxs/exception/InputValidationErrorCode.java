package com.fedex.common.cxs.exception;

import org.springframework.http.HttpStatus;

/**
 * Indiciates why an {@link InputValidationException} was thrown
 */
@SuppressWarnings("unused")
public enum InputValidationErrorCode implements ErrorCode {
    VALUE_MISSING(2000),
    VALUE_NOT_ALLOWED(2001),
    VALUE_LENGTH_TOO_SHORT(2002),
    VALUE_LENGTH_TOO_LONG(2003),
    VALUE_LENGTH_NOT_IN_RANGE(2004),
    VALUE_BAD_FORMAT(2005),
    VALUE_DUPLICATED(2006),
    VALUE_INVALID(2007);

    // suppressing bug as the linter is recommending a fix that actually breaks the code
    @SuppressWarnings("FieldCanBeLocal")
    private final int localErrorCode;

    InputValidationErrorCode(final int localErrorCode) {
        this.localErrorCode = localErrorCode;
    }

    @Override
    public int getNumber() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
