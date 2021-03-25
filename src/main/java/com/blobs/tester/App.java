package com.blobs.tester;

/**
 * Azure blob storage v12 SDK quickstart
 */
import com.azure.core.http.policy.*;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
// import com.azure.storage.common.policy.RequestRetryOptions;

import java.io.*;

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
        
        System.out.println("\tAzure Storage Connection String: " + connectStr.substring(0, 21) + "\n");

        // Create a BlobServiceClient object which will be used to create a container
        // client
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr)
                // .retryOptions(new RequestRetryOptions())
                // .addPolicy(new RetryPolicy())
                .httpLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BASIC)).buildClient();

        // Create a unique name for the container
        String containerName = "quickstartblobs" + java.util.UUID.randomUUID();

        // Create the container and return a container client object
        BlobContainerClient containerClient = blobServiceClient.createBlobContainer(containerName);

        System.out.println(String.format("\nCreated container %s", containerName));

        // Upload local files
        // Thread.currentThread().getContextClassLoader().getResource("");
        String localPath = "src/main/resources";
        File folder = new File(localPath);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String fileName = listOfFiles[i].getName();

                for (int multiplier = 1; multiplier <= fileUploadMultiplier; multiplier++) {
                    String blobName = multiplier + "-" + fileName;
                    BlobClient blobClient = containerClient.getBlobClient(blobName);
                    System.out.println("\tUploading " + blobName);
                    blobClient.uploadFromFile(listOfFiles[i].getAbsolutePath());

                    randomSleep();
                }
            }
        }

        // List and download the blob(s) in the container.
        System.out.println(String.format("\n\nListing and downloading blobs... in %s", containerName));

        String localDownloadPath = System.getProperty("java.io.tmpdir");

        for (BlobItem blobItem : containerClient.listBlobs()) {
            String blobName = blobItem.getName();
            System.out.println("\tDownloading " + blobName);
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            blobClient.downloadToFile(localDownloadPath + blobName, true);

            randomSleep();
        }

        // Clean up
        System.out.println("\nPress the Enter key to begin clean up");
        System.console().readLine();

        System.out.println("Deleting blob container...");
        containerClient.delete();

        System.out.println("Done");
    }

    private static void randomSleep() {
        if (randomSleepsEnabled) {
            // thread to sleep randomly between 100 and 500 milliseconds
            try {
                int min = 100;
                int max = 300;
                long sleepTime = (long) (Math.random() * (max - min + 1) + min);
                System.out.println("\tSleeping for " + sleepTime);
                Thread.sleep(sleepTime);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}