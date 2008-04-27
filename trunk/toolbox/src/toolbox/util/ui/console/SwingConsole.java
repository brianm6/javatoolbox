package toolbox.util.ui.console;

import java.awt.BorderLayout;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import toolbox.util.FontUtil;

/**
 * A Swing based console.
 */
public class SwingConsole extends AbstractConsole
{
    private static final Logger logger_ = Logger.getLogger(SwingConsole.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Lines of console input (Strings) awaiting processing.
     */
    private List lines_;

    /**
     * Wraps the textarea and is exported via getView().
     */
    private JPanel view_;
    
    /**
     * Textarea that is the guts of the console.
     */
    private JTextArea text_;
 
    /**
     * Length of the displayed text excluding the prompt and the current input
     * line.
     */
    private int textLength_;

    /**
     * Length of the command prompt.
     */
    private int promptLength_;

    /**
     * Length of the current input line.
     */
    private int lineLength_;

    /**
     * Location of the insertion point within the current input line.
     */
    private int insertionPoint_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SwingConsole with the given name and dimensions. Use getView()
     * after instantiation to obtain a reference to the JComponent handle of
     * the console.
     * 
     * @param name Name of the console.
     * @param rows Number of rows to display.
     * @param cols Number of columns to display.
     */
    public SwingConsole(String name, int rows, int cols)
    {
        super(name);
        
        textLength_     = 0;
        promptLength_   = 0;
        lineLength_     = 0;
        insertionPoint_ = 0;
        
        lines_ = new ArrayList();
        view_  = new JPanel(new BorderLayout());
        text_  = new JTextArea();
        
        text_.setRows(rows);
        text_.setColumns(cols);
        text_.setFont(FontUtil.getPreferredMonoFont());
        text_.setEditable(false);
        text_.addKeyListener(new SwingConsole.keyHandler());
        text_.setWrapStyleWord(true);
        text_.setLineWrap(true);
        text_.getCaret().setVisible(true);

        JScrollPane scroller = new JScrollPane();
        
        //scroller.setVerticalScrollBarPolicy(
        //  JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        scroller.getViewport().add(text_);
        scroller.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);

        view_.add(scroller, BorderLayout.CENTER);
        text_.requestFocus();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the user interface component of this SwingConsole. Add the view
     * to a Container to display the console.
     * 
     * @return JComponent
     */
    public JComponent getView()
    {
        return view_;
    }
    
    //--------------------------------------------------------------------------
    // Console Interface
    //--------------------------------------------------------------------------
    
    /**
     * Reads a command from the console.
     * 
     * @see toolbox.util.ui.console.Console#read()
     */
    public String read() throws InterruptedIOException
    {
        synchronized (lines_)
        {
            while ((null != view_) && lines_.isEmpty())
            {
                try
                {
                    lines_.wait(0);
                }
                catch (InterruptedException woken)
                {
                    Thread.interrupted();
                    
                    InterruptedIOException wake = 
                        new InterruptedIOException("Interrupted");
                    
                    wake.initCause(woken);
                    throw wake;
                }
            }

            if (view_ == null)
                return null;

            return (String) lines_.remove(0);
        }
    }


    /**
     * Writes a character string to the console.
     * 
     * @see toolbox.util.ui.console.Console#write(java.lang.String)
     */
    public synchronized void write(String msg)
    {
        try
        {
            text_.getCaret().setVisible(false);
            text_.insert(msg, textLength_);
            textLength_ += msg.length();
            
            text_.setCaretPosition(
                textLength_ + promptLength_ + insertionPoint_);
            
            text_.getCaret().setVisible(true);
        }
        catch (Throwable ohno)
        {
            logger_.error(
                "Failure : TextLength=" 
                + textLength_
                + " promptLength=" 
                + promptLength_ 
                + " lineLength="
                + lineLength_ 
                + " text=" 
                + text_.getText().length(), 
                ohno);
        }
    }


    /**
     * @see toolbox.util.ui.console.Console#clear()
     */
    public synchronized void clear()
    {
        try
        {
            text_.setText("");
            textLength_     = 0;
            promptLength_   = 0;
            lineLength_     = 0;
            insertionPoint_ = 0;
            
            text_.setCaretPosition(
                textLength_ + promptLength_ + insertionPoint_);
            
            text_.getCaret().setVisible(true);
        }
        catch (Throwable ohno)
        {
            logger_.error(
                "Failure : TextLength=" 
                + textLength_ 
                + " promptLength="
                + promptLength_ 
                + " lineLength=" 
                + lineLength_ 
                + " text="
                + text_.getText().length(), 
                ohno);
        }
    }


    /**
     * @see toolbox.util.ui.console.Console#renderPrompt()
     */
    public synchronized void renderPrompt()
    {
        try
        {
            text_.replaceRange(getPrompt(), 
                textLength_, textLength_ + promptLength_);
            
            promptLength_ = getPrompt().length();
            
            text_.setCaretPosition(
                textLength_ + promptLength_ + insertionPoint_);
            
            text_.getCaret().setVisible(true);
        }
        catch (Throwable ohno)
        {
            logger_.error(
                "Failure : TextLength=" 
                + textLength_ 
                + " promptLength="
                + promptLength_ 
                + " lineLength=" 
                + lineLength_ 
                + " text="
                + text_.getText().length(), 
                ohno);
        }
    }

    //--------------------------------------------------------------------------
    // AbstractConsole Impl
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.console.AbstractConsole#setCommandLine(
     *      java.lang.String)
     */
    public synchronized void setCommandLine(String cmd)
    {
        try
        {
            text_.replaceRange(
                cmd, 
                textLength_ + promptLength_, 
                textLength_ + promptLength_ + lineLength_);
            
            lineLength_ = cmd.length();
            insertionPoint_ = lineLength_;
            
            text_.setCaretPosition(
                textLength_ + promptLength_ + insertionPoint_);
            
            text_.getCaret().setVisible(true);
        }
        catch (Throwable ohno)
        {
            logger_.error(
                "Failure : TextLength=" 
                + textLength_ 
                + " promptLength="
                + promptLength_ 
                + " lineLength=" 
                + lineLength_ 
                + " text="
                + text_.getText().length(), 
                ohno);
        }
    }


