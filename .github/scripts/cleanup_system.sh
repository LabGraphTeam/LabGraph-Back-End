#!/bin/bash

# Docker Cleanup Script

echo "====================================="
echo "      Starting Docker Cleanup        "
echo "====================================="

echo "Remove everything unused"
docker system prune -a -f

echo "====================================="
echo "      Docker Cleanup Completed       "
echo "====================================="

echo "Disk usage after cleanup:"
docker system df

echo "====================================="
echo "      Starting System Cleanup        "
echo "====================================="
echo "Clearing system cache..."
sync
echo 3 >/proc/sys/vm/drop_caches

echo "Memory usage after cleanup:"
free -h

echo "====================================="
echo "      Starting APT Cleanup           "
echo "====================================="

echo "Cleaning APT cache..."
apt-get clean -y

echo "Removing unused packages..."
apt-get autoremove -y

echo "Removing old downloaded archive files..."
apt-get autoclean -y

echo "Current disk usage:"
df -h

echo "====================================="
echo "      Cleanup Complete               "
echo "====================================="

exit 0
