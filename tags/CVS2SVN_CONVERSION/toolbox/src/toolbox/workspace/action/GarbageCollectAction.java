package toolbox.workspace.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import toolbox.util.ElapsedTime;
import toolbox.workspace.PluginWorkspace;

/**
 * Triggers garbage collection and displays before and after stats on the
 * status bar.
 */
public class GarbageCollectAction extends BaseAction
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a GarbageCollectAction.
     * 
     * @param workspace Plugin workspace.
     */
    public GarbageCollectAction(PluginWorkspace workspace)
    {
        super(workspace, "Run GC");
        putValue(Action.MNEMONIC_KEY, new Integer('G'));
    }

    //--------------------------------------------------------------------------
    // SmartAction Abstract Class
    //--------------------------------------------------------------------------
    
    /**
     * Delegates to <code>runGC()</code>.
     * 
     * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        runGC();
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Runs garbage collection.
     */
    public void runGC()
    {
        long freeMem  = Runtime.getRuntime().freeMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        long maxMem   = Runtime.getRuntime().maxMemory();
        long beforeUsedMem  = (totalMem - freeMem) / 1000;

        ElapsedTime time = new ElapsedTime();
        System.gc();
        time.setEndTime();

        freeMem  = Runtime.getRuntime().freeMemory();
        totalMem = Runtime.getRuntime().totalMemory();
        maxMem   = Runtime.getRuntime().maxMemory();
        long afterUsedMem  = (totalMem - freeMem) / 1000;

        getWorkspace().getStatusBar().setInfo("" +
            "<html>" + "<font color='black'>" +
              "Finished GC in " + time + ".   " +
              "Used Before: " + beforeUsedMem + "K   " +
              "After: "     + afterUsedMem  + "K   " +
              "Freed:<b>"   + (beforeUsedMem - afterUsedMem) + "K</b>   " +
              "Total:     " + totalMem / 1000 + "K   " +
              "Max: "       + maxMem / 1000   + "K   " +
              "</font>" +
            "</html>");
    }
}