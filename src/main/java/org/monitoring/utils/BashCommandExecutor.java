package org.monitoring.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.monitoring.models.User;
import org.monitoring.services.DatabaseManager;

/* 
* The logic of this class is that there is one class that executes all type of bash commands
* and all other classes assembles the command that needs to be executed so that the code looks cleaner.
*/

public class BashCommandExecutor {

    public static String getHTML(String URL) {
        String command = "curl " + URL;
        String results = BashCommandExecutor.runCommand(command);

        DatabaseManager.saveOrUpdateHtmlContent(URL, results);

        return results;
    }

    public static String grepAndSaveHTML(String URL, String keyword, User user) {
        String htmlContent = DatabaseManager.getHtmlContent(URL);

        if (htmlContent == null) {
            htmlContent = getHTML(URL); // Fetch and save HTML content if not present
        }

        String command = "echo \"" + htmlContent + "\" | grep " + keyword;
        String result = runCommand(command);

        // Save the grep result (you might want to save this differently based on your needs)
        DatabaseManager.saveOrUpdateHtmlContent(URL + "_grep_" + keyword, result);

        // Here, you can send a notification to the user if needed
        // For example:
        // NotificationService.sendNotification(user, "Grep result for keyword: " + keyword + " on URL: " + URL);

        return result;
    }

    public static String runCommand(String command) {
        StringBuilder output = new StringBuilder();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            output.append("Exited with code: ").append(exitCode).append("\n");

        } catch (IOException | InterruptedException e) {
            output.append("Error: ").append(e.getMessage());
        }

        return output.toString();
    }
}
