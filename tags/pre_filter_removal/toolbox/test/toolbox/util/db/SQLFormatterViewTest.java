package toolbox.util.db;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;

/**
 * Unit test for {@link toolbox.util.db.SQLFormatterView}.
 */
public class SQLFormatterViewTest extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(SQLFormatterViewTest.class);
    
    public static void main(String[] args)
    {
        TestRunner.run(SQLFormatterViewTest.class);
    }
    
    
    public void testView() throws Exception
    {
        launchInFrame(new SQLFormatterView(new SQLFormatter()));
    }
}


//DefaultProperty p = new DefaultProperty();
//p.setValue("I am the value");
//p.setDisplayName("Semirs Variable");
//p.setEditable(true);
//p.setName("semirsVariable");
//p.setShortDescription("Semirs short description");
//p.setType(String.class);
//
//PropertyChangeListener pcl = new PCL();
//
//DefaultProperty debug = new DefaultProperty();
////debug.setValue(Boolean.valueOf(formatter_.isDebug()));
//debug.setDisplayName("Debug");
//debug.setEditable(true);
//debug.setName("debug");
//debug.setShortDescription("Toggles the debug flag");
//debug.setType(Boolean.class);
//debug.readFromObject(formatter_);
//debug.addPropertyChangeListener(pcl);
//
//DefaultProperty indent = new DefaultProperty();
//indent.setDisplayName("Number of spaces per indentation");
//indent.setEditable(true);
//indent.setName("indent");
//indent.setShortDescription("Number of spaces per indentation");
//indent.setType(Integer.class);
//indent.readFromObject(formatter_);
//indent.addPropertyChangeListener(pcl);
//
//DefaultProperty newLine = new DefaultProperty();
//newLine.setDisplayName("Newline character");
//newLine.setEditable(true);
//newLine.setName("newLine");
//newLine.setShortDescription("Newline character");
//newLine.setType(String.class);
//newLine.readFromObject(formatter_);
//newLine.addPropertyChangeListener(pcl);