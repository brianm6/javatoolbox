package toolbox.jdbc;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.JDBCUtil;
import toolbox.util.StringUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.layout.ParagraphLayout;
import toolbox.util.ui.plugin.IPreferenced;
import toolbox.util.ui.plugin.IStatusBar;
import toolbox.util.ui.plugin.WorkspaceAction;
import toolbox.util.xml.XMLNode;
import toolbox.util.xml.XMLParser;

/**
 * JDBC Drivers configuration panel
 */    
public class DBConfig extends JPanel implements IPreferenced
{
    private static final Logger logger_ = Logger.getLogger(DBConfig.class);
    
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
     * @param  plugin  Query plugin
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
     * @param  profile  Database profile
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
     * Returns an XML representation of the data making up the configuration.
     *  
     * @return XML string
     */
    public String toXML()
    {
        XMLNode profiles = new XMLNode("DBProfileList");
        
        for (int i=0, n=profileCombo_.getItemCount(); i<n; i++)
        {
            DBProfile profile = (DBProfile) profileCombo_.getItemAt(i);
            profiles.addNode(profile.toDOM());
        }
        
        return profiles.toString();
    }
    
    //--------------------------------------------------------------------------
    // Protected 
    //--------------------------------------------------------------------------
    
    /**
     * Builds the panel which displays all the JDBC configuration information 
     * 
     * @return JDBC configuration panel
     */
    protected void buildView()
    {
        setLayout(new ParagraphLayout());

        add(new JLabel("Profile"), ParagraphLayout.NEW_PARAGRAPH);
        add(profileCombo_ = new JComboBox());
        profileCombo_.setEditable(true);
        profileCombo_.setAction(new ProfileChangedAction());
        
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBorderPainted(false);
        tb.add(new SaveAction());
        tb.add(new DeleteAction());
        add(tb);
                
        add(new JLabel("Driver"), ParagraphLayout.NEW_PARAGRAPH);
        add(driverField_ = new JTextField(20));

        add(new JLabel("URL"), ParagraphLayout.NEW_PARAGRAPH);
        add(urlField_ = new JTextField(20));
    
        add(new JLabel("User"), ParagraphLayout.NEW_PARAGRAPH);
        add(userField_ = new JTextField(15));
     
        add(new JLabel("Password"), ParagraphLayout.NEW_PARAGRAPH);
        add(passwordField_ = new JTextField(15));

        add(new JLabel(""), ParagraphLayout.NEW_PARAGRAPH);
        add(new JButton(new ConnectAction()));
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------    
        
    public void savePrefs(Properties prefs)
    {
        prefs.setProperty(QueryPlugin.PROP_PROFILES, toXML());
    }
    
    public void applyPrefs(Properties prefs)
    {
        String xmlProfiles = prefs.getProperty(QueryPlugin.PROP_PROFILES, "");
        
        if (StringUtil.isNullOrBlank(xmlProfiles) || 
            xmlProfiles.trim().equals("<DBProfileList/>"))
        {
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
                "",
                ""));
        }
        else
        {
            try
            {
                XMLNode profiles = 
                    new XMLParser().parseXML(new StringReader(xmlProfiles));
                    
                for(Enumeration e=profiles.enumerateNode();e.hasMoreElements();)
                {
                    XMLNode profile = (XMLNode) e.nextElement();
                    DBProfile dbProfile = new DBProfile(profile.toString());
                    addProfile(dbProfile);
                }
            }
            catch (IOException ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // Actions
    //--------------------------------------------------------------------------

    /**
     * Connects to the database
     */
    class ConnectAction extends WorkspaceAction
    {
        public ConnectAction()  
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
            super("", ImageCache.getIcon("/toolbox/util/ui/images/Save.gif"));
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
            super("", ImageCache.getIcon("/toolbox/util/ui/images/Delete.gif"));
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