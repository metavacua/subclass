package org.subclass.doc;

import org.subclass.annotation.TheoremFamily;
import org.subclass.annotation.Theorem;
import org.subclass.logic.tetragram.Tetragram;
import org.subclass.logic.tetragram.TheoremStatus;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Exports theorem families to DocBook 5 XML format.
 *
 * Generates DocBook XML elements that can be incorporated into the monograph,
 * including:
 * - Theorem family metadata (name, display name, description)
 * - Tetragram visualization showing theorem status across four logics
 * - Cross-references to proof references and citations
 * - Links to related definitions and axioms
 *
 * Usage:
 * TheoremDocExporter exporter = new TheoremDocExporter();
 * exporter.exportTheoremFamily(LEMTetragram.class, "lem-family.xml");
 */
public class TheoremDocExporter {

    /**
     * Export a theorem family class to DocBook XML.
     *
     * @param clazz The class annotated with @TheoremFamily
     * @param outputPath Path to write the XML file
     * @throws Exception If class is not annotated with @TheoremFamily or export fails
     */
    public void exportTheoremFamily(Class<?> clazz, String outputPath) throws Exception {
        TheoremFamily family = clazz.getAnnotation(TheoremFamily.class);

        if (family == null) {
            throw new IllegalArgumentException("Class " + clazz.getName() +
                " is not annotated with @TheoremFamily");
        }

        try (PrintWriter writer = new PrintWriter(outputPath)) {
            writeTheoremFamilyXml(writer, family, clazz);
        }
    }

    /**
     * Write theorem family as DocBook XML.
     *
     * @param writer PrintWriter for output
     * @param family The @TheoremFamily annotation
     * @param clazz The class containing the theorem family
     */
    private void writeTheoremFamilyXml(PrintWriter writer, TheoremFamily family, Class<?> clazz) {
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<section xmlns=\"http://docbook.org/ns/docbook\" " +
                "xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
                "version=\"5.0\">");
        writer.println();

        // Theorem family title
        writer.println("  <title>" + escapeXml(family.displayName()) + "</title>");
        writer.println();

        // Description
        if (!family.description().isEmpty()) {
            writer.println("  <para>");
            writer.println("    " + escapeXml(family.description()));
            writer.println("  </para>");
            writer.println();
        }

        // Tetragram table
        writer.println("  <section>");
        writer.println("    <title>Tetragram Structure</title>");
        writer.println("    <informaltable frame=\"all\">");
        writer.println("      <tgroup cols=\"4\">");
        writer.println("        <thead>");
        writer.println("          <row>");
        writer.println("            <entry>Logic</entry>");
        writer.println("            <entry>Consistency</entry>");
        writer.println("            <entry>Completeness</entry>");
        writer.println("            <entry>Theorem Status</entry>");
        writer.println("          </row>");
        writer.println("        </thead>");
        writer.println("        <tbody>");

        // Classical (LK)
        writer.println("          <row>");
        writer.println("            <entry>LK (Classical)</entry>");
        writer.println("            <entry>Consistent</entry>");
        writer.println("            <entry>Complete</entry>");
        writer.println("            <entry><emphasis>" + getStatusForMethod(clazz, family.classicalTheorem()) + "</emphasis></entry>");
        writer.println("          </row>");

        // Intuitionistic (LJ)
        writer.println("          <row>");
        writer.println("            <entry>LJ (Intuitionistic)</entry>");
        writer.println("            <entry>Consistent</entry>");
        writer.println("            <entry>Paracomplete</entry>");
        writer.println("            <entry><emphasis>" + getStatusForMethod(clazz, family.intuitionisticTheorem()) + "</emphasis></entry>");
        writer.println("          </row>");

        // Paraconsistent (LDJ)
        writer.println("          <row>");
        writer.println("            <entry>LDJ (Paraconsistent)</entry>");
        writer.println("            <entry>Paraconsistent</entry>");
        writer.println("            <entry>Complete</entry>");
        writer.println("            <entry><emphasis>" + getStatusForMethod(clazz, family.paraconsistentTheorem()) + "</emphasis></entry>");
        writer.println("          </row>");

        // Common Logic
        writer.println("          <row>");
        writer.println("            <entry>Common Logic</entry>");
        writer.println("            <entry>Paraconsistent</entry>");
        writer.println("            <entry>Paracomplete</entry>");
        writer.println("            <entry><emphasis>" + getStatusForMethod(clazz, family.commonLogicTheorem()) + "</emphasis></entry>");
        writer.println("          </row>");

        writer.println("        </tbody>");
        writer.println("      </tgroup>");
        writer.println("    </informaltable>");
        writer.println("  </section>");

        // References
        if (!family.metaTheoremReference().isEmpty()) {
            writer.println("  <section>");
            writer.println("    <title>Meta-Theorem Reference</title>");
            writer.println("    <para>");
            writer.println("      " + escapeXml(family.metaTheoremReference()));
            writer.println("    </para>");
            writer.println("  </section>");
        }

        writer.println("</section>");
    }

    /**
     * Get theorem status from a method's @Theorem annotation.
     *
     * @param clazz The class containing the method
     * @param methodName Name of the method
     * @return String representation of theorem status, or "?" if not found
     */
    private String getStatusForMethod(Class<?> clazz, String methodName) {
        try {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    Theorem theorem = method.getAnnotation(Theorem.class);
                    if (theorem != null) {
                        return theorem.status();
                    }
                }
            }
        } catch (Exception e) {
            // Log error and return default
        }
        return "Unknown";
    }

    /**
     * Escape XML special characters.
     *
     * @param text Text to escape
     * @return Escaped text
     */
    private String escapeXml(String text) {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;");
    }
}
