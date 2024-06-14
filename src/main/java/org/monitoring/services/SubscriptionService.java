package org.monitoring.services;

import java.util.UUID;

import org.monitoring.models.Subscription;
import org.monitoring.models.User;

public class SubscriptionService {

    public static Subscription createSubscription(User user, String websiteURL, String frequency, String keyword, int strategie) {
        String subscriptionID = UUID.randomUUID().toString();
        Subscription subscription = new Subscription(subscriptionID, user.getUserID(), websiteURL, frequency, keyword, strategie);
        DatabaseManager.saveSubscription(subscription);
        user.addSubscription(subscription);
        // user.setStrategie(strategie);
        return subscription;
    }
}
