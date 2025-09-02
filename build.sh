#!/bin/bash

echo "Building XILLEN Security Plugin..."
echo

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven from: https://maven.apache.org/download.cgi"
    echo
    exit 1
fi

echo "Maven found, starting build..."
echo

# Clean and build
mvn clean package

if [ $? -eq 0 ]; then
    echo
    echo "SUCCESS: Plugin built successfully!"
    echo "JAR file location: target/xillen-security-2.0.jar"
    echo
    echo "Copy this JAR file to your server's plugins folder"
    echo
else
    echo
    echo "ERROR: Build failed!"
    echo "Check the error messages above"
    echo
    exit 1
fi
