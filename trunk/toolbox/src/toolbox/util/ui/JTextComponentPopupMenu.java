package toolbox.util.ui;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
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
        
    /** 
     * Text component to associate this popup menu with 
     */
    private JTextComponent textComponent_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JTextComponentPopupMenu
     */
    public JTextComponentPopupMenu()
    {
    }
    
    /**
     * Creates a JTextComponentPopupMenu with an associated text component.
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
    class CopyAction extends AbstractAction
    {
        CopyAction()
        {
            super("Copy");
            putValue(Action.MNEMONIC_KEY, new Integer('C'));
            putValue(Action.SMALL_ICON, 
                ImageCache.getIcon(ImageCache.IMAGE_COPY));
        }
        
        public void actionPerformed(ActionEvent e)
        {
            textComponent_.copy();
        }
    }

    /**
     * Pastes the contents of the clipboard into the text component
     */    
    class PasteAction extends AbstractAction
    {
        PasteAction()
        {
            super("Paste");
            putValue(Action.MNEMONIC_KEY, new Integer('P'));
            putValue(Action.SMALL_ICON, 
                ImageCache.getIcon(ImageCache.IMAGE_PASTE));
        }
        
        public void actionPerformed(ActionEvent e)
        {
            textComponent_.paste();
        }
    }
    
    /**
     * Selects all items in the list box 
     */
    class SelectAllAction extends AbstractAction
    {
        SelectAllAction()
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
    class SetFontAction extends AbstractAction
    {
        SetFontAction()
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
            
            boolean antialias = 
                textComponent_ instanceof AntiAliased ? 
                    ((AntiAliased) textComponent_).isAntiAlias():false;

            JFontChooserDialog fontChooser =
                new JFontChooserDialog(
                    frame, false, textComponent_.getFont(), antialias);
                
            fontChooser.addFontDialogListener(new IFontChooserDialogListener()
            {
                public void okButtonPressed(JFontChooser fontChooser)
                {
                    try
                    {
                        // Set the newly selected font
                        textComponent_.setFont(fontChooser.getSelectedFont());

                        if (textComponent_ instanceof AntiAliased)
                        {
                            ((AntiAliased) textComponent_).setAntiAlias(
                                fontChooser.isAntiAlias());
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
                    textComponent_.setFont(originalFont);
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
    
    /**
     * Triggers activation of the Find Dialog box
     */    
    class FindAction extends AbstractAction
    {
        FindAction(JTextComponent textComp)
        {
            super("Find..");
            putValue(Action.MNEMONIC_KEY, new Integer('F'));
            putValue(Action.SMALL_ICON, 
                ImageCache.getIcon(ImageCache.IMAGE_FIND));
            
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
    static class InsertFileAction extends SmartAction
    {
        private static File lastDir_;
        private JTextComponent jtc_;
        
        InsertFileAction(JTextComponent jtc)
        {
            super("Insert..", true, false, null);
            jtc_ = jtc;
        }
        
        public void runAction(ActionEvent e) throws Exception
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
    }
    
    /**
     * Inserts the text of a file at the currnet cursor location
     */
    static class SaveAsAction extends SmartAction
    {
        private static File lastDir_;
        private JTextComponent jtc_;
        
        SaveAsAction(JTextComponent jtc)
        {
            super("Save As..", true, false, null);
            putValue(Action.MNEMONIC_KEY, new Integer('A'));
            putValue(Action.SMALL_ICON, 
                ImageCache.getIcon(ImageCache.IMAGE_SAVEAS));
            jtc_ = jtc;
        }
        
        public void runAction(ActionEvent e) throws Exception
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