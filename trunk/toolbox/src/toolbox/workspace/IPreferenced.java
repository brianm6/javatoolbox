package toolbox.util.ui.plugin;

import java.util.Properties;

/**
 * IPreferenced provides a simple interface for saveing and restoring
 * preferences between application instances.
 */
public interface IPreferenced
{
    /**
     * Saves preferences to the provided preferences object.
     *  
     * @param  prefs  Properties object to save the implementor preferences to.
     */
    public void savePrefs(Properties prefs);
    
    /**
     * Reads preferences from the provided preferences object an apply them to
     * the implementor.
     * 
     * @param  prefs  Properties object from which to read the implementors 
     *                preferences from. 
     * @throws Exception on error
     */
    public void applyPrefs(Properties prefs) throws Exception;
}