package toolbox.plugin.texttools;

import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.regexp.RESyntaxException;

import toolbox.plugin.jtail.filter.RegexLineFilter;
import toolbox.util.StringUtil;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;

/**
 * Panel that allows filtering of text dynamically i.e. As the regular
 * expression is typed in, the matching set is updated accordingly with
 * each keystroke.
 */
public class FilterView extends JPanel
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Reference to the parent plugin.
     */
    private TextToolsPlugin plugin_;

    /**
     * Text field to specify filter contents.
     */
    private JTextField filterField_;

    /**
     * Cache of the filter field contents as it changes character by character.
     */
    private String[] cache_;

    /**
     * Enables listening to filter field so that the filter can be applied
     * in real-time.
     */
    private TextChangedListener docListener_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    FilterView(TextToolsPlugin plugin)
    {
        plugin_ = plugin;
        buildView();
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    protected void buildView()
    {
        setLayout(new FlowLayout());
        add(new JSmartLabel("Filter"));
        add(filterField_ = new JSmartTextField(20));
        add(new JSmartLabel("(regular expression)"));
        filterField_.addKeyListener(new FilterKeyListener());
        docListener_ = new TextChangedListener();
    }


    /**
     * Filters text based on a regular expression.
     *
     * @param regex Regular expression.
     */
    protected void filter(String regex)
    {
        plugin_.getStatusBar().setInfo("Regex = '" + regex + "'");

        if (cache_ == null)
            cache_ = StringUtil.tokenize(plugin_.getInputText(), "\n");

        String[] lines = cache_;

        StringBuffer sb = new StringBuffer();
        RegexLineFilter filter = null;

        try
        {
            filter = new RegexLineFilter(regex);
            filter.setEnabled(true);

            for (int i = 0; i < lines.length; i++)
            {
                StringBuffer tmp = new StringBuffer(lines[i]);
                
                if (filter.filter(tmp))
                {
                    sb.append(tmp);
                    sb.append("\n");
                }
            }

            // Want to ignore document change events while the filter is
            // updating the text area. Just detach and reattach after
            // mutations are done.

            JTextArea area = plugin_.getOutputArea();
            area.getDocument().removeDocumentListener(docListener_);

            area.setText(sb.toString());
            area.moveCaretPosition(0);

            area.getDocument().
            addDocumentListener(docListener_);

        }
        catch (RESyntaxException e)
        {
            // The regular expression is going to be invalid as the user
            // types it in up just shoot the message to the status bar
            plugin_.getStatusBar().setError(e.getMessage());
        }
    }

    //--------------------------------------------------------------------------
    // FilterKeyListener
    //--------------------------------------------------------------------------

    /**
     * Enabled dynamic filtering  of regex as it is typed.
     */
    class FilterKeyListener extends KeyAdapter
    {
        /**
         * Remebers the previous contents of the filter.
         */
        private String oldValue_ = "";

        
        public void keyReleased(KeyEvent e)
        {
            super.keyReleased(e);

            String newValue = filterField_.getText().trim();

            // Only refresh if the filter has changed
            if (!newValue.equals(oldValue_))
            {
                oldValue_ = newValue;
                filter(newValue);
            }
        }
    }

    //--------------------------------------------------------------------------
    // TextChangedListener
    //--------------------------------------------------------------------------

    /**
     * Catchs modifications to the original document so that we know when to
     * throw away our cached copy of the text currently being regex'ed.
     */
    class TextChangedListener implements DocumentListener
    {
        public void changedUpdate(DocumentEvent e)
        {
            crud("changed ");
        }


        public void insertUpdate(DocumentEvent e)
        {
            crud("insert ");
        }


        public void removeUpdate(DocumentEvent e)
        {
            crud("remove ");
        }


        protected void crud(String s)
        {
            s.toString();
            cache_ = null;
        }
    }
}