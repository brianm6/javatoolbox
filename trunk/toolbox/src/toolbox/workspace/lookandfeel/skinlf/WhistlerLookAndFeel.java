package toolbox.workspace.lookandfeel.skinlf;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.plaf.IconUIResource;

/**
 * Look and Feel that is just a wrapper for the SkinLF look and feel coupled
 * with the Whistler theme.
 */
public class WhistlerLookAndFeel extends AbstractSkinLookAndFeel
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a WhistlerLookAndFeel 
     */
    public WhistlerLookAndFeel()
    {
    }

    //--------------------------------------------------------------------------
    // AbstractSkinLookAndFeel Impl
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.lookandfeel.skinlf.AbstractSkinLookAndFeel
     *      #getThemeFile()
     */
    public String getThemeFile()
    {
        return "skinlf/whistler.zip";
    }

    /**
     * @see toolbox.workspace.lookandfeel.skinlf.AbstractSkinLookAndFeel
     *      #getThemeName()
     */
    public String getThemeName()
    {
        return "Whistler";
    }

	

    /**
	 * Utility method that creates a UIDefaults.LazyValue that creates an
	 * ImageIcon UIResource for the specified <code>gifFile</code> filename.
	 */
    public static Object makeIcon(final Class baseClass, final String gifFile)
    {
        System.err.println("Baseclass=" + baseClass);
        return AbstractSkinLookAndFeel.makeIcon(baseClass, gifFile);
        
        
//        return new UIDefaults.LazyValue()
//        {
//            public Object createValue(UIDefaults table)
//            {
//                /*
//				 * Copy resource into a byte array. This is necessary because
//				 * several browsers consider Class.getResource a security risk
//				 * because it can be used to load additional classes.
//				 * Class.getResourceAsStream just returns raw bytes, which we
//				 * can convert to an image.
//				 */
//                final byte[][] buffer = new byte[1][];
//                SwingUtilities.doPrivileged(new Runnable()
//                {
//                    public void run()
//                    {
//                        try
//                        {
//                            InputStream resource = baseClass
//                                    .getResourceAsStream(gifFile);
//                            if (resource == null)
//                            {
//                                return;
//                            }
//                            BufferedInputStream in = new BufferedInputStream(
//                                    resource);
//                            ByteArrayOutputStream out = new ByteArrayOutputStream(
//                                    1024);
//                            buffer[0] = new byte[1024];
//                            int n;
//                            while ((n = in.read(buffer[0])) > 0)
//                            {
//                                out.write(buffer[0], 0, n);
//                            }
//                            in.close();
//                            out.flush();
//                            buffer[0] = out.toByteArray();
//                        }
//                        catch (IOException ioe)
//                        {
//                            System.err.println(ioe.toString());
//                            return;
//                        }
//                    }
//                });
//
//                if (buffer[0] == null)
//                {
//                    System.err.println(baseClass.getName() + "/" + gifFile
//                            + " not found.");
//                    return null;
//                }
//                if (buffer[0].length == 0)
//                {
//                    System.err.println("warning: " + gifFile
//                            + " is zero-length");
//                    return null;
//                }
//
//                return new IconUIResource(new ImageIcon(buffer[0]));
//            }
//        };
    }
}