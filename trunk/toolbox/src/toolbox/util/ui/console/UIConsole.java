package toolbox.util.ui.console;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import toolbox.util.io.JTextAreaOutputStream;
import toolbox.util.ui.JSmartFrame;
import toolbox.util.ui.console.action.HistoryUpAction;

/**
 * Text mode application console wrapped in a Swing frame.
 * 
 * @see toolbox.util.ui.console.UIConsoleArea
 */
public class UIConsole extends JSmartFrame implements Console 
{
    //--------------------------------------------------------------------------
    // Static Fields
    //--------------------------------------------------------------------------

    /**
     * End of line character.
     */
    private static String EOL = System.getProperty("line.separator", "\n");

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Sink for input/output.
     */
    private UIConsoleArea consoleArea_;

    /**
     * Pipes characters read from the console.
     */
    private InputStream inputStream_;

    /**
     * Pipes characters to the console.
     */
    private PrintStream outputStream_;

    /**
     * Ties an inputstream to a textarea.
     */
    private TextAreaInputStream textAreaInputStream_;

    /**
     * Delegate text console.
     */
    private toolbox.util.Console textConsole_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a UIConsole.
     * 
     * @param title Window title.
     * @param rows Number of visible rows.
     * @param columns Number of visible columns.
     */
    public UIConsole(String title, int rows, int columns)
    {
        super(title);
        setJMenuBar(buildMenuBar());
        consoleArea_ = new UIConsoleArea(this, rows, columns);
        getContentPane().setLayout(new BorderLayout());
        
        getContentPane().add(
            BorderLayout.CENTER, 
            new JScrollPane(consoleArea_));
        
        // Register shortcuts
        registerShortcut(
            KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), 
            new HistoryUpAction(this));

        // Create the stream for I/O in the consoles
        textAreaInputStream_ = new TextAreaInputStream(/*consoleArea_*/);
        inputStream_ = new LineInputStream(textAreaInputStream_);
        
        //outputStream_ = new PrintStream(
        //    new TextAreaOutputStream(consoleArea_), true);
        
        outputStream_ = new PrintStream(
            new JTextAreaOutputStream(consoleArea_), true);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        consoleArea_.requestFocus();        
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Builds the menu bar.
     * 
     * @return JMenuBar
     */
    protected JMenuBar buildMenuBar()
    {
        // File
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.addSeparator();

        // Exit
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');
        exitItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent action)
            {
                dispose();
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);


        // Edit
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');

