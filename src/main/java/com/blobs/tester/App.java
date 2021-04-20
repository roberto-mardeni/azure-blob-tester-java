package com.blobs.tester;

import java.io.IOException;

import com.azure.core.util.logging.ClientLogger;

public class App {
    static Boolean randomSleepsEnabled = false;

    public static void main(String[] args) throws IOException {
        ClientLogger logger = new ClientLogger("com.blobs.tester.App");

        logger.info("Azure Blob Storage v12 - Tester\n");
        logger.info("Settings:");

        // Retrieve value to enable random sleeps
        String randomSleepsEnabledSetting = System.getenv("ENABLE_RANDOM_SLEEPS");

        randomSleepsEnabled = (randomSleepsEnabledSetting != null
                && randomSleepsEnabledSetting.toLowerCase().equalsIgnoreCase("true"));

        logger.info("\tRandom Sleep Enabled: " + randomSleepsEnabled);

        // Retrieve value to enable file upload multiplier
        int fileUploadMultiplier = 1;
        String fileUploadMultiplierSetting = System.getenv("FILE_UPLOAD_MULTIPLIER");

        if (fileUploadMultiplierSetting != null) {
            fileUploadMultiplier = Integer.parseInt(fileUploadMultiplierSetting);
        }

        logger.info("\tFile Upload Multipler: " + fileUploadMultiplier);

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

        logger.info("\tAzure Storage Connection String: " + connectStr.substring(0, 21) + "...");

        TestMode mode = TestMode.UploadAndDownload;

        String modeString = System.getenv("TEST_MODE");

        if (modeString != null) {
            try {
                mode = TestMode.valueOf(modeString);
            } catch (IllegalArgumentException ex) {
                String validValues = "";
                for (TestMode m : TestMode.values()) {
                    if (validValues != "") {
                        validValues += ",";
                    }
                    validValues += m.toString();
                }

                logger.error(
                        "\tERROR: Invalid value for Test Mode " + modeString + ". Acceptable values: " + validValues);
                return;
            }
        }

        logger.info("\tTest Mode: " + mode + "...");

        String containerName = System.getenv("CONTAINER_NAME");

        logger.info("\tContainer Name: " + containerName + "...");

        String localDownloadPath = System.getenv("DOWNLOAD_PATH");

        if (localDownloadPath == null || localDownloadPath == "") {
            localDownloadPath = System.getProperty("java.io.tmpdir");
        }

        logger.info("\tLocal Download Path: " + localDownloadPath);

        new BlobTester(randomSleepsEnabled, fileUploadMultiplier, connectStr, mode, containerName, localDownloadPath, logger)
                .PerformTest();
    }
}