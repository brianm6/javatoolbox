package toolbox.workspace.prefs;

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
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartCheckBox;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.action.DisposeAction;

/**
 * Workspace preferences dialog box.
 */
public class PreferencesView extends JDialog implements ActionListener
{
    private static final Logger logger_ =
        Logger.getLogger(PreferencesView.class);

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    private static final String ACTION_OK     = "OK";
    private static final String ACTION_CANCEL = "Cancel";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------


    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Constructor for PreferencesView.
     *
     * @param parent Parent frame.
     */
    public PreferencesView(Frame parent)
    {
        super(parent, "Toolbox Preferences", true);
        buildView();
        pack();
        SwingUtil.centerWindow(parent, this);
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------

    /**
     * Builds the GUI.
     */
    protected void buildView()
    {
        // Build and wire button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JSmartButton(ACTION_OK);
        okButton.setActionCommand(ACTION_OK);
        okButton.addActionListener(this);

        JButton cancelButton = new JSmartButton(new DisposeAction(this));
        cancelButton.setText("Cancel");
        //cancelButton.addActionListener(this);

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Add to content pane
        JPanel view = new JPanel(new BorderLayout());
        view.add(BorderLayout.CENTER, buildPreferencesPanel());
        view.add(BorderLayout.SOUTH, buttonPanel);
        setContentPane(view);
    }


    /**
     * Builds the preferences panel.
     *
     * @return Preferences panel.
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
        gbc.insets = new Insets(0, 4, 7, 4);

        prefPanel.add(new JSmartLabel("AutoScroll", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        prefPanel.add(new JSmartCheckBox(), gbc);

        gbc.gridy++;
        gbc.gridx--;
        prefPanel.add(
            new JSmartLabel("Show Line Numbers", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        prefPanel.add(new JSmartCheckBox(), gbc);

        gbc.gridx--;
        gbc.gridy++;
        prefPanel.add(new JSmartLabel("Filter", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        gbc.fill = GridBagConstraints.NONE;
        prefPanel.add(new JSmartTextField(12), gbc);

        return prefPanel;
    }

    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------

    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();

        if (action.equals(ACTION_OK))
        {
            dispose();
        }
        else if (action.equals(ACTION_CANCEL))
        {
            dispose();
        }
        else
        {
            logger_.warn(
                "No handler in actionPerformed() for command " + action);
        }
    }
}