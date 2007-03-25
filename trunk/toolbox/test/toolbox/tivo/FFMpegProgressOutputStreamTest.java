package toolbox.tivo;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.io.StringOutputStream;

/**
 * Unit test for {@link FFMpegProgressOutputStream}.
 */
public class FFMpegProgressOutputStreamTest extends TestCase {

    private static final Logger logger_ = Logger.getLogger(FFMpegProgressOutputStreamTest.class);
    
    // =========================================================================
    // Main
    // =========================================================================

    public static void main(String[] args) {
        TestRunner.run(FFMpegProgressOutputStreamTest.class);
    }
    
    // =========================================================================
    // Unit Tests
    // =========================================================================

    /**
     * Format for ffmpeg from 2007
     */
    public void testWrite_OneLine() throws Exception {
        logger_.info("Running testWrite_OneLine...");
        
        StringOutputStream sos = new StringOutputStream();
        FFMpegProgressOutputStream pos = new FFMpegProgressOutputStream(100, sos);
        pos.write("frame=  290 fps= 35 q=31.0 size=     958kB time=9.6 bitrate= 813.9kbits/s".getBytes());
        pos.flush();
        pos.close();
        
        logger_.debug(sos.toString());
        assertEquals(9, pos.getProgressSecs());
        assertEquals(290, pos.getProgressFrames());
    }
    
