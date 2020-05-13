package com.fedex.common.cxs.controlleradvice;

/**
 * Helper class to capture the section and field name properties that a violation is found at
 */
@SuppressWarnings("WeakerAccess")
public class PropertyPathNames {
    private final String sectionName;
    private final String fieldName;

    PropertyPathNames(final String sectionName, final String fieldName) {
        this.sectionName = sectionName;
        this.fieldName = fieldName;
    }

    PropertyPathNames(final String fieldName) {
        this(null, fieldName);
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean hasSectionName() {
        return sectionName != null;
    }
}
