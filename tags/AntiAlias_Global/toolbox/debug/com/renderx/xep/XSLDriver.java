package com.renderx.xep;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;

import com.renderx.sax.XMLReaderFactory;
import com.renderx.util.Args;
import com.renderx.util.Hashtable;
import com.renderx.xep.gen.H4base;
import com.renderx.xep.gen.backends.H4XML;
import com.renderx.xep.lib.Conf;
import com.renderx.xep.lib.EventLogger;
import com.renderx.xep.lib.LicenseException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

// Referenced classes of package com.renderx.xep:
//            JAXPDriver, Driver

public class XSLDriver
{
    static
    {
        System.out.println("\n\n\t\tModified XSLDriver v1\n\n");
    }
    
    static class QuietLogger extends DefaultHandler
    {

        String message;

        public void startElement(String s, String s1, String s2, 
            Attributes attributes)
        {
            message = "";
        }

        public void endElement(String s, String s1, String s2)
        {
            if ("warning".equals(s1))
                Conf.err.println("warning: " + message);
            else
            if ("error".equals(s1))
                Conf.err.println("error: " + message);
        }

        public void characters(char ac[], int i, int j)
        {
            message += new String(ac, i, j);
        }

        QuietLogger()
        {
        }
    }


    private static final Hashtable formats;
    private static final String pprefix = "com.renderx.xep.";
    private static final short FO = 1;
    private static final short XSLT = 2;
    private static final short XEP = 3;
    private static final short HELP = 0;
    private static final short OPTIONS = 1;
    private static final short SWITCHES = 2;
    private static final short INPUT = 3;
    private static final short OUTPUT = 4;

    public XSLDriver()
    {
    }

