package com.fedex.peripherals.model;

import java.util.Objects;

import com.fedex.common.cxs.dto.CXSOutput;

public class PeripheralInformationReceievedStatusDTO extends CXSOutput {

    private String recievedStatus;

    public PeripheralInformationReceievedStatusDTO() {
    }

    public PeripheralInformationReceievedStatusDTO(String recievedStatus) {
        this.recievedStatus = recievedStatus;
    }

    public String getRecievedStatus() {
        return recievedStatus;
    }

    public void setRecievedStatus(String recievedStatus) {
        this.recievedStatus = recievedStatus;
    }

    @Override
    public String toString() {
        return "PeripheralServiceHealthReceievedDTO [recievedStatus=" + recievedStatus + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(recievedStatus);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PeripheralInformationReceievedStatusDTO other = (PeripheralInformationReceievedStatusDTO) obj;
        return Objects.equals(recievedStatus, other.recievedStatus);
    }

}