    /**
     * @see toolbox.util.ui.console.AbstractConsole#getCursorDownName()
     */
    public String getCursorDownName()
    {
        return KeyEvent.getKeyText(KeyEvent.VK_DOWN);
    }


    /**
     * @see toolbox.util.ui.console.AbstractConsole#getCursorUpName()
     */
    public String getCursorUpName()
    {
        return KeyEvent.getKeyText(KeyEvent.VK_UP);
    }

    //--------------------------------------------------------------------------
    // KeyHandler
    //--------------------------------------------------------------------------
    
    /**
     * Handle key actions.
     */
    private class keyHandler extends KeyAdapter
    {
        public synchronized void keyPressed(KeyEvent e)
        {
            int val = e.getKeyCode();
            char ch = e.getKeyChar();
            int mod = e.getModifiers();

            boolean consumed = false;

            // There may be user confusion about Ctrl-C: since this is a shell,
            // users might expect Ctrl-C to terminate the currently running
            // command. At this writing, we don't have command termination, so
            // we'll go ahead and grab Ctrl-C for copy. (Suggest "ESC" for
            // termination...)

            try
            {
                if ((mod & InputEvent.CTRL_MASK) != 0)
                {
                    consumed = control(val, ch, mod);
                }
                else
                {
                    if (KeyEvent.CHAR_UNDEFINED == ch)
                    {
                        consumed = handling(val, ch, mod);
                    }
                    else
                    {
                        consumed = typing(val, ch, mod);
                    }
                }


                if (consumed)
                {
                    text_.setCaretPosition(
                        textLength_ + promptLength_ + insertionPoint_);
                    
                    text_.getCaret().setVisible(true);

                    // consume the event so that it doesn't get processed by the
                    // TextArea control.
                    e.consume();
                }
            }
            catch (Throwable ohno)
            {
                logger_.error(
                    "Failure : TextLength=" 
                    + textLength_
                    + " promptLength=" 
                    + promptLength_ 
                    + " lineLength="
                    + lineLength_ 
                    + " text=" 
                    + text_.getText().length(), 
                    ohno);
            }
        }
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Handles non-character editing of the command line. Handling is as
     * follows: <p>
     * <ul>
     *  <li>Ctrl-C - copys the current selection to the Clipboard.
     *  <li>Ctrl-V - Inserts text from the ClipBoard into the current line.
     *  <li>Ctrl-D - Sends an EOT command.
     *  <li>Ctrl-L - Clear the text area.
     *  <li>Ctrl-U - Clear the command line.
     *  <li>Ctrl-bksp - Clear the command line.
     * </ul>
     * <p>There may be user confusion about Ctrl-C: since this is a shell,
     * users might expect Ctrl-C to terminate the currently running command. At
     * this writing, we don't have command termination, so we'll go ahead and
     * grab Ctrl-C for copy. (Suggest Esc for termination...)
     * 
     * @param val KeyCode value of the key pressed.
     * @param ch Character associated with the key pressed.
     * @param modifiers Any modifiers that might have been pressed.
     */
    private boolean control(int val, char ch, int modifiers)
    {
        switch (val)
        {
            case KeyEvent.VK_C:
                if (logger_.isEnabledFor(Level.INFO))
                {
                    logger_.info("--> COPY <--");
                }
                copy();
                return true;

                
            case KeyEvent.VK_V:
                if (logger_.isEnabledFor(Level.INFO))
                {
                    logger_.info("--> PASTE <--");
                }
                paste();
                return true;

                
            // Let's try a ^D quit...
            case KeyEvent.VK_D:
                if (logger_.isEnabledFor(Level.INFO))
                {
                    logger_.info("--> QUIT <--");
                }
                setCommandLine("\u0004");
                submit(true);
                setCommandLine("");
                return true;

                
            case KeyEvent.VK_L:
                if (logger_.isEnabledFor(Level.INFO))
                {
                    logger_.info("--> CLEAR <--");
                }
                setCommandLine("clear");
                submit(true);
                return true;

                
            case KeyEvent.VK_U:
            case KeyEvent.VK_BACK_SPACE:
                setCommandLine("");
                return true;

                
            default:
                return false;
        }
    }


