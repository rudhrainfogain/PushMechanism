package com.fedex.common.cxs.dto;

import java.util.Objects;

/**
 * CSX Envelope error messages.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class CXSError {

    private String code;
    private String message;

    private CXSError() {
        // default constructor intended for jackson (de)serialization
    }

    public CXSError(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "CXSError{" + "code='" + code + '\'' + ", message='" + message + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CXSError)) {
            return false;
        }
        CXSError cxsError = (CXSError) o;
        return Objects.equals(code, cxsError.code) && Objects.equals(message, cxsError.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }
}
