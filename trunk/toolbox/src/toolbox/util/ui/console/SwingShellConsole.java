package toolbox.util.ui.console;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;

import java.awt.HeadlessException;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InterruptedIOException;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import net.jxta.credential.Credential;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.rendezvous.RendezVousService;
import net.jxta.rendezvous.RendezvousEvent;
import net.jxta.rendezvous.RendezvousListener;

import net.jxta.exception.PeerGroupException;

import net.jxta.impl.rendezvous.RendezVousServiceInterface;
import net.jxta.impl.rendezvous.rpv.PeerView;
import net.jxta.impl.rendezvous.rpv.PeerViewEvent;
import net.jxta.impl.rendezvous.rpv.PeerViewListener;

/**
 *  A Swing based container for JXTA Shell
 **/
public class SwingShellConsole extends ShellConsole {
    
    /**
     *  Log4J Logger
     **/
    private static final Logger LOG = Logger.getLogger(SwingShellConsole.class.getName());
    
    /**
     *  The number of consoles opened
     **/
    private static int consoleCount = 0;
    
    /**
     *  Lines of console input awaiting shell processing.
     **/
    private List lines = new ArrayList();
    
    /**
     *  The panel in which our stuff lives.
     **/
    private JPanel panel;
    private boolean embedded = false;
    
    /**
     *  The frame that holds the TextArea object making up the terminal. If we
     *  are running in a panel provided by someone else then this will not be
     *  initialized.
     **/
    private JFrame frame = null;
    
    /**
     *  The TextArea object that displays the status
     **/
    private JLabel statusStart = null;
    
    /**
     *  The TextArea object that displays the status
     **/
    private JLabel statusEnd = null;
    
    /**
     *  The TextArea object that displays the data
     **/
    private JTextArea text = null;
    
    /**
     *  The length of the static text displayed, excludes the prompt and the
     *   current input line.
     **/
    private int textLength = 0;
    
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
    private int lineInsert = 0;
    
    /**
     *  Keeps bits for the status line.
     **/
    private StatusKeeper statusKeeper = null;
    
    /**
     *  Creates a new terminal window with rows row and cols columns to display.
     *
     * @param  consoleName  the name of the console
     * @param  rows     the number of rows displayed
     * @param  cols     the number of columns displayed
     **/
    public SwingShellConsole(String consoleName, int rows, int cols) {
        this( null, consoleName, rows, cols);
    }
    
