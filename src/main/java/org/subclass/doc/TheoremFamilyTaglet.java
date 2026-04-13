package org.subclass.doc;

import com.sun.source.doctree.DocTree;
import jdk.javadoc.doclet.Taglet;
import javax.lang.model.element.Element;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Custom Javadoc taglet for rendering @TheoremFamily annotations.
 *
 * Provides formatted display of theorem family metadata including:
 * - Theorem name and display name
 * - References to theorem methods in each of the four logics
 * - Description of the theorem family
 * - Meta-theorem reference if applicable
 *
 * Usage in Javadoc:
 * {@theoremFamily name="LEM" displayName="Law of Excluded Middle"}
 *
 * This taglet requires Java 9+ (jdk.javadoc API).
 */
public class TheoremFamilyTaglet implements Taglet {

    @Override
    public Set<Taglet.Location> getAllowedLocations() {
        return EnumSet.of(Taglet.Location.TYPE);
    }

    @Override
    public boolean isInlineTag() {
        return false;
    }

    @Override
    public String getName() {
        return "theoremFamily";
    }

    @Override
    public String toString(List<? extends DocTree> tags, Element element) {
        if (tags.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<dt><strong>Theorem Family:</strong></dt>\n");
        sb.append("<dd>\n");
        sb.append("<table border='1' cellpadding='5'>\n");
        sb.append("<tr>\n");
        sb.append("  <th>Logic</th>\n");
        sb.append("  <th>Consistency</th>\n");
        sb.append("  <th>Completeness</th>\n");
        sb.append("  <th>Status</th>\n");
        sb.append("</tr>\n");

        // Note: This is a simplified stub. A full implementation would:
        // 1. Parse the @TheoremFamily annotation from the element
        // 2. Extract theorem method references and their @Theorem statuses
        // 3. Render the tetragram in HTML/table form
        // 4. Link to theorem method documentation

        sb.append("<tr>\n");
        sb.append("  <td>LK (Classical)</td>\n");
        sb.append("  <td>✓</td>\n");
        sb.append("  <td>✓</td>\n");
        sb.append("  <td><em>See documentation</em></td>\n");
        sb.append("</tr>\n");

        sb.append("</table>\n");
        sb.append("</dd>\n");

        return sb.toString();
    }
}
