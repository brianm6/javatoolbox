package toolbox.util.ui.font;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.JList;
import javax.swing.UIManager;

import toolbox.util.FontUtil;
import toolbox.util.ui.list.SmartListCellRenderer;

/**
 * FontFamilyCellRenderer is the custom cell renderer for the listbox
 * containing font family names.
 * 
 * @see toolbox.util.ui.font.JFontChooser
 */
public class FontFamilyCellRenderer extends SmartListCellRenderer
{
    //----------------------------------------------------------------------
    // Fields
    //----------------------------------------------------------------------

    /**
     * Flag to use the actual font to render the cell in the listbox.
     */
    private boolean renderUsingFont_;
    
    /**
     * Flag to make monospaced fonts in the listbox stand out by setting
     * them to BOLD.
     */
    private boolean showMonospaced_;

    //--------------------------------------------------------------------------
    // Overrides DefaultListCellRenderer
    //--------------------------------------------------------------------------
    
    /**
     * Specializes the rendering of the cell by optionally using the font that
     * the cell represents and making monospaced fonts bold.
     * 
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(
     *      javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(
        JList list, 
        Object value,
        int index, 
        boolean isSelected, 
        boolean cellHasFocus)
    {
        // Let the default cell renderer do its thing...
        super.getListCellRendererComponent(
            list,
            value,
            index,
            isSelected,
            cellHasFocus);
        
        if (renderUsingFont_ || showMonospaced_)
        {
            String fontName = value.toString();
            
            Font[] fonts = 
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();

            for (int i = 0; i< fonts.length; i++)
            {
                if (fonts[i].getFamily().equals(fontName)) 
                {
                    if (renderUsingFont_)
                        setFont(fonts[i].deriveFont(
                            UIManager.getFont("List.font").getSize()));
                        
                    if (showMonospaced_ && FontUtil.isMonospaced(fonts[i]))
                        setFont(getFont().deriveFont(Font.BOLD));
                    
                    break;
                }
            }
        }
                
        return this;
    }

    
    //----------------------------------------------------------------------
    // Public
    //----------------------------------------------------------------------
    
    public boolean isRenderUsingFont()
    {
        return renderUsingFont_;
    }


    public void setRenderUsingFont(boolean renderUsingFont)
    {
        renderUsingFont_ = renderUsingFont;
    }


    public boolean isShowMonospaced()
    {
        return showMonospaced_;
    }


    public void setShowMonospaced(boolean showMonospaced)
    {
        showMonospaced_ = showMonospaced;
    }
}