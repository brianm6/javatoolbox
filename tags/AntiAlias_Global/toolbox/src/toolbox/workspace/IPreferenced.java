package toolbox.util.ui.plugin;

import nu.xom.Element;

/**
 * IPreferenced provides a simple interface for saveing and restoring
 * preferences between application instances.
 */
public interface IPreferenced
{
    /**
     * Reads preferences from the prefs DOM and applies them to the preferenced 
     * target.
     * 
     * @param  prefs  Document object model from which to read preferences. 
     * @throws Exception on error
     */
    public void applyPrefs(Element prefs) throws Exception;
    
    /**
     * Saves preferences to the prefs DOM.
     *  
     * @param  prefs  Document object model to persist preferences to.
     */
    public void savePrefs(Element prefs) throws Exception;
    
}