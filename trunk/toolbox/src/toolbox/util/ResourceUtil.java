package toolbox.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.io.CopyUtils;
import org.apache.log4j.Logger;

/**
 * This class provides a central authority for retrieving system resources in 
 * the form of streams, using a variety of location mechanisms.
 */
public final class ResourceUtil
{
    private static final Logger logger_ = Logger.getLogger(ResourceUtil.class);

    // Clover private constructor workaround
    static { new ResourceUtil(); }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Private constructor.
     */
    private ResourceUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Preferred Resource Loading Strategy 
    //--------------------------------------------------------------------------

    /**
	 * Locates a resource with the given name using a variety of strategies.
	 * Attempts to locate and load resource in the following order:
	 * <p>
	 * File Resource -> Class Resource -> PackageResource -> URL Resource
	 * 
	 * @param name Name of the resource
	 * @return InputStream or null if resource not found.
	 */
    public static InputStream getResource(String name) throws IOException
    {
        InputStream is = null;
        
        try
        {
            is = getFileResource(name);
            
            if (is == null)
            {
                logger_.debug("Throwing on null: file resource");
                
                throw new IOException(
                    "Resource " + name + " not found as a file resource");
            }
        }
        catch(IOException e)
        {
            try
            {
                is = getClassResource(name);
                
                if (is == null)
                {  
                    logger_.debug("Throwing on null: class resource");
                    
                    IOException ex = new IOException(
                        "Resource " + name + " not found as a class resource");
                    
                    ex.initCause(e);
                    throw ex;
                }
            }
            catch(IOException ee)
            {
                try
                {
                    ee.initCause(e);
                    
                    is = getPackageResource(ResourceUtil.class, name);
                    
                    if (is == null)
                    {
                        logger_.debug("Throwing on null: package resource");
                        
                        IOException ex = new IOException(
                            "Resource " + name + 
                            " not found as a package resource");
                        
                        ex.initCause(ee);
                        throw ex;
                    }
                }
                catch(IOException eee)
                {
                    try
                    {
                        eee.initCause(ee);
                        
                        is = getURLResource(name);
                        
                        if (is == null)
                        {   
                            logger_.debug("Throwing on null: url resource");
                            
                            IOException ex = new IOException(
                                "Resource " + name + 
                                " not found as a URL resource");
                            
                            ex.initCause(eee);
                            throw ex;
                        }
                    }
                    catch(IOException eeee)
                    {
                        logger_.debug("Resource " + name + " not found");
                        eeee.initCause(eee);
                        throw eeee;
                        //is = null;
                    }
                }
            }
        }
        
        return is;
    }

    //--------------------------------------------------------------------------
    // Convenience methods that return a resource in common forms.
    //--------------------------------------------------------------------------
    
    /**
	 * Locates a resource with the given name using an exhaustive variety of
	 * methods. This is just a convenience method to return the resource in the
	 * form of a byte array instead of a stream.
	 * 
	 * @param name Name of the resource
	 * @return Byte array representing the resource of null if the resource
	 *         could not be found.
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
	 * Returns resource with the given name as a string.
	 * 
	 * @param name Resource name (file, url, etc)
	 * @return Resource contents as a string.
	 */
    public static String getResourceAsString(String name)
    {
        return new String(getResourceAsBytes(name));
    }

    
    /**
	 * This is just a convenience method to return a resource in the form of of
	 * an Icon. The name should refer to a valid GIF or JPG image.
	 * 
	 * @param name Name of the resource
	 * @return Icon representing the named resource
	 */
    public static Icon getResourceAsIcon(String name)
    {
        return new ImageIcon(getResourceAsBytes(name));
    }

    
    /**
	 * Convenience method to load a resource as an image.
	 * 
	 * @param name Path to the GIF/JPG file
	 * @return Image of given resource
	 */
    public static Image getResourceAsImage(String name)
    {
        byte[] data = ResourceUtil.getResourceAsBytes(name);
        Image image = Toolkit.getDefaultToolkit().createImage(data);
        return image;
    }

    
    /**
     * Returns a temp file that contains the resource with the given name.
     * The caller is responsible for deleting after being used. This method is
     * especially useful for passing resources that don't exist as Files (image
     * in a jar file for example) to a method that requires a File as input. 
     * 
     * @param name Resource name (file, url, etc).
     * @return File
     * @throws IOException on I/O error.
     */
    public static File getResourceAsTempFile(String name) throws IOException
    {
        InputStream is = null;
        OutputStream os = null;
        File f = null;
        
        try
        {   
            is = getResource(name);
            f = FileUtil.createTempFile();
            os = new FileOutputStream(f);
            CopyUtils.copy(is, os);
        }
        finally
        {
            StreamUtil.close(is);
            StreamUtil.close(os);
        }
        
        return f;
    }
    
