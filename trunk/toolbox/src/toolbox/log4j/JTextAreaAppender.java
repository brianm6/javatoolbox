package toolbox.log4j;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import toolbox.util.ui.JSmartTextArea;

/**
 * A Log4J appender that dumps into a text area.
 */
public class JTextAreaAppender extends AppenderSkeleton
    implements DocumentListener
{
    /** 
     * Text area that logging statements are directed to. 
     */ 
    private JSmartTextArea textArea_;
    
    /** 
     * Layout for logging statements.
     */ 
    private PatternLayout layout_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /** 
     * Creates a new text area appender.
     */
    public JTextAreaAppender()
    {
        textArea_ = new JSmartTextArea(true, false);
        textArea_.getDocument().addDocumentListener(this);
        layout_ = new PatternLayout("%-5p %3x - %m%n");
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the text area.
     *  
     * @return Text area
     */
    public JTextArea getTextArea()
    {
        return textArea_;
    }

    //--------------------------------------------------------------------------
    // Overrides org.apache.log4j.AppenderSkeleton
    //--------------------------------------------------------------------------
    
    /** 
     * Appends the logging event information to the text area.
     * 
     * @param loggingEvent  Logging event
     */
    public void append(LoggingEvent loggingEvent)
    {
        textArea_.append(layout_.format(loggingEvent));
    }


    /** 
     * Returns whether we need a layout.
     * 
     * @return false
     */
    public boolean requiresLayout()
    {
        return false;
    }


    /** 
     * Closes this appender (does nothing).
     */
    public void close()
    {
    }

    //--------------------------------------------------------------------------
    // javax.swing.event.DocumentListener Interface
    //--------------------------------------------------------------------------
    
    /** 
     * Does nothing.
     * 
     * @param event  Document event.
     */
    public void changedUpdate(DocumentEvent event)
    {
    }


    /** 
     * Does nothing.
     * 
     * @param event  Document event.
     */
    public void removeUpdate(DocumentEvent event)
    {
    }


    /** 
     * Sets the caret position to the end of the text in the text component.
     * 
     * @param  event  Document event.
     */
    public void insertUpdate(DocumentEvent event)
    {
        textArea_.scrollToEnd();
    }
}

/*
 * Jacareto Software License, Version 1.0
 *
 * Copyright (c) 2002 Applied Computer Science Research Group,
 * Darmstadt University of Technology, and Mathematics & Computer Science
 * Department, Ludwigsburg University of Education. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided  that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowledgment:
 *      "This product includes software developed by the Applied Computer
 *       Science Research Group, Darmstadt University of Technology
 *       (http://www.pi.informatik.tu-darmstadt.de), and the Mathematics &
 *       Computer Science Department, Ludwigsburg University of Education
 *       (http://www.ph-ludwigsburg.de/mathematik/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * THE APPLIED COMPUTER SCIENCE RESEARCH GROUP, DARMSTADT UNIVERSITY OF
 * TECHNOLOGY, AND THE MATHEMATICS & COMPUTER SCIENCE DEPARTMENT, LUDWIGSBURG
 * UNIVERSITY OF EDUCATION, OR THEIR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */