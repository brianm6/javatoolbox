package toolbox.util.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.OptionPaneUI;

import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;

/**
 * JSmartOptionPane (mutated JOptionPane to support dialogs with a flippable 
 * detailed message area)
 */
public class JSmartOptionPane extends JOptionPane implements ActionListener
{

    private JButton okButton_;
    private JButton detailsButton_;
    private boolean expanded_;

    private static final String BUTTON_COLLAPSED = "Details >>";
    private static final String BUTTON_EXPANDED = "<< No Details";

    private JTextArea detailArea_;
    private JScrollPane detailScroller_;

    private JButton[] buttons_;

    /** Icon used in pane. **/
    private transient Icon icon_;
    
    /** Message to display. **/
    private transient Object message_;

    /** Message details **/
    private Object details_;

    private Window parent_;

    /** Options to display to the user. */
    private transient Object[] options_;

    /** Value that should be initially selected in <code>options</code>. */
    private transient Object initialValue_;

    /** Message type. */
    private int messageType_;

    /**
     * Option type, one of <code>DEFAULT_OPTION</code>,
     * <code>YES_NO_OPTION</code>,
     * <code>YES_NO_CANCEL_OPTION</code> or
     * <code>OK_CANCEL_OPTION</code>.
     */
    private int optionType_;

    /** 
     * Currently selected value, will be a valid option, or
     * <code>UNINITIALIZED_VALUE</code> or <code>null</code>. 
     */
    private Object value_;

    /** 
     * Array of values the user can choose from. Look and feel will
     * provide the UI component to choose this from. 
     */
    private Object[] selectionValues_;

    /** Value the user has input. */
    private Object inputValue_;
    
    /** Initial value to select in <code>selectionValues</code>. */
    private Object initialSelectionValue_;

    /** If true, a UI widget will be provided to the user to get input. */
    private boolean wantsInput_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a <code>JOptionPane</code> with a test message.
     */
    public JSmartOptionPane()
    {
        this("JSmartOptionPane message", "JSmartOptionPane details");
    }


    /**
     * Creates a instance of <code>JOptionPane</code> to display a
     * message using the 
     * plain-message message type and the default options delivered by
     * the UI.
     *
     * @param  message  <code>Object</code> to display
     * @param  details  Message Details
     */
    public JSmartOptionPane(Object message, Object details)
    {
        this(message, details, JOptionPane.PLAIN_MESSAGE);
    }


    /**
     * Creates an instance of <code>JOptionPane</code> to display a message
     * with the specified message type and the default options,
     *
     * @param message       the <code>Object</code> to display
     * @param details       Message details
     * @param messageType   the type of message to be displayed:
     *                      <code>ERROR_MESSAGE</code>,
     *                      <code>INFORMATION_MESSAGE</code>,
     *                      <code>WARNING_MESSAGE</code>,
     *                      <code>QUESTION_MESSAGE</code>,
     *                      or <code>PLAIN_MESSAGE</code>
     */
    public JSmartOptionPane(Object message, Object details, int messageType)
    {
        this(message, details, messageType, JOptionPane.DEFAULT_OPTION);
    }


    /**
     * Creates an instance of <code>JOptionPane</code> to display a message
     * with the specified message type and options.
     *
     * @param message       the <code>Object</code> to display
     * @param details       Message details
     * @param messageType   the type of message to be displayed:
     *                      <code>ERROR_MESSAGE</code>,
     *                      <code>INFORMATION_MESSAGE</code>,
     *                      <code>WARNING_MESSAGE</code>,
     *                      <code>QUESTION_MESSAGE</code>,
     *                      or <code>PLAIN_MESSAGE</code>
     * @param optionType    the options to display in the pane:
     *                      <code>DEFAULT_OPTION</code>, 
     *                      <code>YES_NO_OPTION</code>,
     *                      <code>YES_NO_CANCEL_OPTION</code>,
     *                      <code>OK_CANCEL_OPTION</code>
     */
    public JSmartOptionPane(
        Object message,
        Object details,
        int messageType,
        int optionType)
    {
        this(message, details, messageType, optionType, null);
    }