    //--------------------------------------------------------------------------
    // URL Resources
    //--------------------------------------------------------------------------

    /**
	 * Locates the resource identified by the url.
	 * 
	 * @param url URL locating the resource.
	 * @return Input stream representing the resource.
	 * @throws IOException if an I/O error occurs.
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
	 * @param url URL locating the resource.
	 * @return URL encapsulating the resource.
	 * @throws IOException if an I/O error occurs.
	 */
    public static URL getResourceURL(String url) throws IOException
    {
        logger_.debug("URL: " + url);

        URL u = new URL(url);

        if (u == null)
        {
            throw new IOException(
                "Resource '" + url + "' not found as resource URL.");
        }

        return u;
    }

    //--------------------------------------------------------------------------
    // File Resources
    //--------------------------------------------------------------------------
    
    /**
	 * Locates the resource identified by the file name.
	 * 
	 * @param filename Name of the file resource.
	 * @return Input stream representing the file resource.
	 * @throws IOException if an I/O error occurs.
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
	 * @param filename Name of the file resource.
	 * @return URL encapsulating the resource.
	 * @throws IOException if an I/O error occurs.
	 */
    public static URL getFileResourceURL(String filename) throws IOException
    {
        URL  url = null;
        File file = new File(filename);

        if (!(file.exists() && file.isFile()))
        {
            throw new IOException(
                "Resource '" + filename + "' not found as a URL resource");
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
	 * @param resource Relative name of the resource.
	 * @return Input stream representing the resource.
	 * @throws IOException if an I/O error occurs.
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
	 * @param context Class context to use.
	 * @param resource Relative name of the resource.
	 * @return Input stream representing the resource.
	 * @throws IOException if an I/O error occurs.
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
	 * @param resource Relative name of the resource.
	 * @return URL encapsulating the resource.
	 * @throws IOException if an I/O error occurs.
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
	 * @param context Class context to use.
	 * @param resource Relative name of the resource.
	 * @return URL encapsulating the resource.
	 * @throws IOException if an I/O error occurs.
	 */
    public static URL getClassResourceURL(Class context, String resource)
        throws IOException
    
    {
        if (context == null)
            context = ResourceUtil.class;
            
        URL url = context.getResource(resource);

        if (url == null)
            throw new IOException(
                "Resource '" + resource + "' not found as a class resource.");
        else
            logger_.debug("Loaded " + resource + " by URL " + url);

        return url;
    }

    //--------------------------------------------------------------------------
    // Package Resources
    //--------------------------------------------------------------------------

    /**
	 * Locates the resource associated with the package of the supplied class.
	 * 
	 * @param context Class context to use.
	 * @param resource Relative name of the resource.
	 * @return Input stream representing the resource.
	 * @throws IOException if an I/O error occurs.
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
	 * @param context Class context to use.
	 * @param resource Relative name of the resource.
	 * @return URL encapsulating the resource.
	 * @throws IOException if an I/O error occurs.
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
                throw new IOException("Resource '" + resource + 
                    "' not found as a package resource.");
            else
                logger_.debug("Loaded " + resource + " by URL " + url);
        }

        return url;
    }
    
    //--------------------------------------------------------------------------
    // Misc
    //--------------------------------------------------------------------------
    
    /**
	 * Exports a resource to bytes and encapsulates in a class.
	 * 
	 * @param resource Name of resource
	 * @param packageName Name of package to create object in
	 * @param className Name of class to assign to created object
	 * @param destDir Directory that generated file should be placed in
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