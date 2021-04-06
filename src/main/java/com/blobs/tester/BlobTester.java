package com.blobs.tester;

import com.azure.core.http.policy.*;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;

import java.io.*;

public class BlobTester {
    Boolean randomSleepsEnabled = false;
    int fileUploadMultiplier = 1;
    String connectStr;
    TestMode mode;

    public BlobTester(Boolean randomSleepsEnabled, int fileUploadMultiplier, String azureConnectionString,
            TestMode mode) {
        this.randomSleepsEnabled = randomSleepsEnabled;
        this.fileUploadMultiplier = fileUploadMultiplier;
        this.connectStr = azureConnectionString;
        this.mode = mode;
    }

    public void PerformTest() {
        // Create a BlobServiceClient object which will be used to create a container
        // client
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr)
                // .retryOptions(new RequestRetryOptions())
                // .addPolicy(new RetryPolicy())
                .httpLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BASIC)).buildClient();

        // Create a unique name for the container
        String containerName = "blobtests" + java.util.UUID.randomUUID();

        // Create the container and return a container client object
        BlobContainerClient containerClient = blobServiceClient.createBlobContainer(containerName);

        System.out.println(String.format("\nCreated container %s", containerName));

        String[] files = new String[] { "small", "medium", "large", "verylarge" };
        String[] extensions = new String[] { "txt", "pdf" };

        // Upload Files
        if (this.mode == TestMode.UploadAndDownload || this.mode == TestMode.UploadOnly) {
            for (String file : files) {
                for (String extension : extensions) {
                    for (int multiplier = 1; multiplier <= fileUploadMultiplier; multiplier++) {
                        String path = "/files/" + file + "." + extension;
                        String blobName = multiplier + "-" + file + "." + extension;
                        System.out.println("\tUploading " + path + " to " + blobName);

                        BlobClient blobClient = containerClient.getBlobClient(blobName);
                        InputStream data = getClass().getResourceAsStream(path);
                        try {
                            blobClient.upload(data, data.available());
                        } catch (IOException ex) {
                            System.out.println("\tError uploading");
                        }

                        randomSleep();
                    }
                }
            }
        }

        if (this.mode == TestMode.UploadAndDownload || this.mode == TestMode.DownloadOnly) {
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
        }

        // Clean up
        if (this.mode == TestMode.UploadAndDownload) {
            System.out.println("\nPress the Enter key to begin clean up");
            System.console().readLine();

            System.out.println("Deleting blob container...");
            containerClient.delete();
        }

        System.out.println("Done");
    }

    private void randomSleep() {
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
