package toolbox.junit.testcase;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

import junit.framework.TestCase;

import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;

import toolbox.junit.CloverTestSuite;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.plaf.LookAndFeelUtil;

/**
 * Test case that is a base class for tests associated with some manual user
 * interface sequence of events. UITestCase is a 'marker' class used by
 * {@link CloverTestSuite} to exclude unit tests not suitable for unattended
 * execution.
 * 
 * @see toolbox.junit.CloverTestSuite
 * @see toolbox.junit.CloverTestFilter
 */
public class UITestCase extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(UITestCase.class);
    
    //--------------------------------------------------------------------------
    // Constants 
    //--------------------------------------------------------------------------
    
    // Window/Frame/Dialog dimensions relative to the size of the desktop.
    public static final int SCREEN_PACK           = 0;
    public static final int SCREEN_FULL           = 1;
    public static final int SCREEN_ONE_HALF       = 2;
    public static final int SCREEN_ONE_THIRD      = 3;
    public static final int SCREEN_TWO_THIRDS     = 4;
    public static final int SCREEN_THREE_QUARTERS = 5;
    
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /**
     * Optional menu bar.
     */
    private JMenuBar menuBar_;

    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Protected constructor.
     */
    protected UITestCase()
    {
        try
        {
            LookAndFeelUtil.setPreferredLAF();
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the menuBar.
     *
     * @return JMenuBar
     */
    public JMenuBar getMenuBar()
    {
        return menuBar_;
    }

    
    /**
     * Sets the menuBar.
     *
     * @param menuBar The menuBar to set.
     */
    public void setMenuBar(JMenuBar menuBar)
    {
        menuBar_ = menuBar;
    }
    
    //--------------------------------------------------------------------------
    // Protected 
    //--------------------------------------------------------------------------
    
    /**
     * Launches test component in a separate frame.
     * 
     * @param c Component containing UI objects to test.
     * @return Newly created frame.
     */
    protected JFrame launchInFrame(JComponent c)
    {
        JFrame frame = new JFrame(ClassUtils.getShortClassName(getClass()));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(c, BorderLayout.CENTER);
        
        if (menuBar_ != null)
            frame.setJMenuBar(menuBar_);
        
        frame.pack();
        SwingUtil.centerWindow(frame);
        frame.setVisible(true);
        return frame;
    }
    
    
    /**
     * Launches test component in a modal dialog.
     * 
     * @param c Component containing UI objects to test.
     * @return Newly created dialog.
     */
    protected JDialog launchInDialog(JComponent c)
    {
        return launchInDialog(c, SCREEN_PACK);
    }

    
    /**
     * Launches test component in a modal dialog.
     * 
     * @param c Component containing UI objects to test.
     * @param size Size constant for the dimensions of the window.
     * @return Newly created dialog.
     */
    protected JDialog launchInDialog(JComponent c, int size)
    {
        JFrame parent = new JFrame();
        
        JDialog dlg = new JDialog(parent, 
            ClassUtils.getShortClassName(getClass().getName()), true);
        
        dlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dlg.getContentPane().setLayout(new BorderLayout());
        dlg.getContentPane().add(c, BorderLayout.CENTER);
        
        if (menuBar_ != null)
            dlg.setJMenuBar(menuBar_);
        
        switch (size)
        {
            case SCREEN_FULL : 
                
                SwingUtil.setSizeAsDesktopPercentage(dlg, 100, 100);
                break;
                
            case SCREEN_ONE_HALF :
                
                SwingUtil.setSizeAsDesktopPercentage(dlg, 50, 50);
                break;
                
            case SCREEN_ONE_THIRD :
                
                SwingUtil.setSizeAsDesktopPercentage(dlg, 33, 33);
                break;
                
            case SCREEN_TWO_THIRDS :
                
                SwingUtil.setSizeAsDesktopPercentage(dlg, 66, 66);
                break;
                
            case SCREEN_THREE_QUARTERS :
                
                SwingUtil.setSizeAsDesktopPercentage(dlg, 75, 75);
                break;
            
            case SCREEN_PACK :
                
                dlg.pack();
                break;
                
            default:
                
                throw new IllegalArgumentException(
                    "Screen size " + size + " is invalid.");
        }
        
        SwingUtil.centerWindow(dlg);
        dlg.setVisible(true);
        parent.dispose();
        return dlg;
    }
    
    
    /**
     * Launches the UI in a panel.
     * 
     * @param c Component.
     */
    protected void launchInPanel(JComponent c)
    {
        throw new IllegalArgumentException("Not implemented");
    }
    
    
    /**
     * Creates an editable property sheet for the given object.
     * 
     * @param bean Java bean.
     * @return JPanel
     * @throws Exception on error.
     */
    protected JPanel createPropertySheet(final Object bean) throws Exception
    {
        BeanInfo beanInfo =
            Introspector.getBeanInfo(
                bean.getClass(), 
                bean.getClass().getSuperclass());
        
        final PropertySheetPanel propSheet = new PropertySheetPanel();
        propSheet.setMode(PropertySheet.VIEW_AS_CATEGORIES);
        propSheet.setProperties(beanInfo.getPropertyDescriptors());
        propSheet.readFromObject(bean);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(propSheet), BorderLayout.CENTER);
        
        JHeaderPanel headerPanel = 
            new JHeaderPanel(
                "JavaBean Properties of " + bean.getClass().getName(), 
                null, 
                panel);
        
        //
        // Changes to property sheet are immediately applied to the bean
        //
        propSheet.addPropertySheetChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                propSheet.writeToObject(bean);
            }
        });
        
        //
        // Changes to the bean are immediately applied to the propertysheet
        // 
        if (bean instanceof Component)
        {
            Component c = (Component) bean;
            
            c.addPropertyChangeListener(new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent evt)
                {
                    propSheet.readFromObject(bean);
                }
            });
        }
        
        return headerPanel;
    }
    
    
    /**
     * Creates a console that spews out all property change events that
     * originate from the given component.
     * 
     * @param c Component to spy on.
     * @return JPanel.
     */
    protected JPanel createPropertyChangeConsole(Component c)
    {
        
        final JSmartTextArea area = new JSmartTextArea("");

        JHeaderPanel p = 
            new JHeaderPanel(
                "Property Change Events - " + c.getClass().getName(), 
                JHeaderPanel.createToolBar(area), new JScrollPane(area));

        c.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                //if (SwingUtil.getFrameAncestor(area).isDisplayable())
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Prop: " + evt.getPropertyName() + "\n");
                    sb.append("Old : " + evt.getOldValue() + "\n");
                    sb.append("New : " + evt.getNewValue() + "\n\n");
                    area.append(sb.toString());
                }
            }
        });
        
        return p;
    }
}