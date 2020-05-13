package com.fedex.common.cxs.controlleradvice;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import com.fedex.common.cxs.dto.CXSEnvelope;
import com.fedex.common.cxs.dto.CXSError;

/**
 * Helper class to assist with the construction of {@link ResponseEntity} messages
 * that contain an error based {@link CXSEnvelope}.
 *
 * @author Ben Bays <ben.bays@projekt202.com>
 * @since 2018-10-01
 */
@SuppressWarnings("WeakerAccess")
@Component
public class ErrorResponseBuilder {

    public CXSError buildInputValidationError(final ValidationErrorType errorType, final PropertyPathNames properties) {

        switch (errorType) {
            case INVALID_VALUE:
                return buildInvalidFormatError(properties);
            case MISSING_PROPERTY:
                return buildMissingFieldError(properties);
            case INVALID_LENGTH:
                return buildBadLengthError(properties);
            case DUPLICATE_VALUE:
                return buildDuplicateError(properties);
            case UNAVAILABLE_PROPERTY:
                return buildUnknownPropertyError(properties);
            default:
                // unable to find a legal mapping from constraint validation type to
                throw new IllegalArgumentException("Unknown error type " + errorType);
        }
    }

    /**
     * Helper method to construct a {@link CXSError}
     * @param props Field and and optional section name
     * @param message The message to embed into the CXSError
     * @param messageSuffix What to append at the end of the error message (e.g. "INVALID.PROPERTY")
     * @return {@link CXSError} constructed, ready to be put in an envelope.
     */
    public CXSError buildCxsError(final PropertyPathNames props, final String message, final String messageSuffix) {
        final String fieldName = props.getFieldName();
        final String sectionName = (props.hasSectionName()) ? props.getSectionName()+"." : "";

        return new CXSError(
                sectionName.toUpperCase() + fieldName.toUpperCase() + "." + messageSuffix,
                message);
    }

    /**
     * @return {@link CXSError} indicating that a required property is missing from a request message
     */
    public CXSError buildMissingFieldError(final PropertyPathNames props) {
        return buildCxsError(
                props,
                "The " + props.getFieldName() + " is missing",
                "MISSING.PROPERTY");
    }

    /**
     * @return {@link CXSError} indicating that a value is either too long or too short
     */
    public CXSError buildBadLengthError(final PropertyPathNames props) {
        return buildCxsError(
                props,
                "The " + props.getFieldName() +  " length is invalid",
                "INVALID.LENGTH");

    }

    /**
     * @return {@link CXSError} indicating that a value is improperly formatted
     */
    public CXSError buildInvalidFormatError(final PropertyPathNames props) {
        return buildCxsError(
                props,
                "The " + props.getFieldName() + " is invalid",
                "INVALID.VALUE");
    }

    /**
     * @return {@link CXSError} indicating that a map key appears more than once in a request
     */
    public CXSError buildDuplicateError(final PropertyPathNames props) {
        return buildCxsError(
                props,
                "The " + props.getFieldName() + " is a duplicate",
                "DUPLICATE.PROPERTY");
    }

    /**
     * @return {@link CXSError} indicating that a property appeared on a request message that is invalid
     */
    public CXSError buildUnknownPropertyError(final PropertyPathNames props) {
        return buildCxsError(
                props,
                "The " + props.getFieldName() + " does not exist",
                "INVALID.PROPERTY");
    }

    /**
     * @return {@link CXSError} indicating an  unknown error has occurred that we cannot recover from
     */
    public CXSError buildInternalServerError() {
        return new CXSError("INTERNAL.SERVER.FAILURE", "An unrecoverable exception has occurred");
    }

    /**
     * @return {@link CXSError} indicating that an error has occurred, but a retry might succeed
     */
    public CXSError buildSystemUnavailableError() {
        return new CXSError("SYSTEM.UNAVAILABLE", "System unavailable; please try again");
    }


    /**
     * Use this message if you know who the user is (i.e. authentication has passed), but the user does not
     * have permission to take an action (has failed authorization).  In other words, you know who the user
     * is, but they're not permitted to do something.
     *
     * @return {@link CXSError} indicating that an the user is not authorized to take the desired action
     */
    public CXSError buildUnauthorizedError() {
        return new CXSError("ACTION.FORBIDDEN", "Action is not permitted by user");
    }

    /**
     * Use this message if you don't know who the user is (i.e. the user has not logged in).
     * @return {@link CXSError} indicating that the user is not logged in
     */
    public CXSError buildUnauthenticatedError() {
        return new CXSError("ACTION.UNAUTHORIZED", "Action requires authentication");
    }

    /**
     * This exception indicates that a single property appears more than once on a request message.
     * For example <code>{"name":"George", "name":"Jose"}</code>.  In this case, we need to detect
     * the property that was duplicated and if available, include the name of the section that
     * the duplicate was found in.
     * @param jpe Exception thrown by jackson when a property has been duplicated
     * @return {@link CXSError} containing a properly formatted error
     * @throws IOException Failure to parse a JSON stream
     */
    public CXSError buildDuplicatePropertyError(final JsonParseException jpe) throws IOException {

        PropertyPathNames properties;

        final JsonParser jsonParser = jpe.getProcessor();
        final String fieldName = jsonParser.getCurrentName();
        final JsonStreamContext parseContext = jsonParser.getParsingContext();

        JsonStreamContext parentContext = parseContext.getParent();

        // For the majority of cases, our parentContext is a JSON object.  However, in some cases,
        // our parent could be a List, in which case we need to check our grandparent.
        // Normal Case:
        // { "parent" : { "duplicateProp": "value1", "duplicateProp": "value2" } }
        // Case of a List:
        // { "parents" : [ { "duplicateProp": "value1" }, {"duplicateProp": "value2"} ] }

        while (null != parentContext && parentContext.getCurrentValue() instanceof List) {
            parentContext = parentContext.getParent();
        }

        if (null != parentContext) {
            properties = new PropertyPathNames(parentContext.getCurrentName(), fieldName);
        } else {
            properties = new PropertyPathNames(fieldName);
        }

        return buildDuplicateError(properties);
    }

    /**
     * This exception indicates that the request message has specified a property that doesn't have
     * a corresponding property on the request bean.  We need to interrogate the Jackson exception
     * to find the bad property name and potentially the section name it came a part of (if nested)
     * @param upe Exception thrown by jackson when an unrecognized property is encountered
     * @return {@link CXSError} containing a property formatted error
     */
    public CXSError buildUnrecognizedPropertyError(final UnrecognizedPropertyException upe) {

        final String fieldName = upe.getPropertyName();

        String sectionName = null;

        // walk the path upward looking for a sectionName
        for (int i = upe.getPath().size() - 2; i >= 0; i--) {
            // if the path element contains "ArrayList" in the description, than our direct parent object
            // is actually a list instead of a map key.  We need to get at our grandparent to get the sectionName
            // example: {"parent":[ {"badProp":"badValue"}, "normalProp":"normalValue"} ] }
            if (!upe.getPath().get(i).getDescription().contains("java.util.ArrayList")) {
                sectionName = upe.getPath().get(i).getFieldName();
                break;
            }
        }

        final PropertyPathNames properties = (sectionName == null) ?
                new PropertyPathNames(fieldName) :
                new PropertyPathNames(sectionName, fieldName);

        return buildUnknownPropertyError(properties);
    }

}
