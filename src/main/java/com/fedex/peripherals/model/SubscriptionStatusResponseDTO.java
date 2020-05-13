package com.fedex.peripherals.model;

import com.fedex.common.cxs.dto.CXSOutput;

public class SubscriptionStatusResponseDTO extends CXSOutput {

    private SubscriptionStatusResponse subscription;

    public SubscriptionStatusResponseDTO() {

    }

    public SubscriptionStatusResponseDTO(SubscriptionStatusResponse subscription) {
        this.subscription = subscription;
    }

    public SubscriptionStatusResponse getSubscription() {
        return subscription;
    }

    public void setSubscription(SubscriptionStatusResponse subscription) {
        this.subscription = subscription;
    }
    

}
