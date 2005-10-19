package toolbox.util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceState;
import toolbox.util.service.ServiceTransition;
import toolbox.util.service.ServiceUtil;
import toolbox.util.service.Startable;
import toolbox.util.statemachine.StateMachine;

/**
 * Monitors a directory for file activity based on a configurable selection 
 * criteria. Interested parties can register for notification of activity by 
 * implementing the {@link toolbox.util.file.IDirectoryListener} interface.
 * <p>
 * <b>Example:</b>
 * <pre class="snippet">
 * 
 * // Monitor the current directory
 * DirectoryMonitor dm = new DirectoryMonitor(new File("."));
 * 
 * // Lets listen for newly created files
 * dm.addFileActivity(new FileCreatedActivity());
 * 
 * // Register a listener
 * dm.addDirectoryListener(new IDirectoryListener() {
 *     public void fileActivity(IFileActivity activity, File[] files)
 *         throws Exception {
 *         System.out.println("Files created: " + ArrayUtil.toString(files));  
 *      }
 *  });
 *
 * // Starts the monitor asynchronously
 * dm.start();
 *
 * // Directory now being monitored...any file activity in the directory will
 * // be reported via the IDirectoryListener interface.
 *
 * // All done..shutdown 
 * dm.stop();
 * </pre>
 * 
 * @see toolbox.util.file.IFileActivity
 * @see toolbox.util.file.IDirectoryListener
 * @see toolbox.util.file.FileCreatedActivity
 */
public class DirectoryMonitor implements Startable {
    
    private static Logger logger_ =  Logger.getLogger(DirectoryMonitor.class);

    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /** 
     * List of listeners interested in file activity that is monitored by this
     * DirectoryMonitor. 
     * 
     * @see IDirectoryListener
     */
    private List listeners_;
    
    /** 
     * List of file activities that this monitor will provide notification for.
     * 
     * @see IFileActivity
     */
    private List activities_;

    /** 
     * Delay interval in millis used to check for new activity. Defaults to 5 
     * seconds.
     */
    private int delay_;

    /** 
     * Directory to monitor. 
     */
    private File directory_;

    /** 
     * Thread that interested listeners are notified on. 
     */
    private Thread monitor_;
    
    /**
     * State machine for the this monitors lifecycle.
     */
    private StateMachine stateMachine_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DirectoryMonitor for the given directory.
     * 
     * @param dir Directory to monitor.
     */
    public DirectoryMonitor(File dir)
    {
        stateMachine_ = ServiceUtil.createStateMachine(this);
        listeners_ = new ArrayList();
        activities_ = new ArrayList();
        setDirectory(dir);
        setDelay(5000);
    }

    //--------------------------------------------------------------------------
    // Startable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Starts execution of the directory monitor.
     * 
     * @throws IllegalStateException if monitor already running.
     */
    public void start() throws IllegalStateException
    {
        stateMachine_.checkTransition(ServiceTransition.START);
        
        if (monitor_ != null && monitor_.isAlive())
            throw new IllegalStateException(
                "The directory monitor for " + 
                directory_.getName() +
                "already running.");

        monitor_ = 
            new Thread(new ActivityRunner(),
                "DirectoryMonitor[" + directory_.getName() + "]");
                        
        monitor_.start();
        stateMachine_.transition(ServiceTransition.START);
    }

    
    /**
     * Requests termination of the monitor. Does not block on termination
     * nor does it guarantee termination.
     * 
     * @see toolbox.util.service.Startable#stop()
     */
    public void stop() throws ServiceException
    {
        stateMachine_.checkTransition(ServiceTransition.STOP);
        
        try
        {
            logger_.debug("Shutting down..");
            stateMachine_.transition(ServiceTransition.STOP);
            
            // wait at most 10 secs for monitor to shutdown
            monitor_.join(10000);
            monitor_ = null;
        }
        catch (InterruptedException e)
        {
            throw new ServiceException(e);
        }
    }

    
    /**
     * @see toolbox.util.service.Startable#isRunning()
     */
    public boolean isRunning()
    {
        return getState() == ServiceState.RUNNING;
    }
    
    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState()
    {
        return (ServiceState) stateMachine_.getState();
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the polling delay in milliseconds.
     *
     * @return int
     */
    public int getDelay()
    {
        return delay_;
    }

    
    /**
     * Sets the polling delay in milliseconds.
     *
     * @param newDelay Delay
     */
    public void setDelay(int newDelay)
    {
        delay_ = newDelay;
    }

    
    /**
     * Returns the directory being monitored for activity.
     * 
     * @return File
     */
    public File getDirectory()
    {
        return directory_;
    }

    
    /**
     * Sets the directory being monitored for activity.
     * 
     * @param directory The directory to monitor.
     */
    public void setDirectory(File directory)
    {
        directory_ = directory;
    }

    
    /**
     * Adds an activity to monitor.
     * 
     * @param activity Activity to monitor
     */
    public void addFileActivity(IFileActivity activity)
    {
        activities_.add(activity);
    }

    
    /**
     * Removes an activity from the list of monitored activities.
     * 
     * @param activity Activity to remove
     */
    public void removeFileActivity(IFileActivity activity)
    {
        activities_.remove(activity);
    }

    //--------------------------------------------------------------------------
    // Event Notification Support
    //--------------------------------------------------------------------------

    /**
     * Fires notification of file activity to the directory monitor listeners.
     *
     * @param activity Activity that generated this event.
     * @param files Files affected by the activity.
     * @throws Exception on error.
     */
    protected void fireFileActivity(IFileActivity activity, File[] files) 
        throws Exception
    {
        // Iterator through listeners and file event
        for (Iterator i = listeners_.iterator(); i.hasNext();)
        {
            IDirectoryListener dirListener = (IDirectoryListener) i.next();
            dirListener.fileActivity(activity, files);
        }
    }

    
    /**
     * Removes a listener from the list that is notified each time a file
     * becomes available.
     * 
     * @param listener Listener to remove from the notification list.
     */
    public void removeDirectoryListener(IDirectoryListener listener)
    {
        listeners_.remove(listener);
    }

    
    /**
     * Adds a listener to the list that's notified each time a new file is 
     * available.
     *
     * @param listener Listener to add to notification list.
     */
    public void addDirectoryListener(IDirectoryListener listener)
    {
        listeners_.add(listener);
    }

    //--------------------------------------------------------------------------
    // ActivityRunner
    //--------------------------------------------------------------------------
    
    /**
     * Runnable object that encapsulates the monitoring activity.
     */
    class ActivityRunner implements Runnable
    {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            // DEBUG 
            logger_.debug("Monitoring: " + getDirectory());
            
            for (Iterator i = activities_.iterator(); i.hasNext();)
                logger_.debug("Checking activity: " + i.next());
    
            // Check termination flag
            while (isRunning())
            {
                // Loop through each activity
                for (Iterator i = activities_.iterator(); i.hasNext();)
                {
                    IFileActivity activity = (IFileActivity) i.next();
                    
                    File[] activeFiles = activity.getFiles(getDirectory());
             
                    logger_.debug("Active files in monitored dir= " + 
                        ArrayUtil.toString(activeFiles));
    
                    // Eat exceptions so rest of listeners get serviced
                    try
                    {
                        fireFileActivity(activity, activeFiles);
                    }
                    catch (Exception e)
                    {
                        logger_.error("ActivityRunner.run", e);
                    }
                    
                    ThreadUtil.sleep(getDelay());
                }
            }
        }
    }
}