package toolbox.util.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;

/**
 * JSmartTextPane 
 */
public class JSmartTextPane extends JTextPane implements AntiAliased
{
    private static final Logger logger_ = 
        Logger.getLogger(JSmartTextPane.class);
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private StyledDocument doc_;
    private Style style_;
    private String styleName_;
    
    /**
     * Flag to activate line wrapping of text.
     */
    private boolean lineWrap_;
    
    /**
     * Flag that toggles antialiasing of text.
     */
    private boolean antiAlias_ = SwingUtil.getDefaultAntiAlias();

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
        
        setLineWrap(false);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Appends a string to the end of the textpane. Makes sure operation is
     * executed on the event dispatch thread otherwise weird things happen!
     * 
     * @param str String to append.
     */
    public void append(final String str)
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            int len = doc_.getLength();
            
            try
            {
                doc_.insertString(len, str, style_);
                setCaretPosition(len);
            }
            catch (BadLocationException e)
            {
                logger_.error(e);
            }
        }
        else
        {
            try
            {
                SwingUtilities.invokeAndWait(
                    new Runnable() {
                        public void run()
                        {
                            append(str);
                        }
                    });
            }
            catch (InterruptedException e)
            {
                logger_.error(e);
            }
            catch (InvocationTargetException e)
            {
                logger_.error(e);
            }
        }
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
    public synchronized void clear()
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

    
    /**
     * @return
     */
    protected boolean isLineWrap()
    {
        return lineWrap_;
    }
    
    
    /**
     * @param lineWrap
     */
    protected void setLineWrap(boolean lineWrap)
    {
        lineWrap_ = lineWrap;
    }
    
    //--------------------------------------------------------------------------
    // Overrides JTextPane
    //--------------------------------------------------------------------------
    
    /**
     * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
     */
    public boolean getScrollableTracksViewportWidth()
    {
        if (isLineWrap())
            return super.getScrollableTracksViewportWidth();
        else
            return getSize().width < getParent().getSize().width;
    }


    /**
     * @see java.awt.Component#setSize(java.awt.Dimension)
     */
    public void setSize(Dimension d)
    {
        if (!isLineWrap())
        {
            if (d.width < getParent().getSize().width)
                d.width = getParent().getSize().width;
        }
        
        super.setSize(d);
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.awt.Component
    //--------------------------------------------------------------------------

    /**
     * Overriden to enable antialiasing.
     *
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g)
    {
        SwingUtil.makeAntiAliased(g, isAntiAliased());
        super.paint(g);
    }
    
    //--------------------------------------------------------------------------
    // Antialiased Interface
    //--------------------------------------------------------------------------
    
    /**
     * Returns true if antialiasing is enabled, false otherwise.
     *
     * @return boolean
     */
    public boolean isAntiAliased()
    {
        return antiAlias_;
    }


    /**
     * Activates antialiasing of text.
     *
     * @param antiAlias True turns antialiasing on; false turns it off.
     */
    public void setAntiAliased(boolean antiAlias)
    {
        boolean old = antiAlias_;
        antiAlias_ = antiAlias;
        firePropertyChange(AntiAliased.PROP_ANTIALIAS, old, antiAlias_);
    }
}