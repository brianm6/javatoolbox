package toolbox.plugin.netmeter;

import java.awt.event.ActionEvent;

import javax.swing.JTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import toolbox.forms.SmartComponentFactory;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.SmartAction;

/**
 * ClientFactoryView is a user interface component that exposes the 
 * functionality provided by a ClientFactory.
 * <p>
 * A ClientFactory view:
 * <ul>
 *  <li>Is a UI component.
 *  <li>Accepts input to configure a ClientView.
 *  <li>Can create any number of ClientViews.
 *  <li>Hands newly created ClientViews back to the NetMeterPlugin.
 * </ul>
 */
public class ClientFactoryView extends JHeaderPanel
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Parent plugin.
     */
    private NetMeterPlugin plugin_;
    
    /**
     * Server hostname field.
     */
    private JTextField serverHostnameField_;
    
    /**
     * Server port field.
     */
    private JTextField serverPortField_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ClientFactoryView.
     * 
     * @param plugin NetMeterPlugin.
     */
    public ClientFactoryView(NetMeterPlugin plugin)
    {
        super(ImageCache.getIcon(
            ImageCache.IMAGE_HARD_DRIVE), 
            "Client Factory");
        
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
        serverHostnameField_ = new JSmartTextField(12);
        serverHostnameField_.setText(NetMeterPlugin.DEFAULT_HOSTNAME);
        
        serverPortField_ = new JSmartTextField(6);
        serverPortField_.setText(NetMeterPlugin.DEFAULT_PORT + "");
        
        FormLayout layout = new FormLayout("right:pref, pref, pref", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setComponentFactory(SmartComponentFactory.getInstance());
        builder.setDefaultDialogBorder();
        
        builder.append("&Server Hostname", serverHostnameField_);
        builder.nextLine();
        
        builder.append("Server &Port", serverPortField_);
        builder.nextLine();
        
        builder.appendRelatedComponentsGapRow();
        builder.nextLine();

        builder.appendRow("pref");
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
     * CreateAction creates the actual ClientView component and hands it back
     * to the plugin.
     */
    class CreateAction extends SmartAction
    {
        /**
         * Creates a CreateAction.
         */
        public CreateAction()
        {
            super("Create Client", true, false, null);
        }

        
        /*
         * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            Client client = new Client(
                serverHostnameField_.getText(),
                Integer.parseInt(serverPortField_.getText()));
            
            ClientView clientView = new ClientView(client);
            plugin_.addCompartment(clientView);
        }
    }
}