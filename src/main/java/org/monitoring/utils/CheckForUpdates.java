package org.monitoring.utils;

import org.monitoring.services.DatabaseManager;

public class CheckForUpdates {
    public static void changed(int strategy, String URL) {
        String newContent = BashCommandExecutor.getHTML(URL);
        String oldContent = DatabaseManager.getHtmlContent(URL);

        if (oldContent == null) {
            System.out.println("No previous content found for URL: " + URL);
            return;
        }

        boolean hasChanged = false;

        switch (strategy) {
            // Identical Content size
            case 1:
                hasChanged = checkContentSize(oldContent, newContent);
                break;

            // Identical html content
            case 2:
                hasChanged = checkHtmlContent(oldContent, newContent);
                break;
                
            // Identical text content
            case 3:
                hasChanged = checkTextContent(oldContent, newContent);
                break;

            default:
                throw new IllegalArgumentException("Invalid strategy: " + strategy);
        }

        if (hasChanged) {
            System.out.println("Content has changed for URL: " + URL);
        } else {
            System.out.println("No change detected for URL: " + URL);
        }
    }

    private static boolean checkContentSize(String oldContent, String newContent) {
        return oldContent.length() != newContent.length();
    }

    private static boolean checkHtmlContent(String oldContent, String newContent) {
        return !oldContent.equals(newContent);
    }

    private static boolean checkTextContent(String oldContent, String newContent) {
        String oldText = oldContent.replaceAll("\\<.*?\\>", "").trim();
        String newText = newContent.replaceAll("\\<.*?\\>", "").trim();
        return !oldText.equals(newText);
    }
}
