package toolbox.dirmon;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import toolbox.util.ResourceUtil;
import toolbox.util.SwingUtil;
import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.dirmon.recognizer.FileChangedRecognizer;
import toolbox.util.dirmon.recognizer.FileCreatedRecognizer;
import toolbox.util.dirmon.recognizer.FileDeletedRecognizer;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartFileChooser;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.plaf.LookAndFeelUtil;
import toolbox.util.ui.tabbedpane.JSmartTabbedPane;
import toolbox.util.ui.tabbedpane.SmartTabbedPaneListener;

/**
 * Directory Monitor GUI that sits in the Windows System Tray.
 */
public class DirMon extends JFrame implements SmartTabbedPaneListener {

    // TODO: Change event table to show amount of time elapsed since event was
    //       generated.
    
    // TODO: Change the ServiceView/ControllerView to used toggle buttons for
    //       start/stop and suspend/resume instead of two buttons for each.
    //       Also allow optionally icons or text or both.
    
    // TODO: Find someway to save all the preferences set on the UI
    
    // TODO: For directories that are deleted, remove subdirectories from the
    //       list of scanned dirs. THis is really a DirectoryMonitor thing.
    
    // TODO: For directories that are creates, add subdirectories to the list
    //       of scanned dirs. THis is really a DirectoryMonitor thing.
    
    private static Logger logger_ =  Logger.getLogger(DirectoryMonitor.class);
    
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /**
     * Default delay in minutes.
     */
    private static final int DEFAULT_DELAY = 30;

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private DesktopNotifier desktopNotifier_;
    
    private SystemTrayUpdater systemTrayUpdater_;

    private ConsoleView consoleView_;

    private EventTableView tableView_;
    
    private JSmartButton addDirButton_;

    private JSmartButton dirChooserButton_;
    
    private JSmartTextField dirField_;
    
    private JSmartTextField delayField_;

    private JSmartFileChooser dirChooser_;

    private JSmartTabbedPane tabbedPane_;
    
    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------

