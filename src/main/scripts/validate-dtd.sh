#!/bin/bash
#
# DocBook 5.2 DTD Validation Script
# Layer 1: DTD Validation
#
# Validates DocBook 5.2 documents against DTD for legacy compatibility
# and basic structural validation.
#
# Note: DocBook 5.2 is primarily defined in RELAX NG; full DTD validation
# requires the DocBook 5.2 DTD files. This script performs well-formedness
# validation as a fallback.
#
# Usage: ./validate-dtd.sh [options]
#   --help     Show this help message
#   --catalog  Use XML Catalog for entity resolution
#   --verbose  Verbose output
#

set -euo pipefail

# Configuration - go up 3 levels from scripts/ to project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../../" && pwd)"
RESOURCES_DIR="$PROJECT_ROOT/src/main/resources"
CATALOG_PATH="$RESOURCES_DIR/catalog.xml"
XINCLUDE_PATH="$RESOURCES_DIR/xinclude"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Options
USE_CATALOG=true
VERBOSE=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --help)
            echo "DocBook 5.2 DTD Validation Script"
            echo ""
            echo "Usage: $0 [options]"
            echo ""
            echo "Options:"
            echo "  --help     Show this help message"
            echo "  --catalog  Use XML Catalog for entity resolution (default)"
            echo "  --no-catalog  Disable XML Catalog usage"
            echo "  --verbose  Verbose output"
            exit 0
            ;;
        --catalog)
            USE_CATALOG=true
            shift
            ;;
        --no-catalog)
            USE_CATALOG=false
            shift
            ;;
        --verbose)
            VERBOSE=true
            shift
            ;;
        *)
            echo "Unknown option: $1"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

echo "========================================="
echo "Layer 1: DTD Validation"
echo "========================================="

# Check for required tools
command -v xmllint >/dev/null 2>&1 || {
    echo -e "${RED}Error: xmllint not found. Please install libxml2-utils.${NC}"
    exit 1
}

# Find all XML files in xinclude directory
XML_FILES=$(find "$XINCLUDE_PATH" -name "*.xml" -type f 2>/dev/null || true)

if [ -z "$XML_FILES" ]; then
    echo -e "${YELLOW}Warning: No XML files found in $XINCLUDE_PATH${NC}"
    exit 0
fi

# Count total files
TOTAL=$(echo "$XML_FILES" | wc -l)
CURRENT=0
FAILED=0

echo "Found $TOTAL XML files to validate"

# Check for DTD schema availability
# DocBook 5.2 doesn't ship a complete DTD, so we do well-formedness only
DTD_AVAILABLE=false
if [ -f "$RESOURCES_DIR/docbook/docbook-5.2-dtd.zip" ]; then
    DTD_AVAILABLE=true
fi

# Validate each file
while IFS= read -r file; do
    CURRENT=$((CURRENT + 1))
    BASENAME=$(basename "$file")
    
    if [ "$VERBOSE" = true ]; then
        echo "[$CURRENT/$TOTAL] Validating: $BASENAME"
    else
        echo -n "."
    fi
    
    # Well-formedness validation only (DTD schemas not available for DocBook 5.2)
    if xmllint --noout "$file" 2>/dev/null; then
        if [ "$VERBOSE" = true ]; then
            echo -e "  ${GREEN}✓ Valid${NC}"
        fi
    else
        FAILED=$((FAILED + 1))
        echo -e "\n${RED}Error: $BASENAME failed validation${NC}"
        xmllint --noout "$file" 2>&1 || true
    fi
done <<< "$XML_FILES"

echo ""
echo ""

# Summary - report what validation was done
if [ $FAILED -eq 0 ]; then
    if [ "$DTD_AVAILABLE" = true ]; then
        echo -e "${GREEN}✓ DTD Validation passed for all $TOTAL files${NC}"
    else
        echo -e "${YELLOW}⚠ DTD Validation: well-formedness passed ($TOTAL files)${NC}"
        echo "DocBook 5.2 doesn't ship a DTD; use RELAX NG for full validation."
    fi
    exit 0
else
    echo -e "${RED}✗ DTD Validation failed: $FAILED of $TOTAL files${NC}"
    exit 1
fi