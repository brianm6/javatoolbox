package toolbox.plugin.netmeter;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.layout.ParagraphLayout;

/**
 * ClientView
 */
public class ClientView extends JPanel
{
    private ServiceView serviceView_;
    private Client client_;
    private JSmartTextField serverHostnameField_;
    private JSmartTextField serverPortField_;
    
    
    public ClientView(Client client)
    {
        client_ = client;
        buildView();
    }
    
    protected void buildView()
    {
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(new BorderLayout());
        add(buildInputPanel(), BorderLayout.CENTER);
        add(new ServiceView(client_), BorderLayout.SOUTH);
        
        if (client_ != null)
        {
            serverHostnameField_.setText(client_.getHostname());
            serverPortField_.setText(client_.getPort()+"");
        }
            
    }
    
    protected JPanel buildInputPanel()
    {
        JPanel p = new JPanel(new ParagraphLayout());
        
        p.add(new JSmartLabel("Server"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(serverHostnameField_ = new JSmartTextField(20));
        
        p.add(new JSmartLabel("Port"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(serverPortField_ = new JSmartTextField(20));
        
        serverHostnameField_.setEditable(false);
        serverPortField_.setEditable(false);
        
        return p;
    }
}
