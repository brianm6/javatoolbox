package toolbox.util.ui.console;

import java.awt.BorderLayout;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.TextAction;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import toolbox.util.io.JTextAreaOutputStream;
import toolbox.util.io.MulticastOutputStream;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceState;
import toolbox.util.ui.JSmartTextArea;

/**
 * UIConsoleArea exhibit basic behavior necessary for a simple text based 
 * console.
 * 
 * @see toolbox.util.ui.console.Console
 */
public class SwingConsole extends JComponent implements Console 
{
    private static final Logger logger_ = Logger.getLogger(SwingConsole.class);
    
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
     * The calling console
     */ 
    protected Console delegate_; 

    /**
     * Composite textarea.
     */
    protected JSmartTextArea textArea_;
    
    
    /**
     *  The length of the static text displayed, excludes the prompt and the
     *   current input line.
     */
    private int historyLength = 0;
    
    /**
     *  Length of the prompt.
     **/
    private int promptLength = 0;
    
    /**
     *  Length of the current input line.
     **/
    private int lineLength = 0;
    
    /**
     *  Location of the insertion point within the current input line
     **/
    private int insertionPoint = 0;
    
    /**
     *  Lines of console input awaiting shell processing.
     **/
    private List lines = new ArrayList();
    
    
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
    public SwingConsole(String name)
    {
        this(name, 25, 80);
    }

    
    /**
     * Create a new console with the specified number of rows and columns.
     * 
     * @param console The calling console program to send back data to.
     * @param rows Number of displayable rows.
     * @param columns Number of displayable columns.
     */
    public SwingConsole(String name, int rows, int columns)
    {
        buildView(rows, columns);
        
        new Thread(new Runnable()
        {
            public void run()
            {
                while (true)
                {
                    synchronized(lines)
                    {
                        if (lines.isEmpty())
                        {
                            try
                            {
                                logger_.debug("Waiting for command...");
                                lines.wait();
                            }
                            catch (InterruptedException e)
                            {
                                logger_.error(e);
                            }
                        }
                        
                        String line = lines.remove(0).toString().trim();
                        logger_.debug("XXRead command: [" + line + "]");
                        
                        try
                        {
                            TextAreaInputStream tais = 
                                (TextAreaInputStream) getInputStream();
                            
                            tais.stuff(line + "\n");
                                
                            //send("You entered commmand: " + line);
                        }
                        catch (NullPointerException e)
                        {
                            logger_.error(e);
                        }
                        
                        ((AbstractConsole) delegate_).handleCommand(line);
                    }
                }
            }
        }).start();
        
    }

