package com.fedex.common.cxs.controlleradvice;

/**
 * A category around what type of validation failure has occurred
 */
public enum ValidationErrorType {

    /**
     * A required property is either too short or too long (too many characters, etc)
     */
    INVALID_LENGTH,

    /**
     * A required property was not provided
     */
    MISSING_PROPERTY,

    /**
     * Indicates that a provided value is invalid (bad pattern, etc)
     */
    INVALID_VALUE,

    /**
     * Indicates that a provided value has been duplicated (ex. a map of properties where a single key appears twice)
     */
    DUPLICATE_VALUE,

    /**
     * Indicates that a message specified a property that the system does not know about
     */
    UNAVAILABLE_PROPERTY,

    /**
     * Indicates that the validation is of an unknown type (the framework will be forced to rely on custom payloads)
     */
    UNKNOWN,
}
