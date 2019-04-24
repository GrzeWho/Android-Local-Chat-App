package com.kot.mova.model;

import java.util.Date;
import java.util.UUID;

public class Message {
    private String message;
    private UUID userId;
    private Date timestamp;
    private double reach;
    private boolean proximityOnly;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public double getReach() {
        return reach;
    }

    public void setReach(double reach) {
        this.reach = reach;
    }

    public boolean isProximityOnly() {
        return proximityOnly;
    }

    public void setProximityOnly(boolean proximityOnly) {
        this.proximityOnly = proximityOnly;
    }

}
