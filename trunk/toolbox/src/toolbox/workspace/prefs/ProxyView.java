package toolbox.workspace.prefs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartCheckBox;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.workspace.IPreferenced;

/**
 * ProxyView is responsible for ___.
 */
public class ProxyView extends JHeaderPanel implements PreferencesView, IPreferenced
{
    //--------------------------------------------------------------------------
    // XML Constants
    //--------------------------------------------------------------------------

    private static final String NODE_HTTP_PROXY = "HttpProxy";
    private static final String ATTR_HTTP_PROXY_ENABLED = "enabled";
    private static final String ATTR_HTTP_PROXY_HOST = "hostname";
    private static final String ATTR_HTTP_PROXY_PORT = "port";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private JSmartTextField proxyHostnameField_;
    private JSmartTextField proxyPortField_;
    private JSmartCheckBox proxyEnabledCheckBox_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a ProxyView.
     */
    public ProxyView()
    {
        super("???");
        setTitle(getLabel());
        buildView();
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Builds the proxy panel.
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
        
        p.add(new JSmartLabel("Enable Proxy", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        p.add(proxyEnabledCheckBox_ = new JSmartCheckBox(), gbc);
        
        gbc.gridy++;
        gbc.gridx--;
        p.add(new JSmartLabel("Hostname", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        p.add(proxyHostnameField_ = new JSmartTextField(14), gbc);

        gbc.gridy++;
        gbc.gridx--;
        p.add(new JSmartLabel("Port", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        p.add(proxyPortField_ = new JSmartTextField(14), gbc);
        
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
        return "HTTP Proxy";
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
        if (proxyEnabledCheckBox_.isSelected())
        {
            System.setProperty("proxyHost", proxyHostnameField_.getText());
            System.setProperty("proxyPort", proxyPortField_.getText());
        }
        else
        {
            System.setProperty("proxyHost", "");
            System.setProperty("proxyPort", "");
        }
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
        Element httpProxy =
            XOMUtil.getFirstChildElement(
                prefs, NODE_HTTP_PROXY, new Element(NODE_HTTP_PROXY));

        proxyEnabledCheckBox_.setSelected(
            XOMUtil.getBooleanAttribute(
                httpProxy,
                ATTR_HTTP_PROXY_ENABLED,
                false));

        proxyHostnameField_.setText(
            XOMUtil.getStringAttribute(
                httpProxy,
                ATTR_HTTP_PROXY_HOST,
                ""));

        proxyPortField_.setText(
            XOMUtil.getStringAttribute(
                httpProxy,
                ATTR_HTTP_PROXY_PORT,
                ""));

        onApply();
    }
    
    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element httpProxy = new Element(NODE_HTTP_PROXY);

        httpProxy.addAttribute(
            new Attribute(
                ATTR_HTTP_PROXY_ENABLED,
                proxyEnabledCheckBox_.isSelected() + ""));
        
        if (!StringUtil.isNullOrBlank(proxyHostnameField_.getText()))
            httpProxy.addAttribute(
                new Attribute(
                    ATTR_HTTP_PROXY_HOST,
                    proxyHostnameField_.getText().trim()));

        if (!StringUtil.isNullOrBlank(proxyPortField_.getText()))
            httpProxy.addAttribute(
                new Attribute(
                    ATTR_HTTP_PROXY_PORT,
                    proxyPortField_.getText().trim()));

        httpProxy.appendChild(httpProxy);
        XOMUtil.insertOrReplace(prefs, httpProxy);
    }
}