package toolbox.plugin.netmeter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JTextField;

import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.layout.ParagraphLayout;

/**
 * ServerFactoryView concepts.
 * <ul>
 * <li>ServerFactoryView is a UI component.
 * <li>ServerFactoryView fields input from the user to configure a ServerView.
 * <li>ServerFactoryView can create any number of ServerViews
 * <li>ServerFactoryView hands newly created ServerViews back to the 
 *     NetMeterPlugin.
 * </ul>
 * 
 * @see ServerFactoryView
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
        JPanel content = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new ParagraphLayout());

        // Port
        inputPanel.add(
            new JSmartLabel("Server Port"), 
            ParagraphLayout.NEW_PARAGRAPH);
        
        serverPortField_ = new JSmartTextField(6);
        serverPortField_.setText(NetMeterPlugin.DEFAULT_PORT + "");
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
     * CreateAction create a ServerView.
     */
    class CreateAction extends AbstractAction
    {
        /**
         * Creates a CreateAction.
         */
        public CreateAction()
        {
            super("Create Server");
        }

        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            Server server = new Server(
                Integer.parseInt(serverPortField_.getText()));
            
            ServerView serverView = new ServerView(server);
            plugin_.addCompartment(serverView);
        }
    }
}