    /**
     *  Creates a new terminal window with rows row and cols columns to display.
     *
     * @param  inPanel  The panel in which the shell console will live.
     * @param  consoleName  the name of the console
     * @param  rows     the number of rows displayed
     * @param  cols     the number of columns displayed
     **/
    public SwingShellConsole(JPanel inPanel, String consoleName, int rows, int cols) {
        super( consoleName );
        
        String consoleFullName;
        synchronized ( this.getClass() ) {
            consoleFullName = consoleName + " - " + (++consoleCount);
        }
        
        try {
            if( null == inPanel ) {
                panel = new JPanel( new GridBagLayout() );
                
                // create a frame for it to live in.
                frame = new JFrame( consoleFullName );
                frame.getContentPane().add( panel);
                
                frame.addWindowListener(
                new WindowAdapter() {
                    /**
                     *  {@inheritDoc}
                     **/
                    public void windowClosing(WindowEvent e) {
                        destroy();
                    }
                    
                    /**
                     *  {@inheritDoc}
                     **/
                    public void windowGainedFocus(WindowEvent e) {
                        text.getCaret().setVisible(true);
                    }
                    
                    /**
                     *  {@inheritDoc}
                     **/
                    public void windowLostFocus(WindowEvent e) {
                        text.getCaret().setVisible(false);
                    }
                }
                );
                
            } else {
                panel = inPanel;
                embedded = true;
            }
        } catch (InternalError error) {
            // this will occur is AWT is not available or can't be init'd (most
            // commonly on unix machines in a terminal window).
            if (LOG.isEnabledFor(Level.ERROR)) {
                LOG.error("Could not initialize AWT, using TTY Console mode");
            }
            
            HeadlessException horseman = new HeadlessException("InternalError");
            horseman.initCause( error );
            throw horseman;
        }
        
        int fontsize = 12;
        String fontname = System.getProperty("SHELLFONTNAME", "Lucida Sans Typewriter");
        String fontsizeProp = System.getProperty("SHELLFONTSIZE");
        if (fontsizeProp != null) {
            try {
                fontsize = Integer.valueOf(fontsizeProp).intValue();
            } catch (NumberFormatException e) {
                // will use default size
                ;
            }
        }
        
        this.statusStart = new JLabel( " ", JLabel.LEADING);
        statusStart.setFont(new Font(fontname, Font.PLAIN, fontsize));
        
        GridBagConstraints constr = new GridBagConstraints();
        
        constr.gridwidth = 1; constr.gridheight = 1;
        constr.gridx = 0; constr.gridy = 0;
        constr.weightx = 1; constr.weighty = 0;
        constr.anchor = GridBagConstraints.FIRST_LINE_START;
        constr.fill = GridBagConstraints.HORIZONTAL;
        
        panel.add( statusStart, constr );
        
        this.statusEnd = new JLabel( " ", JLabel.TRAILING );
        statusEnd.setFont(new Font(fontname, Font.PLAIN, fontsize));
        
        constr.gridwidth = 1; constr.gridheight = 1;
        constr.gridx = 1; constr.gridy = 0;
        constr.weightx = 1; constr.weighty = 0;
        constr.anchor = GridBagConstraints.FIRST_LINE_END;
        constr.fill = GridBagConstraints.HORIZONTAL;
        
        panel.add( statusEnd, constr );
        
        this.text = new JTextArea();
        text.setRows(rows);
        text.setColumns(cols);
        text.setFont(new Font(fontname, Font.PLAIN, fontsize));
        text.setEditable(false);
        text.addKeyListener( new SwingShellConsole.keyHandler() );
        text.setWrapStyleWord(true);
        text.setLineWrap(true);
        text.getCaret().setVisible(true);
        
        JScrollPane stsp = new JScrollPane();
        stsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        stsp.getViewport().add(text);
        stsp.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
        
        constr.gridwidth = 2; constr.gridheight = 1;
        constr.gridx = 0; constr.gridy = 1;
        constr.weightx = 1; constr.weighty = 1;
        constr.anchor = GridBagConstraints.LAST_LINE_END;
        constr.fill = GridBagConstraints.BOTH;
        
        panel.add( stsp, constr );
        
        if( null != frame ) {
            // Offset the frames
            frame.setLocation( (fontsize * 4), (fontsize * 4) );
            frame.pack();
            frame.setVisible(true);
        }
        
        text.requestFocus();
    }
    
    /**
     *  {@inheritDoc}
     *
     *  <p/>Terminates the current console window. If this was the last window
     *  open, the program is terminated.
     **/
    public synchronized void destroy() {
        if (frame != null) {
            frame.dispose();
            frame = null;
        }
        
        panel = null;
    }
    
    /**
     *  {@inheritDoc}
     **/
    public String read() throws InterruptedIOException {
        synchronized( lines ) {
            while( (null != panel) && lines.isEmpty() ) {
                try {
                    lines.wait( 0 );
                } catch (InterruptedException woken ) {
                    Thread.interrupted();
                    InterruptedIOException wake = new InterruptedIOException( "Interrupted" );
                    wake.initCause( woken );
                    throw wake;
                }
            }
            
            if( null == panel ) {
                return null;
            }
            
            return (String) lines.remove( 0 );
        }
    }
    
    /**
     *  {@inheritDoc}
     **/
    public synchronized void write(String msg) {
        try {
            text.getCaret().setVisible(false);
            text.insert(msg, textLength );
            textLength += msg.length();
            text.setCaretPosition(textLength + promptLength + lineInsert);
            text.getCaret().setVisible(true);
        } catch( Throwable ohno ) {
            LOG.error( "Failure : TextLength=" + textLength +
            " promptLength=" + promptLength +
            " lineLength=" + lineLength +
            " text=" + text.getText().length(), ohno );
        }
    }
    
