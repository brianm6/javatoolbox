package toolbox.util;

import java.io.InputStream;
import java.net.URL;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Category;

/**
 * This class provides a central authority for retrieving system resources
 * in the form of streams, using a variety of location mechanisms.
 */
public final class ResourceUtil
{
	// Singleton instance of the Stream Resource Manager logger.
	public static final Category logger = 
        Category.getInstance(ResourceUtil.class);

	/**
	 * Locates a resource with the given name using an exhaustive
	 * variety of methods. 
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
				throw new IOException("Resource " + name + " not found as a file resource");
		}
		catch(IOException e)
		{
			try
			{
				is = getClassResource(name);
				if (is == null)
					throw new IOException("Resource " + name + " not found as a class resource");
			}
			catch(IOException ee)
			{
				try
				{
					is = getPackageResource(ResourceUtil.class, name);
					if (is == null)
						throw new IOException("Resource " + name + " not found as a package resource");
				}
				catch(IOException eee)
				{
					logger.debug("Resource " + name + " not found");
					is = null;
				}
			}
		}
		
		return is;
	}

	/**
	 * Locates the resource identified by the url.
	 *
	 * @param	url		the url locating the resource.
	 * @return	an input stream representing the resource.
	 * @exception IOException if an error occurs.
	 */
	public static InputStream getURLResource(String url) throws IOException
	{
		logger.debug("getURLResource(" + url + ")");
		URL u = getResourceURL(url);
		return (u != null) ? u.openStream() : null;
	}

	/**
	 * Locates the resource identified by the file name.
	 *
	 * @param	filename	the name of the file resource.
	 * @return	an input stream representing the file resource.
	 * @exception IOException if an error occurs.
	 */
	public static InputStream getFileResource(String filename) throws IOException
	{
		logger.debug("getFileResource(" + filename + ")");
		URL url = getFileResourceURL(filename);
		return (url != null) ? url.openStream() : null;
	}

	/**
	 * Locates the resource associated with this class.
	 *
	 * @param	resource	the relative name of the resource.
	 * @return	an input stream representing the resource.
	 * @exception IOException if an error occurs.
	 */
	public static InputStream getClassResource(String resource) throws IOException
	{
		logger.debug("getClassResource(" + resource + ")");
		URL url = getClassResourceURL(resource);
		return (url != null) ? url.openStream() : null;
	}

	/**
	 * Locates the resource associated with the supplied class.
	 *
	 * @param	context		the class context to use.
	 * @param	resource	the relative name of the resource.
	 * @return	an input stream representing the resource.
	 * @exception IOException if an error occurs.
	 */
	public static InputStream getClassResource(Class context, String resource)
		throws IOException
	{
		logger.debug("getClassResource(" + context.getName() + ", " + resource + ")");
		URL url = getClassResourceURL(context, resource);
		return (url != null) ? url.openStream() : null;
	}

	/**
	 * Locates the resource associated with the package of the supplied
	 * class.
	 *
	 * @param	context		the class context to use.
	 * @param	resource	the relative name of the resource.
	 * @return	an input stream representing the resource.
	 * @exception IOException if an error occurs.
	 */
	public static InputStream getPackageResource(Class context, String resource)
		throws IOException
	{
		logger.debug("getPackageResource(" + context.getName() + ", " + resource + ")");
		URL url = getPackageResourceURL(context, resource);
		return (url != null) ? url.openStream() : null;
	}

	/**
	 * Locates the resource identified by the url.
	 *
	 * @param	url		the url locating the resource.
	 * @return	a URL encapsulating the resource.
	 * @exception IOException if an error occurs.
	 */
	public static URL getResourceURL(String url)
		throws IOException
	{
		logger.debug("getURLResource(" + url + ")");

		URL u = new URL(url);

		if (u == null)
		{
			throw new IOException(
				"URL '" + url + "' does not denote a valid resource");
		}

		return u;
	}

	/**
	 * Locates the resource identified by the file name.
	 *
	 * @param	filename	the name of the file resource.
	 * @return	a URL encapsulating the resource.
	 * @exception IOException if an error occurs.
	 */
	public static URL getFileResourceURL(String filename)
		throws IOException
	{
		logger.debug("getFileResource(" + filename + ")");

		File file = new File(filename);

		if (!(file.exists() && file.isFile()))
		{
			throw new IOException(
				"file '" + filename + "' does not denote a valid resource");
		}

		return file.toURL();
	}

	/**
	 * Locates the resource associated with this class.
	 *
	 * @param	resource	the relative name of the resource.
	 * @return	a URL encapsulating the resource.
	 * @exception IOException if an error occurs.
	 */
	public static URL getClassResourceURL(String resource)
		throws IOException
	{
		logger.debug("getClassResource(" + resource + ")");
		return getClassResourceURL(ResourceUtil.class, resource);
	}

	/**
	 * Locates the resource associated with the supplied class.
	 *
	 * @param	context		the class context to use.
	 * @param	resource	the relative name of the resource.
	 * @return	a URL encapsulating the resource.
	 * @exception IOException if an error occurs.
	 */
	public static URL getClassResourceURL(Class context, String resource)
		throws IOException
	
	{
		logger.debug("getClassResource(" + context.getName() + ", " + resource + ")");

		if (context == null)
			context = ResourceUtil.class;
		URL url = context.getResource(resource);

		if (url == null)
			throw new IOException("Resource '" + resource + "' not found");

		return url;
	}

	/**
	 * Locates the resource associated with the package of the supplied
	 * class. 
	 *
	 * @param	context		the class context to use.
	 * @param	resource	the relative name of the resource.
	 * @return	a URL encapsulating the resource.
	 * @exception IOException if an error occurs.
	 */
	public static URL getPackageResourceURL(Class context, String resource)
		throws IOException
	{
		logger.debug("getPackageResource(" + context.getName() + ", " + resource + ")");

		if (context == null || resource.startsWith("/"))
			return getClassResourceURL(context, resource);

		URL url = null;
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

		return url;
	}
	
	/**
	 * Exports a resource to bytes and encapsulates in a class
	 */
	public static void exportToClass(String resource, String packageName, String className) throws IOException
	{
		InputStream is = getResource(resource);
		
		if(is == null)
			throw new IOException("Could not locate resource " + resource);
			
		byte[] data = StreamUtil.toBytes(is);
		StringBuffer javaData = new StringBuffer();
		
		for(int i=0; i<data.length-1; i++)
			javaData.append(data[i] + ", ");
		javaData.append(data[data.length-1] + " ");
		
		String template = 
			"package " + packageName + ";	     \n" +
			"                            	     \n" +
			"public class " + className +       "\n" +
		    "{                                   \n" +
			"    private static byte[] data = new byte[] {" + javaData.toString() + "};\n"  +
	        "                                    \n" +	
		    "    private " + className + "()     \n" +
		    "    {                               \n" +
		    "    }                               \n" +
		    "                                    \n" +
		    "    public static byte[] getBytes() \n" +
		    "    {                               \n" +
		    "	     return data;                \n" +
 		    "    }                               \n" +
		    "}                                   \n" ;

		System.out.println(template);
		
		FileUtil.writeToFile(className+".java",template, false);
	}
}