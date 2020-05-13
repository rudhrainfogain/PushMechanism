package com.fedex.peripherals.model;

import com.fedex.common.cxs.dto.CXSOutput;

public class PeripheralInformation extends CXSOutput{

    private ConnectedPeripheralsResponse connectedPeripheralsInfo;
    private PeripheralHealthResponse peripheralHealthInfo;
    
    public ConnectedPeripheralsResponse getConnectedPeripheralsInfo() {
        return connectedPeripheralsInfo;
    }
    public void setConnectedPeripheralsInfo(ConnectedPeripheralsResponse connectedPeripheralsInfo) {
        this.connectedPeripheralsInfo = connectedPeripheralsInfo;
    }
    public PeripheralHealthResponse getPeripheralHealthInfo() {
        return peripheralHealthInfo;
    }
    public void setPeripheralHealthInfo(PeripheralHealthResponse peripheralHealthInfo) {
        this.peripheralHealthInfo = peripheralHealthInfo;
    }
    
    
}
