package toolbox.dirmon;

import java.awt.Window;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import snoozesoft.systray4j.SysTrayMenu;
import snoozesoft.systray4j.SysTrayMenuEvent;
import snoozesoft.systray4j.SysTrayMenuIcon;
import snoozesoft.systray4j.SysTrayMenuItem;
import snoozesoft.systray4j.SysTrayMenuListener;

import toolbox.util.ResourceUtil;
import toolbox.util.dirmon.DirectoryMonitorEvent;
import toolbox.util.dirmon.IDirectoryMonitorListener;

/**
 * Manages the system tray component of the directory monitor.
 * <ul>
 *   <li>Updates the system tray tooltip with the latest directory monitor event
 *       that was received.
 *   <li>Updates the system tray icon to dirty when a new event is received.
 *   <li>Resets the system tray icon to dormant when the icon is clicked on.
 * </ul>
 */
class SystemTrayUpdater 
    implements 
        IDirectoryMonitorListener, 
        SysTrayMenuListener {

    private static final Logger logger_ = 
        Logger.getLogger(SystemTrayUpdater.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    private static SysTrayMenuIcon ICON_DIRMON;
    
    private static SysTrayMenuIcon ICON_DIRMON_ALERT; 
            
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    private Window parent_;
    
    private SysTrayMenu menu_;
    
    //--------------------------------------------------------------------------
    // Static Initializers
    //--------------------------------------------------------------------------
    
    static {
        try {
            ICON_DIRMON = new SysTrayMenuIcon(
                ResourceUtil.getClassResourceURL(
                    "/toolbox/dirmon/DirMon.ico"));
            
            ICON_DIRMON_ALERT = new SysTrayMenuIcon(
                ResourceUtil.getClassResourceURL(
                    "/toolbox/dirmon/DirMonAlert.ico"));
        }
        catch (IOException e) {
            logger_.error(e);
        }
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    SystemTrayUpdater(Window parent) {
        parent_ = parent;
        
        SysTrayMenuItem exitMenuItem = new SysTrayMenuItem("Exit", "exit");
        SysTrayMenuItem aboutMenuItem = new SysTrayMenuItem("About..", "about");
        
        exitMenuItem.addSysTrayMenuListener(this);
        aboutMenuItem.addSysTrayMenuListener(this);
        ICON_DIRMON.addSysTrayMenuListener(this);
        ICON_DIRMON_ALERT.addSysTrayMenuListener(this);

        // create the main menu
        menu_ = new SysTrayMenu(ICON_DIRMON, "Directory Monitor");

        // insert items
        menu_.addItem(exitMenuItem);
        menu_.addSeparator();
        menu_.addItem(aboutMenuItem);
    }

    //--------------------------------------------------------------------------
    // IDirectoryMonitorListener Interface
    //--------------------------------------------------------------------------
    
    public void directoryActivity(DirectoryMonitorEvent event) throws Exception {
        
        StringBuffer shortMsg = new StringBuffer();
        
        switch (event.getEventType()) {
        
            case DirectoryMonitorEvent.TYPE_CHANGED :
                
                shortMsg.append("Modified: ");
                shortMsg.append(FilenameUtils.getName(
                    event.getAfterSnapshot().getAbsolutePath()));
                
                break;
                
            case DirectoryMonitorEvent.TYPE_CREATED :
                
                shortMsg.append("Created: ");
                shortMsg.append(FilenameUtils.getName(
                    event.getAfterSnapshot().getAbsolutePath()));
                
                break;

            case DirectoryMonitorEvent.TYPE_DELETED :
                
                shortMsg.append("Deleted: ");
                shortMsg.append(FilenameUtils.getName(
                    event.getBeforeSnapshot().getAbsolutePath()));
                
                break;

            default:
                throw new IllegalArgumentException(
                    "unrecognized event type: " 
                    + event.getEventType());
        }
        
        menu_.setToolTip(shortMsg.toString());
        menu_.setIcon(ICON_DIRMON_ALERT);
    }
    
    // -------------------------------------------------------------------------
    // SysTrayMenuListener Interface
    // -------------------------------------------------------------------------

    /*
     * @see snoozesoft.systray4j.SysTrayMenuListener#menuItemSelected(snoozesoft.systray4j.SysTrayMenuEvent)
     */
    public void menuItemSelected(SysTrayMenuEvent e) {

        if (e.getActionCommand().equals("exit")) {
            System.exit(0);
        }
        else if (e.getActionCommand().equals("about")) {
            JOptionPane.showMessageDialog(parent_, "Directory Monitor v1.0");
        }
        else {
            JOptionPane.showMessageDialog(parent_, e.getActionCommand());
        }
    }

    
    /*
     * @see snoozesoft.systray4j.SysTrayMenuListener#iconLeftClicked(snoozesoft.systray4j.SysTrayMenuEvent)
     */
    public void iconLeftClicked(SysTrayMenuEvent e) {
        parent_.setVisible(!parent_.isVisible());
        if (parent_.isVisible())
            parent_.toFront();
        menu_.setIcon(ICON_DIRMON);
    }


    /*
     * @see snoozesoft.systray4j.SysTrayMenuListener#iconLeftDoubleClicked(snoozesoft.systray4j.SysTrayMenuEvent)
     */
    public void iconLeftDoubleClicked(SysTrayMenuEvent e) {
        JOptionPane.showMessageDialog(
            parent_, "You may prefer double-clicking the icon.");
    }    
}