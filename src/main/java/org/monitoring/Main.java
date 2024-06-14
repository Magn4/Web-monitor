package org.monitoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.monitoring.models.Subscription;
import org.monitoring.models.User;
import org.monitoring.services.SubscriptionService;
import org.monitoring.services.UserService;
import org.monitoring.services.WebsiteMonitor;
import org.monitoring.utils.CheckForUpdates;

// ^import sun.util.resources.Bundles;

public class Main {
    private static final List<User> users = new ArrayList<>();

    public static void clearConsole() {
        for (int i = 0; i < 100; i++) {
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        User loggedInUser = null;

        while (loggedInUser == null) {
            clearConsole();
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("\n ---------------- \n");

            int choice = Integer.parseInt(scanner.nextLine());


            switch (choice) {
                case 1:
                    loggedInUser = UserService.login(scanner, users);
                    break;
                case 2:
                    loggedInUser = UserService.register(scanner, users);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

    System.out.println("Welcome, " + loggedInUser.getEmail() + "\n");

        if (!loggedInUser.getSubscriptions().isEmpty()) {
            clearConsole();
            System.out.println("1. Select existing Subscription");
            System.out.println("2. Add new Subscription");
            int choiceSubs = Integer.parseInt(scanner.nextLine());

            switch (choiceSubs) {
                case 1:
                    selectExistingSubscription(scanner, loggedInUser);
                    break;
                case 2:
                    addNewSubscription(scanner, loggedInUser);
                    break;
                default:
                    System.out.println("Invalid choice. Exiting.");
                    break;
            }
        } else {
            addNewSubscription(scanner, loggedInUser);
        }
    }

    private static void selectExistingSubscription(Scanner scanner, User loggedInUser) {
        if (loggedInUser.getSubscriptions().isEmpty()) {
            System.out.println("You have no saved Subscriptions");
            addNewSubscription(scanner, loggedInUser);
        } else {
            int i = 1;
            System.out.println("Select an existing subscription:");
            for (Subscription sub : loggedInUser.getSubscriptions()) {
                System.out.println(i + ". " + sub.getWebsiteURL() + " (Keyword: " + sub.getKeyword() + ", Frequency: " + sub.getFrequency() + " seconds)");
                i++;
            }
            System.out.println(i + ". Add a new subscription");

            int selected = Integer.parseInt(scanner.nextLine());
            if (selected >= 1 && selected < i) {
                Subscription selectedSubscription = loggedInUser.getSubscriptions().get(selected - 1);
                System.out.println("Monitoring website: " + selectedSubscription.getWebsiteURL() + " for keyword: " + selectedSubscription.getKeyword());

                monitorWebsite(loggedInUser, selectedSubscription.getWebsiteURL(), selectedSubscription.getKeyword(), selectedSubscription.getFrequency(), selectedSubscription.getStrategy());
            } else if (selected == i) {
                addNewSubscription(scanner, loggedInUser);
            } else {
                System.out.println("Invalid choice. Exiting.");
            }
        }
    }

    private static void addNewSubscription(Scanner scanner, User loggedInUser) {
        System.out.println("Enter the website URL you want to monitor:");
        String websiteURL = scanner.nextLine();

        System.out.println("Enter the frequency of checks (in seconds):");
        String frequency = scanner.nextLine();

        System.out.println("Enter the keyword to search in the website content:");
        String keyword = scanner.nextLine();

        System.out.println("Enter the strategies for website comparison ");
        System.out.println("1. Identical content size ");
        System.out.println("2. Identical HTML content");
        System.out.println("3. Identical text content");
        int strategies = scanner.nextInt();
    


        SubscriptionService.createSubscription(loggedInUser, websiteURL, frequency, keyword, strategies);

        System.out.println("Monitoring website: " + websiteURL + " for keyword: " + keyword);

        monitorWebsite(loggedInUser, websiteURL, keyword, frequency, strategies);
    }

    private static void monitorWebsite(User loggedInUser, String websiteURL, String keyword, String frequency, int strategies) {
        WebsiteMonitor monitor = new WebsiteMonitor();

        final User finalLoggedInUser = loggedInUser;
        final String finalKeyword = keyword;
        final String finalWebsiteURL = websiteURL;
        final int frequencyInSeconds = Integer.parseInt(frequency);
        final int finalStrategies = strategies;


        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable monitorTask = () -> {
            monitor.checkForUpdates(users);
            try {
                monitor.fetchAndUpdateWebsiteContent(finalWebsiteURL);
                if (!finalKeyword.isEmpty()) {
                    monitor.grepAndNotify(finalWebsiteURL, finalKeyword, finalLoggedInUser);
                    CheckForUpdates.changed(finalStrategies, finalWebsiteURL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        scheduler.scheduleAtFixedRate(monitorTask, 0, frequencyInSeconds, TimeUnit.SECONDS);
    }
}
