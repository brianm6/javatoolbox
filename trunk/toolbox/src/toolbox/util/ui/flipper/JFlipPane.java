/*
 * JFlipPane.java - manages dockable windows
 *
 * Copyright (C) 2000, 2001 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package toolbox.util.ui.flipper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

import org.apache.log4j.Category;
import toolbox.util.ArrayUtil;

/**
 * JFlipPane - panel with flipper like behavior
 * 
 * @author Slava Pestov
 */
public class JFlipPane extends JPanel
{
    private static final Category logger_ =
        Category.getInstance(JFlipPane.class);
    
    
    // Positions
	public static final String TOP    = "top";
	public static final String LEFT   = "left";
	public static final String BOTTOM = "bottom";
	public static final String RIGHT  = "right";

    // Instance variables

    private boolean alternateLayout_;


    public static final int SPLITTER_WIDTH = 10;

    private String position_;
    private int dimension_;
        
    private JPanel buttonPanel_;
    private JButton closeButton_;
    private JButton popupButton_;
    private ButtonGroup buttonGroup_;
    private JToggleButton nullButton_;
    private JPopupMenu popup_;
    
    private FlipCardPanel flipCardPanel_;
    private JComponent current_;
    private Hashtable flippers_;

    private List listeners_ = new ArrayList();

    private Dimension savedSize_;
    
	/**
	 * Creates a new JFlipPane
	 */
	public JFlipPane(String position)
	{
        position_ = position;
        dimension_ = 0;
        flippers_ = new Hashtable();
                        
        buildView();
	} 

    /**
     * Builds the GUI
     */
    protected void buildView()
    {
        // Button panel
        buttonPanel_ = new JPanel(new ButtonLayout(this));
        buttonPanel_.addMouseListener(new MouseHandler());
        
        // Close button
        closeButton_ = new JButton(loadIcon("closebox.gif"));
        closeButton_.setToolTipText("Close");
        
        int left;
        if(position_.equals(JFlipPane.RIGHT) || position_.equals(JFlipPane.LEFT))
            left = 1;
        else
            left = 0;

        closeButton_.setMargin(new Insets(0,left,0,0));
        buttonPanel_.add(closeButton_);
        closeButton_.addActionListener(new ActionHandler());


        // Popup button
        popupButton_ = new JButton(loadIcon("ToolbarMenu.gif"));
        popupButton_.setRequestFocusEnabled(false);
        popupButton_.setToolTipText("Popup menu");
        popupButton_.addMouseListener(new MouseHandler());
        buttonPanel_.add(popupButton_);        
                

        // Adds buttons to mutually exclusive button group
        popup_ = new JPopupMenu();
        buttonGroup_ = new ButtonGroup();
        // JDK 1.4 workaround
        buttonGroup_.add(nullButton_ = new JToggleButton());

        // button and flipCardPanel
        flipCardPanel_ = new FlipCardPanel(this);
        
        
//        setLayout(new DockableLayout(this));
//        add(DockableLayout.LEFT_BUTTONS, buttonPanel_);
//        add(LEFT, flipCardPanel_);

        setLayout(new BorderLayout());
        add(BorderLayout.WEST, buttonPanel_);
        add(BorderLayout.CENTER, flipCardPanel_);

    }


