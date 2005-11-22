package toolbox.dbconsole;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.TextAction;

/**
 * Implements a console with interactive input/output (in collaboration with
 * ConsoleIfc). The stream I/O of ConsoleIfc should probably be moved here.
 */
public class JConsoleArea extends JTextArea {

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Keep tracks of first character entered since the last time a return sent
     * the data to the interpreter. The new data from this point will be sent 
     * on the next return. -1 means no data typed.
     */ 
    private int firstInputLocation = -1; 

    /**
     * Keep track of last output location, to circumvent a bug in jdk1.2
     */
    private int lastOutputLocation = -1;

    /**
     * Keymap for ^C,^V,^X, return.
     */
    private static Keymap keys;
    
    /**
     * The calling console
     */ 
    protected ConsoleIfc mainConsole; 

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Create a new console with the specified number of rows and columns.
     * 
     * @param mainComsole The calling console program to send back data to.
     * @param rows Number of displayable rows.
     * @param columns Number of displayable columns.
     */
    JConsoleArea(ConsoleIfc mainConsole, int rows, int columns) {
        
        super(rows, columns);
        this.mainConsole = mainConsole;
        setFont(new Font("Lucida Console", Font.PLAIN, 12));
        
        if (keys == null) {
            
            Keymap defaultKeymap =
                JTextComponent.getKeymap(JTextComponent.DEFAULT_KEYMAP);
                
            if (defaultKeymap == null) {
                throw new RuntimeException("Could not find default keymap");
            }
            
            keys = JTextComponent.addKeymap("JConsoleAreaKeyMap", defaultKeymap);
            keys.setDefaultAction(new DefaultKeyTypedAction(this));

            keys.addActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                new ReturnKeyTypedAction(this));
                
            keys.addActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK),
                new CopyAction());
                
            keys.addActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK),
                new CutAction());
                
            keys.addActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK),
                new PasteAction(this));
        }

        setKeymap(keys);
    }


    /**
     * registerShortcut
     * 
     * @param keyStroke
     * @param action
     */
    public void registerShortcut(KeyStroke keyStroke, Action action) {
        Keymap keys = JTextComponent.getKeymap("JConsoleAreaKeyMap");
        keys.addActionForKeyStroke(keyStroke, action);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Keep track of first input location (only!)
     * 
     * @param loc Current input location.
     */
    protected void setFirstInputLocation(int loc) {
        if (firstInputLocation == -1)
            firstInputLocation = loc;
    }


    /**
     * Reset the first input location to none.
     */
    protected void resetFirstInputLocation() {
        firstInputLocation = -1;
    }


    /**
     * Returns the first input location.
     * 
     * @return location or -1 if none defined.
     */
    protected int getFirstInputLocation() {
        if (firstInputLocation == -1 && lastOutputLocation != -1) {
            // May be first input location si not set as JDK 1.2
            // do not pass the default event
            return lastOutputLocation;
        }
        return firstInputLocation;
    }


    /**
     * Move caret to the end.
     */
    protected void atEnd() {
        Document doc = getDocument();
        int dot = doc.getLength();
        setCaretPosition(dot);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Override the standard PASTE command with a PASTE AT END.
     */
    public void paste() {

        // Move at end
        Document doc = getDocument();
        int dot = doc.getLength();
        setCaretPosition(dot);

        // Save location if needed
        setFirstInputLocation(dot);
        
        // Paste all
        super.paste();
    }


    /**
     * Appends to the end of the console.
     * 
     * @param str String to append.
     */
    public void append(String str) {
        super.append(str);
        Document doc = getDocument();
        lastOutputLocation = doc.getLength();
        setCaretPosition(lastOutputLocation);
    }

    //--------------------------------------------------------------------------
    // Overrides java.awt.Component
    //--------------------------------------------------------------------------
    
    /**
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g)
    {
        ((Graphics2D) g).setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
        super.paint(g);
    }

    //--------------------------------------------------------------------------
    // DefaultKeyTypedAction
    //--------------------------------------------------------------------------
    
    /**
     * The action that is executed by default if a <em>key typed event</em> is
     * received and there is no keymap entry. There is a variation across
     * different VM's in what gets sent as a <em>key typed</em> event, and
     * this action tries to filter out the undesired events. This filters the
     * control characters and those with the ALT modifier.
     * <p>
     * The character is added at the end of the buffer and the first input
     * location is updated if needed.
     * <p>
     * If the event doesn't get filtered, it will try to insert content into the
     * text editor. The content is fetched from the command string of the
     * ActionEvent. The text entry is done through the
     * <code>replaceSelection</code> method on the target text component. This
     * is the action that will be fired for most text entry tasks.
     * <p>
     * Warning: serialized objects of this class will not be compatible with
     * future swing releases. The current serialization support is appropriate
     * for short term storage or RMI between Swing1.0 applications. It will not
     * be possible to load serialized Swing1.0 objects with future releases of
     * Swing. The JDK1.2 release of Swing will be the compatibility baseline for
     * the serialized form of Swing objects.
     * 
     * @see DefaultEditorKit#defaultKeyTypedAction
     * @see DefaultEditorKit#getActions
     * @see Keymap#setDefaultAction
     * @see Keymap#getDefaultAction
     */
    public static class DefaultKeyTypedAction extends TextAction {

        private JConsoleArea console;

        /**
         * Creates this object with the appropriate identifier.
         */
        public DefaultKeyTypedAction(JConsoleArea console) {
            super(DefaultEditorKit.defaultKeyTypedAction);
            this.console = console;
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            
            JTextComponent target = getTextComponent(e);

            if ((target != null) && (e != null)) {

                String content = e.getActionCommand();
                int mod = e.getModifiers();
                
                if ((content != null)
                    && (content.length() > 0)
                    && ((mod & ActionEvent.ALT_MASK) == 0)) {
                        
                    char c = content.charAt(0);
                    // if ((c >= 0x20) && (c != 0x7F)) { Old
                    
                    if (!Character.isISOControl(c)) {
                        
                        // Printable character - move at end
                        Document doc = target.getDocument();
                        int dot = doc.getLength();
                        target.setCaretPosition(dot);
                        
                        // Save location of first character typed if needed
                        console.setFirstInputLocation(dot);
                        
                        // Insert content
                        target.replaceSelection(content);
                    }
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    // ReturnKeyTypedAction
    //--------------------------------------------------------------------------
    
    /**
     * Action for RETURN - send text to console.
     * <P>
     * The text from the first input location to the end (where the RETURN was
     * typed) is send to the caller. The first input location is reset to none.
     */
    public static class ReturnKeyTypedAction extends TextAction {

        private JConsoleArea console;

        /**
         * Creates this object with the appropriate identifier.
         */
        public ReturnKeyTypedAction(JConsoleArea console) {
            super("return");
            this.console = console;
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);

            if ((target != null) && (e != null)) {

                // Set caret at end
                Document doc = target.getDocument();
                int dot = doc.getLength();
                target.setCaretPosition(dot);

                // Insert return
                target.replaceSelection("\n");

                // Get data
                String inputText = null;
                int start = console.getFirstInputLocation();
                int length = dot - start + 1;
                // System.err.println("\nRET star="+start+", l="+length); //
                // *************
                
                if (start != -1 && length > 0) {
                    try {
                        inputText = doc.getText(start, length);
                    }
                    catch (BadLocationException ex) {
                        throw new RuntimeException(
                            "Unexpected exception: " + ex.toString());
                    }

                    console.mainConsole.send(inputText);

                    // Mark data sent
                    console.resetFirstInputLocation();
                }

            }
        }
    }

    //--------------------------------------------------------------------------
    // CutAction
    //--------------------------------------------------------------------------
    
    /**
     * Cuts the selected region and place its contents into the system
     * clipboard.
     * <p>
     * Warning: serialized objects of this class will not be compatible with
     * future swing releases. The current serialization support is appropriate
     * for short term storage or RMI between Swing1.0 applications. It will not
     * be possible to load serialized Swing1.0 objects with future releases of
     * Swing. The JDK1.2 release of Swing will be the compatibility baseline for
     * the serialized form of Swing objects.
     * 
     * @see DefaultEditorKit#cutAction
     * @see DefaultEditorKit#getActions
     */
    public static class CutAction extends TextAction {

        public CutAction() {
            super(DefaultEditorKit.cutAction);
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) { 
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                target.cut();
            }
        }
    }

    //--------------------------------------------------------------------------
    // CopyAction
    //--------------------------------------------------------------------------
    
    /**
     * Copies the selected region and place its contents into the system
     * clipboard.
     * <p>
     * Warning: serialized objects of this class will not be compatible with
     * future swing releases. The current serialization support is appropriate
     * for short term storage or RMI between Swing1.0 applications. It will not
     * be possible to load serialized Swing1.0 objects with future releases of
     * Swing. The JDK1.2 release of Swing will be the compatibility baseline for
     * the serialized form of Swing objects.
     * 
     * @see DefaultEditorKit#copyAction
     * @see DefaultEditorKit#getActions
     */
    public static class CopyAction extends TextAction {

        public CopyAction() {
            super(DefaultEditorKit.copyAction);
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                target.copy();
            }
        }
    }

    //--------------------------------------------------------------------------
    // PasteAction
    //--------------------------------------------------------------------------
    
    /**
     * Pastes the contents of the system clipboard at the end of the text area.
     * Mark the first input location if needed.
     * <p>
     * Warning: serialized objects of this class will not be compatible with
     * future swing releases. The current serialization support is appropriate
     * for short term storage or RMI between Swing1.0 applications. It will not
     * be possible to load serialized Swing1.0 objects with future releases of
     * Swing. The JDK1.2 release of Swing will be the compatibility baseline for
     * the serialized form of Swing objects.
     * 
     * @see DefaultEditorKit#pasteAction
     * @see DefaultEditorKit#getActions
     */
    public static class PasteAction extends TextAction {

        private JConsoleArea console;

        public PasteAction(JConsoleArea console) {
            super(DefaultEditorKit.pasteAction);
            this.console = console;
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                target.paste();
            }
        }
    }
}