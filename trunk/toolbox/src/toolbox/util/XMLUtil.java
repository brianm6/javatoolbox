package toolbox.util;

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * XML Utility Class
 */
public class XMLUtil
{
    private static final Logger logger_ = 
        Logger.getLogger(XMLUtil.class);
        
    /**
     * Formats and indents XML to make it easy to read
     * 
     * @param   xml  XML string to format
     * @return  Formatted XML string
     */
    public static String format(String xml)
    {
        String formattedXML = null;

        try
        {
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(new StringReader(xml)));
            Document doc = parser.getDocument();
            StringWriter writer = new StringWriter();
            OutputFormat format = new OutputFormat(doc);
            format.setIndenting(true);
            format.setIndent(3);
            format.setOmitXMLDeclaration(true);
            XMLSerializer serializer = new XMLSerializer(writer, format);
            serializer.serialize(doc);
            formattedXML = writer.toString();
        }
        catch (Exception ioe)
        {
            System.out.println(ioe);
        }

        return formattedXML;
    }
}
