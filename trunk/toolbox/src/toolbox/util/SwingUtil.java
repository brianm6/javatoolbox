package toolbox.util;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.font.TextAttribute;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;

import net.sourceforge.mlf.metouia.MetouiaLookAndFeel;
import org.apache.log4j.Category;

/**
 * Swing Utility Class
 */
public class SwingUtil
{
    /** Logger **/
    private static final Category logger_ =
        Category.getInstance(SwingUtil.class);
    
    /** Monospaced font **/
    private static Font monofont_;
    
    /** Serif font **/
    private static Font serifFont_;
    
    
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
    public static Font getPreferredMonoFont()
    {
        String method = "[mono  ] ";
        
        String favoredFont;
        
        if (Platform.isUnix())
            favoredFont = "LucidaSansTypewriter";
        else
            favoredFont = "Lucida Console";
            
        String backupFont  = "monospaced";
        
        if (monofont_ == null)
        {
            logger_.debug(method + "Favored Font = " + favoredFont);
            
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

            attribMap.put(TextAttribute.SIZE, new Float(12));
            monofont_ = new Font(attribMap);
        }
        
        return monofont_;               
    }


    /**
     * Returns preferred variable text font
     * 
     * @return Preferred variable text font
     */
    public static Font getPreferredSerifFont()
    {
        String favoredFont = "Verdana";
        String backupFont  = "serif";
        
        if (serifFont_ == null)
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

            serifFont_ = new Font(attribMap);            
        }
        
        return serifFont_;               
    }
    
    
    /**
     * Sets the Look and Feel to Metal
     */    
    public static void setMetalLAF() throws Exception
    {
        UIManager.setLookAndFeel(
            "javax.swing.plaf.metal.MetalLookAndFeel");
            
        //SwingUtilities.updateComponentTreeUI(frame);
    }
    
    
    /**
     * Sets the Look and Feel to Windows
     */
    public static void setWindowsLAF() throws Exception
    {
        UIManager.setLookAndFeel(
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    }
 
 
    /**
     * Sets the Look and Feel to Motif/CDE
     */   
    public static void setMotifLAF() throws Exception
    {
        UIManager.setLookAndFeel(
            "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    }
 
 
    /**
     * Sets the Look and Feel to Metouia
     */
    public static void setMetouiaLAF() throws Exception
    { 
        UIManager.setLookAndFeel(new MetouiaLookAndFeel());
    }


    /**
     * Sets the preferred Look and Feel
     */
    public static void setPreferredLAF() throws Exception
    { 
        setMetouiaLAF();
    }
    
    
    /**
     * Tiles windows on a desktop
     *
     * @param  desktop  JDesktop
     */
    public static void tile(JDesktopPane desktop)
    {
        String method = "[tile  ] ";
        
        // How many frames do we have?
        JInternalFrame[] allframes = desktop.getAllFrames();
        int count = allframes.length;
        
        if (count == 0)
            return;

        // Determine the necessary grid size
        int sqrt = (int) Math.sqrt(count);
        int rows = sqrt;
        int cols = sqrt;
        
        if (rows * cols < count)
        {
            cols++;
            
            if (rows * cols < count)
                rows++;
        }

        // Define some initial values for size & location
        Dimension size = desktop.getSize();
        logger_.debug(method + "Desktop size: " + size);
        
        int w = size.width / cols;
        int h = size.height / rows;
        int x = 0;
        int y = 0;

        // Iterate over the frames, deiconifying any iconified frames and then
        // relocating & resizing each
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols && ((i * cols) + j < count); j++)
            {
                JInternalFrame f = allframes[(i * cols) + j];

                if ((f.isClosed() == false) && (f.isIcon() == true))
                {
                    try
                    {
                        f.setIcon(false);
                    }
                    catch (PropertyVetoException ex)
                    {
                    }
                }

                desktop.getDesktopManager().resizeFrame(f, x, y, w, h);
                x += w;
            }
            
            y += h; // start the next row
            x = 0;
        }
    }
}