/*
 * Created on Feb 9, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package toolbox.util.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

/**
 * @author analogue
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class JCollapsablePanel extends JHeaderPanel
{
    private static final Logger logger_ = 
        Logger.getLogger(JCollapsablePanel.class);
    
    private Component savedContent_;
    
    /**
     * @param title
     */
    public JCollapsablePanel(String title)
    {
        super(title);
        makeCollapsable();
    }


    /**
     * 
     */
    private void makeCollapsable()
    {
        
        JToggleButton toggle = 
            createToggleButton(
                ImageCache.getIcon(ImageCache.IMAGE_TRIANGLE),
                "Collapse", 
                new CollapseAction());
        
        JToolBar tb = createToolBar();
        tb.add(toggle);
        setToolBar(tb);
    }

    class CollapseAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            if (getContent() instanceof NullComponent)
            {
                logger_.debug("Expanding...");
                setContent(savedContent_);
            }
            else
            {
                logger_.debug("Collapsing...");
                savedContent_ = getContent();
                setContent(new NullComponent());
            }
            
            revalidate();
            ((JComponent) getParent()).revalidate();
        }
    }

    class NullComponent extends Component
    {
//        public Dimension getPreferredSize()
//        {
//            return new Dimension(0,0);
//        }
    }
    
    /**
     * @param icon
     * @param title
     */
    public JCollapsablePanel(Icon icon, String title)
    {
        super(icon, title);
    }


    /**
     * @param title
     * @param bar
     * @param content
     */
    public JCollapsablePanel(String title, JToolBar bar, JComponent content)
    {
        super(title, bar, content);
    }


    /**
     * @param icon
     * @param title
     * @param bar
     * @param content
     */
    public JCollapsablePanel(Icon icon, String title, JToolBar bar,
        JComponent content)
    {
        super(icon, title, bar, content);
    }

    
    
}
