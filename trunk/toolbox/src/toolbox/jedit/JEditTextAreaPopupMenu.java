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
        add(new JMenuItem(new CopyAction()));
        add(new JMenuItem(new PasteAction()));
        add(new JMenuItem(new SelectAllAction()));
        addSeparator();
        add(new JMenuItem(new SetFontAction()));
        add(new JMenuItem(new FindAction(textArea_)));
        add(new JMenuItem(new InsertFileAction(textArea_)));
        add(new JMenuItem(new SaveAsAction(textArea_)));
        
        textArea_.addMouseListener(new JPopupListener(this));
    }
    
    //--------------------------------------------------------------------------
    //  Actions
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
            textArea_.copy();
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
            textArea_.paste();
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
            textArea_.selectAll();
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
            final Font originalFont = textArea_.getPainter().getFont();
            
            // Find parent frame
            Window w = SwingUtilities.getWindowAncestor(textArea_);
            
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
                        textArea_.getPainter().setFont(
                            fontChooser.getSelectedFont());
                    }
                    catch (FontChooserException fce)
                    {
                        logger_.error(fce);
                    }
                }

                public void cancelButtonPressed(JFontChooser fontChooser)
                {
                    // Just restore the original font
                    textArea_.getPainter().setFont(originalFont);
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
    protected class FindAction extends AbstractAction
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
    protected static class InsertFileAction extends AbstractAction
    {
        private static File lastDir_;
        private JEditTextArea jtc_;
        
        public InsertFileAction(JEditTextArea jtc)
        {
            super("Insert..");
            jtc_ = jtc;
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

                if (chooser.showOpenDialog(jtc_) == 
                    JFileChooser.APPROVE_OPTION) 
                {
                    String txt = FileUtil.getFileContents(
                        chooser.getSelectedFile().getCanonicalPath());
                    
                    int curPos = jtc_.getCaretPosition();    
                    
                    jtc_.getDocument().
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
    protected static class SaveAsAction extends AbstractAction
    {
        private static File lastDir_;
        private JEditTextArea jtc_;
        
        public SaveAsAction(JEditTextArea jtc)
        {
            super("Save As..");
            jtc_ = jtc;
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

                if (chooser.showSaveDialog(jtc_) == 
                    JFileChooser.APPROVE_OPTION) 
                {
                    String saveFile = 
                        chooser.getSelectedFile().getCanonicalPath();
                    
                    logger_.debug("save file=" + saveFile);
                    
                    FileUtil.setFileContents(saveFile, jtc_.getText(), false);
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