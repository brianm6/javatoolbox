package toolbox.plugin.netmeter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import toolbox.forms.SmartComponentFactory;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.layout.ParagraphLayout;

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
        buildFormsView();
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

    
    /**
     * Constructs the user interface.
     */
    protected void buildFormsView()
    {
        JPanel content = new JPanel(new BorderLayout());

        serverHostnameField_ = new JSmartTextField(12);
        serverHostnameField_.setText(NetMeterPlugin.DEFAULT_HOSTNAME);
        
        serverPortField_ = new JSmartTextField(6);
        serverPortField_.setText(NetMeterPlugin.DEFAULT_PORT + "");
        
        FormLayout layout = new FormLayout(
            //"right:pref, 4dlu, left:pref",
            "right:pref, pref, pref",
            "pref, pref, 100px, pref, pref"); 
        
        layout.setRowGroups(new int[][] {{1,3}});
        
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setComponentFactory(SmartComponentFactory.getInstance());
        builder.setDefaultDialogBorder();
        
        builder.append("&Server Hostname");
        //builder.appendLabelComponentsGapColumn();
        builder.append(serverHostnameField_);
        builder.nextLine();
        
        builder.append("Server &Port");
        //builder.appendLabelComponentsGapColumn();
        builder.append(serverPortField_);
        builder.nextLine();
        
        builder.nextColumn();
        builder.nextColumn();
        builder.append(new JSmartButton(new CreateAction()));
        
        //JPanel buttonPanel = new JPanel(new FlowLayout());
        //buttonPanel.add(new JSmartButton(new CreateAction()));
        
        //content.add(builder.getPanel(), BorderLayout.NORTH);
        //content.add(buttonPanel, BorderLayout.SOUTH);
        setContent(builder.getPanel());
    }
    
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