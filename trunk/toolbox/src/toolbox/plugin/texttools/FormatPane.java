package toolbox.plugin.texttools;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import org.w3c.tidy.Tidy;

import toolbox.util.XMLUtil;
import toolbox.util.io.StringInputStream;
import toolbox.util.io.StringOutputStream;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.workspace.WorkspaceAction;

/**
 * Flipper for formatting various text formats.
 */
public class FormatPane extends JPanel
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Reference to the parent plugin.
     */
    private final TextToolsPlugin plugin_;
    
    /**
     * Flipper for the preferences.
     */
    private JFlipPane flipper_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a FormatPane.
     *
     * @param plugin Parent plugin.
     */
    FormatPane(TextToolsPlugin plugin)
    {
        buildView();
        plugin_ = plugin;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        setLayout(new FlowLayout());
        add(new JSmartButton(new FormatHTMLAction()));
        add(new JSmartButton(new FormatXMLAction()));
    }

    //----------------------------------------------------------------------
    // FormatHTMLAction
    //----------------------------------------------------------------------

    /**
     * Uses JTidy to format the HTML in the input text area.
     */
    class FormatHTMLAction extends AbstractAction
    {
        /**
         * Creates a FormatHTMLAction.
         */
        FormatHTMLAction()
        {
            super("Format HTML");
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            // TODO: Add UI to manipulate the configuration

            Tidy tidy = new Tidy();

            tidy.setIndentContent(true);
            //tidy.setIndentAttributes(true);
            tidy.setWrapAttVals(true);
            tidy.setBreakBeforeBR(true);
            tidy.setWraplen(100);
            //tidy.setSpaces(2);
            //tidy.setTabsize()
            //tidy.setSmartIndent(true);
            tidy.setMakeClean(true);
            tidy.setWrapScriptlets(true);

            InputStream input = new StringInputStream(plugin_.getInputText());
            OutputStream output = new StringOutputStream();
            tidy.parse(input, output);
            plugin_.getOutputArea().setText(output.toString());
        }
    }

    //--------------------------------------------------------------------------
    // FormatXMLAction
    //--------------------------------------------------------------------------
    
    /**
     * Formats the XML with correct indentation and spacing.
     */
    class FormatXMLAction extends WorkspaceAction 
    {
        /**
         * Creates a FormatXMLAction. 
         */
        FormatXMLAction()
        {
            super("Format XML", false, null, null);
        }
    
        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            plugin_.getOutputArea().setText(
                XMLUtil.format(plugin_.getInputText()));
        }
    }
}