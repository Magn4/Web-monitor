package org.monitoring.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.monitoring.models.Subscription;
import org.monitoring.models.User;
import org.monitoring.utils.BashCommandExecutor;

public class WebsiteMonitor {
    private final NotificationService notificationService = new NotificationService();
    private final Map<String, String> previousContent = new HashMap<>();

    public void checkForUpdates(List<User> users) {
        for (User user : users) {
            for (Subscription subscription : user.getSubscriptions()) {
                boolean updateDetected = checkWebsite(subscription.getWebsiteURL(), subscription.getKeyword(), user);
                if (updateDetected) {
                    notifyUser(user, subscription);
                }
            }
        }
    }

    private boolean checkWebsite(String websiteURL, String keyword, User user) {
        try {
            fetchAndUpdateWebsiteContent(websiteURL);
            String previous = previousContent.get(websiteURL);
            String current = DatabaseManager.getHtmlContent(websiteURL);
    
            if (current == null) {
                return false;
            }
    
            if (previous != null && !previous.equals(current)) {
                previousContent.put(websiteURL, current);
                if (current.contains(keyword)) {
                    return true;
                }
            } else if (previous == null) {
                previousContent.put(websiteURL, current);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    

    public void fetchAndUpdateWebsiteContent(String websiteURL) throws Exception {
        String content = BashCommandExecutor.getHTML(websiteURL);
        DatabaseManager.saveOrUpdateHtmlContent(websiteURL, content);
    }

    public void grepAndNotify(String websiteURL, String keyword, User user) {
        String result = BashCommandExecutor.grepAndSaveHTML(websiteURL, keyword, user);
        String message = "Grep result for keyword: " + keyword + " on website: " + websiteURL + "\n" + result;
        notificationService.sendEmail(user.getEmail(), message);
    }

    private void notifyUser(User user, Subscription subscription) {
        String message = "Update detected on website: " + subscription.getWebsiteURL() + " containing keyword: " + subscription.getKeyword();
        notificationService.sendEmail(user.getEmail(), message);
    }
}
