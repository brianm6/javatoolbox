package toolbox.util.ui.explorer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import toolbox.util.DateTimeUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.explorer.action.RefreshAction;
import toolbox.util.ui.statusbar.JStatusBar;

/**
 * Simple file information status bar. Shows the following information:
 * <ul>
 *  <li>File size
 *  <li>File last modified date
 *  <li>Read/write attribute
 * </ul>
 * <p>
 * Also contains a button to refresh the contents of the file explorer.
 * 
 * @see toolbox.util.ui.explorer.JFileExplorer
 */    
public class InfoBar extends JStatusBar
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Formatter for the file size.
     */
    private static final DecimalFormat sizeFormatter_ = new DecimalFormat();

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Reference to the parent file explorer.
     */
    private final JFileExplorer explorer_;
    
    /**
     * Shows the size of the file in bytes formatted for the current locale.
     */
	private JLabel sizeLabel_;
    
    /**
     * Shows the last modified timestamp of the file.
     */
    private JLabel modifiedLabel_;
    
    /**
     * Shows an R if the file is readonly and/or a W if the file is writable. 
     */
    private JLabel attribLabel_;
    
    /**
     * Contains an icon the user can click on to refresh the file explorer.
     */
    private JLabel refreshLabel_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an InfoBar.
     * 
     * @param explorer File explorer to associate this infobar with.
     */
    InfoBar(final JFileExplorer explorer)
    {
		explorer_ = explorer;
        sizeLabel_ = new JSmartLabel();
        sizeLabel_.setHorizontalAlignment(SwingConstants.CENTER);
        
        modifiedLabel_ = new JSmartLabel();
        modifiedLabel_.setHorizontalAlignment(SwingConstants.CENTER);
        
        attribLabel_ = new JSmartLabel();

        refreshLabel_ = 
            new JSmartLabel(ImageCache.getIcon(ImageCache.IMAGE_REFRESH));
        
        refreshLabel_.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                new RefreshAction(explorer_).refresh();
            }
        });
        
        addStatusComponent(sizeLabel_, false);
        addStatusComponent(modifiedLabel_, true);
        addStatusComponent(attribLabel_, false);
        addStatusComponent(refreshLabel_, false);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Updates the bar to show information for the given file.
     * 
     * @param file File to show info for.
     */
    public void showInfo(File file)
    {
        String size = sizeFormatter_.format(file.length()) + " bytes";
        sizeLabel_.setText(size);
        sizeLabel_.setToolTipText(size);
        
        String date = DateTimeUtil.format(new Date(file.lastModified()));
        modifiedLabel_.setText(date);
        modifiedLabel_.setToolTipText(date);
            
        attribLabel_.setText(
            (file.canRead() ? "R" : "") +
            (file.canWrite() ? "W" : ""));
    }
    
    //--------------------------------------------------------------------------
    // InfoBar
    //--------------------------------------------------------------------------
    
    /** 
     * Updates the infobar with the currently selected file.
     */
    class InfoBarUpdater extends FileExplorerAdapter
    {
        /**
         * @see toolbox.util.ui.explorer.FileExplorerListener#fileSelected(
         *      java.lang.String)
         */
        public void fileSelected(String file)
        {
            showInfo(new File(file));
        }
    }
}