package toolbox.util;

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ParseException;
import nu.xom.Serializer;

import toolbox.util.io.StringInputStream;
import toolbox.util.io.StringOutputStream;

/**
 * XOM Utility Class.
 */
public class XOMUtil
{
    /**
     * Gets the integer value from a node with the option to have a default
     * value returned when one or more of the following conditions are true.
     * <ul>
     *   <li>Node is null
     *   <li>Node does not have a value
     *   <li>Node value is not a valid integer
     * </ul>
     * 
     * @param node Node containing value.
     * @param defaultValue Value to return if the node is null or does not
     *        contain a value.
     * @return int
     */
    public static int getInteger(Element node, int defaultValue)
    {
        int intValue = defaultValue;
        
        if (node != null && !StringUtil.isNullOrBlank(node.getValue()))
        {
            try
            {
                intValue = Integer.parseInt(node.getValue());
            }
            catch (NumberFormatException nfe)
            {
                // return default
            }
        }
            
        return intValue;
    }
    
    
    /**
     * Gets the string value from a node. If the node is null, a default is
     * returned instead.
     * 
     * @param node Node containing value.
     * @param defaultValue Value to return if the node is null or does not
     *        contain a value.
     * @return String
     */
    public static String getString(Element node, String defaultValue)
    {
        String stringValue = defaultValue;
        
        if (node != null && !StringUtil.isNullOrBlank(node.getValue()))
            stringValue = node.getValue();
            
        return stringValue;
    }


    /**
     * Gets the boolean value from a node.
     * 
     * @param node Node containing a boolean value.
     * @param defaultValue Value to return if the node is null or does not
     *        contain a value.
     * @return boolean
     */
    public static boolean getBoolean(Element node, boolean defaultValue)
    {
        boolean booleanValue = defaultValue;
        
        if (node != null && !StringUtil.isNullOrBlank(node.getValue()))
            booleanValue = new Boolean(node.getValue()).booleanValue();
            
        return booleanValue;
    }


    /**
     * Gets a node's attribute as an integer. A default value is returned any
     * of the following conditions are true.
     * <ul>
     *   <li>node is null
     *   <li>attribute name is null
     *   <li>nodes does not have an attribute of the matching name
     *   <li>attribute's value is not a valid integer 
     * <ul>
     * 
     * @param node Node containing the attribute
     * @param attribute Name of the attribute
     * @param defaultValue Value to return if the attribute does not exist or 
     *        does not contain a value.
     * @return int
     */
    public static int getIntegerAttribute(
        Element node, 
        String  attribute, 
        int     defaultValue)
    {
        int intValue = defaultValue;
        
        if (node != null && node.getAttributeValue(attribute) != null)
            try
            {
                intValue = Integer.parseInt(node.getAttributeValue(attribute));
            }
            catch (NumberFormatException nfe)
            {
            }
            
        return intValue;
    }


    /**
     * Gets a node's attribute as a boolean.
     * 
     * @param node Node containing the attribute
     * @param attribute Name of the attribute
     * @param defaultValue Value to return if the attribute does not exist
     *        or does not contain a value
     * @return boolean
     */
    public static boolean getBooleanAttribute(
        Element node, String attribute, boolean defaultValue)
    {
        boolean booleanValue = defaultValue;
        
        if (node != null && node.getAttributeValue(attribute) != null)
            booleanValue = 
                new Boolean(node.getAttributeValue(attribute)).booleanValue();
            
        return booleanValue;
    }


    /**
     * Gets a node's attribute as a string.
     * 
     * @param node Node containing the attribute
     * @param attribute Name of the attribute
     * @param defaultValue Value to return if the attribute does not exist
     *        or does not contain a value.
     * @return String
     */
    public static String getStringAttribute(
        Element node, String attribute, String defaultValue)
    {
        String stringValue = defaultValue;
        
        if (node != null &&  node.getAttributeValue(attribute) != null)
            stringValue = node.getAttributeValue(attribute);
            
        return stringValue;
    }

    
    /**
     * If the parent node does not already contain a child node witht the same
     * name, the child node is appended to the parent node. If the parent 
     * already contains a child node with the same name, then the existing 
     * child node is replaced by the newer child node. If the parent contains
     * more than one child node with the same name as the newer child node then
     * an IllegalArgumentException is throw. This method is only meant to 
     * replace or insert a single node.
     * 
     * @param parent Parent node
     * @param child Child node
     */
    public static void insertOrReplace(Element parent, Element child)
    {
        String childName = child.getLocalName();
        
        Elements existing = parent.getChildElements(childName);
        
        switch (existing.size())
        {
            case 0 : parent.appendChild(child); break;
            
            case 1 : parent.replaceChild(existing.get(0), child); break;
            
            default: throw new IllegalArgumentException(
                "Cannot replace child if there are " + existing.size() + 
                " existing children with the same name: " + 
                child.getLocalName());
        }
    }


    /**
     * Converts a XOM DOM node into its XML equivalent.
     * 
     * @param node Node to convert to XML
     * @return Node as XML
     * @throws IOException on I/O error
     */
    public static String toXML(Node node) throws IOException
    {    
        StringOutputStream sos = new StringOutputStream();
        Serializer serializer = new Serializer(sos);
        serializer.setIndent(3);
        serializer.setLineSeparator("\n");
        serializer.write(new Document((Element)node.copy()));
        return sos.toString();
    }
    
    
    /**
     * Converts a valid XML string into an Element.
     * 
     * @param xml Valid XML string.
     * @return Element
     * @throws ParseException if XML is invalid or not well-formed.
     * @throws IOException if an I/O error occurs.
     */
    public static Element toElement(String xml) throws ParseException, 
        IOException
    {
        return new Builder().build(new StringInputStream(xml)).getRootElement();
    }
    
    
    /**
     * Gets the first named child element from a given node. If the node is 
     * null or the child does not exist, then the defaultNode is returned 
     * instead.
     * 
     * @param node Node to retrieve child element from
     * @param elementName Name of child element
     * @param defaultNode Returned if child element is not found
     * @return First child element if it exists, or defaultNode otherwise
     */    
    public static Element getFirstChildElement(
        Element node, 
        String elementName, 
        Element defaultNode)
    {
        Element child = null;
        
        if (node == null || elementName == null)
            child = defaultNode;
        else    
            child = node.getFirstChildElement(elementName);
            
        return (child == null ? defaultNode : child);
    }
}