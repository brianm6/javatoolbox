package toolbox.plugin.jdbc;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.forms.SmartComponentFactory;
import toolbox.plugin.jdbc.action.BaseAction;
import toolbox.util.JDBCSession;
import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartComboBox;
import toolbox.util.ui.JSmartFileChooser;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.SmartAction;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PreferencedException;

/**
 * JDBC driver and connection settings configuration panel aka. DBProfile view.
 */    
public class DBProfilesView extends JHeaderPanel implements IPreferenced
{
    private static final Logger logger_ = Logger.getLogger(DBProfilesView.class);
    
    //--------------------------------------------------------------------------
    // Icon Constants
    //--------------------------------------------------------------------------
    
    /**
     * Icon for header and flipper.
     */
    public static final Icon ICON_DBPROFILE = ImageCache.getIcon(ImageCache.IMAGE_DATASOURCE);
    
    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------
    
    /**
     * DBProfilesView has zero or more DBProfile children.
     */
    private static final String NODE_DBPROFILES = "DBProfiles";

    /**
     * Index of last selected database profile.
     */
    private static final String  ATTR_SELECTED = "selected";
    
    /**
     * DBProfile is a child of DBProfilesView.
     */
    private static final String  NODE_DBPROFILE = "DBProfile";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
        
    /**
     * Parent of this panel.
     */
    private final QueryPlugin plugin_;
    
    /**
     * Combobox that allows selection of the database profile to use.
     */
    private JComboBox profileCombo_;
    
    /**
     * Jar containing the jdbc driver if not already on the classpath. Multiple
     * jar files can be specified using a comma as a separator.
     */
    private JTextField jarField_;
    
    /**
     * JDBC driver (dot notated class name).
     */
    private JTextField driverField_;
    
    /**
     * JDBC access URL (driver implementation dependent).
     */
    private JTextField urlField_;
    
    /**
     * JDBC username.
     */
    private JTextField userField_;
    
    /**
     * JDBC password. This is in clear text.
     */
    private JPasswordField passwordField_;
    
    /**
     * Reference to the workspace statusbar.
     */
    private IStatusBar statusBar_;

