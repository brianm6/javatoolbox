package toolbox.jedit;

import java.awt.Frame;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JFindDialog;

/**
 * JEdit specific SearchInitiator which allows text searches of the contents
 * of the text area.
 */
public class JEditSearchInitiator implements JFindDialog.SearchInitiator
{
    /**
     * String to search for
     */
    private String searchString_;
    
    /**
     * Search text container
     */
    private JEditTextArea jeta_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JEditSearchInitiator
     * 
     * @param  searchString  String to search for
     * @param  jeta          JEdit text area to search
     */
    public JEditSearchInitiator(String searchString, JEditTextArea jeta)
    {
        searchString_ = searchString;
        jeta_ = jeta;
    }

    //--------------------------------------------------------------------------
    // JFindDialog.SearchInitiator Interface
    //--------------------------------------------------------------------------
        
    /**
     * @see toolbox.util.ui.JFindDialog.SearchInitiator#getSearchString()
     */
    public String getSearchString()
    {
        return searchString_;
    }

    /**
     * @see toolbox.util.ui.JFindDialog.SearchInitiator#getText()
     */
    public String getText()
    {
        return jeta_.getText();
    }

    /**
     * @see toolbox.util.ui.JFindDialog.SearchInitiator#selectText(int, int)
     */
    public void selectText(int start, int end)
    {
        jeta_.select(start, end);
    }
    
    /**
     * @see toolbox.util.ui.JFindDialog.SearchInitiator#getFrame()
     */
    public Frame getFrame()
    {
        return SwingUtil.getFrameAncestor(jeta_);
    }
}