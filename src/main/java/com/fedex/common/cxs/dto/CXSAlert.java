package com.fedex.common.cxs.dto;

import java.util.Objects;

/**
 * A general alert to be included in the output section of a CXS Envelope.  Indicates an auxiliary concern
 * that can be attached to a success message.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class CXSAlert {

    private String code;
    private String message;
    private AlertType alertType;

    private CXSAlert() {
        // default constructor intended for jackson (de)serialization
    }

    public CXSAlert(
            final String code,
            final String message,
            final AlertType alertType) {
        this.code = code;
        this.message = message;
        this.alertType = alertType;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    @Override
    public String toString() {
        return "CXSAlert{" + "code='" + code + '\'' + ", message='" + message + '\'' + ", alertType=" + alertType + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CXSAlert)) {
            return false;
        }
        CXSAlert cxsAlert = (CXSAlert) o;
        return Objects.equals(code, cxsAlert.code) && Objects.equals(message, cxsAlert.message)
                && alertType == cxsAlert.alertType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, alertType);
    }

    public enum AlertType {
        WARNING,
        NOTE
    }
}