    /**
     * Chooser to select a jar file containing jdbc drivers.
     */
    private JSmartFileChooser chooser_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DBProfilesView for the given plugin.
     * 
     * @param plugin Query plugin.
     */
    public DBProfilesView(QueryPlugin plugin)
    {
        super(ICON_DBPROFILE, "Databases");
        plugin_ = plugin;
        statusBar_ = plugin.getStatusBar();
        buildView();
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Adds a profile to the existing list displayed in the combobox.
     * 
     * @param profile Database profile.
     */
    public void addProfile(DBProfile profile)
    {
        DefaultComboBoxModel model = 
            (DefaultComboBoxModel) profileCombo_.getModel();
        
        if (model.getIndexOf(profile) > 0)
            return;
        else
            profileCombo_.addItem(profile);
    }
    
    
    /**
     * Returns the currently selected profile.
     *  
     * @return DBProfile
     */
    public DBProfile getCurrentProfile()
    {
        return (DBProfile) profileCombo_.getSelectedItem();
    }

    
    /**
     * Returns the name of the session for the currently selected DBProfile.
     * 
     * @return String
     */
    public String getSession()
    {
        return getCurrentProfile().getProfileName();
    }
    
    //--------------------------------------------------------------------------
    // Protected 
    //--------------------------------------------------------------------------
    
    /**
     * Builds the panel which displays all the JDBC configuration information. 
     */
    protected void buildView()
    {
        JToolBar tb = new JToolBar();
        tb.add(new SaveAction());
        tb.add(new DeleteAction());
        setToolBar(tb);
        
        FormLayout layout = new FormLayout("r:p:n, p, f:p:g, p, r:p:n", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setComponentFactory(SmartComponentFactory.getInstance());
        builder.setDefaultDialogBorder();

        // Profile combobox
        profileCombo_ = new JSmartComboBox();
        profileCombo_.setEditable(true);
        profileCombo_.setAction(new ProfileChangedAction());
        builder.append("Profile", profileCombo_);
        builder.nextLine();
        
        // Create a jar text field with a "..." button to choose the jar file
        // attached to its right side
        jarField_ = new JSmartTextField(16);
        jarField_.setToolTipText("Optional if not on classpath");
        JButton jarChooserButton = new JSmartButton(new JarChooserAction());
        Dimension buttonDim = new Dimension(20, 20);
        jarChooserButton.setMaximumSize(buttonDim);
        jarChooserButton.setPreferredSize(buttonDim);
        jarChooserButton.setMinimumSize(buttonDim);
        builder.append("Jar File", jarField_, jarChooserButton);
        builder.nextLine();
        
        // JDBC Driver
        driverField_ = new JSmartTextField(16);
        builder.append("Driver", driverField_);
        builder.nextLine();

        // URL 
        urlField_ = new JSmartTextField(16);
        builder.append("URL", urlField_);
        builder.nextLine();

        // User
        userField_ = new JSmartTextField(16);
        builder.append("User", userField_);
        builder.nextLine();

        Action connectDisconnectAction = 
            new ConnectDisconnectAction(ConnectDisconnectAction.MODE_CONNECT);

        // Password
        passwordField_ = new JPasswordField(16);
        passwordField_.setAction(connectDisconnectAction);
        builder.append("Password", passwordField_);
        builder.nextLine();

        builder.appendRelatedComponentsGapRow();
        builder.nextLine();

        builder.appendRow("pref");
        CellConstraints cc = new CellConstraints();
        
        builder.add(
            new JSmartButton(connectDisconnectAction), 
            cc.xyw(3, builder.getRow(), 1, "c,f"));
        
        //setContent(new FormDebugPanel(layout));
        setContent(builder.getPanel());
        
        chooser_ = new JSmartFileChooser();
        chooser_.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
        
    /*
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element dbConfig = prefs.getFirstChildElement(NODE_DBPROFILES);
          
        if (dbConfig == null || dbConfig.getChildCount() == 0)      
        {
            // First time..pre-populate with some canned profiles
            
            profileCombo_.addItem(new DBProfile(
                "DB2",
                "",
                "COM.ibm.db2.jdbc.app.DB2Driver",
                "jdbc:db2:<dbname>",
                "",
                ""));
        
            profileCombo_.addItem(new DBProfile(
                "HSQL",
                "",
                "org.hsqldb.jdbcDriver",
                "jdbc:hsqldb:[hsql://<host> || <file>]",
                "SA",
                ""));
            
            profileCombo_.addItem(new DBProfile(
                "McKoi",
                "mkjdbc.jar",
                "com.mckoi.JDBCDriver",
                "jdbc:mckoi://<host>/",
                "",
                ""));
            
            profileCombo_.addItem(new DBProfile(
                "MySQL",
                "",
                "org.gjt.mm.mysql.Driver",
                "jdbc:mysql://<host>/<db>",
                "",
                ""));
            
            profileCombo_.addItem(new DBProfile(
                "Oracle",
                "",
                "oracle.jdbc.driver.OracleDriver",
                "jdbc:oracle:thin:@<host>:<port>:<sid>",
                "",
                ""));
            
            profileCombo_.addItem(new DBProfile(
                "Sybase",
                "",
                "com.sybase.jdbc2.jdbc.SybDriver",
                "jdbc:sybase:Tds:<host>:<port>/<dbname>",
                "",
                ""));
            
        }
        else
        {
            Elements dbProfiles = dbConfig.getChildElements(NODE_DBPROFILE);
                            
            for (int i = 0, n = dbProfiles.size(); i < n; i++)
            {
                DBProfile profile = new DBProfile();
                Element wrapper = new Element("wrapper");
                dbProfiles.get(i).detach();
                wrapper.appendChild(dbProfiles.get(i));
                profile.applyPrefs(wrapper);
                addProfile(profile);
            }
                
            profileCombo_.setSelectedIndex(
                XOMUtil.getIntegerAttribute(dbConfig, ATTR_SELECTED, -1));
        }
        
        chooser_.applyPrefs(dbConfig);
    }

    
    /*
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element dbConfig = new Element(NODE_DBPROFILES);
            
        for (int i = 0, n = profileCombo_.getItemCount(); i < n; i++)
        {
            DBProfile profile = (DBProfile) profileCombo_.getItemAt(i);
            if (!StringUtils.isBlank(profile.getProfileName()))
            {
                Element wrapper = new Element("wrapper");
                profile.savePrefs(wrapper);
                
                if (wrapper.getChildCount() == 1)
                {
                    Node dbc = wrapper.getChild(0);
                    dbc.detach();
                    dbConfig.appendChild(dbc);
                }
            }
        }

        if (dbConfig.getChildCount() > 0)
            dbConfig.addAttribute(
                new Attribute(ATTR_SELECTED, 
                profileCombo_.getSelectedIndex() + ""));

        chooser_.savePrefs(dbConfig);
        XOMUtil.insertOrReplace(prefs, dbConfig);
    }
    
    //--------------------------------------------------------------------------
    // ConnectDisconnectAction
    //--------------------------------------------------------------------------

    /**
     * Dual mode action that handles connect/disconnect from the database.
     */
    class ConnectDisconnectAction extends BaseAction
    {
        /**
         * Action name when in connect mode.
         */
        public static final String MODE_CONNECT = "Connect";
        
        /**
         * Action name when in disconnect mode.
         */
        public static final String MODE_DISCONNECT = "Disconnect";
        
        /**
         * Creates a ConnectDisconnectAction.
         * 
         * @param mode MODE_CONNECT | MODE_DISCONNECT
         */
        ConnectDisconnectAction(String mode)  
        {
            super(plugin_, mode, false, plugin_.getView(), statusBar_);
            
            putValue(SHORT_DESCRIPTION, 
                "Connects/disconnects from the database");
        }

        
        /*
         * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            try
            {
	            if (getValue(NAME).equals(MODE_CONNECT))
	            {
	                connect();
	                putValue(NAME, MODE_DISCONNECT);
	            }
	            else
	            {
	                disconnect();
	                putValue(NAME, MODE_CONNECT);
	            }
	            
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }


        /**
         * Disconnects from the database.
         * 
         * @throws SQLException on error.
         */
        private void disconnect() throws SQLException
        {
            statusBar_.setInfo("Disconnecting from the database...");
            JDBCSession.shutdown(getSession());
            statusBar_.setInfo("Disconnected from the database.");
        }


        /**
         * Connects to the database.
         *
         * @throws Exception on error. 
         */
        private void connect() throws Exception
        {
            statusBar_.setInfo("Connecting to the database...");
            
            if (StringUtils.isBlank(jarField_.getText()))
            {    
                JDBCSession.init(
                    getSession(),
                    driverField_.getText(),
                    urlField_.getText(),
                    userField_.getText(),
                    String.valueOf(passwordField_.getPassword()),
                    true);
            }
            else
            {
                JDBCSession.init(
                    getSession(),
                    StringUtil.tokenize(jarField_.getText(), ","),
                    driverField_.getText(),
                    urlField_.getText(),
                    userField_.getText(),
                    String.valueOf(passwordField_.getPassword()));
                    
            }
       
            statusBar_.setInfo("Connected to the database!");
        }
    }

    //--------------------------------------------------------------------------
    // ProfileChangedAction.
    //--------------------------------------------------------------------------
    
    /** 
     * Updates the database profile fields when the profile selection changes.
     */
    class ProfileChangedAction extends AbstractAction
    {
        /*
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        { 
            Object obj = profileCombo_.getSelectedItem();
            
            if (obj instanceof DBProfile)
            {
                DBProfile profile = (DBProfile) profileCombo_.getSelectedItem();
                
                jarField_.setText(profile.getJarFile());
                driverField_.setText(profile.getDriver());
                urlField_.setText(profile.getUrl());
                userField_.setText(profile.getUsername());
                passwordField_.setText(profile.getPassword());
            }
        }
    }

    //--------------------------------------------------------------------------
    // SaveAction
    //--------------------------------------------------------------------------
    
    /**
     * Saves the current DB profile. If the profile does not already exist, it
     * is created.
     */
    class SaveAction extends AbstractAction
    {
        SaveAction()
        {
            super("", ImageCache.getIcon(ImageCache.IMAGE_SAVE));
            putValue(SHORT_DESCRIPTION, "Saves the profile");
        }

        
        /*
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            String current = profileCombo_.getEditor().getItem().toString();
            
            boolean found = false;
            
            for (int i = 0; i < profileCombo_.getItemCount(); i++)
            {
                DBProfile profile = (DBProfile) profileCombo_.getItemAt(i);
                
                if (profile.getProfileName().equals(current))
                {
                    profile.setJarFile(jarField_.getText());
                    profile.setDriver(driverField_.getText());
                    profile.setUrl(urlField_.getText());
                    profile.setUsername(userField_.getText());
                    profile.setPassword(
                        String.valueOf(passwordField_.getPassword()));
                    found |= true;
                    break;
                }
            }
            
            if (!found)
            {
                DBProfile profile = new DBProfile(
                    current,
                    jarField_.getText(),
                    driverField_.getText(),
                    urlField_.getText(),
                    userField_.getText(),
                    String.valueOf(passwordField_.getPassword()));
                    
                profileCombo_.addItem(profile);
                profileCombo_.setSelectedItem(profile);    
            }
            
            statusBar_.setInfo("Profile " + current + " saved.");
        }
    }

    //--------------------------------------------------------------------------
    // DeleteAction
    //--------------------------------------------------------------------------
    
    /**
     * Deletes the selected db profile.
     */
    class DeleteAction extends AbstractAction
    {
        public DeleteAction()  
        {
            super("", ImageCache.getIcon(ImageCache.IMAGE_DELETE));
            putValue(SHORT_DESCRIPTION, "Deletes the profile");
        }

        
        /*
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            String current = profileCombo_.getEditor().getItem().toString();
            
            boolean found = false;
            
            for (int i = 0; i < profileCombo_.getItemCount(); i++)
            {
                DBProfile profile = (DBProfile) profileCombo_.getItemAt(i);
                
                if (profile.getProfileName().equals(current))
                {
                    logger_.debug("Removing " + i);
                    profileCombo_.removeItemAt(i);                    
                    
                    if (profileCombo_.getItemCount() > 0)
                        profileCombo_.setSelectedIndex(0);
                    
                    statusBar_.setInfo("Profile " + current + " deleted.");
                    found |= true;
                    break;
                }
            }
            
            if (!found)
            {
                statusBar_.setWarning(
                    "Profile " + current + " does not exist.");
            }   
        }
    }

    //--------------------------------------------------------------------------
    // JarChooserAction
    //--------------------------------------------------------------------------
    
    /**
     * Allows user to pick jdbc driver jar file through the file chooser instead 
     * of typing one in.
     */
    class JarChooserAction extends SmartAction
    {
        JarChooserAction()
        {
            super("...", true, false, null);
            //putValue(SMALL_ICON, ImageCache.getIcon(ImageCache.IMAGE_FIND));
        }

        
        /*
         * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            if (chooser_.showDialog(DBProfilesView.this, 
                "Select JDBC Driver Jar File") == JFileChooser.APPROVE_OPTION)
            {
                jarField_.setText(
                    chooser_.getSelectedFile().getCanonicalPath());
            }
        }
    }
}