package toolbox.util.ui.statusbar.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.beans.Introspector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.statusbar.SimpleStatusBar;

/**
 * Unit test for SimpleStatusBar.
 */
public class SimpleStatusBarTest extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(SimpleStatusBarTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    /**
     * Entrypoint. 
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(SimpleStatusBarTest.class);   
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------

    /**
     * Tests SimpleStatusBar.
     * 
     * @throws Exception on error
     */    
    public void testSimpleStatusBar() throws Exception
    {
        logger_.info("Running testSimpleStatusBar...");
        
        SimpleStatusBar sb = new SimpleStatusBar();
        sb.setStatus("This is the status");
        JPanel p = new JPanel(new BorderLayout());
        p.add(sb, BorderLayout.SOUTH);
        p.add(createPropertySheet(sb), BorderLayout.CENTER);
        launchInFrame(p /* , SCREEN_ONE_HALF*/ );
    }

    
    /**
     * @param sb
     * @return
     */
    private JPanel createPropertySheet(final Object bean) throws Exception
    {
        //BeanInfo beanInfo = new DefaultBeanInfoResolver().getBeanInfo(sb);
        
        BeanInfo beanInfo =
            Introspector.getBeanInfo(
                bean.getClass(), 
                bean.getClass().getSuperclass());
        
        final PropertySheetPanel panel = new PropertySheetPanel();
        panel.setMode(PropertySheet.VIEW_AS_CATEGORIES);
        panel.setProperties(beanInfo.getPropertyDescriptors());
        
        //PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        //System.out.println(ArrayUtil.toString(pds, true));
        //
        //System.out.println(beanInfo.getBeanDescriptor().getBeanClass());
        //
        //for (int i=0; i<pds.length; i++)
        //{
        //    PropertyDescriptor pd = pds[i];
        //    System.out.println(pd.getDisplayName());
        //}
        
        panel.readFromObject(bean);
        
        class UpdateAction extends AbstractAction
        {
            public UpdateAction()
            {
                super("Update");
            }
            
            public void actionPerformed(ActionEvent e)
            {
                panel.writeToObject(bean);
            }
        }
        
        JButton b = new JSmartButton(new UpdateAction());
        JPanel p = new JPanel(new BorderLayout());
        p.add(panel, BorderLayout.CENTER);
        p.add(b, BorderLayout.SOUTH);
        return p;
    }
}