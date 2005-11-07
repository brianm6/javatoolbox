package toolbox.dirmon;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.apache.commons.lang.StringUtils;

import snoozesoft.systray4j.CheckableMenuItem;
import snoozesoft.systray4j.SubMenu;
import snoozesoft.systray4j.SysTrayMenu;
import snoozesoft.systray4j.SysTrayMenuEvent;
import snoozesoft.systray4j.SysTrayMenuIcon;
import snoozesoft.systray4j.SysTrayMenuItem;
import snoozesoft.systray4j.SysTrayMenuListener;
import toolbox.util.ArrayUtil;
import toolbox.util.FontUtil;
import toolbox.util.ResourceUtil;
import toolbox.util.file.DirectoryMonitor;
import toolbox.util.file.IDirectoryListener;
import toolbox.util.file.IFileActivity;
import toolbox.util.file.activity.FileChangedActivity;
import toolbox.util.file.activity.FileCreatedActivity;
import toolbox.util.file.activity.FileDeletedActivity;
import toolbox.util.file.snapshot.FileSnapshot;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.JSmartTextField;

/**
 * Directory Monitor GUI
 */
public class DirMon extends JFrame implements ActionListener,
    SysTrayMenuListener {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------
    
    static final int INIT_WIDTH = 500;

    static final int INIT_HEIGHT = 300;

    private static final String[] toolTips = {
        "SysTray for Java rules!",
        "brought to you by\nSnoozeSoft 2004" };

    static final SysTrayMenuIcon[] icons = {
        new SysTrayMenuIcon("toolbox/util/ui/images/Toolbox"),
        new SysTrayMenuIcon("toolbox/util/ui/images/Toolbox") };

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private SysTrayMenu menu;

    private int currentIndexIcon;

    private int currentIndexTooltip;

    private JSmartTextArea messageArea_;

    private JSmartButton addDirButton_;

    private JSmartTextField dirField_;

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------

    public static void main(String[] args) {

        System.out.println("java.library.path = "
            + System.getProperty("java.library.path"));

        System.out.println(ArrayUtil.toString(StringUtils.split(System
            .getProperty("java.library.path"), ";"), true));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
        }

        new DirMon();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DirMon() {
        super("Directory Monitor");
        buildView();
        show();
    }

    // -------------------------------------------------------------------------
    // ActionListerner Interface
    // -------------------------------------------------------------------------
    
    /* 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("change tooltip")) {
            if (currentIndexTooltip == 0)
                currentIndexTooltip = 1;
            else
                currentIndexTooltip = 0;

            menu.setToolTip(toolTips[currentIndexTooltip]);
        }
        else if (e.getActionCommand().equals("change icon")) {
            if (currentIndexIcon == 0)
                currentIndexIcon = 1;
            else
                currentIndexIcon = 0;

            menu.setIcon(icons[currentIndexIcon]);
        }
        else if (e.getActionCommand().equals("show/hide icon")) {
            if (menu.isIconVisible())
                menu.hideIcon();
            else
                menu.showIcon();
        }
        else if (e.getActionCommand().equals("enable/disable submenu")) {
            SysTrayMenuItem item = menu.getItem("Communication");
            if (item.isEnabled())
                item.setEnabled(false);
            else
                item.setEnabled(true);
        }
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

        setIconImage(ResourceUtil
            .getResourceAsImage("/toolbox/util/ui/images/Toolbox.png"));

        Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();

        int xPos = (dimScreen.width - INIT_WIDTH) / 2;
        int yPos = (dimScreen.height - INIT_HEIGHT) / 2;

        setBounds(xPos, yPos, INIT_WIDTH, INIT_HEIGHT);

        // don´t forget to assign listeners to the icons
        icons[0].addSysTrayMenuListener(this);
        icons[1].addSysTrayMenuListener(this);

        JPanel p = new JPanel(new FlowLayout());
        dirField_ = new JSmartTextField(20);
        addDirButton_ = new JSmartButton(new AddDirAction());
        p.add(dirField_);
        p.add(addDirButton_);

        messageArea_ = new JSmartTextArea("Welcome!", true, true);
        messageArea_.setFont(FontUtil.getPreferredMonoFont());

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(BorderLayout.CENTER, new JScrollPane(messageArea_));
        contentPane.add(BorderLayout.NORTH, p);

        // change this according to the number of buttons used
        // getContentPane().setLayout(new GridLayout(4, 1));

        // addButton("change icon");
        // addButton("change tooltip");
        // addButton("show/hide icon");
        // addButton("enable/disable submenu");

        // create the menu
        createMenu();
    }

    private void createMenu() {

        // create some labeled menu items
        SysTrayMenuItem subItem1 = new SysTrayMenuItem(
            "Windows 98", "windows 98");
        subItem1.addSysTrayMenuListener(this);
        // disable this item
        subItem1.setEnabled(false);

        SysTrayMenuItem subItem2 = new SysTrayMenuItem(
            "Windows 2000", "windows 2000");
        subItem2.addSysTrayMenuListener(this);
        SysTrayMenuItem subItem3 = new SysTrayMenuItem(
            "Windows XP", "windows xp");
        subItem3.addSysTrayMenuListener(this);

        SysTrayMenuItem subItem4 = new SysTrayMenuItem("GNOME", "gnome");
        subItem4.addSysTrayMenuListener(this);
        subItem4.setEnabled(false);

        SysTrayMenuItem subItem5 = new SysTrayMenuItem("KDE 3", "kde 3");
        subItem5.addSysTrayMenuListener(this);

        Vector items = new Vector();
        items.add(subItem1);
        items.add(subItem2);
        items.add(subItem3);
        items.add(subItem4);
        items.add(subItem5);

        // create a submenu and insert the previously created items
        SubMenu subMenu = new SubMenu("Supported", items);

        // create some checkable menu items
        CheckableMenuItem chItem1 = new CheckableMenuItem("IPC", "ipc");
        chItem1.addSysTrayMenuListener(this);

        CheckableMenuItem chItem2 = new CheckableMenuItem("Sockets", "sockets");
        chItem2.addSysTrayMenuListener(this);

        CheckableMenuItem chItem3 = new CheckableMenuItem("JNI", "jni");
        chItem3.addSysTrayMenuListener(this);

        // check this item
        chItem2.setState(true);
        chItem3.setState(true);

        // create another submenu and insert the items through addItem()
        SubMenu chSubMenu = new SubMenu("Communication");
        // disable this submenu
        chSubMenu.setEnabled(false);

        chSubMenu.addItem(chItem1);
        chSubMenu.addItem(chItem2);
        chSubMenu.addItem(chItem3);

        // create an exit item
        SysTrayMenuItem itemExit = new SysTrayMenuItem("Exit", "exit");
        itemExit.addSysTrayMenuListener(this);

        // create an about item
        SysTrayMenuItem itemAbout = new SysTrayMenuItem("About...", "about");
        itemAbout.addSysTrayMenuListener(this);

        // create the main menu
        menu = new SysTrayMenu(icons[0], toolTips[0]);

        // insert items
        menu.addItem(itemExit);
        menu.addSeparator();
        menu.addItem(itemAbout);
        menu.addSeparator();
        menu.addItem(subMenu);
        menu.addItem(chSubMenu);
    }
    
    // -------------------------------------------------------------------------
    // AddDirAction
    // -------------------------------------------------------------------------
    
    class AddDirAction extends AbstractAction {

        public AddDirAction() {
            super("Monitor directory");
        }

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

                            messageArea_.append(msg);
                            messageArea_.append(snapshot.toString());
                            messageArea_.append("\n");
                        }
                    }
                });
                dm.start();

            }
        };
    }
}