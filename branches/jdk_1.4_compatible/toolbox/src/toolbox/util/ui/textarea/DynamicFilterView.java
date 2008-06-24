package toolbox.util.ui.textarea;

import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.regexp.RESyntaxException;

import toolbox.plugin.jtail.filter.RegexLineFilter;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;

/**
 * DynamicFilterView is a simple panel that contains a label and a textfield.
 * The textfield is for the input of a regular expression. The view is 
 * associated with an existing text area for the purposes of filtering its 
 * contents. As a regular expression is typed in, the contents of the text area
 * are filtered in real-time to only the lines that match the filter. The
 * responsiveness of typing into the regular expression field is directly
 * related to number of lines in the textarea. Do not use for large volumes
 * of text that may compromise the responsiveness of the application. 
 */
public class DynamicFilterView extends JPanel
{
    private static final Logger logger_ = 
        Logger.getLogger(DynamicFilterView.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Reference to the parent plugin.
     */
    private JTextArea textArea_;

    /**
     * Text field to specify filter contents.
     */
    private JTextField filterField_;

    /**
     * Cache of the filter field contents as it changes character by character.
     */
    private String[] cache_;

    /**
     * Enables listening to filter field so that the filter can be applied in 
     * real-time.
     */
    private TextChangedListener docListener_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a DynamicFilterView.
     *
     * @param textArea Text area to attach this filterer to.
     */
    public DynamicFilterView(JTextArea textArea)
    {
        textArea_ = textArea;
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
        // Break the text up into lines and store in the cache.
        // TextChangedListener will invalidate the cache when the underlying
        // document is changed. 
        
        if (cache_ == null)
            cache_ = StringUtils.split(textArea_.getText(), '\n');

        String[] lines = cache_;

        StringBuffer sb = new StringBuffer();
        RegexLineFilter filter = null;

        try
        {
            filter = new RegexLineFilter(regex, false);
            filter.setEnabled(true);

            for (int i = 0; i < lines.length; i++)
            {
                boolean passed = filter.filter(new StringBuffer(lines[i]));

                if (passed)
                {
                    sb.append(lines[i]);
                    sb.append("\n");
                }
            }

            // Want to ignore document change events while the filter is
            // updating the text area. Just detach and reattach after
            // mutations are done.

            textArea_.getDocument().removeDocumentListener(docListener_);
            textArea_.setText(sb.toString());
            textArea_.moveCaretPosition(0);
            textArea_.getDocument().addDocumentListener(docListener_);
        }
        catch (RESyntaxException re)
        {
            logger_.error(re);
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

        
        /**
         * @see java.awt.event.KeyListener#keyReleased(
         *      java.awt.event.KeyEvent)
         */
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
        /**
         * @see javax.swing.event.DocumentListener#changedUpdate(
         *      javax.swing.event.DocumentEvent)
         */
        public void changedUpdate(DocumentEvent e)
        {
            crud("changed ");
        }


        /**
         * @see javax.swing.event.DocumentListener#insertUpdate(
         *      javax.swing.event.DocumentEvent)
         */
        public void insertUpdate(DocumentEvent e)
        {
            crud("insert ");
        }


        /**
         * @see javax.swing.event.DocumentListener#removeUpdate(
         *      javax.swing.event.DocumentEvent)
         */
        public void removeUpdate(DocumentEvent e)
        {
            crud("remove ");
        }


        /**
         * Resets the cache.
         *
         * @param s String
         */
        protected void crud(String s)
        {
            s.toString();
            cache_ = null;
        }
    }
}