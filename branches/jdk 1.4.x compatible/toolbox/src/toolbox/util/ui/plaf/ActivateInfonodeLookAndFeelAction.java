package toolbox.util.ui.plaf;

import java.lang.reflect.Method;

import javax.swing.UIManager;

import net.infonode.gui.laf.InfoNodeLookAndFeel;
import net.infonode.gui.laf.InfoNodeLookAndFeelTheme;

import org.apache.commons.beanutils.MethodUtils;

/**
 * Action that activates the <a href="http://infonode.sf.net">InfoNode</a> Look
 * and Feel.
 */    
public class ActivateInfonodeLookAndFeelAction extends ActivateLookAndFeelAction
{
    //--------------------------------------------------------------------------
    // Theme Constants
    //--------------------------------------------------------------------------
    
    /**
     * Property defined in lookandfeel.xml that contains the FCQN of the class 
     * which has accessors for a given theme.
     */
    private static final String PROP_THEME_CLASS = "theme.class";
    
    /**
     * Property defined in lookandfeel.xml that contains the name of the method
     * to invoke on a given class to get access to a given theme.
     */
    private static final String PROP_THEME_METHOD = "theme.method";
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an ActivateInfonodeLookAndFeelAction.
     */
    public ActivateInfonodeLookAndFeelAction()
    {
    }
    
    //--------------------------------------------------------------------------
    // Overrides ActivateLookAndFeelAction
    //--------------------------------------------------------------------------

    /**
     * Overrides method completely (does not call super) since 
     * <code>UIManager.setLookAndFeel()</code> is called with a non-class 
     * argument.
     * <ol>
     *   <li>Read theme class name from props
     *   <li>Read theme method name from props
     *   <li>Invoke method on class to create theme
     *   <li>Install look and feel
     *   <li>Propagate changes to the read of the UI
     * </ol>
     * 
     * @see toolbox.util.ui.plaf.ActivateLookAndFeelAction#activate()
     */
    public void activate() throws Exception
    {
        LAFInfo info = getLookAndFeelInfo();

        // Get class and method to invoke method on to get the theme...
        Class clazz = Class.forName(info.getProperty(PROP_THEME_CLASS));
        String methodName = info.getProperty(PROP_THEME_METHOD);
        
        Method method = 
            MethodUtils.getAccessibleMethod(clazz, methodName, new Class[0]);
        
        // Get an instance of the theme...
        InfoNodeLookAndFeelTheme theme = (InfoNodeLookAndFeelTheme) 
            method.invoke(null, new Object[0]);
        
        // Pass it to the UI manager to activate it...
        UIManager.setLookAndFeel(new InfoNodeLookAndFeel(theme));
        
        // Complete rest of work that superclass implementation executes.
        UIManager.getLookAndFeel().getDefaults().put(
            LAFInfo.PROP_HIDDEN_KEY, 
            getLookAndFeelInfo());
        
        LookAndFeelUtil.propagateChangeInLAF();
    }
}