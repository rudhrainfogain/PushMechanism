package com.fedex.common.cxs.exception;

/**
 * Any {@link AbstractBaseException} that implements this interface is required to know where abouts in a
 * request message an error has occurred. This is typically need for input validation errors, where we must
 * know which field and surround section a validation violation occurred.
 */
public interface RequestPathAware {

    /**
     * If a request message was nested and contained a section name, this method will return the name.
     * @return The section name if it exists, otherwise <code>null</code>.
     * @see RequestPathAware#hasSectionName()
     */
    String getSectionName();

    /**
     * If a request message contains no nesting (no parent elements), then section name will not exist.  This
     * method indicates if the request message contains a surround section.
     * @return {@link Boolean#TRUE} if section name is defined, {@link Boolean#FALSE} otherwise.
     */
    boolean hasSectionName();


    /**
     * Retrieve the field name that is causing an exception
     * @return {@link String} the name of the field name causing the exception (required)
     */
    String getFieldName();

}
