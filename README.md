<!--
SPDX-License-Identifier: CC-BY-SA-4.0
SPDX-FileCopyrightText: 2024 com.github.metavacua
-->

# subclass

An Ironclad licensed Maven archetype for the `com.github.metavacua` organization.

## Overview

The `subclass` project serves as a reference implementation for:

- **Strict licensing compliance** with dual-licensing (AGPL-3.0-only for code, CC-BY-SA-4.0 for documentation)
- **REUSE specification adherence** with SPDX headers on all files
- **Automated quality gates** enforcing syntactic and semantic correctness
- **Modern Java 21** development practices
- **Apache Jena 5.0.0**, **JavaPoet 1.13.0**, and **KeY** integration

## Quick Start

### Prerequisites

Run the system setup script to install all required dependencies:

```bash
./scripts/system-setup.sh
```

This will install:
- OpenJDK 21
- Apache Maven
- libxml2-utils (for `xmllint`)
- pipx and the REUSE tool

### Fetch Dependencies

Prefetch all project dependencies:

```bash
./scripts/project-deps.sh
```

### Build and Verify

Run the quality gate script (includes all validations):

```bash
./scripts/code-quality.sh
```

Or manually with Maven:

```bash
mvn verify -P4
```

## Project Structure

```
.
├── .github/
│   ├── workflows/
│   │   └── ci.yml              # Ironclad CI with SHA-pinned actions
│   └── PULL_REQUEST_TEMPLATE.md
├── LICENSES/
│   ├── AGPL-3.0-only.txt       # Code license
│   └── CC-BY-SA-4.0.txt        # Documentation license
├── scripts/
│   ├── system-setup.sh         # Idempotent environment setup
│   ├── project-deps.sh         # Dependency prefetching
│   └── code-quality.sh         # Hard quality gate
├── src/
│   ├── main/
│   │   ├── docbook/            # Documentation (CC-BY-SA-4.0)
│   │   │   ├── main.xml
│   │   │   └── fragment.xml
│   │   └── java/               # Source code (AGPL-3.0-only)
│   │       └── com/github/metavacua/subclass/
│   │           └── App.java
│   └── test/
│       └── java/
└── pom.xml                     # Maven configuration with dual licenseSets
```

## Licensing

This project uses dual licensing:

| Content Type | License | SPDX ID |
|--------------|---------|---------|
| Source code (`*.java`, `pom.xml`, `scripts/*.sh`) | GNU Affero General Public License v3.0 only | `AGPL-3.0-only` |
| Documentation (`src/main/docbook/*.xml`, `README.md`) | Creative Commons Attribution-ShareAlike 4.0 International | `CC-BY-SA-4.0` |

All files include SPDX headers for REUSE compliance.

## Pull Request Requirements

Every PR must:

1. **Execute all three scripts** and include logs in the PR description:
   - `./scripts/system-setup.sh`
   - `./scripts/project-deps.sh`
   - `./scripts/code-quality.sh`

2. **Include SPDX headers** on all new files

3. **Pass the Ironclad CI gates**:
   - License compliance check (REUSE)
   - XML C14N validation
   - Maven build with `-P4` (bounded parallelism)
   - Syntactic attestation of JAR contents

## CI/CD

The "Ironclad Syntactic Gates PERFECTED" workflow includes:

- **SHA-pinned actions** for supply chain security
- **No caching** to ensure reproducible builds
- **Bounded parallelism** (`-P4`) to prevent resource exhaustion
- **Syntactic attestation** verifying JAR content C14N compliance

## Dependencies

- **Apache Jena 5.0.0** - RDF processing framework
- **JavaPoet 1.13.0** - Java source code generation
- **KeY 2.11.0** - Theorem prover (from https://key-project.org/key-mvn-repo/)

## Maven Plugins

- `com.mycila:license-maven-plugin` - Dual license header enforcement
- `org.codehaus.mojo:xml-maven-plugin` - XML validation
- `maven-antrun-plugin` - DocBook processing (bound to verify phase)

## Contributing

See the [Pull Request Template](.github/PULL_REQUEST_TEMPLATE.md) for detailed requirements.

---

<p align="center">
  <sub>Built with ⚔️ Ironclad compliance principles</sub>
</p>
