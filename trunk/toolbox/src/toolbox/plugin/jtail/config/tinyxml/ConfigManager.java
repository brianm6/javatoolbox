package toolbox.jtail.config.tinyxml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import toolbox.jtail.config.IConfigManager;
import toolbox.jtail.config.IJTailConfig;
import toolbox.jtail.config.ITailPaneConfig;
import toolbox.util.FileUtil;
import toolbox.util.SwingUtil;
import toolbox.util.xml.XMLNode;
import toolbox.util.xml.XMLParser;

/**
 * Configuration manager for tiny xml parser
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
     * Default consturctor
     */
    public ConfigManager()
    {
    }

    //--------------------------------------------------------------------------
    //  Interfaces - IConfigManager
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
            String xmlString = config.marshal().toString();
            FileUtil.setFileContents(configFile, xmlString, false);
            logger_.debug(method + "\n" + xmlString);
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
        String method = "[load  ] ";
        
        String userHome = System.getProperty("user.home");
        String filename = userHome + File.separator + CONFIG_FILE;
        File    xmlFile = new File(filename);

        // Create jtailConfig with defaults        
        IJTailConfig jtailConfig = new JTailConfig();
        
        jtailConfig.setDefaultConfig(new TailPaneConfig(
            null,
            ITailPaneConfig.DEFAULT_AUTOSCROLL,
            ITailPaneConfig.DEFAULT_LINENUMBERS,
            ITailPaneConfig.DEFAULT_ANTIALIAS,
            SwingUtil.getPreferredMonoFont(),
            ITailPaneConfig.DEFAULT_FILTER));
            
        jtailConfig.setTailConfigs(new TailPaneConfig[0]);
                
        if (!xmlFile.exists())
        {
            logger_.debug(method + "No XML configuration present. Using defaults.");
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
                XMLParser parser = new XMLParser();
                XMLNode jtailNode = parser.parseXML(new FileReader(xmlFile));
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