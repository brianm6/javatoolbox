package toolbox.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

/**
 * This class provides a central authority for retrieving system resources in 
 * the form of streams, using a variety of location mechanisms.
 */
public final class ResourceUtil
{
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(ResourceUtil.class);

    // Clover private constructor workaround
    static { new ResourceUtil(); }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Prevent construction
     */
    private ResourceUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Get Resouces in Different Forms
    //--------------------------------------------------------------------------

    /**
     * Locates a resource with the given name using an exhaustive variety of 
     * methods. 
     * 
     * @param   name   Name of the resource
     * @return  InputStream or null if resource not found.
     */
    public static InputStream getResource(String name)
    {
        InputStream is = null;
        
        try
        {
            is = getFileResource(name);
            if (is == null)
                throw new IOException(
                    "Resource " + name + " not found as a file resource");
        }
        catch(IOException e)
        {
            try
            {
                is = getClassResource(name);
                if (is == null)
                    throw new IOException(
                        "Resource " + name + " not found as a class resource");
            }
            catch(IOException ee)
            {
                try
                {
                    is = getPackageResource(ResourceUtil.class, name);
                    if (is == null)
                        throw new IOException(
                            "Resource " + name + 
                                " not found as a package resource");
                }
                catch(IOException eee)
                {
                    logger_.debug("Resource " + name + " not found");
                    is = null;
                }
            }
        }
        
        return is;
    }

    /**
     * Locates a resource with the given name using an exhaustive variety of 
     * methods. This is just a convenience method to return the resource in the
     * form of a byte array instead of a stream. 
     * 
     * @param   name   Name of the resource
     * @return  Byte array representing the resource of null if the resource
     *          could not be found.
     */
    public static byte[] getResourceAsBytes(String name)
    {
        byte[] resource = null;
        
        try
        {
            InputStream stream = getResource(name);
            resource = (stream != null ? StreamUtil.toBytes(stream) : null);
        }
        catch (IOException e)
        {
            logger_.error(name, e);
        }
        
        return resource;
    }

    /**
     * This is just a convenience method to return a resource in the form of 
     * of an Icon. The name should refer to a valid GIF or JPG image. 
     * 
     * @param   name   Name of the resource
     * @return  Icon representing the named resource
     */
    public static Icon getResourceAsIcon(String name)
    {
        return new ImageIcon(getResourceAsBytes(name));
    }

    //--------------------------------------------------------------------------
    // URL Resources
    //--------------------------------------------------------------------------

    /**
     * Locates the resource identified by the url.
     *
     * @param     url  URL locating the resource.
     * @return    Input stream representing the resource.
     * @throws    IOException if an I/O error occurs.
     */
    public static InputStream getURLResource(String url) throws IOException
    {
        logger_.debug("URL: " + url);
        URL u = getResourceURL(url);
        return (u != null) ? u.openStream() : null;
    }

    /**
     * Locates the resource identified by the url.
     *
     * @param   url   URL locating the resource.
     * @return  URL encapsulating the resource.
     * @throws  IOException if an I/O error occurs.
     */
    public static URL getResourceURL(String url) throws IOException
    {
        logger_.debug("URL: " + url);

        URL u = new URL(url);

        if (u == null)
        {
            throw new IOException(
                "URL '" + url + "' does not denote a valid resource");
        }

        return u;
    }

    //--------------------------------------------------------------------------
    // File Resources
    //--------------------------------------------------------------------------
    
    /**
     * Locates the resource identified by the file name.
     *
     * @param   filename    Name of the file resource.
     * @return  Input stream representing the file resource.
     * @throws  IOException if an I/O error occurs.
     */
    public static InputStream getFileResource(String filename) 
        throws IOException
    {
        URL url = getFileResourceURL(filename);
        return (url != null) ? url.openStream() : null;
    }

    /**
     * Locates the resource identified by the file name.
     *
     * @param   filename    Name of the file resource.
     * @return  URL encapsulating the resource.
     * @throws  IOException if an I/O error occurs.
     */
    public static URL getFileResourceURL(String filename) throws IOException
    {
        URL  url = null;
        File file = new File(filename);

        if (!(file.exists() && file.isFile()))
        {
            throw new IOException(
                "File '" + filename + "' does not denote a valid resource");
        }
        else
        {
            url = file.toURL();
            logger_.debug("Loaded " + filename + " by URL " + url);
        }                

        return url; 
    }

    //--------------------------------------------------------------------------
    // Class Resources
    //--------------------------------------------------------------------------

    /**
     * Locates the resource associated with this class.
     *
     * @param   resource    Relative name of the resource.
     * @return  Input stream representing the resource.
     * @throws  IOException if an I/O error occurs.
     */
    public static InputStream getClassResource(String resource) 
        throws IOException
    {
        //logger_.debug("Class   : " + ResourceUtil.class.getName());
        //logger_.debug("Resource: " + resource);
        
        URL url = getClassResourceURL(resource);
        return (url != null) ? url.openStream() : null;
    }

