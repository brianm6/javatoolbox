package toolbox.plugin.docviewer;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import calpa.html.CalCons;
import calpa.html.CalHTMLPane;
import calpa.html.CalHTMLPreferences;
import calpa.html.DefaultCalHTMLObserver;

import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.FontUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartButton;

/**
 * HTML document viewer that uses the Calpha HTML component.
 */
public class CalphaViewer implements DocumentViewer
{
    /**
     * Base pane that houses the browser and button panel.
     */
    private JPanel viewerPane_;
    
    /**
     * HTML viewer component.
     */
    private CalHTMLPane htmlPane_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a CalphaViewer.
     */
    public CalphaViewer()
    {
    }

    //--------------------------------------------------------------------------
    // DocumentViewer Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#startup(java.util.Map)
     */
    public void startup(Map init) throws DocumentViewerException
    {
        CalHTMLPreferences prefs = new CalHTMLPreferences();
        

        prefs.setFormRenderingStyle(CalCons.USE_LOOK_AND_FEEL);
        prefs.setOptimizeDisplay(CalCons.OPTIMIZE_ALL);

        // NOTE: The html component seems to ignore these settings.

        prefs.setDefaultButtonFont(
                10,
                FontUtil.getPreferredMonoFont());
        
        prefs.setDefaultFont(
                10,
                FontUtil.getPreferredMonoFont());
        
        prefs.setDefaultFormTextFont(
                10,
                FontUtil.getPreferredMonoFont());
        
        htmlPane_ = new CalHTMLPane(
            prefs, new DefaultCalHTMLObserver(), "HTML Viewer");
        
        viewerPane_ = new JPanel(new BorderLayout());
        viewerPane_.add(htmlPane_, BorderLayout.CENTER);

        //create a panel, add buttons, and add a listener to the buttons
        JPanel buttonPane = new JPanel();
        ButtonListener ml = new ButtonListener(htmlPane_);
        String[] s = { "Reload", "Back", "Forward", "Stop" };
        
        for (int i = 0; i < s.length; i++)
        {
            JButton b = new JSmartButton(s[i]);
            b.addActionListener(ml);
            buttonPane.add(b);
        }
        
        viewerPane_.add(buttonPane, BorderLayout.NORTH);
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.File)
     */
    public void view(File file) throws DocumentViewerException
    {
        try
        {
            htmlPane_.showHTMLDocument(file.toURL());
        }
        catch (MalformedURLException e)
        {
            throw new DocumentViewerException(e);
        }        
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.InputStream)
     */
    public void view(InputStream is) throws DocumentViewerException
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#canView(java.io.File)
     */
    public boolean canView(File file)
    {
        return ArrayUtil.contains(
                getViewableFileTypes(), FileUtil.getExtension(file));
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getViewableFileTypes()
     */
    public String[] getViewableFileTypes()
    {
        return new String[] { "html", "htm" };
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getComponent()
     */
    public JComponent getComponent()
    {
        return viewerPane_;
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getName()
     */
    public String getName()
    {
        return "HTML Viewer";
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#shutdown()
     */
    public void shutdown()
    {
    }

    //--------------------------------------------------------------------------
    // SmartHTMLPane
    //--------------------------------------------------------------------------
    
    class SmartHTMLPane extends CalHTMLPane
    {
        //--------------------------------------------------------------------------
        // Overrides JComponent
        //--------------------------------------------------------------------------

        /**
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
//        public void paintComponent(Graphics gc)
//        {
//            SwingUtil.makeAntiAliased(gc, true);
//            super.paintComponent(gc);
//        }
        
        /**
         * @see javax.swing.JLayeredPane#paint(java.awt.Graphics)
         */
        public void paint(Graphics g)
        {
            SwingUtil.makeAntiAliased(g, true);
            super.paint(g);
        }
    }
    
    //--------------------------------------------------------------------------
    // ButtonListener
    //--------------------------------------------------------------------------
    
    /**
     * Listener for the button panel.
     */
    private static class ButtonListener implements ActionListener
    {
        CalHTMLPane pane_;

        public ButtonListener(CalHTMLPane pane)
        {
            pane_ = pane;
        }

        public void actionPerformed(ActionEvent e)
        {
            String s = e.getActionCommand();

            if (("Reload").equals(s))
            {
                pane_.reloadDocument();
            }
            else if (("Back").equals(s))
            {
                pane_.goBack();
            }
            else if (("Forward").equals(s))
            {
                pane_.goForward();
            }
            else if (("Stop").equals(s))
            {
                pane_.stopAll();
            }
        }
    }
}
