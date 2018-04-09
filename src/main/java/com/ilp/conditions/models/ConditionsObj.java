package com.ilp.conditions.models;

import java.time.ZonedDateTime;

public class ConditionsObj {

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public long getDestinationAmount() {
        return destinationAmount;
    }

    public void setDestinationAmount(long destinationAmount) {
        this.destinationAmount = destinationAmount;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public byte[] getReceiverSecret() {
        return receiverSecret;
    }

    public void setReceiverSecret(byte[] receiverSecret) {
        this.receiverSecret = receiverSecret;
    }

    String destinationAddress = null;
    long destinationAmount =  0L;
    ZonedDateTime expiresAt = null;
    byte[] receiverSecret = null;

}
