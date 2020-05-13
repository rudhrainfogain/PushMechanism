package com.fedex.peripherals.model;

public class PeripheralServerDetailsDTO {

    String ipAndPort;
    String subscriptionType;
    
    @Override
    public String toString() {
        return "PeripheralServerDetailsDTO [ipAndPort=" + ipAndPort + ", subscriptionType=" + subscriptionType + "]";
    }

    public String getIpAndPort() {
        return ipAndPort;
    }

    public void setIpAndPort(String ipAndPort) {
        this.ipAndPort = ipAndPort;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public PeripheralServerDetailsDTO() {
    }

    public PeripheralServerDetailsDTO(String ipAndPort, String subscriptionType) {
        super();
        this.ipAndPort = ipAndPort;
        this.subscriptionType = subscriptionType;
    }

    
    
}