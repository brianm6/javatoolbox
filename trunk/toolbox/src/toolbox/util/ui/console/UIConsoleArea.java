package toolbox.util.ui.console;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.TextAction;

import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.console.action.DefaultKeyTypedAction;

/**
 * UIConsoleArea exhibit basic behavior necessary for a simple text based 
 * console.
 * 
 * @see toolbox.util.ui.console.Console
 */
public class UIConsoleArea extends JSmartTextArea 
{
    //--------------------------------------------------------------------------
    // Constrants
    //--------------------------------------------------------------------------

    /**
     * Name of the default keymap.
     */
    private static final String DEFAULT_KEYMAP = "JConsoleAreaKeyMap";
        
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Keep tracks of first character entered since the last time a return sent
     * the data to the interpreter. The new data from this point will be sent 
     * on the next return. -1 means no data typed.
     */ 
    private int firstInputLocation_ = -1; 

    /**
     * Keep track of last output location, to circumvent a bug in jdk1.2
     */
    private int lastOutputLocation_ = -1;

    /**
     * Keymap for ^C,^V,^X, return.
     */
    private static Keymap keys_;
    
    /**
     * The calling console
     */ 
    protected Console mainConsole_; 

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
    public UIConsoleArea(Console mainConsole, int rows, int columns)
    {
        super(rows, columns);
        mainConsole_ = mainConsole;
        
        //setFont(new Font("Lucida Console", Font.PLAIN, 12));

        if (keys_ == null)
        {
            Keymap defaultKeymap = 
                JTextComponent.getKeymap(JTextComponent.DEFAULT_KEYMAP);

            if (defaultKeymap == null)
            {
                throw new RuntimeException("Could not find default keymap");
            }

            keys_ = 
                JTextComponent.addKeymap(DEFAULT_KEYMAP, defaultKeymap);
            
            keys_.setDefaultAction(new DefaultKeyTypedAction(this));

            keys_.addActionForKeyStroke(KeyStroke.getKeyStroke(
                KeyEvent.VK_ENTER, 0), new ReturnKeyTypedAction(this));

            keys_.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                InputEvent.CTRL_MASK), new CopyAction());

            keys_.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                InputEvent.CTRL_MASK), new CutAction());

            keys_.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                InputEvent.CTRL_MASK), new PasteAction(this));
        }

        setKeymap(keys_);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Reset the first input location to none.
     */
    protected void resetFirstInputLocation()
    {
        firstInputLocation_ = -1;
    }


    /**
     * Returns the first input location.
     * 
     * @return location or -1 if none defined.
     */
    protected int getFirstInputLocation()
    {
        if (firstInputLocation_ == -1 && lastOutputLocation_ != -1)
        {
            // May be first input location si not set as JDK 1.2
            // do not pass the default event
            return lastOutputLocation_;
        }
        return firstInputLocation_;
    }


    /**
     * Move caret to the end.
     */
    protected void atEnd()
    {
        Document doc = getDocument();
        int dot = doc.getLength();
        setCaretPosition(dot);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Keep track of first input location (only!)
     * 
     * @param loc Current input location.
     */
    public void setFirstInputLocation(int loc)
    {
        if (firstInputLocation_ == -1)
            firstInputLocation_ = loc;
    }


    /**
     * Override the standard PASTE command with a PASTE AT END.
     */
    public void paste()
    {
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
    public void append(String str)
    {
        super.append(str);
        Document doc = getDocument();
        lastOutputLocation_ = doc.getLength();
        setCaretPosition(lastOutputLocation_);
    }

    
    /**
     * Binds a keystroke to an action.
     * 
     * @param keyStroke Keystroke to bind to an action.
     * @param action Action to bind a keystroke.
     */
    public void registerShortcut(KeyStroke keyStroke, Action action)
    {
        Keymap keys = JTextComponent.getKeymap(DEFAULT_KEYMAP);
        keys.addActionForKeyStroke(keyStroke, action);
    }

    //--------------------------------------------------------------------------
    // DefaultKeyTypedAction
    //--------------------------------------------------------------------------
    
    /**
     * Action for RETURN - send text to console.
     * <P>
     * The text from the first input location to the end (where the RETURN was
     * typed) is send to the caller. The first input location is reset to none.
     */
    public static class ReturnKeyTypedAction extends TextAction
    {
        /**
         * Console text area.
         */
        private UIConsoleArea console_;

        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------
        
        /**
         * Creates this object with the appropriate identifier.
         * 
         * @param console UI console text area.
         */
        public ReturnKeyTypedAction(UIConsoleArea console)
        {
            super("return");
            console_ = console;
        }

        //----------------------------------------------------------------------
        // ActionListener Interface
        //----------------------------------------------------------------------
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            JTextComponent target = getTextComponent(e);

            if ((target != null) && (e != null))
            {
                // Set caret at end
                Document doc = target.getDocument();
                int dot = doc.getLength();
                target.setCaretPosition(dot);

                // Insert return
                target.replaceSelection("\n");

                // Get data
                String inputText = null;
                int start = console_.getFirstInputLocation();
                int length = dot - start + 1;
                
                // System.err.println("\nRET star="+start+", l="+length); //
                // *************

                if (start != -1 && length > 0)
                {
                    try
                    {
                        inputText = doc.getText(start, length);
                    }
                    catch (BadLocationException ex)
                    {
                        throw new RuntimeException("Unexpected exception: "
                            + ex.toString());
                    }

                    console_.mainConsole_.send(inputText);

                    // Mark data sent
                    console_.resetFirstInputLocation();
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

        private UIConsoleArea console;

        public PasteAction(UIConsoleArea console) {
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

//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2 of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA