# azure-blob-tester-java
A sample demonstrating how to upload/download blobs to an Azure Storage Account using the Azure Java SDK v12

# usage
By default the application will use the [Azure Storage Emulator](https://docs.microsoft.com/en-us/azure/storage/common/storage-use-emulator) connection string and will perform an upload and download of a sample of small, medium, large and very large files in TXT and PDF formats.

If you want to test against an Azure Storage Account, provide an environment variable **AZURE_STORAGE_CONNECTION_STRING** with the connection string like below.

```bash
setx AZURE_STORAGE_CONNECTION_STRING "<Azure Storage Account Connection String>"

-- or --

$env:AZURE_STORAGE_CONNECTION_STRING = "<Azure Storage Account Connection String>"
```

To include random sleep commands, include a **ENABLE_RANDOM_SLEEPS** environment variable with the value **true** like below.

```bash
setx ENABLE_RANDOM_SLEEPS "true/false"

-- or --

$env:ENABLE_RANDOM_SLEEPS = "true/false"
```

To include multiple file uploads, include a **FILE_UPLOAD_MULTIPLIER** environment variable with the numerical value like below.

```bash
setx FILE_UPLOAD_MULTIPLIER 3

-- or --

$env:FILE_UPLOAD_MULTIPLIER = 3
```

The default behavior is to upload and download files, if you want to change it to either download or upload only include a **TEST_MODE** environment variable with the proper value to be one of "UploadAndDownload", "UploadOnly" or "DownloadOnly"

```bash
setx TEST_MODE UploadAndDownload

-- or --

$env:TEST_MODE = "UploadAndDownload"
```

By default a random container name would be used, to change to a fixed one add a **CONTAINER_NAME** environment variable with the desired name.

```bash
setx CONTAINER_NAME "container-name"

-- or --

$env:CONTAINER_NAME = "container-name"
```

# packaging
The project file has been customized to package with and without all dependencies for convenience.