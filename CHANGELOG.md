<!--
SPDX-FileCopyrightText: 2026 metavacua
SPDX-License-Identifier: CC-BY-SA-4.0
-->

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog 1.1.0](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html).

While the project is pre-`1.0.0`, the public API (the Java library surface,
annotation processor contract, and DocBook module layout) may change in any
`0.MINOR.PATCH` release without further notice. Once `1.0.0` is cut, the usual
SemVer compatibility guarantees take effect.

## [Unreleased]

### Added

- `LICENSE` at the repository root (AGPL-3.0-or-later, for GitHub license
  detection) and a `LICENSES/` directory containing the full text of every
  SPDX identifier referenced by the project:
  - `LICENSES/AGPL-3.0-or-later.txt`
  - `LICENSES/CC-BY-SA-4.0.txt`
- `REUSE.toml` (REUSE specification 3.3) declaring bulk SPDX annotations:
  - `src/**`, `pom.xml`, and `.gitignore` are licensed under
    `AGPL-3.0-or-later`.
  - `README.md`, `CHANGELOG.md`, `MAVEN_PROXY_CONFIG.md`, and `docs/**` are
    licensed under `CC-BY-SA-4.0`.
- `CHANGELOG.md` (this file) following the Keep a Changelog 1.1.0 format.
- README sections documenting the Semantic Versioning commitment, REUSE
  compliance, and the dual-licensing layout.

### Changed

- `README.md` corrected to reflect the actual state of the repository:
  - Removed the "no source files exist yet" claims from the status callout
    and the Building from Source section.
  - Updated the status badge from "pre-alpha – no source yet" to "alpha".
  - Checked off completed roadmap items (`pom.xml`, core interfaces,
    DocBook skeleton).
  - Replaced the aspirational `io/github/metavacua/subclass/...` source tree
    with the actual `org/subclass/...` layout.
  - Replaced `./mvnw` build instructions with `mvn`, since no Maven Wrapper
    is committed to the repository.
  - Linked to `LICENSE`, `LICENSES/`, `CHANGELOG.md`, and the REUSE /
    SemVer specifications.

## Prior history (pre-changelog)

The following summarizes work that landed before this changelog was
introduced. It is recorded here for context only and will not follow the
Keep a Changelog sectioning.

- Core infrastructure for theorem formalization via tetragrams
  (`Tetragram`, `TetragamNode`, `TheoremStatus`).
- Modularized `LogicalSignature` as a Java 14+ record.
- Separated LEM and LNC into distinct dual tetragrams with the correct
  paraconsistent semantics (Urbas–Rauszer, 1990).
- Annotation-based metalanguage for theorem families (`@TheoremFamily`,
  `@Theorem`, `LogicalSignatureDefinition`).
- Compile-time annotation processor (`TheoremProcessor`) that validates
  theorem family structure and enforces the signature-to-return-type mapping
  (`LK → Proof<Many,Many>`, `LJ → Proof<Many,One>`,
  `LDJ → Proof<One,Many>`, `Common → Proof<One,One>`).
- Phantom-typed proof architecture under `org.subclass.logic.proof.typed`
  with capability markers for structural rules (exchange, weakening,
  contraction).
- Legacy string-based proof representation (`org.subclass.logic.proof.Proof`,
  `ProofChecker`, `ProofParser`) retained for reference.
- Documentation generation infrastructure for theorem families
  (`TheoremDocExporter`, `TheoremFamilyTaglet`).
- Maven build fixes, including resolution of circular dependencies in the
  annotation processor configuration and addition of
  `MAVEN_PROXY_CONFIG.md` documenting offline-build prerequisites.
- Initial DocBook 5.1 / XInclude sample chapter under
  `docs/proposal/07-sample-chapter/`.

[Unreleased]: https://github.com/metavacua/subclass/compare/HEAD...HEAD
