package toolbox.jedit;

import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import toolbox.jedit.action.AbstractJEditAction;
import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JFindDialog;
import toolbox.util.ui.font.FontChooserException;
import toolbox.util.ui.font.IFontChooserDialogListener;
import toolbox.util.ui.font.JFontChooser;
import toolbox.util.ui.font.JFontChooserDialog;

/**
 * UI Actions for the JEditTextArea.
 */
public final class JEditActions
{
    private static final Logger logger_ = Logger.getLogger(JEditActions.class);
    
    //--------------------------------------------------------------------------
    // FindAction
    //--------------------------------------------------------------------------
    
    /**
     * Triggers activation of the Find Dialog box.
     */    
    static class FindAction extends AbstractJEditAction
    {
        /**
         * Creates a FindAction.
         * 
         * @param textComp Target textarea.
         */
        public FindAction(JEditTextArea textComp)
        {
            super(
                "Find..", ImageCache.getIcon(ImageCache.IMAGE_FIND), textComp);
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            JFindDialog.SearchInitiator initiator = 
                new JEditSearchInitiator("", area_);
                
            JFindDialog findDialog = new JFindDialog(initiator);
            findDialog.setVisible(true);
        }
    }

    //--------------------------------------------------------------------------
    // SetFontAction
    //--------------------------------------------------------------------------
    
    /**
     * Sets the font in the text component.
     */
    static class SetFontAction extends AbstractJEditAction
    {
        /**
         * Creates a SetFontAction.
         * 
         * @param area Target textarea.
         */
        public SetFontAction(JEditTextArea area)
        {
            super("Set font..", area);
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            final Font originalFont = area_.getPainter().getFont();
            
            // Find parent frame
            Window w = SwingUtilities.getWindowAncestor(area_);

            if (w == null)
                w = new Frame();

            JFontChooserDialog fontChooser = null;
                        
            if (w instanceof Frame)
                fontChooser = new JFontChooserDialog(
                    (Frame) w, false, originalFont, area_.isAntiAliased());
                    
            else if (w instanceof Dialog)
                fontChooser = new JFontChooserDialog(
                    (Dialog) w, false, originalFont, area_.isAntiAliased());
            
            // Listener for font chooser dialog events
                
            fontChooser.addFontDialogListener(new IFontChooserDialogListener()
            {
                public void okButtonPressed(JFontChooser chooser)
                {
                    try
                    {
                        area_.getPainter().setFont(
                            chooser.getSelectedFont());
                            
                        area_.setAntiAliased(chooser.isAntiAliased());    
                    }
                    catch (FontChooserException fce)
                    {
                        ExceptionUtil.handleUI(fce, logger_);
                    }
                }

                public void cancelButtonPressed(JFontChooser chooser)
                {
                    // Just restore the original font
                    area_.getPainter().setFont(originalFont);
                }

                public void applyButtonPressed(JFontChooser chooser)
                {
                    // Same as OK
                    okButtonPressed(chooser);
                }
            });

            SwingUtil.centerWindow(w, fontChooser);                        
            fontChooser.setVisible(true);            
        }
    }

    //--------------------------------------------------------------------------
    // CopyAction
    //--------------------------------------------------------------------------
    
    /**
     * Copies the contents of the currently selected indices to the clipboard.
     */    
    static class CopyAction extends AbstractJEditAction
    {
        /**
         * Creates a CopyAction.
         * 
         * @param area Target text area.
         */
        public CopyAction(JEditTextArea area)
        {
            super("Copy", ImageCache.getIcon(ImageCache.IMAGE_COPY), area);
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            area_.copy();
        }
    }

    //--------------------------------------------------------------------------
    // CutAction
    //--------------------------------------------------------------------------
    
    /**
     * Cuts the contents of the currently selected indices.
     */    
    static class CutAction extends AbstractJEditAction
    {
        /**
         * Creates a CutAction.
         * 
         * @param area Target text area.
         */
        public CutAction(JEditTextArea area)
        {
            super("Cut", area);
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            area_.cut();
        }
    }

    //--------------------------------------------------------------------------
    // PasteAction
    //--------------------------------------------------------------------------
    
    /**
     * Pastes the contents of the clipboard into the text component.
     */    
    static class PasteAction extends AbstractJEditAction
    {
        /**
         * Creates a PasteAction.
         * 
         * @param area Target text area.
         */
        public PasteAction(JEditTextArea area)
        {
            super("Paste", ImageCache.getIcon(ImageCache.IMAGE_PASTE), area);
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            area_.paste();
        }
    }
    
    //--------------------------------------------------------------------------
    // SelectAllAction
    //--------------------------------------------------------------------------
    
    /**
     * Selects the entire contents of the textarea. 
     */
    static class SelectAllAction extends AbstractJEditAction
    {
        /**
         * Creates a SelectAllAction.
         * 
         * @param area Target text area.
         */
        public SelectAllAction(JEditTextArea area)
        {
            super("Select All", area);
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            area_.selectAll();
        }
    }
    
    //--------------------------------------------------------------------------
    // ClearAction
    //--------------------------------------------------------------------------
    
    /**
     * Clears the contents of the text area. 
     */
    public static class ClearAction extends AbstractJEditAction
    {
        /**
         * Creates a ClearAction.
         * 
         * @param area Target text area.
         */
        public ClearAction(JEditTextArea area)
        {
            super("Clear", ImageCache.getIcon(ImageCache.IMAGE_CLEAR), area);
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            area_.setText("");
        }
    }
}