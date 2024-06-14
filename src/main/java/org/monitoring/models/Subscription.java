package org.monitoring.models;

public class Subscription {
    private String subscriptionID;
    private String userID;
    private String websiteURL;
    private String frequency;
    private String keyword; 
    private int strategy;

    public Subscription(String subscriptionID, String userID, String websiteURL, String frequency, String keyword, int strategy) {
        this.subscriptionID = subscriptionID;
        this.userID = userID;
        this.websiteURL = websiteURL;
        this.frequency = frequency;
        this.keyword = keyword;
        this.strategy = strategy;
    }

    public String getSubscriptionID() {
        return subscriptionID;
    }

    public void setSubscriptionID(String subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public int getStrategy() {
        return strategy;
    }

    public void setStrategy(int strategy) {
        this.strategy = strategy;
    }
}
