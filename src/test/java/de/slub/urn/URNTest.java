/*
 * Copyright (C) 2018 Saxon State and University Library Dresden (SLUB)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.slub.urn;

import org.junit.Test;

import java.net.URISyntaxException;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public abstract class URNTest {

    abstract URN getSample(String str) throws URNSyntaxException;

    @Test
    final public void Returns_namespace_identifier() throws URNSyntaxException {
        URN    urn = getSample("urn:isbn:irrelevant");
        String nid = urn.getNamespaceIdentifier();
        assertEquals("isbn", nid);
    }

    @Test
    final public void Returns_namespace_specific_string() throws URNSyntaxException {
        URN    urn = getSample("urn:foo:0451450523");
        String nss = urn.getNamespaceSpecificString();
        assertEquals("0451450523", nss);
    }

    @Test
    final public void Generates_valid_URI() throws URISyntaxException, URNSyntaxException {
        URN    urn         = getSample("urn:isbn:0451450523");
        String asciiString = urn.toURI().toASCIIString();
        assertEquals("urn:isbn:0451450523", asciiString);
    }

    @Test
    final public void Equal_URNs_have_the_same_hash_code() throws URNSyntaxException {
        assertEquals("Hashcode of identical URNs should be equal",
                getSample("urn:foo:bar").hashCode(),
                getSample("urn:foo:bar").hashCode());
    }

    @Test
    final public void Lexical_equivalent_URNs_generate_equivalent_representations() throws URNSyntaxException {
        final String message = "Lexical equivalent URNs should be equal";
        final LinkedHashSet<URN> equivalent = new LinkedHashSet<URN>(){{
            add(getSample("urn:example:a123,z456"));
            add(getSample("URN:example:a123,z456"));
            add(getSample("urn:EXAMPLE:a123,z456"));
        }};
        for (URN urn1 : equivalent) {
            for (URN urn2 : equivalent) {
                assertEquals(message, urn1, urn2);
            }
        }
    }

    @Test
    final public void Identical_URNs_generate_equivalent_representations() throws URNSyntaxException {
        final String message = "Identical URNs should be equal";
        final LinkedHashSet<String> equivalent = new LinkedHashSet<String>(){{
            add("urn:foo:bar");
            add("URN:foo:a123,456");
            add("urn:foo:a123,456");
            add("urn:foo:a123%2C456");
        }};
        for (String urn : equivalent) {
            assertEquals(message, getSample(urn), getSample(urn));
        }
    }

    @Test
    final public void URNs_with_percent_encoding_are_not_equal_to_URNs_without_encoding() throws URNSyntaxException {
        final String message = "URNs with percent encoding should not be equivalent to ones without encoding";
        assertNotEquals(message, getSample("urn:example:a123%2Cz456"), getSample("urn:example:a123,z456"));
        assertNotEquals(message, getSample("URN:EXAMPLE:a123%2cz456"), getSample("urn:example:a123,z456"));
    }

    @Test
    final public void URNs_with_percent_encoding_but_different_case_are_equal_to_each_other() throws URNSyntaxException {
        final String message = "URNs with percent encoding should be equivalent each other";
        assertEquals(message, getSample("urn:foo:a123%2C456"), getSample("URN:FOO:a123%2c456"));
        assertEquals(message, getSample("URN:EXAMPLE:a123%2cz456"), getSample("urn:example:a123%2cz456"));
    }

    @Test
    final public void URNs_with_diffenent_NSS_case_are_not_equal_to_each_other() throws URNSyntaxException {
        final String message = "URNs with different case in NSS should not be equal";
        final LinkedHashSet<URN> equivalent = new LinkedHashSet<URN>(){{
            add(getSample("urn:example:a123,z456"));
            add(getSample("URN:example:a123,z456"));
            add(getSample("urn:EXAMPLE:a123,z456"));
        }};
        final LinkedHashSet<URN> notEquivalent = new LinkedHashSet<URN>(){{
            add(getSample("urn:example:A123,z456"));
            add(getSample("urn:example:a123,Z456"));
        }};
        for (URN urn1 : notEquivalent) {
            for (URN urn2 : equivalent) {
                assertNotEquals(message, urn1, urn2);
            }
        }
    }

    @Test
    final public void URN_with_cyrillic_letter_is_not_equal_to_URN_with_latin_letter() throws URNSyntaxException {
        final String message = "URNs with cyrillic letter should not be equal to URN with latin letter";
        final LinkedHashSet<URN> equivalent = new LinkedHashSet<URN>(){{
            add(getSample("urn:example:a123,z456"));
            add(getSample("URN:example:a123,z456"));
            add(getSample("urn:EXAMPLE:a123,z456"));
        }};
        URN cyrillicLetterA = getSample("urn:example:%D0%B0123,z456");
        for (URN urn1 : equivalent) {
            assertNotEquals(message, urn1, cyrillicLetterA);
        }
    }

}