package toolbox.util.ui;

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
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
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
        
    /** Text component to associate this popup menu with */
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
        addSeparator();
        add(new JMenuItem(new SetFontAction()));
        add(new JMenuItem(new FindAction(textComponent_)));
        add(new JMenuItem(new InsertFileAction(textComponent_)));
        add(new JMenuItem(new SaveAsAction(textComponent_)));
        
        textComponent_.addMouseListener(new JPopupListener(this));
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
    
    /**
     * Triggers activation of the Find Dialog box
     */    
    protected class FindAction extends AbstractAction
    {
        public FindAction(JTextComponent textComp)
        {
            super("Find..");
            final JTextComponent finalTextComp = textComp;
            
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
            JFindDialog findDialog = 
                new JFindDialog(new SearchInitiator(textComponent_));
                
            findDialog.setVisible(true);
        }
    }
    
    /**
     * Inserts the text of a file at the currnet cursor location
     */
    protected static class InsertFileAction extends AbstractAction
    {
        private static File lastDir_;
        private JTextComponent jtc_;
        
        public InsertFileAction(JTextComponent jtc)
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
        private JTextComponent jtc_;
        
        public SaveAsAction(JTextComponent jtc)
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

    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Search initiator for JTextComponents
     */    
    class SearchInitiator implements JFindDialog.SearchInitiator
    {
        private JTextComponent jtc_;
        
        public SearchInitiator(JTextComponent jtc)
        {
            jtc_ = jtc;
        }
        
        public Frame getFrame()
        {
            return SwingUtil.getFrameAncestor(jtc_);
        }

        public String getSearchString()
        {
            return "";
        }

        public String getText()
        {
            return jtc_.getText();
        }

        public void selectText(int start, int end)
        {
            jtc_.setSelectionStart(start);
            jtc_.setSelectionEnd(end);
        }
    }
}