package toolbox.util.ui.statusbar;

import java.awt.Component;

/**
 * StatusArea Interface.
 * <p>
 * Originally created by Claude Duguay<br>
 * Copyright (c) 2000<br>
 */
public interface StatusArea
{
    /**
     * Returns true if the width is relative (variable), false otherwise.
     * 
     * @return boolean
     */
    public boolean isRelativeWidth();
    
    
    /**
     * Returns the required width of the given component.
     * 
     * @param component Component to get required width of
     * @return Required width for the component
     */
    public float getRequiredWidth(Component component);
}