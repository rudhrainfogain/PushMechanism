package com.fedex.peripherals.model;

import java.util.List;
import java.util.Objects;

/**
 * Copyright (c) 2019 FedEx. All Rights Reserved.<br/>
 * 
 * Theme - Core Retail Peripheral Services<br/>
 * Feature - Peripheral Services - Implement application performance monitoring.<br/>
 * Description - This class is 
 * 
 * @author Namit Jain [3696360]
 * @version 0.0.1
 * @since 26-Sep-2019
 */
public class ConnectedPeripheralsResponse{

    private List<ConnectedPeripheral> connectedPeripherals;

    public ConnectedPeripheralsResponse() {
    }

    public ConnectedPeripheralsResponse(List<ConnectedPeripheral> connectedPeripherals) {
        this.connectedPeripherals = connectedPeripherals;
    }

    public List<ConnectedPeripheral> getConnectedPeripherals() {
        return connectedPeripherals;
    }

    public void setConnectedPeripherals(List<ConnectedPeripheral> connectedPeripherals) {
        this.connectedPeripherals = connectedPeripherals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectedPeripherals);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ConnectedPeripheralsResponse)) {
            return false;
        }
        ConnectedPeripheralsResponse other = (ConnectedPeripheralsResponse) obj;
        return Objects.equals(connectedPeripherals, other.connectedPeripherals);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConnectedPeripheralsResponse [connectedPeripherals=").append(connectedPeripherals).append("]");
        return builder.toString();
    }

}
