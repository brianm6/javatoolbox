package toolbox.plugin.jdbc;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.JDBCUtil;
import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartComboBox;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.layout.ParagraphLayout;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.WorkspaceAction;

/**
 * JDBC driver and connection settings configuration panel.
 */    
public class DBConfig extends JPanel implements IPreferenced
{
    private static final Logger logger_ = 
        Logger.getLogger(DBConfig.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * DBConfig has zero or more DBProfile children.
     */
    private static final String NODE_DBCONFIG = "DBConfig";

    /**
     * Index of last selected database profile.
     */
    private static final String ATTR_SELECTED = "selected";
    
    /**
     * DBProfile is a child of DBConfig.
     */
    private static final String NODE_DBPROFILE = "DBProfile";

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
     * Jar containing the jdbc driver if not already on the classpath.
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
    private JTextField passwordField_;
    
    /**
     * Reference to the workspace statusbar.
     */
    private IStatusBar statusBar_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DBConfig for the given plugin.
     * 
     * @param plugin Query plugin
     */
    public DBConfig(QueryPlugin plugin)
    {
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
     * Builds the panel which displays all the JDBC configuration information. 
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

        // Create a jar text field with a "..." button to choose the jar file
        // attached to its right side
        
        jarField_ = new JSmartTextField(20);
        
        jarField_.setToolTipText(
            "Only use if the JDBC driver is not on the classpath");
        
        JButton jarChooserButton = new JSmartButton(new JarChooserAction());
        
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        
        Dimension d = new Dimension(12, jarField_.getPreferredSize().height);
        jarChooserButton.setPreferredSize(d);
        
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 9;
        JPanel jarPanel = new JPanel(gbl);
        jarPanel.add(jarField_, gbc);
        gbc.gridx = 2;
        gbc.weightx = 1;
        jarPanel.add(jarChooserButton, gbc);

        add(new JSmartLabel("Jar"), ParagraphLayout.NEW_PARAGRAPH);
        add(jarPanel);

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
        add(new JSmartButton(new DisconnectAction()));
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
        
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs)
    {
        Element dbConfig = prefs.getFirstChildElement(NODE_DBCONFIG);
          
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
                "Oracle",
                "",
                "oracle.jdbc.driver.OracleDriver",
                "jdbc:oracle:thin:@<host>:<port>:<sid>",
                "",
                ""));
            
            profileCombo_.addItem(new DBProfile(
                "HSQL",
                "",
                "org.hsqldb.jdbcDriver",
                "jdbc:hsqldb:<database>",
                "SA",
                ""));

            profileCombo_.addItem(new DBProfile(
                "MySQL",
                "",
                "org.gjt.mm.mysql.Driver",
                "jdbc:mysql://<host>/<db>",
                "",
                ""));
        }
        else
        {
            try
            { 
                Elements dbProfiles = dbConfig.getChildElements(NODE_DBPROFILE);
                                
                for (int i = 0, n = dbProfiles.size(); i < n; i++)
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
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element dbConfig = new Element(NODE_DBCONFIG);
        
        dbConfig.addAttribute(
            new Attribute(ATTR_SELECTED, 
            profileCombo_.getSelectedIndex() + ""));
            
        for (int i = 0, n = profileCombo_.getItemCount(); i < n; i++)
        {
            DBProfile profile = (DBProfile) profileCombo_.getItemAt(i);
            dbConfig.appendChild(profile.toDOM());
        }
       
        prefs.appendChild(dbConfig);        
    }
    
    //--------------------------------------------------------------------------
    // ConnectAction
    //--------------------------------------------------------------------------

    /**
     * Connects to the database.
     */
    class ConnectAction extends WorkspaceAction
    {
        /**
         * Creates a ConnectAction.
         */
        ConnectAction()  
        {
            super("Connect", false, plugin_.getComponent(), statusBar_);
            putValue(SHORT_DESCRIPTION, "Connects to the database");
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            statusBar_.setInfo("Connecting to the database...");
            
            if (StringUtil.isNullOrBlank(jarField_.getText()))
            {    
                JDBCUtil.init(
                    driverField_.getText(),
                    urlField_.getText(),
                    userField_.getText(),
                    passwordField_.getText());
            }
            else
            {
                JDBCUtil.init(
                    jarField_.getText(),
                    driverField_.getText(),
                    urlField_.getText(),
                    userField_.getText(),
                    passwordField_.getText());
            }
         
            statusBar_.setInfo("Connected to the database!");
        }
    }

    //--------------------------------------------------------------------------
    // DisconnectAction
    //--------------------------------------------------------------------------

    /**
     * Disconnects from the database.
     */
    class DisconnectAction extends WorkspaceAction
    {
        /**
         * Creates a DisconnectAction.
         */
        DisconnectAction()  
        {
            super("Disconnect", false, plugin_.getComponent(), statusBar_);
            putValue(SHORT_DESCRIPTION, "Disconnects from the database.");
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            statusBar_.setInfo("Disconnecting from the database...");
            JDBCUtil.shutdown();
            statusBar_.setInfo("Disconnected from the database.");
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
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
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
        /**
         * Creates a SaveAction.
         */
        SaveAction()
        {
            super("", ImageCache.getIcon(ImageCache.IMAGE_SAVE));
            putValue(SHORT_DESCRIPTION, "Saves the profile");
        }

        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
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
                    profile.setPassword(passwordField_.getText());
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
                    passwordField_.getText());
                    
                profileCombo_.addItem(profile);
                profileCombo_.setSelectedItem(profile);    
            }
            
            statusBar_.setStatus("Profile " + current + " saved.");
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
        /**
         * Creates a DeleteAction.
         */
        public DeleteAction()  
        {
            super("", ImageCache.getIcon(ImageCache.IMAGE_DELETE));
            putValue(SHORT_DESCRIPTION, "Deletes the profile");
        }

        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
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

    //--------------------------------------------------------------------------
    // JarChooserAction
    //--------------------------------------------------------------------------
    
    /**
     * Allows user to pick a source directory through the file chooser instead 
     * of typing one in.
     */
    class JarChooserAction extends SmartAction
    {
        /**
         * Creates a JarChooserAction.
         */
        JarChooserAction()
        {
            super("...", true, false, null);
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            
            if (chooser.showDialog(DBConfig.this, 
                "Select JDBC Driver Jar File") == JFileChooser.APPROVE_OPTION)
            {
                jarField_.setText(chooser.getSelectedFile().getCanonicalPath());
            }
        }
    }
}