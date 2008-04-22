package toolbox.util.ui.table;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import junit.textui.TestRunner;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.RandomUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartToggleButton;
import toolbox.util.ui.table.action.AutoTailAction;

/**
 * Unit test for {@link toolbox.util.ui.table.JSmartTable}.
 */
public class JSmartTableTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JSmartTableTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String[] args)
    {
        TestRunner.run(JSmartTableTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests JSmartTable.
     */
    public void testJSmartTable()
    {
        logger_.info("Running testJSmartTable...");

        final DefaultTableModel model = new DefaultTableModel(0, 3);
        final JSmartTable table = new JSmartTable(model);
        JScrollPane scroller = new JScrollPane(table);
        JPanel content = new JPanel(new BorderLayout());
        content.add(scroller, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(new JSmartButton(new AbstractAction("Add Rows")
        {
            public void actionPerformed(ActionEvent e)
            {
                for (int i = 0, n = RandomUtil.nextInt(1, 10); i < n;  i++)
                {
                    model.addRow(
                        new String[]
                        {
                            RandomUtil.nextUpperAlpha() + "",
                            RandomUtil.nextUpperAlpha() + "",
                            RandomUtil.nextUpperAlpha() + ""
                        });
                }
            }
        }));

        JSmartToggleButton b =
            new JSmartToggleButton(new AutoTailAction(table));
        
        b.toggleOnProperty(table, JSmartTable.PROP_AUTOTAIL);
        buttons.add(b);

        buttons.add(new JSmartButton(new AbstractAction("setAutoTail(true)")
        {
            public void actionPerformed(ActionEvent e)
            {
                table.setAutoTail(true);
            }
        }));

        buttons.add(new JSmartButton(new AbstractAction("setAutoTail(false)")
        {
            public void actionPerformed(ActionEvent e)
            {
                table.setAutoTail(false);
            }
        }));

        content.add(buttons, BorderLayout.SOUTH);

        launchInDialog(content);
    }


    /**
     * Tests to make sure table preferences are save/restored correctly.
     *
     * @throws Exception on error.
     */
    public void testPreferences() throws Exception
    {
        logger_.info("Running testPreferences...");

        JSmartTable table = new JSmartTable();

        {
            table.setAutoTail(true);

            Element root = new Element("root");
            table.savePrefs(root);
            logger_.debug("Saved preferences:\n" + XOMUtil.toXML(root));

            JSmartTable restored = new JSmartTable();
            restored.applyPrefs(root);

            assertTrue(restored.isAutoTail());
        }

        {
            table.setAutoTail(false);

            Element root = new Element("root");
            table.savePrefs(root);
            logger_.debug("Saved preferences:\n" + XOMUtil.toXML(root));

            JSmartTable restored = new JSmartTable();
            restored.applyPrefs(root);

            assertFalse(restored.isAutoTail());
        }
    }
}