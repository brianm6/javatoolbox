package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.JTextField;
import javax.swing.text.Document;

import toolbox.util.SwingUtil;

/**
 * 
 */
public class JSmartTextField extends JTextField implements AntiAliased
{
    private boolean antialiased_ = SwingUtil.isAntiAliased();
    
    /**
     * 
     */
    public JSmartTextField()
    {
        super();
    }

    /**
     * @param columns
     */
    public JSmartTextField(int columns)
    {
        super(columns);
    }

    /**
     * @param text
     */
    public JSmartTextField(String text)
    {
        super(text);
    }

    /**
     * @param text
     * @param columns
     */
    public JSmartTextField(String text, int columns)
    {
        super(text, columns);
    }

    /**
     * @param doc
     * @param text
     * @param columns
     */
    public JSmartTextField(Document doc, String text, int columns)
    {
        super(doc, text, columns);
    }

    //--------------------------------------------------------------------------
    // AntiAliased Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.AntiAliased#isAntiAlias()
     */
    public boolean isAntiAliased()
    {
        return antialiased_;
    }

    /**
     * @see toolbox.util.ui.AntiAliased#setAntiAlias(boolean)
     */
    public void setAntiAliased(boolean b)
    {
        antialiased_ = b;
    }

    //--------------------------------------------------------------------------
    // Overrides JComponent
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics gc)
    {
        SwingUtil.makeAntiAliased(gc, antialiased_);
        super.paintComponent(gc);
    }

}
