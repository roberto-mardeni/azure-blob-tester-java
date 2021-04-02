package com.blobs.tester;

import java.io.IOException;

public class App {
    static Boolean randomSleepsEnabled = false;

    public static void main(String[] args) throws IOException {
        System.out.println("Azure Blob Storage v12 - Tester\n");
        System.out.println("Settings:");

        // Retrieve value to enable random sleeps
        String randomSleepsEnabledSetting = System.getenv("ENABLE_RANDOM_SLEEPS");

        randomSleepsEnabled = (randomSleepsEnabledSetting != null
                && randomSleepsEnabledSetting.toLowerCase().equalsIgnoreCase("true"));

        System.out.println("\tRandom Sleep Enabled: " + randomSleepsEnabled);

        // Retrieve value to enable file upload multiplier
        int fileUploadMultiplier = 1;
        String fileUploadMultiplierSetting = System.getenv("FILE_UPLOAD_MULTIPLIER");

        if (fileUploadMultiplierSetting != null) {
            fileUploadMultiplier = Integer.parseInt(fileUploadMultiplierSetting);
        }

        System.out.println("\tFile Upload Multipler: " + fileUploadMultiplier);

        // Retrieve the connection string for use with the application. The storage
        // connection string is stored in an environment variable on the machine
        // running the application called AZURE_STORAGE_CONNECTION_STRING. If the
        // environment variable
        // is created after the application is launched in a console or with
        // Visual Studio, the shell or application needs to be closed and reloaded
        // to take the environment variable into account.
        String connectStr = System.getenv("AZURE_STORAGE_CONNECTION_STRING");

        if (connectStr == null) {
            connectStr = "UseDevelopmentStorage=true";
        }

        System.out.println("\tAzure Storage Connection String: " + connectStr.substring(0, 21) + "...\n");

        new BlobTester(randomSleepsEnabled, fileUploadMultiplier, connectStr).PerformTest();
    }
}