package toolbox.util.xml;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * An xml tag. It can be a processing instructon, an empty tag or a normal tag. 
 * Currently, if the tag is inside a namespace then that is a part of the name. 
 * That is, all names of tags are fully qualified by the namespace.
 */
public class XMLNode
{
    private Hashtable attrs_;
    private Hashtable nodes_;     // allows quick lookup
    private Vector    nodelist_;  // maintains order of myNodes
    private String    name_;
    private String    value_;
    private boolean   procInstr_;
    private boolean   comment_;
    private boolean   doctype_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default Constructor.
     */
    public XMLNode()
    {
        this("");
    }

    /**
     * Creates a new node with the given name
     * 
     * @param  name  Name
     */
    public XMLNode(String name)
    {
        name_ = name;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Add a child node to this node
     * 
     * @param  node  Child node
     */
    public void addNode(XMLNode node)
    {
        if (nodes_ == null)
        {
            nodes_ = new Hashtable();
            nodelist_ = new Vector();
        }
        
        nodelist_.add(node);
        Object obj = nodes_.get(node.getName());
        
        if (obj == null)
        {
            nodes_.put(node.getName(), node);
        }
        else if (obj instanceof XMLNode)
        {
            Vector vec = new Vector();
            vec.addElement(obj);
            vec.addElement(node);
            nodes_.put(node.getName(), vec);
        }
        else if (obj instanceof Vector)
        {
            Vector vec = (Vector) obj;
            vec.addElement(node);
        }
    }

    /**
     * Enumerates a child node. Possibly needs renaming. That is, it enumerates 
     * a child nodes value.
     * 
     * @param   name  Name of node to enumerate
     * @return  Enumeration 
     */
    public Enumeration enumerateNode(String name)
    {
        Object obj = nodes_.get(name);
        
        if (obj == null)
        {
            return new NullEnumeration();
        }
        else if (obj instanceof Vector)
        {
            return ((Vector) obj).elements();
        }
        else
        {
            return new SingletonEnumeration(obj);
        }
    }

    /**
     * Add an attribute with specified name and value.
     * 
     * @param  name  Name of attribute
     * @param  value Value of attribute
     */
    public void addAttr(String name, String value)
    {
        if (attrs_ == null)
        {
            attrs_ = new Hashtable();
        }
        
        attrs_.put(name, value);
    }

    /**
     * Get the attribute with the specified name.
     * 
     * @param   name  Name of attribute to get
     * @return  Value of attribute
     */
    public String getAttr(String name)
    {
        if (attrs_ == null)
        {
            return null;
        }
        
        return (String) attrs_.get(name);
    }

    /**
     * Enumerate over all the attributes of this node. In the order they were 
     * added.
     * 
     * @return Enumeration over this nodes attributes
     */
    public Enumeration enumerateAttr()
    {
        if (attrs_ == null)
        {
            return new NullEnumeration();
        }
        else
        {
            return attrs_.keys();
        }
    }

    /**
     * Get the node with the specified name.
     * 
     * @param   name  Name of node to get
     * @return  Node with given name
     */
    public XMLNode getNode(String name)
    {
        if (nodes_ == null)
        {
            return null;
        }
        
        Object obj = nodes_.get(name);
        
        if (obj instanceof XMLNode)
        {
            return (XMLNode) obj;
        }
        
        return null;
    }

    /**
     * Enumerate over all of this node's children nodes.
     * 
     * @return  Enumeration over this nodes kids
     */
    public Enumeration enumerateNode()
    {
        if (nodes_ == null)
        {
            return new NullEnumeration();
        }
        else
        {
            //        return myNodes.elements();
            return nodelist_.elements();
        }
    }

    /**
     * Get the name of this node. Includes the namespace.
     * 
     * @return  Name of this node
     */
    public String getName()
    {
        return name_;
    }

    /**
     * Get the namespace of this node.
     * 
     * @return This node's namespace
     */
    public String getNamespace()
    {
        if (name_.indexOf(":") != -1)
        {
            return name_.substring(0, name_.indexOf(":"));
        }
        else
        {
            return "";
        }
    }

    /**
     * Get the tag name of this node. Doesn't include namespace.
     * 
     * @return This nodes tag name
     */
    public String getTagName()
    {
        if (name_.indexOf(":") != -1)
        {
            return name_.substring(name_.indexOf(":") + 1);
        }
        else
        {
            return name_;
        }
    }

    /**
     * Get the appended toString's of the children of this node. For a text 
     * node, it will print out the plaintext.
     * 
     * @return  This nodes value
     */
    public String getValue()
    {
        if (isComment())
        {
            return "<!-- " + value_ + " -->";
        }
        
        if (isDocType())
        {
            return "<!DOCTYPE " + value_ + ">";
        }
        
        if (value_ != null)
        {
            return value_;
        }
        
        if (isInvisible())
        {
            return "";
        }
        
        // QUERY: shouldnt call toString. Needs to improve
        if (nodelist_ != null)
        {
            StringBuffer buffer = new StringBuffer();
            Enumeration enum = enumerateNode();
            
            while (enum.hasMoreElements())
            {
                buffer.append(enum.nextElement().toString());
            }
            
            return buffer.toString();
        }
        return null;
    }

    /**
     * Set the plaintext contained in this node.
     * 
     * @param  str  Text of node
     */
    public void setPlaintext(String str)
    {
        value_ = str;
    }

    /**
     * Is this a normal tag? That is, not plaintext, not comment and not a pi.
     * 
     * @return  True of this node is a tag
     */
    public boolean isTag()
    {
        return !(procInstr_ || (name_ == null) || (value_ != null));
    }

    /**
     * @return  Is it invisible
     */
    public boolean isInvisible()
    {
        return name_ == null;
    }

    /**
     * Set whether this node is invisible or not.
     * 
     * @param  b  Set invisible
     */
    public void setInvisible(boolean b)
    {
        if (b)
        {
            name_ = null;
        }
    }

    /**
     * @return  Is it a doctype
     */
    public boolean isDocType()
    {
        return doctype_;
    }

    /**
     * Set whether this node is a doctype or not.
     * 
     * @param  b  Set doc type
     */
    public void setDocType(boolean b)
    {
        doctype_ = b;
    }

    /**
     * @return  Is it a comment
     */
    public boolean isComment()
    {
        return comment_;
    }

    /**
     * Set whether this node is a comment or not.
     * 
     * @param  b  Set is comment
     */
    public void setComment(boolean b)
    {
        comment_ = b;
    }

    /**
     * @return  Is it a processing instruction    
     */
    public boolean isPI()
    {
        return procInstr_;
    }

    /**
     * Set whether this node is a processing instruction or not.
     * 
     * @param  b  Set PI
     */
    public void setPI(boolean b)
    {
        procInstr_ = b;
    }

    // IMPL: Assumes that you're unable to remove nodes from 
    //       a parent node. removeNode and removeAttr is likely to 
    //       become a needed functionality.
    
    /**
     * @return  Is this node empty.
     */
    public boolean isEmpty()
    {
        return (nodes_ == null);
    }

    /**
     * @return  Is this a text node.
     */
    public boolean isTextNode()
    {
        return ((value_ != null) && !comment_ && !doctype_ && !procInstr_);
    }

    // not entirely necessary, but allows XMLNode's to be output 
    // int XML by calling .toString() on the root node.
    // Probably wants some indentation handling?
    
    /**
     * Turn this node into a String. Outputs the node as XML. So a large amount 
     * of output.
     * 
     * @return  Node as a string
     */
    public String toString()
    {
        if (isComment())
        {
            return "<!-- " + value_ + " -->";
        }
        
        if (isDocType())
        {
            return "<!DOCTYPE " + value_ + ">";
        }
        
        if (value_ != null)
        {
            return value_;
        }
        

        StringBuffer tmp = new StringBuffer();

        if (!isInvisible())
        {
            tmp.append("<");
            
            if (isPI())
            {
                tmp.append("?");
            }
            
            tmp.append(name_);
        }

        Enumeration enum = enumerateAttr();
        
        while (enum.hasMoreElements())
        {
            tmp.append(" ");
            String obj = (String) enum.nextElement();
            tmp.append(obj);
            tmp.append("=\"");
            tmp.append(getAttr(obj));
            tmp.append("\"");
        }
        
        if (isEmpty())
        {
            if (isPI())
            {
                tmp.append("?>");
            }
            else
            {
                if (!isInvisible())
                {
                    tmp.append("/>");
                }
            }
        }
        else
        {
            if (!isInvisible())
            {
                tmp.append(">");
            }

            enum = enumerateNode();
            
            while (enum.hasMoreElements())
            {
                Object obj = enum.nextElement();
                
                if (obj instanceof XMLNode)
                {
                    XMLNode node = (XMLNode) obj;
                    tmp.append(node);
                }
                else if (obj instanceof Vector)
                {
                    Vector nodelist = (Vector) obj;
                    Enumeration nodeEnum = nodelist.elements();
                    
                    while (nodeEnum.hasMoreElements())
                    {
                        XMLNode node = (XMLNode) nodeEnum.nextElement();
                        tmp.append(node);
                    }
                }
            }

            if (!isInvisible())
            {
                tmp.append("</" + name_ + ">");
            }
        }
        
        return tmp.toString();
    }

    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------

    /**
     * A null implementation of an Enumeration. It contains nothing.
     */
    class NullEnumeration implements Enumeration
    {

        public Object nextElement()
        {
            return null;
        }

        public boolean hasMoreElements()
        {
            return false;
        }

    }

    /**
     * An enumeration that contains a single instance.
     */
    class SingletonEnumeration implements Enumeration
    {
        private Object obj_;
        private boolean notdone_ = true;

        /**
         * Construct with the object that is to be returned by this Enumeration.
         */
        public SingletonEnumeration(Object obj)
        {
            obj_ = obj;
        }

        public Object nextElement()
        {
            notdone_ = false;
            return obj_;
        }

        public boolean hasMoreElements()
        {
            return notdone_;
        }
    }
}