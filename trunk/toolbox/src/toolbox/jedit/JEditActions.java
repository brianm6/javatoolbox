package toolbox.jedit;

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

public final class JEditActions
{
    static final Logger logger_ = Logger.getLogger(JEditActions.class);
    
    /**
     * Abstract class for all JEdit actions 
     */
    abstract static class JEditAction extends AbstractAction
    {
        JEditTextArea area_;
        
        public JEditAction(JEditTextArea area)
        {
            area_ = area;
        }
        
        public JEditAction(String label, JEditTextArea area)
        {
            super(label);
            area_ = area;
        }
    }

    /**
     * Triggers activation of the Find Dialog box
     */    
    static class FindAction extends JEditAction
    {
        public FindAction(JEditTextArea textComp)
        {
            super("Find..", textComp);
        }
        
        public void actionPerformed(ActionEvent e)
        {
            JFindDialog.SearchInitiator initiator = 
                new JEditSearchInitiator("", area_);
                
            JFindDialog findDialog = new JFindDialog(initiator);
            findDialog.setVisible(true);
        }
    }

    /**
     * Inserts the text of a file at the currnet cursor location
     */
    static class SaveAsAction extends JEditAction
    {
        private File lastDir_;
        
        public SaveAsAction(JEditTextArea area)
        {
            super("Save As..", area);
        }
        
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                JFileChooser chooser = null;
                
                if (lastDir_ == null)
                    chooser = new JFileChooser();
                else
                    chooser = new JFileChooser(lastDir_);

                if (chooser.showSaveDialog(area_)==JFileChooser.APPROVE_OPTION) 
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

    /**
     * Inserts the text of a file at the currnet cursor location
     */
    static class InsertFileAction extends JEditAction
    {
        private File lastDir_;
        
        public InsertFileAction(JEditTextArea area)
        {
            super("Insert..", area);
        }
        
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

    /**
     * Sets the font in the text component
     */
    static class SetFontAction extends JEditAction
    {
        public SetFontAction(JEditTextArea area)
        {
            super("Set font..", area);
        }
        
        public void actionPerformed(ActionEvent e)
        {
            final Font originalFont = area_.getPainter().getFont();
            
            // Find parent frame
            Window w = SwingUtilities.getWindowAncestor(area_);
            
            Frame frame = 
                (w != null && w instanceof Frame) ? (Frame) w : new Frame();
            
            JFontChooserDialog fontChooser = new JFontChooserDialog(
                frame, false, originalFont);
                
            fontChooser.addFontDialogListener(new IFontChooserDialogListener()
            {
                public void okButtonPressed(JFontChooser fontChooser)
                {
                    try
                    {
                        area_.getPainter().setFont(
                            fontChooser.getSelectedFont());
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

            SwingUtil.centerWindow(frame, fontChooser);                        
            fontChooser.setVisible(true);            
        }
    }

    /**
     * Copies the contents of the currently selected indices to the clipboard
     */    
    static class CopyAction extends JEditAction
    {
        public CopyAction(JEditTextArea area)
        {
            super("Copy", area);
        }
        
        public void actionPerformed(ActionEvent e)
        {
            area_.copy();
        }
    }

    /**
     * Cuts the contents of the currently selected indices
     */    
    static class CutAction extends JEditAction
    {
        public CutAction(JEditTextArea area)
        {
            super("Cut", area);
        }
        
        public void actionPerformed(ActionEvent e)
        {
            area_.cut();
        }
    }

    /**
     * Pastes the contents of the clipboard into the text component
     */    
    static class PasteAction extends JEditAction
    {
        public PasteAction(JEditTextArea area)
        {
            super("Paste", area);
        }
        
        public void actionPerformed(ActionEvent e)
        {
            area_.paste();
        }
    }
    
    /**
     * Selects the entire contents of the textarea 
     */
    static class SelectAllAction extends JEditAction
    {
        public SelectAllAction(JEditTextArea area)
        {
            super("Select All", area);
        }
        
        public void actionPerformed(ActionEvent e)
        {
            area_.selectAll();
        }
    }

}
