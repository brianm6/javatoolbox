package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import toolbox.jtail.config.IJTailConfig;

/**
 * JTail preferences dialog
 */
public class PreferencesDialog extends JDialog implements ActionListener
{
    private static final Logger logger_ =
        Logger.getLogger(PreferencesDialog.class);
    
    private static final String ACTION_OK     = "OK";
    private static final String ACTION_CANCEL = "Cancel";
    
    /** 
     * Preferences will be changed on the config object directly 
     */
    private IJTailConfig config_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for PreferencesDialog.
     * 
     * @param owner     Parent frame
     * @param config    Configuration 
     */
    public PreferencesDialog(Frame owner, IJTailConfig config)
    {
        super(owner, "JTail Preferences", true);
        config_ = config;
        buildView();
        pack();
    }
    
    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Builds the GUI
     */
    protected void buildView()
    {
        // Build and wire button panel        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton(ACTION_OK);
        okButton.setActionCommand(ACTION_OK);
        okButton.addActionListener(this);
        
        JButton cancelButton = new JButton(ACTION_CANCEL);
        cancelButton.setActionCommand(ACTION_CANCEL);
        cancelButton.addActionListener(this);
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        // Add to content pane        
        JPanel view = new JPanel(new BorderLayout());
        view.add(BorderLayout.CENTER, buildPreferencesPanel());
        view.add(BorderLayout.SOUTH, buttonPanel);
        setContentPane(view);
    }


    /**
     * Builds the preferences panel
     */    
    protected JPanel buildPreferencesPanel()
    {
        // Build and wire preferences panel
        JPanel prefPanel = new JPanel(new GridBagLayout());
        prefPanel.setBorder(BorderFactory.createTitledBorder("Defaults"));
        
        GridBagConstraints gbc = new GridBagConstraints();
       
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0,4,7,4);
        
        prefPanel.add(new JLabel("AutoScroll", SwingConstants.RIGHT), gbc);
        
        gbc.gridx++;
        prefPanel.add(new JCheckBox(), gbc);        
        
        gbc.gridy++;
        gbc.gridx--;
        prefPanel.add(
            new JLabel("Show Line Numbers", SwingConstants.RIGHT), gbc);
        
        gbc.gridx++;
        prefPanel.add(new JCheckBox(), gbc);
        
        gbc.gridx--;
        gbc.gridy++;
        prefPanel.add(new JLabel("Filter", SwingConstants.RIGHT), gbc);
        
        gbc.gridx++;
        gbc.fill = GridBagConstraints.NONE;
        prefPanel.add(new JTextField(12), gbc);
                
        return prefPanel;
    }

    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @param  e  ActionEvent
     */
    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();
        
        if (action.equals(ACTION_OK))
        {
            
        }
        else if (action.equals(ACTION_CANCEL))
        {
            
        }
        else
        {
            logger_.warn(
                "No handler in actionPerformed() for command " + action);
        }        
    }
}