package toolbox.util;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import net.sourceforge.mlf.metouia.MetouiaLookAndFeel;
import org.apache.log4j.Logger;

/**
 * Swing Utility Class
 */
public class SwingUtil
{
    /** Logger **/
    private static final Logger logger_ =
        Logger.getLogger(SwingUtil.class);
    
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

    //--------------------------------------------------------------------------
    // Window Stuff
    //--------------------------------------------------------------------------
    
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
     * Centers a child window relative to its parent window
     * 
     * @param  parent  Parent window
     * @param  child   Child window
     */
    public static void centerWindow(Window parent, Window child)
    {
        Dimension parentSize = parent.getSize();
        Dimension childSize = child.getSize();
    
        Point loc = parent.getLocation();
        child.setLocation(
            (parentSize.width - childSize.width) / 2 + loc.x,
            (parentSize.height - childSize.height) / 2 + loc.y);
    }
   
    //--------------------------------------------------------------------------
    // Cursor Stuff
    //--------------------------------------------------------------------------
       
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
    
    //--------------------------------------------------------------------------
    // Font Stuff
    //--------------------------------------------------------------------------
        
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
    
    //--------------------------------------------------------------------------
    // Look and Feel Stuff
    //--------------------------------------------------------------------------
    
    /**
     * Sets the Look and Feel to Metal
     * 
     * @throws Exception on error
     */    
    public static void setMetalLAF() throws Exception
    {
        UIManager.setLookAndFeel(
            "javax.swing.plaf.metal.MetalLookAndFeel");
            
        //SwingUtilities.updateComponentTreeUI(frame);
    }
    
    
    /**
     * Sets the Look and Feel to Windows
     * 
     * @throws Exception on error
     */
    public static void setWindowsLAF() throws Exception
    {
        UIManager.setLookAndFeel(
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    }
 
 
    /**
     * Sets the Look and Feel to Motif/CDE
     * 
     * @throws Exception on error
     */   
    public static void setMotifLAF() throws Exception
    {
        UIManager.setLookAndFeel(
            "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    }
 
 
    /**
     * Sets the Look and Feel to Metouia
     * 
     * @throws Exception on error
     */
    public static void setMetouiaLAF() throws Exception
    { 
        UIManager.setLookAndFeel(new MetouiaLookAndFeel());
    }


    /**
     * Sets the preferred Look and Feel
     * 
     * @throws Exception on error
     */
    public static void setPreferredLAF() throws Exception
    { 
        setMetouiaLAF();
    }
    
    //--------------------------------------------------------------------------
    // Internal Frame Stuff
    //--------------------------------------------------------------------------
    
    /** 
     * Tiles internal frames upon the desktop. 
     * 
     * @param  desktop  Desktop on which to tile windows
     * 
     * <pre>
     * 
     * Based upon the following tiling algorithm:
     * 
     * - take the sqroot of the total frames rounded down, that gives 
     *   the number of columns.
     *
     * - divide the total frames by the # of columns to get the # 
     *   of rows in each column, and any remainder is distributed 
     *   amongst the remaining rows from right to left)
     *
     * eg)
     *     1  frame,  remainder 0, 1 row
     *     2  frames, remainder 0, 2 rows
     *     3  frames, remainder 0, 3 rows
     *     4  frames, remainder 0, 2 rows x 2 columns
     *     5  frames, remainder 1, 2 rows in column I, 
     *                             3 rows in column II
     *     10 frames, remainder 1, 3 rows in column I, 
     *                             3 rows in column II, 
     *                             4 rows in column III
     *     16 frames, 4 rows x 4 columns
     * 
     * Pseudocode:
     * 
     *     while (frames) 
     *     { 
     *         numCols = (int)sqrt(totalFrames);
     *         numRows = totalFrames / numCols;
     *         remainder = totalFrames % numCols;
     * 
     *         if ((numCols-curCol) <= remainder) 
     *             numRows++; // add an extra row for this column
     *     }
     * 
     * </pre>
     */
    public static void tile(JDesktopPane desktop)
    {
        Rectangle viewP = desktop.getBounds();
        int totalNonIconFrames = 0;
        JInternalFrame[] frames = desktop.getAllFrames();
        
        for (int i = 0; i < frames.length; i++)
        {
            if (!frames[i].isIcon())
            { 
                // don't include iconified frames...
                totalNonIconFrames++;
            }
        }

        int curCol = 0;
        int curRow = 0;
        int i = 0;

        if (totalNonIconFrames > 0)
        {
            // compute number of columns and rows then tile the frames
            int numCols = (int) Math.sqrt(totalNonIconFrames);

            int frameWidth = viewP.width / numCols;

            for (curCol = 0; curCol < numCols; curCol++)
            {
                int numRows = totalNonIconFrames / numCols;
                int remainder = totalNonIconFrames % numCols;

                if ((numCols - curCol) <= remainder)
                    numRows++; // add an extra row for this guy

                int frameHeight = viewP.height / numRows;

                for (curRow = 0; curRow < numRows; curRow++)
                {
                    while (frames[i].isIcon())
                    { 
                        // find the next visible frame
                        i++;
                    }

                    frames[i].setBounds(
                        curCol * frameWidth,
                        curRow * frameHeight,
                        frameWidth,
                        frameHeight);

                    i++;
                }
            }
        }
    }
    
    
    //--------------------------------------------------------------------------
    //  Widget/Layout Stuff
    //--------------------------------------------------------------------------

    /**
     * Wraps a component in a JPanel using a flowlayout
     *
     * @param  component  Component to wrap
     * @return JPanel 
     */    
    public static JPanel wrap(JComponent component)
    {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(component);
        return panel;    
    }
}
