package phelps.net;

import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;



/**
    URL-related utility classes.

    <ul>
    <li>{@link #toURI(URL)}
    </ul>

    @version $Revision$ $Date$
*/
public class URLs {
  static final boolean DEBUG = false;
  private URLs() {}

  /**
    With {@link URI#toURL(URI)}, safe interconversion between URI and URL, 
    handling spaces in path and <code>jar:</code>.
  */
  public static URI toURI(URL url) throws URISyntaxException {
    if (url==null) return null;
    String protocol = url.getProtocol(), path = url.getPath(), host = url.getHost();
    // "relative path in absolute URL" on "jar:file:/...".  And URI.getPath() on "jar:file:" returns null because path is "file:/..." which is opaque
    // So normalize to "jar": "jar:file:/D:/prj/Multivalent/www/Multivalent.jar!/sys/Splash.html" => "jar:/D:/prj/Multivalent/www/Multivalent.jar!/sys.Splash.html"
    if ("jar".equals(protocol) && path.startsWith("file:")) path = path.substring("file:".length());
    if ("".equals(host)) host = null;
    // as of Java 1.4, URI.create() and new URI(String) go boom if path has space
    return new URI(protocol, url.getUserInfo(), host, url.getPort(), path, url.getQuery(), url.getRef());
  }
}
