#!/bin/bash

# Docker Cleanup Script

echo "====================================="
echo "      Starting Docker Cleanup        "
echo "====================================="

# Remove stopped containers
echo "Removing stopped containers..."
docker container prune -f

# Remove unused volumes
echo "Removing unused volumes..."
docker volume ls -q | grep -v -E "qualitylab-pro-backend_mysql-volume|quality-lab-pro-react-plotly_qualitylab_node_modules|quality-lab-pro-react-plotly_qualitylab_public" | xargs -r docker volume rm


# Remove unused images
echo "Removing unused images..."
docker image prune -a -f

# Remove unused networks
echo "Removing unused networks..."
docker network prune -f

# Remove build cache
echo "Removing build cache..."
docker builder prune -a -f

echo "====================================="
echo "      Docker Cleanup Completed       "
echo "====================================="

# Show disk usage after cleanup
echo "Disk usage after cleanup:"
docker system df

exit 0