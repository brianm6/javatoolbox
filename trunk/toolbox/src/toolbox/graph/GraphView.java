package toolbox.graph;

import javax.swing.JComponent;

/**
 * A GraphView is responsible for rendering a Graph to a view.
 */
public interface GraphView
{
    /**
     * Returns the component to with the Graph has been rendered.
     * 
     * @return JComponent
     */
    JComponent getComponent();
    
    
    /**
     * Sets the layout used to render the Graph on the GraphView.
     * 
     * @param layout Layout to use.
     */
    void setLayout(Layout layout);
}