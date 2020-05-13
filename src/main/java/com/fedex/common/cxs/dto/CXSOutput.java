package com.fedex.common.cxs.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A CXS Output object contains a list of alert messages along with the API's payload included.  This class
 * is intended to be extended by DTO objects
 */
@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
abstract public class CXSOutput {

    protected List<CXSAlert> alerts = new ArrayList<>();

    protected CXSOutput() {
        // default constructor for use by jackson (de)serialization
    }

    /**
     * Retrieve all known alerts that occurred during processing.  Alerts are a form of a warning.
     * @return {@link List} of {@link CXSAlert}
     */
    public List<CXSAlert> getAlerts() {
        return alerts;
    }

    /**
     * Builder style convenience method to add a vararg set of {@link CXSAlert} objects.
     */
    public CXSOutput withAlerts(final CXSAlert... alerts) {
        if (null != alerts) {
            this.alerts.addAll(Arrays.asList(alerts));
        }
        return this;
    }

}
