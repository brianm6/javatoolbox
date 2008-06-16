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
import toolbox.util.service.ServiceException;
import toolbox.util.ui.JSmartButton;

/**
 * HTML document viewer that uses the Calpha HTML component.
 */
public class CalphaViewer extends AbstractViewer {

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Base pane that houses the browser and button panel.
     */
    private JPanel viewerPane_;

    /**
     * HTML viewer component.
     */
    private CalHTMLPane htmlPane_;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    public CalphaViewer() {
        super("HTML Viewer");
    }

    // --------------------------------------------------------------------------
    // Initializable Interface
    // --------------------------------------------------------------------------

    public void initialize(Map init) throws ServiceException {
        CalHTMLPreferences prefs = new CalHTMLPreferences();
        prefs.setFormRenderingStyle(CalCons.USE_LOOK_AND_FEEL);
        prefs.setOptimizeDisplay(CalCons.OPTIMIZE_ALL);

        // NOTE: The html component seems to ignore these settings.
        prefs.setDefaultButtonFont(10, FontUtil.getPreferredMonoFont());
        prefs.setDefaultFont(10, FontUtil.getPreferredMonoFont());
        prefs.setDefaultFormTextFont(10, FontUtil.getPreferredMonoFont());

        htmlPane_ = new CalHTMLPane(
            prefs, new DefaultCalHTMLObserver(), "HTML Viewer");

        viewerPane_ = new JPanel(new BorderLayout());
        viewerPane_.add(htmlPane_, BorderLayout.CENTER);

        // create a panel, add buttons, and add a listener to the buttons
        JPanel buttonPane = new JPanel();
        ButtonListener ml = new ButtonListener(htmlPane_);
        String[] s = { "Reload", "Back", "Forward", "Stop" };

        for (int i = 0; i < s.length; i++) {
            JButton b = new JSmartButton(s[i]);
            b.addActionListener(ml);
            buttonPane.add(b);
        }

        viewerPane_.add(buttonPane, BorderLayout.NORTH);
    }

    // --------------------------------------------------------------------------
    // DocumentViewer Interface
    // --------------------------------------------------------------------------

    public void view(File file) throws DocumentViewerException {
        try {
            htmlPane_.showHTMLDocument(file.toURI().toURL());
        }
        catch (MalformedURLException e) {
            throw new DocumentViewerException(e);
        }
    }


    public void view(InputStream is) throws DocumentViewerException {
        throw new UnsupportedOperationException("Not implemented");
    }


    public boolean canView(File file) {
        return ArrayUtil.contains(getViewableFileTypes(), FileUtil
            .getExtension(file).toLowerCase());
    }


    public String[] getViewableFileTypes() {
        return FileTypes.HTML;
    }


    public JComponent getComponent() {
        return viewerPane_;
    }

    // --------------------------------------------------------------------------
    // Destroyable Interface
    // --------------------------------------------------------------------------

    public void destroy() throws ServiceException {
        ; // no-op
    }

    // --------------------------------------------------------------------------
    // SmartHTMLPane
    // --------------------------------------------------------------------------

    class SmartHTMLPane extends CalHTMLPane {

        // ----------------------------------------------------------------------
        // Overrides JComponent
        // ----------------------------------------------------------------------

        public void paint(Graphics g) {
            SwingUtil.makeAntiAliased(g, true);
            super.paint(g);
        }
    }

    // --------------------------------------------------------------------------
    // ButtonListener
    // --------------------------------------------------------------------------

    private static class ButtonListener implements ActionListener {

        private CalHTMLPane pane_;

        public ButtonListener(CalHTMLPane pane) {
            pane_ = pane;
        }

        public void actionPerformed(ActionEvent e) {
            String s = e.getActionCommand();

            if (("Reload").equals(s)) {
                pane_.reloadDocument();
            }
            else if (("Back").equals(s)) {
                pane_.goBack();
            }
            else if (("Forward").equals(s)) {
                pane_.goForward();
            }
            else if (("Stop").equals(s)) {
                pane_.stopAll();
            }
        }
    }
}