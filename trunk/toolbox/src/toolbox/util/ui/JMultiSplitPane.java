package toolbox.util.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import toolbox.util.Assert;

/**
 * A panel which contains multiple JComponents, held apart by JSplitPanes.
 */
public class JMultiSplitPane extends JPanel
{
    private static final Logger logger_ =
        Logger.getLogger(JMultiSplitPane.class);

    private int dividerSize_;
    private int orientation_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
        
    /**
     * Create a multiplitpane with the given orientation and dividerSize.
     * 
     * @param orientation JSplitPane.[HORIZONTAL|VERTICAL]_SPLIT
     * @param dividerSize Size of the splitpane dividers
     */
    public JMultiSplitPane(int orientation, int dividerSize)
    {
        setLayout(new GridLayout());
        
        if (orientation != JSplitPane.HORIZONTAL_SPLIT  && 
            orientation != JSplitPane.VERTICAL_SPLIT)
        {
            throw new IllegalArgumentException(
                "Orientation must be one of "  + 
                "JSplitPane.HORIZONTAL_SPLIT or " + 
                "JSplitPane.VERTICAL_SPLIT");
        }

        orientation_ = orientation;
        dividerSize_ = dividerSize;
    }

    //--------------------------------------------------------------------------
    // Overridden from java.awt.Container
    //--------------------------------------------------------------------------
    
    /** 
     * Adds a component to the multisplitpane. If I'm empty, simply add it, 
     * otherwise push a JSplitPane on top, taking the current contents and 
     * placing it as top/left component and adding comp as bottom/right 
     * component. The adding of the component will be deferred using 
     * {@link javax.swing.SwingUtilities#invokeLater}.
     *
     * @param comp Component to add
     */
    public void add(JComponent comp)
    {
        SwingUtilities.invokeLater(new AddComponent(comp));
    }


    /**
     * Removes a component from the multisplitpane. Removal is be deferred 
     * using {@link javax.swing.SwingUtilities#invokeLater}.
     *
     * @param comp Component to remove
     */
    public void remove(JComponent comp)
    {
        SwingUtilities.invokeLater(new RemoveComponent(comp));
    }


