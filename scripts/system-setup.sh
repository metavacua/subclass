#!/bin/bash
# SPDX-License-Identifier: AGPL-3.0-only
# SPDX-FileCopyrightText: 2024 com.github.metavacua

set -euo pipefail

log_info() {
    echo "[INFO] $1"
}

log_error() {
    echo "[ERROR] $1" >&2
}

# Update package lists (idempotent)
log_info "Updating package lists..."
export DEBIAN_FRONTEND=noninteractive
sudo apt-get update -qq

# Install OpenJDK 21 (idempotent check)
if ! command -v java &> /dev/null || ! java -version 2>&1 | grep -q "openjdk version \"21"; then
    log_info "Installing OpenJDK 21..."
    sudo apt-get install -y -qq openjdk-21-jdk
else
    log_info "OpenJDK 21 already installed."
fi

# Dynamic JAVA_HOME detection
JAVA_HOME_PATH=$(readlink -f "$(command -v java)" | sed 's:/bin/java::' | sed 's:/jre::')
export JAVA_HOME="${JAVA_HOME_PATH}"
log_info "JAVA_HOME set to: ${JAVA_HOME}"

# Verify Java version
java_version=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
log_info "Java version: ${java_version}"

# Install Maven (idempotent check)
if ! command -v mvn &> /dev/null; then
    log_info "Installing Maven..."
    sudo apt-get install -y -qq maven
else
    log_info "Maven already installed."
    mvn --version | head -1
fi

# Install libxml2-utils for xmllint (idempotent check)
if ! command -v xmllint &> /dev/null; then
    log_info "Installing libxml2-utils..."
    sudo apt-get install -y -qq libxml2-utils
else
    log_info "libxml2-utils (xmllint) already installed."
fi

# Install pipx (idempotent check)
if ! command -v pipx &> /dev/null; then
    log_info "Installing pipx..."
    sudo apt-get install -y -qq pipx
    pipx ensurepath
else
    log_info "pipx already installed."
fi

# Install REUSE tool via pipx (idempotent check)
if ! command -v reuse &> /dev/null; then
    log_info "Installing REUSE tool via pipx..."
    pipx install reuse
else
    log_info "REUSE tool already installed."
    reuse --version
fi

log_info "System setup complete."
log_info "Installed versions:"
echo "  Java: $(java -version 2>&1 | head -n1)"
echo "  Maven: $(mvn --version 2>&1 | head -n1)"
echo "  xmllint: $(xmllint --version 2>&1 | head -n1)"
echo "  reuse: $(reuse --version 2>&1 | head -n1)"
