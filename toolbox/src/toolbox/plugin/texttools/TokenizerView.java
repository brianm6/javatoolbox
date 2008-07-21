package toolbox.plugin.texttools;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JTextField;

import toolbox.util.StringUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;

/**
 * Flipper that allows the user to tokenize strings by providing the token
 * delimiter. Multiline strings can also be merged into one line.
 */
public class TokenizerView extends JPanel
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Reference to parent plugin.
     */
    private final TextToolsPlugin plugin_;

    /**
     * Token delimiter field.
     */
    private JTextField delimiterField_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    TokenizerView(TextToolsPlugin plugin)
    {
        buildView();
        plugin_ = plugin;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    protected void buildView()
    {
        setLayout(new FlowLayout());

        add(new JSmartLabel("Token Delimiter"));
        add(delimiterField_ = new JSmartTextField(20));
        add(new JSmartButton(new TokenizeAction()));
        add(new JSmartButton(new SingleLineAction()));
    }

    //--------------------------------------------------------------------------
    // TokenizeAction
    //--------------------------------------------------------------------------

    /**
     * Tokenizes the string in the input text area with the entered
     * delimiter and dumps the result to the output text area.
     */
    class TokenizeAction extends AbstractAction
    {
        TokenizeAction()
        {
            super("Tokenize");
        }


        public void actionPerformed(ActionEvent e)
        {
            StringTokenizer st =
                new StringTokenizer(
                    plugin_.getInputText(),
                    delimiterField_.getText());

            while (st.hasMoreElements())
                plugin_.getOutputArea().append(st.nextToken() + StringUtil.NL);

            plugin_.getStatusBar().setInfo(
                st.countTokens() + " tokens identified.");
        }
    }

    //--------------------------------------------------------------------------
    // SingleLineAction
    //--------------------------------------------------------------------------

    /**
     * Compresses multiple lines in the input text area to a single line in the
     * output text area.
     */
    class SingleLineAction extends AbstractAction
    {
        SingleLineAction()
        {
            super("Convert to single line");
        }

        
        public void actionPerformed(ActionEvent e)
        {
            StringTokenizer st =
                new StringTokenizer(plugin_.getInputText(), StringUtil.NL);

            StringBuffer sb = new StringBuffer();

            while (st.hasMoreElements())
                sb.append(st.nextElement());

            plugin_.getOutputArea().setText(sb.toString());
        }
    }
}