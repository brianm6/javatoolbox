package toolbox.jtail;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import toolbox.jtail.config.ITailPaneConfig;
import toolbox.util.SwingUtil;
import toolbox.util.ui.font.JFontChooser;

/**
 * Settings pane
 */
public class SettingsPane extends JPanel
{
    JFontChooser fontChooser_;
    ITailPaneConfig defaults_;
    ITailPaneConfig current_;
    
    public SettingsPane(ITailPaneConfig defaults, ITailPaneConfig current)
    {
        buildView();
    }
     
    public void buildView()
    {
        fontChooser_ = new JFontChooser(SwingUtil.getPreferredMonoFont());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        add(fontChooser_);     
        add(new JButton("Box2"));
        add(new JButton("Box3"));
    }
}
