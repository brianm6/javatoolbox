package toolbox.plugin.docviewer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import multivalent.Browser;
import multivalent.Document;
import multivalent.INode;
import multivalent.Multivalent;
import multivalent.gui.VScrollbar;
import multivalent.std.ui.ForwardBack;
import multivalent.std.ui.Multipage;

import toolbox.util.FileUtil;
import toolbox.util.collections.CaseInsensetiveSet;
import toolbox.util.ui.JSmartButton;

/**
 * A  Viewer is a wrapper for the multivalent pdf viewer component.
 */
public class MultivalentViewer extends JPanel implements DocumentViewer
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * PDF browser component.
     */
    private Browser browser_;
    
    /**
     * Set of supported file extensions.
     */
    private Set extensions_; 
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a MultivalentViewer.
     */
    public MultivalentViewer()
    {
        //buildView();
    }
    
    //--------------------------------------------------------------------------
    // DocumentViewer Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#startup(java.util.Map)
     */
    public void startup(Map init)
    {
        extensions_ = new CaseInsensetiveSet(new HashSet());
        extensions_.addAll(Arrays.asList(
            new String[] {"pdf", "html", "htm", "dvi", "xml"}));
    }
    
    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getName()
     */
    public String getName()
    {
        return "Multivalent";
    }
    
    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.File)
     */
    public void view(File file)
    {
        lazyLoad();
        browser_.eventq(Document.MSG_OPEN, file.toURI());
    }
    
    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.InputStream)
     */
    public void view(InputStream is)
    {
        throw new RuntimeException("Not supported");
    }
    
    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getViewableFileTypes()
     */
    public String[] getViewableFileTypes()
    {
        return (String[]) extensions_.toArray();
    }    


    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#canView(java.io.File)
     */
    public boolean canView(File file)
    {
        return extensions_.contains(FileUtil.getExtension(file));
    }
    
    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getComponent()
     */
    public JComponent getComponent()
    {
        lazyLoad();
        return this;
    }


    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#shutdown()
     */
    public void shutdown()
    {
        browser_ = null;
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        browser_ = Multivalent.getInstance().getBrowser("name", "Basic", false);

        setLayout(new BorderLayout());
        add(new JScrollPane(browser_), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton button = new JSmartButton("Back");
        buttonPanel.add(button);
        button.addActionListener(
            new SemanticSender(browser_, ForwardBack.MSG_BACKWARD, null));

        button = new JSmartButton("Forward");
        button.addActionListener(
            new SemanticSender(browser_, ForwardBack.MSG_FORWARD, null));
        buttonPanel.add(button);

        button = new JSmartButton("Page Up");
        button.addActionListener(
            new SemanticSender(browser_, Multipage.MSG_PREVPAGE, null));
        buttonPanel.add(button);

        button = new JSmartButton("Page Down");
        button.addActionListener(
            new SemanticSender(browser_, Multipage.MSG_NEXTPAGE, null));
        buttonPanel.add(button);

        add(buttonPanel, BorderLayout.NORTH);

        // Appendix A: handle scrollbars in Swing turn off internal scrollbars
        INode root = browser_.getRoot();
        Document doc = (Document) root.findBFS("content");
        doc.setScrollbarShowPolicy(VScrollbar.SHOW_AS_NEEDED);
        
        // then after loading new document, determine page dimensions from
        // doc.bbox and set Swing scrollbars accordingly
    }

    
    /**
     * Lazily constructs the GUI so that loading the viewer does not induce
     * creation of unnecessary objects in the case that the viewer is not
     * used.
     */
    protected void lazyLoad()
    {
        if (browser_ == null)
            buildView();
    }
    
    //--------------------------------------------------------------------------
    // SemanticSender
    //--------------------------------------------------------------------------

    /**
     * SemanticSender queues up events on the browsers event queue.
     */
    class SemanticSender implements ActionListener
    {
        /**
         * Destination.
         */
        private Browser br_;
        
        /**
         * Command.
         */
        private String cmd_;
        
        /**
         * Argument.
         */
        private Object arg_;

        
        /**
         * Creates a SemanticSender.
         * 
         * @param br Browser.
         * @param cmd Command.
         * @param arg Arguments.
         */
        SemanticSender(Browser br, String cmd, Object arg)
        {
            br_ = br;
            cmd_ = cmd;
            arg_ = arg;
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            br_.eventq(cmd_, arg_);
        }
    }
}