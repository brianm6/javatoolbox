package toolbox.util.ui;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import toolbox.util.ResourceUtil;

/**
 * ImageCache is a convenience class to load graphics resources as icons or
 * images.
 */
public class ImageCache
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    /**
     * Directory where all the "known" images are located.
     */
    private static final String ROOT = "/toolbox/util/ui/images/";

    // Images
    public static final String IMAGE_BAR_CHART     = ROOT + "BarChart.png";
    public static final String IMAGE_BRACES        = ROOT + "Braces.png";
    public static final String IMAGE_CLEAR         = ROOT + "Clear.png";
    public static final String IMAGE_COLUMNS       = ROOT + "Columns.png";
    public static final String IMAGE_CONFIG        = ROOT + "Config.png";
    public static final String IMAGE_COPY          = ROOT + "Copy.png";
    public static final String IMAGE_CROSS         = ROOT + "Cross.png";
    public static final String IMAGE_DATASOURCE    = ROOT + "Datasource.png";
    public static final String IMAGE_DELETE        = ROOT + "Delete.png";
    public static final String IMAGE_DUKE          = ROOT + "Duke.png";
    public static final String IMAGE_FIND          = ROOT + "Search.png";
    public static final String IMAGE_FORWARD       = ROOT + "Forward.png";
    public static final String IMAGE_FUNNEL        = ROOT + "Funnel.png";
    public static final String IMAGE_HARD_DRIVE    = ROOT + "HardDrive.png";
    public static final String IMAGE_INFO          = ROOT + "Info.png";
    public static final String IMAGE_LINEWRAP      = ROOT + "Linewrap.png";
    public static final String IMAGE_LOCK          = ROOT + "Lock.png";
    public static final String IMAGE_PASTE         = ROOT + "Paste.png";
    public static final String IMAGE_PAUSE         = ROOT + "Pause.png";
    public static final String IMAGE_PIE_CHART     = ROOT + "PieChart.png";
    public static final String IMAGE_PLAY          = ROOT + "Play.png";
    public static final String IMAGE_QUESTION_MARK = ROOT + "QuestionMark.png";
    public static final String IMAGE_REFRESH       = ROOT + "Refresh.png";
    public static final String IMAGE_REVERSE       = ROOT + "Reverse.png";    
    public static final String IMAGE_SAVE          = ROOT + "Save.png";
    public static final String IMAGE_SAVEAS        = ROOT + "SaveAs.png";
    public static final String IMAGE_SPANNER       = ROOT + "Spanner.png";
    public static final String IMAGE_STOP          = ROOT + "Stop.png";
    public static final String IMAGE_SWAP_PANES    = ROOT + "SwapPanes.png";
    public static final String IMAGE_TABLES        = ROOT + "Tables.png";
    public static final String IMAGE_TOOLBOX       = ROOT + "Toolbox.png";
    public static final String IMAGE_TRASHCAN      = ROOT + "TrashCan.png";
    public static final String IMAGE_TREE_CLOSED   = ROOT + "TreeClosed.png";
    public static final String IMAGE_TREE_OPEN     = ROOT + "TreeOpen.png";
    public static final String IMAGE_TRIANGLE      = ROOT + "Triangle.png";
    public static final String IMAGE_WARNING       = ROOT + "Warning.png";

    public static final String
        IMAGE_DOUBLE_ARROW_DOWN = ROOT + "DoubleArrowDown.png";

    public static final String
        IMAGE_DOUBLE_ARROW_UP = ROOT + "DoubleArrowUp.png";

    public static final String
        IMAGE_SORT_ASCENDING = ROOT + "SortAscending.png";

    public static final String
        IMAGE_SORT_DESCENDING = ROOT + "SortDescending.png";

    /**
     * Map for cached icons.
     */
    private static final Map iconCache_ = new HashMap();

    /**
     * Map for cached images.
     */
    private static final Map imageCache_ = new HashMap();

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Retrieves the icon at the given URL.
     *
     * @param url URL to load icon from.
     * @return Icon if a valid url, null otherwise.
     */
    public static Icon getIcon(String url)
    {
        Icon icon = (Icon) iconCache_.get(url);

        if (icon == null)
        {
            icon = ResourceUtil.getResourceAsIcon(url);

            if (icon != null)
                iconCache_.put(url, icon);
        }

        return icon;
    }


    /**
     * Retrieve the image at the given URL.
     *
     * @param url URL to load image from.
     * @return Image if a valid url, null otherwise.
     */
    public static Image getImage(String url)
    {
        Image image = (Image) imageCache_.get(url);

        if (image == null)
        {
            image = ResourceUtil.getResourceAsImage(url);

            if (image != null)
                iconCache_.put(url, image);
        }

        return image;
    }


    /**
     * Flushes the cache.
     */
    public static void flush()
    {
        iconCache_.clear();
        imageCache_.clear();
    }
}