package toolbox.dbconsole;

import java.awt.event.ActionEvent;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;


/**
 * Scrolls up through the command history.
 */
public class HistoryUpAction extends AbstractAction {

    static int previousHistorySize = -1;
    static Iterator iterator;
    static List list;
    
    private final SwingConsole console;
    
    /**
     * Creates a HistoryUpAction.
     * 
     * @param console Swing console.
     */
    public HistoryUpAction(SwingConsole console) {
        this.console = console;
    }
    
    
    /*
     * @see javax.swing.AbstractAction#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {

        List history = console.getTextConsole().getHistory();
        
        // new history list
        if (history.size() != previousHistorySize) {
            previousHistorySize = history.size();
            list = new ArrayList(history); 
            java.util.Collections.reverse(list);
            iterator = list.iterator();
        }
        
        if (iterator.hasNext()) {
            String command = iterator.next().toString();
            
            PrintStream ps = console.getTextConsole().getPrintStream();
            ps.println();
            ps.print(console.getTextConsole().getPrompt());
            ps.flush();
            
            console.getConsoleArea().replaceSelection(command);
        }
        else {
            iterator = list.iterator();
        }
    }
}