    /**
     *  {@inheritDoc}
     **/
    public synchronized void clear() {
        try {
            text.setText( "" );
            textLength = 0;
            promptLength = 0;
            lineLength = 0;
            lineInsert = 0;
            text.setCaretPosition(textLength + promptLength + lineInsert);
            text.getCaret().setVisible(true);
        } catch( Throwable ohno ) {
            LOG.error( "Failure : TextLength=" + textLength +
            " promptLength=" + promptLength +
            " lineLength=" + lineLength +
            " text=" + text.getText().length(), ohno );
        }
    }
    
    /**
     *  {@inheritDoc}
     **/
    public synchronized void setPrompt( String newPrompt ) {
        try {
            text.replaceRange( newPrompt, textLength, textLength + promptLength);
            promptLength = newPrompt.length();
            text.setCaretPosition(textLength + promptLength + lineInsert);
            text.getCaret().setVisible(true);
        } catch( Throwable ohno ) {
            LOG.error( "Failure : TextLength=" + textLength +
            " promptLength=" + promptLength +
            " lineLength=" + lineLength +
            " text=" + text.getText().length(), ohno );
        }
    }
    
    /**
     *  {@inheritDoc}
     **/
    public synchronized void setCommandLine( String cmd ) {
        try{
            text.replaceRange( cmd, textLength + promptLength, textLength + promptLength + lineLength );
            lineLength = cmd.length();
            lineInsert = lineLength;
            text.setCaretPosition(textLength + promptLength + lineInsert);
            text.getCaret().setVisible(true);
        } catch( Throwable ohno ) {
            LOG.error( "Failure : TextLength=" + textLength +
            " promptLength=" + promptLength +
            " lineLength=" + lineLength +
            " text=" + text.getText().length(), ohno );
        }    }
    
    /**
     *  {@inheritDoc}
     **/
    public synchronized void setStatusGroup( PeerGroup group ) {
        // remove listeners
        if( null != statusKeeper ) {
            MembershipService membership = statusKeeper.statusGroup.getMembershipService();
            
            membership.removePropertyChangeListener( "defaultCredential", statusKeeper.membershipProperties );
            
            statusKeeper.membershipProperties = null;
            
            RendezVousService rendezVous = statusKeeper.statusGroup.getRendezVousService();
            
            rendezVous.removeListener( statusKeeper.rendezvousEventListener );
            
            statusKeeper.rendezvousEventListener = null;
            
            if( rendezVous instanceof RendezVousServiceInterface ) {
                RendezVousServiceInterface stdRdv = (RendezVousServiceInterface) rendezVous;
                
                PeerView rpv = stdRdv.getPeerView();
                
                rpv.removeListener( statusKeeper.peerviewEventListener );
                
                statusKeeper.peerviewEventListener = null;
            }
            
            statusKeeper = null;
        }
        
        // install listeners
        if( null != group ) {
            statusKeeper = new StatusKeeper( group );
            
            MembershipService membership = statusKeeper.statusGroup.getMembershipService();
            
            statusKeeper.membershipProperties = new MembershipPropertyListener();
            
            membership.addPropertyChangeListener( "defaultCredential", statusKeeper.membershipProperties );
            
            try {
                statusKeeper.credential = (membership.getDefaultCredential() != null);
            } catch( PeerGroupException failed ) {
                statusKeeper.credential = false;
            }
            
            RendezVousService rendezVous = statusKeeper.statusGroup.getRendezVousService();
            
            statusKeeper.rendezvousEventListener = new RendezvousEventListener();
            
            rendezVous.addListener( statusKeeper.rendezvousEventListener );
            
            statusKeeper.rendezvous = rendezVous.isRendezVous();
            
            statusKeeper.connectedClients = rendezVous.getConnectedPeerIDs().size();
            
            statusKeeper.connectedRdv = Collections.list(rendezVous.getConnectedRendezVous()).size();
            
            if( rendezVous instanceof RendezVousServiceInterface ) {
                RendezVousServiceInterface stdRdv = (RendezVousServiceInterface) rendezVous;
                
                PeerView rpv = stdRdv.getPeerView();
                
                statusKeeper.peerviewEventListener = new PeerViewEventListener();
                
                rpv.addListener( new PeerViewEventListener() );
                
                statusKeeper.peerview = statusKeeper.statusGroup.getRendezVousService().getLocalWalkView().size();
            }
            
            updateStatusString();
        }
    }
    
