#!/bin/bash
#
# Canonicalize XML using Exclusive XML Canonicalization (exc-c14n)
# This script uses libxml2's xmllint to perform canonicalization
#

set -e

usage() {
    echo "Usage: $0 <input-xml-file> [output-xml-file]"
    echo ""
    echo "Options:"
    echo "  input-xml-file    Input XML file to canonicalize"
    echo "  output-xml-file   Output file (default: stdout)"
    echo ""
    echo "This script uses xmllint with --exc-c14n for Exclusive XML Canonicalization."
    exit 1
}

validate_xmllint() {
    if ! command -v xmllint &> /dev/null; then
        echo "Error: xmllint (from libxml2) is required but not installed." >&2
        exit 1
    fi
    
    # Check for exc-c14n support
    if ! xmllint --help 2>&1 | grep -q "exc-c14n"; then
        echo "Error: xmllint does not support --exc-c14n option." >&2
        echo "Please ensure you have libxml2 with C14N support installed." >&2
        exit 1
    fi
}

canonicalize() {
    local input="$1"
    local output="$2"
    
    if [ ! -f "$input" ]; then
        echo "Error: Input file '$input' not found." >&2
        exit 1
    fi
    
    if [ -z "$output" ]; then
        xmllint --exc-c14n --nocdata "$input" - 
    else
        xmllint --exc-c14n --nocdata "$input" -o "$output"
        echo "Canonicalized output written to: $output"
    fi
}

# Main script
if [ $# -lt 1 ]; then
    usage
fi

INPUT_FILE="$1"
OUTPUT_FILE="$2"

validate_xmllint
canonicalize "$INPUT_FILE" "$OUTPUT_FILE"