    /**
     * Creates an instance of <code>JOptionPane</code> to display a message
     * with the specified message type, options, and icon.
     *
     * @param message     the <code>Object</code> to display
     * @param details     the message details shown in flipper area
     * @param messageType the type of message to be displayed:
     *                    <code>ERROR_MESSAGE</code>,
     *                    <code>INFORMATION_MESSAGE</code>,
     *                    <code>WARNING_MESSAGE</code>,
     *                    <code>QUESTION_MESSAGE</code>, or
     *                    <code>PLAIN_MESSAGE</code>
     * @param optionType  the options to display in the pane:
     *                    <code>DEFAULT_OPTION</code>, 
     *                    <code>YES_NO_OPTION</code>,
     *                    <code>YES_NO_CANCEL_OPTION</code>,
     *                    <code>OK_CANCEL_OPTION</code>
     * @param icon        the <code>Icon</code> image to display
     */
    public JSmartOptionPane(
        Object message,
        Object details,
        int messageType,
        int optionType,
        Icon icon)
    {
        this(message, details, messageType, optionType, icon, null);
    }


    /**
     * Creates an instance of <code>JOptionPane</code> to display a message
     * with the specified message type, icon, and options.
     * None of the options is initially selected.
     * <p>
     * The options objects should contain either instances of
     * <code>Component</code>s, (which are added directly) or
     * <code>Strings</code> (which are wrapped in a <code>JButton</code>).
     * If you provide <code>Component</code>s, you must ensure that when the
     * <code>Component</code> is clicked it messages <code>setValue</code>
     * in the created <code>JOptionPane</code>.
     *
     * @param message the <code>Object</code> to display
     * @param details       Message details
     * @param messageType the type of message to be displayed:
     *                  <code>ERROR_MESSAGE</code>, 
     *          <code>INFORMATION_MESSAGE</code>,
     *          <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @param optionType the options to display in the pane:
     *                  <code>DEFAULT_OPTION</code>,
     *          <code>YES_NO_OPTION</code>,
     *          <code>YES_NO_CANCEL_OPTION</code>,
     *                  <code>OK_CANCEL_OPTION</code>
     * @param icon the <code>Icon</code> image to display
     * @param options  the choices the user can select
     */
    public JSmartOptionPane(
        Object message,
        Object details,
        int messageType,
        int optionType,
        Icon icon,
        Object[] options)
    {
        this(message, details, messageType, optionType, icon, options, null);
    }


    /**
     * Creates an instance of <code>JOptionPane</code> to display a message
     * with the specified message type, icon, and options, with the 
     * initially-selected option specified.
     *
     * @param message the <code>Object</code> to display
     * @param details       Message details
     * @param messageType the type of message to be displayed:
     *                  <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>,
     *          <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @param optionType the options to display in the pane:
     *                  <code>DEFAULT_OPTION</code>,
     *          <code>YES_NO_OPTION</code>,
     *          <code>YES_NO_CANCEL_OPTION</code>,
     *                  <code>OK_CANCEL_OPTION</code>
     * @param icon the Icon image to display
     * @param options  the choices the user can select
     * @param initialValue the choice that is initially selected; if
     *          <code>null</code>, then nothing will be initially selected;
     *          only meaningful if <code>options</code> is used
     */
    public JSmartOptionPane(
        Object message,
        Object details,
        int messageType,
        int optionType,
        Icon icon,
        Object[] options,
        Object initialValue)
    {
        message_ = message;
        details_ = details;

        if (options == null || options.length == 0)
            options_ = getButtons();
        else
            options_ = options;

        initialValue_ = initialValue;
        icon_ = icon;
        setMessageType(messageType);
        setOptionType(optionType);
        value_ = JOptionPane.UNINITIALIZED_VALUE;
        inputValue_ = JOptionPane.UNINITIALIZED_VALUE;
        updateUI();
    }

    //--------------------------------------------------------------------------
    //  Implemenation
    //--------------------------------------------------------------------------

    /**
     * Brings up a dialog displaying a message, specifying all parameters.
     *
     * @param parentComponent determines the <code>Frame</code> in which the
     *          dialog is displayed; if <code>null</code>,
     *          or if the <code>parentComponent</code> has no
     *          <code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param details       Message details
     * @param title     the title string for the dialog
     * @param messageType the type of message to be displayed:
     *                  <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>,
     *          <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @param icon      an icon to display in the dialog that helps the user
     *                  identify the kind of message that is being displayed
     */
    public static void showDetailedMessageDialog(
        Component parentComponent,
        Object message,
        Object details,
        String title,
        int messageType,
        Icon icon)
    {
        showOptionDialog(
            parentComponent,
            message,
            details,
            title,
            JOptionPane.DEFAULT_OPTION,
            messageType,
            icon,
            null,
            null);
    }


