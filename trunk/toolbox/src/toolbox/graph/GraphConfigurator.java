package toolbox.graph;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import toolbox.util.ui.JSmartComboBox;
import toolbox.util.ui.JSmartLabel;

/**
 * GraphConfigurator is a UI component that allows dynamic selection of the
 * graph library and any one of its supported graph layout algorithms.
 */
public class GraphConfigurator extends JPanel {

    private static final Logger logger_ = 
        Logger.getLogger(GraphConfigurator.class);

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Selector for the graph library.
     */
    private JSmartComboBox graphLibComboBox_;

    /**
     * Selector for the graph layout algorithm.
     */
    private JSmartComboBox layoutComboBox_;

    /**
     * Listeners interested in the selection of graph library or graph layout
     * algorithm.
     */
    private List listeners_;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    /**
     * Creates a GraphConfigurator.
     */
    public GraphConfigurator() {
        buildView();
        wireView();
    }

    // --------------------------------------------------------------------------
    // Public
    // --------------------------------------------------------------------------

    /**
     * Adds a listener to this GraphConfigurator.
     * 
     * listener Listener to add.
     */
    public void addListener(GraphConfigurator.Listener listener) {
        listeners_.add(listener);
    }

    public GraphLib getGraphLib() {
        return (GraphLib) graphLibComboBox_.getSelectedItem();
    }

    public Layout getGraphLayout() {
        return (Layout) layoutComboBox_.getSelectedItem();
    }

    // --------------------------------------------------------------------------
    // Protected
    // --------------------------------------------------------------------------

    /**
     * Builds the user interface.
     */
    protected void buildView() {
        setLayout(new FlowLayout());
        graphLibComboBox_ = new JSmartComboBox();
        layoutComboBox_ = new JSmartComboBox();

        add(new JSmartLabel("Graph Library"));
        add(graphLibComboBox_);

        add(new JSmartLabel("Layout"));
        add(layoutComboBox_);

        populateGraphLib();
    }


    /**
     * Wires events and listeners to the user interface.
     */
    protected void wireView() {
        listeners_ = new ArrayList();

        graphLibComboBox_.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                for (Iterator i = listeners_.iterator(); i.hasNext();) {
                    Listener listener = (Listener) i.next();
                    listener.graphLibChanged((GraphLib) graphLibComboBox_
                        .getSelectedItem());
                }
            }
        });

        layoutComboBox_.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                for (Iterator i = listeners_.iterator(); i.hasNext();) {
                    Listener listener = (Listener) i.next();
                    listener.layoutChanged((Layout) layoutComboBox_
                        .getSelectedItem());
                }
            }
        });

        addListener(new GraphConfigurator.Listener() {

            public void graphLibChanged(GraphLib graphLib) {
                logger_.debug("Graph lib changed to " + graphLib);
                populateLayout();
            }

            public void layoutChanged(Layout layout) {
            }
        });

    }


    /**
     * Populates the graph library combobox with the available graphing
     * libraries.
     */
    protected void populateGraphLib() {
        graphLibComboBox_.removeAllItems();
        graphLibComboBox_.setModel(new DefaultComboBoxModel(GraphLibFactory
            .createAll().toArray()));

        populateLayout();
    }


    /**
     * Populates the graph layout combobox with the available graph layout
     * algorithms.
     */
    protected void populateLayout() {
        layoutComboBox_.removeAllItems();
        GraphLib graphLib = (GraphLib) graphLibComboBox_.getSelectedItem();
        Graph tmpGraph = graphLib.createGraph();

        layoutComboBox_.setModel(new DefaultComboBoxModel(graphLib.getLayouts(
            tmpGraph).toArray()));
    }

    // --------------------------------------------------------------------------
    // Listener Interface
    // --------------------------------------------------------------------------

    /**
     * Exposes events generated by this UI component.
     */
    public interface Listener {

        /**
         * Provides notification that the graph library selection has changed.
         * 
         * @param graphLib Newly selected graphing library.
         */
        void graphLibChanged(GraphLib graphLib);


        /**
         * Provides notification that the graph layout algorithm has changed.
         * 
         * @param layout Newly selected graph layout algorithm.
         */
        void layoutChanged(Layout layout);
    }
}