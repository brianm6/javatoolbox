package toolbox.plugin.docviewer;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.outerj.pollo.xmleditor.XmlEditor;
import org.outerj.pollo.xmleditor.displayspec.GenericDisplaySpecification;
import org.outerj.pollo.xmleditor.model.XmlModel;
import toolbox.plugin.pdf.DocumentViewer;
import toolbox.plugin.pdf.DocumentViewerException;

/**
 */
public class PolloViewer implements DocumentViewer
{

    /**
     * @see toolbox.plugin.pdf.DocumentViewer#getComponent()
     */
    public JComponent getComponent()
    {
        /**
        * 
        */
        return null;
    }
    
    /**
     * @see toolbox.plugin.pdf.DocumentViewer#getViewableFileTypes()
     */
    public String[] getViewableFileTypes()
    {
        /**
        * 
        */
        return null;
    }
    
    /**
     * @see toolbox.plugin.pdf.DocumentViewer#isViewable(java.lang.String)
     */
    public boolean isViewable(String fileType)
    {
        /**
        * 
        */
        return false;
    }
    
    /**
     * @see toolbox.plugin.pdf.DocumentViewer#shutdown()
     */
    public void shutdown()
    {
        /**
        * 
        */

    }
    
    /**
     * @see toolbox.plugin.pdf.DocumentViewer#startup(java.util.Map)
     */
    public void startup(Map init) throws DocumentViewerException
    {
        /**
        * 
        */

    }
    
    /**
     * @see toolbox.plugin.pdf.DocumentViewer#view(java.io.File)
     */
    public void view(File file) throws DocumentViewerException
    {
        /**
        * 
        */

    }
    
    /**
     * @see toolbox.plugin.pdf.DocumentViewer#view(java.io.InputStream)
     */
    public void view(InputStream is) throws DocumentViewerException
    {
        /**
        * 
        */

    }
    
    
    public void testXmlEditor() throws Exception
    {
        GenericDisplaySpecification displaySpec = 
            new GenericDisplaySpecification();
        
        HashMap init = new HashMap();
        init.put("use-random-colors", "true");
        init.put("fixed-color", "0xffeeff");
        init.put("background-color", null);
        init.put("treetype", "pollo");
        displaySpec.init(init);
        
        XmlEditor editor = new XmlEditor(null, displaySpec, -1);
                    
        XmlModel model = new XmlModel(0);
        model.readFromResource(new File(
            "c:\\Documents and Settings\\Administrator\\.toolbox.xml"));
        
        editor.setXmlModel(model);
                
        //launchInDialog(new JScrollPane(editor), UITestCase.SCREEN_ONE_HALF);
    }
}