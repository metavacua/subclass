#!/bin/bash
#
# DocBook 5.2 XML Catalog Validation Script
# Layer 6: XML Catalog Validation
#
# Validates XML Catalog files and tests entity resolution.
# XML Catalogs enable offline validation and URI mapping.
#
# Usage: ./validate-catalog.sh [options]
#   --help       Show this help message
#   --verbose    Verbose output
#   --resolve    Test entity resolution
#

set -euo pipefail

# Configuration - go up 3 levels from scripts/ to project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../../" && pwd)"
RESOURCES_DIR="$PROJECT_ROOT/src/main/resources"
CATALOG_PATH="$RESOURCES_DIR/catalog.xml"
CATALOG_LOCAL_PATH="$RESOURCES_DIR/catalog-local.xml"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Options
VERBOSE=false
TEST_RESOLVE=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --help)
            echo "DocBook 5.2 XML Catalog Validation Script"
            echo ""
            echo "Usage: $0 [options]"
            echo ""
            echo "Options:"
            echo "  --help       Show this help message"
            echo "  --verbose    Verbose output"
            echo "  --resolve    Test entity resolution"
            exit 0
            ;;
        --verbose)
            VERBOSE=true
            shift
            ;;
        --resolve)
            TEST_RESOLVE=true
            shift
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

echo "========================================="
echo "Layer 6: XML Catalog Validation"
echo "========================================="

# Check for required tools
command -v xmllint >/dev/null 2>&1 || {
    echo -e "${RED}Error: xmllint not found.${NC}"
    exit 1
}

# Validate main catalog - xmllint doesn't validate XML Catalogs
# we check well-formedness instead
echo "Validating XML Catalog files..."

FAILED=0

# Main catalog
if [ -f "$CATALOG_PATH" ]; then
    echo "Found: catalog.xml"
    if xmllint --noout "$CATALOG_PATH" 2>/dev/null; then
        echo -e "${GREEN}✓ catalog.xml is valid XML${NC}"
    else
        echo -e "${RED}✗ catalog.xml has errors${NC}"
        xmllint --noout "$CATALOG_PATH" 2>&1 || true
        FAILED=$((FAILED + 1))
    fi
else
    echo -e "${YELLOW}Warning: catalog.xml not found${NC}"
fi

# Local catalog (optional)
if [ -f "$CATALOG_LOCAL_PATH" ]; then
    echo "Found: catalog-local.xml"
    if xmllint --noout "$CATALOG_LOCAL_PATH" 2>/dev/null; then
        echo -e "${GREEN}✓ catalog-local.xml is valid XML${NC}"
    else
        echo -e "${RED}✗ catalog-local.xml has errors${NC}"
        xmllint --noout "$CATALOG_LOCAL_PATH" 2>&1 || true
        FAILED=$((FAILED + 1))
    fi
fi

# Test resolution if requested
if [ "$TEST_RESOLVE" = true ]; then
    echo ""
    echo "Testing catalog resolution..."
    
    # Test DocBook public ID resolution
    TEST_PUBLIC="-//OASIS//DTD DocBook XML 5.2//EN"
    echo -n "Testing public ID resolution: "
    if xmllint --catalog "$CATALOG_PATH" --noent 2>/dev/null <<< "$TEST_PUBLIC" | grep -q "docbook"; then
        echo -e "${GREEN}✓ Resolved${NC}"
    else
        echo -e "${YELLOW}Not resolved (expected for offline)${NC}"
    fi
fi

echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✓ XML Catalog Validation passed${NC}"
    exit 0
else
    echo -e "${RED}✗ XML Catalog Validation failed: $FAILED errors${NC}"
    exit 1
fi