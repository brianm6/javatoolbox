package toolbox.jtail;

import javax.swing.JTabbedPane;

/**
 * enclosing_type
 */
public class JTailTabbedPane extends JTabbedPane
{
    /**
     * Constructor for JTailTabbedPane.
     */
    public JTailTabbedPane()
    {
        super();
    }

    /**
     * Constructor for JTailTabbedPane.
     * @param tabPlacement
     */
    public JTailTabbedPane(int tabPlacement)
    {
        super(tabPlacement);
    }
    
    public class TailPaneListener implements TailPane.ITailPaneListener
    {
        /**
         * @see toolbox.jtail.TailPane.ITailPaneListener#newDataAvailable(TailPane)
         */
        public void newDataAvailable(TailPane tailPane)
        {
            int index = indexOfComponent(tailPane);
            setTitleAt(index, "* "+ getTitleAt(index));
        }
    }
}
