package toolbox.jedit;

import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JFindDialog;
import toolbox.util.ui.font.FontChooserException;
import toolbox.util.ui.font.IFontChooserDialogListener;
import toolbox.util.ui.font.JFontChooser;
import toolbox.util.ui.font.JFontChooserDialog;

/**
 * UI Actions for the JEditTextArea
 */
public final class JEditActions
{
    private static final Logger logger_ = Logger.getLogger(JEditActions.class);
    
    //--------------------------------------------------------------------------
    // JEditAction
    //--------------------------------------------------------------------------
    
    /**
     * Abstract class for all JEdit actions. 
     */
    abstract static class JEditAction extends AbstractAction
    {
        protected JEditTextArea area_;
        
        /**
         * Creates a JEditAction.
         * 
         * @param area Target text area.
         */
        public JEditAction(JEditTextArea area)
        {
            area_ = area;
        }
        
        
        /**
         * Creates a JEditAction.
         * 
         * @param label Action label.
         * @param area Target text area.
         */
        public JEditAction(String label, JEditTextArea area)
        {
            super(label);
            area_ = area;
        }
    }

    //--------------------------------------------------------------------------
    // FindAction
    //--------------------------------------------------------------------------
    
    /**
     * Triggers activation of the Find Dialog box.
     */    
    static class FindAction extends JEditAction
    {
        /**
         * Creates a FindAction.
         * 
         * @param textComp Target textarea.
         */
        public FindAction(JEditTextArea textComp)
        {
            super("Find..", textComp);
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
    // SaveAsAction
    //--------------------------------------------------------------------------
    
    /**
     * Saves the contents of the text area to a file.
     */
    static class SaveAsAction extends JEditAction
    {
        private File lastDir_;
        
        /**
         * Creates a SaveAsAction.
         * 
         * @param area Target text area.
         */
        public SaveAsAction(JEditTextArea area)
        {
            super("Save As..", area);
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                JFileChooser chooser = null;
                
                if (lastDir_ == null)
                    chooser = new JFileChooser();
                else
                    chooser = new JFileChooser(lastDir_);

                if (chooser.showSaveDialog(area_)
                    == JFileChooser.APPROVE_OPTION) 
                {
                    String saveFile = 
                        chooser.getSelectedFile().getCanonicalPath();
                        
                    FileUtil.setFileContents(saveFile, area_.getText(), false);
                }
                
                lastDir_ = chooser.getCurrentDirectory();
            }
            catch (FileNotFoundException fnfe)
            {
                ExceptionUtil.handleUI(fnfe, logger_);
            }
            catch (IOException ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }
    }    

    //--------------------------------------------------------------------------
    // InsertFileAction
    //--------------------------------------------------------------------------
    
    /**
     * Inserts the text of a file at the currnet cursor location.
     */
    static class InsertFileAction extends JEditAction
    {
        private File lastDir_;
        
        /**
         * Creates a InsertFileAction.
         * 
         * @param area Target textarea.
         */
        public InsertFileAction(JEditTextArea area)
        {
            super("Insert..", area);
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                JFileChooser chooser = null;
                
                if (lastDir_ == null)
                    chooser = new JFileChooser();
                else
                    chooser = new JFileChooser(lastDir_);

                if (chooser.showOpenDialog(area_) == 
                    JFileChooser.APPROVE_OPTION) 
                {
                    String txt = FileUtil.getFileContents(
                        chooser.getSelectedFile().getCanonicalPath());
                    
                    int curPos = area_.getCaretPosition();    
                    
                    area_.getDocument().
                        insertString(curPos, txt, null);                        
                }
                
                lastDir_ = chooser.getCurrentDirectory();
            }
            catch (BadLocationException ble)
            {
                ExceptionUtil.handleUI(ble, logger_);
            }
            catch (FileNotFoundException fnfe)
            {
                ExceptionUtil.handleUI(fnfe, logger_);
            }
            catch (IOException ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }
    }

    //--------------------------------------------------------------------------
    // SetFontAction
    //--------------------------------------------------------------------------
    
    /**
     * Sets the font in the text component.
     */
    static class SetFontAction extends JEditAction
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
            
            /* Listener for font chooser dialog events */
                
            fontChooser.addFontDialogListener(new IFontChooserDialogListener()
            {
                public void okButtonPressed(JFontChooser fontChooser)
                {
                    try
                    {
                        area_.getPainter().setFont(
                            fontChooser.getSelectedFont());
                            
                        area_.setAntiAliased(fontChooser.isAntiAliased());    
                    }
                    catch (FontChooserException fce)
                    {
                        ExceptionUtil.handleUI(fce, logger_);
                    }
                }

                public void cancelButtonPressed(JFontChooser fontChooser)
                {
                    // Just restore the original font
                    area_.getPainter().setFont(originalFont);
                }

                public void applyButtonPressed(JFontChooser fontChooser)
                {
                    // Same as OK
                    okButtonPressed(fontChooser);
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
    static class CopyAction extends JEditAction
    {
        /**
         * Creates a CopyAction.
         * 
         * @param area Target text area.
         */
        public CopyAction(JEditTextArea area)
        {
            super("Copy", area);
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
    static class CutAction extends JEditAction
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
    static class PasteAction extends JEditAction
    {
        /**
         * Creates a PasteAction.
         * 
         * @param area Target text area.
         */
        public PasteAction(JEditTextArea area)
        {
            super("Paste", area);
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
    static class SelectAllAction extends JEditAction
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
}