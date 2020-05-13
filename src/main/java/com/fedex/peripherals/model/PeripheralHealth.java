package com.fedex.peripherals.model;

import java.util.Date;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Copyright (c) 2019 FedEx. All Rights Reserved.
 * 
 * Theme - Core Retail Peripheral Services
 *
 * Feature - Peripheral Services - Implement application performance monitoring.
 * 
 * Description - This class is the POJO class for the health data of the single peripheral device
 * 
 * @author Puneet Gupta [3696361]
 * @version 0.0.1
 * @since 10-Sep-2019
 */
@ApiModel(value = "PeripheralHealth",
                description = "Health Information which includes peripheral name, peripheral type, peripheral health status and time instance of health check")
public class PeripheralHealth {

    @ApiModelProperty(value = "Name of the peripheral", example = "EPSON TM88V Receipt Printer")
    private String peripheralName;

    @ApiModelProperty(value = "Type of the peripheral", example = "Receipt Printer")
    private String peripheralType;

    @ApiModelProperty(value = "Health status of the peripheral", example = "Offline")
    private String peripheralHealthStatus;

    @ApiModelProperty(value = "Time instance of health check", example = "2019-09-03T11:58:56.949Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", locale = "en_US")
    private Date peripheralHealthCheckTimeStamp;

    @ApiModelProperty(value = "Error code of the exception", example = "PERIPHERAL.JPOS.DEVICENOTCLAIMED")
    private String peripheralErrorCode;

    @ApiModelProperty(value = "Error message of the exception",
                    example = "Device is not claimed, either device is not connected or not in usable state")
    private String peripheralErrorMessage;

    public PeripheralHealth() {}
    
    public PeripheralHealth(String peripheralName, String peripheralType, String peripheralHealthStatus,
                    Date peripheralHealthCheckTimeStamp) {
        this.peripheralName = peripheralName;
        this.peripheralType = peripheralType;
        this.peripheralHealthStatus = peripheralHealthStatus;
        this.peripheralHealthCheckTimeStamp = peripheralHealthCheckTimeStamp;
    }

    public PeripheralHealth(String peripheralName, String peripheralType, String peripheralHealthStatus,
                    Date peripheralHealthCheckTimeStamp, String peripheralErrorCode,
                    String peripheralErrorMessage) {
        this.peripheralName = peripheralName;
        this.peripheralType = peripheralType;
        this.peripheralHealthStatus = peripheralHealthStatus;
        this.peripheralHealthCheckTimeStamp = peripheralHealthCheckTimeStamp;
        this.peripheralErrorCode = peripheralErrorCode;
        this.peripheralErrorMessage = peripheralErrorMessage;
    }

    public String getPeripheralName() {
        return peripheralName;
    }

    public void setPeripheralName(String peripheralName) {
        this.peripheralName = peripheralName;
    }

    public String getPeripheralType() {
        return peripheralType;
    }

    public void setPeripheralType(String peripheralType) {
        this.peripheralType = peripheralType;
    }

    public String getPeripheralHealthStatus() {
        return peripheralHealthStatus;
    }

    public void setPeripheralHealthStatus(String peripheralHealthStatus) {
        this.peripheralHealthStatus = peripheralHealthStatus;
    }

    public Date getPeripheralHealthCheckTimeStamp() {
        return peripheralHealthCheckTimeStamp;
    }

    public void setPeripheralHealthCheckTimeStamp(Date peripheralHealthCheckTimeStamp) {
        this.peripheralHealthCheckTimeStamp = peripheralHealthCheckTimeStamp;
    }

    public String getPeripheralErrorCode() {
        return peripheralErrorCode;
    }

    public void setPeripheralErrorCode(String peripheralErrorCode) {
        this.peripheralErrorCode = peripheralErrorCode;
    }

    public String getPeripheralErrorMessage() {
        return peripheralErrorMessage;
    }

    public void setPeripheralErrorMessage(String peripheralErrorMessage) {
        this.peripheralErrorMessage = peripheralErrorMessage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(peripheralErrorCode, peripheralErrorMessage,
                        peripheralHealthStatus, peripheralName, peripheralType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PeripheralHealth)) {
            return false;
        }
        PeripheralHealth other = (PeripheralHealth) obj;
        return Objects.equals(peripheralErrorCode, other.peripheralErrorCode)
                        && Objects.equals(peripheralErrorMessage, other.peripheralErrorMessage)
                        && Objects.equals(peripheralHealthStatus, other.peripheralHealthStatus)
                        && Objects.equals(peripheralName, other.peripheralName)
                        && Objects.equals(peripheralType, other.peripheralType);
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PeripheralHealth [peripheralName=").append(peripheralName).append(", peripheralType=")
                        .append(peripheralType).append(", peripheralHealthStatus=").append(peripheralHealthStatus)
                        .append(", peripheralHealthCheckTimeStamp=").append(peripheralHealthCheckTimeStamp)
                        .append(", peripheralErrorCode=").append(peripheralErrorCode)
                        .append(", peripheralErrorMessage=").append(peripheralErrorMessage).append("]");
        return builder.toString();
    }

}
