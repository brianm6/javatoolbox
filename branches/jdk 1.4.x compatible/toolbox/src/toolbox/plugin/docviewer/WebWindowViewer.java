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
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;

/**
 * HTML document viewer that uses the WebWindow java component.
 */
public class WebWindowViewer extends AbstractViewer {

    private static final Logger logger_ = 
        Logger.getLogger(WebWindowViewer.class);

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Creates a WebWindowViewer.
     */
    public WebWindowViewer() {
        super("Web Window");
    }

    // -------------------------------------------------------------------------
    // Protected
    // -------------------------------------------------------------------------

    /**
     * Lazily loads the WebWindow component until a document is ready to
     * actually be viewed.
     */
    protected void lazyLoad() {
        if (viewerPane_ == null) {
            webWindow_ = new WebWindow();
            webPane_ = webWindow_.getHTMLPane();
            webPane_.setDefaultFont(FontUtil.getPreferredSerifFont());

            viewerPane_ = new JPanel(new BorderLayout());
            viewerPane_.add(webWindow_, BorderLayout.CENTER);

            locationPanel_ = new LocationView();
            viewerPane_.add(locationPanel_, BorderLayout.NORTH);
        }
    }

    // -------------------------------------------------------------------------
    // Initializable Interface
    // -------------------------------------------------------------------------

    /*
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map init) {
        // Delegated to lazyLoad()
    }

    // -------------------------------------------------------------------------
    // DocumentViewer Interface
    // -------------------------------------------------------------------------

    /*
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.File)
     */
    public void view(File file) throws DocumentViewerException {
        lazyLoad();

        try {
            webPane_.loadPage(file.toURL());
        }
        catch (MalformedURLException e) {
            throw new DocumentViewerException(e);
        }
    }


    /*
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.InputStream)
     */
    public void view(InputStream is) throws DocumentViewerException {
        lazyLoad();
        webPane_.loadPage(new InputStreamReader(is), null);
    }


    /*
     * @see toolbox.plugin.docviewer.DocumentViewer#canView(java.io.File)
     */
    public boolean canView(File file) {
        return ArrayUtil.contains(getViewableFileTypes(), FileUtil
            .getExtension(file).toLowerCase());
    }


    /*
     * @see toolbox.plugin.docviewer.DocumentViewer#getViewableFileTypes()
     */
    public String[] getViewableFileTypes() {
        return FileTypes.HTML;
    }


    /*
     * @see toolbox.plugin.docviewer.DocumentViewer#getComponent()
     */
    public JComponent getComponent() {
        lazyLoad();
        return viewerPane_;
    }

    // -------------------------------------------------------------------------
    // Destroyable Interface
    // -------------------------------------------------------------------------

    /*
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy() {
        locationPanel_ = null;
        webPane_ = null;
        webWindow_ = null;
        viewerPane_ = null;
    }

    // -------------------------------------------------------------------------
    // LocationView
    // -------------------------------------------------------------------------

    /**
     * Allows entry of a URL and a button to load the URL.
     */
    class LocationView extends JPanel implements KeyListener, ActionListener {

        // ---------------------------------------------------------------------
        // Fields
        // ---------------------------------------------------------------------

        /**
         * URL text field.
         */
        private JTextField urlField_;

        /**
         * When pressed, loads the web page in the URL text field.
         */
        private JButton goButton_;

        // ---------------------------------------------------------------------
        // Constructors
        // ---------------------------------------------------------------------

        /**
         * Creates a LocationView.
         */
        LocationView() {
            setLayout(new FlowLayout());
            goButton_ = new JSmartButton("Go");
            goButton_.addActionListener(this);
            urlField_ = new JSmartTextField("http://www.yahoo.com", 35);
            urlField_.setEditable(true);
            urlField_.addKeyListener(this);

            JLabel l = new JSmartLabel("Location",
            // new ImageIcon(Helper.getImage("location24.png")),
                JLabel.RIGHT);

            add(l);

            // Helper.addComponent(this, l,
            // GridBagConstraints.WEST, GridBagConstraints.NONE,
            // 0, 0,
            // 1, 1,
            // new Insets(5,5,5,5),
            // 0, 0,
            // 0.0, 0.0);

            add(urlField_);

            // Helper.addComponent(this, textField_,
            // GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            // 1, 0,
            // 1, 1,
            // new Insets(5,0,5,5),
            // 0, 0,
            // 1.0, 0.0);

            add(goButton_);

            // Helper.addComponent(this, goButton_,
            // GridBagConstraints.WEST, GridBagConstraints.NONE,
            // 2, 0,
            // 1, 1,
            // new Insets(5,0,5,5),
            // 0, 0,
            // 0.0, 0.0);

            JToolBar tb = new JToolBar();

            tb.add(new JSmartButton(new AbstractAction("Back") {

                public void actionPerformed(ActionEvent e) {
                    webPane_.back();
                }
            }));

            tb.add(new JSmartButton(new AbstractAction("Forward") {

                public void actionPerformed(ActionEvent e) {
                    webPane_.forward();
                }
            }));

            add(tb);

        }

        // ----------------------------------------------------------------------
        // Package
        // ----------------------------------------------------------------------

        /**
         * Loads the page.
         */
        void loadPage() {
            String urlStr = urlField_.getText();

            // URL u = Helper.getURL(urlStr);

            URL u = null;

            try {
                u = new URL(urlStr);
            }
            catch (MalformedURLException mue) {
                ExceptionUtil.handleUI(mue, logger_);
            }


            if (u != null)
                webPane_.loadPage(u);
        }

        // ----------------------------------------------------------------------
        // ActionListener Interface
        // ----------------------------------------------------------------------

        /*
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            loadPage();
        }

        // ----------------------------------------------------------------------
        // KeyListener Interface
        // ----------------------------------------------------------------------

        /*
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                loadPage();
            }
        }


        /*
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
         */
        public void keyReleased(KeyEvent e) {
            ; // No-op
        }


        /*
         * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
         */
        public void keyTyped(KeyEvent e) {
            ; // No-op
        }
    }
}