package toolbox.util.ui.textarea.action;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.text.JTextComponent;

import toolbox.util.SwingUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JFindDialog;

/**
 * Launches the Find dialog used to search for text.
 */    
public class FindAction extends AbstractTextComponentAction
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a FindAction.
     * 
     * @param textComponent Component to search.
     */
    public FindAction(final JTextComponent textComponent)
    {
        super(textComponent, "Find..");
        putValue(Action.MNEMONIC_KEY, new Integer('F'));
        putValue(Action.SMALL_ICON, ImageCache.getIcon(ImageCache.IMAGE_FIND));
        
        // Bind Ctrl-F to activate the find action
        textComponent.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                if ((e.getKeyChar() == 6) &&  // F = 6th letter in alphabet
                    ((KeyEvent.getKeyModifiersText(
                        e.getModifiers()).equals("Ctrl"))))
                        actionPerformed(
                            new ActionEvent(textComponent, 0, ""));
            }
        });
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
        JFindDialog findDialog = 
            new JFindDialog(new SearchInitiator(getTextComponent()));
            
        findDialog.setVisible(true);
    }
    
    //--------------------------------------------------------------------------
    // SearchInitiator
    //--------------------------------------------------------------------------
    
    /**
     * Search initiator for JTextComponents.
     */    
    class SearchInitiator implements JFindDialog.SearchInitiator
    {
        /**
         * Text component that initiated the search.
         */
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