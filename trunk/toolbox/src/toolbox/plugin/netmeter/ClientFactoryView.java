package toolbox.plugin.netmeter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import toolbox.util.FontUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.layout.ParagraphLayout;

/**
 * ClientFactoryView.
 */
public class ClientFactoryView extends JPanel
{
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
        
        JLabel title = new JSmartLabel(
                "  Client Factory", 
                Color.LIGHT_GRAY, 
                Color.WHITE); 
        
        FontUtil.setBold(title);

        JPanel inputPanel = new JPanel(new ParagraphLayout());
        inputPanel.add(new JSmartLabel("Server Hostname"), 
            ParagraphLayout.NEW_PARAGRAPH);
        
        serverHostnameField_ = new JSmartTextField(12);
        serverHostnameField_.setText(NetMeterPlugin.DEFAULT_HOSTNAME);
        inputPanel.add(serverHostnameField_);
        
        inputPanel.add(new JSmartLabel("Server Port"), 
            ParagraphLayout.NEW_PARAGRAPH);
        
        serverPortField_ = new JSmartTextField(6);
        serverPortField_.setText(NetMeterPlugin.DEFAULT_PORT+"");
        inputPanel.add(serverPortField_);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JSmartButton(new CreateAction()));
        
        JPanel pp = new JPanel(new BorderLayout());
        pp.add(title, BorderLayout.CENTER);
        
        add(title, BorderLayout.NORTH);
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
            super("Create Client");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            Client client = new Client(
                serverHostnameField_.getText(),
                Integer.parseInt(serverPortField_.getText()));
            
            ClientView clientView = new ClientView(client);
            plugin_.addCompartment(clientView);
        }
    }
}