    /**
     *  {@inheritDoc}
     **/
    public String getCursorDownName() {
        return KeyEvent.getKeyText(KeyEvent.VK_DOWN);
    }
    
    /**
     *  {@inheritDoc}
     **/
    public String getCursorUpName() {
        return KeyEvent.getKeyText(KeyEvent.VK_UP);
    }
    
    /**
     *  Handle key actions
     **/
    private class keyHandler extends KeyAdapter {
        
        /**
         * {@inheritDoc}
         **/
        public synchronized void keyPressed(KeyEvent e) {
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
                if( (mod & InputEvent.CTRL_MASK) != 0 ) {
                    consumed = control(val, ch, mod);
                } else {
                    if( KeyEvent.CHAR_UNDEFINED == ch ) {
                        consumed = handling(val, ch, mod);
                    } else {
                        consumed = typing(val, ch, mod);
                    }
                }
                
                
                if( consumed ) {
                    text.setCaretPosition(textLength + promptLength + lineInsert);
                    text.getCaret().setVisible(true);
                    
                    // consume the event so that it doesn't get processed by the TextArea control.
                    e.consume();
                }
            } catch( Throwable ohno ) {
                LOG.error( "Failure : TextLength=" + textLength +
                " promptLength=" + promptLength +
                " lineLength=" + lineLength +
                " text=" + text.getText().length(), ohno );
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
    private boolean control(int val, char ch, int modifiers) {
        switch (val) {
            case KeyEvent.VK_C :
                if (LOG.isEnabledFor(Level.INFO)) {
                    LOG.info("--> COPY <--");
                }
                copy();
                return true;
                
            case KeyEvent.VK_V:
                if (LOG.isEnabledFor(Level.INFO)) {
                    LOG.info("--> PASTE <--");
                }
                paste();
                return true;
                
            // Let's try a ^D quit...
            case KeyEvent.VK_D :
                if (LOG.isEnabledFor(Level.INFO)) {
                    LOG.info("--> QUIT <--");
                }
                setCommandLine( "\u0004" );
                submit(true);
                setCommandLine( "" );
                return true;
                
            case KeyEvent.VK_L :
                if (LOG.isEnabledFor(Level.INFO)) {
                    LOG.info("--> CLEAR <--");
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
    private boolean handling(int val, char ch, int modifiers) {
        switch (val) {
            case KeyEvent.VK_HOME :
                lineInsert = 0;
                return true;
                
            case KeyEvent.VK_END :
                lineInsert = lineLength;
                return true;
                
            case KeyEvent.VK_KP_LEFT :
            case KeyEvent.VK_LEFT :
                lineInsert--;
                if (lineInsert < 0) {
                    lineInsert = 0;
                }
                return true;
                
            case KeyEvent.VK_KP_RIGHT :
            case KeyEvent.VK_RIGHT :
                lineInsert++;
                if (lineInsert > lineLength) {
                    lineInsert = lineLength;
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
    private boolean typing(int val, char ch, int modifiers) {
        
        switch (ch) {
            case KeyEvent.VK_BACK_SPACE :
                if (lineInsert >= 1 && lineInsert <= lineLength) {
                    text.replaceRange( "", textLength + promptLength + lineInsert - 1, textLength + promptLength + lineInsert);
                    lineInsert--;
                    lineLength--;
                }
                return true;
                
            case KeyEvent.VK_DELETE :
                if (lineInsert < lineLength) {
                    text.replaceRange( "", textLength + promptLength + lineInsert, textLength + promptLength + lineInsert + 1);
                    lineLength--;
                }
                return true;
                
            case KeyEvent.VK_ENTER :
                submit(true);
                return true;
                
            default :
                text.insert( Character.toString( ch ), textLength + promptLength + lineInsert++ );
                lineLength++;
                return true;
        }
    }
    
    /**
     * Copy the selection to the system clipboard.
     **/
    private void copy() {
        
        String selection = text.getSelectedText();
        
        if ( (null != selection) && (selection.length() > 0) ) {
            StringSelection select = new StringSelection(selection);
            Clipboard clip = text.getToolkit().getSystemClipboard();
            clip.setContents(select, select);
        }
    }
    
    /**
     *  Paste text from the clipboard into the shell. Text is added to at the
     *  end of the current command line. If the clipboard contents is non-text,
     * we'll bail out silently.
     */
    private void paste() {
        
        Clipboard cb = text.getToolkit().getSystemClipboard();
        Transferable trans = cb.getContents(this);
        if (trans == null) {
            return;
        }
        
        String cbText = null;
        try {
            cbText = (String) trans.getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
            return;
        } catch (IOException e) {
            return;
        }
        
        if (cbText == null) {
            return;
        }
        
        // Add the clipboard text to the end of the current command line.
        // If there are multiple lines in the clipboard, we paste and
        // execute each line as if the user entered it and and hit return.
        int current = 0;
        boolean fullLine = true;
        do {
            int lineEnd = cbText.indexOf( '\n', current );
            
            if ( -1 == lineEnd ) {
                lineEnd = cbText.length();
                fullLine = false;
            }
            
            // Append text to the current line.
            String aLine = cbText.substring( current, lineEnd );
            text.insert( aLine, textLength + promptLength + lineInsert );
            lineInsert += aLine.length();
            lineLength += aLine.length();
            
            if( fullLine ) {
                submit( true );
            }
            current = lineEnd + 1;
        } while( current < cbText.length() );
    }
    
    /**
     *  Finishes an input line and provides it as input to the console reader.
     *
     *  @param  appendNewLine Clear the line and append a newline
     **/
    private void submit( boolean appendNewLine ) {
        
        synchronized( lines ) {
            try {
                lines.add( text.getText( textLength + promptLength, lineLength ) + "\n" );
            } catch( BadLocationException ble ) {
                IllegalArgumentException badLoc = new IllegalArgumentException( "bad location" );
                badLoc.initCause( ble );
                throw badLoc;
            }
            
            if (appendNewLine) {
                text.append("\n");
                textLength += promptLength + lineLength + 1;
                promptLength = 0;
                lineLength = 0;
                lineInsert = 0;
            }
            
            lines.notify();
        }
    }
    
    /**
     * Container for status statistics
     **/
    private static class StatusKeeper {
        
        final PeerGroup statusGroup;
        
        boolean credential = false;
        
        boolean rendezvous = false;
        
        int peerview = 0;
        
        int connectedClients = 0;
        
        int clientReconnects = 0;
        
        int clientDisconnects = 0;
        
        int clientFailures = 0;
        
        int connectedRdv = 0;
        
        int rdvReconnects = 0;
        
        int rdvDisconnects = 0;
        
        int rdvFailures = 0;
        
        SwingShellConsole.MembershipPropertyListener membershipProperties = null;
        
        SwingShellConsole.RendezvousEventListener rendezvousEventListener = null;
        
        PeerViewListener peerviewEventListener = null;
        
        StatusKeeper( PeerGroup group ) {
            statusGroup = group;
        }
    }
    
    private void updateStatusString() {
        
        String status = (statusKeeper.credential ? " AUTH" : " auth" ) +
        " : " +
        (statusKeeper.rendezvous ? "RDV  " : "EDGE " ) +
        " pv:" +statusKeeper.peerview  +
        "  rdv: " + statusKeeper.connectedRdv + " / " + statusKeeper.rdvReconnects + ":" + statusKeeper.rdvDisconnects + ":" + statusKeeper.rdvFailures +
        "  client: " +statusKeeper.connectedClients + " / " + statusKeeper.clientReconnects + ":" + statusKeeper.clientDisconnects + ":" + statusKeeper.clientFailures ;
        
        Runtime vm = Runtime.getRuntime();
        
        String vmStats = Long.toString( vm.freeMemory() / 1024) + "k/" + Long.toString( vm.totalMemory() / 1024) + "k";
        
        statusStart.setText( status );
        
        statusEnd.setText( vmStats );
    }
    
    /**
     *  Monitors property changed events for Membership Service
     **/
    private class MembershipPropertyListener implements PropertyChangeListener {
        
        /**
         *  {@inheritDoc}
         **/
        public synchronized void propertyChange(PropertyChangeEvent evt) {
            Credential cred = (Credential) evt.getNewValue();
            
            statusKeeper.credential = ( null != cred );
            
            updateStatusString();
        }
    }
    
    /**
     *  Monitors property changed events for Membership Service
     **/
    private class RendezvousEventListener implements RendezvousListener {
        
        /**
         *  {@inheritDoc}
         **/
        public synchronized void rendezvousEvent(RendezvousEvent event) {
            
            int theEventType = event.getType();
            
            if (LOG.isEnabledFor(Level.DEBUG)) {
                LOG.debug("[" + statusKeeper.statusGroup.getPeerGroupName() + "] Processing " + event );
            }
            
            switch (theEventType) {
                
                case RendezvousEvent.RDVCONNECT:
                    statusKeeper.connectedRdv++;
                    break;
                    
                case RendezvousEvent.RDVRECONNECT:
                    statusKeeper.rdvReconnects++;
                    break;
                    
                case RendezvousEvent.RDVFAILED:
                    statusKeeper.rdvFailures++;
                    statusKeeper.connectedRdv--;
                    break;
                    
                case RendezvousEvent.RDVDISCONNECT:
                    statusKeeper.rdvDisconnects++;
                    statusKeeper.connectedRdv--;
                    break;
                    
                case RendezvousEvent.CLIENTCONNECT:
                    statusKeeper.connectedClients++;
                    break;
                    
                case RendezvousEvent.CLIENTRECONNECT:
                    statusKeeper.clientReconnects++;
                    break;
                    
                case RendezvousEvent.CLIENTFAILED:
                    statusKeeper.clientFailures++;
                    statusKeeper.connectedClients--;
                    break;
                    
                case RendezvousEvent.CLIENTDISCONNECT:
                    statusKeeper.clientDisconnects++;
                    statusKeeper.connectedClients--;
                    break;
                    
                case RendezvousEvent.BECAMERDV:
                    statusKeeper.rendezvous = true;
                    break;
                    
                case RendezvousEvent.BECAMEEDGE:
                    statusKeeper.rendezvous = false;
                    break;
                    
                default:
                    if (LOG.isEnabledFor(Level.WARN)) {
                        LOG.warn("[" + statusKeeper.statusGroup.getPeerGroupName() + "] Unexpected RDV event : " + event );
                    }
                    break;
            }
            
            updateStatusString();
        }
    }
    
    /**
     *  Monitors property changed events for Membership Service
     **/
    private class PeerViewEventListener implements PeerViewListener {
        
        /**
         *  {@inheritDoc}
         **/
        public synchronized void peerViewEvent(PeerViewEvent event) {
            
            int theEventType = event.getType();
            
            if (LOG.isEnabledFor(Level.DEBUG)) {
                LOG.debug("[" + statusKeeper.statusGroup.getPeerGroupName() + "] Processing " + event );
            }
            
            switch (theEventType) {
                
                case PeerViewEvent.ADD:
                    statusKeeper.peerview++;
                    break;
                    
                case PeerViewEvent.FAIL:
                    statusKeeper.peerview--;
                    break;
                    
                case PeerViewEvent.REMOVE:
                    statusKeeper.peerview--;
                    break;
                    
                default:
                    if (LOG.isEnabledFor(Level.WARN)) {
                        LOG.warn("[" + statusKeeper.statusGroup.getPeerGroupName() + "] Unexpected PeerView event : " + event );
                    }
                    break;
            }
            
            updateStatusString();
        }
    }
}
