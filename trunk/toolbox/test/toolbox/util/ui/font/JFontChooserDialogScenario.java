package toolbox.util.ui.font;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.swing.JList;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import org.netbeans.jemmy.Action;
import org.netbeans.jemmy.ActionProducer;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.Scenario;
import org.netbeans.jemmy.Test;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.util.NameComponentChooser;

import toolbox.util.StringUtil;
import toolbox.util.io.NullWriter;

/**
 * Automated UI Unit test for JFontChooserDialog.
 */
public class JFontChooserDialogScenario implements Scenario
{
    private static final Logger logger_ = 
        Logger.getLogger(JFontChooserDialogScenario.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entry point.
     * 
     * @param args None recognized.
     * @throws Exception on LAF error.
     */
    public static void main(String[] args) throws Exception
    {
        //Test.main(new String[]{JFontChooserDialogScenario.class.getName()});
        
    }

    public static class Launcher
    {
        public static void main(String[] args) throws Exception
        {
            Test.run(
                new String[] {JFontChooserDialogScenario.class.getName()},
                new PrintWriter(new NullWriter()),
                new PrintWriter(new OutputStreamWriter(System.err)));
        }
    }
    
    //--------------------------------------------------------------------------
    // Scenario Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see org.netbeans.jemmy.Scenario#runIt(java.lang.Object)
     */
    public int runIt(Object param)
    {
        try
        {
            Exception e = (Exception) (new ActionProducer(
                new Action()
                {
                    public Object launch(Object obj)
                    {
                        try
                        {
                            (new ClassReference(
                                JFontChooserDialogTest.class.getName()))
                                .startApplication();
                        }
                        catch (Exception ex)
                        {
                            return (ex);
                        }
                        
                        return (null);
                    }
    
    
                    public String getDescription()
                    {
                        return ("Desc");
                    }
    
                }, false).produceAction(null));
    
            if (e != null)
            {
                //throw (e);
                e.printStackTrace();
            }

            JDialogOperator dialog = new JDialogOperator("Select font");
            logger_.info(StringUtil.banner(dialog.getDump().toString()));

            //==================================================================
            // Font Size Field
            //------------------------------------------------------------------
            JTextFieldOperator sizeField = 
                new JTextFieldOperator(dialog, 
                    new NameComponentChooser(JFontChooser.NAME_SIZE_FIELD));
            
            sizeField.enterText("55");
            
            JListOperator fontList = 
                new JListOperator(dialog, 
                    new NameComponentChooser(JFontChooser.NAME_FONT_LIST));
 
            JListOperator styleList = 
                new JListOperator(dialog, 
                    new NameComponentChooser(JFontChooser.NAME_STYLE_LIST));

            JListOperator sizeList = 
                new JListOperator(dialog, 
                    new NameComponentChooser(JFontChooser.NAME_SIZE_LIST));

            JCheckBoxOperator antialiasCheckBox = 
                new JCheckBoxOperator(dialog, 
                    new NameComponentChooser(
                        JFontChooser.NAME_ANTIALIAS_CHECKBOX));

            JList fontListSource = (JList) fontList.getSource();
            JList styleListSource = (JList) styleList.getSource();
            JList sizeListSource = (JList) sizeList.getSource();
            
            int numStyles = styleListSource.getModel().getSize();
            int numSizes = sizeListSource.getModel().getSize();
            
            for (int i = 0; i < fontListSource.getModel().getSize(); i++)
            {
                //==============================================================
                // Font name
                //--------------------------------------------------------------
                fontList.selectItem(i);
                
                //==============================================================
                // Font style
                //--------------------------------------------------------------
                styleList.selectItem(RandomUtils.nextInt(numStyles));
                
                //==============================================================
                // Font Size List
                //--------------------------------------------------------------
                sizeList.selectItem(RandomUtils.nextInt(numSizes));
                
                //==============================================================
                // AntiAlias CheckBox
                //--------------------------------------------------------------
                antialiasCheckBox.clickMouse();
            }
            
            //==================================================================
            // Tear Down
            //------------------------------------------------------------------
            
            new JButtonOperator(dialog, "OK").clickMouse();
            dialog.waitClosed();
        }
        catch (Exception ex)
        {
            logger_.error(ex);
            return 1;
        }

        return 0;
    }
}