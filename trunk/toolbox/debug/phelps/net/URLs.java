package phelps.net;

import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;

import toolbox.util.StringUtil;

/**
 * URL-related utility classes.
 * 
 * <ul>
 * <li>{@link #toURI(URL)}
 * </ul>
 * 
 * @version $Revision$ $Date$
 */
public class URLs
{
    static
    {
        System.out.println(StringUtil.addBars("Loaded debug phelps.net.URLs"));
    }
    
    static final boolean DEBUG = false;
    
    private URLs()
    {
    }

    /**
	 * With {@link URI#toURL(URI)}, safe interconversion between URI and URL,
	 * handling spaces in path and <code>jar:</code>.
	 */
    public static URI toURI(URL url) throws URISyntaxException
    {
        if (url == null)
            return null;
        
        String
            protocol = url.getProtocol(),
            path = url.getPath(),
            host = url.getHost();
        
        System.out.println("toURI() path before: " + path);
        
        // "relative path in absolute URL" on "jar:file:/...". And
		// URI.getPath() on "jar:file:" returns null because path is
		// "file:/..." which is opaque
        
        // So normalize to "jar":
		// "jar:file:/D:/prj/Multivalent/www/Multivalent.jar!/sys/Splash.html"
		// => "jar:/D:/prj/Multivalent/www/Multivalent.jar!/sys.Splash.html"
        
        if ("jar".equals(protocol) && path.startsWith("file:"))
            path = path.substring("file:".length());
        
        if ("".equals(host))
            host = null;
        
        // You say:
        // ========
        // as of Java 1.4, URI.create() and new URI(String) go boom if path has
		// space
        //
        // My observation
        // ==============
        // Encoding of spaces works fine with (jdk1.4.2). I have to reverse
        // the encoding back to spaces before sending on to new URI() 
        
        path = StringUtil.replace(path, "%20", " ");
        
        // Make sure it starts at the root (URI.checkPath() verifies this and
        // throws an exception accordingly).
        if (!path.startsWith("/"))
            path = "/" + path;
            
        System.out.println("toURI() path after: " + path);
        
        return new URI(
            protocol,
            url.getUserInfo(),
            host,
            url.getPort(),
            path,
            url.getQuery(),
            url.getRef());
    }
}
