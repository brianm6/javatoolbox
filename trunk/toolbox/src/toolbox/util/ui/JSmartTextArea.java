package toolbox.util.ui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.Assert;
import toolbox.util.FontUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;

/**
 * Extends the functionality of JTextArea by adding the following features.
 * <ul>
 *   <li>Autoscrolling of output
 *   <li>Anti-aliased text 
 *   <li>Popup menu with cut/copy/paste/save/insert
 *   <li>Capacity limit with the automatic pruning of text
 *   <li>Support to save/restore preferences to XML
 *   <li>Makes sure all appends are executed on the event dispatch thread
 *   <li>Popupmenu access to toggle the built in line wrapping
 * </ul>
 */
public class JSmartTextArea extends JTextArea implements AntiAliased, 
    IPreferenced
{
    private static final Logger logger_ =
        Logger.getLogger(JSmartTextArea.class);

    private static final String NODE_JSMARTTEXTAREA   = "JSmartTextArea";
    private static final String   ATTR_AUTOSCROLL     = "autoscroll";
    private static final String   ATTR_ANTIALIAS      = "antialias";
    private static final String   ATTR_CAPACITY       = "capacity";
    private static final String   ATTR_PRUNING_FACTOR = "pruningFactor";
    private static final String   ATTR_WRAPLINES      = "wrapLines";
    private static final String NODE_FONT             = "Font";
    
    /** 
     * Popup menu for this component. 
     */
    private JTextComponentPopupMenu popupMenu_;
    
    /**
     * Check box that toggles autoscroll.
     */
    private JCheckBoxMenuItem autoScrollCheckBox_;
    
    /** 
     * Check box that toggles antialiasing of text.
     */
    private JCheckBoxMenuItem antiAliasCheckBox_;
    
    /**
     * Check box that toggles line wrapping.
     */
    private JCheckBoxMenuItem wrapLinesCheckBox_;
    
    /**
     * Maximum number of characters allowable in the text area before the text
     * gets automatically pruned FIFO style.
     */
    private int capacity_;
    
    /**
     * Percentage of text to prune once the capacity is reached. Valid values
     * are in the range [1..100].
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
     * @param text Initial text of textarea
     */
    public JSmartTextArea(String text)
    {
        this(text, false, false);
    }


    /**
     * Creates a JSmartTextArea with the given options.
     * 
     * @param autoScroll Turns on autoscroll of output
     * @param antiAlias Turns on antialiasing of the text
     */
    public JSmartTextArea(boolean autoScroll, boolean antiAlias)
    {
        this("", autoScroll, antiAlias);
    }


    /**
     * Creates a JSmartTextArea with the given text and options.
     * 
     * @param text Initial text
     * @param autoScroll Turns on autoscroll of output
     * @param antiAlias Turns on antialiasing of the text
     */
    public JSmartTextArea(String text, boolean autoScroll, boolean antiAlias)
    {
        super(text);
        buildView();
        setAutoScroll(autoScroll);
        setAntiAliased(antiAlias);
        setCapacity(Integer.MAX_VALUE);
        setPruneFactor(0);
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface 
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs)
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_JSMARTTEXTAREA, new Element(NODE_JSMARTTEXTAREA));
        
        setAutoScroll(XOMUtil.getBooleanAttribute(
            root, ATTR_AUTOSCROLL, true));
                
        setAntiAliased(XOMUtil.getBooleanAttribute(
            root, ATTR_ANTIALIAS, true));
        
        setCapacity(XOMUtil.getIntegerAttribute(
            root, ATTR_CAPACITY, Integer.MAX_VALUE));

        setPruneFactor(XOMUtil.getIntegerAttribute(
            root, ATTR_PRUNING_FACTOR, 0));

        setLineWrap(XOMUtil.getBooleanAttribute(
            root, ATTR_WRAPLINES, false));
                        
        if (XOMUtil.getFirstChildElement(root, NODE_FONT, null) != null)        
            setFont(FontUtil.toFont(root.getFirstChildElement(NODE_FONT)));
    }
    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element root = new Element(NODE_JSMARTTEXTAREA);
        root.addAttribute(new Attribute(ATTR_AUTOSCROLL, isAutoScroll()+""));
        root.addAttribute(new Attribute(ATTR_ANTIALIAS, isAntiAliased()+""));
        root.addAttribute(new Attribute(ATTR_CAPACITY, getCapacity()+""));
        root.addAttribute(new Attribute(ATTR_WRAPLINES, getLineWrap()+""));
        
        root.addAttribute(
            new Attribute(ATTR_PRUNING_FACTOR,getPruneFactor()+""));
        
        root.appendChild(FontUtil.toElement(getFont()));
        prefs.appendChild(root);
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
    // Overrides javax.swing.JTextArea
    //--------------------------------------------------------------------------
    
    /**
     * Appends a string to the textarea. If the current thread is not the
     * event dispatch thread, then it is queued up on the event dispatch
     * thread.
     * 
     * @param str String to append
     * @see javax.swing.JTextArea#append(String)
     */
    public void append(final String str)
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            super.append(str);
            int len = getDocument().getLength();

            // Let the pruning begin...            
            if (len > capacity_)
            {
                int nlen = (int) ((float) pruningFactor_/100 * len);
                logger_.debug("Pruning " + len + " to " + nlen);
                setText(getText().substring(nlen));
            }  
            
            // Handle autoscrolling   
            if (isAutoScroll() && str.indexOf('\n') >= 0)
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
    
    
    /**
     * @see javax.swing.JTextArea#setLineWrap(boolean)
     */
    public void setLineWrap(boolean wrap)
    {
        // Have to keep the cb on the popup menu in sync with the swing comps
        // internal state.
        wrapLinesCheckBox_.setSelected(wrap);
        super.setLineWrap(wrap);
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Convenience method to scroll to the bottom of the text area.
     */
    public void scrollToEnd()
    {
        setCaretPosition(getDocument().getLength());
    }
    
    
    /**
     * Returns true if autoscroll is enabled, false otherwise.
     * 
     * @return boolean
     */
    public boolean isAutoScroll()
    {
        return autoScrollCheckBox_.isSelected();
    }


    /**
     * Sets the autoScroll feature.
     * 
     * @param autoScroll  True to enable autoscroll, false to disable autoscroll
     */
    public void setAutoScroll(boolean autoScroll)
    {
        autoScrollCheckBox_.setSelected(autoScroll);
    }
   
   
    /**
     * Returns true if antialiasing is enabled, false otherwise.
     * 
     * @return boolean
     */
    public boolean isAntiAliased()
    {
        return antiAliasCheckBox_.isSelected();
    }


    /**
     * Activates antialiasing of text.
     * 
     * @param antiAlias True turns antialiasing on; false turns it off
     */
    public void setAntiAliased(boolean antiAlias)
    {
        antiAliasCheckBox_.setSelected(antiAlias);
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
     * Sets the pruning factor.
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
     * Adds a popupmenu to the textarea.
     */
    protected void buildView()
    {
        // Build popup menu and add register with textarea
        autoScrollCheckBox_ = new JSmartCheckBoxMenuItem(new AutoScrollAction());
        antiAliasCheckBox_ = new JSmartCheckBoxMenuItem(new AntiAliasAction());
        wrapLinesCheckBox_ = new JSmartCheckBoxMenuItem(new WrapLinesAction());
        
        popupMenu_ = new JTextComponentPopupMenu(this);
        popupMenu_.addSeparator();
        popupMenu_.add(autoScrollCheckBox_);
        popupMenu_.add(antiAliasCheckBox_);
        popupMenu_.add(wrapLinesCheckBox_);
    }    

    //--------------------------------------------------------------------------
    //  Actions
    //--------------------------------------------------------------------------
    
    /**
     * Toggles autoscroll.
     */    
    class AutoScrollAction extends AbstractAction 
    {
        public AutoScrollAction()
        {
            super("AutoScroll");
        }
        
        
        public void actionPerformed(ActionEvent e)
        {
            if (isAutoScroll())
                scrollToEnd(); 
        }
    }    

    /**
     * Toggles antialiasing.
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
     * Toggles line wrapping.
     */
    class WrapLinesAction extends AbstractAction 
    {
        public WrapLinesAction()
        {
            super("Wrap Lines");
        }
        
        
        public void actionPerformed(ActionEvent e)
        {
            setLineWrap(wrapLinesCheckBox_.isSelected());
        }
    }

    /**
     * Clears the text area.
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