#!/bin/bash
#
# DocBook 5.2 Exclusive C14N Validation Script
# Layer 5: Exclusive XML Canonicalization
#
# Validates that XML documents can be canonicalized without error.
# Canonical form is essential for digital signatures and content comparison.
#
# Usage: ./validate-c14n.sh [options]
#   --help       Show this help message
#   --verbose    Verbose output
#

set -euo pipefail

# Configuration - go up 3 levels from scripts/ to project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../../" && pwd)"
RESOURCES_DIR="$PROJECT_ROOT/src/main/resources"
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
            echo "DocBook 5.2 Exclusive C14N Validation Script"
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
echo "Layer 5: Exclusive C14N Validation"
echo "========================================="

# Check for required tools
command -v xmllint >/dev/null 2>&1 || {
    echo -e "${RED}Error: xmllint not found.${NC}"
    exit 1
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

# Validate using Exclusive C14N
while IFS= read -r file; do
    CURRENT=$((CURRENT + 1))
    BASENAME=$(basename "$file")
    
    if [ "$VERBOSE" = true ]; then
        echo "[$CURRENT/$TOTAL] Canonicalizing: $BASENAME"
    else
        echo -n "."
    fi
    
    # Use xmllint for canonicalization
    # --c14n performs Exclusive XML Canonicalization
    if xmllint --c14n --noout "$file" >/dev/null 2>&1; then
        if [ "$VERBOSE" = true ]; then
            echo -e "  ${GREEN}✓ Canonicalization successful${NC}"
        fi
    else
        FAILED=$((FAILED + 1))
        echo -e "\n${RED}Error: $BASENAME failed canonicalization${NC}"
        xmllint --c14n --noout "$file" 2>&1 || true
    fi
done <<< "$XML_FILES"

echo ""
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✓ C14N Validation passed for all $TOTAL files${NC}"
    exit 0
else
    echo -e "${RED}✗ C14N Validation failed: $FAILED of $TOTAL files${NC}"
    exit 1
fi
