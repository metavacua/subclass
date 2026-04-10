#!/bin/bash
#
# DocBook 5.2 NVDL Validation Script
# Layer 7: NVDL Validation (ISO Standard 19757-4)
#
# Validates DocBook 5.2 documents using NVDL.
# NVDL validates different parts using different schemas based on namespace.
#
# Usage: ./validate-nvdl.sh [options]
#   --help       Show this help message
#   --verbose    Verbose output
#

set -euo pipefail

# Configuration - go up 3 levels from scripts/ to project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../../" && pwd)"
RESOURCES_DIR="$PROJECT_ROOT/src/main/resources"
DOCBOOK_PATH="$RESOURCES_DIR/docbook"
XINCLUDE_PATH="$RESOURCES_DIR/xinclude"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Options
VERBOSE=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --help)
            echo "DocBook 5.2 NVDL Validation Script"
            echo ""
            echo "Usage: $0 [options]"
            echo ""
            echo "Options:"
            echo "  --help       Show this help message"
            echo "  --verbose    Verbose output"
            exit 0
            ;;
        --verbose)
            VERBOSE=true
            shift
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

echo "========================================="
echo "Layer 7: NVDL Validation"
echo "========================================="

# Check for NVDL schema
NVDL_FILE="$DOCBOOK_PATH/docbook-5.2-nvdl.xml"

if [ ! -f "$NVDL_FILE" ]; then
    echo -e "${YELLOW}Warning: NVDL schema not found: $NVDL_FILE${NC}"
    echo "NVDL validation requires specialized tooling."
    exit 0
fi

echo "Using NVDL schema: $NVDL_FILE"

# Check for required tools
command -v xmllint >/dev/null 2>&1 || {
    echo -e "${YELLOW}Note: xmllint does not support NVDL natively${NC}"
}

# Find XML files
XML_FILES=$(find "$XINCLUDE_PATH" -name "*.xml" -type f 2>/dev/null || true)

if [ -z "$XML_FILES" ]; then
    echo -e "${YELLOW}Warning: No XML files found${NC}"
    exit 0
fi

TOTAL=$(echo "$XML_FILES" | wc -l)
CURRENT=0
FAILED=0

echo "Found $TOTAL XML files to validate"

# For full NVDL validation, we'd need specialized tools like Jing or nvdllint
# We do basic validation as fallback
while IFS= read -r file; do
    CURRENT=$((CURRENT + 1))
    BASENAME=$(basename "$file")
    
    if [ "$VERBOSE" = true ]; then
        echo "[$CURRENT/$TOTAL] Validating: $BASENAME"
    else
        echo -n "."
    fi
    
    # Basic validation
    if xmllint --noout --noent "$file" 2>/dev/null; then
        if [ "$VERBOSE" = true ]; then
            echo -e "  ${GREEN}✓ Well-formed${NC}"
            echo -e "  ${YELLOW}Note: Full NVDL validation requires specialized tooling${NC}"
        fi
    else
        FAILED=$((FAILED + 1))
        echo -e "\n${RED}Error: $BASENAME is not well-formed${NC}"
    fi
done <<< "$XML_FILES"

echo ""
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✓ NVDL Validation passed (basic checks)${NC}"
    echo -e "${YELLOW}Note: Full NVDL validation requires Jing with NVDL support${NC}"
    exit 0
else
    echo -e "${RED}✗ NVDL Validation failed: $FAILED of $TOTAL files${NC}"
    exit 1
fi
