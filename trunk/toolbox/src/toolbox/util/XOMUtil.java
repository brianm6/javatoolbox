package toolbox.util;

import nu.xom.Element;
import nu.xom.Elements;

/**
 * 
 */
public class XOMUtil
{
    public static int getInteger(Element node, int defaultValue)
    {
        int intValue = defaultValue;
        
        if (node != null && !StringUtil.isNullOrBlank(node.getValue()))
            intValue = Integer.parseInt(node.getValue());
            
        return intValue;
    }
    
    public static String getString(Element node, String defaultValue)
    {
        String stringValue = defaultValue;
        
        if (node != null && !StringUtil.isNullOrBlank(node.getValue()))
            stringValue = node.getValue();
            
        return stringValue;
    }

    public static boolean getBoolean(Element node, boolean defaultValue)
    {
        boolean booleanValue = defaultValue;
        
        if (node != null && !StringUtil.isNullOrBlank(node.getValue()))
            booleanValue = new Boolean(node.getValue()).booleanValue();
            
        return booleanValue;
    }
        
    public static int getIntegerAttribute(Element node, String attribute, 
        int defaultValue)
    {
        int intValue = defaultValue;
        
        if (node != null && node.getAttributeValue(attribute) != null)
            intValue = Integer.parseInt(node.getAttributeValue(attribute));
            
        return intValue;
    }
        
    public static boolean getBooleanAttribute(Element node, String attribute,
        boolean defaultValue)
    {
        boolean booleanValue = defaultValue;
        
        if (node != null && node.getAttributeValue(attribute) != null)
            booleanValue = 
                new Boolean(node.getAttributeValue(attribute)).booleanValue();
            
        return booleanValue;
    }

    public static String getStringAttribute(Element node, String attribute, 
        String defaultValue)
    {
        String stringValue = defaultValue;
        
        if (node != null &&  node.getAttributeValue(attribute) != null)
            stringValue = node.getAttributeValue(attribute);
            
        return stringValue;
    }
    
    public static void injectChild(Element parent, Element child)
    {
        String childName = child.getLocalName();
        
        Elements existing = parent.getChildElements(childName);
        
        switch (existing.size())
        {
            case 0 : parent.appendChild(child); break;
            
            case 1 : parent.replaceChild(existing.get(0), child); break;
            
            default: throw new IllegalArgumentException(
                "Cannot inject child if there are " + existing.size() + 
                " existing children with the same name: " + 
                child.getLocalName());
        }
    }
}
