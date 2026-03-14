# subclass

Maven project for `com.github.metavacua:subclass`.

## Project Setup

This project uses a script-based workflow for setup and quality assurance.

### Scripts

1. **system-setup.sh** - Installs required system dependencies
   ```bash
   sudo ./scripts/system-setup.sh
   ```

2. **project-deps.sh** - Prefetches Maven dependencies
   ```bash
   ./scripts/project-deps.sh
   ```

3. **code-quality.sh** - Runs quality gate (C14N validation + Maven verify)
   ```bash
   ./scripts/code-quality.sh
   ```

## Dependencies

- Java 21
- Apache Jena (RDF processing)
- JavaPoet (Code generation)
- KeY (Formal verification)

## Documentation

DocBook XML files with XInclude support are located in `src/docbkx/`.
