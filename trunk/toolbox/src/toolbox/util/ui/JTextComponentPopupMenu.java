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
public class JTextComponentPopupMenu extends JSmartPopupMenu
{
    private static final Logger logger_ =
        Logger.getLogger(JTextComponentPopupMenu.class); 
        
    /** 
     * Text component to associate this popup menu with 
     */
    private JTextComponent textComponent_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JTextComponentPopupMenu.
     */
    public JTextComponentPopupMenu()
    {
    }
    
    
    /**
     * Creates a JTextComponentPopupMenu with an associated text component.
     * 
     * @param textComponent JTextComponent to add popup to
     */
    public JTextComponentPopupMenu(JTextComponent textComponent)
    {
        textComponent_ = textComponent;
        buildView();
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Builds popupmenu and adds mouse listener to listbox.
     */
    protected void buildView()
    {
        add(new JSmartMenuItem(new CopyAction()));
        add(new JSmartMenuItem(new PasteAction()));
        add(new JSmartMenuItem(new SelectAllAction()));
        addSeparator();
        add(new JSmartMenuItem(new SetFontAction()));
        add(new JSmartMenuItem(new FindAction(textComponent_)));
        add(new JSmartMenuItem(new InsertFileAction(textComponent_)));
        add(new JSmartMenuItem(new SaveAsAction(textComponent_)));
        
        textComponent_.addMouseListener(new JPopupListener(this));
    }
    
    //--------------------------------------------------------------------------
    // CopyAction
    //--------------------------------------------------------------------------

    /**
     * Copies the contents of the currently selected indices to the clipboard.
     */    
    class CopyAction extends AbstractAction
    {
        /**
         * Creates a CopyAction.
         */
        CopyAction()
        {
            super("Copy");
            putValue(Action.MNEMONIC_KEY, new Integer('C'));
            putValue(Action.SMALL_ICON, 
                ImageCache.getIcon(ImageCache.IMAGE_COPY));
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            textComponent_.copy();
        }
    }

    //--------------------------------------------------------------------------
    // PasteAction
    //--------------------------------------------------------------------------

    /**
     * Pastes the contents of the clipboard into the text component.
     */    
    class PasteAction extends AbstractAction
    {
        /**
         * Creates a PasteAction.
         */
        PasteAction()
        {
            super("Paste");
            putValue(Action.MNEMONIC_KEY, new Integer('P'));
            putValue(Action.SMALL_ICON, 
                ImageCache.getIcon(ImageCache.IMAGE_PASTE));
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            textComponent_.paste();
        }
    }
    
    //--------------------------------------------------------------------------
    // SelectAllAction
    //--------------------------------------------------------------------------
    
    /**
     * Selects all items in the list box.
     */
    class SelectAllAction extends AbstractAction
    {
        /**
         * Creates a SelectAllAction.
         */
        SelectAllAction()
        {
            super("Select All");
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            textComponent_.selectAll();
        }
    }
    
    //--------------------------------------------------------------------------
    // SetFontAction
    //--------------------------------------------------------------------------
    
    /**
     * Sets the font in the text component.
     */
    class SetFontAction extends AbstractAction
    {
        /**
         * Creates a SetFontAction.
         */
        SetFontAction()
        {
            super("Set font..");
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            final Font originalFont = textComponent_.getFont();

            // Find parent frame
            Window w = SwingUtilities.getWindowAncestor(textComponent_);
            
            Frame frame = 
                (w != null && w instanceof Frame) ? (Frame) w : new Frame();
            
            boolean antialias = 
                textComponent_ instanceof AntiAliased ?
                    ((AntiAliased) textComponent_).isAntiAliased() : false;

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
                            ((AntiAliased) textComponent_).setAntiAliased(
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
    
    //--------------------------------------------------------------------------
    // FindAction
    //--------------------------------------------------------------------------
    
    /**
     * Triggers activation of the Find Dialog box.
     */    
    class FindAction extends AbstractAction
    {
        /**
         * Creates a FindAction.
         * 
         * @param textComp Component to search.
         */
        FindAction(JTextComponent textComp)
        {
            super("Find..");
            putValue(Action.MNEMONIC_KEY, new Integer('F'));
            putValue(Action.SMALL_ICON, 
                ImageCache.getIcon(ImageCache.IMAGE_FIND));
            
            final JTextComponent finalTextComp = textComp;
            
            // Bind Ctrl-F to activate the find action
            textComp.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent e)
                {
                    if ((e.getKeyChar() == 6) &&  // F = 6th letter in alphabet
                        ((KeyEvent.getKeyModifiersText(
                            e.getModifiers()).equals("Ctrl"))))
                            actionPerformed(
                                new ActionEvent(finalTextComp, 0, ""));
                }
            });
        }

        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            JFindDialog findDialog = 
                new JFindDialog(new SearchInitiator(textComponent_));
                
            findDialog.setVisible(true);
        }
    }
    
    //--------------------------------------------------------------------------
    // InsertFileAction
    //--------------------------------------------------------------------------

    /**
     * Inserts the text of a file at the currnet cursor location.
     */
    static class InsertFileAction extends SmartAction
    {
        private static File lastDir_;
        private JTextComponent jtc_;
        
        /**
         * Creates a InsertFileAction.
         * 
         * @param jtc Text component to insert to.
         */
        InsertFileAction(JTextComponent jtc)
        {
            super("Insert..", true, false, null);
            jtc_ = jtc;
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
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
    
    //--------------------------------------------------------------------------
    // SaveAsAction
    //--------------------------------------------------------------------------

    /**
     * Inserts the text of a file at the currnet cursor location.
     */
    static class SaveAsAction extends SmartAction
    {
        private static File lastDir_;
        private JTextComponent jtc_;
        
        /**
         * Creates a SaveAsAction.
         * 
         * @param jtc Text component with contents to save.
         */
        SaveAsAction(JTextComponent jtc)
        {
            super("Save As..", true, false, null);
            putValue(Action.MNEMONIC_KEY, new Integer('A'));
            putValue(Action.SMALL_ICON, 
                ImageCache.getIcon(ImageCache.IMAGE_SAVEAS));
            jtc_ = jtc;
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
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
    // SearchInitiator
    //--------------------------------------------------------------------------
    
    /**
     * Search initiator for JTextComponents.
     */    
    class SearchInitiator implements JFindDialog.SearchInitiator
    {
        private JTextComponent jtc_;
        
        /**
         * Creates a SearchInitiator.
         * 
         * @param jtc Textcomponent to search.
         */
        public SearchInitiator(JTextComponent jtc)
        {
            jtc_ = jtc;
        }

        
        /**
         * @see toolbox.util.ui.JFindDialog.SearchInitiator#getFrame()
         */
        public Frame getFrame()
        {
            return SwingUtil.getFrameAncestor(jtc_);
        }


        /**
         * @see toolbox.util.ui.JFindDialog.SearchInitiator#getSearchString()
         */
        public String getSearchString()
        {
            return "";
        }


        /**
         * @see toolbox.util.ui.JFindDialog.SearchInitiator#getText()
         */
        public String getText()
        {
            return jtc_.getText();
        }


        /**
         * @see toolbox.util.ui.JFindDialog.SearchInitiator#selectText(int, int)
         */
        public void selectText(int start, int end)
        {
            jtc_.setSelectionStart(start);
            jtc_.setSelectionEnd(end);
        }
    }
}