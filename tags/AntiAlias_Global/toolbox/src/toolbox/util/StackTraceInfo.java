package toolbox.util;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * StackTraceInfo provides stack trace information given its originating 
 * Throwable/Exception.
 */
public class StackTraceInfo implements Serializable 
{
    //--------------------------------------------------------------------------
    // Static Fields 
    //--------------------------------------------------------------------------
    
    private static StringWriter sw = new StringWriter();
    private static PrintWriter pw = new PrintWriter(sw);
    
    private static final String LINE_SEP = System.getProperty("line.separator");
    
    /**
     * When location information is not available the constant
     * <code>NA</code> is returned. Current value of this string
     * constant is <b>?</b>.  
     */
    private final static String NA = "?";
    
    //--------------------------------------------------------------------------
    // Private Fields 
    //--------------------------------------------------------------------------
    
    /**
     * Caller's line number.
     */
    private transient String lineNumber_;
    
    /**
     * Caller's file name.
     */
    private transient String fileName_;
    
    /**
     * Caller's fully qualified class name.
     */
    private transient String className_;
    
    /**
     * Caller's method name.
     */
    private transient String methodName_;
    
    /**
     * All available caller information, in the format
     * 
     * <code>
     * fully.qualified.classname.of.caller.methodName(Filename.java:line)
     * </code>
     */
    private String fullInfo_;
    

    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Instantiate location information based on a Throwable. We
     * expect the Throwable <code>t</code>, to be in the format
     * 
     *    <pre>
     *    java.lang.Throwable
     *    ...
     *      at org.apache.log4j.PatternLayout.format(PatternLayout.java:413)
     *      at org.apache.log4j.FileAppender.doAppend(FileAppender.java:183)
     *      at org.apache.log4j.Category.callAppenders(Category.java:131)
     *      at org.apache.log4j.Category.log(Category.java:512)
     *      at callers.fully.qualified.className.methodName(FileName.java:74)
     *    ...
     *    </pre>
     *
     * <p>
     * However, we can also deal with JIT compilers that "lose" the
     * location information, especially between the parentheses.
     * 
     * @param  t          Throwable from which to extract stack trace info
     * @param  callerFQN  Fully qualified class name of the caller class
     */
    public StackTraceInfo(Throwable t, String callerFQN)
    {
        if (t == null)
            return;

        String s;
        
        // Protect against multiple access to sw.
        synchronized (sw)
        {
            t.printStackTrace(pw);
            s = sw.toString();
            sw.getBuffer().setLength(0);
        }
        
        //System.out.println("s is ["+s+"].");
        int ibegin, iend;

        // Given the current structure of the package, the line containing 
        // "org.apache.log4j.Category." should be printed just before the 
        // caller.

        // This method of searching may not be fastest but it's safer than 
        // counting the stack depth which is not guaranteed to be constant 
        // across JVM implementations.
        
        ibegin = s.lastIndexOf(callerFQN);
        
        if (ibegin == -1)
            return;

        ibegin = s.indexOf(LINE_SEP, ibegin);
        
        if (ibegin == -1)
            return;
            
        ibegin += LINE_SEP.length();

        // determine end of line
        iend = s.indexOf(LINE_SEP, ibegin);
        
        if (iend == -1)
            return;

        // back up to first blank character
        ibegin = s.lastIndexOf("at ", iend);
        
        if (ibegin == -1)
            return;
            
        // Add 3 to skip "at ";
        ibegin += 3;

        // everything between is the requested stack item
        fullInfo_ = s.substring(ibegin, iend);
    }

    //--------------------------------------------------------------------------
    // Public 
    //--------------------------------------------------------------------------
    
    /**
     * @return Fully qualified class name of the caller making the logging 
     *         request.
     */
    public String getClassName()
    {
        if (fullInfo_ == null)
            return NA;
            
        if (className_ == null)
        {
            // Starting the search from '(' is safer because there is
            // potentially a dot between the parentheses.
            int iend = fullInfo_.lastIndexOf('(');
            
            if (iend == -1)
                className_ = NA;
            else
            {
                iend = fullInfo_.lastIndexOf('.', iend);

                int ibegin = 0;
                
                if (iend == -1)
                    className_ = NA;
                else
                    className_ = this.fullInfo_.substring(ibegin, iend);
            }
        }
        
        return className_;
    }

    /**
     * @return Filename of the caller.This information is not always available.
     */
    public String getFileName()
    {
        if (fullInfo_ == null)
            return NA;

        if (fileName_ == null)
        {
            int iend = fullInfo_.lastIndexOf(':');
            
            if (iend == -1)
                fileName_ = NA;
            else
            {
                int ibegin = fullInfo_.lastIndexOf('(', iend - 1);
                fileName_ = this.fullInfo_.substring(ibegin + 1, iend);
            }
        }
        return fileName_;
    }

    /**
     * @return Line number of the caller.
     */
    public String getLineNumber()
    {
        if (fullInfo_ == null)
            return NA;

        if (lineNumber_ == null)
        {
            int iend = fullInfo_.lastIndexOf(')');
            int ibegin = fullInfo_.lastIndexOf(':', iend - 1);
            
            if (ibegin == -1)
                lineNumber_ = NA;
            else
                lineNumber_ = this.fullInfo_.substring(ibegin + 1, iend);
        }
        return lineNumber_;
    }

    /**
     * @return Method name of the caller.
     */
    public String getMethodName()
    {
        if (fullInfo_ == null)
            return NA;
            
        if (methodName_ == null)
        {
            int iend = fullInfo_.lastIndexOf('(');
            int ibegin = fullInfo_.lastIndexOf('.', iend);
            
            if (ibegin == -1)
                methodName_ = NA;
            else
                methodName_ = this.fullInfo_.substring(ibegin + 1, iend);
        }
        
        return methodName_;
    }
}

/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  
 */

// Contributors: Mathias Rupprecht <mmathias.rupprecht@fja.com>

