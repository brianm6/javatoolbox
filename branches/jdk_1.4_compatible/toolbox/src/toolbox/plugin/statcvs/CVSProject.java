package toolbox.plugin.statcvs;

import java.io.StringReader;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;

import org.apache.commons.codec.binary.Base64;

import toolbox.util.PreferencedUtil;
import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.PreferencedException;

/**
 * Data object that encapsulates information related to a CVS project.
 * 
 * @see toolbox.plugin.statcvs.StatcvsPlugin
 */
public class CVSProject implements Comparable, IPreferenced
{
    //--------------------------------------------------------------------------
    // JavaBean Property Names
    //--------------------------------------------------------------------------
    
    public static final String PROP_PROJECT      = "project";
    public static final String PROP_MODULE       = "CVSModule";
    public static final String PROP_CVSROOT      = "CVSRoot";
    public static final String PROP_PASSWORD     = "password";
    public static final String PROP_CHECKOUT_DIR = "checkoutDir";
    public static final String PROP_DEBUG        = "debug";
    public static final String PROP_LAUNCH_URL   = "launchURL";
    public static final String PROP_ENGINE       = "engine";
    
    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------

    /**
     * Root node of the CVSProject preferences.
     */
    public static final String NODE_CVSPROJECT = "CVSProject";
    
    /**
     * Names of all the javabean properties that will be persisted by the 
     * IPreferenced interface. Password is left out since in has to be 
     * encrypted.
     */
    public static final String[] SAVED_PROPS = {
        PROP_PROJECT,
        PROP_MODULE,     
        PROP_CVSROOT,
        PROP_CHECKOUT_DIR,
        PROP_DEBUG,
        PROP_LAUNCH_URL,
        PROP_ENGINE     
    };
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Field for the project name (required for saving).
     */
    private String project_;

    /**
     * Field for the cvs module name (required).
     */
    private String cvsModule_;

    /**
     * Field for the cvs root (required).
     */
    private String cvsRoot_;

    /**
     * Field for the cvs password (required but empty strings are OK).
     */
    private String cvsPassword_;

    /**
     * Field for the checkout directory (must already exist).
     */
    private String checkoutDir_;

    /**
     * Checkbox to toggle the cvslib.jar debug flag.
     */
    private boolean debug_;

    /**
     * Field that contains the URL to view the generated statcvs report.
     */
    private String launchURL_;

    /**
     * Field that contains the classname of the statcvs engine to use for 
     * report generation.
     */
    private String engine_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a CVSProject.
     */
    public CVSProject()
    {
    }

    
    /**
     * Creates a CVSProject from its XML representation.
     *
     * @param xml String containing a valid XML persistence of CVSProject.
     * @throws Exception on error.
     */
    public CVSProject(String xml) throws Exception
    {
        StringReader rdr = new StringReader(xml);
        Element e = new Builder().build(rdr).getRootElement();

        setProject(XOMUtil.getStringAttribute(e, PROP_PROJECT, "???"));
        setCVSModule(XOMUtil.getStringAttribute(e, PROP_MODULE, ""));
        setCVSRoot(XOMUtil.getStringAttribute(e, PROP_CVSROOT, ""));
        setCVSPassword(XOMUtil.getStringAttribute(e, PROP_PASSWORD, ""));
        setCheckoutDir(XOMUtil.getStringAttribute(e, PROP_CHECKOUT_DIR, ""));
        setDebug(XOMUtil.getBooleanAttribute(e, PROP_DEBUG, false));
        setLaunchURL(XOMUtil.getStringAttribute(e, PROP_LAUNCH_URL, ""));
        setEngine(XOMUtil.getStringAttribute(e, PROP_ENGINE, 
            StatcvsPlugin.CLASS_STATCVS_XML_ENGINE));
    }


    /**
     * Creates a CVSProject.
     *
     * @param project Project name.
     * @param module CVS module name.
     * @param cvsRoot CVS root url.
     * @param password Cleartext password.
     * @param checkOutDir Directory to checkout files to.
     * @param debug Print debug output.
     * @param launchURL Launch URL for viewing generated statistics.
     * @param engine Statcvs engine class name.
     */
    public CVSProject(
        String project,
        String module,
        String cvsRoot,
        String password,
        String checkOutDir,
        boolean debug,
        String launchURL,
        String engine)
    {
        setProject(project);
        setCVSModule(module);
        setCVSRoot(cvsRoot);
        setCVSPassword(password);
        setCheckoutDir(checkOutDir);
        setDebug(debug);
        setLaunchURL(launchURL);
        setEngine(engine);
    }

