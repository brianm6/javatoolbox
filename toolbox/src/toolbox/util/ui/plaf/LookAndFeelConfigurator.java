package toolbox.util.ui.plaf;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartCheckBox;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.font.FontChooserException;
import toolbox.util.ui.font.JFontChooser;
import toolbox.workspace.PreferencedException;
import toolbox.workspace.prefs.IConfigurator;

/**
 * Configures Look and Feel related preferences. Rendered as a panel in the
 * Workspace Preferences dialog box.
 */
public class LookAndFeelConfigurator extends JHeaderPanel 
    implements IConfigurator
{
    private static final Logger logger_ = 
        Logger.getLogger(LookAndFeelConfigurator.class);
    
    //--------------------------------------------------------------------------
    // IPreferened Constants
    //--------------------------------------------------------------------------

    public static final String NODE_LOOK_AND_FEEL = "LookAndFeel";
    public static final String NODE_FONT_OVERRIDE = "FontOverride";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Enables/disables font overrides for the installed look and feel.
     */
    private JSmartCheckBox fontOverrideCheckBox_;

    /**
     * Chooser component for the override font.
     */
    private JFontChooser fontOverrideChooser_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a LookAndFeelConfigurator.
     */
    public LookAndFeelConfigurator()
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
        JPanel p1 = new JPanel(new FlowLayout());
        fontOverrideCheckBox_ = new JSmartCheckBox(new OverrideEnabledAction());
        p1.add(new JSmartLabel("Override font", SwingConstants.LEFT));
        p1.add(fontOverrideCheckBox_);
        
        fontOverrideChooser_ = 
            new JFontChooser(UIManager.getFont("Label.font"));
        
        JPanel content = new JPanel(new BorderLayout());
        content.add(p1, BorderLayout.NORTH);
        content.add(fontOverrideChooser_, BorderLayout.CENTER);
        setContent(content);
    }

    //--------------------------------------------------------------------------
    // IConfigurator Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.prefs.IConfigurator#getLabel()
     */
    public String getLabel()
    {
        return "Look & Feel";
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
        return ImageCache.getIcon(ImageCache.IMAGE_PIE_CHART);
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
        if (fontOverrideCheckBox_.isSelected())
        {
            try
            {
                Font f = fontOverrideChooser_.getSelectedFont();
                UIManager.put("Button.font", f);
                UIManager.put("DesktopIcon.font", f);
                UIManager.put("ComboBox.font", f);
                UIManager.put("CheckBox.font", f);
                UIManager.put("CheckBoxMenuItem.font", f);
                UIManager.put("ColorChooser.font", f);
                UIManager.put("EditorPane.font", f);
                UIManager.put("FormattedTextField.font", f);
                UIManager.put("InternalFrame.titleFont", f);
                UIManager.put("Label.font", f);
                UIManager.put("List.font", f);
                UIManager.put("Menu.font", f);
                UIManager.put("MenuBar.font", f);
                UIManager.put("MenuItem.font", f);
                UIManager.put("MenuItem.acceleratorFont", f);
                UIManager.put("OptionPane.font", f);
                UIManager.put("Panel.font", f);
                UIManager.put("PasswordField.font", f);
                UIManager.put("PopupMenu.font", f);
                UIManager.put("ProgressBar.font", f);
                UIManager.put("RadioButton.font", f);
                UIManager.put("RadioButtonMenuItem.font", f);
                UIManager.put("RadioButtonMenuItem.acceleratorFont", f);
                UIManager.put("RootPane.titleFont", f);
                UIManager.put("ScrollPane.font", f);
                UIManager.put("Spinner.font", f);
                UIManager.put("TabbedPane.font", f);
                UIManager.put("Table.font", f);
                UIManager.put("TableHeader.font", f);
                UIManager.put("TextArea.font", f);
                UIManager.put("TextField.font", f);
                UIManager.put("TextPane.font", f);
                UIManager.put("TitledBorder.font", f);
                UIManager.put("ToggleButton.font", f);
                UIManager.put("Toolbar.font", f);
                UIManager.put("ToolTip.font", f);
                UIManager.put("Tree.font", f);
                UIManager.put("Viewport.font", f);
                
                LookAndFeelUtil.propagateChangeInLAF();
            }
            catch (FontChooserException e)
            {
                ExceptionUtil.handleUI(e, logger_);
            }
        }
        else
        {
        }
    }


    /**
     * @see toolbox.workspace.prefs.IConfigurator#onCancel()
     */
    public void onCancel()
    {
        // Nothing to do
    }

    
    /**
     * @see toolbox.workspace.prefs.IConfigurator#isApplyOnStartup()
     */
    public boolean isApplyOnStartup()
    {
        return false;
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
//        Element httpProxy =
//            XOMUtil.getFirstChildElement(
//                prefs, NODE_HTTP_PROXY, new Element(NODE_HTTP_PROXY));
//
//        fontOverrideCheckBox_.setSelected(
//            XOMUtil.getBooleanAttribute(
//                httpProxy,
//                ATTR_HTTP_PROXY_ENABLED,
//                false));
//
//        proxyHostnameField_.setText(
//            XOMUtil.getStringAttribute(
//                httpProxy,
//                ATTR_HTTP_PROXY_HOST,
//                ""));
//
//        proxyPortField_.setText(
//            XOMUtil.getStringAttribute(
//                httpProxy,
//                ATTR_HTTP_PROXY_PORT,
//                ""));
//
//        proxyUserNameField_.setText(
//            XOMUtil.getStringAttribute(
//                httpProxy,
//                ATTR_HTTP_PROXY_USERNAME,
//                ""));
//
//        proxyPasswordField_.setText(
//            XOMUtil.getStringAttribute(
//                httpProxy,
//                ATTR_HTTP_PROXY_PASSWORD,
//                ""));
//
//        new OverrideEnabledAction().actionPerformed();
//        onApply();
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
//        Element httpProxy = new Element(NODE_HTTP_PROXY);
//
//        httpProxy.addAttribute(
//            new Attribute(
//                ATTR_HTTP_PROXY_ENABLED,
//                fontOverrideCheckBox_.isSelected() + ""));
//
//        httpProxy.addAttribute(
//            new Attribute(
//                ATTR_HTTP_PROXY_HOST,
//                proxyHostnameField_.getText().trim()));
//
//        httpProxy.addAttribute(
//            new Attribute(
//                ATTR_HTTP_PROXY_PORT,
//                proxyPortField_.getText().trim()));
//
//        httpProxy.addAttribute(
//            new Attribute(
//                ATTR_HTTP_PROXY_USERNAME,
//                proxyUserNameField_.getText().trim()));
//
//        httpProxy.addAttribute(
//            new Attribute(
//                ATTR_HTTP_PROXY_PASSWORD,
//                proxyPasswordField_.getText().trim()));
//
//        XOMUtil.insertOrReplace(prefs, httpProxy);
    }

    //--------------------------------------------------------------------------
    // OverrideEnabledAction
    //--------------------------------------------------------------------------

    /**
     * Enables/disables the font chooser based on the enabled state of the
     * override checkbox.
     */
    class OverrideEnabledAction extends AbstractAction
    {
        //----------------------------------------------------------------------
        // ActionListener Interface
        //----------------------------------------------------------------------
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            actionPerformed();
        }

        //----------------------------------------------------------------------
        // Public
        //----------------------------------------------------------------------
        
        /**
         * Non-event triggered execution.
         */
        public void actionPerformed()
        {
            boolean isEnabled = fontOverrideCheckBox_.isSelected();
            fontOverrideChooser_.setEnabled(isEnabled);
        }
    }
}