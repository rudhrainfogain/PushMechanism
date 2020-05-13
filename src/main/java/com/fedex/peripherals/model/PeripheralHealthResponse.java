package com.fedex.peripherals.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;

/**
 * Copyright (c) 2019 FedEx. All Rights Reserved.
 * 
 * Theme - Core Retail Peripheral Services
 * 
 * Feature - Peripheral Services -Implement application performance monitoring.
 * 
 * Description - This class is used to hold the health of the devices
 * 
 * @author Puneet Gupta [3696361]
 * @version 0.0.1
 * @since 29-Aug-2019
 */
@ApiModel(value = "PeripheralHealthResponse",
                description = "Response body in Json Format when getting health of peripheral")
public class PeripheralHealthResponse {

    private List<PeripheralHealth> peripheralHealths = new ArrayList<>();

    public PeripheralHealthResponse() {}

    public PeripheralHealthResponse(List<PeripheralHealth> peripheralHealths) {
        this.peripheralHealths = peripheralHealths;
    }

    public List<PeripheralHealth> getPeripheralHealths() {
        return peripheralHealths;
    }

    public void setPeripheralHealths(List<PeripheralHealth> peripheralHealths) {
        this.peripheralHealths = peripheralHealths;
    }

    public void addPeripheralHealth(PeripheralHealth peripheralHealth) {
        this.peripheralHealths.add(peripheralHealth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(peripheralHealths);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PeripheralHealthResponse)) {
            return false;
        }
        PeripheralHealthResponse other = (PeripheralHealthResponse) obj;
        return Objects.equals(peripheralHealths, other.peripheralHealths);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PeripheralHealthResponse [peripheralsHealth=").append(peripheralHealths).append("]");
        return builder.toString();
    }
}
