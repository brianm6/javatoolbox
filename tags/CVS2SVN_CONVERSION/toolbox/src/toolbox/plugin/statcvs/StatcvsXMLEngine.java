package toolbox.plugin.statcvs;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;

import org.apache.log4j.Logger;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;

/**
 * Alternative report engine for statcvs defined in the Statcvs-XML project.
 */
public class StatcvsXMLEngine implements StatcvsEngine
{
    private static final Logger logger_ =
        Logger.getLogger(StatcvsXMLEngine.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Reference to the statcvs plugin.
     */
    private StatcvsPlugin plugin_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a StatcvsXMLEngine. Necessary for construction via reflection.
     */
    public StatcvsXMLEngine()
    {
    }

    //--------------------------------------------------------------------------
    // StatcvsEngine Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.plugin.statcvs.StatcvsEngine#setPlugin(toolbox.plugin.statcvs.StatcvsPlugin)
     */
    public void setPlugin(StatcvsPlugin plugin)
    {
        plugin_ = plugin;
    }


    /*
     * @see toolbox.plugin.statcvs.StatcvsEngine#getLaunchURL()
     */
    public String getLaunchURL()
    {
        return
            //"file:/" +
            plugin_.getCVSBaseDir() +
            "statcvs-xml" +
            File.separator +
            "index.html";
    }

    
    /*
     * @see toolbox.plugin.statcvs.StatcvsEngine#generateStats()
     */
    public void generateStats() throws Exception
    {
        // TODO: Move jar urls to an external resource.
        
        ClassWorld world = new ClassWorld();
        ClassRealm statcvsRealm = world.newRealm("statcvs");

        statcvsRealm.addConstituent(new URL(
            "http://www.ibiblio.org/maven/commons-logging/jars/commons-logging-1.0.3.jar"));

        //statcvsRealm.addConstituent(new File("lib/log4j.jar").toURL());

        statcvsRealm.addConstituent(new URL(
            "http://download.berlios.de/statcvs-xml/statcvs-xml-0.9.3.jar"));

        statcvsRealm.addConstituent(new URL(
            "http://www.ibiblio.org/maven/jdom/jars/jdom-b10.jar"));

        statcvsRealm.addConstituent(new URL(
            "http://www.ibiblio.org/maven/jfreechart/jars/jfreechart-0.9.16.jar"));

        statcvsRealm.addConstituent(new URL(
            "http://www.ibiblio.org/maven/jcommon/jars/jcommon-0.9.1.jar"));

        statcvsRealm.addConstituent(new URL(
            "http://www.ibiblio.org/maven/commons-jexl/jars/commons-jexl-1.0-beta-1.jar"));

        Thread.currentThread().setContextClassLoader(
            statcvsRealm.getClassLoader());

        //System.setProperty(
        //    "org.apache.commons.logging.Log",
        //    "org.apache.commons.logging.impl.SimpleLog"); 
        //"org.apache.commons.logging.impl.Log4JLogger");


        Class statcvsClass = statcvsRealm.loadClass(
            "de.berlios.statcvs.xml.Main");
        
        Object statcvsMain = statcvsClass.newInstance();

        String args[] = new String[] 
        {
            "-output-dir",
            plugin_.getCVSBaseDir() + "statcvs-xml",
            "-verbose",
            plugin_.getCVSLogFile(),
            plugin_.getCVSBaseDir()
        };

        // Not calling main() because it calls System.exit()

        //
        // Replicating: ReportSettings settings = readSettings(args);
        //
        Method readSettings =
            getAccessibleMethod(
                statcvsClass,
                "readSettings",
                new String[0].getClass());

        Object settings = readSettings.invoke(null, new Object[] {args});

        //
        // Replicating: generateSuite(settings);
        //
        Method generateSuite =
            getAccessibleMethod(
                statcvsClass,
                "generateSuite",
                settings.getClass());

        generateSuite.invoke(null, new Object[] {settings});

        /*
        "Usage: java -jar @JAR@ [options] [logfile [directory]]\n"
        + "\n"
        + "Optional parameters:\n"
        + "  <logfile>          path to the cvs logfile of the module (default: cvs.log)\n"
        + "  <directory>        path to the working directory (default: current directory)\n"
        + "\n"
        + "Some options:\n"
        + "  -version           print the version information and exit\n"
        + "  -output-dir <dir>  directory where HTML suite will be saved\n"
        + "  -include <pattern> include only files matching pattern, e.g. ***.c;***.h\n"
        + "  -exclude <pattern> exclude matching files, e.g. tests/**;docs/**\n"
        + "  -title <title>     Project title to be used in reports\n"
        + "  -render <class>    class can be either html, xdoc, xml or a Java class name\n"
        + "  -suite <file>      xml file that is used to generate the documents\n"
        + "  -weburl <url>      integrate with web repository installation at <url>\n"
        + "  -verbose           print extra progress information\n"
        + "  -debug             print debug information\n"
        + "\n"
        + "If statcvs cannot recognize the type of your web repository, please use the\n"
        + "following switches:\n"
        + "  -viewcvs <url>     integrate with viewcvs installation at <url>\n"
        + "  -cvsweb <url>      integrate with cvsweb installation at <url>\n"
        + "  -chora <url>       integrate with chora installation at <url>\n"
        + "\n");
        */
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    // TODO: Move to MethodUtils or find out why using commons-logging from
    //      classworlds blows up.

    /**
     * <p>Return an accessible method (that is, one that can be invoked via
     * reflection) with given name and a single parameter.  If no such method
     * can be found, return <code>null</code>.
     * Basically, a convenience wrapper that constructs a <code>Class</code>
     * array for you.</p>
     *
     * @param clazz get method from this class
     * @param methodName get method with this name
     * @param parameterType taking this type of parameter
     * @return Method
     */
    public static Method getAccessibleMethod(
            Class clazz,
            String methodName,
            Class parameterType) {

        Class[] parameterTypes = {parameterType};
        return getAccessibleMethod(clazz, methodName, parameterTypes);

    }


    /**
     * <p>Return an accessible method (that is, one that can be invoked via
     * reflection) with given name and parameters.  If no such method
     * can be found, return <code>null</code>.
     * This is just a convenient wrapper for
     * {@link #getAccessibleMethod(Method method)}.</p>
     *
     * @param clazz get method from this class
     * @param methodName get method with this name
     * @param parameterTypes with these parameters types
     * @return Method
     */
    public static Method getAccessibleMethod(
            Class clazz,
            String methodName,
            Class[] parameterTypes)
    {
        try
        {
            return getAccessibleMethod(clazz.getMethod(methodName,
                parameterTypes));
        }
        catch (NoSuchMethodException e)
        {
            return (null);
        }
    }


    /**
     * Return an accessible method (that is, one that can be invoked via
     * reflection) that implements the specified Method. If no such method can
     * be found, return <code>null</code>.
     *
     * @param method The method that we wish to call
     * @return Method
     */
    public static Method getAccessibleMethod(Method method)
    {
        // Make sure we have a method to check
        if (method == null)
        {
            return (null);
        }

        // If the requested method is not public we cannot call it
        if (!Modifier.isPublic(method.getModifiers()))
        {
            return (null);
        }

        // If the declaring class is public, we are done
        Class clazz = method.getDeclaringClass();

        if (Modifier.isPublic(clazz.getModifiers()))
        {
            return (method);
        }

        // Check the implemented interfaces and subinterfaces
        method = getAccessibleMethodFromInterfaceNest(clazz, method.getName(),
            method.getParameterTypes());
        return (method);
    }


    /**
     * Return an accessible method (that is, one that can be invoked via
     * reflection) that implements the specified method, by scanning through all
     * implemented interfaces and subinterfaces. If no such method can be found,
     * return <code>null</code>. There isn't any good reason why this method
     * must be private. It is because there doesn't seem any reason why other
     * classes should call this rather than the higher level methods.
     *
     * @param clazz Parent class for the interfaces to be checked
     * @param methodName Method name of the method we wish to call
     * @param parameterTypes The parameter type signatures
     * @return Method
     */
    private static Method getAccessibleMethodFromInterfaceNest(
        Class clazz,
        String methodName,
        Class parameterTypes[])
    {
        Method method = null;

        // Search up the superclass chain
        for (; clazz != null; clazz = clazz.getSuperclass())
        {
            // Check the implemented interfaces of the parent class
            Class interfaces[] = clazz.getInterfaces();

            for (int i = 0; i < interfaces.length; i++)
            {
                // Is this interface public?
                if (!Modifier.isPublic(interfaces[i].getModifiers()))
                    continue;

                // Does the method exist on this interface?
                try
                {
                    method =
                        interfaces[i].getDeclaredMethod(
                            methodName,
                            parameterTypes);
                }
                catch (NoSuchMethodException e)
                {
                    ; // NOOP
                }

                if (method != null)
                    break;

                // Recursively check our parent interfaces
                method =
                    getAccessibleMethodFromInterfaceNest(
                        interfaces[i],
                        methodName,
                        parameterTypes);

                if (method != null)
                    break;
            }
        }

        // If we found a method return it
        if (method != null)
            return (method);

        // We did not find anything
        return (null);
    }
}