    public static void main(String args[])
        throws Exception
    {
        String s = null;
        String s1 = null;
        String s2 = null;
        String args1[] = null;
        Hashtable hashtable = new Hashtable();
        java.util.Properties properties = System.getProperties();
        Args args2 = new Args(args);
        byte byte0 = 0;
        try
        {
            short word0 = 0;
            for (int j = 0; j != args2.tokens.length; j++)
label0:
                switch (word0)
                {
                default:
                    break;

                case 0: // '\0'
                    if (args2.tokens[j].typ == 0)
                    {
                        String s3 = args2.tokens[j].s0.toLowerCase();
                        if (s3.equals("help") || s3.equals("h"))
                        {
                            printUsage();
                            System.out.println("Hack..not exiting " + 0);
                        }
                    }
                    j--;
                    word0++;
                    break;

                case 1: // '\001'
                    switch (args2.tokens[j].typ)
                    {
                    case 1: // '\001'
                    default:
                        break;

                    case 2: // '\002'
                        String s4 = args2.tokens[j].s0;
                        String s8 = args2.tokens[j].s1;
                        if (!s4.startsWith("com.renderx.xep."))
                            s4 = "com.renderx.xep." + s4;
                        properties.put(s4, s8);
                        break;

                    case 0: // '\0'
                    case 3: // '\003'
                        j--;
                        word0++;
                        break;
                    }
                    break;

                case 2: // '\002'
                    switch (args2.tokens[j].typ)
                    {
                    case 1: // '\001'
                    default:
                        break;

                    case 2: // '\002'
                        Conf.err.println("error: unexpected command line argument #" + (j + 1) + "(mode switch): '" + args[j] + "'");
                        break label0;

                    case 0: // '\0'
                        String s5 = args2.tokens[j].s0.toLowerCase();
                        String s9 = args2.tokens[j].s1;
                        if (s5.equals("version"))
                        {
                            Conf.err.println("XEP 3.3.1 Trial");
                            if (args2.tokens.length == 1)
                                System.out.println("Hack..not exiting " + 0);
                            break label0;
                        }
                        if (s5.equals("quiet"))
                        {
                            EventLogger.logger().setContentHandler(new QuietLogger());
                            break label0;
                        }
                        if (s5.equals("q"))
                        {
                            Conf.err.println("warning: -q is deprecated, use -quiet instead");
                            args2.tokens[j--].s0 = "quiet";
                            break label0;
                        }
                        if (s5.equals("valid"))
                        {
                            properties.put("com.renderx.xep.VALIDATE", "false");
                        } else
                        {
                            j--;
                            word0++;
                        }
                        break;

                    case 3: // '\003'
                        j--;
                        word0++;
                        break;
                    }
                    break;

                case 3: // '\003'
                    switch (args2.tokens[j].typ)
                    {
                    case 1: // '\001'
                    default:
                        break;

                    case 2: // '\002'
                        Conf.err.println("error: unexpected command line argument #" + (j + 1) + "(input parameter): '" + args[j] + "'");
                        break label0;

                    case 0: // '\0'
                        String s6 = args2.tokens[j].s0.toLowerCase();
                        String s10 = args2.tokens[j].s1;
                        if (byte0 == 0)
                        {
                            if (s6.equals("xml"))
                            {
                                byte0 = 2;
                                s = args2.tokens[++j].s0;
                                break label0;
                            }
                            if (s6.equals("fo"))
                            {
                                byte0 = 1;
                                s = args2.tokens[++j].s0;
                                word0++;
                                break label0;
                            }
                            if (byte0 == 0 && s6.equals("xep"))
                            {
                                byte0 = 3;
                                s = args2.tokens[++j].s0;
                                word0++;
                            }
                            break label0;
                        }
                        if (byte0 != 2)
                            break label0;
                        if (s6.equals("xsl"))
                        {
                            s1 = args2.tokens[++j].s0;
                            break label0;
                        }
                        if (s6.equals("param"))
                        {
                            String s13 = args2.tokens[++j].s0;
                            int k = s13.indexOf('=');
                            if (k == -1)
                                hashtable.put(s13, "");
                            else
                                hashtable.put(s13.substring(0, k), s13.substring(k + 1));
                        } else
                        {
                            j--;
                            word0++;
                        }
                        break label0;

                    case 3: // '\003'
                        if (byte0 == 0)
                        {
                            byte0 = 2;
                            s = args2.tokens[j].s0;
                        } else
                        {
                            j--;
                            word0++;
                        }
                        break;
                    }
                    break;

                case 4: // '\004'
                    switch (args2.tokens[j].typ)
                    {
                    case 1: // '\001'
                    default:
                        break label0;

                    case 2: // '\002'
                        Conf.err.println("error: unexpected command line argument #" + (j + 1) + " (output parameter): '" + args[j] + "'");
                        break label0;

                    case 0: // '\0'
                        String s7 = args2.tokens[j].s0.toLowerCase();
                        String s11 = args2.tokens[j].s1;
                        if (s7.equals("out"))
                            break label0;
                        if (s7.equals("format"))
                        {
                            args2.tokens[j + 1].typ = 0;
                            break label0;
                        }
                        if (formats.containsKey(s7))
                        {
                            if (args1 != null)
                                Conf.err.println("error: duplicate output format");
                            args1 = (String[])formats.get(s7);
                        } else
                        {
                            Conf.err.println("error: unexpected command line argument #" + (j + 1) + " (output parameter): '" + args[j] + "'");
                        }
                        break label0;

                    case 3: // '\003'
                        break;
                    }
                    if (s2 != null)
                    {
                        String s14 = args2.tokens[j].s0;
                        int l = s14.indexOf('=');
                        if (l != -1 && byte0 == 2)
                        {
                            Conf.err.println("warning: trailing transformation parameters are deprecated, use -param instead");
                            hashtable.put(s14.substring(0, l), s14.substring(l + 1));
                        } else
                        {
                            Conf.err.println("error: duplicate output");
                        }
                    } else
                    {
                        s2 = args2.tokens[j].s0;
                    }
                    break;
                }

        }
        catch (ArrayIndexOutOfBoundsException arrayindexoutofboundsexception)
        {
            Conf.err.println("error: too few command line arguments");
            System.out.println("Hack..not exiting " + 1);
        }
        if (byte0 == 0)
            byte0 = 2;
        if (s == null)
            s = "";
        if (args1 == null)
            args1 = (String[])formats.get("pdf");
        if (s2 == null)
            if (s.equals(""))
            {
                s2 = "";
            } else
            {
                int i = s.lastIndexOf(".");
                s2 = (i != -1 ? s.substring(0, i) : s) + "." + args1[2];
            }
        String s12 = args1[1];
        if (s12 == null)
        {
            Conf.err.println("Generation of " + args1[0] + " not supported");
            enumerateFormats();
            System.out.println("Hack..not exiting " + 1);
        }
        try
        {
            System.setProperties(properties);
            Driver.init();
        }
        catch (LicenseException licenseexception)
        {
            Conf.err.println("License check failed: ");
            Conf.err.println(licenseexception.getMessage());
            Conf.err.println("If you keep getting this error after the activation procedure, ");
            Conf.err.println("please contact support@renderx.com attaching your license.txt file. ");
            System.out.println("Hack..not exiting " + 1);
        }
        catch (Exception exception)
        {
            Conf.err.println("Formatter initialization failed: the formatter has thrown an exception.");
            Conf.err.println(exception.toString());
            System.out.println("Hack..not exiting " + 1);
        }
        H4base h4base = null;
        try
        {
            Class class1 = Class.forName(s12);
            class1.getMethod("init", null).invoke(null, null);
            h4base = (H4base)class1.newInstance();
        }
        catch (Exception exception1)
        {
            Conf.err.println("error: cannot load output producer '" + args1[1] + "' for '" + args1[0] + "'.");
            System.out.println("Hack..not exiting " + 1);
        }
        Object obj = null;
        Object obj1 = null;
        try
        {
            if (s2.equals(""))
            {
                obj = new File("stdout") {

                    public boolean delete()
                    {
                        return false;
                    }

                };
                obj1 = System.out;
            } else
            {
                obj = new File(s2);
                obj1 = new FileOutputStream(((File) (obj)));
            }
        }
        catch (IOException ioexception)
        {
            Conf.err.println("error: cannot open file " + s2 + " for writing: " + ioexception.getMessage());
            System.out.println("Hack..not exiting " + 1);
        }
        catch (Exception exception2)
        {
            Conf.err.println("error: problem accessing file '" + s2 + "': " + exception2.toString());
            System.out.println("Hack..not exiting " + 1);
        }
        try
        {
            try
            {
                h4base.setOutFile(((OutputStream) (obj1)));
                if (h4base instanceof H4XML)
                    ((H4XML)h4base).setImageDir(s2);
                InputSource inputsource;
                if (s.equals(""))
                {
                    inputsource = new InputSource(System.in);
                    inputsource.setSystemId((new URL(new URL(Conf.BASE), "stdin")).toExternalForm());
                } else
                {
                    inputsource = new InputSource(makeSystemId(s));
                }
                switch (byte0)
                {
                case 1: // '\001'
                    Driver.render(inputsource, h4base);
                    break;

                case 3: // '\003'
                    h4base.setBasePath((new File((new File(s)).getAbsolutePath())).getParent());
                    XMLReader xmlreader = XMLReaderFactory.createXMLReader();
                    xmlreader.setContentHandler(h4base);
                    xmlreader.parse(inputsource);
                    break;

                case 2: // '\002'
                    Object obj2 = null;
                    if (s1 != null)
                        obj2 = new SAXSource(new InputSource(makeSystemId(s1)));
                    else
                    if (s.equals(""))
                        Conf.err.println("warning: will not try to get associated stylesheet for standard input");
                    else
                        try
                        {
                            SAXSource saxsource = new SAXSource(inputsource);
                            TransformerFactory transformerfactory = TransformerFactory.newInstance();
                            obj2 = transformerfactory.getAssociatedStylesheet(saxsource, null, null, null);
                        }
                        catch (TransformerConfigurationException transformerconfigurationexception)
                        {
                            Conf.err.println("error: cannot get associated stylesheet: " + transformerconfigurationexception.getMessage());
                        }
                    JAXPDriver jaxpdriver = obj2 != null ? new JAXPDriver(((javax.xml.transform.Source) (obj2))) : new JAXPDriver();
                    String s15;
                    String s16;
                    for (Enumeration enumeration = hashtable.keys(); enumeration.hasMoreElements(); jaxpdriver.setParameter(s15, s16))
                    {
                        s15 = (String)enumeration.nextElement();
                        s16 = (String)hashtable.get(s15);
                    }

                    jaxpdriver.transform(inputsource, h4base);
                    break;
                }
            }
            finally
            {
                try
                {
                    ((OutputStream) (obj1)).close();
                }
                catch (Exception exception5) { }
            }
        }
        catch (IOException ioexception1)
        {
            Conf.err.println("error: I/O error: " + ioexception1.getMessage());
            ((File) (obj)).delete();
            System.out.println("Hack..not exiting " + 1);
        }
        catch (SAXException saxexception)
        {
            Conf.err.println("error: SAX parsing error: " + saxexception.getMessage());
            ((File) (obj)).delete();
            System.out.println("Hack..not exiting " + 1);
        }
        catch (Exception exception3)
        {
            Conf.err.println("error: formatting failed: " + exception3.toString());
            if (!Conf.VALIDATE)
                Conf.err.println("XEP has been run in non-validating mode. Check your input for possible XSL FO errors.");
            ((File) (obj)).delete();
            System.out.println("Hack..not exiting " + 1);
        }
        System.out.println("Hack..not exiting " + 0);
    }

