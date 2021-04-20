package com.blobs.tester;

import com.azure.core.http.policy.*;
import com.azure.core.util.logging.ClientLogger;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;

import java.io.*;
import java.util.*;

public class BlobTester {
    Boolean randomSleepsEnabled = false;
    int fileUploadMultiplier = 1;
    String connectStr;
    TestMode mode;
    String containerName;
    String localDownloadPath;
    ClientLogger logger;
    int MaxTries = 3;

    public BlobTester(Boolean randomSleepsEnabled, int fileUploadMultiplier, String azureConnectionString,
            TestMode mode, String containerName, String localDownloadPath, ClientLogger logger) {
        this.randomSleepsEnabled = randomSleepsEnabled;
        this.fileUploadMultiplier = fileUploadMultiplier;
        this.connectStr = azureConnectionString;
        this.mode = mode;
        this.containerName = containerName;
        this.localDownloadPath = localDownloadPath;
        this.logger = logger;
    }

    public void PerformTest() {
        Boolean doCleanUp = true;
        // Create a BlobServiceClient object which will be used to create a container
        // client
        Set<String> allowedHeaderNames = new HashSet<>(Arrays.asList("x-ms-meta-foo"));
        Set<String> allowedQueryParamNames = new HashSet<>(Arrays.asList("sv"));
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr)
                // .retryOptions(new RequestRetryOptions())
                // .addPolicy(new RetryPolicy())
                .httpLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BODY_AND_HEADERS)
                        .setAllowedHeaderNames(allowedHeaderNames).setAllowedQueryParamNames(allowedQueryParamNames))
                .buildClient();

        // Create a unique name for the container if not already provided
        if (containerName == null) {
            containerName = "blobtests" + java.util.UUID.randomUUID();
        } else {
            doCleanUp = false;
        }

        // Create the container and return a container client object
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        try {
            if (!containerClient.exists()) {
                containerClient.create();
                logger.info(String.format("\nCreated container %s", containerName));
            }
        } catch (Exception ex) {
            logger.error(String.format("\nContainer already present %s", containerName));
        }

        String[] files = new String[] { "small", "medium", "large", "verylarge" };
        String[] extensions = new String[] { "txt", "pdf" };

        // Upload Files
        if (this.mode == TestMode.UploadAndDownload || this.mode == TestMode.UploadOnly) {
            for (String file : files) {
                for (String extension : extensions) {
                    for (int multiplier = 1; multiplier <= fileUploadMultiplier; multiplier++) {
                        String path = "/files/" + file + "." + extension;
                        String blobName = multiplier + "-" + file + "." + extension;
                        logger.info("\tUploading " + path + " to " + blobName);

                        BlobClient blobClient = containerClient.getBlobClient(blobName);
                        InputStream data = getClass().getResourceAsStream(path);

                        try {
                            blobClient.upload(data, data.available(), true);
                        } catch (IOException ex) {
                            logger.error("\tError uploading");
                        }

                        randomSleep();
                    }
                }
            }
        }

        if (this.mode == TestMode.UploadAndDownload || this.mode == TestMode.DownloadOnly) {
            // List and download the blob(s) in the container.
            logger.info(String.format("\n\nListing and downloading blobs... in %s", containerName));

            for (BlobItem blobItem : containerClient.listBlobs()) {
                String blobName = blobItem.getName();
                logger.info("\tDownloading " + blobName);
                BlobClient blobClient = containerClient.getBlobClient(blobName);

                try {
                    for (int tries = 1; tries <= MaxTries; tries++) {
                        // Add an exponential sleep for retries
                        if (tries > 1) {
                            long sleepTime = tries * 100;
                            try {
                                Thread.sleep(sleepTime);
                            } catch (Exception e) {
                                logger.error(e.getMessage());
                            }
                        }
                        try {
                            blobClient.downloadToFile(this.localDownloadPath + blobName, true);
                            logger.info("\tDownloaded " + blobName + ", Tries: " + tries);
                            randomSleep();
                            break;
                        } catch (UncheckedIOException ex) {
                            if (tries == MaxTries) {
                                throw ex;
                            }
                        }
                    }
                } catch (UncheckedIOException ex) {
                    logger.error("\tError downloading " + blobName + ", " + ex.getMessage());
                }
            }
        }

        // Clean up
        if (doCleanUp && this.mode == TestMode.UploadAndDownload) {
            logger.info("\nPress the Enter key to begin clean up");
            System.console().readLine();

            logger.info("Deleting blob container...");
            containerClient.delete();
        }

        logger.info("Done");
    }

    private void randomSleep() {
        if (randomSleepsEnabled) {
            // thread to sleep randomly between 100 and 500 milliseconds
            try {
                int min = 100;
                int max = 300;
                long sleepTime = (long) (Math.random() * (max - min + 1) + min);
                logger.info("\tSleeping for " + sleepTime);
                Thread.sleep(sleepTime);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
}
