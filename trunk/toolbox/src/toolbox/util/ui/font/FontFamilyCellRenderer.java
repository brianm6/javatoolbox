package toolbox.util.ui.font;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.UIManager;

import toolbox.util.FontUtil;
import toolbox.util.ui.ImageCache;
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
    private boolean renderedUsingFont_;
    
    /**
     * Flag to make monospaced fonts in the listbox stand out by setting
     * them to BOLD.
     */
    private boolean monospaceEmphasized_;

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
        
        if (renderedUsingFont_ || monospaceEmphasized_)
        {
            String fontName = value.toString();
            
            Font[] fonts = 
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();

            for (int i = 0; i< fonts.length; i++)
            {
                if (fonts[i].getFamily().equals(fontName)) 
                {
                    if (renderedUsingFont_)
                        setFont(fonts[i].deriveFont((float)
                            UIManager.getFont("List.font").getSize()));
                        
                    if (monospaceEmphasized_ && FontUtil.isMonospaced(fonts[i]))
                    {
                        //setFont(getFont().deriveFont(Font.BOLD));
                        //setBackground(Colors.azure);
                        //setBorder(new EtchedBorder(EtchedBorder.RAISED));
                        
                        // TODO: Change lock image to someting else
                        setIcon(ImageCache.getIcon(ImageCache.IMAGE_LOCK));
                        setHorizontalTextPosition(JLabel.LEFT); 
                    }
                    
                    break;
                }
            }
        }
                
        return this;
    }

    
    //----------------------------------------------------------------------
    // Public
    //----------------------------------------------------------------------
    
    /**
     * Returns true if a font family cell is rendered using the font name 
     * occupying that cell. False otherwise.
     *   
     * @return boolean
     */
    public boolean isRenderedUsingFont()
    {
        return renderedUsingFont_;
    }

    
    /**
     * Sets the flag to render a font family cell using the font name occupying
     * that cell.
     * 
     * @param renderedUsingFont True to use the font, false to use the default
     *        font.
     */
    public void setRenderedUsingFont(boolean renderedUsingFont)
    {
        renderedUsingFont_ = renderedUsingFont;
    }

    
    /**
     * Returns true if monospaced fonts are emphasized in the font family list
     * box by being made bold. False otherwise.
     * 
     * @return booelean
     */
    public boolean isMonospacedEmphasized()
    {
        return monospaceEmphasized_;
    }


    /**
     * Sets the flag to emphasize monospaced fonts in the font family list box
     * by making them bold. 
     * 
     * @param monospaceEmphasized True to emphasize monospaced fonts, false
     *        otherwise.
     */
    public void setMonospacedEmphasized(boolean monospaceEmphasized)
    {
        monospaceEmphasized_ = monospaceEmphasized;
    }
}