    /**
     * Adds the given flipper to the JFlipPane
     */
    public void addFlipper(String name, JComponent flipper)
    {
        flipper.setName(name);   
           
        // Add to internal map
        flippers_.put(name, flipper);
        
        // Add to card panel
        flipCardPanel_.add(name, flipper);
        
        // Figure out rotation of text for button
        int rotation;
        if(position_.equals(JFlipPane.TOP) || position_.equals(JFlipPane.BOTTOM))
            rotation = RotatedTextIcon.NONE;
        else if(position_.equals(JFlipPane.LEFT))
            rotation = RotatedTextIcon.CCW;
        else if(position_.equals(JFlipPane.RIGHT))
            rotation = RotatedTextIcon.CW;
        else
            throw new InternalError("Invalid position: " + position_);

        // Create the button
        JToggleButton button = new JToggleButton();
        button.setMargin(new Insets(0,0,0,0));
        button.setRequestFocusEnabled(false);
        button.setIcon(new RotatedTextIcon(rotation,button.getFont(),
            flipper.getName()));
        button.setActionCommand(name);
        button.addActionListener(new ActionHandler());
        button.setName(name);

        logger_.debug("Button name before= " + button.getName() + " " + button);

        // Add to button group and button panel
        buttonGroup_.add(button);
        buttonPanel_.add(button);

        logger_.debug("Button name after= " + button.getName() + " " + button );

        // Add mouse listener
        button.addMouseListener(new MouseHandler());

        // Create menu item
        JMenuItem menuItem = new JMenuItem(name);

        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                System.out.println("FIX ME!!! menuItem actionlistener");
                //showDockableWindow(entry.getName());
            }
        }); 

        popup_.add(menuItem);

        setSelectedFlipper(flipper);
        
        revalidate();
        
        logger_.debug("Buttonfor=" + getButtonFor(flipper));
    } 


    /**
     * Removes the select flipper
     */
    public void removeFlipper(JComponent entry)
    {
        // Remove from buttons
        buttonPanel_.remove(getButtonFor(entry));
        buttonGroup_.remove(getButtonFor(entry));
        
        // Remove from card panel        
        flipCardPanel_.remove(entry);
        
        // Remove from internal table
        flippers_.remove(entry.getName());
                
               
        revalidate();
    } 


    /**
     * Sets the currently selected flipper
     */
    public void setSelectedFlipper(final JComponent entry)
    {
        if(current_ == entry)
            return;

        if(current_ == null)
        {
            // we didn't have a component previously, so create a border
            flipCardPanel_.setBorder(new FlipPaneBorder(position_));
        }

        if(entry != null)
        {
            current_ = entry;
            flipCardPanel_.showDockable(entry.getName());
            getButtonFor(entry).setSelected(true);
        }
        else
        {
            current_ = null;
            nullButton_.setSelected(true);
            // removing last component, so remove border
            flipCardPanel_.setBorder(null);
        }

        revalidate();
        flipCardPanel_.repaint();
    } 


    /**
     * Returns the buttons for the given flipper
     */
    public JToggleButton getButtonFor(JComponent flipper)
    {
        String method = "[butFor] ";
        
        logger_.debug(method + "Button count=" + buttonGroup_.getButtonCount());
        
        Enumeration e = buttonGroup_.getElements();
        
        while(e.hasMoreElements())
        {
            JComponent c = (JComponent) e.nextElement();
            if (c instanceof JToggleButton)
            {
                if (c.getName() != null)
                {
                    logger_.debug("button " + c);
                    if (c.getName().equals(flipper.getName()))
                        return (JToggleButton) c;
                }
            }
        }
        
        return null;
    }


    protected void fireFlipperExpanded()
    {
        Iterator i = listeners_.iterator();
        
        while (i.hasNext())
        {
            IFlipPaneListener l = (IFlipPaneListener)i.next();
            l.flipperExpanded(this);
        }
    }

    protected void fireFlipperCollapsed()
    {
        Iterator i = listeners_.iterator();
        
        while (i.hasNext())
        {
            IFlipPaneListener l = (IFlipPaneListener)i.next();
            l.flipperCollapsed(this);
        }
    }

    public Dimension getMinimumSize()
    {
        if (isCollapsed())
            return buttonPanel_.getMinimumSize();
        else
            return new Dimension(200, 0);
    }
    
    public Dimension getPreferredSize()
    {
        if (!isCollapsed())
        {
            if (savedSize_ == null)
            {
                int width = buttonPanel_.getPreferredSize().width +
                            flipCardPanel_.getPreferredSize().width;
                            
                int height = buttonPanel_.getPreferredSize().height + 
                            flipCardPanel_.getPreferredSize().height;
                            
                return new Dimension(width, height);
            }
            else
                return savedSize_;
        }
        else
            return buttonPanel_.getPreferredSize();
    }

