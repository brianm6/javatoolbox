package toolbox.util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ThreadUtil;

/**
 * DirectoryMonitor monitors a given directory for the availability of files
 * based on a configurable selection criteria. Interested parties can register
 * interest in these files by implementing the IDirectoryListener interface.
 */
public class DirectoryMonitor
{
    /** Logger */
    private static Logger logger_ = 
        Logger.getLogger(DirectoryMonitor.class);

    /** 
     * Notification list 
     */
    private List listeners_ = new ArrayList();
    
    /** 
     * File activities that this monitor will provide notification for  
     */
    private List activities_ = new ArrayList();

    /** 
     * Delay interval in ms used to check for new activity 
     */
    private int delay_ = 5000;

    /** 
     * Termination flag 
     */
    private boolean shutdown_ = false;

    /** 
     * Directory to monitor 
     */
    private File directory_;

    /** 
     * Thread that the activities are dispatched on 
     */
    private Thread monitor_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DirectoryMonitor with the given directory and selection policy
     * 
     * @param  dir  Directory to monitor for file activity
     */
    public DirectoryMonitor(File dir)
    {
        setDirectory(dir);
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Starts execution of the directory monitor
     * 
     * @throws IllegalStateException if monitor already running
     */
    public void start() throws IllegalStateException
    {
        if (monitor_ != null && monitor_.isAlive())
            throw new IllegalStateException(
                "The directory monitor is already running.");

        monitor_ = 
            new Thread(new ActivityRunner(),
                "DirectoryMonitor " + directory_.getName());
                        
        monitor_.start();
        shutdown_ = false;               
    }

    /**
     * Requests termination of the monitor. Does not block on termination
     * nor does it guarantee termination.
     * 
     * @throws InterruptedException when interrupted
     */
    public void stop() throws InterruptedException
    {
        String method = "[stop  ]";
        
        logger_.debug(method + "Shutting down..");
        shutdown_ = true;
        
        // wait at most 10 secs for monitor to shutdown
        monitor_.join(10000);
        monitor_ = null;
    }

    /**
     * Accessor for delay
     *
     * @return int
     */
    public int getDelay()
    {
        return delay_;
    }

    /**
     * Mutator for delay
     *
     * @param newDelay    Delay
     */
    public void setDelay(int newDelay)
    {
        delay_ = newDelay;
    }

    /**
     * Returns the directory.
     * 
     * @return File
     */
    public File getDirectory()
    {
        return directory_;
    }

    /**
     * Sets the directory.
     * 
     * @param directory The directory to set
     */
    public void setDirectory(File directory)
    {
        directory_ = directory;
    }

    /**
     * Adds an activity to monitor
     * 
     * @param activity  Activity to monitor
     */
    public void addFileActivity(IFileActivity activity)
    {
        activities_.add(activity);
    }

    /**
     * Removes an activity from the list of monitored activities
     * 
     * @param  activity  Activity to remove
     */
    public void removeFileActivity(IFileActivity activity)
    {
        activities_.remove(activity);
    }

    //--------------------------------------------------------------------------
    // Event Notification Support
    //--------------------------------------------------------------------------

    /**
     * Fires notification of file activity to the directory monitor listeners
     *
     * @param   actvitity  Activity that generated this event
     * @param   files      Files affected by the activity 
     * @throws  Exception on error
     */
    protected void fireFileActivity(IFileActivity activity, File[] files) 
        throws Exception
    {
        // Iterator through listeners and file event
        for (Iterator i = listeners_.iterator(); i.hasNext(); )
        {
            IDirectoryListener dirListener = (IDirectoryListener) i.next();
            dirListener.fileActivity(activity, files);
        }
    }

    /**
     * Removes a listener from the list that is notified each time
     * a file becomes available.
     *
     * @param    listener    Listener to remove from the notification list
     */
    public void removeDirectoryListener(IDirectoryListener listener)
    {
        listeners_.remove(listener);
    }

    /**
     * Adds a listener to the list that's notified each time a 
     * new file is available.
     *
     * @param    listener    Listener to add to notification list
     */
    public void addDirectoryListener(IDirectoryListener listener)
    {
        listeners_.add(listener);
    }


    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Starts running monitor
     */
    class ActivityRunner implements Runnable
    {
        public void run()
        {
            String method = "[run   ] ";

            // DEBUG 
            logger_.debug(method + "Monitoring: " + getDirectory());
            for (Iterator i = activities_.iterator(); i.hasNext(); )
                logger_.debug(method + "Activity: " + i.next());

    
            // Check termination flag
            while (!shutdown_)
            {
                // Loop thourh each activity
                for (Iterator i = activities_.iterator(); i.hasNext(); )
                {
                    IFileActivity activity = (IFileActivity)i.next();
                    
                    File[] activeFiles = activity.getFiles(getDirectory());
             
                    // Only notify if there is actually some activity       
                    if (activeFiles.length > 0)
                    {
                        logger_.debug(method + 
                            "Active files in monitored dir= " + 
                                ArrayUtil.toString(activeFiles));
        
                        // Eat exceptions so rest of listeners get serviced
                        try
                        {
                            fireFileActivity(activity, activeFiles);
                        }
                        catch(Exception e)
                        {
                            logger_.error(method + e.getMessage(), e);
                        }
                    }
                    
                    ThreadUtil.sleep(getDelay());
                }
            }
        }
    }
}