    /**
     * Locates the resource associated with the supplied class.
     *
     * @param   context     Class context to use.
     * @param   resource    Relative name of the resource.
     * @return  Input stream representing the resource.
     * @throws  IOException if an I/O error occurs.
     */
    public static InputStream getClassResource(Class context, String resource)
        throws IOException
    {
        //logger_.debug("Context : " + context.getName());
        //logger_.debug("Resource: " + resource);
            
        URL url = getClassResourceURL(context, resource);
        return (url != null) ? url.openStream() : null;
    }

    /**
     * Locates the resource associated with this class.
     *
     * @param   resource    Relative name of the resource.
     * @return  URL encapsulating the resource.
     * @throws  IOException if an I/O error occurs.
     */
    public static URL getClassResourceURL(String resource) throws IOException
    {
        //logger_.debug("Class   : " + ResourceUtil.class.getName());
        //logger_.debug("Resource: " + resource);
        return getClassResourceURL(ResourceUtil.class, resource);
    }

    /**
     * Locates the resource associated with the supplied class. If a class is 
     * not supplied, then the current class is used instead.
     *
     * @param   context     Class context to use.
     * @param   resource    Relative name of the resource.
     * @return  URL encapsulating the resource.
     * @throws  IOException if an I/O error occurs.
     */
    public static URL getClassResourceURL(Class context, String resource)
        throws IOException
    
    {
        if (context == null)
            context = ResourceUtil.class;
            
        URL url = context.getResource(resource);

        if (url == null)
            throw new IOException("Resource '" + resource + "' not found");
        else
            logger_.debug("Loaded " + resource + " by URL " + url);

        return url;
    }

    //--------------------------------------------------------------------------
    // Package Resources
    //--------------------------------------------------------------------------

    /**
     * Locates the resource associated with the package of the supplied
     * class.
     *
     * @param   context     Class context to use.
     * @param   resource    Relative name of the resource.
     * @return  Input stream representing the resource.
     * @throws  IOException if an I/O error occurs.
     */
    public static InputStream getPackageResource(Class context, String resource)
        throws IOException
    {
        URL url = getPackageResourceURL(context, resource);
        return (url != null) ? url.openStream() : null;
    }

    /**
     * Locates the resource associated with the package of the supplied class. 
     *
     * @param   context   Class context to use.
     * @param   resource  Relative name of the resource.
     * @return  URL encapsulating the resource.
     * @throws  IOException if an I/O error occurs.
     */
    public static URL getPackageResourceURL(Class context, String resource)
        throws IOException
    {
        URL url = null;
        
        if (context == null || resource.startsWith("/"))
        {
            url = getClassResourceURL(context, resource);
        }
        else
        {
            String pkg = '/' + context.getName().replace('.', '/');
            StringBuffer dir = new StringBuffer(pkg);
    
            int index = dir.length();
            do
            {
                index = pkg.lastIndexOf('/', --index);
                dir.delete(index + 1, dir.length()).append(resource);
                url = context.getResource(dir.toString());
            }
            while (url == null && index != -1);
    
            if (url == null)
                throw new IOException("Resource '" + resource + "' not found");
            else
                logger_.debug("Loaded " + resource + " by URL " + url);
        }

        return url;
    }
    
    //--------------------------------------------------------------------------
    // Misc
    //--------------------------------------------------------------------------
    
    /**
     * Exports a resource to bytes and encapsulates in a class
     * 
     * @param  resource     Name of resource
     * @param  packageName  Name of package to create object in
     * @param  className    Name of class to assign to created object
     * @param  destDir      Directory that generated file should be placed in
     * @return Generated java file as a string
     * @throws IOException if an I/O error occurs
     */
    public static String exportToClass(
        String resource, 
        String packageName, 
        String className, 
        File destDir) throws IOException
    {
        InputStream is = getResource(resource);
        
        if(is == null)
            throw new IOException("Could not locate resource " + resource);
            
        byte[] data = StreamUtil.toBytes(is);
        StringBuffer javaData = new StringBuffer();
        
        for(int i=0; i<data.length-1; i++)
            javaData.append(data[i] + ", ");
        javaData.append(data[data.length-1] + " ");
        
        String arraySrc = 
            StringUtil.wrap(
                javaData.toString(), 72, "        ", "");
        
        String template = 
            "package " + packageName + ";         \n" +
            "                                     \n" +
            "public class " + className +        "\n" +
            "{                                    \n" +
            "    private static byte[] data = new byte[] {\n" + 
            arraySrc + "\n    };\n" +
            "                                    \n" +    
            "    private " + className + "()     \n" +
            "    {                               \n" +
            "    }                               \n" +
            "                                    \n" +
            "    public static byte[] getBytes() \n" +
            "    {                               \n" +
            "        return data;                \n" +
            "    }                               \n" +
            "}                                   \n" ;

        FileUtil.setFileContents(
            destDir.getAbsolutePath() + File.separator + className + ".java",
                template, false);
        
        return template;
    }
}