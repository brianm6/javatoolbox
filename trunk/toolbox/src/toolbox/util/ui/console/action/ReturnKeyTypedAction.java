package toolbox.util.ui.console.action;

import java.awt.event.ActionEvent;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import toolbox.util.ui.console.UIConsole;

/**
 * Action for RETURN - send text to console.
 * <P>
 * The text from the first input location to the end (where the RETURN was
 * typed) is send to the caller. The first input location is reset to none.
 */
public class ReturnKeyTypedAction extends TextAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Console.
     */
    private UIConsole console_;

    //----------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------
    
    /**
     * Creates this object with the appropriate identifier.
     * 
     * @param console UI console text area.
     */
    public ReturnKeyTypedAction(UIConsole console)
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
            int start = console_.getConsoleArea().getFirstInputLocation();
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
                    throw new RuntimeException(
                        "Unexpected exception: " + ex.toString());
                }

                console_.send(inputText);

                // Mark data sent
                console_.getConsoleArea().resetFirstInputLocation();
            }
        }
    }
}