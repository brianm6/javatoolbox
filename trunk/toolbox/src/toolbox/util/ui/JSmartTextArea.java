package toolbox.util.ui;

import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 * Extends the functionality of JTextArea with the added features of autoscroll
 */
public class JSmartTextArea extends JTextArea
{
    /** auto scrolling of vertical scrollbar **/
    private boolean autoScroll_;
    
    
    /**
     * Constructor for JSmartTextArea.
     */
    public JSmartTextArea(boolean autoScroll)
    {
        setAutoScroll(autoScroll);
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
    
    
}