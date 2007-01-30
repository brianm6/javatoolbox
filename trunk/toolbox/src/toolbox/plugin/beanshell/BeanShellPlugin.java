package toolbox.plugin.beanshell;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import bsh.Interpreter;
import bsh.util.JConsole;

import nu.xom.Element;

import toolbox.util.SwingUtil;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceTransition;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.JSmartTextArea;
import toolbox.workspace.AbstractPlugin;
import toolbox.workspace.PreferencedException;

/**
 * Interactive <a href=http://www.beanshell.org>Beanshell</a> Plugin.
 */
public class BeanShellPlugin extends AbstractPlugin
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * View component of this plugin.
     */
    private JPanel view_;
    
    /**
     * Output area.
     */
    private JSmartTextArea output_;
    
    /**
     * Input area.
     */
    private JConsole console_;

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        view_ = new JPanel();
        
        output_ = new JSmartTextArea(true, SwingUtil.getDefaultAntiAlias());
        
        console_ = new JConsole();
        
        Interpreter interpreter = new Interpreter(console_);
        new Thread(interpreter).start();
        
        JSplitPane splitter = 
            new JSmartSplitPane(JSplitPane.VERTICAL_SPLIT, console_, output_);
            
        view_.setLayout(new BorderLayout());
        view_.add(BorderLayout.CENTER, splitter);
    }    
    
    //--------------------------------------------------------------------------
    // Initializable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map props) throws ServiceException
    {
        checkTransition(ServiceTransition.INITIALIZE);
        buildView();
        transition(ServiceTransition.INITIALIZE);
    }

    //--------------------------------------------------------------------------
    // IPlugin Interface 
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "Bean Shell";
    }


    /**
     * @see toolbox.workspace.IPlugin#getView()
     */
    public JComponent getView()
    {
        return view_;
    }


    /**
     * @see toolbox.workspace.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Beanshell Console";
    }

    //--------------------------------------------------------------------------
    // Destroyable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy() throws ServiceException
    {
        checkTransition(ServiceTransition.DESTROY);
        console_ = null;
        output_ = null;
        view_ = null;
        transition(ServiceTransition.DESTROY);
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface 
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
    }

    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
    }
}