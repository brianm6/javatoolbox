package toolbox.workspace.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import toolbox.util.ElapsedTime;
import toolbox.workspace.PluginWorkspace;
import toolbox.workspace.WorkspaceAction;

/**
 * Triggers garbage collection.
 */
public class GarbageCollectAction extends WorkspaceAction
{
    private final PluginWorkspace workspace_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a GarbageCollectAction.
     */
    public GarbageCollectAction(PluginWorkspace workspace)
    {
        super("Run GC", false, null, null);
        this.workspace_ = workspace;
        putValue(Action.MNEMONIC_KEY, new Integer('G'));
    }

    //--------------------------------------------------------------------------
    // SmartAction Abstract Class
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.SmartAction#runAction(
     *      java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
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

        workspace_.getStatusBar().setInfo("" +
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