package toolbox.log4j;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartTextArea;

/**
 * A Log4J appender that dumps into a text area.
 */
public class JTextAreaAppender extends AppenderSkeleton
    implements DocumentListener
{
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /** 
     * Text area that logging statements are directed to. 
     */ 
    private JTextArea textArea_;
    
    /** 
     * Layout for logging statements.
     */ 
    private PatternLayout layout_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /** 
     * Creates a new text area appender.
     */
    public JTextAreaAppender()
    {
        this(new JSmartTextArea(true, SwingUtil.getDefaultAntiAlias()));
    }

    
    /** 
     * Creates a new text area appender.
     * 
     * @param textArea Text area to connect the appender to.
     */
    public JTextAreaAppender(JTextArea textArea)
    {
        textArea_ = textArea;
        textArea_.getDocument().addDocumentListener(this);
        layout_ = new PatternLayout("%-5p %3x - %m%n");
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the text area.
     *  
     * @return JTextArea
     */
    public JTextArea getTextArea()
    {
        return textArea_;
    }

    //--------------------------------------------------------------------------
    // Overrides org.apache.log4j.AppenderSkeleton
    //--------------------------------------------------------------------------
    
    /**
     * @see org.apache.log4j.AppenderSkeleton#append(
     *      org.apache.log4j.spi.LoggingEvent)
     */
    public void append(LoggingEvent loggingEvent)
    {
        textArea_.append(layout_.format(loggingEvent));
    }


    /**
     * @see org.apache.log4j.Appender#requiresLayout()
     */
    public boolean requiresLayout()
    {
        return false;
    }


    /**
     * @see org.apache.log4j.Appender#close()
     */
    public void close()
    {
    }

    //--------------------------------------------------------------------------
    // javax.swing.event.DocumentListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see javax.swing.event.DocumentListener#changedUpdate(
     *      javax.swing.event.DocumentEvent)
     */
    public void changedUpdate(DocumentEvent event)
    {
    }


    /**
     * @see javax.swing.event.DocumentListener#removeUpdate(
     *      javax.swing.event.DocumentEvent)
     */
    public void removeUpdate(DocumentEvent event)
    {
    }


    /**
     * Sets the caret position to the end of the text in the text component
     * whenever it is updated.
     * 
     * @see javax.swing.event.DocumentListener#insertUpdate(
     *      javax.swing.event.DocumentEvent)
     */
    public void insertUpdate(DocumentEvent event)
    {
        //textArea_.scrollToEnd();
    }
}