package toolbox.jtail.config.tinyxml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import toolbox.jtail.config.IConfigManager;
import toolbox.jtail.config.IJTailConfig;
import toolbox.jtail.config.ITailPaneConfig;
import toolbox.util.FileUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XMLUtil;
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
    //  IConfigManager Interface
    //--------------------------------------------------------------------------
    
    public void save(IJTailConfig jtailConfig)
    {
        JTailConfig config = (JTailConfig)jtailConfig;
        
        String userHome = System.getProperty("user.home");
        String filename = userHome + File.separator + CONFIG_FILE;
        File configFile = new File(filename);

        try
        {
            String xmlString = config.marshal().toString();
            FileUtil.setFileContents(configFile, xmlString, false);
            logger_.debug("\n" + XMLUtil.format(xmlString));
        }
        catch (IOException ioe)
        {
            throw new IllegalArgumentException(ioe.getMessage());
        } 
        catch (SAXException se)
        {
            throw new IllegalArgumentException(se.getMessage());
        }
    }

    public IJTailConfig load()
    {
        String userHome = System.getProperty("user.home");
        String filename = userHome + File.separator + CONFIG_FILE;
        File    xmlFile = new File(filename);

        // Create jtailConfig with defaults        
        IJTailConfig jtailConfig = new JTailConfig();
        
        jtailConfig.setDefaultConfig(
            new TailPaneConfig(
                null,
                ITailPaneConfig.DEFAULT_AUTOSCROLL,
                ITailPaneConfig.DEFAULT_LINENUMBERS,
                ITailPaneConfig.DEFAULT_ANTIALIAS,
                SwingUtil.getPreferredMonoFont(),
                ITailPaneConfig.DEFAULT_REGEX,
                ITailPaneConfig.DEFAULT_CUT_EXPRESSION,
                ITailPaneConfig.DEFAULT_AUTOSTART));
            
        jtailConfig.setTailConfigs(new TailPaneConfig[0]);
                
        if (!xmlFile.exists())
        {
            logger_.debug("No XML configuration present. Using defaults.");
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
    
    public ITailPaneConfig createTailPaneConfig()
    {
        return new TailPaneConfig();
    }
}