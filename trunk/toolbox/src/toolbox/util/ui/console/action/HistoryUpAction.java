/*
 * Copyright © 2004 Southwest Airlines, Inc.  All Rights Reserved.
 *   
 * This software is the proprietary and confidential information of 
 * Southwest Airlines, Inc.
 */
package toolbox.util.ui.console.action;

import java.awt.event.ActionEvent;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;

import toolbox.util.ui.console.UIConsole;


/**
 * Scrolls up through the command history.
 * 
 * @author Semir Patel
 */
public class HistoryUpAction extends AbstractAction 
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Number of commands in the history list last time the command was invoked.
     */
    private static int previousHistorySize_ = -1;
    
    /**
     * Iterator for the history list.
     */
    private static Iterator iterator_;
    
    /**
     * History list.
     */
    private static List list_;
    
    /**
     * Associated UI console.
     */
    private final UIConsole console_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a HistoryUpAction.
     * 
     * @param UIConsole User interface console.
     */
    public HistoryUpAction(UIConsole console)
    {
        console_ = console;
    }
    
    
    /**
     * @see javax.swing.AbstractAction#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0)
    {
        List history = console_.getTextConsole().getHistory();

        // new history list
        if (history.size() != previousHistorySize_)
        {
            previousHistorySize_ = history.size();
            list_ = new ArrayList(history);
            java.util.Collections.reverse(list_);
            iterator_ = list_.iterator();
        }

        if (iterator_.hasNext())
        {
            String command = iterator_.next().toString();
            PrintStream ps = console_.getTextConsole().getPrintStream();
            ps.println();
            ps.print(console_.getTextConsole().getPrompt());
            ps.flush();
            console_.getConsoleArea().replaceSelection(command);
        }
        else
        {
            iterator_ = list_.iterator();
        }
    }
}