    /**
      * Brings up a dialog that displays a message using a default
      * icon determined by the <code>messageType</code> parameter.
      *
      * @param parentComponent determines the <code>Frame</code>
      *      in which the dialog is displayed; if <code>null</code>,
      *      or if the <code>parentComponent</code> has no
      *      <code>Frame</code>, a default <code>Frame</code> is used
      * @param message   the <code>Object</code> to display
      * @param details       Message details
      * @param title     the title string for the dialog
      * @param messageType the type of message to be displayed:
      *                  <code>ERROR_MESSAGE</code>,
      *          <code>INFORMATION_MESSAGE</code>,
      *          <code>WARNING_MESSAGE</code>,
      *                  <code>QUESTION_MESSAGE</code>,
      *          or <code>PLAIN_MESSAGE</code>
      */
    public static void showDetailedMessageDialog(
        Component parentComponent,
        Object message,
        Object details,
        String title,
        int messageType)
    {
        showDetailedMessageDialog(
            parentComponent,
            message,
            details,
            title,
            messageType,
            null);
    }


    /**
     * Brings up an information-message dialog titled "Message".
     *
     * @param parentComponent determines the <code>Frame</code> in
     *      which the dialog is displayed; if <code>null</code>,
     *      or if the <code>parentComponent</code> has no
     *      <code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param details   Message details
     */
    public static void showDetailedMessageDialog(
        Component parentComponent,
        Object message,
        Object details)
    {
        showDetailedMessageDialog(
            parentComponent,
            message,
            details,
            UIManager.getString("OptionPane.messageDialogTitle"),
            JOptionPane.INFORMATION_MESSAGE);
    }


    /**
     * Shows an error message dialog box with with the exception stack trace
     * as the message detail
     * 
     * @param  parentComponent  Parent component
     * @param  exception        Exception
     */
    public static void showExceptionMessageDialog(Component parentComponent,
        Throwable exception)
    {
        showDetailedMessageDialog(parentComponent, exception.getMessage(),
            ExceptionUtil.getStackTrace(exception), "Error", 
                JOptionPane.ERROR_MESSAGE);    
    }


    /**
     * Brings up a dialog with a specified icon, where the initial
     * choice is determined by the <code>initialValue</code> parameter and
     * the number of choices is determined by the <code>optionType</code> 
     * parameter.
     * <p>
     * If <code>optionType</code> is <code>YES_NO_OPTION</code>,
     * or <code>YES_NO_CANCEL_OPTION</code>
     * and the <code>options</code> parameter is <code>null</code>,
     * then the options are
     * supplied by the look and feel. 
     * <p>
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the look and feel.
     *
     * @param parentComponent determines the <code>Frame</code>
     *          in which the dialog is displayed;  if 
     *                  <code>null</code>, or if the
     *          <code>parentComponent</code> has no
     *          <code>Frame</code>, a 
     *                  default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param details   Message details
     * @param title     the title string for the dialog
     * @param optionType an integer designating the options available on the
     *          dialog: <code>YES_NO_OPTION</code>,
     *          or <code>YES_NO_CANCEL_OPTION</code>
     * @param messageType an integer designating the kind of message this is, 
     *                  primarily used to determine the icon from the
     *          pluggable Look and Feel: <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>, 
     *                  <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @param icon      the icon to display in the dialog
     * @param options   an array of objects indicating the possible choices
     *                  the user can make; if the objects are components, they
     *                  are rendered properly; non-<code>String</code>
     *          objects are
     *                  rendered using their <code>toString</code> methods;
     *                  if this parameter is <code>null</code>,
     *          the options are determined by the Look and Feel
     * @param initialValue the object that represents the default selection
     *                  for the dialog; only meaningful if <code>options</code>
     *          is used; can be <code>null</code>
     * @return an integer indicating the option chosen by the user, 
     *              or <code>CLOSED_OPTION</code> if the user closed
     *                  the dialog
     */
    public static int showOptionDialog(
        Component parentComponent,
        Object message,
        Object details,
        String title,
        int optionType,
        int messageType,
        Icon icon,
        Object[] options,
        Object initialValue)
    {
        JSmartOptionPane pane =
            new JSmartOptionPane(
                message,
                details,
                messageType,
                optionType,
                icon,
                options,
                initialValue);

        pane.setInitialValue(initialValue);
        pane.setComponentOrientation(
            ((parentComponent == null) ? getRootFrame() : parentComponent)
                .getComponentOrientation());

        int style = styleFromMessageType(messageType);
        JDialog dialog = pane.createDialog(parentComponent, title, style);
        pane.setEnclosingDialog(dialog);

        pane.selectInitialValue();
        dialog.show();
        dialog.dispose();

        Object selectedValue = pane.getValue();

        if (selectedValue == null)
            return JOptionPane.CLOSED_OPTION;
        if (options == null)
        {
            if (selectedValue instanceof Integer)
                return ((Integer) selectedValue).intValue();
            return JOptionPane.CLOSED_OPTION;
        }
        for (int counter = 0, maxCounter = options.length;
            counter < maxCounter;
            counter++)
        {
            if (options[counter].equals(selectedValue))
                return counter;
        }
        return JOptionPane.CLOSED_OPTION;
    }


