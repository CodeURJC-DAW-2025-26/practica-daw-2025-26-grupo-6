#!/bin/bash

# Check if both arguments are provided
# $# represents the number of arguments passed to the script
if [ "$#" -lt 2 ]; then
    echo "Error. Usage: $0 <docker_hub_username> <image_name>"
    exit 1
fi

USERNAME=$1
IMAGE_NAME=$2

# Check if the Docker image exists locally
# We filter by name to ensure an exact match
if [ -z "$(docker images -q "$IMAGE_NAME" 2> /dev/null)" ]; then
  echo "Error: Docker image '$IMAGE_NAME' not found locally."
  echo "Please run ./create_image.sh first."
  exit 1
fi

# Tag the Docker image
echo "Tagging $IMAGE_NAME as $USERNAME/$IMAGE_NAME:latest..."
docker tag "$IMAGE_NAME" "$USERNAME/$IMAGE_NAME:latest"

# Publish Docker image
docker push "$USERNAME/$IMAGE_NAME:latest"

# Error check using the exit status of the push command
if [ $? -eq 0 ]; then
    echo "Docker image published successfully to Hub."
else
    echo "There was an error publishing the Docker image."
    exit 1
fi