package toolbox.dbconsole;

import java.awt.BorderLayout;
import java.awt.Font;
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
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

/**
 * Text mode application console wrapped in a Swing frame.
 */
public class SwingConsole extends JFrame implements ConsoleIfc {

    //--------------------------------------------------------------------------
    // Static Fields
    //--------------------------------------------------------------------------

    private static String EOL = System.getProperty("line.separator", "\n");

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Sink for input/output.
     */
    private JConsoleArea consoleArea;

    /**
     * Pipes characters read from the console.
     */
    private InputStream inputStream;

    /**
     * Pipes characters to the console.
     */
    private PrintStream outputStream;

    /**
     * Ties an inputstream to a textarea.
     */
    private TextAreaInputStream textAreaInputStream;

    /**
     * Delegate text console.
     */
    private Console textConsole;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Create a new console interface.
     * 
     * @param title Window title.
     * @param rows Number of visible rows.
     * @param columns Number of visible columns.
     */
    public SwingConsole(String title, int rows, int columns) {
        super(title);
        getContentPane().setLayout(new BorderLayout());
        consoleArea = new JConsoleArea(this, rows, columns);
        consoleArea.setFont(new Font("Lucida Console", Font.PLAIN, 12));
        getContentPane().add(BorderLayout.CENTER, new JScrollPane(consoleArea));

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        // Exit
        fileMenu.addSeparator();
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent action) {
                dispose();
            }
        });
        fileMenu.add(exitItem);

        // Add menu bar
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');

        // Clear
        JMenuItem clearItem = new JMenuItem("Clear all");
        clearItem.setMnemonic('a');
        clearItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent action) {
                SwingConsole.this.clear();
            }
        });
        editMenu.add(clearItem);
        editMenu.addSeparator();

        // Cut
        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.setMnemonic('t');
        cutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent action) {
                SwingConsole.this.consoleArea.cut();
            }
        });
        editMenu.add(cutItem);

        // Copy
        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.setMnemonic('C');
        copyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent action) {
                SwingConsole.this.consoleArea.copy();
            }
        });
        editMenu.add(copyItem);

        // Paste at end
        JMenuItem pasteItem = new JMenuItem("Paste at end");
        pasteItem.setMnemonic('P');
        pasteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent action) {
                SwingConsole.this.consoleArea.paste();
            }
        });
        editMenu.add(pasteItem);

        // Add menu to menu bar
        menuBar.add(editMenu);

        // Add menu bar
        getContentPane().add(BorderLayout.NORTH, menuBar);

        // Register shortcuts
        registerShortcut(
            KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
            new HistoryUpAction(this));

        int scrwidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int scrheight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int conwidth = getSize().width;
        int conheight = getSize().height;

        setLocation((scrwidth - conwidth) / 2, (scrheight - conheight) / 2);

        // Create the stream for I/O in the consoles
        textAreaInputStream = new TextAreaInputStream(consoleArea);
        inputStream = new LineInputStream(textAreaInputStream);
        
        outputStream =
            new PrintStream(new TextAreaOutputStream(consoleArea), true);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        consoleArea.requestFocus();
    }

    /**
     * Get the stream consisting of characters typed by the user
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Get the stream to which to print characters to be displayed on the console
     */
    public PrintStream getOutputStream() {
        return outputStream;
    }

    /** 
     * Send data to console input
     */
    public synchronized void send(String text) {
        textAreaInputStream.send(text);
    }

    /**
     * Clear the console content
     */
    public synchronized void clear() {
        consoleArea.setText("");
        consoleArea.resetFirstInputLocation();
    }

   
    public void registerShortcut(KeyStroke keyStroke, Action action) {
        consoleArea.registerShortcut(keyStroke, action);
    }

    //--------------------------------------------------------------------------
    // LineInputStream
    //--------------------------------------------------------------------------

    /**
     * FilterInputStream that buffers input until newline occurs. Then 
     * everything is passed along. If backspace (ch = 8) is received then the 
     * last character in the buffer is removed.
     */
    public class LineInputStream extends FilterInputStream {

        byte byteArray[];
        int arrayOffset;
        int arrayLength;

        public LineInputStream(InputStream in) {
            super(in);
        }

        public synchronized int read() throws IOException {
            // If there are data in buffer the return the first character
            // in buffer.
            if (byteArray != null && arrayOffset < arrayLength)
                return byteArray[arrayOffset++];

            // if buffer is empty, fill buffer...
            byteArray = readLine();
            arrayOffset = 0;
            arrayLength = byteArray.length;

            // If there are data in buffer the return the first character
            // in buffer.
            if (byteArray != null && arrayOffset < arrayLength)
                return byteArray[arrayOffset++];
            else
                return -1;
        }

        public synchronized int read(byte bytes[], int offset, int length)
            throws IOException {

            if (byteArray != null && arrayOffset < arrayLength) {
                int available = available();

                if (length > available)
                    length = available;

                System.arraycopy(byteArray, arrayOffset, bytes, offset, length);
                arrayOffset += length;
                return length;
            }

            byteArray = readLine();
            arrayOffset = 0;
            arrayLength = byteArray.length;

            if (byteArray == null || arrayOffset >= arrayLength)
                return -1;

            int available = available();

            if (length > available)
                length = available;

            System.arraycopy(byteArray, arrayOffset, bytes, offset, length);
            arrayOffset += length;
            return length;
        }

        public synchronized int available() throws IOException {
            return arrayLength - arrayOffset + super.available();
        }

        public synchronized byte[] readLine() throws IOException {

            byte bytes[];
            int ch;
            boolean ready = false;
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();

            while (!ready) {
                ch = this.in.read();

                if (ch == -1) {
                    // EOF
                    ready = true;
                }
                else if (ch == 8) {
                    // Backspace: Remove last character in buffer.
                    bytes = bytesOut.toByteArray();
                    bytesOut.reset();
                    int length = bytes.length - 1;

                    if (length > 0)
                        bytesOut.write(bytes, 0, length);
                }
                else if (ch == 21) {
                    // ^U: Remove all character in buffer.
                    bytesOut.reset();
                    int length = 0;
                }
                else if (ch == 10) {
                    bytesOut.write(ch);
                    // NewLine: Return current buffer.
                    ready = true;
                }
                else {
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
    public class TextAreaOutputStream extends OutputStream {

        private JConsoleArea consoleArea;
        private String buffer;

        /**
         * Connect the stream to a TextArea.
         */
        public TextAreaOutputStream(JConsoleArea textArea) {
            buffer = "";
            consoleArea = textArea;
        }

        /**
         * Add the contents in the internal buffer to the TextArea and
         * delete the buffer.
         */
        public synchronized void flush() {
            consoleArea.append(buffer);
            buffer = "";
        }

        /**
         * Write to the internal buffer.
         */
        public synchronized void write(int b) {
            //if (b == 13) {
            //  buffer += eol;
            //} else if (b != 10) { // ignore LF
            if (b < 0)
                b += 256;
            buffer += (char) b;
            //  }
        }
    }

    //--------------------------------------------------------------------------
    // TextAreaInputStream
    //--------------------------------------------------------------------------

    public class TextAreaInputStream extends PipedInputStream {

        private JConsoleArea consoleArea;
        private OutputStream out;
        private int numKeysTyped;

        public TextAreaInputStream(JConsoleArea newTextArea) {
            try {
                consoleArea = newTextArea;
                out = new PipedOutputStream(this);
                numKeysTyped = 0;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Process the end of line (as received from paste) but no other.
         */
        private void send(char ch) {

            try {
                if (ch == 10) { // LF
                    byte[] beol = EOL.getBytes();
                    out.write(beol, 0, beol.length);
                    out.flush();
                    numKeysTyped = 0;
                }
                else if (ch >= 32 && ch < 256) {
                    out.write(ch);
                    numKeysTyped++;
                }
                else if (ch == 13) {
                    ; // ignore RETURN
                }
                else {
                    out.write('?');
                    numKeysTyped++;
                }
            }
            catch (IOException e) {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        /**
         * send
         * 
         * @param s
         */
        private void send(String s) {
            for (int i = 0; i < s.length(); i++) {
                send(s.charAt(i));
            }
        }
    }
    
    /**
     * getTextConsole
     * 
     * @return
     */
    public Console getTextConsole() {
        return textConsole;
    }

    /**
     * setTextConsole
     * 
     * @param console
     */
    public void setTextConsole(Console console) {
        textConsole = console;
    }

    /**
     * getConsoleArea
     * 
     * 
     */
    public JConsoleArea getConsoleArea() {
        return consoleArea;
    }

}