    /**
     * Dunno what this is for
     */
    private static int styleFromMessageType(int messageType)
    {
        return messageType;
        
        /*
        switch (messageType)
        {
            case JOptionPane.ERROR_MESSAGE :
                return JRootPane.ERROR_DIALOG;
            case JOptionPane.QUESTION_MESSAGE :
                return JRootPane.QUESTION_DIALOG;
            case JOptionPane.WARNING_MESSAGE :
                return JRootPane.WARNING_DIALOG;
            case JOptionPane.INFORMATION_MESSAGE :
                return JRootPane.INFORMATION_DIALOG;
            case JOptionPane.PLAIN_MESSAGE :
            default :
                return JRootPane.PLAIN_DIALOG;
        }
        */
    }

    /**
     * Creates and returns a new <code>JDialog</code> wrapping
     * <code>this</code> centered on the <code>parentComponent</code>
     * in the <code>parentComponent</code>'s frame.
     * <code>title</code> is the title of the returned dialog.
     * The returned <code>JDialog</code> will not be resizable by the
     * user, however programs can invoke <code>setResizable</code> on
     * the <code>JDialog</code> instance to change this property.
     * The returned <code>JDialog</code> will be set up such that
     * once it is closed, or the user clicks on one of the buttons,
     * the optionpane's value property will be set accordingly and
     * the dialog will be closed.  Each time the dialog is made visible,
     * it will reset the option pane's value property to 
     * <code>JOptionPane.UNINITIALIZED_VALUE</code> to ensure the
     * user's subsequent action closes the dialog properly.
     *
     * @param parentComponent determines the frame in which the dialog
     *      is displayed; if the <code>parentComponent</code> has
     *      no <code>Frame</code>, a default <code>Frame</code> is used
     * @param title     the title string for the dialog
     * @return a new <code>JDialog</code> containing this instance
     */
    public JDialog createDialog(Component parentComponent, String title)
    {
        int style = styleFromMessageType(getMessageType());
        return createDialog(parentComponent, title, style);
    }


