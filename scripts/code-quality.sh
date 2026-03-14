#!/bin/bash
set -euo pipefail

export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk-amd64}

echo "=== Code Quality Gate ==="

if [ -d "src" ]; then
    echo "Validating XML files with Exclusive C14N 1.0..."
    find src -name "*.xml" -type f | while read -r file; do
        echo "  Checking: $file"
        if ! xmllint --exc-c14n "$file" > /dev/null 2>&1; then
            echo "ERROR: C14N validation failed for: $file"
            exit 1
        fi
    done
    echo "✓ All XML files passed C14N validation"
else
    echo "No src directory found, skipping C14N validation"
fi

echo "Running Maven verify..."
mvn verify -q

echo "=== Quality gate PASSED ==="
