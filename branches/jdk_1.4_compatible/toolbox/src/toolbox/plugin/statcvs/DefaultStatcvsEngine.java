package toolbox.plugin.statcvs;

import java.io.File;

import net.sf.statcvs.Main;
import net.sf.statcvs.output.CommandLineParser;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;

/**
 * Report engine that ships with Statcvs.
 */
public class DefaultStatcvsEngine implements StatcvsEngine
{
    private static final Logger logger_ =
        Logger.getLogger(DefaultStatcvsEngine.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Reference to the statcvs plugin.
     */
    private StatcvsPlugin plugin_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a DefaultStatcvsEngine. Necessary for instantiation via
     * reflection.
     */
    public DefaultStatcvsEngine()
    {
    }

    //--------------------------------------------------------------------------
    // StatcvsEngine Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.plugin.statcvs.StatcvsEngine#setPlugin(toolbox.plugin.statcvs.StatcvsPlugin)
     */
    public void setPlugin(StatcvsPlugin plugin)
    {
        plugin_ = plugin;
    }


    /*
     * @see toolbox.plugin.statcvs.StatcvsEngine#getLaunchURL()
     */
    public String getLaunchURL()
    {
        return
            "file://" +
            plugin_.getCVSBaseDir() +
            "statcvs" +
            File.separator +
            "index.html";

    }


    /*
     * @see toolbox.plugin.statcvs.StatcvsEngine#generateStats()
     */
    public void generateStats() throws Exception
    {
        String[] args = new String[]
        {
            "-verbose",
            //"-debug",
            "-output-dir", plugin_.getCVSBaseDir() + "statcvs",
            //"-title " + cvsModuleField_.getText(),
            plugin_.getCVSLogFile(),
            plugin_.getCVSBaseDir()
        };

        try
        {
            new CommandLineParser(args).parse();
            Main.generateDefaultHTMLSuite();
        }
        catch (Exception ee)
        {
            System.setSecurityManager(null);
            logger_.error("Generate Stats failed", ee);
            ExceptionUtil.handleUI(ee, logger_);
        }
    }
}
