package toolbox.util.ui.layout;

import java.awt.Rectangle;

/**
 * Alignment. 
 */
public class Alignment implements Direction
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /** 
     * None 
     */
    public static final int FILL_NONE = 0;
    
    /** 
     * Horiz 
     */
    public static final int FILL_HORIZONTAL = 1;
    
    /** 
     * Vert 
     */
    public static final int FILL_VERTICAL = 2;
    
    /** 
     * Both 
     */
    public static final int FILL_BOTH = 3;

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Aligns in a cell.
     * 
     * @param r Rectangle
     * @param cell Cell
     * @param alignment Alignment
     * @param fill Fill
     */
    public static void alignInCell(
        Rectangle r,
        Rectangle cell,
        int alignment,
        int fill)
    {
        r.x = cell.x;
        r.y = cell.y;

        // Horizontal fill 
        switch (fill)
        {
            case FILL_BOTH :
            case FILL_HORIZONTAL :
                r.width = cell.width;
                break;
        }

        // Vertical fill 
        switch (fill)
        {
            case FILL_BOTH :
            case FILL_VERTICAL :
                r.height = cell.height;
                break;
        }

        // Horizontal alignment 
        switch (alignment)
        {
            case CENTER :
            case NORTH :
            case SOUTH :
                r.x += (cell.width - r.width) / 2;
                break;
            case WEST :
            case NORTHWEST :
            case SOUTHWEST :
                break;
            case EAST :
            case NORTHEAST :
            case SOUTHEAST :
                r.x += cell.width - r.width;
                break;
        }

        // Vertical alignment 
        switch (alignment)
        {
            case CENTER :
            case WEST :
            case EAST :
                r.y += (cell.height - r.height) / 2;
                break;
            case NORTH :
            case NORTHWEST :
            case NORTHEAST :
                break;
            case SOUTH :
            case SOUTHWEST :
            case SOUTHEAST :
                r.y += cell.height - r.height;
                break;
        }
    }
}