    /**
     * Handles non-character editing of the command line. Handling is as
     * follows: <p/>
     * <ul>
     *  <li>Cursor keys left and right - move the caret and update the current
     *      insertLine value.
     *  <li><Home> - Move cursor to the beginning of the current line.
     *  <li><End> - Move cursor to the end of the current line.
     * </ul>
     * 
     * @param val KeyCode value of the key pressed.
     * @param ch Character associated with the key pressed.
     * @param modifiers Any modifiers that might have been pressed.
     */
    private boolean handling(int val, char ch, int modifiers)
    {
        switch (val)
        {
            case KeyEvent.VK_HOME:
                insertionPoint_ = 0;
                return true;

            case KeyEvent.VK_END:
                insertionPoint_ = lineLength_;
                return true;

            case KeyEvent.VK_KP_LEFT:
            case KeyEvent.VK_LEFT:
                insertionPoint_--;
                if (insertionPoint_ < 0)
                {
                    insertionPoint_ = 0;
                }
                return true;

            case KeyEvent.VK_KP_RIGHT:
            case KeyEvent.VK_RIGHT:
                insertionPoint_++;
                if (insertionPoint_ > lineLength_)
                {
                    insertionPoint_ = lineLength_;
                }
                return true;

            case KeyEvent.VK_KP_UP:
            case KeyEvent.VK_UP:
                setCommandLine(getCursorUpName());
                submit(false);
                return true;

            case KeyEvent.VK_KP_DOWN:
            case KeyEvent.VK_DOWN:
                setCommandLine(getCursorDownName());
                submit(false);
                return true;

            default:
                return false;
        }
    }


