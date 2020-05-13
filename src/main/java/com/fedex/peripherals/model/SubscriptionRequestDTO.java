package com.fedex.peripherals.model;

public class SubscriptionRequestDTO {
    
    SubscriptionRequest subscriptionRequest;

    public SubscriptionRequestDTO() {
        
    }

    public SubscriptionRequestDTO(SubscriptionRequest subscriptionRequest) {
        this.subscriptionRequest = subscriptionRequest;
    }

    public SubscriptionRequest getSubscriptionRequest() {
        return subscriptionRequest;
    }

    public void setSubscriptionRequest(SubscriptionRequest subscriptionRequest) {
        this.subscriptionRequest = subscriptionRequest;
    }

}

