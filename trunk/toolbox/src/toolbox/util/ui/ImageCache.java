package toolbox.util.ui;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import toolbox.util.ResourceUtil;

/**
 * Caches images!
 */
public class ImageCache
{
    private static final String ROOT = "/toolbox/util/ui/images/";
    
    public static final String IMAGE_TOOLBOX = ROOT + "Toolbox.gif";
    public static final String IMAGE_COPY    = ROOT + "Copy.gif";
    public static final String IMAGE_PASTE   = ROOT + "Paste.gif";
    public static final String IMAGE_SAVE    = ROOT + "Save.gif";
    public static final String IMAGE_SAVEAS  = ROOT + "SaveAs.gif";
    public static final String IMAGE_FIND    = ROOT + "Search.gif";
    public static final String IMAGE_DELETE  = ROOT + "Delete.gif";
    public static final String IMAGE_CROSS   = ROOT + "Cross.gif";
    public static final String IMAGE_TRIANGLE= ROOT + "Triangle.gif";
    public static final String IMAGE_REFRESH = ROOT + "Refresh.gif";
    
    public static final String IMAGE_SORT_ASCENDING = ROOT+"SortAscending.gif";
    public static final String IMAGE_SORT_DESCENDING= ROOT+"SortDescending.gif";
    
    public static final String IMAGE_TREE_OPEN   = ROOT + "TreeOpen.gif";
    public static final String IMAGE_TREE_CLOSED = ROOT + "TreeClosed.gif";
    public static final String IMAGE_HARD_DRIVE  = ROOT + "HardDrive.gif";
    
    
    /**
     * Hashmap for cached icons
     */
    private static final Map iconCache_ = new HashMap();
    
    /**
     * Hashmap for cached images
     */
    private static final Map imageCache_ = new HashMap();
    
    //--------------------------------------------------------------------------
    // Public Static
    //--------------------------------------------------------------------------
    
    /**
     * Retrieve icon at the specified URL
     * 
     * @param  url  URL to load icon from
     * @return Icon if a valid url, null otherwise
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
     * Retrieve image at the specified URL
     * 
     * @param  url  URL to load image from
     * @return Image if a valid url, null otherwise
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
     * Flushes the cache
     */
    public static void flush()
    {
        iconCache_.clear();
        imageCache_.clear();    
    }
}