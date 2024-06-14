package org.monitoring.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.monitoring.models.Subscription;
import org.monitoring.models.User;

// import sun.util.resources.Bundles;

public class DatabaseManager {
    private static final String JDBC_URL_USER_DB = "jdbc:mysql://localhost:3307/website_monitor"; // Update port if necessary
    private static final String JDBC_URL_HTML_DB = "jdbc:mysql://localhost:3307/website_monitor_html"; // Update port if necessary
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password123";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void saveUser(User user) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL_USER_DB, USERNAME, PASSWORD)) {
            String insertQuery = "INSERT INTO users (user_id, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, user.getUserID());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setString(3, user.getPassword());
                preparedStatement.executeUpdate();
                System.out.println("User saved to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static User getUserByEmailAndPassword(String email, String password) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL_USER_DB, USERNAME, PASSWORD)) {
            String selectQuery = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return new User(
                            resultSet.getString("user_id"),
                            resultSet.getString("email"),
                            resultSet.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveSubscription(Subscription subscription) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL_USER_DB, USERNAME, PASSWORD)) {
            String insertQuery = "INSERT INTO subscriptions (subscription_id, user_id, website_url, frequency, keyword, strategy) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, subscription.getSubscriptionID());
                preparedStatement.setString(2, subscription.getUserID());
                preparedStatement.setString(3, subscription.getWebsiteURL());
                preparedStatement.setString(4, subscription.getFrequency());
                preparedStatement.setString(5, subscription.getKeyword());
                preparedStatement.setInt(6, subscription.getStrategy());
                preparedStatement.executeUpdate();
                System.out.println("Subscription saved to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Subscription> getSubscriptionsForUser(String userID) {
        List<Subscription> subscriptions = new ArrayList<>();
        String query = "SELECT * FROM subscriptions WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(JDBC_URL_USER_DB, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String subscriptionID = resultSet.getString("subscription_id");
                String websiteURL = resultSet.getString("website_url");
                String frequency = resultSet.getString("frequency");
                String keyword = resultSet.getString("keyword");
                int strategy = resultSet.getInt("strategy");

                Subscription subscription = new Subscription(subscriptionID, userID, websiteURL, frequency, keyword, strategy);
                subscriptions.add(subscription);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subscriptions;
    }

    public static void saveOrUpdateHtmlContent(String url, String content) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL_HTML_DB, USERNAME, PASSWORD)) {
            String selectQuery = "SELECT id FROM html_content WHERE url = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setString(1, url);
                ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    // Update existing record
                    int id = resultSet.getInt("id");
                    String updateQuery = "UPDATE html_content SET content = ?, last_updated = CURRENT_TIMESTAMP WHERE id = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setString(1, content);
                        updateStatement.setInt(2, id);
                        updateStatement.executeUpdate();
                        System.out.println("HTML content updated in the database.");
                    }
                } else {
                    // Insert new record
                    String insertQuery = "INSERT INTO html_content (url, content) VALUES (?, ?)";
                    try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                        insertStatement.setString(1, url);
                        insertStatement.setString(2, content);
                        insertStatement.executeUpdate();
                        System.out.println("HTML content saved to the database.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getHtmlContent(String url) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL_HTML_DB, USERNAME, PASSWORD)) {
            String selectQuery = "SELECT content FROM html_content WHERE url = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, url);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getString("content");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
