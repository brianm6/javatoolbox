package toolbox.workspace.prefs;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;

public class PreferencesModel implements IPreferenced
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    private static final String NODE_WORKSPACE_PREFERENCES = "Preferences";
    private static final String NODE_HTTP_PROXY = "HttpProxy";
    private static final String ATTR_HTTP_PROXY_ENABLED = "enabled";
    private static final String ATTR_HTTP_PROXY_HOST = "hostname";
    private static final String ATTR_HTTP_PROXY_PORT = "port";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    private boolean httpProxyEnabled_;
    private String httpProxyHost_;
    private String httpProxyPort_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    public PreferencesModel()
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
        Element root =
            XOMUtil.getFirstChildElement(
                prefs,
                NODE_WORKSPACE_PREFERENCES,
                new Element(NODE_WORKSPACE_PREFERENCES));

        Element httpProxy =
            XOMUtil.getFirstChildElement(
                root, NODE_HTTP_PROXY, new Element(NODE_HTTP_PROXY));

        setHttpProxyEnabled(
            XOMUtil.getBooleanAttribute(
                httpProxy,
                ATTR_HTTP_PROXY_ENABLED,
                false));

        setHttpProxyHost(
            XOMUtil.getStringAttribute(
                httpProxy,
                ATTR_HTTP_PROXY_HOST,
                ""));

        setHttpProxyPort(
            XOMUtil.getStringAttribute(
                httpProxy,
                ATTR_HTTP_PROXY_PORT,
                ""));
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_WORKSPACE_PREFERENCES);

        Element httpProxy = new Element(NODE_HTTP_PROXY);

        httpProxy.addAttribute(
            new Attribute(
                ATTR_HTTP_PROXY_ENABLED,
                isHttpProxyEnabled() + ""));

        if (!StringUtil.isNullOrBlank(getHttpProxyHost()))
            httpProxy.addAttribute(
                new Attribute(
                    ATTR_HTTP_PROXY_HOST,
                    getHttpProxyHost().trim()));

        if (!StringUtil.isNullOrBlank(getHttpProxyPort()))
            httpProxy.addAttribute(
                new Attribute(
                    ATTR_HTTP_PROXY_PORT,
                    getHttpProxyPort().trim()));

        root.appendChild(httpProxy);

        XOMUtil.insertOrReplace(prefs, root);
    }

    //--------------------------------------------------------------------------
    // Accessors/Mutators
    //--------------------------------------------------------------------------

    /**
     * @return Returns the httpProxyEnabled.
     */
    public boolean isHttpProxyEnabled()
    {
        return httpProxyEnabled_;
    }


    /**
     * @param httpProxyEnabled The httpProxyEnabled to set.
     */
    public void setHttpProxyEnabled(boolean httpProxyEnabled)
    {
        httpProxyEnabled_ = httpProxyEnabled;
    }


    /**
     * @return Returns the httpProxyHost.
     */
    public String getHttpProxyHost()
    {
        return httpProxyHost_;
    }


    /**
     * @param httpProxyHost The httpProxyHost to set.
     */
    public void setHttpProxyHost(String httpProxyHost)
    {
        httpProxyHost_ = httpProxyHost;
    }


    /**
     * @return Returns the httpProxyPort.
     */
    public String getHttpProxyPort()
    {
        return httpProxyPort_;
    }


    /**
     * @param httpProxyPort The httpProxyPort to set.
     */
    public void setHttpProxyPort(String httpProxyPort)
    {
        httpProxyPort_ = httpProxyPort;
    }
}