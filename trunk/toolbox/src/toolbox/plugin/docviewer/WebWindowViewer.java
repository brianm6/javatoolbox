package toolbox.plugin.docviewer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.javio.webwindow.HTMLPane;
import com.javio.webwindow.WebWindow;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.FontUtil;

/**
 * HTML document viewer that uses the Calpha HTML component.
 */
public class WebWindowViewer implements DocumentViewer
{
    private static final Logger logger_ = 
        Logger.getLogger(WebWindowViewer.class);
    
    /**
     * Base pane that houses the browser and button panel.
     */
    private JPanel viewerPane_;
    
    /**
     * HTML viewer component.
     */
    private WebWindow webWindow_;
    
    /**
     * HTML component.
     */
    private HTMLPane webPane_;
    
    /**
     * Navigation pane.
     */
    private JPanel locationPanel_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a WebWindowViewer.
     */
    public WebWindowViewer()
    {
    }

    //--------------------------------------------------------------------------
    // Protected 
    //--------------------------------------------------------------------------
    
//    /**
//     * Creates the toolbar.
//     */
//    protected JToolBar createToolBar()
//    {
//        JToolBar tb = new JToolBar();
//        tb.setOpaque(false);
//        //tb.add(getAction(OPEN_ACTION));
//        //tb.addSeparator();
//        
//        tb.add(new AbstractAction("Back") 
//        {
//            public void actionPerformed(ActionEvent e)
//            {
//                webPane_.back();
//            }
//        });
//        
//        tb.add(new AbstractAction("Forward") 
//        {
//            public void actionPerformed(ActionEvent e)
//            {
//                webPane_.forward();
//            }
//        });
//        
//        return tb;
//    }
    
    //--------------------------------------------------------------------------
    // DocumentViewer Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#startup(java.util.Map)
     */
    public void startup(Map init) throws DocumentViewerException
    {
        webWindow_ = new WebWindow();
        webPane_ = webWindow_.getHTMLPane();
        webPane_.setDefaultFont(FontUtil.getPreferredSerifFont());
        
        viewerPane_ = new JPanel(new BorderLayout());
        viewerPane_.add(webWindow_, BorderLayout.CENTER);
        
        locationPanel_ = new LocationPanel(webPane_);
        viewerPane_.add(locationPanel_, BorderLayout.NORTH);
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.File)
     */
    public void view(File file) throws DocumentViewerException
    {
        try
        {
            webPane_.loadPage(file.toURL());
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
        webPane_.loadPage(new InputStreamReader(is), null);        
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
        return "Web Window";
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#shutdown()
     */
    public void shutdown()
    {
    }
    
    //--------------------------------------------------------------------------
    // LocationPanel 
    //--------------------------------------------------------------------------
    
    /**
     * URL location panel for the browser.
     *
     * @author Javio
     */
    class LocationPanel extends JPanel implements KeyListener, ActionListener
    {
//        static
//        {
//            System.out.println(StringUtil.addBars("Loaded Locatino pantelll"));
//        }
        
        //private static final Logger logger_ = Logger.getLogger(LocationPanel.class);
        
        protected JTextField textField_;
        protected JButton goButton_;
        protected HTMLPane htmlPane_;

        LocationPanel(HTMLPane pane)
        {
            htmlPane_ = pane;
            setLayout(new FlowLayout());
            goButton_ = new JButton("Go");
            goButton_.addActionListener(this);
            textField_ = new JTextField("http://www.yahoo.com");
            textField_.setEditable(true);
            textField_.addKeyListener(this);
            
            JLabel l = new JLabel("Location:",
                    //new ImageIcon(Helper.getImage("location24.png")), 
                    JLabel.RIGHT);
            
            add(l);
            
//        Helper.addComponent(this, l,
//                            GridBagConstraints.WEST, GridBagConstraints.NONE,
//                            0, 0,
//                            1, 1,
//                            new Insets(5,5,5,5),
//                            0, 0,
//                            0.0, 0.0);

            add(textField_);
            
//        Helper.addComponent(this, textField_,
//                            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
//                            1, 0,
//                            1, 1,
//                            new Insets(5,0,5,5),
//                            0, 0,
//                            1.0, 0.0);
            
            add(goButton_);
            
//        Helper.addComponent(this, goButton_,
//                            GridBagConstraints.WEST, GridBagConstraints.NONE,
//                            2, 0,
//                            1, 1,
//                            new Insets(5,0,5,5),
//                            0, 0,
//                            0.0, 0.0);
            
            JToolBar tb = new JToolBar();
            
            tb.add(new AbstractAction("Back") 
                    {
                public void actionPerformed(ActionEvent e)
                {
                    htmlPane_.back();
                }
            });
            
            tb.add(new AbstractAction("Forward") 
                    {
                public void actionPerformed(ActionEvent e)
                {
                    htmlPane_.forward();
                }
            });
            
            add(tb);
            
        }

        void loadPage()
        {
            String urlStr = textField_.getText();
            
            //URL u = Helper.getURL(urlStr);
            
            URL u = null;
            
            try
            {
                u = new URL(urlStr);
            }
            catch (MalformedURLException mue)
            {
                ExceptionUtil.handleUI(mue, logger_);
            }
            
            
            if (u != null)
                htmlPane_.loadPage(u);
        }

        //--------------------------------------------------------------------------
        // ActionListener Interface 
        //--------------------------------------------------------------------------
        
        public void actionPerformed(ActionEvent e)
        {
            loadPage();
        }

        //--------------------------------------------------------------------------
        // KeyListener Interface 
        //--------------------------------------------------------------------------
        
        public void keyPressed(KeyEvent e)
        {
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
               {
                loadPage();
            }
        }
        
        public void keyReleased(KeyEvent e){}
        
        public void keyTyped(KeyEvent e){}
    }
}