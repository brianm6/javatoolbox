package toolbox.util.ui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.util.Assert;
import toolbox.util.FontUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;

/**
 * Extends the functionality of JTextArea by adding the following features.
 * <ul>
 *   <li>Autoscrolling of output
 *   <li>Anti-aliased text 
 *   <li>Popup menu with cut/copy/paste/save/insert
 *   <li>Capacity limit with the automatic pruning of text
 *   <li>Support to save/restore preferences to XML
 * </ul>
 */
public class JSmartTextArea extends JTextArea
{
    private static final Logger logger_ =
        Logger.getLogger(JSmartTextArea.class);

    private static final String NODE_JSMARTTEXTAREA = "JSmartTextArea";
    private static final String NODE_FONT           = "Font";
    private static final String ATTR_AUTOSCROLL     = "autoscroll";
    private static final String ATTR_ANTIALIAS      = "antialias";
    
    /** 
     * Popup menu for this component 
     */
    private JTextComponentPopupMenu popup_;
    
    /**
     * Check box that toggles autoscroll
     */
    private JCheckBoxMenuItem autoScrollItem_;
    
    /** 
     * Check box that toggles antialiasing of text
     */
    private JCheckBoxMenuItem antiAliasItem_;
    
    /**
     * Maximum number of characters allowable in the text area before the text
     * gets automatically pruned FIFO style.
     */
    private int capacity_;
    
    /**
     * Percentage of text to prune once the capacity is reached. Valid values
     * are in the range [1..100]
     */
    private int pruningFactor_;
    
    //--------------------------------------------------------------------------
    // Constructors
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
        setCapacity(Integer.MAX_VALUE);
        setPruneFactor(0);
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface 
    //--------------------------------------------------------------------------
    
    public void applyPrefs(Element fromNode)
    {
        if (fromNode != null)
        {
            Element prefs = fromNode.getFirstChildElement(NODE_JSMARTTEXTAREA);
            
            setAutoScroll(
                XOMUtil.getBooleanAttribute(prefs, ATTR_AUTOSCROLL, false));
                    
            setAntiAlias(
                XOMUtil.getBooleanAttribute(prefs, ATTR_ANTIALIAS, false));
            
            if (XOMUtil.getFirstChildElement(prefs, NODE_FONT, null) != null)        
                setFont(FontUtil.toFont(prefs.getFirstChildElement(NODE_FONT)));
        }
    }
    
    public void savePrefs(Element inNode)
    {
        Element prefs = new Element(NODE_JSMARTTEXTAREA);
        prefs.addAttribute(new Attribute(ATTR_AUTOSCROLL, isAutoScroll()+""));
        prefs.addAttribute(new Attribute(ATTR_ANTIALIAS, isAntiAlias()+""));
        prefs.appendChild(FontUtil.toElement(getFont()));
        inNode.appendChild(prefs);
    }

    //--------------------------------------------------------------------------
    // Overrides java.awt.Component
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
    // Overrides javax.swing.JTextArea
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
         
            int len = getText().length();

            // Let the pruning begin...            
            if (len > capacity_)
                setText(getText().substring((pruningFactor_/100) * len));  
               
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
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Convenience method to scroll to the bottom of the text area
     */
    public void scrollToEnd()
    {
        setCaretPosition(getDocument().getLength());
    }
    
    /**
     * Returns true if autoscroll is enabled, false otherwise
     * 
     * @return boolean
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
     * Returns true if antialiasing is enabled, false otherwise
     * 
     * @return boolean
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

    /**
     * Returns the maximum number of characters displayable by the text area
     * before the contents get pruned.
     * 
     * @return Max number of displayable characters
     */
    public int getCapacity()
    {
        return capacity_;
    }

    /**
     * Returns the percentage of text that gets pruned from the text area when
     * the capacity is reached.
     * 
     * @return  Percent of text to prune (0 - 100)
     */
    public int getPruneFactor()
    {
        return pruningFactor_;
    }

    /**
     * Sets the max capacity of the text area.
     * 
     * @param i Max capacity
     */
    public void setCapacity(int i)
    {
        capacity_ = i;
    }

    /**
     * Sets the pruning factor
     * 
     * @param f Pruning factor
     */
    public void setPruneFactor(int f)
    {
        Assert.isTrue(f>=0 && f<=100,
            "Pruning factor must be an integer between 0 and 100"); 
                
        pruningFactor_ = f;
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
            repaint();
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