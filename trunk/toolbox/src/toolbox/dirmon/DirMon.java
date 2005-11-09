package toolbox.dirmon;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import snoozesoft.systray4j.SysTrayMenu;
import snoozesoft.systray4j.SysTrayMenuEvent;
import snoozesoft.systray4j.SysTrayMenuIcon;
import snoozesoft.systray4j.SysTrayMenuItem;
import snoozesoft.systray4j.SysTrayMenuListener;

import toolbox.util.FontUtil;
import toolbox.util.ResourceUtil;
import toolbox.util.SwingUtil;
import toolbox.util.file.DirectoryMonitor;
import toolbox.util.file.IDirectoryListener;
import toolbox.util.file.IFileActivity;
import toolbox.util.file.activity.FileChangedActivity;
import toolbox.util.file.activity.FileCreatedActivity;
import toolbox.util.file.activity.FileDeletedActivity;
import toolbox.util.file.snapshot.FileSnapshot;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartFileChooser;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.layout.StackLayout;
import toolbox.util.ui.plaf.LookAndFeelUtil;

/**
 * Directory Monitor GUI that sits in the Windows System Tray.
 */
public class DirMon extends JFrame implements ActionListener,
    SysTrayMenuListener {

    private static Logger logger_ =  Logger.getLogger(DirectoryMonitor.class);
    
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------
    
    private static SysTrayMenuIcon ICON_DIRMON;
            
    private static SysTrayMenuIcon ICON_DIRMON_ALERT; 
            
    static {
        try {
            ICON_DIRMON = 
                new SysTrayMenuIcon(ResourceUtil.getClassResourceURL(
                    "/toolbox/dirmon/DirMon.ico"));
            
            ICON_DIRMON_ALERT = 
                new SysTrayMenuIcon(ResourceUtil.getClassResourceURL(
                    "/toolbox/dirmon/DirMonAlert.ico"));
        }
        catch (IOException e) {
            logger_.error(e);
        }
    }
    
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private SysTrayMenu menu_;

    private JSmartTextArea messageArea_;

    private JSmartButton addDirButton_;

    private JSmartButton dirChooserButton_;
    
    private JSmartTextField dirField_;

    private JPanel viewStack_;
    
    private JSmartFileChooser dirChooser_;
    
    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------

    public static void main(String[] args) {

//        System.out.println("java.library.path = "
//            + System.getProperty("java.library.path"));
//
//        System.out.println(ArrayUtil.toString(StringUtils.split(System
//            .getProperty("java.library.path"), ";"), true));

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
    // ActionListerner Interface
    // -------------------------------------------------------------------------
    
    /* 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

//        if (e.getActionCommand().equals("show/hide icon")) {
//            if (menu_.isIconVisible())
//                menu_.hideIcon();
//            else
//                menu_.showIcon();
//        }
//        else if (e.getActionCommand().equals("enable/disable submenu")) {
//            SysTrayMenuItem item = menu_.getItem("Communication");
//            if (item.isEnabled())
//                item.setEnabled(false);
//            else
//                item.setEnabled(true);
//        }
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
            JOptionPane.showMessageDialog(this, "Directory Monitor v1.0");
        }
        else {
            JOptionPane.showMessageDialog(this, e.getActionCommand());
        }
    }

    
    /*
     * @see snoozesoft.systray4j.SysTrayMenuListener#iconLeftClicked(snoozesoft.systray4j.SysTrayMenuEvent)
     */
    public void iconLeftClicked(SysTrayMenuEvent e) {
        setVisible(!isVisible());
        if (isVisible())
            toFront();
        menu_.setIcon(ICON_DIRMON);
    }


    /*
     * @see snoozesoft.systray4j.SysTrayMenuListener#iconLeftDoubleClicked(snoozesoft.systray4j.SysTrayMenuEvent)
     */
    public void iconLeftDoubleClicked(SysTrayMenuEvent e) {
        JOptionPane.showMessageDialog(
            this, "You may prefer double-clicking the icon.");
    }    
    
    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------
    
    private void buildView() {

        setIconImage(ResourceUtil.getResourceAsImage(
            "/toolbox/util/ui/images/Toolbox.png"));
        
        ICON_DIRMON.addSysTrayMenuListener(this);
        ICON_DIRMON_ALERT.addSysTrayMenuListener(this);

        JPanel p = new JPanel(new FlowLayout());
        dirField_ = new JSmartTextField(20);
        addDirButton_ = new JSmartButton(new AddDirAction());
        dirChooserButton_ = new JSmartButton(new PickDirectoryAction());

        p.add(dirField_);
        p.add(dirChooserButton_);
        p.add(addDirButton_);

        messageArea_ = new JSmartTextArea("Welcome!\n", true, true);
        messageArea_.setRows(10);
        messageArea_.setColumns(80);
        messageArea_.setFont(FontUtil.getPreferredMonoFont());

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(BorderLayout.CENTER, new JScrollPane(messageArea_));
        contentPane.add(BorderLayout.NORTH, p);
        
        viewStack_ = new JPanel(new StackLayout(StackLayout.VERTICAL));
        contentPane.add(BorderLayout.SOUTH, viewStack_);
        
        // create the menu
        createMenu();
        
        pack();
        
        SwingUtil.centerWindow(this);
    }

    private void createMenu() {

//        // create some labeled menu items
//        SysTrayMenuItem subItem1 = new SysTrayMenuItem(
//            "Windows 98", "windows 98");
//        subItem1.addSysTrayMenuListener(this);
//        // disable this item
//        subItem1.setEnabled(false);
//
//        SysTrayMenuItem subItem2 = new SysTrayMenuItem(
//            "Windows 2000", "windows 2000");
//        subItem2.addSysTrayMenuListener(this);
//        SysTrayMenuItem subItem3 = new SysTrayMenuItem(
//            "Windows XP", "windows xp");
//        subItem3.addSysTrayMenuListener(this);
//
//        SysTrayMenuItem subItem4 = new SysTrayMenuItem("GNOME", "gnome");
//        subItem4.addSysTrayMenuListener(this);
//        subItem4.setEnabled(false);
//
//        SysTrayMenuItem subItem5 = new SysTrayMenuItem("KDE 3", "kde 3");
//        subItem5.addSysTrayMenuListener(this);
//
//        Vector items = new Vector();
//        items.add(subItem1);
//        items.add(subItem2);
//        items.add(subItem3);
//        items.add(subItem4);
//        items.add(subItem5);
//
//        // create a submenu and insert the previously created items
//        SubMenu subMenu = new SubMenu("Supported", items);
//
//        // create some checkable menu items
//        CheckableMenuItem chItem1 = new CheckableMenuItem("IPC", "ipc");
//        chItem1.addSysTrayMenuListener(this);
//
//        CheckableMenuItem chItem2 = new CheckableMenuItem("Sockets", "sockets");
//        chItem2.addSysTrayMenuListener(this);
//
//        CheckableMenuItem chItem3 = new CheckableMenuItem("JNI", "jni");
//        chItem3.addSysTrayMenuListener(this);
//
//        // check this item
//        chItem2.setState(true);
//        chItem3.setState(true);
//
//        // create another submenu and insert the items through addItem()
//        SubMenu chSubMenu = new SubMenu("Communication");
//        // disable this submenu
//        chSubMenu.setEnabled(false);
//
//        chSubMenu.addItem(chItem1);
//        chSubMenu.addItem(chItem2);
//        chSubMenu.addItem(chItem3);

        // create an exit item
        SysTrayMenuItem itemExit = new SysTrayMenuItem("Exit", "exit");
        itemExit.addSysTrayMenuListener(this);

        // create an about item
        SysTrayMenuItem itemAbout = new SysTrayMenuItem("About...", "about");
        itemAbout.addSysTrayMenuListener(this);

        // create the main menu
        menu_ = new SysTrayMenu(ICON_DIRMON, "Directory Monitor");

        // insert items
        menu_.addItem(itemExit);
        menu_.addSeparator();
        menu_.addItem(itemAbout);
//        menu_.addSeparator();
//        menu_.addItem(subMenu);
//        menu_.addItem(chSubMenu);
    }
    
    // -------------------------------------------------------------------------
    // AddDirAction
    // -------------------------------------------------------------------------
    
    class AddDirAction extends AbstractAction {

        public AddDirAction() {
            super("Monitor directory");
        }

        /*
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            File f = new File(dirField_.getText().trim());

            if (!f.isDirectory()) {
                JSmartOptionPane.showMessageDialog(
                    DirMon.this, "Not a valid directory");
            }
            else {
                DirectoryMonitor dm = new DirectoryMonitor(f, true);

                dm.setDelay(30000);
                dm.addDirectory(f);
                // dm.addFileActivity(new FileCreatedActivity());
                dm.addFileActivity(new FileChangedActivity());
                dm.addDirectoryListener(new IDirectoryListener() {

                    public void fileActivity(
                        IFileActivity activity,
                        List affectedFiles) throws Exception {

                        String msg = null;
                        if (activity instanceof FileChangedActivity) {
                            msg = "File changed: ";
                        }
                        else if (activity instanceof FileCreatedActivity) {
                            msg = "File created: ";
                        }
                        else if (activity instanceof FileDeletedActivity) {
                            msg = "File deleted: ";
                        }


                        for (Iterator i = affectedFiles.iterator(); i.hasNext();) {
                            FileSnapshot snapshot = (FileSnapshot) i.next();

                            StringBuffer sb = new StringBuffer();
                            sb.append(msg);
                            sb.append(snapshot.toString());
                            sb.append(" at ");
                            
                            sb.append(
                                new SimpleDateFormat().format(
                                    new Date(snapshot.getLastModified())));

                            messageArea_.append(sb.toString());
                            messageArea_.append("\n");
                            menu_.setToolTip(sb.toString());
                        }
                        
                        menu_.setIcon(ICON_DIRMON_ALERT);
                    }
                });
                
                DirectoryMonitorView monitorView = new DirectoryMonitorView(dm);
                viewStack_.add(monitorView);
                dm.start();
                pack();
            }
        };
    }
    
    //--------------------------------------------------------------------------
    // PickDirectoryAction
    //--------------------------------------------------------------------------
    
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