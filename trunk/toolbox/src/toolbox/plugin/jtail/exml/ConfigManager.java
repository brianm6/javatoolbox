package toolbox.jtail.exml;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Category;
import toolbox.jtail.config.IConfigManager;
import toolbox.jtail.config.IJTailConfig;
import toolbox.jtail.config.ITailPaneConfig;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartOptionPane;

import electric.xml.Document;
import electric.xml.Element;

/**
 * Configuration manager for Electric XML persistence strategy
 */
public class ConfigManager implements IConfigManager
{
    private static final Category logger_ = 
        Category.getInstance(ConfigManager.class);
        
    private static final String CONFIG_FILE = ".jtail.xml";
    
    /**
     * Constructor for ConfigManager.
     */
    public ConfigManager()
    {
        super();
    }

    /**
     * @see toolbox.jtail.IConfigManager#save(IJTailConfig)
     */
    public void save(IJTailConfig jtailConfig)
    {
        JTailConfig config = (JTailConfig)jtailConfig;
        
        String userHome = System.getProperty("user.home");
        String filename = userHome + File.separator + CONFIG_FILE;
        File configFile = new File(filename);

        try
        {
            Document document = new Document();    
            document.setRoot(config.marshal());
            document.write(configFile);
            
            logger_.debug("\n" + document);
        }
        catch (IOException ioe)
        {
            throw new IllegalArgumentException(ioe.getMessage());
        }
    }

    /**
     * @see toolbox.jtail.IConfigManager#load()
     */
    public IJTailConfig load()
    {
        String userHome = System.getProperty("user.home");
        String filename = userHome + File.separator + CONFIG_FILE;
        File    xmlFile = new File(filename);
        
        IJTailConfig jtailConfig = new JTailConfig();
        ITailPaneConfig defaultConfig = new TailPaneConfig();
        
        if (!xmlFile.exists())
        {
            TailPaneConfig crap = new TailPaneConfig(
                null,
                ITailPaneConfig.DEFAULT_AUTOSCROLL,
                ITailPaneConfig.DEFAULT_LINENUMBERS,
                ITailPaneConfig.DEFAULT_ANTIALIAS,
                SwingUtil.getPreferredMonoFont(),
                ITailPaneConfig.DEFAULT_FILTER);

            jtailConfig.setDefaultConfig(defaultConfig);            
            jtailConfig.setTailConfigs(new TailPaneConfig[0]);

        }
        else if (!xmlFile.canRead())
        {
            throw new IllegalArgumentException("Cannot read configuration from " 
                + filename + ". " + "Using defaults.");
        }
        else if (!xmlFile.isFile())
        {
            throw new IllegalArgumentException("Configuration file " + filename 
                + " cannot be a directory. Using defaults.");
        }
        else
        {
            try
            {
                Document config = new Document(xmlFile);            
                Element jtailNode = config.getRoot();
                jtailConfig = JTailConfig.unmarshal(jtailNode);
            }
            catch (Exception pe)
            {
                throw new IllegalArgumentException(pe.getMessage());       
            }
        }
        
        return jtailConfig;
    }
}
