package toolbox.util;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.TextAction;

/**
 * Swing Utility Class
 */
public class SwingUtil
{
    /** Monospaced font **/
    private static Font monofont_;
    
    /**
     * Prevent construction
     */
    private SwingUtil()
    {
    }

    /**
     * Sets the size of a window to a given percentage of the users desktop
     *
     * @param  w               Window to size
     * @param  percentWidth    Percent width from 1 to 100
     * @param  percentHeight   Percent height from 1 to 100
     */
    public static void setWindowSizeAsPercentage(Window w, int percentWidth,
        int percentHeight)
    {

        Dimension desktopSize = w.getToolkit().getScreenSize();

        Dimension windowSize =
            new Dimension(
                (desktopSize.width * percentWidth) / 100,
                (desktopSize.height * percentHeight) / 100);

        w.setSize(windowSize);
    }


    /**
     * Moves the window to the center of the screen
     * 
     * @param  w  Window to move
     */
    public static void centerWindow(Window w)
    {
        Dimension d = w.getSize();
        Dimension size = w.getToolkit().getScreenSize();

        int left = size.width / 2 - d.width / 2;
        int top = size.height / 2 - d.height / 2;

        w.setLocation(left, top);
    }


    /**
     * Sets the cursor to the default cursor on the given component
     * 
     * @param  c  Component
     */
    public static void setDefaultCursor(Component c)
    {
        c.setCursor(Cursor.getDefaultCursor());
    }


    /**
     * Sets the cursor to the wait cursor on the given component
     * 
     * @param  c  Component
     */
    public static void setWaitCursor(Component c)
    {
        c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    /**
     * Returns preferred monospaced font
     * 
     * @return Monospaced font
     */
    public static Font getMonospacedFont()
    {
        String favoredFont = "Lucida Console";
        String backupFont  = "monospaced";
        
        if (monofont_ == null)
        {
            GraphicsEnvironment ge = 
                GraphicsEnvironment.getLocalGraphicsEnvironment();
                
            String[] familyNames = ge.getAvailableFontFamilyNames();
            Map attribMap = new HashMap();
            
            if (ArrayUtil.contains(familyNames, favoredFont))
            {
                attribMap.put(TextAttribute.FAMILY, favoredFont);
                attribMap.put(TextAttribute.FONT, favoredFont);
            }
            else
            {
                attribMap.put(TextAttribute.FAMILY, backupFont);
                attribMap.put(TextAttribute.FONT, backupFont);        
            }

            monofont_ = new Font(attribMap);            
        }
        
        return monofont_;               
    }
    
}