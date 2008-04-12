package toolbox.util.ui.splitpane;

import java.awt.Component;

import javax.swing.JSplitPane;

/**
 * SplitOrientation is responsible for horizontal or vertical orientaiton of 
 * a JMultiSplitPane.
 */
public abstract class SplitOrientation
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    private SplitOrientation()
    {
    }

    //--------------------------------------------------------------------------
    // Abstract Methods
    //--------------------------------------------------------------------------
    
    public abstract int getSplitPaneConstant();
    public abstract int getPreferredSize(Component component);
    public abstract int getSize(Component component);

    //--------------------------------------------------------------------------
    // HORIZONTAL
    //--------------------------------------------------------------------------
    
    public static final SplitOrientation HORIZONTAL = 
        new SplitOrientation()
    {
        public int getSplitPaneConstant()
        {
            return JSplitPane.HORIZONTAL_SPLIT;
        }


        public int getPreferredSize(Component component)
        {
            return component.getPreferredSize().width;
        }


        public int getSize(Component component)
        {
            return component.getWidth();
        }
    };

    //--------------------------------------------------------------------------
    // VERTICAL
    //--------------------------------------------------------------------------
    
    public static final SplitOrientation VERTICAL = 
        new SplitOrientation()
    {
        public int getSplitPaneConstant()
        {
            return JSplitPane.VERTICAL_SPLIT;
        }


        public int getPreferredSize(Component component)
        {
            return component.getPreferredSize().height;
        }


        public int getSize(Component component)
        {
            return component.getHeight();
        }
    };
}

/*
 * Sun Public License Notice The contents of this file are subject to the Sun
 * Public License Version 1.0 (the "License"); you may not use this file except
 * in compliance with the License. A copy of the License is available at
 * http://www.sun.com/ The Original Code is SoftSmithy Utility Library. The
 * Initial Developer of the Original Code is Florian Brunner (Sourceforge.net
 * user: puce). All Rights Reserved. Contributor(s): .
 */