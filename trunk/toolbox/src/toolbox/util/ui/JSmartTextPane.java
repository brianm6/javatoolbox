package toolbox.util.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Map;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * JSmartTextPane is responsible for ___.
 */
public class JSmartTextPane extends JTextPane
{
    private StyledDocument doc_;

    private Font font_;
    private Map styles_;


    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartTextPane.
     */
    public JSmartTextPane()
    {
        buildUI();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Builds the user interface for this component.
     */
    public void buildUI()
    {
        doc_ = getStyledDocument();
        setEditable(false);
    }


    /**
     * @param str
     * @param fg
     * @param bg
     * @throws BadLocationException
     */
    public void append(String str, Color fg, Color bg) 
        throws BadLocationException
    {
        String styleName = "semir";
        Style style = doc_.addStyle(styleName, null);
        StyleConstants.setForeground(style, fg);
        StyleConstants.setBackground(style, bg);
        int len = doc_.getLength();
        doc_.insertString(len, str, style);
        setCaretPosition(len);
    }


    /**
     * 
     */
    public void clear()
    {
        setText("");
    }
}