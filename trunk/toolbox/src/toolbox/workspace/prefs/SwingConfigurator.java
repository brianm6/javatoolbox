package toolbox.workspace.prefs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.commons.lang.StringUtils;

import toolbox.util.XOMUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartCheckBox;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;

/**
 * Configures proxy settings for the JVM. Plain old proxy servers are supported
 * in addition to those requiring user authentication. A button to test the
 * validity of the proxy information is on the same panel to provide immediate
 * feedback.
 */
public class SwingConfigurator extends JHeaderPanel implements IConfigurator
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    
    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------

    public  static final String NODE_HTTP_PROXY          = "CRAP";
    private static final String ATTR_HTTP_PROXY_ENABLED  =   "enabled";
    private static final String ATTR_HTTP_PROXY_HOST     =   "hostname";
    private static final String ATTR_HTTP_PROXY_PORT     =   "port";
    private static final String ATTR_HTTP_PROXY_USERNAME =   "username";
    private static final String ATTR_HTTP_PROXY_PASSWORD =   "password";

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

    /**
     * Field for the proxy hostname. Can also be an IP address.
     */
    private JSmartTextField proxyHostnameField_;

    /**
     * Field for the proxy port. Must be a valid integer.
     */
    private JSmartTextField proxyPortField_;

    /**
     * Field for the username if the proxy requires authentication. If the
     * field is left blank, then it us assumed that the proxy does not require
     * authentication.
     */
    private JSmartTextField proxyUserNameField_;

    /**
     * Field for the passward if the proxy requires authentication. The password
     * is stored in clear text.
     */
    protected JPasswordField proxyPasswordField_;

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
        p.add(java2dNoDrawCheckBox_ = makeCheckBox("sun.java2d.noddraw"), gbc);

        gbc.gridy++;
        gbc.gridx--;
        p.add(new JSmartLabel("Use System Fonts", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        p.add(useSystemFontsCheckBox_ = 
            makeCheckBox("swing.useSystemFontSettngs"), gbc);

        gbc.gridy++;
        gbc.gridx--;
        p.add(new JSmartLabel("Port", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        p.add(proxyPortField_ = new JSmartTextField(14), gbc);

        gbc.gridy++;
        gbc.gridx--;
        p.add(new JSmartLabel("Username", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        p.add(proxyUserNameField_ = new JSmartTextField(14), gbc);

        gbc.gridy++;
        gbc.gridx--;
        p.add(new JSmartLabel("Password", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        p.add(proxyPasswordField_ = new JPasswordField(14), gbc);

//        gbc.gridy++;
//        p.add(new JSmartButton(new TestProxyAction()), gbc);

        setContent(p);
    }

    //--------------------------------------------------------------------------
    // IConfigurator Interface
    //--------------------------------------------------------------------------

    /**
     * 
     */
    private JSmartCheckBox makeCheckBox(String property)
    {
        JSmartCheckBox cb = new JSmartCheckBox();
        cb.setAction(new CheckBoxPropertyAction(cb, property));
        return cb;
    }


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
        if (java2dNoDrawCheckBox_.isSelected())
        {
            System.setProperty("proxySet", "true");
            System.setProperty("proxyHost", proxyHostnameField_.getText());
            System.setProperty("proxyPort", proxyPortField_.getText());

            if (!StringUtils.isBlank(proxyUserNameField_.getText()))
            {
                Authenticator.setDefault(new Authenticator()
                {
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication(
                            proxyUserNameField_.getText(),
                            proxyPasswordField_.getPassword());

                            //proxyPassword == null ? new char[0] : proxyPassword.toCharArray() );
                    }
                });
            }
        }
        else
        {
            System.setProperty("proxySet", "false");
            System.setProperty("proxyHost", "");
            System.setProperty("proxyPort", "");
        }
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
//        Element httpProxy =
//            XOMUtil.getFirstChildElement(
//                prefs, NODE_HTTP_PROXY, new Element(NODE_HTTP_PROXY));
//
//        java2dNoDrawCheckBox_.setSelected(
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
//                java2dNoDrawCheckBox_.isSelected() + ""));
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
//                new String(proxyPasswordField_.getPassword()).trim()));
//
//        XOMUtil.insertOrReplace(prefs, httpProxy);
    }

    //--------------------------------------------------------------------------
    // ProxyEnabledAction
    //--------------------------------------------------------------------------

    /**
     * Sets the editability of the host and port fields based on whether the
     * proxy is enabled or not.
     */
    class Java2dNoDrawAction extends AbstractAction
    {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
           setEnabled(java2dNoDrawCheckBox_.isSelected());
        }
        
        public void setEnabled(boolean b)
        {
            System.setProperty("sun.java2d.noddraw", b + "");
        }
    
    }

    
    /**
     * Sets the editability of the host and port fields based on whether the
     * proxy is enabled or not.
     */
    class CheckBoxPropertyAction extends AbstractAction
    {
        private JCheckBox cb_;
        private String prop_;
        
        /**
         * Creates a CheckBoxPropertyAction.
         * 
         * @param cb
         * @param prop
         */
        public CheckBoxPropertyAction(JCheckBox cb, String prop)
        {
            cb_ = cb;
            prop_ = prop;
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
           setEnabled(cb_.isSelected());
        }
        
        
        public void setEnabled(boolean b)
        {
            System.setProperty(prop_, b + "");
        }
    }
}