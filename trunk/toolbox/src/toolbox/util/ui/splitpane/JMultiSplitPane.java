package toolbox.util.ui.splitpane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;

import toolbox.util.ui.event.DebugComponentListener;
import toolbox.util.ui.event.DebugContainerListener;
import toolbox.util.ui.event.DebugPropertyChangeListener;

/**
 * A splitpane that supports any number of panes.
 */
public class JMultiSplitPane extends JPanel
{
    private static final Logger logger_ =
        Logger.getLogger(JMultiSplitPane.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * List of panes.
     */
    private List splitPanes_;

    /**
     * Listeners for location changed.
     */
    private Map fireLocationChangedListeners_;

    /**
     * Listeners for divider location changed.
     */
    private List dividerLocationListeners_;

    /**
     * Holds value of property orientation.
     */
    private SplitOrientation splitOrientation_;

    private boolean distributeOnVisible_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JMultiSplitPane with 2 panes.
     */
    public JMultiSplitPane()
    {
        this(2);
    }


    /**
     * Creates a JMultiSplitPane.
     *
     * @param initNumber Number of panes.
     */
    public JMultiSplitPane(int initNumber)
    {
        this(initNumber, SplitOrientation.HORIZONTAL);
    }


    /**
     * Creates a JMultiSplitPane.
     *
     * @param splitOrientation SplitOrientation.HORIZONTAL or VERTICAL.
     */
    public JMultiSplitPane(SplitOrientation splitOrientation)
    {
        this(2, splitOrientation);
    }


    /**
     * Creates a JMultiSplitPane.
     *
     * @param initNumber Number of panes.
     * @param splitOrientation SplitOrientation.HORIZONTAL or VERTICAL.
     */
    public JMultiSplitPane(int initNumber, SplitOrientation splitOrientation)
    {
        addComponentListener(new DebugComponentListener());
        addContainerListener(new DebugContainerListener());
        addPropertyChangeListener(new DebugPropertyChangeListener());
        addComponentListener(new MyComponentListener());

        splitPanes_ = new ArrayList();
        fireLocationChangedListeners_ = new HashMap();
        dividerLocationListeners_ = new ArrayList();

        initComponents();

        // at least 2 panes (= 1 SplitPane)
        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.0);
        splitPane.setOpaque(false);

        //splitPane.setDividerLocation(0.5);
        add(BorderLayout.CENTER, splitPane);
        splitPanes_.add(splitPane);
        registerFireLocationChangedListeners();
        setSplitOrientation(splitOrientation);

        for (int i = 2; i < initNumber; i++)
            splitLastPane();
    }

    //--------------------------------------------------------------------------
    // MyComponentListener
    //--------------------------------------------------------------------------

    class MyComponentListener extends ComponentAdapter
    {
        /**
         * @see java.awt.event.ComponentAdapter#componentShown(
         *      java.awt.event.ComponentEvent)
         */
        public void componentResized(ComponentEvent e)
        {
            if (distributeOnVisible_)
            {
                logger_.debug("Component shown");
                distributeEvenly();
                distributeOnVisible_ = false;
            }
        }
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Distributes the splitpanes evenly across the available space.
     */
    public void distributeEvenly()
    {
        int pixels = 0;

        if (getSplitOrientation() == SplitOrientation.HORIZONTAL)
            pixels = getWidth();
        else
            pixels = getHeight();

        logger_.debug("Pixels = " + pixels);

        if (pixels > 0)
        {
            pixels = pixels/3;

            logger_.debug("Distributing evenly with pixels = " + pixels);

            for (int i = 0; i < getPanesCount() - 1; i++)
                setDividerLocation(i, pixels);
        }
        else
        {
            logger_.debug("distributeonvisible = true");
            distributeOnVisible_ = true;
        }

        /*
        else
        {
            logger_.debug("Cannot distribute evenly...pixels is zero");

            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    distributeEvenly();
                }
            });

        }
        */
    }


    public void splitLastPane()
    {
        splitPane(splitPanes_.size(), false);
    }


    public void splitPane(int index, boolean shiftComponent)
    {
        JSplitPane pane =
            new JSplitPane(getSplitOrientation().getSplitPaneConstant());

        pane.setResizeWeight(1);


        //pane.setOpaque(false);
        //pane.setDividerLocation(0.5);
        //    pane.setBorder(null);


        JSplitPane splitPane;

        if (index == splitPanes_.size())
        {
            splitPane = (JSplitPane) splitPanes_.get(index - 1);
            Component comp = splitPane.getRightComponent();
            if (shiftComponent)
            {
                pane.setRightComponent(comp);
            }
            else
            {
                pane.setLeftComponent(comp);
            }

            splitPanes_.add(index, pane);
        }
        else
        {
            splitPane = (JSplitPane) splitPanes_.get(index);
            Component comp = splitPane.getRightComponent();
            pane.setRightComponent(comp);

            if (shiftComponent)
            {
                pane.setLeftComponent(splitPane.getLeftComponent());
                splitPane.setLeftComponent(null);
            }

            splitPanes_.add(index + 1, pane);
        }

        splitPane.setRightComponent(pane);
        registerFireLocationChangedListeners();
    }


