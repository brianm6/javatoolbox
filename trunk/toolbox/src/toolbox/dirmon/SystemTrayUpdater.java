package toolbox.dirmon;

import java.awt.Window;
import java.awt.event.WindowFocusListener;
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
    
    /**
     * System tray icon when there are no alerts.
     */
    private static SysTrayMenuIcon ICON_DIRMON;
    
    /**
     * System tray icon when there are alerts.
     */
    private static SysTrayMenuIcon ICON_DIRMON_ALERT; 
            
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Window associated with the SystemTray.
     */
    private Window parent_;
    
    /**
     * Manages the platform native system tray icon and submenus.
     */
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

    /**
     * Creates a SystemTrayUpdater.
     * 
     * @param parent Parent window.
     */
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
        
        parent_.addWindowFocusListener(new WindowFocusListener() {
            
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                //logger_.debug("gained focus");
                resetAlert();
            };
            
            public void windowLostFocus(java.awt.event.WindowEvent e) {
                //logger_.debug("lost focus");
                resetAlert();
            };
        });

//        logger_.debug("Adding window listener...");
//        
//        parent_.addWindowStateListener(new WindowStateListener() {
//            
//            public void windowStateChanged(WindowEvent e) {
//                logger_.debug("widnow state changed: " + e.paramString());
//                resetAlert();
//            }
//        });
        
//        parent_.addPropertyChangeListener(new PropertyChangeListener() {
//            
//            public void propertyChange(PropertyChangeEvent evt) {
//                logger_.debug(evt.getPropertyName());
//                logger_.debug(evt.getOldValue());
//                logger_.debug(evt.getNewValue());
//            }
//        });
    }

    
    /**
     * Changes the systray icon to show that an alert is available.
     */
    public void showAlert() {
        menu_.setIcon(ICON_DIRMON_ALERT);
    }

    
    /**
     * Resets the systray icon to show no alerts.
     */
    public void resetAlert() {
        menu_.setIcon(ICON_DIRMON);
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
        showAlert();
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
        resetAlert();
    }


    /*
     * @see snoozesoft.systray4j.SysTrayMenuListener#iconLeftDoubleClicked(snoozesoft.systray4j.SysTrayMenuEvent)
     */
    public void iconLeftDoubleClicked(SysTrayMenuEvent e) {
        JOptionPane.showMessageDialog(
            parent_, "You may prefer double-clicking the icon.");
    }    
}