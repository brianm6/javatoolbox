package toolbox.util.formatter;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.formatter.Formatter;
import toolbox.util.formatter.JavaFormatter;
import toolbox.util.io.StringInputStream;
import toolbox.util.io.StringOutputStream;

/**
 * Unit test for JavaFormatter.
 * 
 * @see toolbox.util.formatter.JavaFormatter
 */
public class JavaFormatterTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(JavaFormatterTest.class);
    
    //--------------------------------------------------------------------------
    // Test Data
    //--------------------------------------------------------------------------
    
    /**
     * Nice bit of cryptic java code to pretty print.
     */
    private static final String JAVA_OBFUSCATED = 
        "import java.awt.*;import java.util.*;public class C extends "
        + "Frame{Date D=new Date();void T(Date d){D=d;repaint();}double"
        + " P=Math.PI,A=P/2,a,c,U=.05;int W,H,m,R;double E(int a,int u)"
        + "{return(3*P/2+2*P*a/u)%(2*P);}void N(Graphics g,double q,dou"
        + "ble s){g.fillPolygon(new int[]{H(s,q),H(U,q+A),H(U,q+3*A)},n"
        + "ew int[]{J(s,q),J(U,q+A),J(U,q+3*A)},3);}public void paint(G"
        + "raphics g){Color C=SystemColor.control;g.setColor(C);g.fillR"
        + "ect(0,0,W=size().width,H=size().height);W-=52;H-=52;R=Math.m"
        + "in(W/2,H/2);g.translate(W/2+25,H/2+36);g.setColor(C.darker()"
        + ");for(m=0;m<12;++m){a=E(m,12);g.drawLine(H(.8),J(.8),H(.9),J"
        + "(.9));}m=D.getMinutes();N(g,E(D.getHours()*60+m,720),.5);N(g"
        + ",E(m,60),.8);N(g,E(D.getSeconds(),60),.9);}int H(double y){r"
        + "eturn(int)(R*y*Math.cos(a));}int H(double y,double q){a=q;re"
        + "turn H(y);}int J(double y){return(int)(R*y*Math.sin(a));}int"
        + " J(double y,double q){a=q;return J(y);}public static void ma"
        + "in(String[]_)throws Exception{C c=new C();c.resize(200,200);"
        + "c.show();for(;;){c.T(new Date());Thread.sleep(200);}}}";
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(JavaFormatterTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests String format(String) 
     */
    public void testFormatStrings() throws Exception
    {
        logger_.info("Running testFormatStrings...");
        
        Formatter f = new JavaFormatter();
        String t = f.format(JAVA_OBFUSCATED);
        assertNotNull(t);
        logger_.info(StringUtil.banner(t));
        assertNotSame(JAVA_OBFUSCATED, t);
    }
        

    /**
     * Tests format(InputStream, OutputStream)
     */
    public void testFormatStreams() throws Exception
    {
        logger_.info("Running testFormatStreams...");
        
        Formatter f = new JavaFormatter();
        StringOutputStream sos = null;
        StringInputStream sis = null;
        String out = null;
        
        sis = new StringInputStream(JAVA_OBFUSCATED);
        sos = new StringOutputStream();
        f.format(sis, sos);
        out = sos.toString().trim();
        assertNotNull(out);
        logger_.info(StringUtil.banner(out));
        assertNotSame(JAVA_OBFUSCATED, out);
    }
}