package toolbox.jdbc;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.JDBCUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartComboBox;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.layout.ParagraphLayout;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.WorkspaceAction;

/**
 * JDBC Drivers configuration panel
 */    
public class DBConfig extends JPanel implements IPreferenced
{
    private static final Logger logger_ = 
        Logger.getLogger(DBConfig.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * DBConfig has zero or more DBProfile children
     */
    private static final String NODE_DBCONFIG = "DBConfig";

    /**
     * Index of last selected database profile
     */
    private static final String ATTR_SELECTED = "selected";
    
    /**
     * DBProfile is a child of DBConfig
     */
    private static final String NODE_DBPROFILE = "DBProfile";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
        
    /**
     * Parent of this panel
     */
    private final QueryPlugin plugin_;
    
    /**
     * Combobox that allows selection of the database profile to use
     */
    private JComboBox profileCombo_;
    
    /**
     * JDBC driver (dot notated class name)
     */
    private JTextField driverField_;
    
    /**
     * JDBC access URL (driver implementation dependent)
     */
    private JTextField urlField_;
    
    /**
     * JDBC username
     */
    private JTextField userField_;
    
    /**
     * JDBC password. This is in clear text
     */
    private JTextField passwordField_;
    
    /**
     * Reference to the workspace statusbar
     */
    private IStatusBar statusBar_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DBConfig for the given plugin
     * 
     * @param plugin Query plugin
     */
    public DBConfig(QueryPlugin plugin)
    {
        plugin_    = plugin;
        statusBar_ = plugin.getStatusBar();
        buildView();
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Adds a profile to the existing list displayed in the combobox
     * 
     * @param profile Database profile
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
    
    //--------------------------------------------------------------------------
    // Protected 
    //--------------------------------------------------------------------------
    
    /**
     * Builds the panel which displays all the JDBC configuration information 
     */
    protected void buildView()
    {
        setLayout(new ParagraphLayout());

        add(new JSmartLabel("Profile"), ParagraphLayout.NEW_PARAGRAPH);
        add(profileCombo_ = new JSmartComboBox());
        profileCombo_.setEditable(true);
        profileCombo_.setAction(new ProfileChangedAction());
        
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBorderPainted(false);
        tb.add(new SaveAction());
        tb.add(new DeleteAction());
        add(tb);
                
        add(new JSmartLabel("Driver"), ParagraphLayout.NEW_PARAGRAPH);
        add(driverField_ = new JSmartTextField(20));

        add(new JSmartLabel("URL"), ParagraphLayout.NEW_PARAGRAPH);
        add(urlField_ = new JSmartTextField(20));
    
        add(new JSmartLabel("User"), ParagraphLayout.NEW_PARAGRAPH);
        add(userField_ = new JSmartTextField(15));
     
        add(new JSmartLabel("Password"), ParagraphLayout.NEW_PARAGRAPH);
        add(passwordField_ = new JSmartTextField(15));

        add(new JSmartLabel(""), ParagraphLayout.NEW_PARAGRAPH);
        add(new JSmartButton(new ConnectAction()));
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------    
        
    /**
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs)
    {
        Element dbConfig = prefs.getFirstChildElement(NODE_DBCONFIG);
          
        if (dbConfig == null || dbConfig.getChildCount() == 0)      
        {
            // First time..pre-populate with some canned profiles
            
            profileCombo_.addItem(new DBProfile(
                "DB2",
                "COM.ibm.db2.jdbc.app.DB2Driver",
                "jdbc:db2:<dbname>",
                "",
                ""));
        
            profileCombo_.addItem(new DBProfile(
                "Oracle",
                "oracle.jdbc.driver.OracleDriver",
                "jdbc:oracle:thin:@<host>:<port>:<sid>",
                "",
                ""));
            
            profileCombo_.addItem(new DBProfile(
                "HSQL",
                "org.hsqldb.jdbcDriver",
                "jdbc:hsqldb:<database>",
                "SA",
                ""));
        }
        else
        {
            try
            { 
                Elements dbProfiles = dbConfig.getChildElements(NODE_DBPROFILE);
                                
                for (int i=0, n = dbProfiles.size(); i<n; i++)
                    addProfile(new DBProfile(dbProfiles.get(i).toXML()));
                    
                profileCombo_.setSelectedIndex(
                    XOMUtil.getIntegerAttribute(dbConfig, ATTR_SELECTED, 0));
            }
            catch (Exception ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }

    }

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element dbConfig = new Element(NODE_DBCONFIG);
        
        dbConfig.addAttribute(
            new Attribute(ATTR_SELECTED, profileCombo_.getSelectedIndex()+""));
            
        for (int i=0, n=profileCombo_.getItemCount(); i<n; i++)
        {
            DBProfile profile = (DBProfile) profileCombo_.getItemAt(i);
            dbConfig.appendChild(profile.toDOM());
        }
       
        prefs.appendChild(dbConfig);        
    }
    
    //--------------------------------------------------------------------------
    // Actions
    //--------------------------------------------------------------------------

    /**
     * Connects to the database
     */
    class ConnectAction extends WorkspaceAction
    {
        ConnectAction()  
        {
            super("Connect", true, plugin_.getComponent(), statusBar_);
            putValue(SHORT_DESCRIPTION, "Connects to the database");
        }
    
        public void runAction(ActionEvent e) throws Exception
        {
            statusBar_.setInfo("Connecting to the database...");
            
            JDBCUtil.init(
                driverField_.getText(),
                urlField_.getText(),
                userField_.getText(),
                passwordField_.getText());
         
            statusBar_.setInfo("Connected to the database!");
        }
    }
    
    /** 
     * Updates the database profile fields when the profile selection changes.
     */
    class ProfileChangedAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        { 
            Object obj = profileCombo_.getSelectedItem();
            
            if (obj instanceof DBProfile)
            {
                DBProfile profile = (DBProfile) profileCombo_.getSelectedItem();
                driverField_.setText(profile.getDriver());
                urlField_.setText(profile.getUrl());
                userField_.setText(profile.getUsername());
                passwordField_.setText(profile.getPassword());
            }
        }
    }

    /**
     * Saves the current DB profile. If the profile does not already exist,
     * it is created.
     */
    class SaveAction extends AbstractAction
    {
        SaveAction()
        {
            super("", ImageCache.getIcon(ImageCache.IMAGE_SAVE));
            putValue(SHORT_DESCRIPTION, "Saves the profile");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            String current = profileCombo_.getEditor().getItem().toString();
            
            boolean found = false;
            
            for (int i=0; i< profileCombo_.getItemCount(); i++)
            {
                DBProfile profile = (DBProfile) profileCombo_.getItemAt(i);
                
                if (profile.getProfileName().equals(current))
                {
                    profile.setDriver(driverField_.getText());
                    profile.setUrl(urlField_.getText());
                    profile.setUsername(userField_.getText());
                    profile.setPassword(passwordField_.getText());
                    found |= true;
                    break;
                }
            }
            
            if (!found)
            {
                DBProfile profile = new DBProfile(
                    current,
                    driverField_.getText(),
                    urlField_.getText(),
                    userField_.getText(),
                    passwordField_.getText());
                    
                profileCombo_.addItem(profile);
                profileCombo_.setSelectedItem(profile);    
            }
            
            statusBar_.setStatus("Profile " + current + " saved.");               
        }
    }

    /**
     * Deletes the selected db profile
     */
    class DeleteAction extends AbstractAction
    {
        public DeleteAction()  
        {
            super("", ImageCache.getIcon(ImageCache.IMAGE_DELETE));
            putValue(SHORT_DESCRIPTION, "Deletes the profile");
        }
    
        public void actionPerformed(ActionEvent e)
        {
            String current = profileCombo_.getEditor().getItem().toString();
            
            boolean found = false;
            
            for (int i=0; i< profileCombo_.getItemCount(); i++)
            {
                DBProfile profile = (DBProfile) profileCombo_.getItemAt(i);
                
                if (profile.getProfileName().equals(current))
                {
                    logger_.debug("Removing " + i);
                    profileCombo_.removeItemAt(i);                    
                    
                    if (profileCombo_.getItemCount() > 0)
                        profileCombo_.setSelectedIndex(0);
                    
                    statusBar_.setStatus("Profile " + current + " deleted.");    
                    found |= true;
                    break;
                }
            }
            
            if (!found)
            {
                statusBar_.setStatus("Profile " + current + " does not exist.");
            }   
        }
    }
}