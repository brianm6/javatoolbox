package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import toolbox.plugin.jdbc.DBBenchmark;
import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.ui.ImageCache;

/**
 * Runs the jdbc benchmark.
 */
public class BenchmarkAction extends BaseAction 
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Reference to master reference in QueryPlugin.
     */
    private DBBenchmark benchmark_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a BenchmarkAction.
     * 
     * @param plugin Query plugin.
     * @param benchmark Database benchmark to run.
     */
    public BenchmarkAction(QueryPlugin plugin, DBBenchmark benchmark) 
    {
        super(plugin, "Run benchmark", true, null, plugin.getStatusBar());
        putValue(SMALL_ICON, ImageCache.getIcon(ImageCache.IMAGE_DUKE));
        putValue(Action.NAME, "");
        putValue(SHORT_DESCRIPTION, "Runs JDBC Benchmark");
        benchmark_ = benchmark;
    }
    
    //--------------------------------------------------------------------------
    // SmartAction Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.SmartAction#runAction(
     *      java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        benchmark_.start();
        benchmark_.stop();
    }
}