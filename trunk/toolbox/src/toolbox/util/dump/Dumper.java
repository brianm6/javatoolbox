package toolbox.util.dump;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.apache.regexp.RESyntaxException;

/**
 * Simple Object Dumper
 */
public class Dumper
{
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(Dumper.class);
    
    /**
     * Carriage-return 
     */
    private static final String CR = "\n";

    /** 
     * Maximum depth of recursion
     */
    private int maxDepth_ = Integer.MAX_VALUE;

    /** 
     * Generates distinct labels
     */
    private Sequence labelGenerator_ = new Sequence();

    /**
     * Caches objects which have already been traversed
     */
    private Cache cache_ = new Cache();
    
    /**
     * Dump formatter and configuration
     */
    private IDumpFormatter formatter_;
    
    private static final String BAR      = "|   ";
    private static final String JUNCTION = "+";
    private static final String ARM      = "---";

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a Dumper with the default BasicDumpFormatter
     * 
     * @throws  RESyntaxException on regular expression error
     */
    public Dumper() throws RESyntaxException
    {
        this (new BasicDumpFormatter());
    }   

    /**
     * Creates a Dumper with the given formatter
     * 
     * @param  formatter  Dump formatting and output criteria
     */
    public Dumper(IDumpFormatter formatter)
    {
        formatter_ = formatter;
    }

    //--------------------------------------------------------------------------
    //  Public Static 
    //--------------------------------------------------------------------------
    
    /**
     * Recursively (depth-first) dives down this object's attributes and 
     * converts them to a readable formatted string.
     * 
     * @param  obj  Object to dump
     * @return String dump
     */
    public static String dump(Object obj)
    {
        return dump(obj, Integer.MAX_VALUE);
    }

