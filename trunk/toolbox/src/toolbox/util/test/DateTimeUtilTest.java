package toolbox.util.test;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.DateTimeUtil;

/**
 * @author analogue
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DateTimeUtilTest extends TestCase
{

    /**
     * Constructor for DateTimeUtilTest.
     * @param arg0
     */
    public DateTimeUtilTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        TestRunner.run(DateTimeUtilTest.class);
    }
    
    /**
     *    Tests getBeginningOfDay()
     */
    public void testGetBeginningOfDay() throws Exception
    {

        Date d = DateTimeUtil.getBeginningOfDay();

        Calendar c = Calendar.getInstance();
        c.setTime(d);
        assertTrue("hour not zero", c.get(Calendar.HOUR_OF_DAY) == 0);
        assertTrue("minute not zero", c.get(Calendar.MINUTE) == 0);
        assertTrue("sedond not zero", c.get(Calendar.SECOND) == 0);
        assertTrue("millis not zero", c.get(Calendar.MILLISECOND) == 0);
    }

}
