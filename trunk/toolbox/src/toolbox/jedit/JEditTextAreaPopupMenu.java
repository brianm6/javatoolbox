package toolbox.jedit;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;
import org.jedit.syntax.JEditTextArea;

import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JPopupListener;
import toolbox.util.ui.font.FontChooserException;
import toolbox.util.ui.font.IFontChooserDialogListener;
import toolbox.util.ui.font.JFontChooser;
import toolbox.util.ui.font.JFontChooserDialog;

/**
 * Popup menu with commonly used functionality for JEditTextArea subclasses.
 */
public class JEditTextAreaPopupMenu extends JPopupMenu
{
    private static final Logger logger_ =
        Logger.getLogger(JEditTextAreaPopupMenu.class); 
        
    /**
     * Text component to associate this popup menu with
     */
    private JEditTextArea textArea_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Default constructor 
     */
    public JEditTextAreaPopupMenu()
    {
    }
    
    /**
     * Constructor for JEditTextAreaPopupMenu.
     * 
     * @param  textArea  JEditTextArea to add popup to
     */
    public JEditTextAreaPopupMenu(JEditTextArea textArea)
    {
        this("", textArea);
    }

    /**
     * Constructor for JEditTextAreaPopupMenu.
     * 
     * @param  label     Popupmenu label
     * @param  textArea  JEditTextArea to add popup to
     */
    public JEditTextAreaPopupMenu(String label, JEditTextArea textArea)
    {
        super(label);
        textArea_ = textArea;
        buildView();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * @param  area  Textarea
     */
    public void setTextArea(JEditTextArea area)
    {
        textArea_ = area;
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------

    /**
     * Builds popupmenu and adds mouse listener to listbox
     */
    public void buildView()
    {
        add(new JMenuItem(new CopyAction(textArea_)));
        add(new JMenuItem(new CutAction(textArea_)));
        add(new JMenuItem(new PasteAction(textArea_)));
        add(new JMenuItem(new SelectAllAction(textArea_)));
        addSeparator();
        add(new JMenuItem(new SetFontAction(textArea_)));
        add(new JMenuItem(new FindAction(textArea_)));
        add(new JMenuItem(new InsertFileAction(textArea_)));
        add(new JMenuItem(new SaveAsAction(textArea_)));
        
        textArea_.addMouseListener(new JPopupListener(this));
    }
    
    //--------------------------------------------------------------------------
    //  Actions
    //--------------------------------------------------------------------------

    /**
     * Abstract class for all JEdit actions 
     */
    protected static abstract class JEditAction extends AbstractAction
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
     * Copies the contents of the currently selected indices to the clipboard
     */    
    protected static class CopyAction extends JEditAction
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
    protected static class CutAction extends JEditAction
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
    protected static class PasteAction extends JEditAction
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
    protected static class SelectAllAction extends JEditAction
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
    
    /**
     * Sets the font in the text component
     */
    protected static class SetFontAction extends JEditAction
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
     * Triggers activation of the Find Dialog box
     */    
    protected static class FindAction extends AbstractAction
    {
        public FindAction(JEditTextArea textComp)
        {
            super("Find..");
            final JEditTextArea finalTextComp = textComp;
            
            // Bind Ctrl-F to activate the find action
            textComp.addKeyListener( new KeyAdapter()
            {
                public void keyTyped(KeyEvent e)
                {
                    if ((e.getKeyChar() == 6) &&  // F = 6th letter in alphabet
                        ((KeyEvent.getKeyModifiersText(
                            e.getModifiers()).equals("Ctrl"))))
                            actionPerformed(
                                new ActionEvent(finalTextComp, 0, "" ));
                }
            });
        }
        
        public void actionPerformed(ActionEvent e)
        {
            ExceptionUtil.handleUI(
                new IllegalArgumentException("Not supported"), logger_);
                
            //JFindDialog findDialog = new JFindDialog(textArea_);
            //findDialog.setVisible(true);
        }
    }
    
    /**
     * Inserts the text of a file at the currnet cursor location
     */
    protected static class InsertFileAction extends JEditAction
    {
        private static File lastDir_;
        
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
     * Inserts the text of a file at the currnet cursor location
     */
    protected static class SaveAsAction extends JEditAction
    {
        private static File lastDir_;
        
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
}