package toolbox.util.ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 * Extends the functionality of JTextArea with the added features of autoscroll
 */
public class JSmartTextArea extends JTextArea
{
    /** auto scrolling of vertical scrollbar **/
    private boolean autoScroll_;
    
    /** antialiasing of textarea font **/
    private boolean antiAlias_ = true;
    
    Map           renderMap_;
    Color         darkblue   = new Color(63, 64, 124);
    Color         darkrose   = new Color(159, 61, 100);
    GradientPaint myGradient = new GradientPaint(0, 0, darkblue, 0, 50,
                                   darkrose);

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Constructor for JSmartTextArea.
     */
    public JSmartTextArea(boolean autoScroll, boolean antiAlias)
    {
        setAutoScroll(autoScroll);
        setAntiAlias(antiAlias);
    }


    /**
     * Constructor for JSmartTextArea.
     * 
     * @param text
     */
    public JSmartTextArea(String text)
    {
        super(text);
    }


    /**
     * Constructor for JSmartTextArea.
     * 
     * @param rows
     * @param columns
     */
    public JSmartTextArea(int rows, int columns)
    {
        super(rows, columns);
    }


    /**
     * Constructor for JSmartTextArea.
     * 
     * @param text
     * @param rows
     * @param columns
     */
    public JSmartTextArea(String text, int rows, int columns)
    {
        super(text, rows, columns);
    }


    /**
     * Constructor for JSmartTextArea.
     * 
     * @param doc
     */
    public JSmartTextArea(Document doc)
    {
        super(doc);
    }


    /**
     * Constructor for JSmartTextArea.
     * 
     * @param doc
     * @param text
     * @param rows
     * @param columns
     */
    public JSmartTextArea(Document doc, String text, int rows, int columns)
    {
        super(doc, text, rows, columns);
    }
    
    //--------------------------------------------------------------------------
    //  Implementation
    //--------------------------------------------------------------------------
    
    /**
     * Override paint to enable antialiasing
     */    
    public void paint(Graphics g) 
    {
        if (isAntiAlias())
        {
            Graphics2D g2 = (Graphics2D) g;
    
            if (renderMap_ == null)
            {
                renderMap_ = new HashMap();
                
                renderMap_.put(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    
                renderMap_.put(
                    RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            }
                
            g2.setRenderingHints(renderMap_);
            g2.setPaint(myGradient);
            super.paint(g2);
        }
        else
            super.paint(g);
    }
    
    
    /**
     * Determines if the autoscroll feature is active
     * 
     * @return  True is autoscroll is enable, false otherwise
     */
    public boolean isAutoScroll()
    {
        return autoScroll_;
    }


    /**
     * Sets the autoScroll feature
     * 
     * @param autoScroll  True to enable autoscroll, false to disable autoscroll
     */
    public void setAutoScroll(boolean autoScroll)
    {
        autoScroll_ = autoScroll;
    }
    

    /**
     * Appends a string to the textarea
     * 
     * @param   str  String to append
     * @see     javax.swing.JTextArea#append(String)
     */
    public void append(String str)
    {
        super.append(str);
        
        if (isAutoScroll())
            scrollToEnd();
    }
    
    
    /**
     * Scrolls to the end of the text area
     */
    public void scrollToEnd()
    {
        setCaretPosition(getDocument().getLength());
    }
    
    
    /**
     * Returns the antiAlias.
     * 
     * @return boolean
     */
    public boolean isAntiAlias()
    {
        return antiAlias_;
    }


    /**
     * Sets the antiAlias.
     * 
     * @param antiAlias The antiAlias to set
     */
    public void setAntiAlias(boolean antiAlias)
    {
        antiAlias_ = antiAlias;
    }
}