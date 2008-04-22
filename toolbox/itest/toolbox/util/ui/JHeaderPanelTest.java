package toolbox.util.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.FontUtil;
import toolbox.util.RandomUtil;

/**
 * Unit test for {@link toolbox.util.ui.JHeaderPanel}.
 */
public class JHeaderPanelTest extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(JHeaderPanelTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args) 
    {
        TestRunner.run(JHeaderPanelTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
    
    /**
     * Tests single header panel.
     * 
     * @throws Exception on error.
     */
    public void testHeaderPanel() throws Exception
    {
        logger_.info("Running testHeaderPanel...");
        
        JPanel cp = new JPanel(new BorderLayout());
        JHeaderPanel hp = new JCollapsablePanel("Collapsable Header Panel");

        JSmartLabel content = new JSmartLabel(
            "Header Panel Content", 
            Color.red, 
            Color.white);
        
        content.setHorizontalAlignment(SwingConstants.CENTER);
        content.setFont(FontUtil.grow(content.getFont(), 20));
        hp.setContent(content);
        
        cp.add(hp, BorderLayout.NORTH);
        cp.add(createPropertySheet(hp), BorderLayout.CENTER);
        launchInDialog(cp, SCREEN_ONE_THIRD);
    }
    
    
    /**
     * Tests a bunch of stacked header panels.
     * 
     * @throws Exception on error.
     */
    public void testHeaderPanelStacked() throws Exception
    {
        logger_.info("Running testHeaderPanelStacked...");
        
        JPanel wrapper = new JPanel(new BorderLayout());
        
        final GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel container = new JPanel(gbl);
        
        for (int i = 0; i < 5; i++)
        {
            JCollapsablePanel cp = createCollapsablePanel("Box " + i);
            
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.fill  = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.NORTH;
            gbc.gridheight = 1;
            gbc.gridwidth = 1;
            gbc.weightx = 1;
            gbc.weighty = cp.isCollapsed() ? 1 : 0;
            
            cp.addPropertyChangeListener(new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent evt)
                {
                    JCollapsablePanel source = 
                        (JCollapsablePanel) evt.getSource();
                    
                    GridBagConstraints gbc2 = gbl.getConstraints(source);
                    gbc2.weighty = (source.isCollapsed() ? 1 : 0);
                    gbl.setConstraints(source, gbc2);
                }
            });
            
            container.add(cp, gbc);
        }

        wrapper.add(container, BorderLayout.NORTH);
        launchInDialog(wrapper, SCREEN_TWO_THIRDS);
    }

    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------
    
    protected JCollapsablePanel createCollapsablePanel(String caption)
    {
        JCollapsablePanel hp = new JCollapsablePanel(caption);

        JSmartLabel content = new JSmartLabel(
            caption, 
            Color.red, 
            Color.white);
        
        content.setHorizontalAlignment(SwingConstants.CENTER);
        
        content.setFont(
            FontUtil.grow(content.getFont(), RandomUtil.nextInt(5, 60)));
        
        hp.setContent(content);
        return hp;
    }
}