package toolbox.plugin.netmeter;

import java.awt.event.ActionEvent;

import javax.swing.JTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import toolbox.forms.SmartComponentFactory;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.SmartAction;

/**
 * ServerFactoryView concepts.
 * <ul>
 *  <li>ServerFactoryView is a UI component.
 *  <li>ServerFactoryView fields input from the user to configure a ServerView.
 *  <li>ServerFactoryView can create any number of ServerViews
 *  <li>ServerFactoryView hands newly created ServerViews back to the 
 *      NetMeterPlugin.
 * </ul>
 * 
 * @see toolbox.plugin.netmeter.ServerView
 */
public class ServerFactoryView extends JHeaderPanel
{
    // TODO: Set icon in header
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Parent plugin.
     */
    private NetMeterPlugin plugin_;
    
    /**
     * Server port.
     */
    private JTextField serverPortField_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ServerFactoryView.
     * 
     * @param plugin NetMeterPlugin.
     */
    public ServerFactoryView(NetMeterPlugin plugin)
    {
        super("Server Factory");
        plugin_ = plugin;
        buildView();
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        serverPortField_ = new JSmartTextField(6);
        serverPortField_.setText(NetMeterPlugin.DEFAULT_PORT + "");
        
        FormLayout layout = new FormLayout("r:p:g, p, l:p:g", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setComponentFactory(SmartComponentFactory.getInstance());
        builder.setDefaultDialogBorder();
        
        builder.append("Server &Port", serverPortField_);
        builder.nextLine();
        
        builder.appendRelatedComponentsGapRow();
        builder.nextLine();

        builder.appendRow("p");
        CellConstraints cc = new CellConstraints();
        
        builder.add(
            new JSmartButton(new CreateAction()), 
            cc.xyw(builder.getColumn(), builder.getRow(), 3, "c,f"));
        
        setContent(builder.getPanel());
    }
    
    //--------------------------------------------------------------------------
    // CreateAction
    //--------------------------------------------------------------------------
    
    /**
     * CreateAction create a ServerView.
     */
    class CreateAction extends SmartAction
    {
        /**
         * Creates a CreateAction.
         */
        public CreateAction()
        {
            super("Create Server", true, false, null);
        }

        
        /*
         * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            Server server = new Server(
                Integer.parseInt(serverPortField_.getText()));
            
            ServerView serverView = new ServerView(server);
            plugin_.addCompartment(serverView);
        }
    }
}