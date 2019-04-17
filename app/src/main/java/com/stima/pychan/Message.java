package com.stima.pychan;

public class Message {
    private String message;
    private User sender;
    private long createdAt;

    public Message(String message, User sender, long createdAt) {
        setMessage(message);
        setSender(sender);
        setCreatedAt(createdAt);
    }

    public String getMessage() {
        return this.message;
    }

    public User getSender() {
        return this.sender;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
