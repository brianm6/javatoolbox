package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.swing.Action;

import toolbox.plugin.jdbc.DBBenchmark;
import toolbox.plugin.jdbc.DBProfile;
import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.io.JTextAreaOutputStream;
import toolbox.util.ui.ImageCache;

/**
 * Runs the jdbc benchmark.
 */
public class BenchmarkAction extends BaseAction 
{
    /**
     * Creates a BenchmarkAction.
     * 
     * @param plugin Query plugin.
     */
    public BenchmarkAction(QueryPlugin plugin) 
    {
        super(plugin, "Run benchmark", false, null, plugin.getStatusBar());
        putValue(SMALL_ICON, ImageCache.getIcon(ImageCache.IMAGE_DUKE));
        putValue(Action.NAME, "");
        putValue(SHORT_DESCRIPTION, "Runs JDBC Benchmark");
    }
    
    
    /**
     * @see toolbox.util.ui.SmartAction#runAction(
     *      java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        DBProfile profile = getPlugin().getCurrentProfile();
        
        OutputStream os = 
            new JTextAreaOutputStream(getPlugin().getResultsArea());
        
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(os), true);
        
        DBBenchmark benchmark = new DBBenchmark(
            profile.getUrl(), 
            profile.getUsername(), 
            profile.getPassword(), 
            true,
            pw);
    }
}