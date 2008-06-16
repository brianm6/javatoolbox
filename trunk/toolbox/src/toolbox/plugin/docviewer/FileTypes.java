package toolbox.plugin.docviewer;

/**
 * Filetype to extensions mapping.
 */
public interface FileTypes {

    /**
     * File extensions that are characteristic of XML content.
     */
    String[] XML = new String[] {
        "fo",
        "jelly",
        "jnlp",
        "jsl",
        "svg",
        "xmi",
        "xml",
        "xsd",
        "xsl",
        "xslt"
    };

    /**
     * File extensions that are characteristic of HTML content.
     */
    String[] HTML = new String[] { "html", "htm" };
}
