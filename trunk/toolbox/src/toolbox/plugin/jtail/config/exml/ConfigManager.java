package toolbox.jtail.config.exml;

import java.io.File;
import java.io.IOException;

import electric.xml.Document;
import electric.xml.Element;

import org.apache.log4j.Logger;

import toolbox.jtail.config.IConfigManager;
import toolbox.jtail.config.IJTailConfig;
import toolbox.jtail.config.ITailPaneConfig;

/**
 * Configuration manager for Electric XML persistence strategy
 */
public class ConfigManager implements IConfigManager
{
    private static final Logger logger_ = 
        Logger.getLogger(ConfigManager.class);
        
    private static final String CONFIG_FILE = ".jtail.xml";
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for ConfigManager.
     */
    public ConfigManager()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //  IConfigManager Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.jtail.IConfigManager#save(IJTailConfig)
     */
    public void save(IJTailConfig jtailConfig)
    {
        String method = "[save  ] ";
        
        JTailConfig config = (JTailConfig)jtailConfig;
        
        String userHome = System.getProperty("user.home");
        String filename = userHome + File.separator + CONFIG_FILE;
        File configFile = new File(filename);

        try
        {
            Document document = new Document();    
            document.setRoot(config.marshal());
            document.write(configFile);
            
            logger_.debug(method + "\n" + document);
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
            jtailConfig.setDefaultConfig(defaultConfig);            
            jtailConfig.setTailConfigs(new TailPaneConfig[0]);

        }
        else if (!xmlFile.canRead())
        {
            throw new IllegalArgumentException(
                "Cannot read configuration from " + filename + ". " + 
                "Using defaults.");
        }
        else if (!xmlFile.isFile())
        {
            throw new IllegalArgumentException(
                "Configuration file " + filename + 
                " cannot be a directory. Using defaults.");
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
   
    /**
     * @see toolbox.jtail.config.IConfigManager#createTailPaneConfig()
     */
    public ITailPaneConfig createTailPaneConfig()
    {
        return new TailPaneConfig();
    }
}
