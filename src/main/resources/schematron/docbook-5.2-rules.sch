<?xml version="1.0" encoding="UTF-8"?>
<!--
    DocBook 5.2 Schematron Validation Rules
    Layer 4: Schematron Validation (ISO Standard 19757-3)

    Schematron provides rule-based validation for business rules
    that cannot be expressed in schema languages like RELAX NG or XSD.

    ISO Schematron: https://schematron.com/
    DocBook 5.2: https://docbook.org/specs/docbook-5.2-spec.html
-->
<schema xmlns="http://purl.oclc.org/dsdl/schematron"
         xmlns:db="http://docbook.org/ns/docbook"
         xmlns:xi="http://www.w3.org/2001/XInclude"
         xmlns:xlink="http://www.w3.org/1999/xlink"
         xmlns:html="http://www.w3.org/1999/xhtml"
         queryBinding="xslt2">

    <!-- ==================== TITLE AND METADATA RULES ==================== -->

    <!-- Chapter must have a title -->
    <pattern id="chapter-title-required">
        <title>Chapter must have a title</title>
        <rule context="db:chapter">
            <assert test="db:title" severity="error">
                Chapter must have a title element
            </assert>
        </rule>
    </pattern>

    <!-- Section must have a title -->
    <pattern id="section-title-required">
        <title>Section must have a title</title>
        <rule context="db:section">
            <assert test="db:title" severity="error">
                Section must have a title element
            </assert>
        </rule>
    </pattern>

    <!-- Book must have a booktitle or title -->
    <pattern id="book-title-required">
        <title>Book must have a title</title>
        <rule context="db:book">
            <assert test="db:booktitle or db:title" severity="error">
                Book must have a booktitle or title element
            </assert>
        </rule>
    </pattern>

    <!-- ==================== ID UNIQUE RULES ==================== -->

    <!-- All xml:id attributes must be unique -->
    <pattern id="id-uniqueness">
        <title>ID uniqueness</title>
        <rule context="*[@xml:id]">
            <assert test="count(//*[@xml:id = current()/@xml:id]) = 1"
                    severity="error">
                ID '<value-of select="@xml:id"/>' is duplicated in the document
            </assert>
        </rule>
    </pattern>

    <!-- ==================== XINCLUDE RULES ==================== -->

    <!-- XInclude must have href attribute -->
    <pattern id="xinclude-href-required">
        <title>XInclude href required</title>
        <rule context="xi:include">
            <assert test="@href" severity="error">
                xi:include must have an href attribute
            </assert>
        </rule>
    </pattern>

    <!-- XInclude fallback should be present -->
    <pattern id="xinclude-fallback-recommended">
        <title>XInclude fallback recommended</title>
        <rule context="xi:include">
            <assert test="xi:fallback" severity="warning">
                xi:include should have an xi:fallback element for graceful degradation
            </assert>
        </rule>
    </pattern>

    <!-- XInclude parse attribute must be valid -->
    <pattern id="xinclude-parse-valid">
        <title>XInclude parse attribute valid</title>
        <rule context="xi:include[@parse]">
            <assert test="@parse = 'xml' or @parse = 'text'" severity="error">
                xi:include parse attribute must be 'xml' or 'text'
            </assert>
        </rule>
    </pattern>

    <!-- ==================== LINKING RULES ==================== -->

    <!-- xref must have linkend attribute -->
    <pattern id="xref-linkend-required">
        <title>Cross-reference linkend required</title>
        <rule context="db:xref">
            <assert test="@linkend" severity="error">
                xref must have a linkend attribute
            </assert>
        </rule>
    </pattern>

    <!-- xref linkend must point to existing ID -->
    <pattern id="xref-linkend-target-exists">
        <title>Cross-reference target must exist</title>
        <rule context="db:xref[@linkend]">
            <assert test="//*[@xml:id = current()/@linkend]"
                    severity="error">
                xref linkend '<value-of select="@linkend"/>' points to non-existent ID
            </assert>
        </rule>
    </pattern>

    <!-- link must have href or linkend -->
    <pattern id="link-href-or-linkend">
        <title>Link must have href or linkend</title>
        <rule context="db:link">
            <assert test="@xlink:href or @linkend" severity="error">
                link must have xlink:href or linkend attribute
            </assert>
        </rule>
    </pattern>

    <!-- ==================== CONTENT RULES ==================== -->

    <!-- Para must not be empty -->
    <pattern id="para-not-empty">
        <title>Paragraph must not be empty</title>
        <rule context="db:para">
            <assert test="normalize-space(.) != '' or *"
                    severity="error">
                Paragraph must not be empty
            </assert>
        </rule>
    </pattern>

    <!-- Title must not be empty -->
    <pattern id="title-not-empty">
        <title>Title must not be empty</title>
        <rule context="db:title">
            <assert test="normalize-space(.) != ''"
                    severity="error">
                Title must not be empty
            </assert>
        </rule>
    </pattern>

    <!-- List must have at least one listitem -->
    <pattern id="listitem-required">
        <title>List must have listitem</title>
        <rule context="db:itemizedlist | db:orderedlist">
            <assert test="db:listitem" severity="error">
                List must have at least one listitem
            </assert>
        </rule>
    </pattern>

    <!-- ==================== VERSION RULES ==================== -->

    <!-- DocBook 5.2 version attribute check -->
    <pattern id="docbook-version-check">
        <title>DocBook version 5.2</title>
        <rule context="db:*[@version]">
            <assert test="@version = '5.2'" severity="warning">
                Expected DocBook version 5.2, found '<value-of select="@version"/>'
            </assert>
        </rule>
    </pattern>

    <!-- ==================== ATTRIBUTE VALUE RULES ==================== -->

    <!-- xml:lang should be valid language code -->
    <pattern id="xmllang-valid">
        <title>XML lang attribute format</title>
        <rule context="*[@xml:lang]">
            <assert test="string-length(@xml:lang) = 2 or string-length(@xml:lang) = 5 or string-length(@xml:lang) = 2 + 1 + 2"
                    severity="warning">
                xml:lang should be a valid language code (e.g., 'en', 'en-US')
            </assert>
        </rule>
    </pattern>

    <!-- revisionflag must be valid -->
    <pattern id="revisionflag-valid">
        <title>Revision flag valid</title>
        <rule context="*[@revisionflag]">
            <assert test="@revisionflag = 'changed' or @revisionflag = 'added' or @revisionflag = 'deleted' or @revisionflag = 'none'"
                    severity="error">
                revisionflag must be 'changed', 'added', 'deleted', or 'none'
            </assert>
        </rule>
    </pattern>

    <!-- ==================== LIST ATTRIBUTE RULES ==================== -->

    <!-- itemizedlist mark attribute must be valid -->
    <pattern id="itemizedlist-mark-valid">
        <title>Itemized list mark attribute valid</title>
        <rule context="db:itemizedlist[@mark]">
            <assert test="@mark = 'bullet' or @mark = 'circle' or @mark = 'disc' or @mark = 'square' or @mark = 'none'"
                    severity="error">
                itemizedlist mark must be 'bullet', 'circle', 'disc', 'square', or 'none'
            </assert>
        </rule>
    </pattern>

    <!-- orderedlist numeration must be valid -->
    <pattern id="orderedlist-numeration-valid">
        <title>Ordered list numeration valid</title>
        <rule context="db:orderedlist[@numeration]">
            <assert test="@numeration = 'arabic' or @numeration = 'upperalpha' or @numeration = 'loweralpha' or @numeration = 'upperroman' or @numeration = 'lowerroman'"
                    severity="error">
                orderedlist numeration must be 'arabic', 'upperalpha', 'loweralpha', 'upperroman', or 'lowerroman'
            </assert>
        </rule>
    </pattern>

    <!-- ==================== CODE ELEMENT RULES ==================== -->

    <!-- programlisting should have language attribute -->
    <pattern id="programlisting-language-recommended">
        <title>Program listing language recommended</title>
        <rule context="db:programlisting">
            <assert test="@language" severity="info">
                programlisting should have a language attribute for syntax highlighting
            </assert>
        </rule>
    </pattern>

    <!-- ==================== METADATA RULES ==================== -->

    <!-- Book should have bookmeta -->
    <pattern id="bookmeta-recommended">
        <title>Book metadata recommended</title>
        <rule context="db:book">
            <assert test="db:bookmeta" severity="info">
                Book should have bookmeta for bibliographic information
            </assert>
        </rule>
    </pattern>

    <!-- Copyright should have year -->
    <pattern id="copyright-year-required">
        <title>Copyright must have year</title>
        <rule context="db:copyright">
            <assert test="db:year" severity="error">
                Copyright element must have at least one year element
            </assert>
        </rule>
    </pattern>

    <!-- ==================== STRUCTURAL RULES ==================== -->

    <!-- Chapter should have content -->
    <pattern id="chapter-content-required">
        <title>Chapter must have content</title>
        <rule context="db:chapter">
            <assert test="count(*) > 1 or (db:section or db:simplesect or db:para)"
                    severity="warning">
                Chapter should have content beyond the title
            </assert>
        </rule>
    </pattern>

    <!-- ==================== LINK TARGET RULES ==================== -->

    <!-- All linkend targets should exist -->
    <pattern id="linkend-target-exists">
        <title>Link target must exist</title>
        <rule context="*[@linkend]">
            <assert test="//@xml:id[. = current()/@linkend]"
                    severity="error">
                linkend '<value-of select="@linkend"/>' references non-existent ID
            </assert>
        </rule>
    </pattern>

    <!-- ==================== XPOINTER RULES ==================== -->

    <!-- xi:xpointer should be present with xi:include -->
    <pattern id="xpointer-recommended">
        <title>XPointer recommended for XInclude</title>
        <rule context="xi:include">
            <assert test="@xpointer or @href[contains(., '#')]"
                    severity="info">
                xi:include with partial href should have xi:xpointer
            </assert>
        </rule>
    </pattern>

    <!-- ==================== FIRSTTERM RULES ==================== -->

    <!-- firstterm with glosssee should have matching glossterm -->
    <pattern id="firstterm-glosssee-valid">
        <title>Firstterm glosssee must match glossterm</title>
        <rule context="db:firstterm[@glosssee]">
            <assert test="//db:glossterm[@xml:id = current()/@glosssee]"
                    severity="error">
                firstterm glosssee '<value-of select="@glosssee"/>' does not match any glossterm ID
            </assert>
        </rule>
    </pattern>

    <!-- ==================== COMPLEX CONTENT RULES ==================== -->

    <!-- Nested itemizedlist should have different marks -->
    <pattern id="nested-list-mark-different">
        <title>Nested lists should have different marks</title>
        <rule context="db:itemizedlist[parent::db:listitem]">
            <let name="parent-mark" value="parent::db:listitem/parent::db:itemizedlist/@mark"/>
            <assert test="not(@mark = $parent-mark) or not(@mark)"
                    severity="info">
                Nested itemizedlist should have a different mark than its parent
            </assert>
        </rule>
    </pattern>

    <!-- ==================== EXAMPLE RULES ==================== -->

    <!-- example should have title -->
    <pattern id="example-title-recommended">
        <title>Example title recommended</title>
        <rule context="db:example">
            <assert test="db:title" severity="info">
                Example should have a title for accessibility
            </assert>
        </rule>
    </pattern>

    <!-- figure should have title -->
    <pattern id="figure-title-recommended">
        <title>Figure title recommended</title>
        <rule context="db:figure">
            <assert test="db:title" severity="info">
                Figure should have a title for accessibility
            </assert>
        </rule>
    </pattern>

    <!-- ==================== ACCESSIBILITY RULES ==================== -->

    <!-- Links should have text content -->
    <pattern id="link-text-required">
        <title>Link should have text content</title>
        <rule context="db:link[not(@xlink:href)] | db:link[@linkend]">
            <assert test="normalize-space(.) != ''" severity="warning">
                Link should have descriptive text content
            </assert>
        </rule>
    </pattern>

    <!-- ==================== NAMESPACE RULES ==================== -->

    <!-- Elements should be in DocBook namespace -->
    <pattern id="docbook-namespace">
        <title>DocBook namespace</title>
        <rule context="db:*">
            <assert test="namespace-uri(.) = 'http://docbook.org/ns/docbook' or not(parent::*)"
                    severity="error">
                DocBook elements should be in the DocBook namespace
            </assert>
        </rule>
    </pattern>

</schema>
