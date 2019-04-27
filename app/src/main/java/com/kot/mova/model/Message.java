package com.kot.mova.model;

import java.util.UUID;

public class Message {
    private String message;
    private String userId;
    private String messageId;
    private long timestamp;
    private Coordinates coordinates;
    private double reach;
    private boolean proximityOnly;

    public Message(String message, String userId, long timestamp, Coordinates coordinates, double reach, boolean proximityOnly) {
        this.message = message;
        this.userId = userId;
        this.messageId = UUID.randomUUID().toString();
        this.timestamp = timestamp;
        this.coordinates = coordinates;
        this.reach = reach;
        this.proximityOnly = proximityOnly;
    }

    public Message(String message, String userId, String messageId, long timestamp, Coordinates coordinates, double reach, boolean proximityOnly) {
        this.message = message;
        this.userId = userId;
        this.messageId = messageId;
        this.timestamp = timestamp;
        this.coordinates = coordinates;
        this.reach = reach;
        this.proximityOnly = proximityOnly;
    }
    public Message() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
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

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
