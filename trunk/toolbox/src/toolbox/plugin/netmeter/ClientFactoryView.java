package toolbox.plugin.netmeter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JTextField;

import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.layout.ParagraphLayout;

/**
 * ClientFactoryView concepts.
 * <ul>
 *  <li>ClientFactoryView is a UI component.
 *  <li>ClientFactoryView fields input from the user to configure a ClientView.
 *  <li>ClientFactoryView can create any number of ClientViews.
 *  <li>ClientFactoryView hands newly created ClientViews back to the 
 *      NetMeterPlugin.
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
            ImageCache.IMAGE_HARD_DRIVE), "Client Factory");
        
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
        JPanel content = new JPanel(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new ParagraphLayout());
        
        inputPanel.add(new JSmartLabel("Server Hostname"), 
            ParagraphLayout.NEW_PARAGRAPH);
        
        serverHostnameField_ = new JSmartTextField(12);
        serverHostnameField_.setText(NetMeterPlugin.DEFAULT_HOSTNAME);
        
        serverPortField_ = new JSmartTextField(6);
        serverPortField_.setText(NetMeterPlugin.DEFAULT_PORT + "");
        
        inputPanel.add(serverHostnameField_);
        
        inputPanel.add(
            new JSmartLabel("Server Port"), 
            ParagraphLayout.NEW_PARAGRAPH);
        
        inputPanel.add(serverPortField_);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JSmartButton(new CreateAction()));
        
        content.add(inputPanel, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);
        setContent(content);
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

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
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