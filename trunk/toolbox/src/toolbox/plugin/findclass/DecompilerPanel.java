package toolbox.plugin.findclass;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.TableModel;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;

import org.jedit.syntax.JavaTokenMarker;
import org.jedit.syntax.TextAreaDefaults;

import toolbox.jedit.JEditPopupMenu;
import toolbox.jedit.JEditTextArea;
import toolbox.jedit.JavaDefaults;
import toolbox.util.ClassUtil;
import toolbox.util.FileUtil;
import toolbox.util.FontUtil;
import toolbox.util.decompiler.Decompiler;
import toolbox.util.decompiler.DecompilerFactory;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartComboBox;
import toolbox.util.ui.tabbedpane.JSmartTabbedPane;
import toolbox.workspace.WorkspaceAction;

/**
 * Decompiler Panel facilitates decompiling of class files that are 
 * identified by the JFindClass plugin.
 * <p>
 * Supports:
 * <ul>
 *   <li>User selectable decompiler.
 *   <li>Decompiled files are stacked on a tabbed pane.
 * </ul>
 * 
 * @see toolbox.plugin.findclass.FindClassPlugin
 */
public class DecompilerPanel extends JHeaderPanel
{
    private static final Logger logger_ = 
        Logger.getLogger(DecompilerPanel.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Allows user to choose which decompiler to use for decompiling.
     */
    private JSmartComboBox decompilerCombo_;
    
    /**
     * Tab panel for decompiled source. One tab per class. 
     */
    private JSmartTabbedPane tabbedPane_;
    
    /**
     * Reference to the enclosing plugin's results table that is passed in at
     * time of construction. The table is the source for the location and FQCN
     * for the class to decompile.
     */
    private JTable resultTable_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DecompilerPanel.
     * 
     * @param resultTable Table containing list of class locations and names.
     */
    public DecompilerPanel(JTable resultTable)
    {
        super("Decompiler");
        resultTable_ = resultTable;
        buildView();
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        Decompiler[] decompilers = DecompilerFactory.createAll(); 
        JPanel content = new JPanel(new BorderLayout());
        decompilerCombo_ = new JSmartComboBox(decompilers);
        
        JButton decompile = createButton(
            ImageCache.getIcon(ImageCache.IMAGE_SPANNER),
            "Decompile",
            new DecompileAction());
        
        JToolBar tb = createToolBar();
        tb.add(decompilerCombo_);
        tb.add(decompile);
        setToolBar(tb);
        tabbedPane_ = new JSmartTabbedPane(true);
        content.add(tabbedPane_, BorderLayout.CENTER);
        setContent(content);
    }
    
    
    /**
     * Adds a tab to the tabbed pane. The class name is used as the tab title
     * and the source is embedded in a text area for viewing.
     * 
     * @param clazz Class associated with the tab.
     * @param source Source code.
     */
    protected void addTab(String clazz, String source)
    {
        TextAreaDefaults defaults = new JavaDefaults();
        
        JEditTextArea sourceArea = new JEditTextArea(
            new JavaTokenMarker(), defaults);
        
        sourceArea.getPainter().setFont(FontUtil.getPreferredMonoFont());

        // Hack for circular reference in popup menu            
        ((JEditPopupMenu) defaults.popup).setTextArea(sourceArea);
        ((JEditPopupMenu) defaults.popup).buildView();
        
        sourceArea.setText(source);
        sourceArea.setCaretPosition(0);
        tabbedPane_.addTab(clazz, sourceArea);
        tabbedPane_.setSelectedComponent(sourceArea);
    }
    
    //--------------------------------------------------------------------------
    // DecompileAction
    //--------------------------------------------------------------------------
        
    /**
     * Action to decompile the currently selected class file.
     */
    class DecompileAction extends WorkspaceAction
    {
        /**
         * Creates a DecompileAction.
         */
        DecompileAction()
        {
            // TODO: Re-integrate the status bar
            super("Decompile", true, null, null /*statusBar_*/);
            putValue(MNEMONIC_KEY, new Integer('D'));    
            putValue(SHORT_DESCRIPTION, "Decompiles the selected class");
        }
        
        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            int idx = resultTable_.getSelectedRow();
            
            if (idx >= 0)
            {
                // Jar or directory path
                String location = (String) 
                    resultTable_.getModel().getValueAt(
                        idx, ResultsTableModel.COL_SOURCE); 
                    
                // FQN of class
                String clazz  = (String) 
                    resultTable_.getModel().getValueAt(
                        idx, ResultsTableModel.COL_CLASS);
            
                location = location.trim();
                clazz  = clazz.trim();
                
                Decompiler d = (Decompiler) decompilerCombo_.getSelectedItem();

                String source = null;
                
                try
                {
                    source = d.decompile(clazz, location);
                }
                catch (UnsupportedOperationException iae)
                {
                    if (ClassUtil.isArchive(location))
                    {   
                        // Collect all inner and anonymous innerclasses since 
                        // they're requires as Files to by the decompiler.
                        
                        TableModel m = resultTable_.getModel();
                        List innerClasses = new ArrayList();
                        
                        for (int i = 0; i < m.getRowCount(); i++)
                        {
                            String innerClass = (String) 
                                resultTable_.getModel().getValueAt(i, 
                                    ResultsTableModel.COL_CLASS);
                            
                            if (innerClass.startsWith(clazz + "$")) 
                            {
                                // Make sure the inner class comes from the same 
                                // source as the parent class
                                
                                String innerClassLoc = (String) 
                                    resultTable_.getModel().getValueAt(
                                        i, ResultsTableModel.COL_SOURCE); 
    
                                if (location.equals(innerClassLoc))
                                {    
                                    logger_.debug(
                                        "Found inner class: " + innerClass);
                                    
                                    innerClasses.add(
                                        extractClassFile(location, innerClass));
                                }
                            }
                        }
                                        
                        File classFile = extractClassFile(location, clazz);
                        
                        // Do some decompiling...
                        source = d.decompile(classFile);
                        
                        // Cleanup
                        FileUtil.deleteQuietly(classFile);
                        
                        for (Iterator iter = innerClasses.iterator(); 
                             iter.hasNext();)
                        {
                            FileUtil.deleteQuietly((File) iter.next());
                        }
                    }
                    else
                    {                   
                        source = "Not supported";
                    }
                }

                addTab(clazz, source);
            }
        }
        
        
        /**
         * Extracts a class file from a jar and writes it to the temp directory
         * also returning the handle to the file.
         * 
         * @param location Absolute path of the jar file.
         * @param clazz FQCN name of the class file to extract.
         * @return File
         * @throws IOException on I/O error.
         */
        protected File extractClassFile(String location, String clazz)
            throws IOException
        {
            // Build up a jar url pointing to the class file inside the jar
            
            StringBuffer sb = new StringBuffer();
            sb.append("jar:file:");
            sb.append(new File(location).getCanonicalPath());
            sb.append("!/");
            sb.append(clazz.replace('.', '/'));
            sb.append(".class");
        
            // jar:file:/c:/crap/my.jar!/com/company/MyClass.class"
            //logger_.debug("JAR URL=" + sb);
        
            URL url = new URL(sb.toString());
            
            //logger_.debug("URL as file=" + url.getFile());
            //logger_.debug("Class file len=" + 
            //    url.openConnection().getContentLength());
        
            // Extract the class file as an array of bytes
            byte[] bytecode = IOUtils.toByteArray(
                url.openConnection().getInputStream());
        
            // Write out class file to the temp dir on disk 
            // (least common denominator so that all decompilers 
            // can get to it.
            
            String shortName = null;
 
            // Innerclass is a little tricky...
            if (ClassUtil.isInnerClass(clazz))
            {
                int innerClassSep = clazz.lastIndexOf("$");
                String innerClass = clazz.substring(innerClassSep);
                String parentClass = clazz.substring(0, innerClassSep);
                shortName = ClassUtils.getShortClassName(parentClass);
                shortName = shortName + innerClass;
            }
            else
            {
                shortName = ClassUtils.getShortClassName(clazz);
            }
           
            File tempClassFile = 
                new File(FileUtil.getTempDir(), shortName + ".class");
            
            FileUtil.setFileContents(
                tempClassFile.getCanonicalPath(), bytecode, false);
            
            logger_.debug("Wrote " + tempClassFile.getAbsolutePath());
            
            return tempClassFile;
        }
    }
}