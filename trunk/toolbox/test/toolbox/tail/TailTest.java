package toolbox.tail;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.RandomUtil;
import toolbox.util.StringUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.file.FileStuffer;
import toolbox.util.io.NullWriter;
import toolbox.util.io.StringOutputStream;

/**
 * Unit test for {@link toolbox.tail.Tail}.
 */
public class TailTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(TailTest.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Word list used for test data. 
     */
    private String[] words_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(TailTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Builds the word list.
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

    /*
     * Tests tailing of a regular old file sitting around not doing anything.<p>
     * This implies the file:               <br>
     * - exists                             <br>
     * - has no locks                       <br>
     * - has no intended future activity    <br>
     * - can be deleted without consequence
     */
    
    /**
     * Tests tailing of a files backlog for a specific number of lines.
     * 
     * @throws Exception on error.
     */
    public void testTailBacklog() throws Exception
    {
        logger_.info("Running testTailBacklog...");

        File file = FileUtil.createTempFile();
        
        try
        {
            int backlog = 10;   // backlog lines to test
            int total = 200;    // num lines in test file

            String para = makeParagraph(total);
            FileUtil.setFileContents(file, para , false);
            MockTailListener listener = new MockTailListener();
                   
            Tail tail = new Tail();
            tail.follow(file, new NullWriter());
            tail.setBacklog(backlog);
            tail.addTailListener(listener);
            tail.start();
            listener.waitForStart();

            logger_.debug("toString: " + toString());
                 
            String[] paras = StringUtil.tokenize(para, "\n");
                   
            for (int i = 0; i < backlog; i++)
            {
                String line = listener.waitForNextLine();
                logger_.debug("Backlog line " + i + ": " + line);
                assertEquals(paras[i + total - backlog], line);
            }
                
            tail.stop();
            listener.waitForStop();
        }
        finally
        {
            FileUtil.deleteQuietly(file);
        }
        
        logger_.debug("Done!");
    }

    
    /**
     * Tests tailing a file.
     * 
     * @throws Exception on error.
     */
    public void testTailFile() throws Exception
    {
        logger_.info("Running testTailFile...");
       
        File tmpFile = FileUtil.createTempFile();
        FileStuffer tmpFileStuffer = new FileStuffer(tmpFile, 200);
        Writer sink = null;
         
        try
        {
            tmpFileStuffer.start();
            
            ThreadUtil.sleep(1000);
            
            Tail tail = new Tail();
            StringOutputStream sos = new StringOutputStream();
            sink = new OutputStreamWriter(sos);
            tail.follow(tmpFile, sink);
            tail.setBacklog(4);
            tail.start();
            
            ThreadUtil.sleep(1000);
            
            tail.stop();
            sink.flush();
            
            logger_.debug(StringUtil.banner(sos.toString()));
        }
        finally
        {
            tmpFileStuffer.stop();
            IOUtils.closeQuietly(sink);
            FileUtil.deleteQuietly(tmpFile);
        }
    }
    
    
    /**
     * Tests the lifecycle of a Tail object (start/stop/pause/unpause).
     * 
     * @throws Exception on error.
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
        MockTailListener listener = new MockTailListener();        
        tail.addTailListener(listener);

        // Create sinks for tail and attach them to the tail
        // Attach the input reader to the tail        
        StringWriter sw = new StringWriter();
        tail.follow(reader, sw, "testTailLifeCycle");
        
        // Lifecycle of tail
        tail.start();
        listener.waitForStart();
        ThreadUtil.sleep(1000);
        
        tail.suspend();
        listener.waitForPause();
        ThreadUtil.sleep(1000);
        
        tail.resume();
        listener.waitForUnpause();
        ThreadUtil.sleep(1000);
        
        tail.stop();
        listener.waitForStop();
        
        // Dump contents of the sinks
        logger_.debug("OutputWriter sink:\n" + sw.toString());
        
        tail.removeTailListener(listener);
        
        logger_.debug(tail.toString());
    }

    
    /**
     * Tests monkeying with the lifecycle of a tail.
     * 
     * @throws Exception on error.
     */
    public void testTailMonkeyWithLifeCycle() throws Exception
    {
        logger_.info("Running testTailMonkeyWithLifeCycle...");
       
        PipedWriter writer = new PipedWriter();
        PipedReader reader = new PipedReader(writer);

        ThreadUtil.run(
            this,
            "writeDelayed",
            new Object[] {
                writer,
                new Integer(20),
                new Integer(500),
                "line " });

        int d = 1000;
        
        Tail tail = new Tail();

        // Create a listener so we can test event
        MockTailListener listener = new MockTailListener();        
        tail.addTailListener(listener);

        // Create sinks for tail and attach them to the tail
        // Attach the input reader to the tail        
        StringWriter sw = new StringWriter();
        tail.follow(reader, sw, "testTailMonkeyWithLifeCycle");
        
        
        // Start twice - should print warning ----------------------------------
        tail.start();
        listener.waitForStart();
        
        try
        {
            tail.start();
        }
        catch (IllegalStateException ise)
        {
            logger_.debug("SUCCESS: " + ise);
        }
        
        ThreadUtil.sleep(d);
        
        // Pause twice ---------------------------------------------------------
        tail.suspend();
        listener.waitForPause();
        
        try
        {
            tail.suspend();
        }
        catch (IllegalStateException ise)
        {
            logger_.debug("SUCCESS: " + ise);
        }
        ThreadUtil.sleep(d);
        
        // Unpause twice -------------------------------------------------------
        tail.resume();
        listener.waitForUnpause();
        
        try
        {
            tail.resume();
        }
        catch (IllegalStateException ise)
        {
            logger_.debug("SUCCESS: " + ise);
        }

        ThreadUtil.sleep(d);
        
        // Stop twice ----------------------------------------------------------
        tail.stop();
        listener.waitForStop();
        
        try
        {
            tail.stop();
        }
        catch (IllegalStateException ise)
        {
            logger_.debug("SUCCESS: " + ise);
        }

        // Stop while tail is paused -------------------------------------------
        tail.start();
        listener.waitForStart();
        tail.suspend();
        listener.waitForPause();
        
        try
        {
            tail.stop();
        }
        catch (IllegalStateException ise)
        {
            logger_.debug("SUCCESS: " + ise);
        }

        //listener.waitForStop();

        // ---------------------------------------------------------------------
        
        // Dump contents of the sinks
        logger_.debug("OutputWriter sink:\n" + sw.toString());
        
        tail.removeTailListener(listener);
    }

    
    /**
     * Tests tailing of a stream
     * 
     * @throws Exception on error.
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
//        logger_.debug(tail.toString());
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
//        logger_.debug("OutputStream sink:\n" + sos.getBuffer());
//        logger_.debug("OutputWriter sink:\n" + sw.toString());
//        
//        tail.removeOutputStream(sos);
//        tail.removeWriter(sw);
//        tail.removeTailListener(listener);
    }

    
    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------
    
    /**
     * Writes output to a writer in a delayed fashion.
     * 
     * @param writer Writer to send output to.
     * @param iterations Number of times to iterate.
     * @param delay Delay between writes in seconds.
     * @param value String to send to writer.
     */                
    public void writeDelayed(
            PipedWriter writer, 
            int iterations, 
            int delay, 
            String value)
    {
        logger_.debug("Running writeDelayed...");
        
        PrintWriter pw = new PrintWriter(writer);
        
        for (int i = 0; i < iterations; i++)
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
     * @param lines Number of lines contained in the paragraph.
     * @return Paragraph of random and gramatically incorrect sentences.
     */
    protected String makeParagraph(int lines)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < lines; i++)
            sb.append(i + " " + makeSentence() + (i + 1 == lines ? "" : "\n"));
        return sb.toString();
    }
    
    
    /**
     * Creates a sentence full of random words.
     * 
     * @return Sentence.
     */
    protected String makeSentence()
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, n = RandomUtil.nextInt(6, 10); i < n; i++)
            sb.append(RandomUtil.nextElement(words_) + " ");
        return sb.toString();
    }
}

