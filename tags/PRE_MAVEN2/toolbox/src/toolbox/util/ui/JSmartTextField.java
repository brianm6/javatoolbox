package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.JTextField;
import javax.swing.text.Document;

import toolbox.util.SwingUtil;

/**
 * JSmartTextField adds the following behavior.
 * <p>
 * <ul>
 *   <li>Antialiased text
 *   <li>Right mouse click popup menu with cut/copy/paste
 * </ul>
 */
public class JSmartTextField extends JTextField implements AntiAliased
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Antialiased flag.
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JSmartTextField.
     */
    public JSmartTextField()
    {
    }


    /**
     * Creates a JSmartTextField.
     *
     * @param columns Number of columns.
     */
    public JSmartTextField(int columns)
    {
        super(columns);
        init();
    }


    /**
     * Creates a JSmartTextField.
     *
     * @param text Field text.
     */
    public JSmartTextField(String text)
    {
        super(text);
        init();
    }


    /**
     * Creates a JSmartTextField.
     *
     * @param text Field text.
     * @param columns Number of columns.
     */
    public JSmartTextField(String text, int columns)
    {
        super(text, columns);
        init();
    }


    /**
     * Creates a JSmartTextField.
     *
     * @param doc Document.
     * @param text Field text.
     * @param columns Number of columns.
     */
    public JSmartTextField(Document doc, String text, int columns)
    {
        super(doc, text, columns);
        init();
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Initialzies the text area by adding a popup menu with commonly used RMB
     * accessible operations.
     */
    protected void init()
    {
        new JTextComponentPopupMenu(this);
    }

    //--------------------------------------------------------------------------
    // AntiAliased Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.ui.AntiAliased#isAntiAliased()
     */
    public boolean isAntiAliased()
    {
        return antiAliased_;
    }


    /**
     * @see toolbox.util.ui.AntiAliased#setAntiAliased(boolean)
     */
    public void setAntiAliased(boolean b)
    {
        antiAliased_ = b;
    }

    //--------------------------------------------------------------------------
    // Overrides JComponent
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics gc)
    {
        SwingUtil.makeAntiAliased(gc, isAntiAliased());
        super.paintComponent(gc);
    }
}