# DocBook 5.2 Validation Pipeline

A comprehensive DocBook 5.2 + XInclude validation architecture with tiered validation, anchored on Maven POM as Layer 0.

## Overview

This project implements a 7-layer validation pipeline for DocBook 5.2 documents with XInclude support:

| Layer | Technology | Purpose |
|-------|------------|---------|
| Layer 0 | Maven POM | Project foundation and build configuration |
| Layer 1 | DTD | Legacy compatibility and basic structure |
| Layer 2 | W3C XSD | W3C standard schema validation |
| Layer 3 | RELAX NG | ISO standard validation with expressive patterns |
| Layer 4 | Schematron | ISO standard for rule-based validation |
| Layer 5 | Exclusive C14N | Canonicalization for digital signatures |
| Layer 6 | XML Catalogs | Entity resolution and system ID mapping |
| Layer 7 | NVDL | Namespace-based validation dispatching |

## Features

- **Maven Integration**: xml-maven-plugin bound to validate phase
- **Tiered Validation**: Each layer validates a specific aspect
- **XInclude Support**: Document composition from multiple fragments
- **Offline Validation**: XML Catalogs enable offline schema resolution
- **CI/CD Ready**: GitHub Actions workflow for automated validation

## Project Structure

```
.
├── pom.xml                          # Layer 0: Maven Project Object Model
├── .github/
│   └── workflows/
│       └── validate.yml              # CI/CD Pipeline
├── src/main/
│   ├── resources/
│   │   ├── catalog.xml              # XML Catalog for entity resolution
│   │   ├── catalog-local.xml        # Local catalog additions
│   │   ├── docbook/                 # DocBook 5.2 schemas
│   │   │   ├── docbook-5.2-nvdl.xml
│   │   │   └── xinclude.rnc
│   │   ├── relaxng/                 # RELAX NG constraint schemas
│   │   │   ├── docbook-5.2-constraint.rnc
│   │   │   └── docbook-5.2-constraint.rng
│   │   ├── schematron/              # Schematron rules
│   │   │   └── docbook-5.2-rules.sch
│   │   └── xinclude/                # XInclude fragments
│   │       ├── book.xml             # Main document
│   │       ├── chapter-preface.xml
│   │       ├── chapter-intro.xml
│   │       └── chapter-architecture.xml
│   └── scripts/                     # Validation scripts
│       ├── validate-all.sh          # Full pipeline
│       ├── validate-dtd.sh          # Layer 1
│       ├── validate-xsd.sh          # Layer 2
│       ├── validate-relaxng.sh      # Layer 3
│       ├── validate-schematron.sh    # Layer 4
│       ├── validate-c14n.sh         # Layer 5
│       ├── validate-catalog.sh      # Layer 6
│       └── validate-nvdl.sh         # Layer 7
└── assembly/
    └── docbook-dist.xml             # Maven Assembly descriptor
```

## Usage

### Validate with Maven

```bash
# Validate all layers
mvn validate

# Run specific profile
mvn validate -Pvalidate-all
mvn validate -Pvalidate-relaxng
```

### Validate with Scripts

```bash
# Full validation pipeline
./src/main/scripts/validate-all.sh

# Single layer validation
./src/main/scripts/validate-dtd.sh --verbose
./src/main/scripts/validate-relaxng.sh --verbose
./src/main/scripts/validate-schematron.sh --verbose
```

### Available Script Options

```bash
--help       Show help message
--verbose    Verbose output
--catalog    Use XML Catalog (default)
--no-catalog Disable XML Catalog
```

## Validation Layers

### Layer 1: DTD Validation
Validates using Document Type Definition for legacy compatibility.

### Layer 2: XSD Validation
Validates using W3C XML Schema for strong typing and constraints.

### Layer 3: RELAX NG Validation
Validates using ISO standard 19757-2 with expressive patterns.

### Layer 4: Schematron Validation
Validates using ISO standard 19757-3 for rule-based validation.

### Layer 5: Exclusive C14N
Canonicalizes documents for digital signature and comparison.

### Layer 6: XML Catalogs
Validates catalog files and tests entity resolution.

### Layer 7: NVDL Validation
Validates using namespace-based dispatching.

## Requirements

- Java 17+
- Maven 3.9+
- libxml2-utils (for xmllint)

## Dependencies

- **xml-maven-plugin**: XML validation in Maven lifecycle
- **JING**: RELAX NG validator
- **Saxon HE**: XSLT processor for Schematron
- **xmlresolver**: XML Catalog resolution

## License

See project documentation for license information.

## References

- [DocBook 5.2 Specification](https://docbook.org/specs/docbook-5.2-spec.html)
- [RELAX NG](https://relaxng.org/)
- [Schematron](https://schematron.com/)
- [XInclude 1.1](https://www.w3.org/TR/xinclude-11/)
- [XML Catalogs](https://www.oasis-open.org/specs/tm9901.htm)
