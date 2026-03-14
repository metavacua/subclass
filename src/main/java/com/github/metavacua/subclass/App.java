/*
 * SPDX-License-Identifier: AGPL-3.0-only
 * SPDX-FileCopyrightText: 2024 com.github.metavacua
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, version 3 only of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 */

package com.github.metavacua.subclass;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

/**
 * Main application class for the subclass project.
 *
 * <p>This class demonstrates the integration of Apache Jena for RDF processing
 * within a strictly licensed Maven project. It serves as both a functional
 * example and a template for future development.</p>
 *
 * <p>All code is licensed under AGPL-3.0-only. See LICENSES/AGPL-3.0-only.txt
 * for the full license text.</p>
 *
 * @author com.github.metavacua
 * @since 1.0.0
 */
public class App {

    /**
     * The namespace for the subclass application ontology.
     */
    public static final String NS = "https://github.com/metavacacua/subclass#";

    /**
     * Main entry point for the application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(final String[] args) {
        final App app = new App();
        app.run();
    }

    /**
     * Executes the main application logic.
     */
    public void run() {
        System.out.println("Subclass Application Starting...");
        System.out.println("Java Version: " + System.getProperty("java.version"));

        final Model model = createSampleModel();
        System.out.println("RDF Model created with " + model.size() + " statements.");

        System.out.println("Subclass Application Complete.");
    }

    /**
     * Creates a sample RDF model demonstrating Apache Jena usage.
     *
     * @return a Jena Model containing sample data
     */
    public Model createSampleModel() {
        final Model model = ModelFactory.createDefaultModel();

        final Resource appResource = model.createResource(NS + "SubClassApplication")
            .addProperty(RDFS.label, "Subclass Application")
            .addProperty(RDFS.comment, "An Ironclad licensed Maven archetype");

        final Resource licenseResource = model.createResource(NS + "License")
            .addProperty(RDFS.label, "AGPL-3.0-only")
            .addProperty(RDFS.seeAlso, "https://www.gnu.org/licenses/agpl-3.0.txt");

        appResource.addProperty(model.createProperty(NS + "hasLicense"), licenseResource);

        return model;
    }

    /**
     * Returns the application version.
     *
     * @return the version string from package implementation
     */
    public String getVersion() {
        final Package pkg = getClass().getPackage();
        return pkg != null && pkg.getImplementationVersion() != null
            ? pkg.getImplementationVersion()
            : "1.0.0-SNAPSHOT";
    }
}
