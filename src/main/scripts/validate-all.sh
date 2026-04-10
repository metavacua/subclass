#!/bin/bash
#
# DocBook 5.2 Full Tiered Validation Script
# All Layers: DTD → XSD → RELAX NG → Schematron → C14N → Catalog → NVDL
#
# Runs the complete validation pipeline for all layers.
#
# Usage: ./validate-all.sh [options]
#   --help       Show this help message
#   --verbose    Verbose output
#   --stop-on-fail  Stop on first failure
#   --layer      Run specific layer only (dtd|xsd|relaxng|schematron|c14n|catalog|nvdl)
#

set -euo pipefail

# Configuration - go up 3 levels from scripts/ to project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../../" && pwd)"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
BOLD='\033[1m'
NC='\033[0m'

# Options
VERBOSE=false
STOP_ON_FAIL=false
LAYER=""

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --help)
            echo "DocBook 5.2 Full Tiered Validation Script"
            echo ""
            echo "Usage: $0 [options]"
            echo ""
            echo "Options:"
            echo "  --help           Show this help message"
            echo "  --verbose        Verbose output"
            echo "  --stop-on-fail   Stop on first failure"
            echo "  --layer LAYER    Run specific layer only"
            echo ""
            echo "Layers:"
            echo "  dtd         Layer 1: DTD Validation"
            echo "  xsd         Layer 2: XSD Validation"
            echo "  relaxng     Layer 3: RELAX NG Validation"
            echo "  schematron  Layer 4: Schematron Validation"
            echo "  c14n        Layer 5: Exclusive C14N Validation"
            echo "  catalog     Layer 6: XML Catalog Validation"
            echo "  nvdl        Layer 7: NVDL Validation"
            exit 0
            ;;
        --verbose)
            VERBOSE=true
            shift
            ;;
        --stop-on-fail)
            STOP_ON_FAIL=true
            shift
            ;;
        --layer)
            LAYER="$2"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

echo "=============================================="
echo -e "${BOLD}DocBook 5.2 Full Tiered Validation${NC}"
echo "=============================================="
echo ""

# Track overall status
OVERALL_STATUS=0

# Function to run a validation layer
run_layer() {
    local layer=$1
    local script=$2
    local options=""
    
    [ "$VERBOSE" = true ] && options="--verbose"
    
    if [ ! -f "$script" ]; then
        echo -e "${RED}Error: Script not found: $script${NC}"
        return 1
    fi
    
    # Make script executable
    chmod +x "$script"
    
    # Run validation
    if "$script" $options; then
        return 0
    else
        return 1
    fi
}

# Layer definitions - note: use absolute paths via PROJECT_ROOT
declare -A LAYERS=(
    ["dtd"]="src/main/scripts/validate-dtd.sh"
    ["xsd"]="src/main/scripts/validate-xsd.sh"
    ["relaxng"]="src/main/scripts/validate-relaxng.sh"
    ["schematron"]="src/main/scripts/validate-schematron.sh"
    ["c14n"]="src/main/scripts/validate-c14n.sh"
    ["catalog"]="src/main/scripts/validate-catalog.sh"
    ["nvdl"]="src/main/scripts/validate-nvdl.sh"
)

# Run all layers or specific layer
if [ -n "$LAYER" ]; then
    # Run specific layer
    if [ -z "${LAYERS[$LAYER]:-}" ]; then
        echo -e "${RED}Error: Unknown layer: $LAYER${NC}"
        exit 1
    fi
    
    echo -e "${BLUE}Running layer: $LAYER${NC}"
    echo ""
    
    if run_layer "$LAYER" "$PROJECT_ROOT/${LAYERS[$LAYER]}"; then
        echo -e "${GREEN}✓ Layer $LAYER passed${NC}"
    else
        echo -e "${RED}✗ Layer $LAYER failed${NC}"
        OVERALL_STATUS=1
    fi
else
    # Run all layers in order
    echo "Running all validation layers..."
    echo ""
    
    for layer in dtd xsd relaxng schematron c14n catalog nvdl; do
        echo -e "${BOLD}--- Layer: ${layer^^} ---${NC}"
        
        if run_layer "$layer" "$PROJECT_ROOT/${LAYERS[$layer]}"; then
            echo -e "${GREEN}✓ Layer $layer passed${NC}"
        else
            echo -e "${RED}✗ Layer $layer failed${NC}"
            OVERALL_STATUS=1
            
            if [ "$STOP_ON_FAIL" = true ]; then
                echo ""
                echo -e "${YELLOW}Stopping due to --stop-on-fail${NC}"
                break
            fi
        fi
        
        echo ""
    done
fi

# Summary
echo "=============================================="
echo -e "${BOLD}Validation Summary${NC}"
echo "=============================================="

if [ $OVERALL_STATUS -eq 0 ]; then
    echo -e "${GREEN}✓ All validation layers passed${NC}"
    exit 0
else
    echo -e "${RED}✗ Some validation layers failed${NC}"
    exit 1
fi
