package toolbox.plugin.statcvs;

import java.io.StringReader;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;

import toolbox.util.XOMUtil;

/**
 * Data object that encapsulates information related to a CVS project.
 */
public class CVSProject 
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    // XML nodes and attributes for saving of preferences.
    
    /**
     * Root node of the CVSProject preferences.
     */
    public  static final String NODE_CVSPROJECT    = "CVSProject";
    private static final String   ATTR_PROJECT     = "project";
    private static final String   ATTR_MODULE      = "module";
    private static final String   ATTR_CVSROOT     = "cvsroot";
    private static final String   ATTR_PASSWORD    = "password";
    private static final String   ATTR_CHECKOUTDIR = "checkoutdir";
    private static final String   ATTR_DEBUG       = "debug";
    private static final String   ATTR_LAUNCHURL   = "launchurl";
    
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
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a CVSProject from its XML representation.
     *
     * @param xml String containing a valid XML persistence of CVSProject.
     * @throws Exception on error.
     */
    public CVSProject(String xml) throws Exception
    {
        Element project = 
            new Builder().build(new StringReader(xml)).getRootElement();
        
        setProject(
            XOMUtil.getStringAttribute(project, ATTR_PROJECT, "???"));
            
        setCVSModule(XOMUtil.getStringAttribute(project, ATTR_MODULE, ""));
        setCVSRoot(XOMUtil.getStringAttribute(project, ATTR_CVSROOT, ""));
        
        setCVSPassword(
            XOMUtil.getStringAttribute(project, ATTR_PASSWORD, ""));
            
        setCheckoutDir(
            XOMUtil.getStringAttribute(project, ATTR_CHECKOUTDIR, ""));
            
        setDebug(XOMUtil.getBooleanAttribute(project, ATTR_DEBUG, false));
        
        setLaunchURL(
            XOMUtil.getStringAttribute(project, ATTR_LAUNCHURL, ""));
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
     */
    public CVSProject(String project, String module, String cvsRoot, 
        String password, String checkOutDir, boolean debug, 
        String launchURL)
    {
        setProject(project);
        setCVSModule(module);
        setCVSRoot(cvsRoot);
        setCVSPassword(password);
        setCheckoutDir(checkOutDir);
        setDebug(debug);
        setLaunchURL(launchURL);
    }

    //----------------------------------------------------------------------
    // Public
    //----------------------------------------------------------------------
    
    /**
     * Returns an XML representation of the data contained in this project.
     * 
     * @return XML string.
     */
    public String toXML()
    {
        return toDOM().toString();
    }

    
    /**
     * Returns a DOM representation of the data contained in this project.
     * 
     * @return DOM tree.
     */    
    public Element toDOM()
    {
        Element project = new Element(NODE_CVSPROJECT);
        project.addAttribute(new Attribute(ATTR_PROJECT, getProject()));
        project.addAttribute(new Attribute(ATTR_MODULE, getCVSModule()));
        project.addAttribute(new Attribute(ATTR_CVSROOT, getCVSRoot()));
        
        project.addAttribute(
            new Attribute(ATTR_PASSWORD, getCVSPassword()));
            
        project.addAttribute(
            new Attribute(ATTR_CHECKOUTDIR, getCheckoutDir()));
            
        project.addAttribute(
            new Attribute(ATTR_DEBUG, isDebug() ? "true" : "false"));
            
        project.addAttribute(new Attribute(ATTR_LAUNCHURL, getLaunchURL()));
        return project;
    }

    
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
     * Returns the project name so it is displayed by the renderer for
     * the comboxbox.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getProject();
    }
}