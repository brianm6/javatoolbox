package toolbox.plugin.docviewer;

/**
 * Filetype to extensions mapping.
 */
public interface FileTypes
{
    /**
     * File extensions that are characteristic of XML content.
     */
    String[] XML = new String[] {
        "fo", 
        "jelly",
        "jnlp",
        "svg",
        "xmi",
        "xml", 
        "xsl", 
        "xslt", 
    };
    
    /**
     * File extensions that are characteristic of HTML content.
     */
    String[] HTML = new String[] {
        "html", 
        "htm" 
    };    
}