    private static String makeSystemId(String s)
    {
        try
        {
            return Conf.pathToURL(s);
        }
        catch (Exception exception)
        {
            Conf.err.println(exception.getMessage());
        }
        System.out.println("Hack..not exiting " + 1);
        return null;
    }

    private static void complain(String s)
    {
        Conf.err.println(s);
        printUsage();
    }

    private static void printUsage()
    {
        Conf.err.println("XEP 3.3.1 Trial");
        Conf.err.println("java com.renderx.xep.XSLDriver \n       -help\n     | {option} {-quiet|-version|-valid}\n       ( \n         [-xml] <infile> [-xsl <stylesheet>] {-param <name=value>}\n       | -fo <infile> \n       | -xep <infile> \n       )\n       [[-<output format>] <outfile>]\n       [-format <output format>]\n");
        enumerateFormats();
    }

    private static void enumerateFormats()
    {
        Conf.err.print("Available output formats:");
        Enumeration enumeration = formats.keys();
        String s = " ";
        while (enumeration.hasMoreElements()) 
        {
            String s1 = (String)enumeration.nextElement();
            String as[] = (String[])formats.get(s1);
            if (as[1] != null)
            {
                Conf.err.print(s + s1 + " (" + as[0] + ")");
                s = ", ";
            }
        }
        Conf.err.println(".");
    }

    static 
    {
        formats = new Hashtable();
        formats.put("pdf", new String[] {
            "PDF", "com.renderx.xep.gen.backends.H4PDF", "pdf"
        });
        formats.put("ps", new String[] {
            "Postscript", "com.renderx.xep.gen.backends.H4PS", "ps"
        });
        formats.put("xep", new String[] {
            "XEP", "com.renderx.xep.gen.backends.H4XML", "xep"
        });
        formats.put("at", formats.get("xep"));
        formats.put("pcl", new String[] {
            "PCL", null, "pcl"
        });
        formats.put("mif", new String[] {
            "MIF", null, "mif"
        });
        formats.put("txt", new String[] {
            "Plain Text", null, "txt"
        });
        formats.put("awt", new String[] {
            "AWT Preview", null, null
        });
    }
}
