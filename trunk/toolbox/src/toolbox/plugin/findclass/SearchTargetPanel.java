package toolbox.plugin.findclass;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import toolbox.util.ClassUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JPopupListener;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.JSmartPopupMenu;
import toolbox.util.ui.list.JSmartList;

/**
 * Search targets panel.
 */
public class SearchTargetPanel extends JHeaderPanel
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * List containing the jars/directories that are included in the search.
     */
    private JList searchList_;

    /**
     * Data model for the list of search targets.
     */
    private DefaultListModel searchListModel_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    public SearchTargetPanel()
    {
        super("Search Targets");
        
        //
        // addSearchTarget() is called before buildView() so we have to 
        // instantiate the list a little early.
        //
        searchListModel_ = new DefaultListModel();
        searchList_ = new JSmartList(searchListModel_);
        
        buildView();
    }
    
    //--------------------------------------------------------------------------
    // Public 
    //--------------------------------------------------------------------------
    
    /**
     * Adds an archive or directory to the search target list.
     * 
     * @param target Absolute name of archive or directory.
     */
    public void addSearchTarget(String target)
    {
        searchListModel_.addElement(target);
    }
    
    
    /**
     * Selects the given search target in the search list.
     * 
     * @param target Search target to select which is an archive or directory.
     */
    public void selectSearchTarget(String target)
    {
        searchList_.setSelectedValue(target, true);
    }
    
    
    /**
     * Returns a list of the search targets in the intended search order.
     * 
     * @return String[] of archive or directory names.
     */
    public Object[] getSearchTargets()
    {
        return (Object[]) searchListModel_.toArray();
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Builds the Classpath panel which shows all paths/archives that have been
     * targeted for the current search.
     */
    protected void buildView()
    {
        //
        // Popup menu activated by right clicking on the search target list.
        // Allows the user to clear the list or remove individual entries.
        //
        JPopupMenu searchPopup = new JSmartPopupMenu();
        searchPopup.add(new JSmartMenuItem(new ClearTargetsAction()));
        searchPopup.add(new JSmartMenuItem(new RemoveTargetsAction()));
        searchPopup.add(new JSmartMenuItem(new AddClasspathTargetAction()));

        searchList_.addMouseListener(new JPopupListener(searchPopup));

        JToolBar tb = JHeaderPanel.createToolBar();
        tb.add(JHeaderPanel.createButton(
            ImageCache.getIcon(ImageCache.IMAGE_TABLES),
            "Add classpath",
            new AddClasspathTargetAction()));

        tb.add(JHeaderPanel.createButton(
            ImageCache.getIcon(ImageCache.IMAGE_DELETE),
            "Remove search target",
            new RemoveTargetsAction()));

        tb.add(JHeaderPanel.createButton(
            ImageCache.getIcon(ImageCache.IMAGE_CLEAR),
            "Clear",
            new ClearTargetsAction()));

        setToolBar(tb);
        setContent(new JScrollPane(searchList_));
    }

    //--------------------------------------------------------------------------
    // ClearTargetsAction
    //--------------------------------------------------------------------------

    /**
     * Clears all entries in the search list.
     */
    class ClearTargetsAction extends AbstractAction
    {
        /**
         * Creates a ClearTargetsAction.
         */
        ClearTargetsAction()
        {
            super("Clear");
            putValue(MNEMONIC_KEY, new Integer('C'));
            putValue(SHORT_DESCRIPTION, "Clears search targets");
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            searchListModel_.removeAllElements();
        }
    }

    //--------------------------------------------------------------------------
    // AddClasspathTargetAction
    //--------------------------------------------------------------------------

    /**
     * Adds the current classpath to the search list.
     */
    class AddClasspathTargetAction extends AbstractAction
    {
        /**
         * Creates a AddClasspathTargetAction.
         */
        AddClasspathTargetAction()
        {
            super("Add Classpath");
            putValue(MNEMONIC_KEY, new Integer('A'));
            putValue(SHORT_DESCRIPTION,
                "Adds the current classpath to the search list");
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            String[] cp = ClassUtil.getClassPathElements();

            for (int i = 0; i < cp.length; 
            	searchListModel_.addElement(cp[i++]));
        }
    }

    //--------------------------------------------------------------------------
    // RemoteTargetsAction
    //--------------------------------------------------------------------------

    /**
     * Removes selected target entries from the search list.
     */
    class RemoveTargetsAction extends AbstractAction
    {
        /**
         * Creates a RemoveTargetsAction.
         */
        RemoveTargetsAction()
        {
            super("Remove");
            putValue(MNEMONIC_KEY, new Integer('R'));
            putValue(SHORT_DESCRIPTION, "Removes search targets");
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            Object[] selected = searchList_.getSelectedValues();

            for (int i = 0; i < selected.length; i++)
                searchListModel_.removeElement(selected[i]);
        }
    }
}