//    public Dimension getMaximumSize()
//    {
//        return getPreferredSize();
//    }


    public void addFlipPaneListener(IFlipPaneListener l)
    {
        listeners_.add(l);
    }

    public void removeFlipPaneListener(IFlipPaneListener l)
    {
        listeners_.remove(l);
    }

    public boolean isFlipperSelected(JComponent entry)
    {
        return current_ == entry;
    } 


    public JComponent getCurrent()
    {
        return current_;
    } 


    void setDimension(int dimension)
    {
        if(dimension != 0)
            this.dimension_ = dimension - SPLITTER_WIDTH - 3;
    } 

    public int getDimension()
    {
        return dimension_;
    }
    
    public boolean isAlternateLayout()
    {
        return alternateLayout_;
    }
            
            
    public String getPosition()
    {
        return position_;
    }            

    public JButton getPopupButton()
    {
        return popupButton_;
    }

    public JButton getCloseButton()
    {
        return closeButton_;
    }

    protected void toggleFlipper()
    {
        if (!isCollapsed())
        {
            // Flipper is expanded so collapse it

            saveSize();
            
            logger_.debug("Minsize before: " + getMinimumSize());
            remove(flipCardPanel_);
            logger_.debug("Minsize after : " + getMinimumSize());
            
            fireFlipperCollapsed();
        }
        else
        {
            // Flipper is collapsed so expand it

            logger_.debug("Minsize before: " + getMinimumSize());            
            add(BorderLayout.CENTER, flipCardPanel_);
            logger_.debug("MinSize after : " + getMinimumSize());
            
            fireFlipperExpanded();
        }
    }
    
    protected void saveSize()
    {
        savedSize_ = getSize();
    }
    
    protected boolean isCollapsed()
    {
        return !ArrayUtil.contains(getComponents(), flipCardPanel_);
    }
    
    
    //
    // Inner classes
    //
    
    class ActionHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            if(evt.getSource() == closeButton_)
                setSelectedFlipper(null);
            else
            {
                JComponent button = (JComponent)evt.getSource();
                String name = button.getName();
                logger_.debug("Flipper " + name + " selected");
                JComponent flipper = (JComponent)flippers_.get(name);
                
                if (isFlipperSelected(flipper))
                {
                    logger_.debug("Toggeling flipper");
                    toggleFlipper();
                    
                    // Deselect button if the flipper was collapsed
                    if(isCollapsed())
                    {
                        nullButton_.setSelected(true);
                    }
                }
                else 
                {
                    logger_.debug("Selecting flipper");
                    
                    if (isCollapsed())
                        toggleFlipper();
                        
                    setSelectedFlipper(flipper);
                }
                
                revalidate();
                repaint();
            }
        }
    } 

    // MouseHandler class
    class MouseHandler extends MouseAdapter
    {
        public void mousePressed(MouseEvent evt)
        {
            if(evt.getSource() == popupButton_
                || isPopupTrigger(evt))
            {
                if(popup_.isVisible())
                    popup_.setVisible(false);
                else
                {
                    showPopupMenu(popup_,
                        (Component)evt.getSource(),
                        evt.getX(),evt.getY());
                }
            }
        }
    } 


    /**
     * Loads an icon.
     * @param iconName The icon name
     */
    public static Icon loadIcon(String iconName)
    {
        Icon icon = null;
        
        // get the icon
        if(iconName.startsWith("file:"))
        {
            icon = new ImageIcon(iconName.substring(5));
        }
        else
        {
            URL url = JFlipPane.class.getClass().getResource(
                "/toolbox/util/ui/images/" + iconName);

            if(url == null)
            {
                System.err.println("Icon not found: " + iconName);
                return null;
            }

            icon = new ImageIcon(url);
        }
        
        return icon;
    }     


    /**
     * Returns if the specified event is the popup trigger event.
     * This implements precisely defined behavior, as opposed to
     * MouseEvent.isPopupTrigger().
     * @param evt The event
     * @since jEdit 3.2pre8
     */
    public static boolean isPopupTrigger(MouseEvent evt)
    {
//      if(OperatingSystem.isMacOS())
//          return evt.isControlDown();
//      else
            return ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0);
    } 

    /**
     * Shows the specified popup menu, ensuring it is displayed within
     * the bounds of the screen.
     * @param popup The popup menu
     * @param comp The component to show it for
     * @param x The x co-ordinate
     * @param y The y co-ordinate
     * @since jEdit 4.0pre1
     */
    public static void showPopupMenu(JPopupMenu popup, Component comp,
        int x, int y)
    {
        Point p = new Point(x,y);
        //SwingUtilities.convertPointToScreen(p,comp);

        Dimension size = popup.getPreferredSize();

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        boolean horiz = false;
        boolean vert = false;

        // might need later
        int origX = x;

        if(p.x + size.width > screen.width
            && size.width < screen.width)
        {
            x += (screen.width - p.x - size.width);
            horiz = true;
        }

        if(p.y + size.height > screen.height
            && size.height < screen.height)
        {
            y += (screen.height - p.y - size.height);
            vert = true;
        }

        // If popup needed to be moved both horizontally and
        // vertically, the mouse pointer might end up over a
        // menu item, which will be invoked when the mouse is
        // released. This is bad, so move popup to a different
        // location.
        if(horiz && vert)
        {
            x = origX - size.width - 2;
        }

        popup.show(comp,x,y);
    } 
    
    
}



    // Entry class
