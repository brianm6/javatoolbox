package toolbox.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML Utility Class.
 */
public class XMLUtil
{
    private static final Logger logger_ = 
        Logger.getLogger(XMLUtil.class);
        
    /**
     * Formats and indents XML. 
     * 
     * <pre>
     * Indent = 2 spaces
     * Line Width = default (72)
     * Omit Declaration = false
     * </pre>
     * 
     * @param xml XML string to format
     * @return Formatted XML string
     * @throws SAXException on parsing error
     * @throws IOException on I/O error
     */
    public static String format(String xml) throws SAXException, IOException
    {
        return format(xml, 2, -1, false);
    }

    
    /**
     * Formats and indents XML to make it easy to read.
     * 
     * @param xml XML string to format
     * @param indent Number of spaces per indentation 
     * @param lineWidth Max line width afterwhich the line is wrapped
     * @param omitDeclaration Set to true to omit the xml declaration
     * @return Formatted XML string
     * @throws SAXException on parsing error
     * @throws IOException on I/O error
     */
    public static String format(
        String xml, 
        int indent, 
        int lineWidth, 
        boolean omitDeclaration) 
        throws SAXException, IOException
    {
        String formattedXML = null;

        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new StringReader(xml)));
        Document doc = parser.getDocument();
        StringWriter writer = new StringWriter();
        OutputFormat format = new OutputFormat();
        
        // TODO: what difference does this make?
        format.setMethod(Method.FOP);
        
        format.setIndenting(indent>0);
        format.setIndent(indent);
        if (lineWidth > 0) format.setLineWidth(lineWidth);
        format.setOmitXMLDeclaration(omitDeclaration);
        XMLSerializer serializer = new XMLSerializer(writer, format);
        serializer.serialize(doc);
        formattedXML = writer.toString();
        return formattedXML;
    }

    
    /**
     * Converts a string XML element into its DOM counterpart.
     * 
     * @param xml XML to load into a DOM element
     * @return DOM Element representing xml string
     * @throws IOException on I/O error
     * @throws SAXException on XML parsing error
     */
    public static Element loadElement(String xml) 
        throws IOException, SAXException
    {
        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new StringReader(xml)));
        return parser.getDocument().getDocumentElement();
    }
}