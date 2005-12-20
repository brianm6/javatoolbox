package toolbox.util.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;

import junit.textui.TestRunner;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;

/**
 * Unit test for {@link toolbox.util.ui.JSmartFileChooser}.
 */
public class JSmartFileChooserTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JSmartFileChooserTest.class);
        
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private Element chooserPrefs_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(JSmartFileChooserTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     *  
     */
    public void testPrefs()
    {
        logger_.info("Running testPrefs...");
        
        JPanel p = new JPanel(new FlowLayout());
        p.add(new JSmartButton(new SavePrefsAction()));
        p.add(new JSmartButton(new ApplyPrefsAction()));
        launchInDialog(p, UITestCase.SCREEN_ONE_THIRD);
    }
    
    //--------------------------------------------------------------------------
    // SavePrefsAction
    //--------------------------------------------------------------------------
    
    class SavePrefsAction extends AbstractAction
    {
        public SavePrefsAction()
        {
            super("Save Prefs");
        }
        
        
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                JSmartFileChooser sfc = new JSmartFileChooser();
                sfc.showSaveDialog((JComponent) e.getSource());
                
                chooserPrefs_ = new Element("saved");
                sfc.savePrefs(chooserPrefs_);
                
                logger_.debug(chooserPrefs_.toXML());
            }
            catch (Exception ex)
            {
                logger_.error("save prefs", ex);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // ApplyPrefsAction
    //--------------------------------------------------------------------------
    
    class ApplyPrefsAction extends AbstractAction
    {
        public ApplyPrefsAction()
        {
            super("Apply Prefs");
        }
        
        
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                JSmartFileChooser sfc = new JSmartFileChooser();
                sfc.applyPrefs(chooserPrefs_);
                sfc.showSaveDialog((JComponent) e.getSource());
            }
            catch (Exception ex)
            {
                logger_.error("apply prefs", ex);
            }
        }
    }    
}