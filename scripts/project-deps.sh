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

# Set up KeY Maven repository
KEY_REPO="https://key-project.org/key-mvn-repo/"
log_info "Configuring KeY Maven repository: ${KEY_REPO}"

# Prefetch Apache Jena 5.0.0
log_info "Prefetching Apache Jena 5.0.0..."
mvn dependency:get \
    -Dartifact=org.apache.jena:apache-jena-libs:5.0.0:pom \
    -Dtransitive=true \
    -q

# Prefetch JavaPoet 1.13.0
log_info "Prefetching JavaPoet 1.13.0..."
mvn dependency:get \
    -Dartifact=com.squareup:javapoet:1.13.0 \
    -Dtransitive=true \
    -q

# Prefetch KeY dependencies from their custom repository
log_info "Prefetching KeY dependencies from ${KEY_REPO}..."
mvn dependency:get \
    -Dartifact=org.key-project:key.core:2.11.0 \
    -DremoteRepositories=key-repo::default::https://key-project.org/key-mvn-repo/ \
    -Dtransitive=true \
    -q || {
    log_info "Note: Some KeY artifacts may not be available in remote repository. Will be resolved during build."
}

# Additional KeY artifacts
KEY_ARTIFACTS=(
    "org.key-project:key.util:2.11.0"
    "org.key-project:key.ui:2.11.0"
)

for artifact in "${KEY_ARTIFACTS[@]}"; do
    log_info "Attempting to prefetch ${artifact}..."
    mvn dependency:get \
        -Dartifact="${artifact}" \
        -DremoteRepositories=key-repo::default::https://key-project.org/key-mvn-repo/ \
        -Dtransitive=false \
        -q 2>/dev/null || {
        log_info "  (artifact resolution deferred to build phase)"
    }
done

log_info "Dependency prefetching complete."
