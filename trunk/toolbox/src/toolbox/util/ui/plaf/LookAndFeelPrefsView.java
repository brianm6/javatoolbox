package toolbox.util.ui.plaf;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartCheckBox;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.font.FontChooserException;
import toolbox.util.ui.font.JFontChooser;
import toolbox.workspace.prefs.Preferences;

/**
 * Configures Look and Feel related preferences.
 */
public class LookAndFeelPrefsView extends JHeaderPanel implements Preferences
{
    private static final Logger logger_ = 
        Logger.getLogger(LookAndFeelPrefsView.class);
    
    //--------------------------------------------------------------------------
    // XML Constants
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
     * Creates a LookAndFeelPrefsView.
     */
    public LookAndFeelPrefsView()
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
    // Preferences Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.prefs.Preferences#getLabel()
     */
    public String getLabel()
    {
        return "Look & Feel";
    }


    /**
     * @see toolbox.workspace.prefs.Preferences#getView()
     */
    public JComponent getView()
    {
        return this;
    }


    /**
     * @see toolbox.workspace.prefs.Preferences#onOK()
     */
    public void onOK()
    {
        onApply();
    }


    /**
     * @see toolbox.workspace.prefs.Preferences#onApply()
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
     * @see toolbox.workspace.prefs.Preferences#onCancel()
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
    public void savePrefs(Element prefs) throws Exception
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
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            actionPerformed();
        }

        
        /**
         * Non-event triggered execution.
         */
        public void actionPerformed()
        {
            boolean enabled = fontOverrideCheckBox_.isSelected();
            fontOverrideChooser_.setEnabled(enabled);
        }
    }
}