//  class Entry
//  {
//      Factory factory;
//      String name;
//      String position;
//      String title;
//
//      IFlipPaneContainer container;
//
//      // only set if open
//      JComponent win;
//
//      // Entry constructor
//      Entry(Factory factory)
//      {
//          this.factory = factory;
//          this.name = factory.name;
//          this.position = System.getProperty(name + ".dock-position"); // ,FLOATING);
//          title = System.getProperty(name + ".title");
//          if(title == null)
//          {
//              System.err.println(name + ".title property"
//                  + " not defined");
//              title = name;
//          }
//
////            if(position == null)
////                position = FLOATING;
//
////            if(position.equals(FLOATING))
////                /* do nothing */;
////            else
//          {
//              if(position.equals(LEFT))
//                  container = this;
//              else
//                  throw new InternalError("Unknown position: " + position);
//
//              container.register(this);
//          }
//      } 
//
//      // open() method
//      void open()
//      {
//          win = factory.createDockableWindow(position);
//          if(win == null)
//          {
//              // error occurred
//              return;
//          }
//
//          System.err.println("Adding " + name + " with position " + position);
//
////            if(position.equals(FLOATING))
////            {
////                container = new FloatingWindowContainer(
////                    JFlipPane.this);
////                container.register(this);
////            }
//
//          container.add(this);
//      } 
//
//      // remove() method
//      void remove()
//      {
//          System.err.println("Removing " + name + " from "
//              + container);
//
//          container.save(this);
//          container.remove(this);
//
////            if(container instanceof FloatingWindowContainer)
////                container = null;
//
//          win = null;
//      } 
//  } 
    


