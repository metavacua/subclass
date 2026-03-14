#!/bin/bash
# SPDX-License-Identifier: AGPL-3.0-only
# SPDX-FileCopyrightText: 2024 com.github.metavacua

# Hard gate: Any failure blocks the build
set -euo pipefail

log_info() {
    echo "[INFO] $1"
}

log_error() {
    echo "[ERROR] $1" >&2
}

log_section() {
    echo ""
    echo "========================================"
    echo "  $1"
    echo "========================================"
}

EXIT_CODE=0

# Find XML files (DocBook, Maven POMs, etc.)
log_section "XML C14N Validation"
XML_FILES=$(find . -type f \( -name "*.xml" -o -name "*.xsd" -o -name "*.xsl" \) \
    -not -path "./target/*" \
    -not -path "./.git/*" 2>/dev/null || true)

if [ -n "$XML_FILES" ]; then
    for xml_file in $XML_FILES; do
        log_info "Validating C14N: ${xml_file}"
        if ! xmllint --noout --exc-c14n "${xml_file}" 2>&1; then
            log_error "C14N validation failed for: ${xml_file}"
            EXIT_CODE=1
        fi
    done
else
    log_info "No XML files found to validate."
fi

# Maven license check
log_section "Maven License Check"
if ! mvn license:check -q; then
    log_error "Maven license check failed."
    EXIT_CODE=1
else
    log_info "Maven license check passed."
fi

# REUSE lint
log_section "REUSE Compliance Check"
if ! reuse lint; then
    log_error "REUSE lint failed."
    EXIT_CODE=1
else
    log_info "REUSE lint passed."
fi

# Maven verify (includes tests, validation, DocBook processing)
log_section "Maven Verify"
if ! mvn verify -P4 -q; then
    log_error "Maven verify failed."
    EXIT_CODE=1
else
    log_info "Maven verify passed."
fi

# Final gate
log_section "Quality Gate Result"
if [ $EXIT_CODE -ne 0 ]; then
    log_error "QUALITY GATE FAILED - Build blocked."
    exit 1
else
    log_info "QUALITY GATE PASSED - All checks successful."
    exit 0
fi
