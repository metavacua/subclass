#!/bin/bash
set -euo pipefail

echo "=== System Setup for subclass project ==="
echo "Updating package lists..."
apt-get update -qq

echo "Installing required packages: openjdk-21-jdk, maven, libxml2-utils..."
apt-get install -y -qq openjdk-21-jdk maven libxml2-utils

export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
echo "export JAVA_HOME=$JAVA_HOME" >> /etc/profile.d/java.sh
echo "JAVA_HOME set to: $JAVA_HOME"

java -version
mvn -version
xmllint --version

echo "=== System setup complete ==="
