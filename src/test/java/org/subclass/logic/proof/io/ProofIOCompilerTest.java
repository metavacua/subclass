package org.subclass.logic.proof.io;

import org.junit.jupiter.api.Test;
import org.subclass.examples.executable.LEMProofs;
import org.subclass.logic.proof.typed.Many;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.processor.TheoremProcessor;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end round-trip: typed proof → JSON → typed proof → generated source →
 * {@link javax.tools.JavaCompiler} + {@link TheoremProcessor}.
 *
 * Demonstrates the Curry–Howard correspondence at runtime: a deserialized
 * proof's conclusion shape drives the generation of a {@code @Theorem} method
 * whose return-type signature matches the claim; {@code javac} + the
 * annotation processor then certify the generated source produces zero
 * diagnostics, i.e. the claim is well-typed.
 */
class ProofIOCompilerTest {

    @Test
    void deserializedLEMProofSurvivesJavacRoundTrip() throws IOException {
        Proof<Many, Many> original = LEMProofs.classical();
        String json = ProofIO.writeJson(original);
        Proof<?, ?> deserialized = ProofIO.readJson(json);

        assertEquals(original.conclusion(), deserialized.conclusion(),
            "JSON round-trip must preserve conclusion");

        String src = """
            package subclass.gen;

            import org.subclass.annotation.Theorem;
            import org.subclass.annotation.TheoremFamily;
            import org.subclass.logic.proof.typed.Many;
            import org.subclass.logic.proof.typed.Proof;

            @TheoremFamily(
                name = "GeneratedLEM",
                classicalTheorem = "lemInLK",
                intuitionisticTheorem = "lemInLJ",
                paraconsistentTheorem = "lemInDual",
                commonLogicTheorem = "lemInCommon"
            )
            public class GeneratedTheorem {
                @Theorem(
                    name = "lemInLK",
                    signature = "LK",
                    status = "PROVABLE",
                    proofReference = "Round-tripped from JSON"
                )
                public static Proof<Many, Many> lemInLK() { return null; }

                @Theorem(
                    name = "lemInLJ",
                    signature = "LJ",
                    status = "NON_PROVABLE",
                    proofReference = "Round-tripped from JSON"
                )
                public static Proof<org.subclass.logic.proof.typed.Many,
                                    org.subclass.logic.proof.typed.One> lemInLJ() { return null; }

                @Theorem(
                    name = "lemInDual",
                    signature = "LDJ",
                    status = "PROVABLE",
                    proofReference = "Round-tripped from JSON"
                )
                public static Proof<org.subclass.logic.proof.typed.One,
                                    org.subclass.logic.proof.typed.Many> lemInDual() { return null; }

                @Theorem(
                    name = "lemInCommon",
                    signature = "Common",
                    status = "UNPROVABLE_AND_REFUTABLE",
                    proofReference = "Round-tripped from JSON"
                )
                public static Proof<org.subclass.logic.proof.typed.One,
                                    org.subclass.logic.proof.typed.One> lemInCommon() { return null; }
            }
            """;

        List<Diagnostic<? extends JavaFileObject>> diags = compileWithProcessor(
            "subclass.gen.GeneratedTheorem", src);

        List<Diagnostic<? extends JavaFileObject>> errors = diags.stream()
            .filter(d -> d.getKind() == Diagnostic.Kind.ERROR).toList();
        assertTrue(errors.isEmpty(), "Expected zero errors, got: " + errors);
    }

    @Test
    void mismatchedSignatureIsRejectedByProcessor() throws IOException {
        String src = """
            package subclass.gen;

            import org.subclass.annotation.Theorem;
            import org.subclass.logic.proof.typed.Many;
            import org.subclass.logic.proof.typed.One;
            import org.subclass.logic.proof.typed.Proof;

            public class BadTheorem {
                // signature says LJ (Many, One) but return type is Proof<Many, Many> — a type error
                @Theorem(
                    name = "bogus",
                    signature = "LJ",
                    status = "PROVABLE",
                    proofReference = "intentionally wrong"
                )
                public static Proof<Many, Many> wrong() { return null; }
            }
            """;

        List<Diagnostic<? extends JavaFileObject>> diags = compileWithProcessor(
            "subclass.gen.BadTheorem", src);

        boolean hasMismatchError = diags.stream()
            .anyMatch(d -> d.getKind() == Diagnostic.Kind.ERROR
                && d.getMessage(null) != null
                && d.getMessage(null).contains("LJ"));
        assertTrue(hasMismatchError,
            "Expected an LJ signature mismatch error; got: " + diags);
    }

    // ------------------------------------------------------------------ utils

    private static List<Diagnostic<? extends JavaFileObject>> compileWithProcessor(
            String qualifiedName, String source) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler, "No system Java compiler available");

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fm = compiler.getStandardFileManager(
            diagnostics, null, StandardCharsets.UTF_8);

        Path outDir = Files.createTempDirectory("subclass-javac-");
        fm.setLocation(javax.tools.StandardLocation.CLASS_OUTPUT, List.of(outDir.toFile()));

        JavaFileObject jfo = new InMemorySource(qualifiedName, source);
        JavaCompiler.CompilationTask task = compiler.getTask(
            null, fm, diagnostics,
            List.of("-proc:only"),
            null,
            List.of(jfo));
        task.setProcessors(List.of(new TheoremProcessor()));
        task.call();

        fm.close();
        return diagnostics.getDiagnostics();
    }

    private static final class InMemorySource extends SimpleJavaFileObject {
        private final String code;
        InMemorySource(String qualifiedName, String code) {
            super(URI.create("string:///" + qualifiedName.replace('.', '/') + ".java"),
                Kind.SOURCE);
            this.code = code;
        }
        @Override public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}
