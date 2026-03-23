#!/bin/bash

# Check if a parameter was provided
if [ -z "$1" ]; then
    echo "Error: No image name provided."
    echo "Usage: $0 <image_name>"
    exit 1
fi

IMAGE_NAME=$1

# Build Docker image using the parameter
docker build --network host -t "$IMAGE_NAME" -f Dockerfile ..

# Error check
if [ $? -eq 0 ]; then
    echo "Docker image '$IMAGE_NAME' built successfully."
else
    echo "There was an error building the Docker image."
    exit 1
fi