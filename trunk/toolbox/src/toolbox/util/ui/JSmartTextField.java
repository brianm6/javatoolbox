package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.JTextField;
import javax.swing.text.Document;

import toolbox.util.SwingUtil;

/**
 * JSmartTextField adds the following behavior.
 * <p>
 * <ul>
 *   <li>Support for antialised text
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
    }


    /**
     * Creates a JSmartTextField.
     * 
     * @param text Field text.
     */
    public JSmartTextField(String text)
    {
        super(text);
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