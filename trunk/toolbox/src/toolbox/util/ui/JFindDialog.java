package toolbox.util.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;

/**
 * Simple text search find dialog that is tied to a TextComponent
 */
class JFindDialog extends JDialog
{
    private static final Logger logger_ =
        Logger.getLogger(JFindDialog.class);
        
    private JTextField     findField_;
    private JTextComponent textComp_;
    private JStatusPane    status_;
    
    /** 
     * Index of string when last found, used to determine if conducting a new 
     * search of continuing an existing search 
     */
    private int lastFound_;
    
    /** 
     * Last used search string 
     */
    private String lastSearched_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a find dialog attached to the given text component
     * 
     * @param textComponent  Contains text to search
     */
    public JFindDialog(JTextComponent textComponent)
    {
        super(SwingUtil.getFrameAncestor(textComponent), "Find", false);
        textComp_ = textComponent;
        buildView();
        pack();
        
        SwingUtil.centerWindow(
            SwingUtil.getFrameAncestor(textComponent), this);
        
        lastFound_ = 0;
        lastSearched_ = "";
    }
    
    //--------------------------------------------------------------------------
    // Private 
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
        
        c.add(BorderLayout.SOUTH, status_ = new JStatusPane());
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
                String text = textComp_.getText();
                
                int start = 0;
                
                if (searchFor.equals(lastSearched_))
                    start = lastFound_+1;
                else
                    lastSearched_ = searchFor;
                
                int found = text.indexOf(searchFor, start);
                
                if (found >= 0)
                {
                    textComp_.select(found, found + searchFor.length());
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
     * Disposes of the find dialog
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
}