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

import org.apache.log4j.Logger;

import toolbox.util.JDBCUtil;
import toolbox.util.StringUtil;
import toolbox.util.ui.layout.ParagraphLayout;
import toolbox.util.ui.plugin.IStatusBar;
import toolbox.util.ui.plugin.WorkspaceAction;
import toolbox.util.xml.XMLNode;
import toolbox.util.xml.XMLParser;

/**
 * JDBC Drivers configuration panel
 */    
public class DBConfig extends JPanel
{
    private static final Logger logger_ = Logger.getLogger(DBConfig.class);
    
    private final QueryPlugin plugin_;
    private JComboBox  dbCombo_;
    private JTextField driverField_;
    private JTextField urlField_;
    private JTextField userField_;
    private JTextField passwordField_;
    
    private IStatusBar statusBar_;
    
    public DBConfig(QueryPlugin plugin)
    {
        plugin_    = plugin;
        statusBar_ = plugin.getStatusBar();
        buildView();
    }
    
    public void addProfile(DBProfile profile)
    {
        DefaultComboBoxModel model = (DefaultComboBoxModel) dbCombo_.getModel();
        
        if (model.getIndexOf(profile) > 0)
            return;
        else
            dbCombo_.addItem(profile);
    }
    
    /**
     * Builds the panel which displays all the JDBC configuration information 
     * 
     * @return JDBC configuration panel
     */
    protected void buildView()
    {
        setLayout(new ParagraphLayout());

        add(new JLabel("Database"), ParagraphLayout.NEW_PARAGRAPH);
        add(dbCombo_ = new JComboBox());
        dbCombo_.setEditable(true);
        dbCombo_.setAction(new ProfileChangedAction());
        add(new JButton(new SaveAction()));
        
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
    
    public void savePrefs(Properties prefs)
    {
        prefs.setProperty(QueryPlugin.PROP_PROFILES, toXML());
    }
    
    public void applyPrefs(Properties prefs) throws IOException
    {
        String xmlProfiles = prefs.getProperty(QueryPlugin.PROP_PROFILES, "");
        
        if (StringUtil.isNullOrBlank(xmlProfiles))
        {
            dbCombo_.addItem(new DBProfile(
                "DB2",
                "COM.ibm.db2.jdbc.app.DB2Driver",
                "jdbc:db2:<dbname>",
                "",
                ""));
        
            dbCombo_.addItem(new DBProfile(
                "Oracle",
                "oracle.jdbc.driver.OracleDriver",
                "jdbc:oracle:thin:@<host>:<port>:<sid>",
                "",
                ""));
            
            dbCombo_.addItem(new DBProfile(
                "HSQL",
                "org.hsqldb.jdbcDriver",
                "jdbc:hsqldb:<database>",
                "",
                ""));
        }
        else
        {
            XMLNode profiles = 
                new XMLParser().parseXML(new StringReader(xmlProfiles));
                
            for (Enumeration e = profiles.enumerateNode(); e.hasMoreElements();)
            {
                XMLNode profile = (XMLNode) e.nextElement();
                DBProfile dbProfile = new DBProfile(profile.toString());
                addProfile(dbProfile);
            }
            
        }
    }
    
    public String toXML()
    {
        XMLNode profiles = new XMLNode("DBProfileList");
        
        for (int i=0, n=dbCombo_.getItemCount(); i<n; i++)
        {
            DBProfile profile = (DBProfile) dbCombo_.getItemAt(i);
            profiles.addNode(profile.toDOM());
        }
        
        return profiles.toString();
    }
    
    class ProfileChangedAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        { 
            Object obj = dbCombo_.getSelectedItem();
            
            if (obj instanceof DBProfile)
            {
                DBProfile profile = (DBProfile) dbCombo_.getSelectedItem();
                
                logger_.debug("Profile changed to: " + profile);
                
                driverField_.setText(profile.getDriver());
                urlField_.setText(profile.getUrl());
                userField_.setText(profile.getUsername());
                passwordField_.setText(profile.getPassword());
            }
        }
    }

    class SaveAction extends AbstractAction
    {
        public SaveAction()
        {
            super("Save");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            String current = dbCombo_.getEditor().getItem().toString();
            
            logger_.debug("Profile=" + current);
            boolean found = false;
            
            for (int i=0; i< dbCombo_.getItemCount(); i++)
            {
                DBProfile profile = (DBProfile) dbCombo_.getItemAt(i);
                
                if (profile.getDatabase().equals(current))
                {
                    profile.setDriver(driverField_.getText());
                    profile.setUrl(urlField_.getText());
                    profile.setUsername(userField_.getText());
                    profile.setPassword(passwordField_.getText());
                    found = true;
                    break;
                }
                else
                    found |= false;
            }
            
            if (!found)
            {
                DBProfile profile = new DBProfile(
                    current,
                    driverField_.getText(),
                    urlField_.getText(),
                    userField_.getText(),
                    passwordField_.getText());
                    
                dbCombo_.addItem(profile);
                dbCombo_.setSelectedItem(profile);    
            }   
        }
    }

    /**
     * Connects to the database
     */
    class ConnectAction extends WorkspaceAction
    {
        public ConnectAction()  
        {
            super("Connect", true, plugin_.getComponent(), statusBar_);
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
}