    class TrackerOutputstream extends OutputStream
    {
        /**
         * @see java.io.OutputStream#write(int)
         */
        public void write(int b) throws IOException
        {
            char ch = (char) b;
            
            switch (ch)
            {
                case '\n' :
                    historyLength += promptLength + lineLength + 1;
                    lineLength = 0;
                    insertionPoint = promptLength;
                    break;
                    
                default :
                    lineLength++;
                    insertionPoint++;
                    break;
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     */
    protected void buildView(int rows, int columns)
    {
        setLayout(new BorderLayout());
        textArea_ = new JSmartTextArea(rows, columns);
        add(new JScrollPane(textArea_), BorderLayout.CENTER);
        
        // Create the stream for I/O in the consoles
        //InputStream is = new LineInputStream(new TextAreaInputStream());
        InputStream is = new TextAreaInputStream();
        
        MulticastOutputStream os = new MulticastOutputStream();
        os.addStream(new JTextAreaOutputStream(textArea_));
        os.addStream(new TrackerOutputstream());
        
        delegate_ = new MyConsole(is, os);
        textArea_.addKeyListener(new KeyHandler());
    }
    
    
    /**
     * Move caret to the end.
     */
    protected void atEnd()
    {
        Document doc = textArea_.getDocument();
        int dot = doc.getLength();
        textArea_.setCaretPosition(dot);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
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
        private SwingConsole console;

        public PasteAction(SwingConsole console)
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
    
    /**
     * @see toolbox.util.ui.console.Console#clear()
     */
    public void clear()
    {
        textArea_.setText("");
        historyLength = 0;
        lineLength = 0;
        insertionPoint = 0;
        promptLength = 0;
    }
    
    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy() throws IllegalStateException, ServiceException
    {
        delegate_.destroy();
    }
    
    
    /**
     * @see toolbox.util.ui.console.Console#getInputStream()
     */
    public InputStream getInputStream()
    {
        return delegate_.getInputStream();
    }
    
    /**
     * @see toolbox.util.service.Nameable#getName()
     */
    public String getName()
    {
        return delegate_.getName();
    }
    
    /**
     * @see toolbox.util.ui.console.Console#getOutputStream()
     */
    public OutputStream getOutputStream()
    {
        return delegate_.getOutputStream();
    }
    
    /**
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState()
    {
        return delegate_.getState();
    }
    
    /**
     * @see toolbox.util.ui.console.Console#send(java.lang.String)
     */
    public void send(String text) throws IOException
    {
        delegate_.send(text);
    }
    
    /**
     * @see toolbox.util.service.Nameable#setName(java.lang.String)
     */
    public void setName(String name)
    {
        delegate_.setName(name);
    }
    
    /**
     * @see toolbox.util.ui.console.Console#getPrompt()
     */
    public String getPrompt()
    {
        return delegate_.getPrompt();
    }
    
    //--------------------------------------------------------------------------
    // MyConsole
    //--------------------------------------------------------------------------
    
    class MyConsole extends AbstractConsole 
    {
        public MyConsole(InputStream is, OutputStream os)
        {
            super("MyConsole", is, os);
        }
        
        /**
         * @see toolbox.util.ui.console.Console#clear()
         */
        public void clear()
        {
        }
        
        /**
         * @see toolbox.util.service.Destroyable#destroy()
         */
        public void destroy() throws IllegalStateException, ServiceException
        {
        }
        
        /**
         * @see toolbox.util.ui.console.AbstractConsole#getPrompt()
         */
        public String getPrompt()
        {
            return "COMMAND>";
        }
        
        /**
         * @see toolbox.util.service.Service#getState()
         */
        public ServiceState getState()
        {
            return null;
        }
        
        /**
         * @see toolbox.util.ui.console.Console#send(java.lang.String)
         */
        public void send(String text) throws IOException
        {
            getOutputStream().write(text.getBytes());
            getOutputStream().flush();
        }
    }
    
    //--------------------------------------------------------------------------
    // KeyHandler
    //--------------------------------------------------------------------------
    
    /**
     * Handle key actions
     */
    class KeyHandler extends KeyAdapter 
    {
        /**
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        public synchronized void keyPressed(KeyEvent e) 
        {
            int val = e.getKeyCode();
            char ch = e.getKeyChar();
            int mod = e.getModifiers();
            
            boolean consumed = false;
            
            // There may be user confusion about Ctrl-C: since this is a shell,
            // users might expect Ctrl-C to terminate the currently running
            // command. At this writing, we don't have command termination, so
            // we'll go ahead and grab Ctrl-C for copy.  (Suggest "ESC" for
            // termination...)
            
            try {
                if( (mod & InputEvent.CTRL_MASK) != 0 ) 
                {
                    consumed = control(val, ch, mod);
                } 
                else 
                {
                    if( KeyEvent.CHAR_UNDEFINED == ch ) 
                    {
                        consumed = handling(val, ch, mod);
                    } 
                    else 
                    {
                        consumed = typing(val, ch, mod);
                    }
                }
                
                if( consumed ) 
                {
                    textArea_.setCaretPosition(
                        historyLength + promptLength + insertionPoint);
                    
                    textArea_.getCaret().setVisible(true);
                    
                    // consume the event so that it doesn't get processed by 
                    // the TextArea control.
                    e.consume();
                }
            } 
            catch( Throwable ohno ) 
            {
                logger_.error( "Failure : TextLength=" + historyLength +
                " promptLength=" + promptLength +
                " lineLength=" + lineLength +
                " text=" + textArea_.getText().length(), ohno );
            }
        }
    }
    
    
    /**
     *  Handles non-character editing of the command line. Handling is as follows:
     *
     *  <p/><ul>
     *    <li> Ctrl-C - copys the current selection to the Clipboard.</li>
     *    <li> Ctrl-V - Inserts text from the ClipBoard into the current line.</li>
     *    <li> Ctrl-D - Sends an EOT command.</li>
     *    <li> Ctrl-L - Clear the text area.</li>
     *    <li> Ctrl-U - Clear the command line</li>
     *    <li> Ctrl-bksp - Clear the command line</li>
     *  </ul>
     *
     * <p/>There may be user confusion about Ctrl-C: since this is a shell, users
     * might expect Ctrl-C to terminate the currently running command.
     * At this writing, we don't have command termination, so we'll go ahead
     * and grab Ctrl-C for copy.  (Suggest Esc for termination...)
     *
     *@param  val        the KeyCode value of the key pressed
     *@param  ch         the character associated with the key pressed
     *@param  modifiers  any modifiers that might have been pressed
     **/
    private boolean control(int val, char ch, int modifiers) 
    {
        switch (val) 
        {
            case KeyEvent.VK_C :
                if (logger_.isEnabledFor(Level.INFO)) {
                    logger_.info("--> COPY <--");
                }
                copy();
                return true;
                
            case KeyEvent.VK_V:
                if (logger_.isEnabledFor(Level.INFO)) {
                    logger_.info("--> PASTE <--");
                }
                paste();
                return true;
                
            // Let's try a ^D quit...
            case KeyEvent.VK_D :
                if (logger_.isEnabledFor(Level.INFO)) {
                    logger_.info("--> QUIT <--");
                }
                setCommandLine( "\u0004" );
                submit(true);
                setCommandLine( "" );
                return true;
                
            case KeyEvent.VK_L :
                if (logger_.isEnabledFor(Level.INFO)) {
                    logger_.info("--> CLEAR <--");
                }
                setCommandLine( "clear" );
                submit(true);
                return true;
                
            case  KeyEvent.VK_U :
            case  KeyEvent.VK_BACK_SPACE :
                setCommandLine( "" );
                return true;
                
            default :
                return false;
        }
    }
    
    /**
     *  Handles non-character editing of the command line. Handling is as follows:
     *
     *  <p/><ul>
     *    <li> Cursor keys left and right - move the caret and update the
     *    current insertLine value
     *    <li> <Home> - Move cursor to the begining of the current line.
     *    <li> <End> - Move cursor to the end of the current line.
     *  </ul>
     *
     *@param  val        the KeyCode value of the key pressed
     *@param  ch         the character associated with the key pressed
     *@param  modifiers  any modifiers that might have been pressed
     **/
    private boolean handling(int val, char ch, int modifiers) 
    {
        switch (val) 
        {
            case KeyEvent.VK_HOME :
                insertionPoint = 0;
                return true;
                
            case KeyEvent.VK_END :
                insertionPoint = lineLength;
                return true;
                
            case KeyEvent.VK_KP_LEFT :
            case KeyEvent.VK_LEFT :
                insertionPoint--;
            
                if (insertionPoint < 0) 
                {
                    insertionPoint = 0;
                }
                return true;
                
            case KeyEvent.VK_KP_RIGHT :
            case KeyEvent.VK_RIGHT :
                insertionPoint++;
                if (insertionPoint > lineLength) 
                {
                    insertionPoint = lineLength;
                }
                return true;
                
            case KeyEvent.VK_KP_UP:
            case KeyEvent.VK_UP:
                setCommandLine( getCursorUpName() );
                submit(false);
                return true;
                
            case KeyEvent.VK_KP_DOWN :
            case KeyEvent.VK_DOWN :
                setCommandLine(getCursorDownName());
                submit(false);
                return true;
                
            default :
                return false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCursorDownName()
    {
        return KeyEvent.getKeyText(KeyEvent.VK_DOWN);
    }


    /**
     * {@inheritDoc}
     */
    public String getCursorUpName()
    {
        return KeyEvent.getKeyText(KeyEvent.VK_UP);
    }
    
    /**
     *  Handles the editing of the command line. Handling is as follows:
     *
     *  <p/><ul>
     *    <li> backspace - Delete the character ahead of lineInsert from input line.</li>
     *    <li> delete - Delete the character after lineInsert from input line.</li>
     *    <li>enter - Finish the input line by calling <code>submit()</code>.</li>
     *    <li>otherwise insert the character.</li>
     *  </ul>
     *
     *@param  val        the KeyCode value of the key pressed
     *@param  ch         the character associated with the key pressed
     *@param  modifiers  any modifiers that might have been pressed
     **/
    private boolean typing(int val, char ch, int modifiers) 
    {
        switch (ch) 
        {
            case KeyEvent.VK_BACK_SPACE :
                if (insertionPoint >= 1 && insertionPoint <= lineLength) {
                    textArea_.replaceRange( "", historyLength + promptLength + insertionPoint - 1, historyLength + promptLength + insertionPoint);
                    insertionPoint--;
                    lineLength--;
                }
                return true;
                
            case KeyEvent.VK_DELETE :
                if (insertionPoint < lineLength) 
                {
                    textArea_.replaceRange( "", historyLength + promptLength + insertionPoint, historyLength + promptLength + insertionPoint + 1);
                    lineLength--;
                }
                return true;
                
            case KeyEvent.VK_ENTER :
                submit(true);
                return true;
                
            default :
                textArea_.insert( Character.toString( ch ), historyLength + promptLength + insertionPoint++ );
                lineLength++;
                return true;
        }
    }
    
    /**
     * Copy the selection to the system clipboard.
     **/
    private void copy() {
        
        String selection = textArea_.getSelectedText();
        
        if ( (null != selection) && (selection.length() > 0) ) {
            StringSelection select = new StringSelection(selection);
            Clipboard clip = textArea_.getToolkit().getSystemClipboard();
            clip.setContents(select, select);
        }
    }
    
    /**
     * Paste text from the clipboard into the shell. Text is added to at the end
     * of the current command line. If the clipboard contents is non-text, we'll
     * bail out silently.
     */
    private void paste()
    {

        Clipboard cb = textArea_.getToolkit().getSystemClipboard();
        Transferable trans = cb.getContents(this);
        if (trans == null)
        {
            return;
        }

        String cbText = null;
        try
        {
            cbText = (String) trans.getTransferData(DataFlavor.stringFlavor);
        }
        catch (UnsupportedFlavorException e)
        {
            return;
        }
        catch (IOException e)
        {
            return;
        }

        if (cbText == null)
        {
            return;
        }

        // Add the clipboard text to the end of the current command line.
        // If there are multiple lines in the clipboard, we paste and
        // execute each line as if the user entered it and and hit return.
        int current = 0;
        boolean fullLine = true;
        do
        {
            int lineEnd = cbText.indexOf('\n', current);

            if (-1 == lineEnd)
            {
                lineEnd = cbText.length();
                fullLine = false;
            }

            // Append text to the current line.
            String aLine = cbText.substring(current, lineEnd);
            textArea_.insert(aLine, historyLength + promptLength + insertionPoint);
            insertionPoint += aLine.length();
            lineLength += aLine.length();

            if (fullLine)
            {
                submit(true);
            }
            current = lineEnd + 1;
        }
        while (current < cbText.length());
    }
    
    /**
     * Finishes an input line and provides it as input to the console reader.
     * 
     * @param appendNewLine Clear the line and append a newline
     */
    private void submit(boolean appendNewLine)
    {
        synchronized (lines)
        {
            try
            {
                lines.add(textArea_.getText(historyLength + promptLength,
                    lineLength)
                    + "\n");
            }
            catch (BadLocationException ble)
            {
                IllegalArgumentException badLoc = new IllegalArgumentException(
                    "bad location");
                badLoc.initCause(ble);
                throw badLoc;
            }

            if (appendNewLine)
            {
                textArea_.append("\n");
                historyLength += promptLength + lineLength + 1;
                promptLength = 0;
                lineLength = 0;
                insertionPoint = 0;
            }

            lines.notify();
        }
    }

    
    public synchronized void setCommandLine(String cmd)
    {
        try
        {
            textArea_.replaceRange(cmd, historyLength + promptLength, historyLength
                + promptLength + lineLength);
            lineLength = cmd.length();
            insertionPoint = lineLength;
            textArea_.setCaretPosition(historyLength + promptLength + insertionPoint);
            textArea_.getCaret().setVisible(true);
        }
        catch (Throwable ohno)
        {
            logger_.error("Failure : TextLength=" + historyLength + " promptLength="
                + promptLength + " lineLength=" + lineLength + " text="
                + textArea_.getText().length(), ohno);
        }
    }
    
}