package toolbox.plugin.netmeter;

import java.awt.BorderLayout;

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
    private Service client_;
    private JSmartTextField serverHostnameField_;
    private JSmartTextField serverPortField_;
    
    
    public ClientView()
    {
        buildView();
    }
    
    protected void buildView()
    {
        setLayout(new BorderLayout());
        add(buildInputPanel(), BorderLayout.CENTER);
        add(new ServiceView(client_), BorderLayout.SOUTH);
    }
    
    protected JPanel buildInputPanel()
    {
        JPanel p = new JPanel(new ParagraphLayout());
        
        p.add(new JSmartLabel("Server"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(serverHostnameField_ = new JSmartTextField(20));
        
        p.add(new JSmartLabel("Port"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(serverPortField_ = new JSmartTextField(20));
        
        return p;
    }
}
