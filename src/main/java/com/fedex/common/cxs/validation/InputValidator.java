package com.fedex.common.cxs.validation;

import java.util.regex.Pattern;

import com.fedex.common.cxs.exception.InputValidationErrorCode;
import com.fedex.common.cxs.exception.InputValidationException;

public class InputValidator {
    private static final String FIELD_DELIMITER = ".";
    private static final Pattern PATTERN_UUID = Pattern.compile("^[a-zA-Z0-9\\-]+$");
    private static final Pattern PATTERN_ALPHANUM = Pattern.compile("^[a-zA-Z0-9]+$");
    private static final Pattern PATTERN_ALPHA = Pattern.compile("^[a-zA-Z]+$");
    private static final Pattern PATTERN_PHONE =
            Pattern.compile("^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$");
    private static final Pattern PATTERN_SAFE_STRING =
            Pattern.compile("^[\\.\\+;@'!?&$#\\(\\)\\/|%=,`*_\\-a-zA-Z0-9À-ÖØ-öø-ÿ   :~'\"]*$");


    //region conditional methods

    public static  String buildNm(final String prefix, final String fieldName) {
        return prefix + FIELD_DELIMITER + fieldName;
    }

    public static  String buildNm(final String prefix, final int index) {
        return prefix + "[" + index + "]";
    }

    public static boolean isAlphaNumeric(final String str) {
        return PATTERN_ALPHANUM.matcher(str).matches();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isSafeString(final String str) {
        return PATTERN_SAFE_STRING.matcher(str).matches();
    }

    public static boolean isAlpha(final String str) {
        return PATTERN_ALPHA.matcher(str).matches();
    }

    public static boolean isUUID(final String str) {
        return PATTERN_UUID.matcher(str).matches();
    }

    public static boolean isPhoneNumber(final String str) {
        return PATTERN_PHONE.matcher(str).matches();
    }

    //endregion

    //region validation methods that assert content

    public static void validateAlphaNumeric(
            final String sectionName,
            final String fieldName,
            final String str) throws InputValidationException {

        if(!isAlphaNumeric(str)) {
            throwInvalidFieldFormat(sectionName, fieldName);
        }
    }

    public static void validateSafeString(
            final String sectionName,
            final String fieldName,
            final String str) throws InputValidationException {

        if(!isSafeString(str)) {
            throwInvalidFieldFormat(sectionName, fieldName);
        }
    }

    public static void validateUUIDFormat(
            final String sectionName,
            final String fieldName,
            final String str) throws InputValidationException {

        if(!isUUID(str)) {
            throwInvalidFieldFormat(sectionName, fieldName);
        }
    }

    public static void validateEmailFormat(
            final String sectionName,
            final String fieldName,
            final String str) throws InputValidationException {

        if(!isSafeString(str)) {
            throwInvalidFieldFormat(sectionName, fieldName);
        }
    }

    public static void validatePhoneNumberFormat(
            final String sectionName,
            final String fieldName,
            final String str) throws InputValidationException {

        if(!isPhoneNumber(str)) {
            throwInvalidFieldFormat(sectionName, fieldName);
        }
    }

    public static void validateRequiredField(
            final String sectionName,
            final String fieldName,
            final Object obj) throws InputValidationException {

        if(obj == null) {
            throwMissingField(sectionName, fieldName);
        }
    }

    public static void validateFieldNotAllowed(
            final String sectionName,
            final String fieldName,
            final Object obj) throws InputValidationException {

        if(obj != null) {
            throwFieldNotAllowed(sectionName, fieldName);
        }
    }

    public static void validateStringLengthRange(
            final String sectionName,
            final String fieldName,
            final String str,
            final int min,
            final int max) throws InputValidationException {

        if(str == null) {
            throwMissingField(sectionName, fieldName);
        }

        if(str.length() < min || str.length() > max) {
            throwFieldLengthNotInRange(sectionName, fieldName, min, max);
        }
    }

    public static void validateStringLengthMin(
            final String sectionName,
            final String fieldName,
            final String str,
            final int min) throws InputValidationException {

        if(str == null) {
            throwMissingField(sectionName, fieldName);
        }

        if(str.trim().length()<min) {
            throwFieldLengthTooShort(sectionName, fieldName, min);
        }
    }

    public static void validateStringLengthMax(
            final String sectionName,
            final String fieldName,
            final String str,
            final int max) throws InputValidationException {

        if(str == null) {
            throwMissingField(sectionName, fieldName);
        }
        if(str.length() > max) {
            throwFieldLengthTooLong(sectionName, fieldName, max);
        }
    }

    public static void validateRequiredSafeString(
            final String sectionName,
            final String fieldName,
            final String str,
            final int minLength,
            final int maxLength) throws InputValidationException {

        validateRequiredField(sectionName, fieldName, str);
        validateStringLengthMin(sectionName, fieldName, str, minLength);
        validateStringLengthMax(sectionName, fieldName, str, maxLength);
        validateSafeString(sectionName, fieldName, str);
    }

    //endregion

    //region InputValidationException factory methods
    
    public static void throwInvalidFieldFormat(
            final String sectionName,
            final String fieldName) throws InputValidationException {

        throw new InputValidationException(
                sectionName,
                fieldName,
                fieldName + " is not formatted correctly or has invalid characters",
                InputValidationErrorCode.VALUE_BAD_FORMAT).set("field", fieldName);
    }

    public static void throwMissingField(
            final String sectionName,
            final String fieldName) throws InputValidationException {

        throw new InputValidationException(
                sectionName,
                fieldName,
                fieldName + " is a required field",
                InputValidationErrorCode.VALUE_MISSING).set("field", fieldName);
    }

    public static void throwFieldNotAllowed(
            final String sectionName,
            final String fieldName) throws InputValidationException {

        throw new InputValidationException(
                sectionName,
                fieldName,
                fieldName + " is not an allowed field in this context",
                InputValidationErrorCode.VALUE_NOT_ALLOWED).set("field", fieldName);
    }

    public static void throwFieldLengthNotInRange(
            final String sectionName,
            final String fieldName,
            final int min,
            final int max) throws InputValidationException {

        throw new InputValidationException(
                sectionName,
                fieldName,
                fieldName + " length must be between " + min + " and " + max,
                InputValidationErrorCode.VALUE_LENGTH_NOT_IN_RANGE).set("field", fieldName);
    }

    public static void throwFieldLengthTooLong(
            final String sectionName,
            final String fieldName,
            final int max) throws InputValidationException {

        throw new InputValidationException(
                sectionName,
                fieldName,
                fieldName + " length must be less than " + max,
                InputValidationErrorCode.VALUE_LENGTH_TOO_LONG).set("field", fieldName);
    }

    public static void throwFieldLengthTooShort(
            final String sectionName,
            final String fieldName,
            final int min) throws InputValidationException {

        throw new InputValidationException(
                sectionName,
                fieldName,
                fieldName + " length must be greater than " + min,
                InputValidationErrorCode.VALUE_LENGTH_TOO_SHORT).set("field", fieldName);
    }

    public static void throwInvalidValue(
            final String sectionName,
            final String fieldName) throws InputValidationException {

        throw new InputValidationException(
                sectionName,
                fieldName,
                fieldName + " contains an invalid value",
                InputValidationErrorCode.VALUE_INVALID).set("field", fieldName);
    }

    //endregion

}
