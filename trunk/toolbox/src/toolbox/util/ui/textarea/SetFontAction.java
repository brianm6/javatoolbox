package toolbox.util.ui.textarea;

import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.AntiAliased;
import toolbox.util.ui.font.FontChooserException;
import toolbox.util.ui.font.IFontChooserDialogListener;
import toolbox.util.ui.font.JFontChooser;
import toolbox.util.ui.font.JFontChooserDialog;

/**
 * Pops up a font chooser dialog box and sets the font for a component.
 */
public class SetFontAction extends AbstractAction
{
    private static final Logger logger_ = Logger.getLogger(SetFontAction.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Component to set the font for.
     */
    private final Component component_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SetFontAction.
     */
    public SetFontAction(Component component)
    {
        super("Set font..");
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
        final Font originalFont = component_.getFont();

        // Find parent frame
        Window w = SwingUtilities.getWindowAncestor(component_);
        
        Frame frame = 
            (w != null && w instanceof Frame) ? (Frame) w : new Frame();
        
        boolean antialias = 
            component_ instanceof AntiAliased ?
                ((AntiAliased) component_).isAntiAliased() : false;

        JFontChooserDialog fontChooser =
            new JFontChooserDialog(
                frame, 
                false, 
                component_.getFont(), 
                antialias);
            
        fontChooser.addFontDialogListener(new IFontChooserDialogListener()
        {
            public void okButtonPressed(JFontChooser fontChooser)
            {
                try
                {
                    // Set the newly selected font
                    component_.setFont(fontChooser.getSelectedFont());

                    if (component_ instanceof AntiAliased)
                    {
                        ((AntiAliased) component_).setAntiAliased(
                            fontChooser.isAntiAliased());
                    }
                }
                catch (FontChooserException fce)
                {
                    ExceptionUtil.handleUI(fce, logger_);
                }
            }


            public void cancelButtonPressed(JFontChooser fontChooser)
            {
                // Just restore the original font...skip antialias cuz
                // I'm a lazy bum sometimes..
                component_.setFont(originalFont);
            }


            public void applyButtonPressed(JFontChooser fontChooser)
            {
                okButtonPressed(fontChooser);
            }
        });

        SwingUtil.centerWindow(frame, fontChooser);                        
        fontChooser.setVisible(true);            
    }
}