#!/bin/bash

echo "======================================"
echo "Starting Account Service"
echo "======================================"
echo ""
echo "Port: 8081"
echo "Database: PostgreSQL (localhost:5433)"
echo ""

# Build the project
echo "Building project..."
./gradlew clean build -x test

if [ $? -eq 0 ]; then
    echo ""
    echo "Build successful! Starting application..."
    echo ""

    # Run the application
    ./gradlew bootRun
else
    echo ""
    echo "Build failed. Please check the errors above."
    exit 1
fi
