package org.monitoring.services;

import java.io.Console;
import java.util.List;
import java.util.Scanner;

import org.monitoring.models.User;

// import sun.util.resources.Bundles;

public class UserService {

    public static User login(Scanner scanner, List<User> users) {
        System.out.println("Enter your email:");
        String email = scanner.nextLine();
        
        Console console = System.console();
        String password;

        if (console != null) {
            char[] passwordArray = console.readPassword("Enter your password: ");
            password = new String(passwordArray);
        } else {
            System.out.println("Enter your password:");
            password = scanner.nextLine();
        }

        User user = DatabaseManager.getUserByEmailAndPassword(email, password);
        if (user != null) {
            users.add(user);
            return user;
        }
        System.out.println("Invalid email or Password. Please try again.");
        return null;
    }

    public static User register(Scanner scanner, List<User> users) {
        System.out.println("Enter your email:");
        String email = scanner.nextLine();

        Console console = System.console();
        String password;

        if (console != null) {
            char[] passwordArray = console.readPassword("Enter your password: ");
            password = new String(passwordArray);
        } else {
            System.out.println("Enter your password:");
            password = scanner.nextLine();
        }

        String userID = "user" + (users.size() + 1);
        User newUser = new User(userID, email, password);
        DatabaseManager.saveUser(newUser);
        users.add(newUser);
        return newUser;
    }

    public static String getWebsiteURL(Scanner scanner) {
        System.out.println("Enter the website URL you want to monitor:");
        return scanner.nextLine();
    }

    public static String getFrequency(Scanner scanner) {
        System.out.println("Enter the frequency of checks (in seconds):");
        return scanner.nextLine();
    }
}
