# azure-blob-tester-java
A sample demonstrating how to upload/download blobs to an Azure Storage Account using the Azure Java SDK v12

# usage
By default the application will use the [Azure Storage Emulator](https://docs.microsoft.com/en-us/azure/storage/common/storage-use-emulator) connection string. 

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

# packaging
The project file has been customized to package with and without all dependencies for convenience.