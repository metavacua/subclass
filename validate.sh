#!/bin/bash
# DocBook 5.2 Tiered Validation Script
# Validates XInclude fragments at different hierarchy levels:
# - para level: individual paragraphs
# - section level: sections containing paragraphs
# - chapter level: chapters containing sections
# - book level: complete book assembly

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Default XML catalog
export XML_CATALOG_FILES="$PROJECT_ROOT/catalog.xml"

# Track validation status
VALIDATION_FAILED=0

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if xmllint is available
check_dependencies() {
    log_info "Checking dependencies..."
    if ! command -v xmllint &> /dev/null; then
        log_error "xmllint not found. Please install libxml2-utils."
        exit 1
    fi
    log_info "Dependencies OK"
}

# Validate a single XML file (well-formedness check)
validate_xml() {
    local file="$1"
    local description="$2"
    
    if [ ! -f "$file" ]; then
        log_error "File not found: $file"
        return 1
    fi
    
    log_info "Validating $description: $file"
    
    # Check XML well-formedness with XInclude expansion
    if xmllint --noout --xinclude "$file" -o /dev/null 2>/dev/null; then
        log_info "  ✓ $description is well-formed XML"
        return 0
    else
        log_error "  ✗ $description validation failed"
        return 1
    fi
}

# Validate para-level fragments
validate_paras() {
    log_info "=== Tier 1: Validating Paragraph Fragments ==="
    
    local para_dir="$PROJECT_ROOT/fragments/paras"
    
    if [ ! -d "$para_dir" ]; then
        log_warn "No paragraphs directory found"
        return 0
    fi
    
    for para_file in "$para_dir"/*.xml; do
        [ -e "$para_file" ] || continue
        
        if validate_xml "$para_file" "paragraph"; then
            :
        else
            VALIDATION_FAILED=1
        fi
    done
}

# Validate section-level fragments
validate_sections() {
    log_info "=== Tier 2: Validating Section Fragments ==="
    
    local section_dir="$PROJECT_ROOT/fragments/sections"
    
    if [ ! -d "$section_dir" ]; then
        log_warn "No sections directory found"
        return 0
    fi
    
    for section_file in "$section_dir"/*.xml; do
        [ -e "$section_file" ] || continue
        
        if validate_xml "$section_file" "section"; then
            :
        else
            VALIDATION_FAILED=1
        fi
    done
}

# Validate chapter-level fragments
validate_chapters() {
    log_info "=== Tier 3: Validating Chapter Fragments ==="
    
    local chapter_dir="$PROJECT_ROOT/fragments/chapters"
    
    if [ ! -d "$chapter_dir" ]; then
        log_warn "No chapters directory found"
        return 0
    fi
    
    for chapter_file in "$chapter_dir"/*.xml; do
        [ -e "$chapter_file" ] || continue
        
        if validate_xml "$chapter_file" "chapter"; then
            :
        else
            VALIDATION_FAILED=1
        fi
    done
}

# Validate part-level fragments
validate_parts() {
    log_info "=== Tier 4: Validating Part Fragments ==="
    
    local part_dir="$PROJECT_ROOT/fragments/parts"
    
    if [ ! -d "$part_dir" ]; then
        log_warn "No parts directory found"
        return 0
    fi
    
    for part_file in "$part_dir"/*.xml; do
        [ -e "$part_file" ] || continue
        
        if validate_xml "$part_file" "part"; then
            :
        else
            VALIDATION_FAILED=1
        fi
    done
}

# Validate assembly (book-level)
validate_assembly() {
    log_info "=== Tier 5: Validating Book Assembly ==="
    
    local assembly_file="$PROJECT_ROOT/assembly/book.xml"
    
    if [ ! -f "$assembly_file" ]; then
        log_error "Assembly file not found: $assembly_file"
        VALIDATION_FAILED=1
        return
    fi
    
    # Expand XIncludes and validate full document
    log_info "Expanding XIncludes and validating full assembly..."
    
    if xmllint --noout --xinclude "$assembly_file" -o /tmp/expanded_book.xml 2>/dev/null; then
        log_info "  ✓ Book assembly is well-formed XML with XIncludes"
        rm -f /tmp/expanded_book.xml
    else
        log_error "Failed to expand XIncludes"
        VALIDATION_FAILED=1
    fi
}

# Check IDREF integrity across fragments
validate_idrefs() {
    log_info "=== IDREF Integrity Check ==="
    
    # Extract all ID values
    local id_file="/tmp/docbook_ids.txt"
    local ref_file="/tmp/docbook_refs.txt"
    
    find "$PROJECT_ROOT" -name "*.xml" -exec grep -h 'xml:id=' {} \; 2>/dev/null | \
        sed 's/.*xml:id="\([^"]*\)".*/\1/' > "$id_file" || true
    
    # Extract all xref linkends
    find "$PROJECT_ROOT" -name "*.xml" -exec grep -h 'linkend=' {} \; 2>/dev/null | \
        sed 's/.*linkend="\([^"]*\)".*/\1/' > "$ref_file" || true
    
    # Check for broken references
    local broken=0
    while IFS= read -r ref; do
        if ! grep -q "^${ref}$" "$id_file" 2>/dev/null; then
            log_warn "Broken reference: $ref"
            broken=1
        fi
    done < "$ref_file"
    
    rm -f "$id_file" "$ref_file"
    
    if [ $broken -eq 0 ]; then
        log_info "All IDREFs resolved correctly"
    else
        log_error "Some IDREFs could not be resolved"
        VALIDATION_FAILED=1
    fi
}

# Main validation pipeline
main() {
    log_info "Starting DocBook 5.2 Tiered Validation"
    log_info "Project root: $PROJECT_ROOT"
    log_info "XML Catalog: $XML_CATALOG_FILES"
    echo ""
    
    check_dependencies
    
    # Run tiered validation
    validate_paras
    validate_sections
    validate_chapters
    validate_parts
    validate_assembly
    
    # IDREF integrity check
    validate_idrefs
    
    echo ""
    if [ $VALIDATION_FAILED -eq 0 ]; then
        log_info "=== Validation Complete: All checks passed ==="
        exit 0
    else
        log_error "=== Validation Failed: Some checks did not pass ==="
        exit 1
    fi
}

# Handle arguments
case "${1:-}" in
    --help|-h)
        echo "Usage: $0 [command]"
        echo ""
        echo "Commands:"
        echo "  paras      - Validate paragraph fragments only"
        echo "  sections   - Validate section fragments only"
        echo "  chapters   - Validate chapter fragments only"
        echo "  parts      - Validate part fragments only"
        echo "  assembly   - Validate book assembly only"
        echo "  idrefs     - Check IDREF integrity only"
        echo "  all        - Run all validations (default)"
        exit 0
        ;;
    paras)
        check_dependencies
        validate_paras
        ;;
    sections)
        check_dependencies
        validate_sections
        ;;
    chapters)
        check_dependencies
        validate_chapters
        ;;
    parts)
        check_dependencies
        validate_parts
        ;;
    assembly)
        check_dependencies
        validate_assembly
        ;;
    idrefs)
        check_dependencies
        validate_idrefs
        ;;
    all|"")
        main
        ;;
    *)
        log_error "Unknown command: $1"
        echo "Use --help for usage information"
        exit 1
        ;;
esac