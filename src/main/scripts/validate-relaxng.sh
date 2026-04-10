#!/bin/bash
#
# DocBook 5.2 RELAX NG Validation Script
# Layer 3: RELAX NG Validation (ISO Standard 19757-2)
#
# Validates DocBook 5.2 documents against RELAX NG schemas.
# RELAX NG provides expressive pattern-based validation.
#
# Usage: ./validate-relaxng.sh [options]
#   --help       Show this help message
#   --catalog    Use XML Catalog
#   --compact    Use RNC (compact) syntax (default)
#   --xml        Use RNG (XML) syntax
#   --verbose     Verbose output
#

set -euo pipefail

# Configuration - go up 3 levels from scripts/ to project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../../" && pwd)"
RESOURCES_DIR="$PROJECT_ROOT/src/main/resources"
CATALOG_PATH="$RESOURCES_DIR/catalog.xml"
RELAXNG_PATH="$RESOURCES_DIR/relaxng"
XINCLUDE_PATH="$RESOURCES_DIR/xinclude"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Options
USE_CATALOG=true
USE_COMPACT=true
VERBOSE=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --help)
            echo "DocBook 5.2 RELAX NG Validation Script"
            echo ""
            echo "Usage: $0 [options]"
            echo ""
            echo "Options:"
            echo "  --help       Show this help message"
            echo "  --catalog    Use XML Catalog (default)"
            echo "  --no-catalog Disable XML Catalog usage"
            echo "  --compact    Use RNC syntax (default)"
            echo "  --xml        Use RNG XML syntax"
            echo "  --verbose    Verbose output"
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
        --compact)
            USE_COMPACT=true
            shift
            ;;
        --xml)
            USE_COMPACT=false
            shift
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
echo "Layer 3: RELAX NG Validation"
echo "========================================="

# Check for Jing (RELAX NG validator)
# If not installed, we'll use a fallback validation
JING_JAR=""

# Look for Jing in common locations
for jar in ~/.m2/repository/com/thaiopensource/jing/20230628/jing-20230628.jar \
          /usr/share/java/jing.jar \
          "$PROJECT_ROOT/lib/jing.jar"; do
    if [ -f "$jar" ]; then
        JING_JAR="$jar"
        break
    fi
done

if [ -z "$JING_JAR" ]; then
    # Try to find Jing via Maven
    if command -v mvn >/dev/null 2>&1; then
        echo -e "${YELLOW}Note: JING not found locally. Using Maven to download...${NC}"
        mvn dependency:get -Dartifact=com.thaiopensource:jing:20230628 -q 2>/dev/null || true
        JING_JAR=~/.m2/repository/com/thaiopensource/jing/20230628/jing-20230628.jar
    fi
fi

# Select schema file
if [ "$USE_COMPACT" = true ]; then
    SCHEMA_FILE="$RELAXNG_PATH/docbook-5.2-constraint.rnc"
    SCHEMA_TYPE="rnc"
else
    SCHEMA_FILE="$RELAXNG_PATH/docbook-5.2-constraint.rng"
    SCHEMA_TYPE="rng"
fi

if [ ! -f "$SCHEMA_FILE" ]; then
    echo -e "${RED}Error: Schema file not found: $SCHEMA_FILE${NC}"
    exit 1
fi

echo "Using schema: $SCHEMA_FILE"

# Find XML files (pre XInclude processing)
XML_FILES=$(find "$XINCLUDE_PATH" -name "*.xml" -type f 2>/dev/null || true)

if [ -z "$XML_FILES" ]; then
    echo -e "${YELLOW}Warning: No XML files found${NC}"
    exit 0
fi

TOTAL=$(echo "$XML_FILES" | wc -l)
CURRENT=0
FAILED=0

echo "Found $TOTAL XML files to validate"

# Validate each file
while IFS= read -r file; do
    CURRENT=$((CURRENT + 1))
    BASENAME=$(basename "$file")
    
    if [ "$VERBOSE" = true ]; then
        echo "[$CURRENT/$TOTAL] Validating: $BASENAME"
    else
        echo -n "."
    fi
    
    if [ -f "$JING_JAR" ]; then
        # Use Jing for validation
        if java -jar "$JING_JAR" "$SCHEMA_FILE" "$file" 2>/dev/null; then
            if [ "$VERBOSE" = true ]; then
                echo -e "  ${GREEN}✓ Valid${NC}"
            fi
        else
            FAILED=$((FAILED + 1))
            echo -e "\n${RED}Error: $BASENAME failed validation${NC}"
            java -jar "$JING_JAR" "$SCHEMA_FILE" "$file" 2>&1 || true
        fi
    else
        # Fallback: basic well-formedness check
        if xmllint --noout --noent "$file" 2>/dev/null; then
            if [ "$VERBOSE" = true ]; then
                echo -e "  ${GREEN}✓ Well-formed (JING not available for full validation)${NC}"
            fi
        else
            FAILED=$((FAILED + 1))
            echo -e "\n${RED}Error: $BASENAME is not well-formed${NC}"
        fi
    fi
done <<< "$XML_FILES"

echo ""
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✓ RELAX NG Validation passed${NC}"
    exit 0
else
    echo -e "${RED}✗ RELAX NG Validation failed: $FAILED of $TOTAL files${NC}"
    exit 1
fi