    /**
     * Handles the editing of the command line. Handling is as follows: <p/>
     * <ul>
     *  <li>backspace - Delete the character ahead of lineInsert from input ln.
     *  <li>delete - Delete the character after lineInsert from input line.
     *  <li>enter - Finish the input line by calling <code>submit()</code>.
     *  <li>otherwise insert the character.
     * </ul>
     * 
     * @param val KeyCode value of the key pressed.
     * @param ch Character associated with the key pressed.
     * @param modifiers Any modifiers that might have been pressed.
     */
    private boolean typing(int val, char ch, int modifiers)
    {
        switch (ch)
        {
            case KeyEvent.VK_BACK_SPACE:
                if (insertionPoint_ >= 1 && insertionPoint_ <= lineLength_)
                {
                    text_.replaceRange("", 
                        textLength_ + promptLength_ + insertionPoint_ - 1, 
                        textLength_ + promptLength_ + insertionPoint_);
                    
                    insertionPoint_--;
                    lineLength_--;
                }
                return true;

                
            case KeyEvent.VK_DELETE:
                if (insertionPoint_ < lineLength_)
                {
                    text_.replaceRange("", 
                        textLength_ + promptLength_ + insertionPoint_, 
                        textLength_ + promptLength_ + insertionPoint_ + 1);
                    
                    lineLength_--;
                }
                return true;

                
            case KeyEvent.VK_ENTER:
                submit(true);
                return true;

                
            default:
                text_.insert(
                    Character.toString(ch), 
                    textLength_ + promptLength_ + insertionPoint_++);
            
                lineLength_++;
                return true;
        }
    }


    /**
     * Copy the selection to the system clipboard.
     */
    private void copy()
    {
        String selection = text_.getSelectedText();

        if ((selection != null) && (selection.length() > 0))
        {
            StringSelection select = new StringSelection(selection);
            Clipboard clip = text_.getToolkit().getSystemClipboard();
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
        Clipboard cb = text_.getToolkit().getSystemClipboard();
        Transferable trans = cb.getContents(this);
        
        if (trans == null)
            return;

        String cbText = null;
        
        try
        {
            cbText = (String) trans.getTransferData(DataFlavor.stringFlavor);
        }
        catch (UnsupportedFlavorException ufe)
        {
            return;
        }
        catch (IOException ioe)
        {
            return;
        }

        if (cbText == null)
            return;

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
            String line = cbText.substring(current, lineEnd);
            text_.insert(line, textLength_ + promptLength_ + insertionPoint_);
            insertionPoint_ += line.length();
            lineLength_ += line.length();

            if (fullLine)
                submit(true);
            
            current = lineEnd + 1;
        }
        while (current < cbText.length());
    }


    /**
     * Finishes an input line and provides it as input to the console reader.
     * 
     * @param appendNewLine Clear the line and append a newline.
     */
    private void submit(boolean appendNewLine)
    {
        synchronized (lines_)
        {
            try
            {
                lines_.add(
                    text_.getText(
                        textLength_ + promptLength_, lineLength_) + "\n");
            }
            catch (BadLocationException ble)
            {
                IllegalArgumentException badLoc = 
                    new IllegalArgumentException("bad location");
                
                badLoc.initCause(ble);
                throw badLoc;
            }

            if (appendNewLine)
            {
                text_.append("\n");
                textLength_     += promptLength_ + lineLength_ + 1;
                promptLength_   = 0;
                lineLength_     = 0;
                insertionPoint_ = 0;
            }

            lines_.notify();
        }
    }
}