//  // DockableListHandler class
//  static class DockableListHandler extends HandlerBase
//  {
//      // DockableListHandler constructor
//      DockableListHandler(String path, ActionSet actionSet)
//      {
//          this.path = path;
//          this.actionSet = actionSet;
//          stateStack = new Stack();
//          actions = true;
//      } 
//
//      // resolveEntity() method
//      public Object resolveEntity(String publicId, String systemId)
//      {
//          if("dockables.dtd".equals(systemId))
//          {
//              // this will result in a slight speed up, since we
//              // don't need to read the DTD anyway, as AElfred is
//              // non-validating
//              return new StringReader("<!-- -->");
//
//              /* try
//              {
//                  return new BufferedReader(new InputStreamReader(
//                      getClass().getResourceAsStream
//                      ("/org/gjt/sp/jedit/dockables.dtd")));
//              }
//              catch(Exception e)
//              {
//                  System.err.println("Error while opening"
//                      + " dockables.dtd:");
//                  System.err.println(e);
//              } */
//          }
//
//          return null;
//      } 
//
//      // attribute() method
//      public void attribute(String aname, String value, boolean isSpecified)
//      {
//          aname = (aname == null) ? null : aname.intern();
//          value = (value == null) ? null : value.intern();
//
//          if(aname == "NAME")
//              dockableName = value;
//          else if(aname == "NO_ACTIONS")
//              actions = (value == "FALSE");
//      } 
//
//      // doctypeDecl() method
//      public void doctypeDecl(String name, String publicId,
//          String systemId) throws Exception
//      {
//          if("DOCKABLES".equals(name))
//              return;
//
//          System.err.println(path + ": DOCTYPE must be DOCKABLES");
//      } 
//
//      // charData() method
//      public void charData(char[] c, int off, int len)
//      {
//          String tag = peekElement();
//          String text = new String(c, off, len);
//
//          if (tag == "DOCKABLE")
//          {
//              code = text;
//          }
//      } 
//
//      // startElement() method
//      public void startElement(String tag)
//      {
//          tag = pushElement(tag);
//      } 
//
//      // endElement() method
//      public void endElement(String name)
//      {
//          if(name == null)
//              return;
//
//          String tag = peekElement();
//
//          if(name.equals(tag))
//          {
//              if(tag == "DOCKABLE")
//              {
//                  registerDockableWindow(dockableName,
//                      code,actions,actionSet);
//                  // make default be true for the next
//                  // action
//                  actions = true;
//              }
//
//              popElement();
//          }
//          else
//          {
//              // can't happen
//              throw new InternalError();
//          }
//      } 
//
//      // startDocument() method
//      public void startDocument()
//      {
//          try
//          {
//              pushElement(null);
//          }
//          catch (Exception e)
//          {
//              e.printStackTrace();
//          }
//      } 
//
//      // Private members
//
//      // Instance variables
//      private String path;
//      private ActionSet actionSet;
//
//      private String dockableName;
//      private String code;
//      private boolean actions;
//
//      private Stack stateStack;
//      
//
//      // pushElement() method
//      private String pushElement(String name)
//      {
//          name = (name == null) ? null : name.intern();
//
//          stateStack.push(name);
//
//          return name;
//      } 
//
//      // peekElement() method
//      private String peekElement()
//      {
//          return (String) stateStack.peek();
//      } 
//
//      // popElement() method
//      private String popElement()
//      {
//          return (String) stateStack.pop();
//      } 
//
//      
//  } 

    // Factory class
//  class Factory
//  {
//      String name = "name" ;
////        String code;
//
//      // Factory constructor
////        Factory(String name, String code, boolean actions, ActionSet actionSet)
////        {
////            this.name = name;
////            this.code = code;
////            if(actions)
////            {
////                actionSet.addAction(new OpenAction());
////                actionSet.addAction(new ToggleAction("toggle"));
////            }
////        } 
//
//      // createDockableWindow() method
//      JComponent createDockableWindow(String position)
//      {
//            return new DockableLabel("Hello!");
//      } 
//
//
//      // OpenAction class
//      class OpenAction extends AbstractAction //EditAction
//      {
//          // OpenAction constructor
//          OpenAction(String name)
//          {
//              super(name);
//          } 
//
//          // invoke() method
//          public void invoke(View view)
//          {
////                showDockableWindow(name);
//          } 
//
//            /**
//             * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
//             */
//            public void actionPerformed(ActionEvent e)
//            {
//                showDockableWindow(getName());                
//            }
//
//
//          // getCode() method
//          public String getCode()
//          {
//              return "view.getDockableWindowManager()"
//                  + ".showDockableWindow(\"" + getName() + "\");";
//          } 
//      } 
//
//      // ToggleAction class
//      class ToggleAction extends AbstractAction
//      {
//          // ToggleAction constructor
//          ToggleAction(String name)
//          {
//              super(name + "-toggle");
//          } 
//
//          // invoke() method
//          public void actionPerformed(ActionEvent e)
//          {
//              /*view.getDockableWindowManager().*/
//                toggleDockableWindow(getName());
//          } 
//
//          // isToggle() method
//          public boolean isToggle()
//          {
//              return true;
//          } 
//
//          // isSelected() method
//          public boolean isSelected(View view)
//          {
//              return isDockableWindowVisible(getName());
//          } 
//
//          // getCode() method
//          public String getCode()
//          {
//              return "view.getDockableWindowManager()"
//                  + ".toggleDockableWindow(\"" + getName() + "\");";
//          } 
//      } 
//  } 

    // FlipPaneContainer constructor
