package toolbox.graph;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;

import toolbox.util.ui.JSmartComboBox;
import toolbox.util.ui.JSmartLabel;

/**
 * GraphConfigurator is responsible for ___.
 */
public class GraphConfigurator extends JPanel
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private JSmartComboBox graphLibComboBox_;
    
    private JSmartComboBox layoutComboBox_;
    
    private List listeners_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a GraphConfigurator.
     */
    public GraphConfigurator()
    {
        buildView();
        wireView();
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Adds a listener to this GraphConfigurator.
     */
    public void addListener(GraphConfigurator.Listener listener)
    {
        listeners_.add(listener);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    protected void buildView()
    {
        setLayout(new FlowLayout());
        graphLibComboBox_ = new JSmartComboBox();
        layoutComboBox_ = new JSmartComboBox();
        
        add(new JSmartLabel("Graph Library"));
        add(graphLibComboBox_);
        
        add(new JSmartLabel("Layout"));
        add(layoutComboBox_);
        
        populateGraphLib();
    }
    
    
    protected void wireView()
    {
        listeners_ = new ArrayList();
        
        graphLibComboBox_.addActionListener(
            new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    for (Iterator i = listeners_.iterator(); i.hasNext();)
                    {
                        Listener listener = (Listener) i.next();
                        listener.graphLibChanged((GraphLib)
                            graphLibComboBox_.getSelectedItem());
                    }
                }
            });
        
        layoutComboBox_.addActionListener(
            new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    for (Iterator i = listeners_.iterator(); i.hasNext();)
                    {
                        Listener listener = (Listener) i.next();
                        listener.layoutChanged((Layout) 
                            layoutComboBox_.getSelectedItem());
                    }
                }
            });
    }
    
    
    protected void populateGraphLib()
    {
        graphLibComboBox_.setModel(
            new DefaultComboBoxModel(
                GraphLibFactory.createAll().toArray()));
        
        populateLayout();
    }
    
    
    protected void populateLayout()
    {
        GraphLib graphLib = (GraphLib) graphLibComboBox_.getSelectedItem();
        Graph tmpGraph = graphLib.createGraph();
        
        layoutComboBox_.setModel(
            new DefaultComboBoxModel(
                graphLib.getLayouts(tmpGraph).toArray()));
    }
    
    //--------------------------------------------------------------------------
    // Listener Interface
    //--------------------------------------------------------------------------
    
    public interface Listener 
    {
        void graphLibChanged(GraphLib graphLib);
        void layoutChanged(Layout layout);
    }
}