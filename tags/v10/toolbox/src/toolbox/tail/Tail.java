package toolbox.tail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Category;
import sun.security.krb5.internal.i;
import sun.security.krb5.internal.w;
import sun.security.krb5.internal.crypto.e;
import sun.security.krb5.internal.crypto.j;
import sun.security.krb5.internal.util.k;
import toolbox.util.ThreadUtil;

/**
 * @author analogue
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class Tail
{
    public static final int NUM_LINES_BACKLOG = 10;
    
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(Tail.class);
    
    /** Collection of listeners **/
    private List listeners_ = new ArrayList();

    private List streams_ = new ArrayList();
    
    private List writers_ = new ArrayList();
         
    /** Thread that runner is associated with **/
    private Thread thread_;
 
    /** Reader to tail **/
    private Reader reader_;

 
    class TailRunner implements Runnable
    {
        Reader reader_;
        boolean paused_ = false;       
    
        /**
         * Creates a TailRunner
         * 
         * @arg  filename   Name of file to tail
         */
        public TailRunner(Reader reader)
        {
            reader_ = reader;
        }
    
        public void pause()
        {
            paused_ = true;
        }
        
        public void unpause()
        {
            paused_ = false;
        }
        
        public boolean isPaused()
        {
            return paused_;
        }
    
        /**
         * Runnable interface 
         */
        public void run()
        {
            try
            {
                LineNumberReader lnr = new LineNumberReader(reader_);
    
                int cnt = 0;
                lnr.mark(1000);
    
                synchronized (TailRunner.class)
                {
                    while (lnr.ready())
                    {
                        cnt++;
                        if ((cnt % NUM_LINES_BACKLOG) == 0)
                            lnr.mark(1000);
                        lnr.readLine();
                    }
                }
    
                lnr.reset();
                
                boolean atEnd = false;
                
                while (!atEnd)
                {
                    /* loop de loop while paused */
                    while(paused_)
                    {
                        fireTailPaused();
                        
                        while(paused_)
                            ThreadUtil.sleep(1);
                            
                        fireTailUnpaused();
                    }
                    
                    String line = lnr.readLine();
                    
                    if (line != null)
                    {
                        fireNextLine(line+"\n");
                        ThreadUtil.sleep(1);
                    }
                    else
                    {
                        atEnd = true;
                        fireTailEnded();
                    }
                }
            }
            catch (Exception e)
            {
                logger_.error("run", e);
            }
        }    
    }

    /** Tail dispatches on this runner **/
    TailRunner runner_;

             
    /**
     * Constructor for Tail.
     */
    public Tail()
    {
    }
    
    public void tail(String filename) throws FileNotFoundException
    {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(filename);
        tail(fis);
    }
    
    public void tail(InputStream stream)
    {
        InputStreamReader reader = new InputStreamReader(stream);
        tail(reader);    
    }
    
    public void tail(Reader reader)
    {
        reader_ = reader;
        start();
    }
    
    public void start()
    {
        if (thread_ == null || !thread_.isAlive())
        {
            runner_ = new TailRunner(reader_);
            thread_ = new Thread(runner_);
            thread_.start();
            fireTailStarted();
        }
        else
             logger_.warn("Tail is already running");
    }
    
    public void stop()
    {
        if (thread_.isAlive())
        {
            try
            {
                reader_.close();
                thread_.interrupt();
                thread_.join();
            }
            catch (IOException e)
            {
                logger_.error("stop", e);
            }
            catch (InterruptedException e)
            {
                logger_.error("stop", e);
            }
            finally
            {
                fireTailStopped();
            }
        }
        else
            logger_.warn("Tail is already stopped");
      }

    public void pause()
    {
        if (thread_.isAlive() && !runner_.isPaused())
           runner_.pause();
    }

    public void unpause()
    {
        if (thread_.isAlive() && runner_.isPaused())
            runner_.unpause();
    }

    public void join()
    {
        try 
        {
			if (thread_.isAlive())
	            thread_.join();
		} 
        catch(InterruptedException e) 
        {
            logger_.error("join", e);    
		}
        
    }
  
    public void addTailListener(ITailListener listener)
    {
        listeners_.add(listener);
    }

    public void addWriter(Writer writer)
    {
        writers_.add(writer);
    }
    
    public void removeTailListener(ITailListener listener)
    {
        listeners_.remove(listener);
    }
  
    public void addOutputStream(OutputStream os)
    {
        streams_.add(os);
    }
    
    public void removeOutputStream(OutputStream os)
    {
        streams_.remove(os);
    }
    
    public void fireNextLine(String line)
    {
        for(int i=0; i<listeners_.size(); i++)
        {
            try
            {
                ITailListener listener = (ITailListener) listeners_.get(i);
                listener.nextLine(line);

            }
            catch (Exception e)
            {
                logger_.error("fireNextLine", e);
            }
        }
        
        for (int j=0; j<streams_.size(); j++)
        {
        	
            try 
            {
				OutputStream os = (OutputStream)streams_.get(j);
	            os.write(line.getBytes());
                os.flush();
			}  
			catch(IOException e) 
			{
			    logger_.error("fireNextLine", e); 	
			}
        }
        
        for (int k=0; k<writers_.size(); k++)
        {
            try
            {
                Writer w = (Writer)writers_.get(k);
                w.write(line);
                w.flush();
            }
            catch (IOException e)
            {
                logger_.error("fireNextLine", e);
            }            
        }                    
    }

    public void fireTailStopped()
    {
        for(int i=0; i<listeners_.size(); i++)
        {
            try
            {
                ITailListener listener = (ITailListener) listeners_.get(i);
                listener.tailStopped();

            }
            catch (Exception e)
            {
                logger_.error("fireTailStopped", e);
            }
        }
    }

    public void fireTailStarted()
    {
        for(int i=0; i<listeners_.size(); i++)
        {
            try
            {
                ITailListener listener = (ITailListener) listeners_.get(i);
                listener.tailStarted();

            }
            catch (Exception e)
            {
                logger_.error("fireTailStarted", e);
            }
        }
    }

    public void fireTailEnded()
    {
        for(int i=0; i<listeners_.size(); i++)
        {
            try
            {
                ITailListener listener = (ITailListener) listeners_.get(i);
                listener.tailEnded();

            }
            catch (Exception e)
            {
                logger_.error("fireTailEnded", e);
            }
        }
    }


    public void fireTailUnpaused()
    {
        for(int i=0; i<listeners_.size(); i++)
        {
            try
            {
                ITailListener listener = (ITailListener) listeners_.get(i);
                listener.tailUnpaused();

            }
            catch (Exception e)
            {
                logger_.error("fireTailUnpaused", e);
            }
        }
    }


    public void fireTailPaused()
    {
        for(int i=0; i<listeners_.size(); i++)
        {
            try
            {
                ITailListener listener = (ITailListener) listeners_.get(i);
                listener.tailPaused();

            }
            catch (Exception e)
            {
                logger_.error("fireTailPaused", e);
            }
        }
    }

   
    public String toString()
    {
        return "Listeners = " + listeners_.size() + "\n" +
                "Streams   = " + streams_.size() + "\n" +
                "Writers   = " + writers_.size() + "\n";
                
    }
}