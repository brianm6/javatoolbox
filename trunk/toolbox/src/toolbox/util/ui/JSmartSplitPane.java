package toolbox.util.ui;

import java.awt.Component;

import javax.swing.JSplitPane;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;

/**
 * JSmartSplitPane is a split pane that can remember its divider location.
 */
public class JSmartSplitPane extends JSplitPane implements IPreferenced
{
    // Preferences
    public static final String NODE_JSPLITPANE = "JSplitPane";
    public static final String ATTR_DIVIDER_LOCATION = "dividerLocation";

    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartSplitPane.
     */
    public JSmartSplitPane()
    {
    }


    /**
     * Creates a JSmartSplitPane.
     * 
     * @param newOrientation Orientation
     */
    public JSmartSplitPane(int newOrientation)
    {
        super(newOrientation);
    }


    /**
     * Creates a JSmartSplitPane.
     * 
     * @param newOrientation Orientation
     * @param newContinuousLayout Continuous layout
     */
    public JSmartSplitPane(int newOrientation, boolean newContinuousLayout)
    {
        super(newOrientation, newContinuousLayout);
    }


    /**
     * Creates a JSmartSplitPane.
     * 
     * @param newOrientation Orientation
     * @param newLeftComponent Left hand side component
     * @param newRightComponent Right hand side component
     */
    public JSmartSplitPane(
        int newOrientation,
        Component newLeftComponent,
        Component newRightComponent)
    {
        super(newOrientation, newLeftComponent, newRightComponent);
    }


    /**
     * Creates a JSmartSplitPane.
     * 
     * @param newOrientation Orientation
     * @param newContinuousLayout Continuous layout
     * @param newLeftComponent Left hand side component
     * @param newRightComponent Right hand side component
     */
    public JSmartSplitPane(
        int newOrientation,
        boolean newContinuousLayout,
        Component newLeftComponent,
        Component newRightComponent)
    {
        super(
            newOrientation,
            newContinuousLayout,
            newLeftComponent,
            newRightComponent);
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface 
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs)
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_JSPLITPANE, new Element(NODE_JSPLITPANE));
        
        setDividerLocation(
            XOMUtil.getIntegerAttribute(
                root, 
                ATTR_DIVIDER_LOCATION, 
                getDividerLocation()));
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element root = new Element(NODE_JSPLITPANE);
        
        root.addAttribute(
            new Attribute(
                ATTR_DIVIDER_LOCATION,
                getDividerLocation()+""));
            
        XOMUtil.insertOrReplace(prefs, root);
    }
}