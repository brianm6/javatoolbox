package toolbox.workspace.prefs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.l2fprod.common.swing.JButtonBar;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JButtonGroup;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartDialog;
import toolbox.util.ui.JSmartToggleButton;
import toolbox.workspace.IPlugin;
import toolbox.workspace.PluginWorkspace;

/**
 * The PreferencesDialog is invoked by the PluginWorkspace to allow the user 
 * to change various preferences supported by the program. Preferences are
 * grouped together on different panels and can be switched between be selecting
 * that panel's node on the configuration tree. New panels can easily be
 * added by implementing the Preferences interface and providing the 
 * necessary information.
 * 
 * @see toolbox.workspace.PluginWorkspace
 * @see toolbox.workspace.prefs.PreferencesManager
 * @see toolbox.workspace.prefs.IConfigurator
 */
public class PreferencesDialog extends JSmartDialog
{
    private static final Logger logger_ =
        Logger.getLogger(PreferencesDialog.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Button bar on the left side of the dialog box.
     */
    private JButtonBar navButtonBar_;
    
    /**
     * Makes sure only one nav button is selected.
     */
    private JButtonGroup navButtonGroup_;
    
    /**
     * Card panel used to switch out the currently visisble Preferences
     * based on which node in the tree is selected.
     */
    private JPanel cardPanel_;

    /**
     * Layout for switching out the PreferencesViews.
     */
    private CardLayout cardLayout_;

    /**
     * Source of the PreferencesViews.
     */
    private PreferencesManager preferencesManager_;

    /**
     * Parent workspace.
     */
    private final PluginWorkspace workspace_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a PreferencesDialog.
     *
     * @param parent Parent frame.
     * @param preferencesManager PreferencesManager from the workspace.
     */
    public PreferencesDialog(
        Frame parent,
        PreferencesManager preferencesManager)
    {
        super(parent, "Toolbox Preferences", true);
        workspace_ = (PluginWorkspace) parent;
        preferencesManager_ = preferencesManager;
        buildView();
        pack();
        SwingUtil.setSizeAsDesktopPercentage(this, 40, 40); // May have to tweak later
        SwingUtil.centerWindow(parent, this);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Adds a PreferenceView as a node in the tree and adds it to the stack of
     * cards in the card layout.
     * 
     * @param prefs Preferences view to add to the prefs tree.
     */
    public void registerView(IConfigurator prefs)
    {
        logger_.debug("Registering prefs view = " + prefs.getLabel());

        JSmartToggleButton b = new JSmartToggleButton(new ButtonAction(prefs));
        navButtonGroup_.add(b);
        navButtonBar_.add(b);
        cardPanel_.add(prefs.getView(), prefs.getLabel());
    }

    //--------------------------------------------------------------------------
    // Build View
    //--------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        Container view = getContentPane();
        view.setLayout(new BorderLayout());
        view.add(buildNavButtonBar(), BorderLayout.WEST);
        view.add(buildCardPanel(), BorderLayout.CENTER);
        view.add(buildButtonPanel(), BorderLayout.SOUTH);

        navButtonGroup_ = new JButtonGroup();
        
        // Non-plugin based preferences
        IConfigurator[] prefs = preferencesManager_.getConfigurators();
        for (int i = 0; i < prefs.length; registerView(prefs[i++]));

        // Plugin based preferences
        IPlugin[] plugins = workspace_.getPluginHost().getPlugins();
        
        for (int i = 0; i < plugins.length; i++)
        {
            IPlugin plugin = plugins[i];
            IConfigurator p = plugin.getConfigurator();
            
            if (p != null)
            {
                logger_.debug("Registering " + plugin.getPluginName());
                registerView(p);
            }
        } 
    }


    /**
     * Builds the card panel that switches out panels based on the currently
     * selected Preferences button.
     */
    protected JComponent buildCardPanel()
    {
        cardLayout_ = new CardLayout();
        cardPanel_ = new JPanel(cardLayout_);
        cardPanel_.setBorder(new EmptyBorder(10, 0, 0, 10));
        return cardPanel_;
    }

    
    /**
     * Builds the ok/cancel/apply button panel.
     *
     * @return JPanel
     */
    protected JPanel buildButtonPanel()
    {
        JPanel p = new JPanel(new FlowLayout());
        p.add(new JSmartButton(new OKAction()));
        p.add(new JSmartButton(new ApplyAction()));
        p.add(new JSmartButton(new CancelAction()));
        return p;
    }


    /**
     * Builds the button bar navigation panel. One Preferences per button.
     *
     * @return JComponent
     */
    protected JComponent buildNavButtonBar()
    {
        navButtonBar_ = new JButtonBar(SwingConstants.VERTICAL);
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(10,10,0,5));
        p.add(navButtonBar_, BorderLayout.CENTER);
        return p;
    }

    //--------------------------------------------------------------------------
    // ButtonAction
    //--------------------------------------------------------------------------
    
    class ButtonAction extends AbstractAction
    {
        ButtonAction(IConfigurator prefs)
        {
            super(prefs.getLabel(), prefs.getIcon());
            putValue("prefs", prefs);
        }

        
        public void actionPerformed(ActionEvent e)
        {
            IConfigurator prefs = (IConfigurator) getValue("prefs");
            cardLayout_.show(cardPanel_, prefs.getLabel());
        }
    }

    //--------------------------------------------------------------------------
    // OKAction
    //--------------------------------------------------------------------------

    /**
     * Propagates the OK event to all the views and dismisses the dialog box.
     */
    class OKAction extends AbstractAction
    {
        public OKAction()
        {
            super("OK");
        }

        
        public void actionPerformed(ActionEvent e)
        {
            IConfigurator[] prefs = preferencesManager_.getConfigurators();
            for (int i = 0; i < prefs.length; prefs[i++].onOK());
            dispose();
        }
    }

    //--------------------------------------------------------------------------
    // ApplyAction
    //--------------------------------------------------------------------------

    /**
     * Propagates the Apply event to all the views.
     */
    class ApplyAction extends AbstractAction
    {
        public ApplyAction()
        {
            super("Apply");
        }

        
        public void actionPerformed(ActionEvent e)
        {
            IConfigurator[] prefs = preferencesManager_.getConfigurators();
            for (int i = 0; i < prefs.length; prefs[i++].onApply());
        }
    }

    //--------------------------------------------------------------------------
    // CancelAction
    //--------------------------------------------------------------------------

    /**
     * Propagates the Cancel event to all the views and disposes of the 
     * dialog box.
     */
    class CancelAction extends AbstractAction
    {
        public CancelAction()
        {
            super("Cancel");
        }

        
        public void actionPerformed(ActionEvent e)
        {
            IConfigurator[] prefs = preferencesManager_.getConfigurators();
            for (int i = 0; i < prefs.length; prefs[i++].onCancel());
            dispose();
        }
    }
}