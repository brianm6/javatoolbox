package toolbox.util.ui;

import java.awt.Component;

import javax.swing.JSplitPane;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;

/**
 * JSmartSplitPane
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
     * Creates a JSmartSplitPane
     */
    public JSmartSplitPane()
    {
    }

    /**
     * @param newOrientation
     */
    public JSmartSplitPane(int newOrientation)
    {
        super(newOrientation);
    }

    /**
     * @param newOrientation
     * @param newContinuousLayout
     */
    public JSmartSplitPane(int newOrientation, boolean newContinuousLayout)
    {
        super(newOrientation, newContinuousLayout);
    }

    /**
     * @param newOrientation
     * @param newLeftComponent
     * @param newRightComponent
     */
    public JSmartSplitPane(
        int newOrientation,
        Component newLeftComponent,
        Component newRightComponent)
    {
        super(newOrientation, newLeftComponent, newRightComponent);
    }

    /**
     * @param newOrientation
     * @param newContinuousLayout
     * @param newLeftComponent
     * @param newRightComponent
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
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(nu.xom.Element)
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
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(nu.xom.Element)
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