#!/bin/bash
set -euo pipefail

export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk-amd64}

echo "=== Prefetching Maven dependencies ==="

cd /home/engine/project

echo "Fetching Apache Jena..."
mvn dependency:get -Dartifact=org.apache.jena:apache-jena-libs:4.10.0:pom -q

echo "Fetching JavaPoet..."
mvn dependency:get -Dartifact=com.squareup:javapoet:1.13.0 -q

echo "✓ Dependencies prefetched"

echo "=== Dependency prefetch complete ==="
