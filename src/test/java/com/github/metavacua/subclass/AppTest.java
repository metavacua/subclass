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
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link App} class.
 *
 * <p>These tests verify the basic functionality of the App class,
 * including RDF model creation and application metadata.</p>
 *
 * @author com.github.metavacua
 * @since 1.0.0
 */
class AppTest {

    @Test
    void testAppInstantiation() {
        final App app = new App();
        assertNotNull(app, "App instance should not be null");
    }

    @Test
    void testCreateSampleModel() {
        final App app = new App();
        final Model model = app.createSampleModel();

        assertNotNull(model, "Model should not be null");
        assertTrue(model.size() > 0, "Model should contain statements");
    }

    @Test
    void testGetVersion() {
        final App app = new App();
        final String version = app.getVersion();

        assertNotNull(version, "Version should not be null");
        assertFalse(version.isEmpty(), "Version should not be empty");
    }

    @Test
    void testNamespaceConstant() {
        assertEquals("https://github.com/metavacacua/subclass#", App.NS,
            "Namespace should match expected value");
    }
}
