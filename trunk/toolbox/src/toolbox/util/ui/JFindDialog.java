package toolbox.util.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;

/**
 * Simple text search find dialog that is tied to a SearchInitiator
 */
public class JFindDialog extends JDialog
{
    // TODO: Search from current cursor position
    
    private static final Logger logger_ =
        Logger.getLogger(JFindDialog.class);
   
    /** 
     * Textfield for the user to change/updatee the search string 
     */     
    private JTextField findField_;
    
    /** 
     * Search client 
     */
    private SearchInitiator initiator_;
    
    /** 
     * Used to display informative information regarding the search 
     */
    private JStatusBar status_;

    /** 
     * Most recently used search string 
     */
    private String lastSearched_;
    
    /** 
     * Index of string when last found, used to determine if conducting a new 
     * search of continuing an existing search 
     */
    private int lastFound_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a find dialog for the given search initiator
     * 
     * @param initiator  Initiator of the searchs
     */
    public JFindDialog(SearchInitiator initiator)
    {
        super(initiator.getFrame(), "Find", false);
        
        initiator_ = initiator;
        
        buildView();
        pack();
       
        SwingUtil.centerWindow(initiator.getFrame(), this);
        
        lastFound_ = 0;
        lastSearched_ = "";
    }
    
    //--------------------------------------------------------------------------
    // Protected 
    //--------------------------------------------------------------------------
    
    /**
     * Builds the view of the find dialog
     */
    protected void buildView()
    {
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        
        JPanel findPanel = new JPanel(new FlowLayout());
        findPanel.add(new JLabel("Find"));
        findPanel.add(findField_ = new JTextField(15));
        findField_.addActionListener(new FindAction());
        c.add(BorderLayout.NORTH, findPanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        findPanel.add(new JButton(new FindAction()));
        findPanel.add(new JButton(new CancelAction()));
        c.add(BorderLayout.CENTER, buttonPanel);
        
        c.add(BorderLayout.SOUTH, status_ = new JStatusBar());

        // Bind ESC to the CancelAction
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW ).
            put(KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "escPressed");
            
        getRootPane().getActionMap().put("escPressed", new CancelAction());
        
    }
    
    //--------------------------------------------------------------------------
    // Actions 
    //--------------------------------------------------------------------------
    
    /**
     * Kicks of text search. A successful search will result in the  found text 
     * being selected and visible in the text component's  viewport. A failure 
     * will be indicated on the status bar in the find dialog.
     */
    class FindAction extends AbstractAction
    {
        public FindAction()
        {
            super("Find Next");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            String searchFor = findField_.getText();
            
            if (!StringUtil.isNullOrEmpty(searchFor))
            {
                String text = initiator_.getText();
                
                int start = 0;
                
                if (searchFor.equals(lastSearched_))
                    start = lastFound_+1;
                else
                    lastSearched_ = searchFor;
                
                int found = text.indexOf(searchFor, start);
                
                if (found >= 0)
                {
                    initiator_.selectText(found, found + searchFor.length());
                    lastFound_ = found;
                    status_.setStatus("Found at position " + found);
                }
                else
                {
                    status_.setStatus("String not found.");
                }
            }
            else
            {
                status_.setStatus("Enter a valid search string.");    
            }
        }
    }
    
    /**
     * Disposes of the find dialog when dismissed
     */
    class CancelAction extends AbstractAction
    {
        public CancelAction()
        {
            super("Cancel");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            dispose();
        }
    }
    
    //--------------------------------------------------------------------------
    // Interfaces
    //--------------------------------------------------------------------------

    /**
     * Search client must implement this interface
     */    
    public interface SearchInitiator
    {
        /**
         * @return Text to search for
         */
        public String getSearchString();
        
        /**
         * @return Text to search within
         */
        public String getText();
        
        /**
         * Selects the text after it has been found
         * 
         * @param start Starting index of selection
         * @param end   Ending index of selection
         */
        public void selectText(int start, int end);
        
        /**
         * @return  Frame that the search initiator is located in.
         */
        public Frame getFrame();
    }
}