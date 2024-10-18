#!/bin/bash
# A simple shell script to create ds-web release artifact for internal Nexus
# 
# Author: jt@kb.dk  2024-05-15
# Last updated: 2024-05-15 
#
# Requirements: 
# Bash commands required: tar 
# You need node and npm in the version specified in the projects package.json
#

# Build project
npm run build

# Get version tag from npm package and strip surrounding quotes
VERSION=$(npm pkg get version | tr -d '"')

# Define prefix
PREFIX="infomedia-webQA-"

# Create gzipped tar file from specified folder
FOLDER_PATH="dist/"
TAR_FILE="${PREFIX}${VERSION}.tar.gz"

# Create tar.gz file
tar -czvf "${TAR_FILE}" "${FOLDER_PATH}" CHANGELOG.md

