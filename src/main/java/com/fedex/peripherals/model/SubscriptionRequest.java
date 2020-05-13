package com.fedex.peripherals.model;

public class SubscriptionRequest {
    
	private PushMechanismSubscriptionType subscriptionType;

    private String subscriptionURL;

    public SubscriptionRequest() {}

    public SubscriptionRequest(PushMechanismSubscriptionType subscriptionType, String subscriptionURL) {
        this.subscriptionType = subscriptionType;
        this.subscriptionURL = subscriptionURL;
    }

    public PushMechanismSubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(PushMechanismSubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public String getSubscriptionURL() {
        return subscriptionURL;
    }

    public void setSubscriptionURL(String subscriptionURL) {
        this.subscriptionURL = subscriptionURL;
    }

}
