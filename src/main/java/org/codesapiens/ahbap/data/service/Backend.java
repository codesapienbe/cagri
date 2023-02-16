package org.codesapiens.ahbap.data.service;

/**
 * Utility class to hold the backend services for the application.
 */
public class Backend {

    private static final Backend instance = new Backend();

    private Backend() {
    }

    public static Backend getInstance() {
        return instance;
    }

}
