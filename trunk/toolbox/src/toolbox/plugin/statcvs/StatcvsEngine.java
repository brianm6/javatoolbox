package toolbox.plugin.statcvs;

/**
 * StatcvsEngine defines the interface of a report engine that generates cvs
 * documentation.
 */
public interface StatcvsEngine
{
    /**
     * Returns the browser launch URL for the generated reports and
     * documentation.
     *
     * @return String
     */
    String getLaunchURL();


    /**
     * Generates cvs statistics.
     */
    void generateStats();


    /**
     * Once the engine has been instantiated, it need to be passed a reference
     * to the plugin to establish context.
     *
     * @param plugin StatcvsPlugin.
     */
    void setPlugin(StatcvsPlugin plugin);
}
