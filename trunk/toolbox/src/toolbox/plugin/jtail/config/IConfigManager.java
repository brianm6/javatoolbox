package toolbox.jtail.config;



/**
 * enclosing_type
 */
public interface IConfigManager
{
    public void save(IJTailConfig jtailConfig);
    public IJTailConfig load();
}
