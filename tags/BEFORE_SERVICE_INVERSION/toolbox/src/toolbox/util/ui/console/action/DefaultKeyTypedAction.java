package toolbox.util.ui.console.action;

import java.awt.event.ActionEvent;

import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import toolbox.util.ui.console.UIConsoleArea;

/**
 * Default handler for keystrokes as they are typed in.
 * 
 * @see toolbox.util.ui.console.UIConsoleArea
 */
public class DefaultKeyTypedAction extends TextAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Associated console.
     */
    private UIConsoleArea consoleArea_;

    //----------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------
    
    /**
     * Creates this object with the appropriate identifier.
     * 
     * @param consoleArea UI console text area.
     */
    public DefaultKeyTypedAction(UIConsoleArea consoleArea)
    {
        super(DefaultEditorKit.defaultKeyTypedAction);
        consoleArea_ = consoleArea;
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
            String content = e.getActionCommand();
            int mod = e.getModifiers();

            if ((content != null) && (content.length() > 0)
                && ((mod & ActionEvent.ALT_MASK) == 0))
            {
                char c = content.charAt(0);
                // if ((c >= 0x20) && (c != 0x7F)) { Old

                if (!Character.isISOControl(c))
                {
                    // Printable character - move at end
                    Document doc = target.getDocument();
                    int dot = doc.getLength();
                    target.setCaretPosition(dot);

                    // Save location of first character typed if needed
                    consoleArea_.setFirstInputLocation(dot);

                    // Insert content
                    target.replaceSelection(content);
                }
            }
        }
    }
}