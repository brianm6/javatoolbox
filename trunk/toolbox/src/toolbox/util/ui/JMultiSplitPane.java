package toolbox.util.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
 *
 * @author matso
 **/
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
     * orientation is one of JSplitPane.VERTICAL_SPLIT or
     * JSplitPane.HORIZONTAL_SPLIT
     * 
     * @param orientation top/bottom or left/right split
     * @param dividerSize size of split-pane dividers.
     */
    public JMultiSplitPane(int orientation, int dividerSize)
    {
        setLayout(new GridLayout());
        
        if (orientation != JSplitPane.HORIZONTAL_SPLIT  && 
            orientation != JSplitPane.VERTICAL_SPLIT)
        {
            throw new IllegalArgumentException(
                "orientation must be one of "  + 
                "JSplitPane.HORIZONTAL_SPLIT or " + 
                "JSplitPane.VERTICAL_SPLIT");
        };

        orientation_ = orientation;
        dividerSize_ = dividerSize;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /** 
     * Add comp last. If I'm empty, simply add it, otherwise push a
     * JSplitPane on top, taking the current contents and placing it
     * as top/left component and adding comp as bottom/right component.
     * <p>
     * The component adding will be deferred using
     * {@link javax.swing.SwingUtilities#invokeLater}.
     *
     * @param comp component to add.
     */
    public void add(JComponent comp)
    {
        SwingUtilities.invokeLater(new AddComponent(comp));
    }

    /**
     * Remove given component. 
     * Removal will be deferred using
     * {@link javax.swing.SwingUtilities#invokeLater}.
     *
     * @param comp component to add.
     */
    public void remove(JComponent comp)
    {
        SwingUtilities.invokeLater(new SubComponent(comp));
    }

    /**
     * Set the divider size of all JSplitPane to sz.
     *
     * @param sz new size for all dividers.
     */
    public void setDividerSize(int sz)
    {
        logger_.debug("setSplitWidth " + sz);
     
        dividerSize_ = sz;
        
        // run through contents and set the splitpane separators width
        if (getComponentCount() > 0)
        {
            doSetSplitWidth(getComponent(0));
        }
        
        revalidate();
    }

    /**
     * Return current size of JSplitPane dividers.
     * 
     * @return size of dividers.
     */
    public int getDividerSize()
    {
        return dividerSize_;
    }

    /** 
     * Returns orientation, either JSplitPane.HORIZONTAL_SPLIT or
     * JSplitPane.VERTICAL_SPLIT.
     *
     * @return oriontation
     */
    public int getOrientation() 
    {
        return orientation_;
    }
    
    /**
     * Return the current dividerlocations. 
     */
    public int[] getDividerLocations()
    {
        IntVector ivec = new IntVector();
        
        if (getComponentCount() == 1)
        {
            doGetDividerLocations(ivec, getComponent(0));
        }
        return ivec.getArray();
    }

    private void doGetDividerLocations(IntVector ivec,
        Component comp)
    {
        if (comp instanceof JSplitPane)
        {
            JSplitPane jsp = (JSplitPane) comp;
            ivec.add(jsp.getDividerLocation());
            doGetDividerLocations(ivec, jsp.getTopComponent());
            doGetDividerLocations(ivec, jsp.getBottomComponent());
        }
    }

    /**
     * Set dividerlocations. 
     */
    public void setDividerLocations(final int[] locs)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                if (getComponentCount() > 0)
                {
                    doSetDividerLocations(locs, 0, getComponent(0));
                }
            }
        });
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    private int doSetDividerLocations(int[] locs, int index, Component comp)
    {
        if (comp instanceof JSplitPane)
        {
            JSplitPane jsp = (JSplitPane) comp;
            
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
     * Return the contents of the MSP. The order is the order by
     * which they should be added in order to get the same ordering.
     */
    public Component[] getComponents()
    {
        Vector vec = new java.util.Vector();
        
        if (getComponentCount() > 0)
        {
            doGetComponents(vec, getComponent(0));
        }
        
        Component[] carr = new Component[vec.size()];
        Enumeration e = vec.elements();
        int i = 0;
        
        while (e.hasMoreElements())
        {
            carr[i++] = (java.awt.Component) e.nextElement();
        }
        
        return carr;
    }

    private void doGetComponents(Vector vec, Component comp)
    {
        if (comp instanceof JSplitPane)
        {
            JSplitPane jsp = (JSplitPane) comp;
            doGetComponents(vec, jsp.getTopComponent());
            doGetComponents(vec, jsp.getBottomComponent());
        }
        else
        {
            vec.addElement(comp);
        }
    }

    private void doSetSplitWidth(Component comp)
    {
        if (comp instanceof JSplitPane)
        {
            JSplitPane jsp = (JSplitPane) comp;
            jsp.setDividerSize(dividerSize_);
            doSetSplitWidth(jsp.getTopComponent());
            doSetSplitWidth(jsp.getBottomComponent());
        }
    }

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

    // subtract a component. Bit more tricky than add, as we have to
    // find the component first
    private void subComponent(JComponent comp)
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
            Assert.isTrue(cont instanceof JSplitPane,"container not a splitpane!");
            
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
            Assert.isTrue(parent instanceof JSplitPane,"parent not a JSplitPane!");
            
            JSplitPane split_parent = (JSplitPane) parent;
            
            if (split_parent.getTopComponent() == split)
            {
                split_parent.setTopComponent(comp);
            }
            else
            {
                split_parent.setBottomComponent(comp);
            }
        }
    }

    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------

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
    
    private class SubComponent implements Runnable
    {
        private JComponent comp_;
                
        SubComponent(JComponent comp)
        {
            comp_ = comp;
        }
        
        public void run()
        {
            subComponent(comp_);
            revalidate();
        }
    }


    /**
     * A simple dynamic vector of ints.
     * 
     * @author matso
     **/
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
         */
        public IntVector(int start_size)
        {
            arr_ = new int[start_size];
        }

        /** 
         * Add an int last 
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
                    int[] tmp_arr = new int[arr_.length * 2];
                    System.arraycopy(arr_, 0, tmp_arr, 0, arr_.length);
                    arr_ = tmp_arr;
                }
            }
            
            arr_[end_++] = v;
        }

        public int front()
        {
            if (start_ >= end_)
                throw new ArrayIndexOutOfBoundsException();
                
            return arr_[start_];
        }

        public int popFront()
        {
            if (start_ >= end_)
                throw new ArrayIndexOutOfBoundsException();
                
            return arr_[start_++];
        }

        public int back()
        {
            if (start_ >= end_)
                throw new ArrayIndexOutOfBoundsException();
                
            return arr_[end_ - 1];
        }
        
        public int popBack()
        {
            if (start_ >= end_)
                throw new ArrayIndexOutOfBoundsException();
                
            return arr_[--end_];
        }

        public int[] getArray()
        {
            int[] res = new int[size()];
            
            if (arr_ != null)
            {
                System.arraycopy(arr_, start_, res, 0, size());
            }
            
            return res;
        }

        public int size()
        {
            return end_ - start_;
        }

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
    
    public static void main(String[] args) throws IOException
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

/**
 * CSGC - Client-Server Game Core
 * Copyright (C) 1998 Mats Olsson
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * 
 * matso@dtek.chalmers.se
 */
