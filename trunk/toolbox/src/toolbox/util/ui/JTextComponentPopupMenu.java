package toolbox.util.ui;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.font.FontChooserException;
import toolbox.util.ui.font.IFontChooserDialogListener;
import toolbox.util.ui.font.JFontChooser;
import toolbox.util.ui.font.JFontChooserDialog;

/**
 * Popup menu with commonly used functionality for JTextComponent subclasses.
 */
public class JTextComponentPopupMenu extends JPopupMenu
{
	private static final Logger logger_ =
		Logger.getLogger(JTextComponentPopupMenu.class); 
		
    /**
     * Text component to associate this popup menu with
     */
    private JTextComponent textComponent_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default Constructor
     */
    public JTextComponentPopupMenu()
    {
    }
    
    /**
     * Constructor for JTextComponentPopupMenu.
     * 
     * @param  textComponent  JTextComponent to add popup to
     */
    public JTextComponentPopupMenu(JTextComponent textComponent)
    {
        this("", textComponent);
    }

    /**
     * Constructor for JTextComponentPopupMenu.
     * 
     * @param  label          Popupmenu label
     * @param  textComponent  JTextComponent to add popup to
     */
    public JTextComponentPopupMenu(String label, JTextComponent textComponent)
    {
        super(label);
        textComponent_ = textComponent;
        buildView();
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------

    /**
     * Builds popupmenu and adds mouse listener to listbox
     */
    protected void buildView()
    {
        add(new JMenuItem(new CopyAction()));
        add(new JMenuItem(new PasteAction()));
        add(new JMenuItem(new SelectAllAction()));
        add(new JMenuItem(new SetFontAction()));
        textComponent_.addMouseListener(new JPopupListener(this));
    }
    
    //--------------------------------------------------------------------------
    //  Action Inner Classes
    //--------------------------------------------------------------------------

    /**
     * Copies the contents of the currently selected indices to the clipboard
     */    
    protected class CopyAction extends AbstractAction
    {
        public CopyAction()
        {
            super("Copy");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            textComponent_.copy();
        }
    }

    /**
     * Pastes the contents of the clipboard into the text component
     */    
    protected class PasteAction extends AbstractAction
    {
        public PasteAction()
        {
            super("Paste");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            textComponent_.paste();
        }
    }
    
    /**
     * Selects all items in the list box 
     */
    protected class SelectAllAction extends AbstractAction
    {
        public SelectAllAction()
        {
            super("Select All");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            textComponent_.selectAll();
        }
    }
    
    /**
     * Sets the font in the text component
     */
    protected class SetFontAction extends AbstractAction
    {
    	public SetFontAction()
    	{
    		super("Set font..");
    	}
    	
        public void actionPerformed(ActionEvent e)
        {
        	final Font originalFont = textComponent_.getFont();
        	
        	// Find parent frame
        	Window w = SwingUtilities.getWindowAncestor(textComponent_);
        	
        	Frame frame = 
        		(w != null && w instanceof Frame) ? (Frame) w : new Frame();
        	
        	JFontChooserDialog fontChooser = new JFontChooserDialog(
        		frame, false, textComponent_.getFont());
        		
			fontChooser.addFontDialogListener(new IFontChooserDialogListener()
            {
                public void okButtonPressed(JFontChooser fontChooser)
                {
                	try
                	{
                		// Set the newly selected font
						textComponent_.setFont(fontChooser.getSelectedFont());                		
                	}
                	catch (FontChooserException fce)
                	{
                		logger_.error(fce);
                	}
                }

                public void cancelButtonPressed(JFontChooser fontChooser)
                {
                	// Just restore the original font
					textComponent_.setFont(originalFont);                		
                }

                public void applyButtonPressed(JFontChooser fontChooser)
                {
                    // Same as OK
                    okButtonPressed(fontChooser);
                }
            });

			SwingUtil.centerWindow(frame, fontChooser);			            
            fontChooser.setVisible(true);        	
        }
    }
}