//    public FlipPaneContainer(JFlipPane wm, String position)
//    {
//        this.wm_ = wm;
//        this.position_ = position;
//
//        // Button box setup
//        buttons_ = new JPanel(new ButtonLayout());
//
//        // the close box must be the same size as the other buttons to look good.
//        // there are two ways to achieve this:
//        // a) write a custom layout manager
//        // b) when the first button is added, give the close box the proper size
//        // I'm lazy so I chose "b". See register() for details.
//
//        closeButton_ = new JButton(loadIcon("closebox.gif"));
//        closeButton_.setRequestFocusEnabled(false);
//        //sp closeButton_.setToolTipText(jEdit.getProperty("view.docking.close-tooltip"));
//        closeButton_.setToolTipText("Close");
//        
//        // makes it look a bit better
//        int left;
//        if(position.equals(JFlipPane.RIGHT)
//            || position.equals(JFlipPane.LEFT))
//            left = 1;
//        else
//            left = 0;
//
//        closeButton_.setMargin(new Insets(0,left,0,0));
//        buttons_.add(closeButton_);
//
//        closeButton_.addActionListener(new ActionHandler());
//
//        popupButton_ = new JButton(loadIcon("ToolbarMenu.gif"));
//        popupButton_.setRequestFocusEnabled(false);
//        //sp popupButton.setToolTipText(jEdit.getProperty("view.docking.menu-tooltip"));
//        popupButton_.setToolTipText("Popup menu");
//        buttons_.add(popupButton_);
//
//        popupButton_.addMouseListener(new MouseHandler());
//        popup_ = new JPopupMenu();
//
//        buttonGroup_ = new ButtonGroup();
//        // JDK 1.4 workaround
//        buttonGroup_.add(nullButton_ = new JToggleButton());
//        
//
//        dockables_ = new Vector();
//        dockablePanel_ = new FlipCardPanel();
//
//        //sp dimension = jEdit.getIntegerProperty("view.dock." + position + ".dimension",0);
//        dimension_ = 0;
//
//        buttons_.addMouseListener(new MouseHandler());
//    } 



//  /**
//   * Plugins shouldn't need to call this method.
//   * @since jEdit 4.0pre1
//   */
//  public static boolean loadDockableWindows(String path, Reader in, ActionSet actionSet)
//  {
//      try
//      {
//          System.err.println("Loading dockables from " + path);
//
//          DockableListHandler dh = new DockableListHandler(path,actionSet);
//          XmlParser parser = new XmlParser();
//          parser.setHandler(dh);
//          parser.parse(null, null, in);
//          return true;
//      }
//      catch(XmlException xe)
//      {
//          int line = xe.getLine();
//          String message = xe.getMessage();
//          Log.log(Log.ERROR,jEdit.class,path + ":" + line
//              + ": " + message);
//      }
//      catch(Exception e)
//      {
//          Log.log(Log.ERROR,jEdit.class,e);
//      }
//
//      return false;
//  } 

    // registerDockableWindow() method
//  public static void registerDockableWindow(String name, String code,
//      boolean actions, ActionSet actionSet)
//  {
//      dockableWindowFactories.addElement(new Factory(name,code,
//          actions,actionSet));
//  } 

    // getRegisteredDockableWindows() method
//  public static String[] getRegisteredDockableWindows()
//  {
//      String[] retVal = new String[dockableWindowFactories.size()];
//      for(int i = 0; i < dockableWindowFactories.size(); i++)
//      {
//          retVal[i] = ((Factory)dockableWindowFactories.elementAt(i)).name;
//      }
//      return retVal;
//  } 




//
//
//    class DockableLabel extends JLabel implements IFlippableWindow
//    {
//        public DockableLabel(String s)
//        {
//            super(s);
//        }
//        
//        public String getName()
//        {
//            return "Crap";
//        }
//    
//        public Component getComponent()
//        {
//            return this;
//        }
//    }
//




//  /**
//   * Opens the specified dockable window. As of version 4.0pre1, has the same
//   * effect as calling showDockableWindow().
//   * @param name The dockable window name
//   */
//  public void addDockableWindow(String name)
//  {
//      showDockableWindow(name);
//  } 


