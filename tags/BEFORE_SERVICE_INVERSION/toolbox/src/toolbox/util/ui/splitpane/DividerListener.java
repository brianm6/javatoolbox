package toolbox.util.ui.splitpane;

import java.util.EventListener;

/**
 * DividerListener.
 */
public interface DividerListener extends EventListener 
{
    /**
     * Notification that the divider location has changed.
     * 
     * @param splitPane Splitpane containing the divider.
     * @param index Index of the divider.
     * @param location New location of the divider.
     */
    void locationChanged(JMultiSplitPane splitPane, int index, int location);
}

/*
 *                  Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is SoftSmithy Utility Library. The Initial Developer of the
 * Original Code is Florian Brunner (Sourceforge.net user: puce). All Rights Reserved.
 *
 * Contributor(s): .
 */