package toolbox.util.ui.flippane;

/**
 * Listener interface for notifications provided by JFlipPane
 */
public interface FlipPaneListener
{
    /**
     * Called when a flippane is collapsed
     * 
     * @param  flipPane  Flip pane that was collapsed
     */
    public void collapsed(JFlipPane flipPane);
    
    /**
     * Called when a flippane is expanded
     * 
     * @param  flipPane  Flip pane that was expanded
     */
    public void expanded(JFlipPane flipPane);
}