    /**
     * Creates a dialog
     */
    private JDialog createDialog(Component parentComponent, String title,
        int style)
    {

        final JDialog dialog;

        Window window = 
            JSmartOptionPane.getWindowForComponent2(parentComponent);
            
        if (window instanceof Frame)
        {
            dialog = new JDialog((Frame) window, title, true);
        }
        else
        {
            dialog = new JDialog((Dialog) window, title, true);
        }
        Container contentPane = dialog.getContentPane();

        contentPane.setLayout(new BorderLayout());
        contentPane.add(this, BorderLayout.CENTER);

        //dialog.setResizable(false);
        /*
        if (JDialog.isDefaultLookAndFeelDecorated())
        {
            boolean supportsWindowDecorations =
                UIManager.getLookAndFeel().getSupportsWindowDecorations();
            if (supportsWindowDecorations)
            {
                dialog.setUndecorated(true);
                getRootPane().setWindowDecorationStyle(style);
            }
        }
        */

        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        dialog.addWindowListener(new WindowAdapter()
        {
            private boolean gotFocus = false;
            public void windowClosing(WindowEvent we)
            {
                setValue(null);
            }
            public void windowGainedFocus(WindowEvent we)
            {
                // Once window gets focus, set initial focus
                if (!gotFocus)
                {
                    selectInitialValue();
                    gotFocus = true;
                }
            }
        });
        dialog.addComponentListener(new ComponentAdapter()
        {
            public void componentShown(ComponentEvent ce)
            {
                // reset value to ensure closing works properly
                setValue(JOptionPane.UNINITIALIZED_VALUE);
            }
        });
        addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                // Let the defaultCloseOperation handle the closing
                // if the user closed the window without selecting a button
                // (newValue = null in that case).  Otherwise, close the dialog.
                if (dialog.isVisible() && 
                    event.getSource() == JSmartOptionPane.this && 
                    (event.getPropertyName().
                        equals(JOptionPane.VALUE_PROPERTY))    && 
                    event.getNewValue() != null                && 
                    event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE)
                {
                    dialog.setVisible(false);
                }
            }
        });
        return dialog;
    }


    /**
     * Returns the <code>Frame</code> to use for the class methods in
     * which a frame is not provided.
     *
     * @return the default <code>Frame</code> to use
     */
    public static Frame getRootFrame()
    {
        return new Frame();
    }


    /**
     * Returns the specified component's toplevel <code>Frame</code> or
     * <code>Dialog</code>.
     * 
     * @param parentComponent the <code>Component</code> to check for a 
     *      <code>Frame</code> or <code>Dialog</code>
     * @return the <code>Frame</code> or <code>Dialog</code> that
     *      contains the component, or the default
     *          frame if the component is <code>null</code>,
     *      or does not have a valid 
     *          <code>Frame</code> or <code>Dialog</code> parent
     */
    static Window getWindowForComponent2(Component parentComponent)
    {
        if (parentComponent == null)
            return getRootFrame();
            
        if (parentComponent instanceof Frame || 
            parentComponent instanceof Dialog)
            return (Window) parentComponent;
            
        return JSmartOptionPane.getWindowForComponent2(
                    parentComponent.getParent());
    }


    /**
     * Returns the icon this pane displays.
     * @return the <code>Icon</code> that is displayed
     *
     * @see #setIcon
     */
    public Icon getIcon()
    {
        return icon_;
    }


    /**
     * Sets the value the user has chosen. 
     * 
     * @param  newValue  the chosen value
     * @see    #getValue
     */
    public void setValue(Object newValue)
    {
        Object oldValue = value_;

        value_ = newValue;
        firePropertyChange(JOptionPane.VALUE_PROPERTY, oldValue, value_);
    }


    /**
     * Returns the value the user has selected. <code>UNINITIALIZED_VALUE</code>
     * implies the user has not yet made a choice, <code>null</code> means the
     * user closed the window with out choosing anything. Otherwise
     * the returned value will be one of the options defined in this
     * object.
     *
     * @return the <code>Object</code> chosen by the user,
     *         <code>UNINITIALIZED_VALUE</code>
     *         if the user has not yet made a choice, or <code>null</code> if
     *         the user closed the window without making a choice
     *
     * @see #setValue
     */
    public Object getValue()
    {
        return value_;
    }


    /**
     * Sets the options this pane displays. If an element in
     * <code>newOptions</code> is a <code>Component</code>
     * it is added directly to the pane,
     * otherwise a button is created for the element.
     *
     * @param newOptions an array of <code>Objects</code> that create the
     *                   buttons the user can click on, or arbitrary 
     *                   <code>Components</code> to add to the pane
     * @see   #getOptions
     */
    public void setOptions(Object[] newOptions)
    {
        Object[] oldOptions = options_;

        options_ = newOptions;
        firePropertyChange(JOptionPane.OPTIONS_PROPERTY, oldOptions, options_);
    }


    /**
     * Returns the choices the user can make.
     * @return the array of <code>Objects</code> that give the user's choices
     *
     * @see #setOptions
     */
    public Object[] getOptions()
    {
        if (options_ != null)
        {
            int optionCount = options_.length;
            Object[] retOptions = new Object[optionCount];

            System.arraycopy(options_, 0, retOptions, 0, optionCount);
            return retOptions;
        }
        return options_;
    }


    /**
     * Sets the initial value that is to be enabled -- the
     * <code>Component</code>
     * that has the focus when the pane is initially displayed.
     *
     * @param newInitialValue the <code>Object</code> that gets the initial 
     *                        keyboard focus
     *
     * @see   #getInitialValue
     */
    public void setInitialValue(Object newInitialValue)
    {
        Object oldIV = initialValue_;

        initialValue_ = newInitialValue;
        
        firePropertyChange(
            JOptionPane.INITIAL_VALUE_PROPERTY, oldIV, initialValue_);
    }


    /**
     * Returns the initial value.
     *
     * @return the <code>Object</code> that gets the initial keyboard focus
     *
     * @see #setInitialValue
     */
    public Object getInitialValue()
    {
        return initialValue_;
    }


    /**
     * Sets the option pane's message type.
     * The message type is used by the Look and Feel to determine the
     * icon to display (if not supplied) as well as potentially how to
     * lay out the <code>parentComponent</code>.
     * 
     * @param newType an integer specifying the kind of message to display:
     *                <code>ERROR_MESSAGE</code>, 
     *                <code>INFORMATION_MESSAGE</code>,
     *                <code>WARNING_MESSAGE</code>,
     *                <code>QUESTION_MESSAGE</code>, or 
     *                <code>PLAIN_MESSAGE</code>
     * @exception     RuntimeException if <code>newType</code> is not one of the
     *                legal values listed above
     * @see           #getMessageType
     */
    public void setMessageType(int newType) throws RuntimeException
    {
        if (newType != JOptionPane.ERROR_MESSAGE && 
            newType != JOptionPane.INFORMATION_MESSAGE && 
            newType != JOptionPane.WARNING_MESSAGE && 
            newType != JOptionPane.QUESTION_MESSAGE && 
            newType != JOptionPane.PLAIN_MESSAGE)
            throw new RuntimeException(
                "JOptionPane: type must be one of JOptionPane.ERROR_MESSAGE, "+
                "JOptionPane.INFORMATION_MESSAGE, JOptionPane.WARNING_MESSAGE,"+
                "JOptionPane.QUESTION_MESSAGE or JOptionPane.PLAIN_MESSAGE");

        int oldType = messageType_;

        messageType_ = newType;
        
        firePropertyChange(
            JOptionPane.MESSAGE_TYPE_PROPERTY, oldType, messageType_);
    }


    /**
     * Returns the message type.
     *
     * @return an integer specifying the message type
     *
     * @see #setMessageType
     */
    public int getMessageType()
    {
        return messageType_;
    }


    /**
     * Sets the options to display. 
     * The option type is used by the Look and Feel to
     * determine what buttons to show (unless options are supplied).
     * 
     * @param newType An integer specifying the options the L&F is to display:
     *                <code>DEFAULT_OPTION</code>, 
     *                <code>YES_NO_OPTION</code>,
     *                <code>YES_NO_CANCEL_OPTION</code> or 
     *                <code>OK_CANCEL_OPTION</code>
     * @exception     RuntimeException if <code>newType</code> is not one of
     *                the legal values listed above
     * @see           #getOptionType
     * @see           #setOptions
      */
    public void setOptionType(int newType)  throws RuntimeException
    {
        if (newType != JOptionPane.DEFAULT_OPTION && 
            newType != JOptionPane.YES_NO_OPTION  && 
            newType != JOptionPane.YES_NO_CANCEL_OPTION && 
            newType != JOptionPane.OK_CANCEL_OPTION)
            throw new RuntimeException(
                "JOptionPane: option type must be one of " + 
                "JOptionPane.DEFAULT_OPTION, JOptionPane.YES_NO_OPTION, " +
                "JOptionPane.YES_NO_CANCEL_OPTION or " + 
                "JOptionPane.OK_CANCEL_OPTION");

        int oldType = optionType_;

        optionType_ = newType;
        
        firePropertyChange(
            JOptionPane.OPTION_TYPE_PROPERTY, oldType, optionType_);
    }


    /**
     * Returns the type of options that are displayed.
     *
     * @return an integer specifying the user-selectable options
     *
     * @see #setOptionType
     */
    public int getOptionType()
    {
        return optionType_;
    }


    /** 
     * Sets the input selection values for a pane that provides the user
     * with a list of items to choose from. (The UI provides a widget 
     * for choosing one of the values.)  A <code>null</code> value
     * implies the user can input whatever they wish, usually by means 
     * of a <code>JTextField</code>.  
     * <p>
     * Sets <code>wantsInput</code> to true. Use
     * <code>setInitialSelectionValue</code> to specify the initially-chosen
     * value. After the pane as been enabled, <code>inputValue</code> is 
     * set to the value the user has selected.
     * 
     * @param newValues An array of <code>Objects</code> the user to be
     *                  displayed (usually in a list or combo-box) from which
     *                  the user can make a selection
     * @see   #setWantsInput
     * @see   #setInitialSelectionValue
     * @see   #getSelectionValues
     */
    public void setSelectionValues(Object[] newValues)
    {
        Object[] oldValues = selectionValues_;

        selectionValues_ = newValues;
        
        firePropertyChange(
            JOptionPane.SELECTION_VALUES_PROPERTY, oldValues, newValues);
            
        if (selectionValues_ != null)
            setWantsInput(true);
    }


    /**
     * Returns the input selection values.
     *
     * @return the array of <code>Objects</code> the user can select
     * @see #setSelectionValues
     */
    public Object[] getSelectionValues()
    {
        return selectionValues_;
    }


    /**
     * Sets the input value that is initially displayed as selected to the user.
     * Only used if <code>wantsInput</code> is true.
     * 
     * @param  newValue  The initially selected value
     * @see    #setSelectionValues
     * @see    #getInitialSelectionValue
     */
    public void setInitialSelectionValue(Object newValue)
    {
        Object oldValue = initialSelectionValue_;

        initialSelectionValue_ = newValue;
        firePropertyChange(
            JOptionPane.INITIAL_SELECTION_VALUE_PROPERTY,
            oldValue,
            newValue);
    }


    /**
     * Returns the input value that is displayed as initially selected 
     * to the user.
     *
     * @return the initially selected value
     * @see #setInitialSelectionValue
     * @see #setSelectionValues
     */
    public Object getInitialSelectionValue()
    {
        return initialSelectionValue_;
    }


    /**
     * Sets the input value that was selected or input by the user.
     * Only used if <code>wantsInput</code> is true.  Note that this method
     * is invoked internally by the option pane (in response to user action) 
     * and should generally not be called by client programs.  To set the
     * input value initially displayed as selected to the user, use
     * <code>setInitialSelectionValue</code>.
     *
     * @param  newValue  The <code>Object</code> used to set the
     *                   value that the user specified (usually in a text field)
     * @see    #setSelectionValues
     * @see    #setInitialSelectionValue
     * @see    #setWantsInput
     * @see    #getInputValue
     */
    public void setInputValue(Object newValue)
    {
        Object oldValue = inputValue_;
        inputValue_ = newValue;
        
        firePropertyChange(
            JOptionPane.INPUT_VALUE_PROPERTY, oldValue, newValue);
    }


    /**
     * Returns the value the user has input, if <code>wantsInput</code>
     * is true.
     *
     * @return the <code>Object</code> the user specified,
     *      if it was one of the objects, or a 
     *          <code>String</code> if it was a value typed into a
     *          field
     * @see #setSelectionValues
     * @see #setWantsInput
     * @see #setInputValue
     */
    public Object getInputValue()
    {
        return inputValue_;
    }


    /**
     * Sets the <code>wantsInput</code> property.
     * If <code>newValue</code> is true, an input component
     * (such as a text field or combo box) whose parent is
     * <code>parentComponent</code> is provided to
     * allow the user to input a value. If <code>getSelectionValues</code>
     * returns a non-<code>null</code> array, the input value is one of the
     * objects in that array. Otherwise the input value is whatever
     * the user inputs.
     * <p>
     * This is a bound property.
     *
     * @param  newValue  New wants input flag
     * @see #setSelectionValues
     * @see #setInputValue
     */
    public void setWantsInput(boolean newValue)
    {
        boolean oldValue = wantsInput_;

        wantsInput_ = newValue;
        firePropertyChange(
            JOptionPane.WANTS_INPUT_PROPERTY, oldValue, newValue);
    }


    /**
     * Sets the option pane's message-object.
     * 
     * @param  newMessage   The <code>Object</code> to display
     * @see    #getMessage
     */
    public void setMessage(Object newMessage) 
    {
        Object oldMessage = message_;
        message_ = newMessage;
        firePropertyChange(MESSAGE_PROPERTY, oldMessage, message_);
    }

    /**
     * Returns the message-object this pane displays.
     * @see #setMessage
     *
     * @return the <code>Object</code> that is displayed
     */
    public Object getMessage() 
    {
        return message_;
    }


    /**
     * Requests that the initial value be selected, which will set
     * focus to the initial value. This method
     * should be invoked after the window containing the option pane
     * is made visible.
     */
    public void selectInitialValue()
    {
        OptionPaneUI ui = getUI();
        if (ui != null)
        {
            ui.selectInitialValue(this);
        }
    }

    /**
     * Returns the UI object which implements the L&F for this component.
     *
     * @return the <code>OptionPaneUI</code> object
     */
    public OptionPaneUI getUI()
    {
        return (OptionPaneUI) ui;
    }


    /**
     * Notification from the <code>UIManager</code> that the L&F has changed. 
     * Replaces the current UI object with the latest version from the 
     * <code>UIManager</code>.
     *
     * @see JComponent#updateUI
     */
    public void updateUI()
    {
        setUI((OptionPaneUI) UIManager.getUI(this));
    }


    /**
     * Builds the GUI
     */
    protected void buildView()
    {
    }


    /**
     * Handles actions 
     *
     * @param  e  Actionevent
     */
    public void actionPerformed(ActionEvent e)
    {
        Object obj = e.getSource();

        if (obj == okButton_)
            getEnclosingDialog().setVisible(false);
        else if (obj == detailsButton_)
            detailsButtonClicked();
    }


    /**
     * Called when the details button is clicked
     */
    protected void detailsButtonClicked()
    {
        if (expanded_)
            hideDetail();
        else
            showDetail();

        getEnclosingDialog().pack();
        SwingUtil.centerWindow(getEnclosingDialog());
    }


    /**
     * Switches dialog box mode to show details
     */
    protected void showDetail()
    {
        if (detailArea_ == null)
        {
            detailArea_ = new JSmartTextArea();
            detailArea_.setFont(SwingUtil.getPreferredMonoFont());
            detailScroller_ = new JScrollPane(detailArea_);

            Border topFiller = new EmptyBorder(10, 0, 0, 0);
            Border existing = detailScroller_.getBorder();

            if (existing != null)
                existing = new CompoundBorder(topFiller, existing);

            detailScroller_.setBorder(existing);
        }

        detailArea_.setText(details_.toString());
        add(detailScroller_, BorderLayout.SOUTH);
        expanded_ = true;
        detailsButton_.setText(BUTTON_EXPANDED);
    }


    /**
     * Switches dialog box mode to hide the details
     */
    protected void hideDetail()
    {
        remove(detailScroller_);
        expanded_ = false;
        detailsButton_.setText(BUTTON_COLLAPSED);
    }


    /**
     * Wraps a component in a flow layout
     * 
     * @param   c  Component to wrap
     * @return  Jpanel
     */
    public static JPanel wrapInFlowLayout(Component c)
    {
        JPanel p = new JPanel(new FlowLayout());
        p.add(c);
        return p;
    }


    /**
     * @return  Array of buttons for dialog box
     */
    protected JButton[] getButtons()
    {
        if (buttons_ == null)
        {
            okButton_ = new JButton("OK");
            okButton_.addActionListener(this);

            detailsButton_ = new JButton(BUTTON_COLLAPSED);
            detailsButton_.addActionListener(this);

            buttons_ = new JButton[2];
            buttons_[0] = okButton_;
            buttons_[1] = detailsButton_;
        }

        return buttons_;
    }


    /**
     * @param  parent  Parent window
     */
    public void setEnclosingDialog(Window parent)
    {
        parent_ = parent;
    }


    /**
     * @return  Enclosing window
     */
    public Window getEnclosingDialog()
    {
        return parent_;
    }


    /**
     * Wraps a component with filler as a border
     * 
     * @param   c  Component to add filler to
     * @return  Component with filler added
     */
    public static JComponent wrapWithFiller(JComponent c)
    {
        Border b = new EmptyBorder(5, 5, 5, 5);
        Border old = c.getBorder();

        if (old != null)
            b = new CompoundBorder(b, old);

        c.setBorder(b);

        return c;
    }
}