    public static void main(String[] args) {

        try {
            LookAndFeelUtil.setPreferredLAF();
        }
        catch (Exception e) {
            logger_.error(e);
        }

        DirMon dirMon = new DirMon();
        dirMon.setVisible(true);        
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DirMon() {
        super("Directory Monitor");
        buildView();
    }

    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------
    
    private void buildView() {

        setIconImage(ResourceUtil.getResourceAsImage(
            "/toolbox/util/ui/images/Toolbox.png"));
        
        consoleView_ = new ConsoleView();
        tableView_ = new EventTableView();
        systemTrayUpdater_ = new SystemTrayUpdater(this);
        desktopNotifier_ = new DesktopNotifier();
        
        tabbedPane_ = new JSmartTabbedPane(true);
        tabbedPane_.addSmartTabbedPaneListener(this);
        tabbedPane_.addTab("Console", consoleView_);
        tabbedPane_.addTab("All Events", tableView_);
        
        JPanel inputPanel = new JPanel(new FlowLayout());
        dirField_ = new JSmartTextField(40);
        delayField_ = new JSmartTextField(DEFAULT_DELAY + "", 4);
        addDirButton_ = new JSmartButton(new MonitorDirectoryAction());
        dirChooserButton_ = new JSmartButton(new PickDirectoryAction());

        inputPanel.add(new JSmartLabel("Directory"));
        inputPanel.add(dirField_);
        inputPanel.add(dirChooserButton_);
        inputPanel.add(addDirButton_);
        inputPanel.add(new JSmartLabel("Delay"));
        inputPanel.add(delayField_);
        inputPanel.add(new JSmartLabel("minutes"));
        
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(BorderLayout.CENTER, tabbedPane_);
        contentPane.add(BorderLayout.NORTH, inputPanel);

        SwingUtil.setSizeAsDesktopPercentage(this, 50, 50); 
        SwingUtil.centerWindow(this);
        
        //dirField_.setText("M:\\x1700_vacany_10_dynamic\\staffplanning\\vacancy\\build");
        //delayField_.setText("10s");
    }

    // -------------------------------------------------------------------------
    // SmartTabbedPaneListener Interface
    // -------------------------------------------------------------------------
    
    /**
     * When a tab is closed, deregister the console and event table views from
     * the directory monitor associated with that tab.
     * 
     * who is responsible..
     * 
     * when the tab is closed, the service should destroy itself.
     * 
     * who should listen for the tab closing and who should tell the service to
     * destroy and clean up after itself?
     * 
     * @see toolbox.util.ui.tabbedpane.SmartTabbedPaneListener#tabClosing(toolbox.util.ui.tabbedpane.JSmartTabbedPane, int)
     */
    public void tabClosing(JSmartTabbedPane tabbedPane, int tabIndex) {
        
        Component source = tabbedPane.getComponentAt(tabIndex);
        
        if (source instanceof SingleMonitorView) {
            SingleMonitorView singleView = (SingleMonitorView) source;
            DirectoryMonitor monitor = singleView.getMonitor();
            monitor.destroy();
        }
    }
    
    // -------------------------------------------------------------------------
    // MonitorDirectoryAction
    // -------------------------------------------------------------------------
    
    class MonitorDirectoryAction extends SmartAction {

        public MonitorDirectoryAction() {
            super("Monitor", true, true, null);
        }

        public void runAction(ActionEvent e) throws Exception {
            File f = new File(dirField_.getText().trim());

            if (!f.isDirectory()) {
                JSmartOptionPane.showMessageDialog(
                    DirMon.this, "Not a valid directory");
            }
            else {
                DirectoryMonitor dm = new DirectoryMonitor(f, true);

                String delayValue = delayField_.getText().trim();
                
                // seconds
                if (delayValue.endsWith("s")) {
                    delayValue = delayValue.substring(0, delayValue.length() - 1);
                    dm.setDelay(Integer.parseInt(delayValue) * 1000);
                }
                
                // minutes
                else {
                    dm.setDelay(Integer.parseInt(delayValue) * 1000 * 60);
                }
                
                // Events for the directory monitor to recognize
                dm.addRecognizer(new FileCreatedRecognizer(dm));
                dm.addRecognizer(new FileDeletedRecognizer(dm));
                dm.addRecognizer(new FileChangedRecognizer(dm));
                
                // Components interested in directtory monitor events
                dm.addDirectoryMonitorListener(systemTrayUpdater_);
                dm.addDirectoryMonitorListener(desktopNotifier_);
                dm.addDirectoryMonitorListener(consoleView_);
                dm.addDirectoryMonitorListener(tableView_);
                
                dm.start();
                
                // TODO: Fix me
                // has to be after service is started cuz ServiceView (which
                // is a aggregated by SingleMonitorView) requires that the
                // service state be something
                
                SingleMonitorView singleView = new SingleMonitorView(dm);
                
                String dirName = 
                    dm.getMonitoredDirectories().iterator().next().toString();
                
                tabbedPane_.insertTab(
                    FilenameUtils.getName(dirName), // last dir name as tab name
                    null,
                    singleView,
                    dirName,                        // full dir name as tooltip
                    0);                             // insert as first tab
            }
        };
    }

    // -------------------------------------------------------------------------
    // PickDirectoryAction
    // -------------------------------------------------------------------------
    
    /**
     * Allows user to pick a source directory through the file chooser instead 
     * of typing one in.
     */
    class PickDirectoryAction extends SmartAction {

        PickDirectoryAction() {
            super("...", true, false, null);
        }


        /*
         * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception {
            
            if (dirChooser_ ==  null) {
                dirChooser_ = new JSmartFileChooser();
                dirChooser_.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            }

            if (dirChooser_.showDialog(DirMon.this, "Select Directory") == 
                JFileChooser.APPROVE_OPTION) {
                
                dirField_.setText(
                    dirChooser_.getSelectedFile().getCanonicalPath());
            }
        }
    }    
}