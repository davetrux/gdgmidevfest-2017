#!/bin/bash

imageName="gdg-demo"

#Create the new image
docker build -t "${imageName}" .

# Remove running instances
running=$(docker ps -a -q -f "name=${imageName}")

for i in $running;
do
        echo "Stopping Container"
        docker stop $i
        echo "Removing Container"
        docker rm $i
done

#Start a container with this image
docker run --name "${imageName}" --restart=always -p 8003:8080 -d "${imageName}"
