package toolbox.workspace.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.workspace.PluginWorkspace;

/**
 * Toggles smooth fonts.
 */
public class SmoothFontsAction extends BaseAction
{
    private static final Logger logger_ = 
        Logger.getLogger(SmoothFontsAction.class);

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SmoothFontAction.
     */
    public SmoothFontsAction(PluginWorkspace workspace)
    {
        super(workspace);
        putValue(Action.NAME, "Smooth Fonts");
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
        // TODO: Figure out where menus aren't adhering.

        JCheckBoxMenuItem cb = (JCheckBoxMenuItem) e.getSource();
        boolean b = cb.isSelected();
        SwingUtil.setDefaultAntiAlias(b);
        Component[] comps = getWorkspace().getRootPane().getComponents();

        for (int i = 0; i < comps.length;
            SwingUtil.setAntiAliased(comps[i++], b));

//            SwingUtil.setAntiAliased(getJMenuBar(), b);
//
        for (int i = 0, n = getWorkspace().getJMenuBar().getMenuCount(); i < n; 
             i++)
        {
            JMenu menu = getWorkspace().getJMenuBar().getMenu(i);
            SwingUtil.setAntiAliased(menu, b);

			// WORKAROUND: Try/catch added as workaround for FH LookAndFeel
			//             throwing NPE.
            try
            {
                for (int j = 0; j < menu.getItemCount(); j++)
                    SwingUtil.setAntiAliased(menu.getMenuComponent(j), b);
            }
            catch (Exception ex)
            {
                logger_.error(ex);
            }
        }

        getWorkspace().repaint();
    }
}