//  /**
//   * Removes the specified dockable window.
//   * @param name The dockable window name
//   */
//  public void removeDockableWindow(String name)
//  {
//      JComponent entry = (JComponent)flippers_.get(name);
//      if(entry == null)
//      {
//          System.err.println("This JFlipPane"
//              + " does not have a window named " + name);
//          return;
//      }
//        
//      setSelectedFlipper(null);
//  } 


//  /**
//   * Toggles the visibility of the specified dockable window.
//   * @param name The dockable window name
//   */
//  public void toggleDockableWindow(String name)
//  {
//      if(isDockableWindowVisible(name))
//          removeDockableWindow(name);
//      else
//          showDockableWindow(name);
//  } 


//  /**
//   * @deprecated The IFlippableWindow interface is deprecated, as is this
//   * method. Use <code>getDockable()</code> instead.
//   */
//  public IFlippableWindow getDockableWindow(String name)
//  {
//      /* this is broken, so you should switch to getDockable() ASAP.
//       * first of all, if the dockable in question returns something
//       * other than itself from the getComponent() method, it won't
//       * work. it will also fail with dockables using the new API,
//       * which don't implement the IFlippableWindow interface (in
//       * which case, this method will return null). */
//      Component comp = getDockable(name);
//      if(comp instanceof IFlippableWindow)
//          return (IFlippableWindow)comp;
//      else
//          return null;
//  } 


//  /**
//   * Returns the specified dockable window. Use this method instead of
//   * the deprecated <code>getDockableWindow()</code> method.
//   * @param name The name of the dockable window
//   */
//  public JComponent getDockable(String name)
//  {
//      JComponent entry = (JComponent)flippers_.get(name);
//      if(entry == null )
//          return null;
//      else
//          return entry;
//  } 


//  /**
//   * Returns if the specified dockable window is visible.
//   * @param name The dockable window name
//   */
//  public boolean isDockableWindowVisible(String name)
//  {
//      JComponent entry = (JComponent)flippers_.get(name);
//      if(entry == null )
//          return false;
//      else
//          return isVisible(entry);
//  } 



//  /**
//   * Opens the specified dockable window.
//   * @param name The dockable window name
//   * @since jEdit 2.6pre3
//   */
//  public void showDockableWindow(String name)
//  {
//      JComponent entry = (JComponent)flippers_.get(name);
//        
//      if(entry == null)
//      {
//          System.err.println("Unknown dockable window: " + name);
//          return;
//      }
//        else
//          addFlipper(entry);
//  } 



//  /**
//   * Called by the view when properties change.
//   */
//  public void propertiesChanged()
//  {
//      alternateLayout_ = false; //jEdit.getBooleanProperty("view.docking.alternateLayout");
//
//      Enumeration enum = flippers_.elements();
//      while(enum.hasMoreElements())
//      {
//          JComponent entry = (JComponent)enum.nextElement();
//          String position = (String)entry.getClientProperty("position");
//          String newPosition = System.getProperty(entry.getName() + ".dock-position");
//          if(newPosition != null /* ??? */
//              && !newPosition.equals(position))
//          {
//              entry.putClientProperty("position", newPosition);
//              removeFlipper(entry);
//
//
////                if(newPosition.equals(FLOATING))
////                    /* do nothing */;
////                else
//              {
////                    if(newPosition.equals(LEFT))
////                        entry.container = this;
//
//                  registerFlipper(entry);
//              }
//          }
//
////            if(entry.container instanceof FloatingWindowContainer)
////            {
////                SwingUtilities.updateComponentTreeUI(((JFrame)entry.container)
////                    .getRootPane());
////            }
//      }
//
//      revalidate();
//  } 




//  /**
//   * Returns if the specified dockable window is docked into the
//   * view.
//   * @param name The dockable's name
//   */
//  public boolean isDockableWindowDocked(String name)
//  {
//      JComponent entry = (JComponent)flippers_.get(name);
//        
//      if(entry == null)
//          return false;
//      else
//          return true; 
//  } 
