package toolbox.plugin.docviewer;

import java.awt.BorderLayout;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

import org.apache.log4j.Logger;

import toolbox.jedit.JEditTextArea;
import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.FontUtil;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.list.JSmartList;
import toolbox.util.ui.list.SmartListCellRenderer;
import toolbox.util.ui.list.SortedListModel;

/**
 * A java source file viewer with list box that contains method names. Uses
 * the QDOX library for parsing java source and extracting line numbers.
 */
public class JavaViewer extends JEditViewer
{
    private static final Logger logger_ = Logger.getLogger(JavaViewer.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Contains the main panel that displays java source code in a text editor.
     */
    private JPanel sourceView_;
    
    /**
     * Flipper that houses a listbox containing method names.
     */
    private JFlipPane sourceFlipper_;
    
    /**
     * Contains a list of methods. Once a method is selected in the list, 
     * its corresponding declaration is selected and made visible in the source
     * code viewer.
     */
    private JSmartList methodList_;
    
    /**
     * Header panel for the method list.
     */
    private JHeaderPanel methodPane_;
    
    /**
     * Sorted model for the list of methods.
     */
    private SortedListModel methodListModel_;
    
    /**
     * Maps a methods signature to the line number in the source code on which
     * it appears.
     */
    private Map lineNumbers_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JavaViewer. Necessary for instantiation via reflection.
     */
    public JavaViewer()
    {
        super("Java Viewer");
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Populates the method listbox.
     */
    protected void populateMethods() 
    {
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSource(new StringReader(getTextArea().getText()));
        JavaClass[] classes = builder.getClasses();
        lineNumbers_ = new HashMap();
        
        for (int i = 0; i < classes.length; i++)
        {       
            JavaClass jc = classes[i];
            JavaMethod[] methods = jc.getMethods();
            logger_.info("Class " + jc.getName());
            
            for (int j = 0; j < methods.length; j++)
            {
                JavaMethod m = methods[j];
                logger_.info("Method " + m.getName() + ":" + m.getLineNumber());
                //logger_.info("Sign   " + m.getCallSignature());
                //logger_.info("decl   " + m.getDeclarationSignature(true));
                
                methodListModel_.add(m.getName());
                lineNumbers_.put(m.getName(), new Integer(m.getLineNumber()));
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // Overrides JEditViewer
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.plugin.docviewer.JEditViewer#createTextArea(
     *      java.lang.String)
     */
    protected void createTextArea(String fileExtension) 
    {
        super.createTextArea("java");
        
        sourceView_ = new JPanel(new BorderLayout());
        sourceFlipper_ = new JFlipPane(JFlipPane.LEFT);
        methodPane_ = new JHeaderPanel("Methods");
        sourceFlipper_.addFlipper("Methods", methodPane_);
        sourceView_.add(BorderLayout.WEST, sourceFlipper_);
        sourceView_.add(BorderLayout.CENTER, getTextArea());
        sourceFlipper_.setExpanded(false);
        methodListModel_ = new SortedListModel();
        methodList_ = new JSmartList(methodListModel_);
        methodList_.setFont(FontUtil.getPreferredMonoFont());
        methodPane_.setContent(new JScrollPane(methodList_));
        methodList_.setCellRenderer(new SmartListCellRenderer());
        methodList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        methodList_.addListSelectionListener(new MethodSelector());
    }
    
    
    /**
     * @see toolbox.plugin.docviewer.JEditViewer#getComponent()
     */
    public JComponent getComponent()
    {
        return sourceView_;
    }
    
    
    /**
     * @see toolbox.plugin.docviewer.JEditViewer#view(java.io.InputStream)
     */
    public void view(InputStream is) throws DocumentViewerException
    {
        super.view(is);
        populateMethods();
    }
    
    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.File)
     */
    public void view(File file) throws DocumentViewerException
    {
        super.view(file);
        populateMethods();
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#canView(java.io.File)
     */
    public boolean canView(File file)
    {
        return (ArrayUtil.contains(
            getViewableFileTypes(), 
            FileUtil.getExtension(file).toLowerCase()));
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getViewableFileTypes()
     */
    public String[] getViewableFileTypes()
    {
        return new String[] {"java"};
    }

    //--------------------------------------------------------------------------
    // MethodSelector
    //--------------------------------------------------------------------------
    
    /**
     * MethodSelector is responsible for making the currently selected method
     * visible (if scrolled off the page) and selected.
     */
    class MethodSelector implements ListSelectionListener 
    {
        /**
         * @see javax.swing.event.ListSelectionListener#valueChanged(
         *      javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e)
        {
            if (e.getValueIsAdjusting())
                return;
            
            JSmartList list = (JSmartList) e.getSource();
            String method = list.getSelectedValue().toString();
            int lineNumber = ((Integer) lineNumbers_.get(method)).intValue();
            
            logger_.debug("line number = " + lineNumber);
            
            JEditTextArea ta = getTextArea();
            ta.scrollTo(lineNumber, 1);
            ta.setSelectionStart(ta.getLineStartOffset(lineNumber));
            ta.setSelectionEnd(ta.getLineEndOffset(lineNumber));
            
            // TODO: QDox doesn't support line numbers for methods yet.
        }
    }
}