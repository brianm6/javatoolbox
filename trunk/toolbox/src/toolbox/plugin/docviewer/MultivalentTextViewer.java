package toolbox.plugin.docviewer;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tool.ExtractText;

import toolbox.util.FileUtil;
import toolbox.util.collections.CaseInsensetiveSet;

/**
 * A viewer that operates on all Multivalent supported formats (PDF, HTML, DVI)
 * and extracts only the plain text for display.
 */
public class MultivalentTextViewer extends TextViewer
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Set of supported file extensions.
     */
    private Set extensions_; 
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a MultivalentTextViewer.
     */
    public MultivalentTextViewer()
    {
    }

    //--------------------------------------------------------------------------
    // Overrides TextViewer
    //--------------------------------------------------------------------------
    
    /**
     * Builds the list of viewable file types.
     * 
     * @see toolbox.plugin.docviewer.TextViewer#startup(java.util.Map)
     */
    public void startup(Map init) throws DocumentViewerException
    {
        super.startup(init);
        
        extensions_ = new CaseInsensetiveSet(new HashSet());
        extensions_.addAll(Arrays.asList(
            new String[] {"pdf", "html", "htm", "dvi", "xml"}));
    }
    
    
    /**
     * @see toolbox.plugin.docviewer.TextViewer#canView(java.io.File)
     */
    public boolean canView(File file)
    {
        return extensions_.contains(FileUtil.getExtension(file));
    }
    
    
    /**
     * @see toolbox.plugin.docviewer.TextViewer#getName()
     */
    public String getName()
    {
        return "Multivalent Text Viewer";
    }

    
    /**
     * Converts the passed in PDF, HTML, DVI to plain text and displays it in
     * the textarea.
     * 
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.File)
     */
    public void view(File file) throws DocumentViewerException
    {
        try
        {
            ExtractText extract = new ExtractText();
            String text = extract.extract(file.toURI(), null);;
            getTextArea().setText(text);
            getTextArea().setCaretPosition(0);
        }
        catch (Exception e)
        {
            throw new DocumentViewerException(e);
        }
    }
}
