package toolbox.plugin.texttools;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import toolbox.util.formatter.Formatter;
import toolbox.util.formatter.HTMLFormatter;
import toolbox.util.formatter.JavaFormatter;
import toolbox.util.formatter.XMLFormatter;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.SmartAction;

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
        
        add(new JSmartButton(
            new FormatAction("Format HTML", new HTMLFormatter())));
        
        add(new JSmartButton(
            new FormatAction("Format XML", new XMLFormatter())));
        
        add(new JSmartButton(
            new FormatAction("Format Java", new JavaFormatter())));
    }

    //----------------------------------------------------------------------
    // FormatHTMLAction
    //----------------------------------------------------------------------

    class FormatAction extends SmartAction
    {
        private Formatter formatter_;
        
        /**
         * Creates a FormatAction.
         */
        FormatAction(String label, Formatter formatter)
        {
            super(label, true, false, null);
            formatter_ = formatter;
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            plugin_.getOutputArea().setText(
                formatter_.format(plugin_.getInputText()));
        }
    }
}