//--------------------------------------------------------------------------
// Helper Classes
//--------------------------------------------------------------------------

/**
 * Test tail listener.
 */
class MockTailListener implements TailListener
{
    private static final Logger logger_ = 
        Logger.getLogger(MockTailListener.class);
        
    private BlockingQueue startEvents_    = new LinkedBlockingQueue();
    private BlockingQueue stopEvents_     = new LinkedBlockingQueue();
    private BlockingQueue nextLineEvents_ = new LinkedBlockingQueue();
    private BlockingQueue endedEvents_    = new LinkedBlockingQueue();
    private BlockingQueue pauseEvents_    = new LinkedBlockingQueue();
    private BlockingQueue unpauseEvents_  = new LinkedBlockingQueue();

    /**
     * @see TailListener#nextLine(Tail, String)
     */
    public void nextLine(Tail tail, String line)
    {
        //logger_.debug(line);
        
        try
        {
            nextLineEvents_.put(line);    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
        
    }

    
    /**
     * @see toolbox.tail.TailListener#tailStarted(toolbox.tail.Tail)
     */
    public void tailStarted(Tail tail)
    {
        logger_.debug("tail started");
        
        try
        {
            startEvents_.put("start");    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }
    

    /**
     * @see toolbox.tail.TailListener#tailStopped(toolbox.tail.Tail)
     */
    public void tailStopped(Tail tail)
    {
        logger_.debug("tail stopped");
        
        try
        {
            stopEvents_.put("stop");    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }

    
    /**
     * @see toolbox.tail.TailListener#tailEnded(toolbox.tail.Tail)
     */
    public void tailEnded(Tail tail)
    {
        logger_.debug("tail ended");
        
        try
        {
            endedEvents_.put("ended");    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }
    
    
    /**
     * @see toolbox.tail.TailListener#tailPaused(toolbox.tail.Tail)
     */
    public void tailPaused(Tail tail)
    {
        logger_.debug("tail paused");
        
        try
        {
            pauseEvents_.put("pause");    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }
    

    /**
     * @see toolbox.tail.TailListener#tailUnpaused(toolbox.tail.Tail)
     */
    public void tailUnpaused(Tail tail)
    {
        logger_.debug("tail unpaused");
        
        try
        {
            unpauseEvents_.put("unpause");    
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
        logger_.debug("Tail re-attached");
    }

    
    /**
     * Waits for the start event.
     * 
     * @throws InterruptedException on interruption.
     */
    public void waitForStart() throws InterruptedException
    {
        startEvents_.take();
    }
    
    
    /**
     * Waits for the stop event.
     * 
     * @throws InterruptedException on interruption.
     */
    public void waitForStop() throws InterruptedException
    {
        stopEvents_.take();
    }
    
    
    /**
     * Waits for the pause event.
     * 
     * @throws InterruptedException on interruption.
     */
    public void waitForPause() throws InterruptedException
    {
        pauseEvents_.take();
    }
    
    
    /**
     * Waits for the unpause event.
     * 
     * @throws InterruptedException on interruption.
     */
    public void waitForUnpause() throws InterruptedException
    {
        unpauseEvents_.take();
    }
    
    
    /**
     * Waits for the end event.
     * 
     * @throws InterruptedException on interruption.
     */
    public void waitForEnded() throws InterruptedException
    {
        endedEvents_.take();
    }
    
    
    /**
     * Waits for the next line event.
     * 
     * @return Next line.
     * @throws InterruptedException on interruption.
     */
    public String waitForNextLine() throws InterruptedException
    {
        return (String) nextLineEvents_.take();
    }
}