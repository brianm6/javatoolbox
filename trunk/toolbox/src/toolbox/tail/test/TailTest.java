package toolbox.tail.test;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.tail.TailListener;
import toolbox.tail.Tail;
import toolbox.util.FileUtil;
import toolbox.util.RandomUtil;
import toolbox.util.StringUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.concurrent.BlockingQueue;
import toolbox.util.file.FileStuffer;
import toolbox.util.io.NullWriter;

/**
 * Unit test for Tail
 */
public class TailTest extends TestCase
{
    public static final Logger logger_ =
        Logger.getLogger(TailTest.class);

    /** 
     * Word list used for test data 
     */
    private String[] words_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(TailTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor - builds the word list
     */
    public TailTest()
    {
        String paragraph =         
            "now is the time for all good men to come to the aid of " +
            "their country the early bird gets the worm what hast " +
            "thou done for me lately build it and they will come " +
            "evil knows no bounds i know you are but what am i zoinks " +
            "scooby ummmm donuts take it easy big fella life is like " +
            "a box of chocolates shit happens and then you're " +
            "reincarnated don't have a cow man";
                   
        words_ = StringUtil.tokenize(paragraph, " ");        
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests tailing of a regular old file sitting around not doing anything.<p>
     * This implies the file:               <br>
     * - exists                             <br>
     * - has no locks                       <br>
     * - has no intended future activity    <br>
     * - can be deleted without consequence
     */
    
    /**
     * Tests tailing of a files backlog for a specific number of lines
     * 
     * @throws Exception on error
     */
    public void testTailBacklog() throws Exception
    {
        logger_.info("Running testTailBacklog...");

        String file = FileUtil.generateTempFilename();
        
        try
        {
            int backlog = 10;   // backlog lines to test
            int total = 200;    // num lines in test file

            String para = makeParagraph(total);
            FileUtil.setFileContents(file, para , false);
            TestTailListener listener = new TestTailListener();
                   
            Tail tail = new Tail();
            tail.follow(new File(file), new NullWriter());
            tail.setBacklog(backlog);
            tail.addTailListener(listener);
            tail.start();
            listener.waitForStart();

            logger_.info("toString: " + toString());
                 
            String[] paras = StringUtil.tokenize(para, "\n");
                   
            for (int i=0; i<backlog; i++)
            {
                String line = listener.waitForNextLine();
                logger_.info("Backlog line " + i + ": " + line);
                assertEquals(paras[i + total - backlog], line);
            }
                
            tail.stop();
            listener.waitForStop();
        }
        finally
        {
            FileUtil.delete(file);
        }
        
        logger_.info("Done!");
    }

    /**
     * Tests tailing a file
     * 
     * @throws Exception on error
     */
    public void testTailFile() throws Exception
    {
        logger_.info("Running testTailFile...");
       
        String file = FileUtil.generateTempFilename();
        File   ffile = new File(file);
        FileStuffer stuffer = new FileStuffer(ffile, 250);
        Writer sink = null;
         
        try
        {
            stuffer.start();
            
            ThreadUtil.sleep(1000);
            
            Tail tail = new Tail();
            //Writer sink = new StringWriter();
            sink = new OutputStreamWriter(System.out);
            tail.follow(ffile, sink);
            tail.setBacklog(4);
            tail.start();
            
            ThreadUtil.sleep(500);
            
            tail.stop();
            
            sink.flush();
        }
        finally
        {
            sink.close();
            stuffer.stop();
            FileUtil.delete(file);
        }
    }
    
    /**
     * Tests the lifecycle of a Tail object (start/stop/pause/unpause)
     * 
     * @throws Exception on error
     */
    public void testTailLifeCycle() throws Exception
    {
       logger_.info("Running testTailLifeCycle...");
       
        PipedWriter writer = new PipedWriter();
        PipedReader reader = new PipedReader(writer);

        ThreadUtil.run(
            this,
            "writeDelayed",
            new Object[] {
                writer,
                new Integer(5),
                new Integer(500),
                "line " });

        Tail tail = new Tail();
        

        // Create a listener so we can test event
        TestTailListener listener = new TestTailListener();        
        tail.addTailListener(listener);

        // Create sinks for tail and attach them to the tail
        // Attach the input reader to the tail        
        StringWriter sw = new StringWriter();
        tail.follow(reader, sw, "testTailLifeCycle");
        
        // Lifecycle of tail
        tail.start();
        listener.waitForStart();
        ThreadUtil.sleep(1000);
        
        tail.pause();
        listener.waitForPause();
        ThreadUtil.sleep(1000);
        
        tail.unpause();
        listener.waitForUnpause();
        ThreadUtil.sleep(1000);
        
        tail.stop();
        listener.waitForStop();
        
        // Dump contents of the sinks
        logger_.info("OutputWriter sink:\n" + sw.toString());
        
        tail.removeTailListener(listener);
    }

    /**
     * Tests tailing of a stream
     * 
     * @throws Exception on error
     */
    public void testTailStream() throws Exception
    {
        // TODO: Implement testTailStream()
//       logger_.info("Running testTailStream...");
//       
//        PipedOutputStream pos = new PipedOutputStream();
//        PipedInputStream  pis = new PipedInputStream(pos);
//
//        ThreadUtil.run(
//            this,
//            "writeDelayed",
//            new Object[] {
//                pos,
//                new Integer(5),
//                new Integer(500),
//                "line " });
//
//        Tail tail = new Tail();
//        
//        // Create sinks for tail and attach them to the tail
//        StringOutputStream sos = new StringOutputStream();
//        StringWriter sw = new StringWriter();
//        tail.addOutputStream(sos);
//        tail.addWriter(sw);
//
//        // Create a listener so we can test event
//        TestTailListener listener = new TestTailListener();        
//        tail.addTailListener(listener);
//        
//        logger_.info(tail.toString());
//        
//        // Attach the input reader to the tail
//        tail.setTailReader(pis);
//        
//        // Lifecycle of tail
//        tail.start();
//        listener.waitForStart();
//        ThreadUtil.sleep(1000);
//        
//        tail.pause();
//        listener.waitForPause();
//        ThreadUtil.sleep(1000);
//        
//        tail.unpause();
//        listener.waitForUnpause();
//        ThreadUtil.sleep(1000);
//        
//        tail.stop();
//        listener.waitForStop();
//        
//        // Dump contents of the sinks
//        logger_.info("OutputStream sink:\n" + sos.getBuffer());
//        logger_.info("OutputWriter sink:\n" + sw.toString());
//        
//        tail.removeOutputStream(sos);
//        tail.removeWriter(sw);
//        tail.removeTailListener(listener);
    }

    
    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------
    
    /**
     * Writes output to a writer in a delayed fashion
     * 
     * @param writer Writer to send output to
     * @param iterations Number of times to iterate
     * @param delay Delay between writes in seconds
     * @param value String to send to writer
     */                
    public void writeDelayed(PipedWriter writer, int iterations, 
        int delay, String value)
    {
        logger_.info("Running writeDelayed...");
        
        PrintWriter pw = new PrintWriter(writer);
        
        for(int i=0; i<iterations; i++)
        {
            pw.println(i + " " + value);
            pw.flush();
            ThreadUtil.sleep(delay);
        }
        
        pw.close();
    }
    
    /**
     * Creates a paragraph full of random words.
     * 
     * @param lines Number of lines contained in the paragraph
     * @return Paragraph of random and gramatically incorrect sentences
     */
    protected String makeParagraph(int lines)
    {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i < lines; i++)
            sb.append(i + " " + makeSentence() + (i+1 == lines ? "" : "\n"));
        return sb.toString();
    }
    
    /**
     * Creates a sentence full of random words
     * 
     * @return Sentence
     */
    protected String makeSentence()
    {
        StringBuffer sb = new StringBuffer();
        for (int i=0, n = RandomUtil.nextInt(6,10); i<n; i++)
            sb.append(RandomUtil.nextElement(words_) + " ");
        return sb.toString();
    }
}

//--------------------------------------------------------------------------
// Helper Classes
//--------------------------------------------------------------------------

/**
 * Test tail listener 
 */
class TestTailListener implements TailListener
{
    private static final Logger logger_ = TailTest.logger_;
        
