package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

import nu.xom.Element;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import toolbox.util.FontUtil;
import toolbox.util.PreferencedUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.action.AntiAliasAction;
import toolbox.util.ui.textarea.action.AutoTailAction;
import toolbox.util.ui.textarea.action.LineWrapAction;
import toolbox.workspace.IPreferenced;

/**
 * Extends the functionality of JTextArea by adding the following features.
 * <ul>
 *   <li>Autotailing of output
 *   <li>Anti-aliased text
 *   <li>Popup menu with cut/copy/paste/save/insert
 *   <li>Capacity limit with the automatic pruning of text to a given percentage
 *       when the capacity is reached.
 *   <li>Support to save/restore preferences to XML including font, autotail, 
 *       capacity, and linewrap
 *   <li>Option to make sure all appends are executed on the event dispatch 
 *       thread
 *   <li>Popupmenu access to toggle the built in line wrapping
 * </ul>
 */
public class JSmartTextArea extends JTextArea
    implements AntiAliased, IPreferenced
{
    private static final Logger logger_ =
        Logger.getLogger(JSmartTextArea.class);

    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------

    private static final String NODE_JSMARTTEXTAREA = "JSmartTextArea";
    private static final String NODE_FONT = "Font";

    //--------------------------------------------------------------------------
    // JavaBean Constants
    //--------------------------------------------------------------------------
    
    public static final String PROP_AUTOTAIL = "autoTail";
    public static final String PROP_CAPACITY = "capacity";
    public static final String PROP_LINEWRAP = "lineWrap";
    public static final String PROP_PRUNING_FACTOR = "pruningFactor";
    public static final String PROP_USE_EVENT_DISPATCH_THREAD = 
        "useEventDispatchThread";

    /**
     * List of properties that are saved via the IPreferenced interface.
     */
    public static final String[] SAVED_PROPS = {
        PROP_AUTOTAIL,
        PROP_CAPACITY,
        PROP_PRUNING_FACTOR,
        PROP_LINEWRAP,
        AntiAliased.PROP_ANTIALIAS,
        PROP_USE_EVENT_DISPATCH_THREAD
    };
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Popup menu for this component.
     */
    private JTextComponentPopupMenu popupMenu_;

    /**
     * Flag that toggles antialiasing of text.
     */
    private boolean antiAlias_;

    /**
     * Flag that toggles autotailing of output as it is appended to this
     * textarea.
     */
    private boolean autoTail_;

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
    
    /**
     * Flag to make sure updates to the UI are made on the event dispatch 
     * thread.
     */
    private boolean useEventDispatchThread_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JSmartTextArea with autotail and antialias turned off by
     * default.
     */
    public JSmartTextArea()
    {
        this("");
    }


    /**
     * Creates a JSmartTextArea with the given text and autotail and antialias
     * turned off by default.
     *
     * @param text Initial text of textarea.
     */
    public JSmartTextArea(String text)
    {
        this(text, false, false);
    }


    /**
     * Creates a JSmartTextArea with the given options.
     *
     * @param autoTail Turns on autotailing of output.
     * @param antiAlias Turns on antialiasing of the text.
     */
    public JSmartTextArea(boolean autoTail, boolean antiAlias)
    {
        this("", autoTail, antiAlias);
    }


    /**
     * Creates a JSmartTextArea with the given text and options.
     *
     * @param text Initial text.
     * @param autoTail Turns on autoTail of output.
     * @param antiAlias Turns on antialiasing of the text.
     */
    public JSmartTextArea(String text, boolean autoTail, boolean antiAlias)
    {
        super(text);
        init();
        setAutoTail(autoTail);
        setAntiAliased(antiAlias);
    }


    /**
     * Creates a JSmartTextArea with the given number of rows and columns.
     *
     * @param rows Number of rows.
     * @param columns Number of columns.
     */
    public JSmartTextArea(int rows, int columns)
    {
        super(rows, columns);
        init();
    }


    /**
     * Creates a JSmartTextArea.
     *
     * @param text Initial text.
     * @param rows Number of rows.
     * @param columns Number of columns.
     */
    public JSmartTextArea(String text, int rows, int columns)
    {
        super(text, rows, columns);
        init();
    }


    /**
     * Creates a JSmartTextArea from a Document.
     *
     * @param doc Document.
     */
    public JSmartTextArea(Document doc)
    {
        super(doc);
        init();
    }


    /**
     * Creates a JSmartTextArea from a Document.
     *
     * @param doc Document.
     * @param text Initial text.
     * @param rows Number of rows.
     * @param columns Number of columns.
     */
    public JSmartTextArea(Document doc, String text, int rows, int columns)
    {
        super(doc, text, rows, columns);
        init();
        setAntiAliased(true);
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
     * Returns true if autotail is enabled, false otherwise.
     *
     * @return boolean
     */
    public boolean isAutoTail()
    {
        return autoTail_;
    }


    /**
     * Sets the autoTail feature.
     *
     * @param autoTail True to enable autoTail, false otherwise.
     */
    public void setAutoTail(boolean autoTail)
    {
        boolean old = isAutoTail();
        autoTail_ = autoTail;
        firePropertyChange(PROP_AUTOTAIL, old, autoTail);
    }

    
    /**
     * Returns the maximum number of characters displayable by the text area
     * before the contents get pruned.
     *
     * @return Max number of displayable characters.
     */
    public int getCapacity()
    {
        return capacity_;
    }


    /**
     * Sets the max capacity of the text area.
     *
     * @param capacity Max capacity.
     */
    public void setCapacity(int capacity)
    {
        int old = capacity_;
        capacity_ = capacity;
        firePropertyChange(PROP_CAPACITY, old, capacity_);
    }


    /**
     * Returns the percentage of text that gets pruned from the text area when
     * the capacity is reached.
     *
     * @return Percent of text to prune (0 - 100).
     */
    public int getPruningFactor()
    {
        return pruningFactor_;
    }


    /**
     * Sets the pruning factor.
     *
     * @param pruningFactor Pruning factor.
     * @throws IllegalArgumentException if the pruning factor is not between
     *         0 and 100.
     */
    public void setPruningFactor(int pruningFactor)
    {
        Validate.isTrue(
            pruningFactor >= 0 && pruningFactor <= 100,
            "Pruning factor must be an integer between 0 and 100");

        int old = pruningFactor_;
        pruningFactor_ = pruningFactor;
        firePropertyChange(PROP_PRUNING_FACTOR, old, pruningFactor_);
    }


    /**
     * Returns true if updates to the UI are made on the event dispatch thread,
     * false otherwise.
     * 
     * @return boolean
     */
    public boolean isUseEventDispatchThread()
    {
        return useEventDispatchThread_;
    }


    /**
     * Sets the flag to make sure updates to the UI take place on the event
     * dispatch thread.
     * 
     * @param useEventDispatchThread True to use dispatch thread, false 
     *        otherwise
     */
    public void setUseEventDispatchThread(boolean useEventDispatchThread)
    {
        boolean old = useEventDispatchThread_;
        useEventDispatchThread_ = useEventDispatchThread;
        
        firePropertyChange(
            PROP_USE_EVENT_DISPATCH_THREAD, old, useEventDispatchThread_);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * JSmartTextArea specific initialization routine for the constructors.
     */
    protected void init()
    {
        buildView();
        setCapacity(Integer.MAX_VALUE);
        setPruningFactor(0);
        setAntiAliased(SwingUtil.getDefaultAntiAlias());
        setAutoTail(true);
        setLineWrap(false);
    }


    /**
     * Adds a popupmenu to the textarea.
     */
    protected void buildView()
    {
        //
        // Build popup menu and append autotail, line wrap, and antialias
        // onto the end.
        //
        popupMenu_ = new JTextComponentPopupMenu(this);
        popupMenu_.addSeparator();

        popupMenu_.add(
            new JSmartCheckBoxMenuItem(
                new AutoTailAction(this),
                this,
                PROP_AUTOTAIL));

        popupMenu_.add(
            new JSmartCheckBoxMenuItem(
                new LineWrapAction(this),
                this,
                PROP_LINEWRAP));

        popupMenu_.add(
            new JSmartCheckBoxMenuItem(
                new AntiAliasAction(this),
                this,
                AntiAliased.PROP_ANTIALIAS));
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
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_JSMARTTEXTAREA, new Element(NODE_JSMARTTEXTAREA));

        PreferencedUtil.readPreferences(this, root, SAVED_PROPS);

        if (XOMUtil.getFirstChildElement(root, NODE_FONT, null) != null)
            setFont(FontUtil.toFont(root.getFirstChildElement(NODE_FONT)));
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_JSMARTTEXTAREA);
        PreferencedUtil.writePreferences(this, root, SAVED_PROPS);
        root.appendChild(FontUtil.toElement(getFont()));
        XOMUtil.insertOrReplace(prefs, root);
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
     * @param str String to append.
     * @see javax.swing.JTextArea#append(String)
     */
    public void append(final String str)
    {
        //super.append(str);

        if (SwingUtilities.isEventDispatchThread())
        {
            super.append(str);
            int len = getDocument().getLength();

            // Let the pruning begin...
            if (len > capacity_)
            {
                int nlen = (int) ((float) pruningFactor_ / 100 * len);
                logger_.debug("Pruning " + len + " to " + nlen);
                setText(getText().substring(nlen));
            }

            // Handle autotailing
            if (isAutoTail() && str.indexOf('\n') >= 0)
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
        // NOOP override so line wrap gets included in the property sheet.
        super.setLineWrap(wrap);
    }


    /**
     * @see javax.swing.JTextArea#getLineWrap()
     */
    public boolean getLineWrap()
    {
        // NOOP override so line wrap gets included in the property sheet.
        return super.getLineWrap();
    }
}