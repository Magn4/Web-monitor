package org.monitoring.models;

import java.util.ArrayList;
import java.util.List;

import org.monitoring.services.DatabaseManager;

public class User {
    private final String userID;
    private String email;
    private String password;
    // private String strategie;
    private final List<Subscription> subscriptions;

    public User(String userID, String email, String password/*, String strategie*/) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        // this.strategie = strategie;
        this.subscriptions = new ArrayList<>();
        loadSubscriptions();
    }

    private void loadSubscriptions() {
        this.subscriptions.clear();
        this.subscriptions.addAll(DatabaseManager.getSubscriptionsForUser(this.userID));
    }

    public void updatePreferences(String newEmail, String newPassword) {
        this.email = newEmail;
        this.password = newPassword;
    }

    public void cancelSubscription(String subscriptionID) {
        subscriptions.removeIf(subscription -> subscription.getSubscriptionID().equals(subscriptionID));
    }

    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }
    // public void setStrategie(String newStrategie) {
    //     this.strategie = newStrategie;

    // }


    public String getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    // public String getStrategie() {
    //     return strategie;
    // }
    

    public List<Subscription> getSubscriptions() {
        loadSubscriptions(); 
        return subscriptions;
    }
}
