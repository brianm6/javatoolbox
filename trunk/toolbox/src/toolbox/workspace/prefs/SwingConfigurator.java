package toolbox.workspace.prefs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.util.XOMUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartCheckBox;
import toolbox.util.ui.JSmartLabel;

/**
 * Configures proxy settings for the JVM. Plain old proxy servers are supported
 * in addition to those requiring user authentication. A button to test the
 * validity of the proxy information is on the same panel to provide immediate
 * feedback.
 */
public class SwingConfigurator extends JHeaderPanel implements IConfigurator
{
    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------

    public  static final String NODE_SWING            = "SwingConfigurator";
    private static final String ATTR_JAVA_2D_NO_DRAW  =   "java2dNoDraw";
    private static final String ATTR_USE_SYSTEM_FONTS =   "useSystemFonts";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Enables/disables entry of proxy settings.
     */
    private JSmartCheckBox java2dNoDrawCheckBox_;

    /**
     * Enables/disables entry of proxy settings.
     */
    private JSmartCheckBox useSystemFontsCheckBox_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a HttpProxyConfigurator.
     */
    public SwingConfigurator()
    {
        super("");
        setTitle(getLabel());
        buildView();
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 4, 7, 4);

        p.add(new JSmartLabel("Java 2D No Draw", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        p.add(java2dNoDrawCheckBox_ = new JSmartCheckBox(), gbc);

        gbc.gridy++;
        gbc.gridx--;
        p.add(new JSmartLabel("Use System Fonts", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        p.add(useSystemFontsCheckBox_ = new JSmartCheckBox(), gbc); 

        setContent(p);
    }

    //--------------------------------------------------------------------------
    // IConfigurator Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.prefs.IConfigurator#getLabel()
     */
    public String getLabel()
    {
        return "Swing";
    }


    /**
     * @see toolbox.workspace.prefs.IConfigurator#getView()
     */
    public JComponent getView()
    {
        return this;
    }

    
    /**
     * @see toolbox.workspace.prefs.IConfigurator#getIcon()
     */
    public Icon getIcon()
    {
        return ImageCache.getIcon(ImageCache.IMAGE_DUKE);
    }
    
    
    /**
     * @see toolbox.workspace.prefs.IConfigurator#onOK()
     */
    public void onOK()
    {
        onApply();
    }


    /**
     * @see toolbox.workspace.prefs.IConfigurator#onApply()
     */
    public void onApply()
    {
        System.setProperty(
            "sun.java2d.noddraw",
            java2dNoDrawCheckBox_.isSelected() + "");
        
        System.setProperty(
            "swing.useSystemFontSettngs", 
            useSystemFontsCheckBox_.isSelected() + "");
    }


    /**
     * @see toolbox.workspace.prefs.IConfigurator#onCancel()
     */
    public void onCancel()
    {
        // Nothing to do
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        
        Element swing =
            XOMUtil.getFirstChildElement(
                prefs, NODE_SWING, new Element(NODE_SWING));

        java2dNoDrawCheckBox_.setSelected(
            XOMUtil.getBooleanAttribute(
                swing,
                ATTR_JAVA_2D_NO_DRAW,
                false));

        useSystemFontsCheckBox_.setSelected(
            XOMUtil.getBooleanAttribute(
                swing,
                ATTR_USE_SYSTEM_FONTS,
                false));

        onApply();
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element swing = new Element(NODE_SWING);

        swing.addAttribute(
            new Attribute(
                ATTR_JAVA_2D_NO_DRAW,
                java2dNoDrawCheckBox_.isSelected() + ""));
        
        swing.addAttribute(
            new Attribute(
                ATTR_USE_SYSTEM_FONTS,
                useSystemFontsCheckBox_.isSelected() + ""));

        XOMUtil.insertOrReplace(prefs, swing);
    }
}