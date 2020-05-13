package com.fedex.peripherals.model;

public class SubscriptionStatusResponse {

    private String status;

    public SubscriptionStatusResponse() {}

    public SubscriptionStatusResponse(String status) {

        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