    private BlockingQueue startEvents_    = new BlockingQueue();
    private BlockingQueue stopEvents_     = new BlockingQueue();
    private BlockingQueue nextLineEvents_ = new BlockingQueue();
    private BlockingQueue endedEvents_    = new BlockingQueue();
    private BlockingQueue pauseEvents_    = new BlockingQueue();
    private BlockingQueue unpauseEvents_  = new BlockingQueue();

    /**
     * @see TailListener#nextLine(Tail, String)
     */
    public void nextLine(Tail tail, String line)
    {
        //logger_.debug(line);
        
        try
        {
            nextLineEvents_.push(line);    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
        
    }

    /**
     * Tail was started
     */
    public void tailStarted(Tail tail)
    {
        logger_.info("tail started");
        
        try
        {
            startEvents_.push("start");    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }
    
    /** 
     * Tail was stopped
     */
    public void tailStopped(Tail tail)
    {
        logger_.info("tail stopped");
        
        try
        {
            stopEvents_.push("stop");    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }
  
    /**
     * Tail was ended
     */
    public void tailEnded(Tail tail)
    {
        logger_.info("tail ended");
        
        try
        {
            endedEvents_.push("ended");    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }
    
    /** 
     * Tail was paused
     */
    public void tailPaused(Tail tail)
    {
        logger_.info("tail paused");
        
        try
        {
            pauseEvents_.push("pause");    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }
    
    /**
     * Tail was unpaused
     */
    public void tailUnpaused(Tail tail)
    {
        logger_.info("tail unpaused");
        
        try
        {
            unpauseEvents_.push("unpause");    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }
    
    /**
     * @see toolbox.tail.TailListener#tailReattached(toolbox.tail.Tail)
     */
    public void tailReattached(Tail tail)
    {
        logger_.info("Tail re-attached");
    }

    public void waitForStart() throws InterruptedException
    {
        startEvents_.pull();
    }
    
    public void waitForStop() throws InterruptedException
    {
        stopEvents_.pull();
    }
    
    public void waitForPause() throws InterruptedException
    {
        pauseEvents_.pull();
    }
    
    public void waitForUnpause() throws InterruptedException
    {
        unpauseEvents_.pull();
    }
    
    public void waitForEnded() throws InterruptedException
    {
        endedEvents_.pull();
    }
    
    public String waitForNextLine() throws InterruptedException
    {
        return (String) nextLineEvents_.pull();
    }
}