    /**
     * Components of the multisplitpane.
     * 
     * @return Contents of the multisplitpane. The order is the order by
     *         which they should be added in order to get the same ordering.
     */
    public Component[] getComponents()
    {
        Vector vec = new java.util.Vector();
        
        if (getComponentCount() > 0)
            doGetComponents(vec, getComponent(0));
        
        Component[] carr = new Component[vec.size()];
        Enumeration e = vec.elements();
        int i = 0;
        
        while (e.hasMoreElements())
            carr[i++] = (java.awt.Component) e.nextElement();
        
        return carr;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Sets the split pane divider size.
     *
     * @param size New size for all dividers.
     */
    public void setDividerSize(int size)
    {
        logger_.debug("setSplitWidth " + size);
     
        dividerSize_ = size;
        
        // run through contents and set the splitpane separators width
        if (getComponentCount() > 0)
            doSetSplitWidth(getComponent(0));
        
        revalidate();
    }


    /**
     * Returns the size of the split pane divider.
     * 
     * @return int
     */
    public int getDividerSize()
    {
        return dividerSize_;
    }


    /** 
     * Returns the splitpane orientation.
     * 
     * @return int
     */
    public int getOrientation() 
    {
        return orientation_;
    }
    
    
    /**
     * Returns the array of divider locations.
     * 
     * @return int[]
     */
    public int[] getDividerLocations()
    {
        IntVector ivec = new IntVector();
        
        if (getComponentCount() == 1)
            doGetDividerLocations(ivec, getComponent(0));
        
        return ivec.getArray();
    }


    /**
     * Set the divider locations.
     * 
     * @param locs  Array of divider locations
     */
    public void setDividerLocations(final int[] locs)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                if (getComponentCount() > 0)
                    doSetDividerLocations(locs, 0, getComponent(0));
            }
        });
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Gets divider locations.
     * 
     * @param ivec Vector of divider locations (populated on exit)
     * @param splitpane JSplitPane
     */
    private void doGetDividerLocations(IntVector ivec, Component splitpane)
    {
        if (splitpane instanceof JSplitPane)
        {
            JSplitPane jsp = (JSplitPane) splitpane;
            ivec.add(jsp.getDividerLocation());
            doGetDividerLocations(ivec, jsp.getTopComponent());
            doGetDividerLocations(ivec, jsp.getBottomComponent());
        }
    }


    /**
     * Sets divider locations.
     * 
     * @param locs Divider locations to set
     * @param index Index
     * @param splitpane JSplitPane
     * @return Index
     */
    private int doSetDividerLocations(int[] locs, int index, 
        Component splitpane)
    {
        if (splitpane instanceof JSplitPane)
        {
            JSplitPane jsp = (JSplitPane) splitpane;
            
            // make sure the index is ok
            if (index < locs.length)
            {
                jsp.setDividerLocation(locs[index++]);
                
                index = doSetDividerLocations(
                            locs, index, jsp.getTopComponent());
                    
                index = doSetDividerLocations(
                            locs, index, jsp.getBottomComponent());
            }
        }
        
        return index;
    }


    /**
     * Gets the components.
     * 
     * @param vec Vector
     * @param splitpane JSplitPane
     */
    private void doGetComponents(Vector vec, Component splitpane)
    {
        if (splitpane instanceof JSplitPane)
        {
            JSplitPane jsp = (JSplitPane) splitpane;
            doGetComponents(vec, jsp.getTopComponent());
            doGetComponents(vec, jsp.getBottomComponent());
        }
        else
        {
            vec.addElement(splitpane);
        }
    }


    /**
     * Sets the split width.
     * 
     * @param splitpane JSplitPane
     */
    private void doSetSplitWidth(Component splitpane)
    {
        if (splitpane instanceof JSplitPane)
        {
            JSplitPane jsp = (JSplitPane) splitpane;
            jsp.setDividerSize(dividerSize_);
            doSetSplitWidth(jsp.getTopComponent());
            doSetSplitWidth(jsp.getBottomComponent());
        }
    }


    /**
     * Adds a component.
     * 
     * @param comp Component to add
     */
    private void addComponent(JComponent comp)
    {
        logger_.debug("addComponent " + comp);

        int n = getComponentCount();
        
        if (n == 0)
        {
            // just add it
            super.add(comp);
        }
        else
        {
            Assert.equals(1, n, "Should be exactly one component in " + this);
            
            // get and remove the current component
            Component oldcomp = getComponent(0);
            remove(oldcomp);
            
            // create a splitpane and add the original to the top/left
            // and the new to the bot/right
            JSplitPane split = new JSplitPane(orientation_);
            split.setTopComponent(oldcomp);
            split.setBottomComponent(comp);
            split.setBorder(BorderFactory.createEmptyBorder());
            split.setDividerSize(dividerSize_);
            super.add(split);
        }
    }


    /**
     * Remove a component.
     * 
     * @param comp Component to remove
     */
    private void removeComponent(JComponent comp)
    {
        logger_.debug("subComponent " + comp);
            
        Container cont = comp.getParent();
        
        if (cont instanceof JPanel)
        {
            // topmost
            super.remove(comp);
        }
        else
        {
            Assert.isTrue(
                cont instanceof JSplitPane,"container not a splitpane!");
            
            JSplitPane split = (JSplitPane) cont;
            Container parent = split.getParent();
            
            // remove the splitpane from its parent
            if (split.getTopComponent() == comp)
            {
                // replace split with its bottom component
                replaceInParent(parent, split, split.getBottomComponent());
            }
            else
            {
                // bottom part to go; add back top part
                replaceInParent(parent, split, split.getTopComponent());
            }
        }
    }


    /**
     * Replaces a component in its parent.
     * 
     * @param parent Parent container
     * @param split Splitpane
     * @param comp Component to replace
     */
    private void replaceInParent(Container parent, JSplitPane split, 
        Component comp)
    {
        logger_.debug("replaceInParent " + parent + ", " + split + ", " + comp);

        if (parent instanceof JPanel)
        {
            super.remove(split);
            super.add(comp);
        }
        else
        {
            Assert.isTrue(
                parent instanceof JSplitPane, "parent not a JSplitPane!");
            
            JSplitPane splitParent = (JSplitPane) parent;
            
            if (splitParent.getTopComponent() == split)
                splitParent.setTopComponent(comp);
            else
                splitParent.setBottomComponent(comp);
        }
    }

    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------

    /**
     * Adds a component to the multisplit pane.
     */
    private class AddComponent implements Runnable
    {
        private JComponent comp_;
                
        AddComponent(JComponent comp)
        {
            comp_ = comp;
        }
        
        public void run()
        {
            addComponent(comp_);
            revalidate();
        }
    }
    
    /**
     * Removes a component from the multisplitpane.
     */
    private class RemoveComponent implements Runnable
    {
        private JComponent comp_;
                
        RemoveComponent(JComponent comp)
        {
            comp_ = comp;
        }
        
        public void run()
        {
            removeComponent(comp_);
            revalidate();
        }
    }

    /**
     * A simple dynamic vector of ints.
     */
    public class IntVector
    {
        private static final int STARTING_SIZE = 8;

        private int start_;     // first valid pos
        private int end_;       // one beyond last valid pos
        private int[] arr_;     // data

        /**
         * Default constructor
         */
        public IntVector()
        {
        }

        /**
         * Arg constructor
         * 
         * @param initialSize Initial size
         */
        public IntVector(int initialSize)
        {
            arr_ = new int[initialSize];
        }

        /** 
         * Add an int last 
         * 
         * @param v int
         */
        public void add(int v)
        {
            if (arr_ == null)
            {
                arr_ = new int[STARTING_SIZE];
            }
            
            if (end_ >= arr_.length)
            {
                if (start_ > 0)
                {
                    int n = size();
                    System.arraycopy(arr_, start_, arr_, 0, n);
                    start_ = 0;
                    end_ = n;
                }
                else
                {
                    int[] tmp = new int[arr_.length * 2];
                    System.arraycopy(arr_, 0, tmp, 0, arr_.length);
                    arr_ = tmp;
                }
            }
            
            arr_[end_++] = v;
        }

        /**
         * @return int at the front
         */
        public int front()
        {
            if (start_ >= end_)
                throw new ArrayIndexOutOfBoundsException();
                
            return arr_[start_];
        }

        /**
         * @return int popped from the front
         */
        public int popFront()
        {
            if (start_ >= end_)
                throw new ArrayIndexOutOfBoundsException();
                
            return arr_[start_++];
        }

        /**
         * @return Last int in array
         */
        public int back()
        {
            if (start_ >= end_)
                throw new ArrayIndexOutOfBoundsException();
                
            return arr_[end_ - 1];
        }

        /**
         * @return Last element 
         */        
        public int popBack()
        {
            if (start_ >= end_)
                throw new ArrayIndexOutOfBoundsException();
                
            return arr_[--end_];
        }

        /**
         * @return Array representation
         */
        public int[] getArray()
        {
            int[] res = new int[size()];
            
            if (arr_ != null)
            {
                System.arraycopy(arr_, start_, res, 0, size());
            }
            
            return res;
        }

        /**
         * @return Size of vector
         */
        public int size()
        {
            return end_ - start_;
        }

        /**
         * Resets vector
         */
        public void clean()
        {
            start_ = end_ = 0;
        }
    }

    //--------------------------------------------------------------------------
    // Entry Point
    //--------------------------------------------------------------------------

    private static int serial = 0;
    private static int splitWidth = 10;
    
    /**
     * Entrypoint
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        JFrame f = new JFrame("MultiSplitPane Test");
        f.getContentPane().setLayout(new GridLayout());
        
        final JMultiSplitPane msp =
            new JMultiSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitWidth);
            
        f.getContentPane().add(msp);

        JPanel butPanel = new JPanel();
        f.getContentPane().add(butPanel);

        JButton butAdd = new JButton("add");
        butPanel.add(butAdd);
        
        butAdd.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ect)
            {
                JButton but = new JButton("remove" + (serial++));
                msp.add(but);
                msp.revalidate();
                
                but.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent evt)
                    {
                        msp.remove((JComponent) evt.getSource());
                    }
                });
            }
        });
        
        JButton butThin = new JButton("thin");
        
        butThin.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                msp.setDividerSize(--splitWidth);
            }
        });
        
        butPanel.add(butThin);

        JButton butThick = new JButton("thick");
        
        butThick.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                msp.setDividerSize(++splitWidth);
            }
        });
        
        butPanel.add(butThick);
        f.pack();
        f.show();
    }
}