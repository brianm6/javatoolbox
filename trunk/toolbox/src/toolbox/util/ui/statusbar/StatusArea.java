package toolbox.util.ui.statusbar;

import java.awt.Component;

/**
 * StatusArea Interface
 */
public interface StatusArea
{
    /**
     * @return True if the width is relative (variable), false otherwise
     */
    public boolean isRelativeWidth();
    
    /**
     * @param  component  Component to get required width of
     * @return Required width for the component
     */
    public float getRequiredWidth(Component component);
}

/*
Originally created by Claude Duguay
Copyright (c) 2000
*/

