package toolbox.util.ui.console;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.TextAction;

import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.console.action.DefaultKeyTypedAction;
import toolbox.util.ui.console.action.ReturnKeyTypedAction;

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
    protected Console console_; 

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Create a new console with the specified number of rows and columns.
     * 
     * @param console The calling console program to send back data to.
     * @param rows Number of displayable rows.
     * @param columns Number of displayable columns.
     */
    public UIConsoleArea(UIConsole console, int rows, int columns)
    {
        super(rows, columns);
        console_ = console;
        
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
                KeyEvent.VK_ENTER, 0), new ReturnKeyTypedAction(console));

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
     * Reset the first input location to none.
     */
    public void resetFirstInputLocation()
    {
        firstInputLocation_ = -1;
    }


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
     * Returns the first input location.
     * 
     * @return location or -1 if none defined.
     */
    public int getFirstInputLocation()
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
     * Cuts the selected region and place its contents into the system
     * clipboard.
     * 
     * @see DefaultEditorKit#cutAction
     * @see DefaultEditorKit#getActions
     */
    public static class CutAction extends TextAction
    {
        public CutAction()
        {
            super(DefaultEditorKit.cutAction);
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            JTextComponent target = getTextComponent(e);
            if (target != null)
            {
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
     * 
     * @see DefaultEditorKit#copyAction
     * @see DefaultEditorKit#getActions
     */
    public static class CopyAction extends TextAction
    {
        public CopyAction()
        {
            super(DefaultEditorKit.copyAction);
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            JTextComponent target = getTextComponent(e);
            if (target != null)
            {
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
     * 
     * @see DefaultEditorKit#pasteAction
     * @see DefaultEditorKit#getActions
     */
    public static class PasteAction extends TextAction
    {
        private UIConsoleArea console;

        public PasteAction(UIConsoleArea console)
        {
            super(DefaultEditorKit.pasteAction);
            this.console = console;
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            JTextComponent target = getTextComponent(e);
            if (target != null)
            {
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