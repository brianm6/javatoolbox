package toolbox.util.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

/**
 * JSmartTextPane is responsible for ___.
 */
public class JSmartTextPane extends JTextPane
{
    private static final Logger logger_ = 
        Logger.getLogger(JSmartTextPane.class);
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private StyledDocument doc_;
    private Style style_;
    private String styleName_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartTextPane.
     */
    public JSmartTextPane()
    {
        doc_ = getStyledDocument();
        setEditable(false);
        styleName_ = getClass().getName();
        style_ = doc_.addStyle(styleName_, null);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Appends a string to the end of the textpane.
     * 
     * @param str String to append.
     */
    public void append(String str)
    {
        int len = doc_.getLength();
        
        try
        {
            doc_.insertString(len, str, style_);
        }
        catch (BadLocationException e)
        {
            logger_.error(e);
        }
        setCaretPosition(len);
    }

    
    /**
     * Appends a string to the end of the textpane.
     * 
     * @param str String to append.
     * @param fg Foreground color of the text.
     * @param bg Background color of the text.
     * @throws BadLocationException if inserting to an invalid location.
     */
    public void append(String str, Color fg, Color bg) 
        throws BadLocationException
    {
        setActiveForeground(fg);
        setActiveBackground(bg);
        append(str);
    }

    
    /**
     * Clears the contents of the textpane. 
     */
    public void clear()
    {
        setText("");
    }

    
    /**
     * Sets the active font.
     * 
     * @param font Font
     */
    public void setActiveFont(Font font)
    {
        StyleConstants.setFontFamily(style_, font.getFamily());
        StyleConstants.setFontSize(style_, font.getSize());
        StyleConstants.setBold(style_, font.getStyle() == Font.BOLD);
        StyleConstants.setItalic(style_, font.getStyle() == Font.ITALIC);
    }
    
    
    /**
     * Sets the active background color.
     * 
     * @param bg Background color.
     */
    public void setActiveBackground(Color bg)
    {
        StyleConstants.setBackground(style_, bg);
    }

    
    /**
     * Sets the active foreground color.
     * 
     * @param fg Foreground color.
     */
    public void setActiveForeground(Color fg)
    {
        StyleConstants.setForeground(style_, fg);
    }

    //--------------------------------------------------------------------------
    // Overrides JTextPane
    //--------------------------------------------------------------------------
    
    /**
     * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
     */
    public boolean getScrollableTracksViewportWidth()
    {
        if (getSize().width < getParent().getSize().width)
            return true;
        else
            return false;
    }


    /**
     * @see java.awt.Component#setSize(java.awt.Dimension)
     */
    public void setSize(Dimension d)
    {
        if (d.width < getParent().getSize().width)
            d.width = getParent().getSize().width;
        super.setSize(d);
    }
}