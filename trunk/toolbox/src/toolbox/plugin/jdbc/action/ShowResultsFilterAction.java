package toolbox.plugin.jdbc.action;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.ui.textarea.DynamicFilterView;
import toolbox.workspace.WorkspaceAction;

/**
 * Toggles the visibility of the filter view that is attached to the botton
 * of the results text area.
 */
public class ShowResultsFilterAction extends WorkspaceAction
{
    private static final Logger logger_ = 
        Logger.getLogger(ShowResultsFilterAction.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Parent plugin.
     */
    private QueryPlugin plugin_;
    
    /**
     * Panel to add the filter too.
     */
    private JPanel resultsPanel_;
    
    /**
     * UI component that allows entry of the text to filter on.
     */
    private DynamicFilterView filterView_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ShowResultsFilterAction.
     * 
     * @param plugin Parent plugin.
     */
    public ShowResultsFilterAction(QueryPlugin plugin, JPanel resultsPanel)
    {
        super("Show results filter", false, false, null, plugin.getStatusBar());
        plugin_ = plugin;
        resultsPanel_ = resultsPanel;
    }

    //--------------------------------------------------------------------------
    // Abstract SmartAction
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        if (filterView_ == null)
            filterView_ = new DynamicFilterView(plugin_.getResultsArea());

        if (filterView_.isShowing())
            resultsPanel_.remove(filterView_);
        else
            resultsPanel_.add(BorderLayout.SOUTH, filterView_);
        
        // Component added/removed from container..needs revalidation.
        resultsPanel_.revalidate();
    }
}