    /**
     * Recursively (depth-first) dives down this object's attributes and 
     * converts them to a readable formatted string.
     * 
     * @param   obj          Object to dump
     * @param   maxDepth     Max number of levels to recurse down into the obj
     * @return  Object dumped as a tree 
     */
    public static String dump(Object obj, int maxDepth)
    {
        String dump = null;
        
        try
        {
            Dumper dumper = new Dumper();
            dumper.setMaxDepth(maxDepth);
            dump = dumper.nonStaticDumpObject(obj);    
        }
        catch (RESyntaxException re)
        {
            logger_.error("dump", re);
        }
        
        return dump;        
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    /**
     * Returns the maxDepth.
     * 
     * @return Max number of levels to recurse from the root object
     */
    public int getMaxDepth()
    {
        return maxDepth_;
    }

    /**
     * Sets the maxDepth.
     * 
     * @param maxDepth Max number of levels to recurse from the root object
     */
    public void setMaxDepth(int maxDepth)
    {
        maxDepth_ = maxDepth;
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------
    
    /**
     * Non-static method to allow access to instance variables which in turn is 
     * required for multiple simultaneous threads without interference.
     * 
     * @param  obj  Object to dump
     * @return Stringified dump of object
     */
    private String nonStaticDumpObject(Object obj)
    {
        StringBuffer buffer = new StringBuffer();
        
        try
        {
            if (obj == null)
            {
                buffer.append("null");
                return buffer.toString();
            }
            
            cache_.put(obj);
            buffer.append(obj.getClass().getName());
            buffer.append(CR);
            
            dump(obj, buffer, "");
        }
        catch (Throwable ex)
        {
            logger_.error("dump", ex);
            logger_.error(buffer.toString());
        }
        
        return buffer.toString();
    }

    /**
     * Recursively dumps an object
     *
     * @param   obj     Object to dump
     * @param   buffer  Dump buffer
     * @param   depth   Recursion depth
     * @throws  IllegalAccessException if attribute/method not accessible
     */
    private void dump(Object obj, StringBuffer buffer, String depth) 
        throws IllegalAccessException
    {
        if (obj != null)
        {
            // Go back in the class hierarchy and find the 
            // first one that's not Object
            Class cl = obj.getClass();
            Stack stack = new Stack();
            Class superClass = cl;
            
            do
            {
                stack.push(superClass);
            }
            while ((superClass = superClass.getSuperclass()) != null);
            
            while (!stack.isEmpty())
            {
                Class c = (Class) stack.pop();
                
                if (depth.length()/4 <= maxDepth_)           
                    dump(c, obj, buffer, depth);
            }
        }
    }

    /**
     * Calls itself recursively. Gets all attributes of obj and dumps relevant
     * data.
     * 
     * @param   clazz   Class representing level in class hierarchy
     * @param   obj     Object to dump
     * @param   buffer  Dump buffer
     * @param   depth   Recursion depth
     * @throws  IllegalAccessException on illegal access
     */    
    private void dump(Class clazz,  Object obj, StringBuffer buffer,
        String depth) throws IllegalAccessException
    {
        if (!formatter_.shouldInclude(clazz))
            return;
        
        // Get list of classses fields
        Field[] fields = clazz.getDeclaredFields();
        
        if (formatter_.sortFields())
            Arrays.sort(fields, new FieldNameComparator());
        
        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            field.setAccessible(true);

            // Exclude field based on criteria            
            if (formatter_.shouldInclude(field))
            {
                Class  type  = field.getType();
                Object value = field.get(obj);
                
                if ((value != null) && (!cache_.contains(value)))
                    cache_.put(value, field);
    
                // Figure out if bar needed            
                buffer.append(depth);
                buffer.append(BAR);
                buffer.append(CR);
                
                buffer.append(depth);
                buffer.append(JUNCTION);
                buffer.append(ARM);
                
                buffer.append(field.getName());
                buffer.append(" = ");
    
    
                if (value == null)
                {
                    buffer.append("null");
                    buffer.append(CR);
                }
                else
                {
                    if ((!type.isPrimitive()) && 
                        (value != null) &&
                        (cache_.hasTraversed(value)))
                    {
                        buffer.append("[Visited] ");
                        buffer.append(
                            cache_.getInfo(value).getField().getName());
                        buffer.append(CR);
                    }
                    else
                    {
                        ObjectInfo objInfo = cache_.getInfo(value);
    
                        if (objInfo != null)
                            objInfo.setTraversed(true);
                        
                        String strObj = value.toString();
                        buffer.append(strObj == null ? "null" : strObj);
                        
                        appendInheritance(type, buffer);
                        
                        buffer.append(CR);
                        
                        if ((depth + BAR).length()/4 < maxDepth_)
                            dump(type, value, buffer, depth + BAR);
                    }
                }
            }
        }
    }

    /**
     * Appends the given classes inheritance tree to the dump buffer
     * 
     * @param  clazz   Class for which to print the tree    
     * @param  buffer  Dump buffer
     */
    public void appendInheritance(Class clazz, StringBuffer buffer)
    {
        if (formatter_.showInheritance())
        {
            if (clazz.isPrimitive())
            {
                ; // NOOP
            }
            else
            {
                Class currentClass = clazz;
                buffer.append(" [");
                
                do
                {
                    String className = currentClass.getName();
                    buffer.append(formatter_.formatClass(className));

                    if (!(currentClass == Object.class))
                        buffer.append(" -> ");
                }
                while ((currentClass = currentClass.getSuperclass()) != null);
                
                buffer.append("] ");
            }
        }
    }
    
    /**
     * Appends relevant information about this object to the buffer.
     * 
     * @param  obj     Object to append info for
     * @param  buffer  Dump buffer
     * @param  depth   Recursion depth
     */
    private void appendObjectInfo(Object obj, StringBuffer buffer,
        String depth)
    {
        String label = cache_.getInfo(obj).getSequenceNumber();
        appendObjectInfo(obj, buffer, depth, label);
    }
  
    /**
     * Appends relevant information about this object to the buffer.
     * 
     * @param  obj     Object to append info for
     * @param  buffer  Dump buffer
     * @param  depth   Recursion depth
     * @param  label   Label
     */
    private void appendObjectInfo(Object obj, StringBuffer buffer,
        String depth, String label)
    {
        buffer.append(depth);
        
        if (obj == null)
        {
            buffer.append("null");
        }
        else
        {
            Class type = obj.getClass();
            
            if (showActualType())
            {
                appendTypeInfo(
                    formatColumn("Type"), type, buffer, depth);
            }
            
            if (showHashCode())
            {
                buffer.append(" (hash:");
                buffer.append(Integer.toHexString(obj.hashCode()));
                buffer.append(")");
            }
            
            if (showHashCode() || showActualType())
                buffer.append(CR);
        }
    }

    /**
     * Appends relevant information about this type to the buffer.
     * 
     * @param  txt     Text
     * @param  type    Class type
     * @param  buffer  Dump buffer
     * @param  depth   Recursion depth
     */
    private void appendTypeInfo(String txt, Class type, StringBuffer buffer,
        String depth)
    {
        if (type.isPrimitive())
        {
            indent(buffer, depth);
            buffer.append(txt);
            buffer.append(type.getName());
        }
        else
        {
            // Class hierarchy
            Class tmpClass = type;
            indent(buffer, depth);
            buffer.append(txt);
            
            if (!formatter_.showInheritance())
            {
                String className = tmpClass.getName();
                buffer.append(formatter_.formatClass(className));
            }
            else
            {
                do
                {
                    String className = tmpClass.getName();
                    buffer.append(formatter_.formatClass(className));
                    
                    if (!(tmpClass == Object.class))
                        buffer.append(" -> ");
                }
                while ((tmpClass = tmpClass.getSuperclass()) != null);
            }
        }
    }
    
    /**
     * Translates modifiers passed as an INT to a STRING.
     * 
     * @param   mod  Modifier
     * @return  String modifier
     */
    private String getModifiersAsString(int mod)
    {
        StringBuffer sb = new StringBuffer();
        List list = new ArrayList();
        
        if (Modifier.isSynchronized(mod))
            list.add("synchronized");
        if (Modifier.isPrivate(mod))
            list.add("private");
        if (Modifier.isProtected(mod))
            list.add("protected");
        if (Modifier.isPublic(mod))
            list.add("public");
        if (Modifier.isStatic(mod))
            list.add("static");
        if (Modifier.isFinal(mod))
            list.add("final");
        if (Modifier.isTransient(mod))
            list.add("transient");
        if (Modifier.isVolatile(mod))
            list.add("volatile");
            
        sb.append("[");
        Iterator i = list.iterator();
        
        while (i.hasNext())
        {
            sb.append((String) i.next());
            if (i.hasNext())
                sb.append(", ");
        }
        
        sb.append("] ");
        return sb.toString();
    }

    /**
     * Appends to the object dump buffer
     * 
     * @param  buffer  Dump buffer
     * @param  txt     Text to append
     * @param  depth   Recursion Depth
     */
    private final void append(StringBuffer buffer, String txt, String depth)
    {
        indent(buffer, depth);
        buffer.append(txt);
        buffer.append(CR);
    }

    /**
     * Indents based on recursion level
     * 
     * @param  buffer  Dump buffer
     * @param  depth   Levels of recursion
     */
    private final void indent(StringBuffer buffer, String depth)
    {
        indent(buffer, depth, "" /* LABEL_SPACE */);
    }

    /**
     * Indents based on recursion level
     * 
     * @param  buffer  Dump buffer
     * @param  depth   Recursion depth
     * @param  label   Label to use
     */
    private final void indent(StringBuffer buffer, String depth, String label)
    {
        buffer.append(depth);
    }

    /**
     * @return Show the hash Code?
     */
    private boolean showHashCode()
    {
        return false;
    }

    /**
     * @return Show the class that a field is declared in
     */
    private boolean showDeclaredIn()
    {
        return false;
    }
    
    /**
     * @return Show the fields access modifiers 
     */
    private boolean showAccessModifiers()
    {
        return false;
    }

    /**
     * @return Show the actual type of an object (int -> Integer)
     */
    private boolean showActualType()
    {
        return false;
    }

    /**
     * Formats a column field
     *
     * @param   col     Text of column
     * @return  Fixed width formatted text
     */
    private String formatColumn(String col)
    {
        return col + "--";
    }

    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Sequence number generator
     */
    private class Sequence
    {
        private int num_ = 0;
        
        public Sequence()
        {
        }
        
        public String getNext()
        {
            return "" + num_++;                 
        }
    }

    /**
     * Information (value) stored for each object (key) in the cache.
     */
    private class ObjectInfo
    {
        private Object  object_;
        private boolean traversed_;
        private String  seqNum_;
        private Field   field_;
        
        
        public ObjectInfo(Object object, String seqNum)
        {
            this(object, seqNum, null);
        }

        public ObjectInfo(Object object, String seqNum, Field field)
        {
            object_ = object;
            seqNum_ = seqNum;
            field_  = field;            
        }
        
        /**
         * @return Sequence number for unique identification
         */
        public String getSequenceNumber()
        {
            return seqNum_;
        }
        
        /**
         * @return  True if the object has been traversed previously, false
         *          otherwise.
         */
        public boolean hasTraversed()
        {
            return traversed_;
        }

        /**
         * Set the flag for whether the object has been traversed already
         * 
         * @param  traversed  Traversed flag
         */
        public void setTraversed(boolean traversed)
        {
            traversed_ = traversed;
        }
        
        /**
         * @return Field if any that was related to this object
         */
        public Field getField()
        {
            return field_;
        }
    }

    /**
     * Keeps information (value) stored for each object (key). Necessary in 
     * order to know wheather this object has been displayed before and by 
     * which label it can be referred to.
     */
    private class Cache
    {
        private Map visitedMap_ = new HashMap();
        
        public void put(Object obj)
        {
            put(obj, null);    
        }
        
        public void put(Object obj, Field field)
        {
            if (obj == null)
                throw new RuntimeException("Object null");
                
            visitedMap_.put(obj, 
                new ObjectInfo(obj, labelGenerator_.getNext(), field));
        }
        
        public ObjectInfo getInfo(Object obj)
        {
            if (obj == null)
                return null;
            else
            {
                ObjectInfo oi = (ObjectInfo) visitedMap_.get(obj);
                return oi;
            }
        }
        
        public boolean contains(Object obj)
        {
            if (obj == null)
                throw new RuntimeException("Object null");
                
            return visitedMap_.containsKey(obj);
        }
        
        public boolean hasTraversed(Object obj)
        {
            if (obj == null)
                throw new RuntimeException("Object Null");
                
            ObjectInfo oi = null;
            return (((oi = getInfo(obj)) != null) && oi.hasTraversed());
        }
    }

    /** 
     * Comparator for field names
     */
    class FieldNameComparator implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            String name1 = ((Field)o1).getName();
            String name2 = ((Field)o2).getName();
            return name1.compareTo(name2);
        }
    }
}