        // Clear
        JMenuItem clearItem = new JMenuItem("Clear all");
        clearItem.setMnemonic('a');
        clearItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent action)
            {
                UIConsole.this.clear();
            }
        });
        editMenu.add(clearItem);
        editMenu.addSeparator();

        // Cut
        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.setMnemonic('t');
        cutItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent action)
            {
                UIConsole.this.consoleArea_.cut();
            }
        });
        editMenu.add(cutItem);

        // Copy
        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.setMnemonic('C');
        copyItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent action)
            {
                UIConsole.this.consoleArea_.copy();
            }
        });
        editMenu.add(copyItem);

        // Paste at end
        JMenuItem pasteItem = new JMenuItem("Paste at end");
        pasteItem.setMnemonic('P');
        pasteItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent action)
            {
                UIConsole.this.consoleArea_.paste();
            }
        });
        editMenu.add(pasteItem);

        // Add menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        return menuBar;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the stream consisting of characters typed by the user.
     * 
     * @return InputStream
     */
    public InputStream getInputStream()
    {
        return inputStream_;
    }


    /**
     * Returns the stream that prints characters on the console.
     * 
     * @return PrintStream
     */
    public PrintStream getOutputStream()
    {
        return outputStream_;
    }


    /**
     * Sends data to the console.
     * 
     * @param text Text to send to the console.
     */
    public synchronized void send(String text)
    {
        getOutputStream().print(text);
    }


    /**
     * Clear the contents of the console.
     */
    public synchronized void clear()
    {
        consoleArea_.setText("");
        consoleArea_.resetFirstInputLocation();
    }

    
    /**
     * Convenience method to register shortcuts with the console.
     * 
     * @param keyStroke Keystroke to register.
     * @param action Actio to run.
     */
    public void registerShortcut(KeyStroke keyStroke, Action action)
    {
        consoleArea_.registerShortcut(keyStroke, action);
    }

    
    /**
     * Returns the text mode console.
     * 
     * @return toolbox.util.Console
     */
    public toolbox.util.Console getTextConsole()
    {
        return textConsole_;
    }


    /**
     * Sets the text mode console.
     * 
     * @param console Text mode console.
     */
    public void setTextConsole(toolbox.util.Console console)
    {
        textConsole_ = console;
    }


    /**
     * Returns the text area within the UI console.
     * 
     * @return UIConsoleArea
     */
    public UIConsoleArea getConsoleArea()
    {
        return consoleArea_;
    }
    
    //--------------------------------------------------------------------------
    // LineInputStream
    //--------------------------------------------------------------------------

    /**
     * FilterInputStream that buffers input until newline occurs. Then
     * everything is passed along. If backspace (ch = 8) is received then the
     * last character in the buffer is removed.
     */
    public class LineInputStream extends FilterInputStream
    {
        byte byteArray_[];
        int arrayOffset_;
        int arrayLength_;

        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------
        
        /**
         * Creates a LineInputStream.
         * 
         * @param in Stream to chain.
         */
        public LineInputStream(InputStream in)
        {
            super(in);
        }

        //----------------------------------------------------------------------
        // Overrides InputStream
        //----------------------------------------------------------------------
        
        /**
         * @see java.io.InputStream#read()
         */
        public synchronized int read() throws IOException
        {
            // If there are data in buffer the return the first character
            // in buffer.
            if (byteArray_ != null && arrayOffset_ < arrayLength_)
                return byteArray_[arrayOffset_++];

            // if buffer is empty, fill buffer...
            byteArray_ = readLine();
            arrayOffset_ = 0;
            arrayLength_ = byteArray_.length;

            // If there are data in buffer the return the first character
            // in buffer.
            if (byteArray_ != null && arrayOffset_ < arrayLength_)
                return byteArray_[arrayOffset_++];
            else
                return -1;
        }


        /**
         * @see java.io.InputStream#read(byte[], int, int)
         */
        public synchronized int read(byte bytes[], int offset, int length)
            throws IOException
        {

            if (byteArray_ != null && arrayOffset_ < arrayLength_)
            {
                int available = available();

                if (length > available)
                    length = available;

                System.arraycopy(
                    byteArray_, 
                    arrayOffset_, 
                    bytes, 
                    offset, 
                    length);
                
                arrayOffset_ += length;
                return length;
            }

            byteArray_ = readLine();
            arrayOffset_ = 0;
            arrayLength_ = byteArray_.length;

            if (byteArray_ == null || arrayOffset_ >= arrayLength_)
                return -1;

            int available = available();

            if (length > available)
                length = available;

            System.arraycopy(byteArray_, arrayOffset_, bytes, offset, length);
            arrayOffset_ += length;
            return length;
        }


        /**
         * @see java.io.InputStream#available()
         */
        public synchronized int available() throws IOException
        {
            return arrayLength_ - arrayOffset_ + super.available();
        }

        //----------------------------------------------------------------------
        // Public 
        //----------------------------------------------------------------------
        
        /**
         * Reads a line from this input stream.
         * 
         * @return Array of characters read.
         * @throws IOException on I/O exception.
         */
        public synchronized byte[] readLine() throws IOException
        {
            byte bytes[];
            int ch;
            boolean ready = false;
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();

            while (!ready)
            {
                ch = in.read();

                if (ch == -1)
                {
                    // EOF
                    ready = true;
                }
                else if (ch == 8)
                {
                    // Backspace: Remove last character in buffer.
                    bytes = bytesOut.toByteArray();
                    bytesOut.reset();
                    int length = bytes.length - 1;

                    if (length > 0)
                        bytesOut.write(bytes, 0, length);
                }
                else if (ch == 21)
                {
                    // ^U: Remove all character in buffer.
                    bytesOut.reset();
                    int length = 0;
                }
                else if (ch == 10)
                {
                    bytesOut.write(ch);
                    // NewLine: Return current buffer.
                    ready = true;
                }
                else
                {
                    // Other: Add to buffer.
                    bytesOut.write(ch);

                    //out.write(ch);
                    //out.flush();
                }
            }

            return bytesOut.toByteArray();
        }
    }

    //--------------------------------------------------------------------------
    // TextAreaOutputStream
    //--------------------------------------------------------------------------

    /**
     * The final output stream that send the output to the associated
     * TextArea. The output is *appended* to the text in the TextArea,
     * because it will only be used like a console output.
     */
//    public class TextAreaOutputStream extends OutputStream {
//
//        private UIConsoleArea consoleArea;
//        private String buffer;
//
//        /**
//         * Connect the stream to a TextArea.
//         */
//        public TextAreaOutputStream(UIConsoleArea textArea) {
//            buffer = "";
//            consoleArea = textArea;
//        }
//
//        /**
//         * Add the contents in the internal buffer to the TextArea and
//         * delete the buffer.
//         */
//        public synchronized void flush() {
//            consoleArea.append(buffer);
//            buffer = "";
//        }
//
//        /**
//         * Write to the internal buffer.
//         */
//        public synchronized void write(int b) {
//            //if (b == 13) {
//            //  buffer += eol;
//            //} else if (b != 10) { // ignore LF
//            if (b < 0)
//                b += 256;
//            buffer += (char) b;
//            //  }
//        }
//    }

    //--------------------------------------------------------------------------
    // TextAreaInputStream
    //--------------------------------------------------------------------------

    public class TextAreaInputStream extends PipedInputStream
    {
        private OutputStream out;
        private int numKeysTyped;

        public TextAreaInputStream()
        {
            try
            {
                out = new PipedOutputStream(this);
                numKeysTyped = 0;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }


        /**
         * Process the end of line (as received from paste) but no other.
         */
        private void send(char ch)
        {
            try
            {
                if (ch == 10)
                { // LF
                    byte[] beol = EOL.getBytes();
                    out.write(beol, 0, beol.length);
                    out.flush();
                    numKeysTyped = 0;
                }
                else if (ch >= 32 && ch < 256)
                {
                    out.write(ch);
                    numKeysTyped++;
                }
                else if (ch == 13)
                {
                    ; // ignore RETURN
                }
                else
                {
                    out.write('?');
                    numKeysTyped++;
                }
            }
            catch (IOException e)
            {
                Toolkit.getDefaultToolkit().beep();
            }
        }


        /**
         * send
         * 
         * @param s
         */
        private void send(String s)
        {
            for (int i = 0; i < s.length(); i++)
            {
                send(s.charAt(i));
            }
        }
    }
}

//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2 of the License, or (at your option) any later version.

//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//Lesser General Public License for more details.

//You should have received a copy of the GNU Lesser General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