    //--------------------------------------------------------------------------
    // Comparable Interface
    //--------------------------------------------------------------------------
    
    /**
     * CVSProject implements Comparable so that the SortedComboBoxModel can
     * maintain its elements in alphabetical order.
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj)
    {
        return getProject().compareToIgnoreCase(obj.toString());
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_CVSPROJECT, new Element(NODE_CVSPROJECT));
        
        PreferencedUtil.readPreferences(this, root, SAVED_PROPS);
        String scrambled = XOMUtil.getStringAttribute(root, PROP_PASSWORD, "");
        setCVSPassword(new String(Base64.decodeBase64(scrambled.getBytes())));
    }


    /*
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_CVSPROJECT);
        PreferencedUtil.writePreferences(this, root, SAVED_PROPS);

        // Password has to be done explicitly because of encoding
        root.addAttribute(new Attribute(PROP_PASSWORD, 
            new String(Base64.encodeBase64(cvsPassword_.getBytes()))));
        
        XOMUtil.insertOrReplace(prefs, root);
    }
    
    //--------------------------------------------------------------------------
    // Accessors/Mutators
    //--------------------------------------------------------------------------

    /**
     * Returns directory that files will be checked out to.
     *
     * @return Checkout directory.
     */
    public String getCheckoutDir()
    {
        return checkoutDir_;
    }


    /**
     * Returns the CVS module name that will be analyzed.
     *
     * @return CVS module name.
     */
    public String getCVSModule()
    {
        return cvsModule_;
    }


    /**
     * Returns the CVS password used for authentication.
     *
     * @return CVS password.
     */
    public String getCVSPassword()
    {
        return cvsPassword_;
    }


    /**
     * Returns the CVSROOT for the cvs module.
     *
     * @return CVSROOT.
     */
    public String getCVSRoot()
    {
        return cvsRoot_;
    }


    /**
     * Returns the debug flag for the CVS library.
     *
     * @return Debug flag.
     */
    public boolean isDebug()
    {
        return debug_;
    }


    /**
     * Returns the URL that points to the generated statistics in HTML.
     *
     * @return URL to generated statistics.
     */
    public String getLaunchURL()
    {
        return launchURL_;
    }


    /**
     * Returns the project name used to identify the set of configuration
     * values.
     *
     * @return Project name.
     */
    public String getProject()
    {
        return project_;
    }


    /**
     * Sets the checkout directory.
     *
     * @param string Path to existing directory that cvs files will be
     *        checked out to.
     */
    public void setCheckoutDir(String string)
    {
        checkoutDir_ = string;
    }


    /**
     * Sets the name of the CVS module.
     *
     * @param string CVS module name.
     */
    public void setCVSModule(String string)
    {
        cvsModule_ = string;
    }


    /**
     * Sets the CVS password in clear text.
     *
     * @param string CVS authentication password.
     */
    public void setCVSPassword(String string)
    {
        cvsPassword_ = string;
    }


    /**
     * Sets the CVSROOT.
     *
     * @param string CVSROOT.
     */
    public void setCVSRoot(String string)
    {
        cvsRoot_ = string;
    }


    /**
     * Sets the debug flag.
     *
     * @param b Debug flag.
     */
    public void setDebug(boolean b)
    {
        debug_ = b;
    }


    /**
     * Sets the launch URL for the generated statistics.
     *
     * @param string URL to the generated statistics.
     */
    public void setLaunchURL(String string)
    {
        launchURL_ = string;
    }


    /**
     * Sets the project name.
     *
     * @param string Project name.
     */
    public void setProject(String string)
    {
        project_ = string;
    }


    /**
     * Returns the statcvs engines class name.
     * 
     * @return String
     */
    public String getEngine()
    {
        return engine_;
    }
    
    
    /**
     * Sets the statcvs engines class name.
     * 
     * @param engine The engine to set.
     */
    public void setEngine(String engine)
    {
        engine_ = engine;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Returns the project name so it is displayed by the renderer for the
     * combobox.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getProject();
    }
}