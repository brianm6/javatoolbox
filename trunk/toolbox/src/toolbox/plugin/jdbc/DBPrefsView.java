package toolbox.plugin.jdbc;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.util.XOMUtil;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartCheckBox;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.workspace.prefs.PreferencesView;

/**
 * JDBC Plugin Preferences.
 */
public class DBPrefsView extends JHeaderPanel implements PreferencesView
{
    //--------------------------------------------------------------------------
    // XML Constants
    //--------------------------------------------------------------------------

    public  static final String NODE_JDBC_PLUGIN          = "DBPrefsView";
    private static final String ATTR_ERROR_PANE_ENABLED   = "errorPaneEnabled";
    private static final String ATTR_AUTOSCROLL_THRESHOLD = "autoscrollThreshold";
    private static final String ATTR_SQL_TERMINATOR       = "sqlTerminator";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Enables routing of JDBC error messages to an error page instead of 
     * popping up a modal dialog box each time an error occurs. This is not 
     * enabled by default.
     */
    private JSmartCheckBox errorPaneEnabledCheckBox_;

    /**
     * Sequence of characters that the SQL parser will use to determine the 
     * termination of a SQL in the editor pane. The default value is a 
     * semicolon.
     */
    private JSmartTextField sqlTerminatorField_;

    /**
     * Holds a positive number that specifies the number of lines of output 
     * that must occur for the output pane to automatically scroll to the bottom
     * after the execution of a sql statement.
     */
    private JSmartTextField autoscrollThresholdField_;

//    /**
//     * Field for the username if the proxy requires authentication. If the
//     * field is left blank, then it us assumed that the proxy does not require
//     * authentication.
//     */
//    private JSmartTextField proxyUserNameField_;
//
//    /**
//     * Field for the passward if the proxy requires authentication. The password
//     * is stored in clear text.
//     */
//    protected JSmartTextField proxyPasswordField_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a DBPrefsView.
     */
    public DBPrefsView()
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

        p.add(new JSmartLabel("Use Error Pane", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        p.add(errorPaneEnabledCheckBox_ = new JSmartCheckBox(), gbc);

        gbc.gridy++;
        gbc.gridx--;
        p.add(new JSmartLabel("SQL Terminator", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        p.add(sqlTerminatorField_ = new JSmartTextField(14), gbc);

        gbc.gridy++;
        gbc.gridx--;
        p.add(new JSmartLabel("Autoscroll Threshold", SwingConstants.RIGHT),
            gbc);

        gbc.gridx++;
        p.add(autoscrollThresholdField_ = new JSmartTextField(4), gbc);

//        gbc.gridy++;
//        gbc.gridx--;
//        p.add(new JSmartLabel("Username", SwingConstants.RIGHT), gbc);
//
//        gbc.gridx++;
//        p.add(proxyUserNameField_ = new JSmartTextField(14), gbc);
//
//        gbc.gridy++;
//        gbc.gridx--;
//        p.add(new JSmartLabel("Password", SwingConstants.RIGHT), gbc);
//
//        gbc.gridx++;
//        p.add(proxyPasswordField_ = new JSmartTextField(14), gbc);
//
//        gbc.gridy++;
//        p.add(new JSmartButton(new TestProxyAction()), gbc);

        setContent(p);
    }

    //--------------------------------------------------------------------------
    // PreferencesView Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.prefs.PreferencesView#getLabel()
     */
    public String getLabel()
    {
        return "JDBC Plugin Preferences";
    }


    /**
     * @see toolbox.workspace.prefs.PreferencesView#getView()
     */
    public JComponent getView()
    {
        return this;
    }


    /**
     * @see toolbox.workspace.prefs.PreferencesView#onOK()
     */
    public void onOK()
    {
        onApply();
    }


    /**
     * @see toolbox.workspace.prefs.PreferencesView#onApply()
     */
    public void onApply()
    {
    }


    /**
     * @see toolbox.workspace.prefs.PreferencesView#onCancel()
     */
    public void onCancel()
    {
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element prefsView =
            XOMUtil.getFirstChildElement(
                prefs, NODE_JDBC_PLUGIN, new Element(NODE_JDBC_PLUGIN));

        errorPaneEnabledCheckBox_.setSelected(
            XOMUtil.getBooleanAttribute(
                prefsView,
                ATTR_ERROR_PANE_ENABLED,
                false));

        sqlTerminatorField_.setText(
            XOMUtil.getStringAttribute(
                prefsView,
                ATTR_SQL_TERMINATOR,
                ";"));

        autoscrollThresholdField_.setText(
            XOMUtil.getStringAttribute(
                prefsView,
                ATTR_AUTOSCROLL_THRESHOLD,
                "50"));

//        proxyUserNameField_.setText(
//            XOMUtil.getStringAttribute(
//                prefsView,
//                ATTR_HTTP_PROXY_USERNAME,
//                ""));
//
//        proxyPasswordField_.setText(
//            XOMUtil.getStringAttribute(
//                prefsView,
//                ATTR_HTTP_PROXY_PASSWORD,
//                ""));
        onApply();
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element prefsView = new Element(NODE_JDBC_PLUGIN);

        prefsView.addAttribute(
            new Attribute(
                ATTR_ERROR_PANE_ENABLED,
                errorPaneEnabledCheckBox_.isSelected() + ""));

        prefsView.addAttribute(
            new Attribute(
                ATTR_SQL_TERMINATOR,
                sqlTerminatorField_.getText()));

        prefsView.addAttribute(
            new Attribute(
                ATTR_AUTOSCROLL_THRESHOLD,
                autoscrollThresholdField_.getText().trim()));

//        prefsView.addAttribute(
//            new Attribute(
//                ATTR_HTTP_PROXY_USERNAME,
//                proxyUserNameField_.getText().trim()));
//
//        prefsView.addAttribute(
//            new Attribute(
//                ATTR_HTTP_PROXY_PASSWORD,
//                proxyPasswordField_.getText().trim()));

        XOMUtil.insertOrReplace(prefs, prefsView);
    }
}