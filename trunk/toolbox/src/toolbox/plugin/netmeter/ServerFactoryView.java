package toolbox.plugin.netmeter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.layout.ParagraphLayout;

/**
 * ServerFactoryView. 
 */
public class ServerFactoryView extends JPanel
{
    /**
     * Parent plugin.
     */
    private NetMeterPlugin plugin_;
    
    /**
     * Server port.
     */
    private JTextField serverPortField_;
    
    /**
     * Button that creates a new ServerView.
     */
    private JButton createButton_;
    
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
        plugin_ = plugin;
        buildView();
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Builds the GUI.
     */
    protected void buildView()
    {
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel(new ParagraphLayout());

        // Port
        inputPanel.add(new JSmartLabel("Server Port"), 
            ParagraphLayout.NEW_PARAGRAPH);
        serverPortField_ = new JSmartTextField(6);
        serverPortField_.setText(NetMeterPlugin.DEFAULT_PORT+"");
        inputPanel.add(serverPortField_);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createButton_ = new JSmartButton(new CreateAction()));
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    //--------------------------------------------------------------------------
    // CreateAction
    //--------------------------------------------------------------------------
    
    class CreateAction extends AbstractAction
    {
        public CreateAction()
        {
            super("Create Server");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            Server server = new Server(
                Integer.parseInt(serverPortField_.getText()));
            
            ServerView serverView = new ServerView(server);
            plugin_.addCompartment(serverView);
        }
    }
}