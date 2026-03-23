#!/bin/bash

# Check if both arguments are provided
# $# represents the number of arguments passed to the script
if [ "$#" -lt 2 ]; then
    echo "Error. Usage: $0 <docker_hub_username> <image_name>"
    exit 1
fi

USERNAME=$1
IMAGE_NAME=$2

# Publish docker_compose
docker compose publish $1/$IMAGE_NAME-compose:latest

# Error check
if [ $? -eq 0 ]
then
    echo "docker-compose published successfully."
else
    echo "There was an error publishing the docker-compose."
fi