package toolbox.tivo;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.io.StringOutputStream;

public class FFMpegProgressOutputStreamTest extends TestCase {

    private static final Logger logger_ = 
        Logger.getLogger(FFMpegProgressOutputStreamTest.class);
    
    public static void main(String[] args) {
        TestRunner.run(FFMpegProgressOutputStreamTest.class);
    }

    public void test1() throws Exception {
        logger_.info("Running test1...");
        
        StringOutputStream sos = new StringOutputStream();
        FFMpegProgressOutputStream pos = new FFMpegProgressOutputStream(sos);
        
        pos.write("frame=  299 q=0.0 size=     820kB time=9.9 bitrate= 675.6kbits/s".getBytes());
        pos.flush();
        pos.close();
        
        logger_.debug(sos.toString());
        assertEquals(9, pos.getProgressSecs());
        assertEquals(299, pos.getProgressFrames());
    }
    
    
    public void test2() throws Exception {
        
        logger_.info("Running test2...");
        
        String input = 
              "  frame=   10 q=0.0 size=      60kB time=0.3 bitrate=1636.8kbits/s    \n"
            + "  frame=   24 q=0.0 size=     102kB time=0.8 bitrate=1088.8kbits/s    \n"
            + "  frame=   40 q=0.0 size=     150kB time=1.3 bitrate= 944.3kbits/s    \n"
            + "  frame=   54 q=0.0 size=     174kB time=1.8 bitrate= 806.0kbits/s    \n"
            + "  frame=   68 q=0.0 size=     220kB time=2.2 bitrate= 806.2kbits/s    \n"
            + "  frame=   82 q=0.0 size=     282kB time=2.7 bitrate= 854.8kbits/s    \n"
            + "  frame=   96 q=0.0 size=     324kB time=3.2 bitrate= 837.3kbits/s    \n"
            + "  frame=  110 q=0.0 size=     346kB time=3.6 bitrate= 779.3kbits/s    \n"
            + "  frame=  123 q=0.0 size=     396kB time=4.1 bitrate= 796.9kbits/s    \n"
            + "  frame=  138 q=0.0 size=     414kB time=4.6 bitrate= 741.9kbits/s    \n"
            + "  frame=  153 q=0.0 size=     448kB time=5.1 bitrate= 723.6kbits/s    \n"
            + "  frame=  165 q=0.0 size=     486kB time=5.5 bitrate= 727.6kbits/s    \n"
            + "  frame=  175 q=0.0 size=     518kB time=5.8 bitrate= 730.9kbits/s    \n"
            + "  frame=  191 q=0.0 size=     560kB time=6.3 bitrate= 723.6kbits/s    \n"
            + "  frame=  206 q=0.0 size=     602kB time=6.8 bitrate= 721.0kbits/s    \n"
            + "  frame=  221 q=0.0 size=     618kB time=7.3 bitrate= 689.7kbits/s    \n"
            + "  frame=  233 q=0.0 size=     656kB time=7.7 bitrate= 694.2kbits/s    \n"
            + "  frame=  245 q=0.0 size=     690kB time=8.1 bitrate= 694.3kbits/s    \n"
            + "  frame=  257 q=0.0 size=     714kB time=8.5 bitrate= 684.8kbits/s    \n"
            + "  frame=  271 q=0.0 size=     752kB time=9.0 bitrate= 683.8kbits/s    \n"
            + "  frame=  284 q=0.0 size=     786kB time=9.4 bitrate= 681.9kbits/s    \n"
            + "  frame=  299 q=0.0 size=     820kB time=9.9 bitrate= 675.6kbits/s    \n"
            + "  frame=  313 q=0.0 size=     858kB time=10.4 bitrate= 675.2kbits/s    \n"
            + "  frame=  327 q=0.0 size=     878kB time=10.9 bitrate= 661.2kbits/s    \n"
            + "  frame=  342 q=0.0 size=     912kB time=11.4 bitrate= 656.6kbits/s    \n"
            + "  frame=  356 q=0.0 size=     952kB time=11.8 bitrate= 658.4kbits/s    \n"
            + "  frame=  370 q=0.0 size=     986kB time=12.3 bitrate= 656.0kbits/s    \n"
            + "  frame=  384 q=0.0 size=    1022kB time=12.8 bitrate= 655.1kbits/s    \n"
            + "  frame=  399 q=0.0 size=    1062kB time=13.3 bitrate= 655.1kbits/s    \n"
            + "  frame=  414 q=0.0 size=    1078kB time=13.8 bitrate= 640.8kbits/s    \n"
            + "  frame=  429 q=0.0 size=    1114kB time=14.3 bitrate= 639.0kbits/s    \n"
            + "  frame=  445 q=0.0 size=    1148kB time=14.8 bitrate= 634.8kbits/s    \n"
            + "  frame=  458 q=0.0 size=    1200kB time=15.2 bitrate= 644.7kbits/s    \n"
            + "  frame=  472 q=0.0 size=    1216kB time=15.7 bitrate= 633.9kbits/s    \n"
            + "  frame=  487 q=0.0 size=    1250kB time=16.2 bitrate= 631.5kbits/s    \n"
            + "  frame=  500 q=0.0 size=    1286kB time=16.6 bitrate= 632.7kbits/s    \n"
            + "  frame=  514 q=0.0 size=    1322kB time=17.1 bitrate= 632.7kbits/s    \n"
            + "  frame=  527 q=0.0 size=    1358kB time=17.6 bitrate= 633.9kbits/s    \n"
            + "  frame=  542 q=0.0 size=    1396kB time=18.1 bitrate= 633.5kbits/s    \n";
        
        StringOutputStream sos = new StringOutputStream();
        FFMpegProgressOutputStream pos = new FFMpegProgressOutputStream(sos);
        
        pos.write(input.getBytes());
        pos.flush();
        pos.close();
    }
}