    public void setComponent(int index, Component component)
    {
        if (index < splitPanes_.size())
        {
            ((JSplitPane) splitPanes_.get(index)).setLeftComponent(component);
        }
        else
        {
            ((JSplitPane)
                splitPanes_.get(index - 1)).setRightComponent(component);
        }

        //repaint();
    }


    public Component getPane(int index)
    {
        Component component;

        if (index < splitPanes_.size())
        {
            component = ((JSplitPane)
                splitPanes_.get(index)).getLeftComponent();
        }
        else
        {
            component = ((JSplitPane)
                splitPanes_.get(index - 1)).getRightComponent();
        }
        return component;
    }


    public int getPanesCount()
    {
        return splitPanes_.size() + 1;
    }


    public int getDividerLocation(int index)
    {
        return ((JSplitPane) splitPanes_.get(index)).getDividerLocation();
    }


    public void setDividerLocation(int index, int location)
    {
        ((JSplitPane) splitPanes_.get(index)).setDividerLocation(location);
        fireLocationChanged(index, location);
    }


    public int getPreferredSize(int index)
    {
        return getSplitOrientation().getPreferredSize(getPane(index));
    }


    public int getSize(int index)
    {
        return getSplitOrientation().getSize(getPane(index));
    }


    /**
     * This method is called from within the constructor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()
    {
        setLayout(new BorderLayout());
    }


    /**
     * Getter for property orientation.
     *
     * @return Value of property orientation.
     */
    public SplitOrientation getSplitOrientation()
    {
        return splitOrientation_;
    }


    /**
     * Setter for property orientation.
     *
     * @param splitOrientation New value of property orientation.
     */
    public void setSplitOrientation(SplitOrientation splitOrientation)
    {
        splitOrientation_ = splitOrientation;

        for (int i = 0; i < splitPanes_.size(); i++)
        {
            ((JSplitPane)
                splitPanes_.get(i))
                    .setOrientation(splitOrientation.getSplitPaneConstant());
        }
    }


    public void addDividerLocationListener(DividerListener listener)
    {
        dividerLocationListeners_.add(listener);
    }


    public void removeDividerLocationListener(DividerListener listener)
    {
        dividerLocationListeners_.remove(listener);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    protected void fireLocationChanged(int index, int location)
    {
        for (int i = 0; i < dividerLocationListeners_.size(); i++)
            ((DividerListener)
                dividerLocationListeners_.get(i)).locationChanged(
                    this, index, location);
    }


    protected void registerFireLocationChangedListeners()
    {
        for (int i = 0; i < splitPanes_.size(); i++)
        {
            JSplitPane splitPane = (JSplitPane) splitPanes_.get(i);

            if (fireLocationChangedListeners_.containsKey(splitPane))
            {
                FireLocationChangedListener listener =
                    (FireLocationChangedListener)
                        fireLocationChangedListeners_.get(splitPane);

                if (listener.getIndex() != i)
                    listener.setIndex(i);
            }
            else
            {
                FireLocationChangedListener listener =
                    new FireLocationChangedListener(i);

                splitPane.addPropertyChangeListener(
                    "dividerLocation", listener);

                fireLocationChangedListeners_.put(splitPane, listener);
            }
        }
    }


    protected void unregisterFireLocationChangedListener(JSplitPane splitPane)
    {
        if (fireLocationChangedListeners_.containsKey(splitPane))
        {
            FireLocationChangedListener listener =
                (FireLocationChangedListener)
                    fireLocationChangedListeners_.get(splitPane);

            splitPane.removePropertyChangeListener("dividerLocation", listener);
            fireLocationChangedListeners_.remove(splitPane);
        }
    }

    //--------------------------------------------------------------------------
    // FireLocationChangedListener
    //--------------------------------------------------------------------------

    class FireLocationChangedListener implements PropertyChangeListener
    {

        /**
         * Holds value of property index.
         */
        private int index_;


        public FireLocationChangedListener(int index)
        {
            setIndex(index);
        }


        /**
         * Getter for property index.
         *
         * @return Value of property index.
         */
        public int getIndex()
        {
            return index_;
        }


        public void propertyChange(PropertyChangeEvent evt)
        {
            fireLocationChanged(
                getIndex(),
                ((Integer) evt.getNewValue()).intValue());
        }


        /**
         * Setter for property index.
         *
         * @param index New value of property index.
         */
        public void setIndex(int index)
        {
            index_ = index;
        }

    }
}

/*
 * Sun Public License Notice The contents of this file are subject to the Sun
 * Public License Version 1.0 (the "License"); you may not use this file except
 * in compliance with the License. A copy of the License is available at
 * http://www.sun.com/ The Original Code is SoftSmithy Utility Library. The
 * Initial Developer of the Original Code is Florian Brunner (Sourceforge.net
 * user: puce). All Rights Reserved. Contributor(s): .
 */