    public void testWrite_ManyLines() throws Exception {
    	logger_.info("Running testWrite_ManyWrites...");
        
        String input =
	    	"frame=   18 fps=  0 q=4.4 size=     118kB time=0.6 bitrate=1704.2kbits/s    \n"
	    	+ "frame=   36 fps= 35 q=4.7 size=     176kB time=1.2 bitrate=1234.6kbits/s    \n"
	    	+ "frame=   54 fps= 35 q=8.0 size=     258kB time=1.8 bitrate=1195.1kbits/s    \n"
	    	+ "frame=   73 fps= 35 q=7.7 size=     332kB time=2.4 bitrate=1132.1kbits/s    \n"
	    	+ "frame=   90 fps= 35 q=15.0 size=     398kB time=3.0 bitrate=1097.9kbits/s    \n"
	    	+ "frame=  109 fps= 35 q=13.4 size=     468kB time=3.6 bitrate=1063.9kbits/s    \n"
	    	+ "frame=  128 fps= 35 q=13.2 size=     506kB time=4.2 bitrate= 978.2kbits/s    \n"
	    	+ "frame=  149 fps= 36 q=12.0 size=     568kB time=4.9 bitrate= 942.2kbits/s    \n"
	    	+ "frame=  165 fps= 35 q=12.4 size=     602kB time=5.5 bitrate= 901.2kbits/s    \n"
	    	+ "frame=  183 fps= 35 q=24.5 size=     688kB time=6.1 bitrate= 928.1kbits/s    \n"
	    	+ "frame=  203 fps= 35 q=23.7 size=     726kB time=6.7 bitrate= 882.4kbits/s    \n"
	    	+ "frame=  221 fps= 35 q=23.8 size=     788kB time=7.3 bitrate= 879.4kbits/s    \n"
	    	+ "frame=  239 fps= 35 q=31.0 size=     840kB time=7.9 bitrate= 866.5kbits/s    \n"
	    	+ "frame=  254 fps= 35 q=31.0 size=     874kB time=8.4 bitrate= 848.1kbits/s    \n"
	    	+ "frame=  273 fps= 35 q=31.0 size=     926kB time=9.1 bitrate= 835.8kbits/s    \n"
	    	+ "frame=  290 fps= 35 q=31.0 size=     958kB time=9.6 bitrate= 813.9kbits/s    \n"
	    	+ "frame=  309 fps= 35 q=31.0 size=    1006kB time=10.3 bitrate= 801.9kbits/s    \n"
	    	+ "frame=  325 fps= 35 q=31.0 size=    1036kB time=10.8 bitrate= 785.0kbits/s    \n"
	    	+ "frame=  344 fps= 35 q=31.0 size=    1090kB time=11.4 bitrate= 780.2kbits/s    \n"
	    	+ "frame=  362 fps= 35 q=31.0 size=    1136kB time=12.0 bitrate= 772.6kbits/s    \n"
	    	+ "frame=  382 fps= 35 q=31.0 size=    1188kB time=12.7 bitrate= 765.5kbits/s    \n"
	    	+ "frame=  392 fps= 34 q=31.0 size=    1218kB time=13.0 bitrate= 764.8kbits/s    \n"
	    	+ "frame=  411 fps= 34 q=31.0 size=    1248kB time=13.7 bitrate= 747.3kbits/s    \n"
	    	+ "frame=  429 fps= 34 q=31.0 size=    1302kB time=14.3 bitrate= 746.9kbits/s    \n"
	    	+ "frame=  447 fps= 34 q=31.0 size=    1334kB time=14.9 bitrate= 734.3kbits/s    \n"
	    	+ "frame=  464 fps= 34 q=31.0 size=    1378kB time=15.4 bitrate= 730.7kbits/s    \n"
	    	+ "frame=  484 fps= 35 q=31.0 size=    1430kB time=16.1 bitrate= 726.9kbits/s    \n"
	    	+ "frame=  501 fps= 35 q=31.0 size=    1458kB time=16.7 bitrate= 715.9kbits/s    \n"
	    	+ "frame=  521 fps= 35 q=31.0 size=    1512kB time=17.4 bitrate= 713.9kbits/s    \n"
	    	+ "frame=  541 fps= 35 q=31.0 size=    1558kB time=18.0 bitrate= 708.4kbits/s    \n"
	    	+ "frame=  561 fps= 35 q=31.0 size=    1610kB time=18.7 bitrate= 705.9kbits/s    \n"
	    	+ "frame=  577 fps= 35 q=31.0 size=    1646kB time=19.2 bitrate= 701.6kbits/s    \n"
	    	+ "frame=  595 fps= 35 q=31.0 size=    1696kB time=19.8 bitrate= 701.0kbits/s    \n"
	    	+ "frame=  611 fps= 35 q=31.0 size=    1726kB time=20.4 bitrate= 694.7kbits/s    \n"
	    	+ "frame=  631 fps= 35 q=31.0 size=    1774kB time=21.0 bitrate= 691.3kbits/s    \n"
	    	+ "frame=  645 fps= 35 q=31.0 size=    1800kB time=21.5 bitrate= 686.2kbits/s    \n"
	    	+ "frame=  661 fps= 34 q=31.0 size=    1850kB time=22.0 bitrate= 688.2kbits/s    \n"
	    	+ "frame=  675 fps= 34 q=31.0 size=    1876kB time=22.5 bitrate= 683.4kbits/s    \n"
	    	+ "frame=  688 fps= 34 q=31.0 size=    1916kB time=22.9 bitrate= 684.7kbits/s    \n"
	    	+ "frame=  705 fps= 34 q=31.0 size=    1952kB time=23.5 bitrate= 680.7kbits/s    \n"
	    	+ "frame=  722 fps= 33 q=31.0 size=    2006kB time=24.1 bitrate= 683.1kbits/s    \n"
	    	+ "frame=  741 fps= 33 q=31.0 size=    2046kB time=24.7 bitrate= 678.8kbits/s    \n"
	    	+ "frame=  758 fps= 33 q=31.0 size=    2104kB time=25.3 bitrate= 682.4kbits/s    \n"
	    	+ "frame=  777 fps= 33 q=31.0 size=    2144kB time=25.9 bitrate= 678.3kbits/s    \n"
	    	+ "frame=  796 fps= 33 q=31.0 size=    2202kB time=26.5 bitrate= 680.0kbits/s    \n"
	    	+ "frame=  812 fps= 33 q=31.0 size=    2248kB time=27.1 bitrate= 680.5kbits/s    \n"
	    	+ "frame=  831 fps= 33 q=31.0 size=    2282kB time=27.7 bitrate= 675.0kbits/s    \n"
	    	+ "frame=  850 fps= 33 q=31.0 size=    2330kB time=28.3 bitrate= 673.8kbits/s    \n"
	    	+ "frame=  869 fps= 34 q=31.0 size=    2376kB time=29.0 bitrate= 672.1kbits/s    \n"
	    	+ "frame=  886 fps= 34 q=31.0 size=    2414kB time=29.5 bitrate= 669.7kbits/s    \n"
	    	+ "frame=  905 fps= 34 q=31.0 size=    2466kB time=30.2 bitrate= 669.7kbits/s    \n"
	    	+ "frame=  924 fps= 34 q=31.0 size=    2516kB time=30.8 bitrate= 669.2kbits/s    \n"
	    	+ "frame=  943 fps= 34 q=31.0 size=    2544kB time=31.4 bitrate= 663.0kbits/s    \n"
	    	+ "frame=  963 fps= 34 q=31.0 size=    2598kB time=32.1 bitrate= 663.0kbits/s    \n"
	    	+ "frame=  977 fps= 34 q=31.0 size=    2626kB time=32.6 bitrate= 660.6kbits/s    \n"
	    	+ "frame=  991 fps= 34 q=31.0 size=    2658kB time=33.0 bitrate= 659.2kbits/s    \n"
	    	+ "frame= 1011 fps= 34 q=31.0 size=    2710kB time=33.7 bitrate= 658.8kbits/s    \n"
	    	+ "frame= 1027 fps= 34 q=31.0 size=    2744kB time=34.2 bitrate= 656.6kbits/s    \n"
	    	+ "frame= 1045 fps= 34 q=31.0 size=    2810kB time=34.8 bitrate= 660.8kbits/s    \n"
	    	+ "frame= 1062 fps= 34 q=31.0 size=    2848kB time=35.4 bitrate= 659.0kbits/s    \n"
	    	+ "frame= 1081 fps= 34 q=31.0 size=    2904kB time=36.0 bitrate= 660.2kbits/s    \n"
	    	+ "frame= 1099 fps= 34 q=31.0 size=    2938kB time=36.6 bitrate= 656.9kbits/s    \n"
	    	+ "frame= 1118 fps= 34 q=31.0 size=    2982kB time=37.3 bitrate= 655.4kbits/s    \n"
	    	+ "frame= 1138 fps= 34 q=31.0 size=    3014kB time=37.9 bitrate= 650.8kbits/s    \n"
	    	+ "frame= 1157 fps= 34 q=31.0 size=    3070kB time=38.6 bitrate= 652.0kbits/s    \n"
	    	+ "frame= 1171 fps= 34 q=24.8 size=    3124kB time=39.0 bitrate= 655.5kbits/s    \n"
	    	+ "frame= 1189 fps= 34 q=31.0 size=    3166kB time=39.6 bitrate= 654.3kbits/s    \n"
	    	+ "frame= 1206 fps= 34 q=31.0 size=    3208kB time=40.2 bitrate= 653.6kbits/s    \n"
	    	+ "frame= 1225 fps= 34 q=31.0 size=    3268kB time=40.8 bitrate= 655.5kbits/s    \n"
	    	+ "frame= 1242 fps= 34 q=31.0 size=    3304kB time=41.4 bitrate= 653.7kbits/s    \n"
	    	+ "frame= 1261 fps= 34 q=31.0 size=    3356kB time=42.0 bitrate= 653.9kbits/s    \n"
	    	+ "frame= 1280 fps= 34 q=31.0 size=    3402kB time=42.7 bitrate= 653.0kbits/s    \n"
	    	+ "frame= 1300 fps= 34 q=31.0 size=    3434kB time=43.3 bitrate= 649.0kbits/s    \n"
	    	+ "frame= 1320 fps= 34 q=31.0 size=    3484kB time=44.0 bitrate= 648.5kbits/s    \n"
	    	+ "frame= 1339 fps= 34 q=31.0 size=    3534kB time=44.6 bitrate= 648.5kbits/s    \n"
	    	+ "frame= 1359 fps= 34 q=31.0 size=    3566kB time=45.3 bitrate= 644.7kbits/s    \n"
	    	+ "frame= 1379 fps= 34 q=31.0 size=    3622kB time=46.0 bitrate= 645.3kbits/s    \n"
	    	+ "frame= 1398 fps= 34 q=31.0 size=    3674kB time=46.6 bitrate= 645.7kbits/s    \n"
	    	+ "frame= 1412 fps= 34 q=31.0 size=    3702kB time=47.1 bitrate= 644.1kbits/s    \n"
	    	+ "frame= 1431 fps= 34 q=31.0 size=    3736kB time=47.7 bitrate= 641.4kbits/s    \n"
	    	+ "frame= 1451 fps= 34 q=31.0 size=    3784kB time=48.4 bitrate= 640.7kbits/s    \n"
	    	+ "frame= 1470 fps= 34 q=31.0 size=    3832kB time=49.0 bitrate= 640.4kbits/s    \n"
	    	+ "frame= 1489 fps= 34 q=31.0 size=    3874kB time=49.6 bitrate= 639.2kbits/s    \n"
	    	+ "frame= 1508 fps= 34 q=31.0 size=    3928kB time=50.3 bitrate= 639.9kbits/s    \n"
	    	+ "frame= 1526 fps= 34 q=31.0 size=    3958kB time=50.9 bitrate= 637.2kbits/s    \n"
	    	+ "frame= 1546 fps= 34 q=31.0 size=    4012kB time=51.6 bitrate= 637.5kbits/s    \n"
	    	+ "frame= 1564 fps= 34 q=31.0 size=    4048kB time=52.2 bitrate= 635.9kbits/s    \n"
	    	+ "frame= 1566 fps= 34 q=31.0 size=    4066kB time=52.2 bitrate= 637.9kbits/s    \n"
	    	+ "frame= 1580 fps= 34 q=31.0 size=    4100kB time=52.7 bitrate= 637.5kbits/s    \n"
	    	+ "frame= 1599 fps= 34 q=31.0 size=    4144kB time=53.3 bitrate= 636.7kbits/s    \n"
	    	+ "frame= 1617 fps= 34 q=31.0 size=    4172kB time=53.9 bitrate= 633.8kbits/s    \n"
	    	+ "frame= 1636 fps= 34 q=31.0 size=    4224kB time=54.6 bitrate= 634.3kbits/s    \n"
	    	+ "frame= 1656 fps= 34 q=31.0 size=    4274kB time=55.2 bitrate= 634.0kbits/s    \n"
	    	+ "frame= 1675 fps= 34 q=31.0 size=    4316kB time=55.9 bitrate= 633.0kbits/s    \n"
	    	+ "frame= 1692 fps= 34 q=31.0 size=    4364kB time=56.4 bitrate= 633.6kbits/s    \n"
	    	+ "frame= 1712 fps= 34 q=31.0 size=    4394kB time=57.1 bitrate= 630.5kbits/s    \n"
	    	+ "frame= 1730 fps= 34 q=31.0 size=    4444kB time=57.7 bitrate= 631.0kbits/s    \n"
	    	+ "frame= 1750 fps= 34 q=31.0 size=    4488kB time=58.4 bitrate= 630.0kbits/s    \n"
	    	+ "frame= 1767 fps= 34 q=31.0 size=    4518kB time=58.9 bitrate= 628.1kbits/s    \n"
	    	+ "frame= 1778 fps= 34 q=31.0 size=    4540kB time=59.3 bitrate= 627.3kbits/s    \n"
	    	+ "frame= 1796 fps= 34 q=31.0 size=    4592kB time=59.9 bitrate= 628.1kbits/s    \n"
	    	+ "frame= 1815 fps= 34 q=31.0 size=    4640kB time=60.5 bitrate= 628.0kbits/s    \n"
	    	+ "frame= 1833 fps= 34 q=31.0 size=    4674kB time=61.1 bitrate= 626.4kbits/s    \n"
	    	+ "frame= 1852 fps= 34 q=31.0 size=    4722kB time=61.8 bitrate= 626.3kbits/s    \n"
	    	+ "frame= 1870 fps= 34 q=31.0 size=    4760kB time=62.4 bitrate= 625.3kbits/s    \n"
	    	+ "frame= 1891 fps= 34 q=31.0 size=    4810kB time=63.1 bitrate= 624.8kbits/s    \n"
	    	+ "frame= 1910 fps= 34 q=31.0 size=    4844kB time=63.7 bitrate= 623.0kbits/s    \n"
	    	+ "frame= 1929 fps= 34 q=31.0 size=    4890kB time=64.3 bitrate= 622.7kbits/s    \n"
	    	+ "frame= 1948 fps= 34 q=31.0 size=    4944kB time=65.0 bitrate= 623.4kbits/s    \n"
	    	+ "frame= 1967 fps= 34 q=31.0 size=    4984kB time=65.6 bitrate= 622.4kbits/s    \n"
	    	+ "frame= 1985 fps= 34 q=31.0 size=    5042kB time=66.2 bitrate= 623.9kbits/s    \n"
	    	+ "frame= 2004 fps= 34 q=31.0 size=    5078kB time=66.8 bitrate= 622.4kbits/s    \n"
	    	+ "frame= 2024 fps= 34 q=31.0 size=    5122kB time=67.5 bitrate= 621.6kbits/s    \n"
	    	+ "frame= 2043 fps= 34 q=24.8 size=    5174kB time=68.1 bitrate= 622.1kbits/s    \n"
	    	+ "frame= 2061 fps= 34 q=31.0 size=    5206kB time=68.7 bitrate= 620.5kbits/s    \n"
	    	+ "frame= 2076 fps= 34 q=31.0 size=    5244kB time=69.2 bitrate= 620.5kbits/s    \n"
	    	+ "frame= 2094 fps= 34 q=31.0 size=    5294kB time=69.8 bitrate= 621.0kbits/s    \n"
	    	+ "frame= 2113 fps= 34 q=31.0 size=    5332kB time=70.5 bitrate= 619.8kbits/s    \n"
	    	+ "frame= 2131 fps= 34 q=31.0 size=    5384kB time=71.1 bitrate= 620.6kbits/s    \n"
	    	+ "frame= 2147 fps= 34 q=31.0 size=    5428kB time=71.6 bitrate= 621.0kbits/s    \n"
	    	+ "frame= 2163 fps= 34 q=31.0 size=    5458kB time=72.1 bitrate= 619.8kbits/s    \n"
	    	+ "frame= 2179 fps= 34 q=31.0 size=    5504kB time=72.7 bitrate= 620.4kbits/s    \n"
	    	+ "frame= 2196 fps= 34 q=31.0 size=    5534kB time=73.2 bitrate= 619.0kbits/s    \n"
	    	+ "frame= 2203 fps= 34 q=31.0 size=    5556kB time=73.5 bitrate= 619.5kbits/s    \n"
	    	+ "frame= 2215 fps= 34 q=31.0 size=    5582kB time=73.9 bitrate= 619.0kbits/s    \n"
	    	+ "frame= 2230 fps= 34 q=24.8 size=    5622kB time=74.4 bitrate= 619.2kbits/s    \n"
	    	+ "frame= 2243 fps= 34 q=31.0 size=    5648kB time=74.8 bitrate= 618.5kbits/s    \n"
	    	+ "frame= 2257 fps= 34 q=31.0 size=    5676kB time=75.3 bitrate= 617.7kbits/s    \n"
	    	+ "frame= 2274 fps= 34 q=31.0 size=    5714kB time=75.8 bitrate= 617.2kbits/s    \n"
	    	+ "frame= 2292 fps= 34 q=31.0 size=    5760kB time=76.4 bitrate= 617.3kbits/s    \n"
	    	+ "frame= 2308 fps= 34 q=31.0 size=    5786kB time=77.0 bitrate= 615.8kbits/s    \n"
	    	+ "frame= 2326 fps= 34 q=24.8 size=    5834kB time=77.6 bitrate= 616.1kbits/s    \n"
	    	+ "frame= 2346 fps= 34 q=31.0 size=    5860kB time=78.2 bitrate= 613.5kbits/s    \n"
	    	+ "frame= 2365 fps= 34 q=31.0 size=    5918kB time=78.9 bitrate= 614.6kbits/s    \n"
	    	+ "frame= 2384 fps= 34 q=31.0 size=    5952kB time=79.5 bitrate= 613.2kbits/s    \n"
	    	+ "frame= 2399 fps= 34 q=31.0 size=    6000kB time=80.0 bitrate= 614.3kbits/s    \n"
	    	+ "frame= 2418 fps= 34 q=31.0 size=    6034kB time=80.6 bitrate= 612.9kbits/s    \n"
	    	+ "frame= 2437 fps= 34 q=31.0 size=    6078kB time=81.3 bitrate= 612.6kbits/s    \n"
	    	+ "frame= 2455 fps= 34 q=31.0 size=    6114kB time=81.9 bitrate= 611.7kbits/s    \n"
	    	+ "frame= 2474 fps= 34 q=31.0 size=    6170kB time=82.5 bitrate= 612.5kbits/s    \n"
	    	+ "frame= 2492 fps= 34 q=31.0 size=    6212kB time=83.1 bitrate= 612.3kbits/s    \n"
	    	+ "frame= 2511 fps= 34 q=31.0 size=    6268kB time=83.8 bitrate= 613.1kbits/s    \n"
	    	+ "frame= 2529 fps= 34 q=31.0 size=    6308kB time=84.4 bitrate= 612.6kbits/s    \n"
	    	+ "frame= 2547 fps= 34 q=31.0 size=    6366kB time=85.0 bitrate= 613.9kbits/s    \n"
	    	+ "frame= 2567 fps= 34 q=31.0 size=    6430kB time=85.6 bitrate= 615.2kbits/s    \n"
	    	+ "frame= 2586 fps= 34 q=31.0 size=    6466kB time=86.3 bitrate= 614.1kbits/s    \n"
	    	+ "frame= 2599 fps= 34 q=31.0 size=    6486kB time=86.7 bitrate= 612.9kbits/s    \n"
	    	+ "frame= 2618 fps= 34 q=31.0 size=    6536kB time=87.3 bitrate= 613.2kbits/s    \n"
	    	+ "frame= 2635 fps= 34 q=31.0 size=    6560kB time=87.9 bitrate= 611.5kbits/s    \n"
	    	+ "frame= 2654 fps= 34 q=31.0 size=    6606kB time=88.5 bitrate= 611.3kbits/s    \n"
	    	+ "frame= 2673 fps= 34 q=31.0 size=    6662kB time=89.2 bitrate= 612.1kbits/s    \n"
	    	+ "frame= 2694 fps= 34 q=31.0 size=    6692kB time=89.9 bitrate= 610.1kbits/s    \n"
	    	+ "frame= 2710 fps= 34 q=31.0 size=    6744kB time=90.4 bitrate= 611.2kbits/s    \n"
	    	+ "frame= 2728 fps= 34 q=31.0 size=    6772kB time=91.0 bitrate= 609.7kbits/s    \n"
	    	+ "frame= 2745 fps= 34 q=31.0 size=    6816kB time=91.6 bitrate= 609.8kbits/s    \n"
	    	+ "frame= 2764 fps= 34 q=31.0 size=    6852kB time=92.2 bitrate= 608.9kbits/s    \n"
	    	+ "frame= 2781 fps= 34 q=31.0 size=    6898kB time=92.8 bitrate= 609.2kbits/s    \n"
	    	+ "frame= 2788 fps= 34 q=31.0 size=    6908kB time=93.0 bitrate= 608.5kbits/s    \n"
	    	+ "frame= 2806 fps= 34 q=31.0 size=    6954kB time=93.6 bitrate= 608.7kbits/s    \n"
	    	+ "frame= 2824 fps= 34 q=31.0 size=    6988kB time=94.2 bitrate= 607.7kbits/s    \n"
	    	+ "frame= 2842 fps= 34 q=31.0 size=    7048kB time=94.8 bitrate= 609.1kbits/s    \n"
	    	+ "frame= 2859 fps= 34 q=31.0 size=    7082kB time=95.4 bitrate= 608.4kbits/s    \n"
	    	+ "frame= 2878 fps= 34 q=31.0 size=    7138kB time=96.0 bitrate= 609.1kbits/s    \n"
	    	+ "frame= 2896 fps= 34 q=31.0 size=    7178kB time=96.6 bitrate= 608.7kbits/s    \n"
	    	+ "frame= 2909 fps= 34 q=31.0 Lsize=    7222kB time=97.0 bitrate= 609.7kbits/s    \n";
        
        StringOutputStream sos = new StringOutputStream();
        FFMpegProgressOutputStream pos = new FFMpegProgressOutputStream(100, sos);
        
        pos.write(input.getBytes());
        pos.flush();
        pos.close();
    }

    // =========================================================================
    // Deprecated tests for older versions of ffmpeg
    // =========================================================================
    
    /**
     * Tests output format for ffmpeg from 2004 - comment out
     */
    public void deprecated_test1() throws Exception {
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

    /**
     * Tests output format for ffmpeg from 2004 - comment out
     */
    public void deprecated_test2() throws Exception {
        
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