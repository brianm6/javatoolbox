package toolbox.plugin.netmeter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import toolbox.util.ArrayUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.layout.ParagraphLayout;

/**
 * 
 */
public class ClientFactoryView extends JPanel
{
    private JTextField serverHostnameField_;
    private JTextField serverPortField_;
    private JButton createButton_;
    private ClientDelivery[] listeners_;
    
    public ClientFactoryView()
    {
        listeners_ = new ClientDelivery[0];
        
        buildView();
    }
    
    protected void buildView()
    {
        setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new ParagraphLayout());
        
        inputPanel.add(new JSmartLabel("Server Hostname"), 
            ParagraphLayout.NEW_PARAGRAPH);
        
        inputPanel.add(serverHostnameField_ = new JSmartTextField(20));
        
        inputPanel.add(new JSmartLabel("Server Port"), 
            ParagraphLayout.NEW_PARAGRAPH);
        
        inputPanel.add(serverPortField_ = new JSmartTextField(6));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createButton_ = new JSmartButton(new CreateAction()));
        
        
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public void addRecipient(ClientDelivery recipient)
    {
        listeners_ = (ClientDelivery[]) ArrayUtil.add(listeners_, recipient);
    }
    
    protected void fireClientCreated(ClientView clientView)
    {
        for(int i=0; i<listeners_.length; i++)
            listeners_[i].acceptDelivery(clientView);
    }
    
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
            
            fireClientCreated(clientView);
        }
    }
       
    
    
    interface ClientDelivery
    {
        void acceptDelivery(ClientView clientView);
    }
}

