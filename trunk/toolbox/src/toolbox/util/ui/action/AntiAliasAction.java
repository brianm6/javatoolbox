package toolbox.util.ui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import toolbox.util.ui.AntiAliased;

/**
 * Toggles antialiasing.
 */
public class AntiAliasAction extends AbstractAction 
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Antialias aware component.
     */
    private Component component_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an AntiAliasAction.
     * 
     * @param component Component that supports antialiasing.
     */
    public AntiAliasAction(Component component)
    {
        super("AntiAlias");
        component_ = component;
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
        AntiAliased aa = (AntiAliased) component_;
        aa.setAntiAliased(!aa.isAntiAliased());
        component_.repaint();
    }
}