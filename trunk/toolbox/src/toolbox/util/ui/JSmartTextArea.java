package toolbox.util.ui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import toolbox.util.SwingUtil;

/**
 * Extends the functionality of JTextArea by adding:
 * <ul>
 *  <li>Autoscrolling of output</li>
 *  <li>Popup menu with cut/copy/paste/save/insert</li>
 *  <li>Anti-aliased text</li>
 * </ul>
 */
public class JSmartTextArea extends JTextArea // implements IPreferenced
{
    // TODO: Implement saving of preferences  
    
    //private static final Logger logger_ =
    //    Logger.getLogger(JSmartTextArea.class);
    
    /** 
     * Popup menu for this component 
     */
    private JPopupMenu popup_;
    
    /**
     * Check box that toggles autoscroll
     */
    private JCheckBoxMenuItem autoScrollItem_;
    
    /** 
     * Check box that toggles antialiasing of text
     */
    private JCheckBoxMenuItem antiAliasItem_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JSmartTextArea with autoscroll and antialias turned off by
     * default.
     */
    public JSmartTextArea()
    {
        this("");
    }

    /**
     * Creates a JSmartTextArea with the given text and autoscroll and antialias
     * turned off by default.
     * 
     * @param text  Initial text of textarea
     */
    public JSmartTextArea(String text)
    {
        this(text, false, false);
    }

    /**
     * Creates a JSmartTextArea with the given options
     * 
     * @param  autoScroll  Turns on autoscroll of output
     * @param  antiAlias   Turns on antialiasing of the text
     */
    public JSmartTextArea(boolean autoScroll, boolean antiAlias)
    {
        this("", autoScroll, antiAlias);
    }

    /**
     * Creates a JSmartTextArea with the given text and options
     * 
     * @param  text        Initial text
     * @param  autoScroll  Turns on autoscroll of output
     * @param  antiAlias   Turns on antialiasing of the text
     */
    public JSmartTextArea(String text, boolean autoScroll, boolean antiAlias)
    {
        super(text);
        buildView();
        setAutoScroll(autoScroll);
        setAntiAlias(antiAlias);
    }

    //--------------------------------------------------------------------------
    //  Public 
    //--------------------------------------------------------------------------
    
    /**
     * Convenience method to scroll to the bottom of the text area
     */
    public void scrollToEnd()
    {
        setCaretPosition(getDocument().getLength());
    }

    //--------------------------------------------------------------------------
    //  Overrides java.awt.Component
    //--------------------------------------------------------------------------
    
    /**
     * Overrides paint to enable antialiasing
     * 
     * @param  g  Graphics context
     */    
    public void paint(Graphics g) 
    {
        SwingUtil.setAntiAlias(g, isAntiAlias());
        super.paint(g);
    }
    
    //--------------------------------------------------------------------------
    //  Overrides javax.swing.JTextArea
    //--------------------------------------------------------------------------
    
    /**
     * Appends a string to the textarea. If the current thread is not the
     * event dispatch thread, then it is queued up on the event dispatch
     * thread.
     * 
     * @param   str  String to append
     * @see     javax.swing.JTextArea#append(String)
     */
    public void append(final String str)
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            super.append(str);
            
            if (isAutoScroll())
                scrollToEnd();
        }
        else
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    append(str);
                }
            });
        }
    }
    
    //--------------------------------------------------------------------------
    //  Accessors/Mutators
    //--------------------------------------------------------------------------
    
    /**
     * @return  True is autoscroll is enabled, false otherwise
     */
    public boolean isAutoScroll()
    {
        return autoScrollItem_.isSelected();
    }

    /**
     * Sets the autoScroll feature
     * 
     * @param autoScroll  True to enable autoscroll, false to disable autoscroll
     */
    public void setAutoScroll(boolean autoScroll)
    {
        autoScrollItem_.setSelected(autoScroll);
    }
   
    /**
     * @return True if antialiasing is enabled, false otherwise
     */
    public boolean isAntiAlias()
    {
        return antiAliasItem_.isSelected();
    }

    /**
     * Activates antialiasing of text
     * 
     * @param antiAlias True turns antialiasing on; false turns it off
     */
    public void setAntiAlias(boolean antiAlias)
    {
        antiAliasItem_.setSelected(antiAlias);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Adds a popupmenu to the textarea
     */
    protected void buildView()
    {
        // Build popup menu and add register with textarea
        autoScrollItem_ = new JCheckBoxMenuItem(new AutoScrollAction());
        antiAliasItem_  = new JCheckBoxMenuItem(new AntiAliasAction());
        popup_ = new JTextComponentPopupMenu(this);    
        popup_.addSeparator();    
        popup_.add(autoScrollItem_);
        popup_.add(antiAliasItem_);
    }    
    
    //--------------------------------------------------------------------------
    //  Actions
    //--------------------------------------------------------------------------
    
    /**
     * Toggles autoscroll
     */    
    class AutoScrollAction extends AbstractAction 
    {
        public AutoScrollAction()
        {
            super("AutoScroll");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            // NO OP
        }
    }    

    /**
     * Toggles antialiasing
     */
    class AntiAliasAction extends AbstractAction 
    {
        public AntiAliasAction()
        {
            super("AntiAlias");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            // NO OP
        }
    }
    
    /**
     * Clears the text area
     */
    public class ClearAction extends AbstractAction
    {
        public ClearAction()
        {
            this("Clear");
        }
        
        public ClearAction(String name)
        {
            super(name);
            putValue(MNEMONIC_KEY, new Integer('C'));
            putValue(SHORT_DESCRIPTION, "Clears the output");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            setText("");
        }
    }
}