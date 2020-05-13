package com.fedex.peripherals.model;

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
public class ConnectedPeripheral {

    private String name;
    private PeripheralTypeConstants type;
    private String serialId;
    private String firmwareVersion;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public PeripheralTypeConstants getType() {
        return type;
    }
    public void setType(PeripheralTypeConstants type) {
        this.type = type;
    }
    public String getSerialId() {
        return serialId;
    }
    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

}
