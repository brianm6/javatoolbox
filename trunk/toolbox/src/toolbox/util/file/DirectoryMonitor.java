package toolbox.util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.find.FileFinder;
import org.apache.commons.io.find.Finder;
import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.service.ObservableService;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceListener;
import toolbox.util.service.ServiceNotifier;
import toolbox.util.service.ServiceState;
import toolbox.util.service.ServiceTransition;
import toolbox.util.service.ServiceUtil;
import toolbox.util.service.Startable;
import toolbox.util.service.Suspendable;
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
 * @see toolbox.util.file.activity.FileCreatedActivity
 */
public class DirectoryMonitor 
    implements Startable, Suspendable, ObservableService {
    
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
     * Delay between each directory being processed in list of directories.
     */
    private int perDirectoryDelay_ = 100;

    /** 
     * List of directories to monitor for file activity.
     * 
     * @see java.io.File
     */
    private List directories_;
    
    /** 
     * Thread that interested listeners are notified on. 
     */
    private Thread monitor_;
    
    /**
     * State machine for the this monitors lifecycle.
     */
    private StateMachine stateMachine_;

    /**
     * Handles notification of service state changes.
     */
    private ServiceNotifier notifier_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DirectoryMonitor for the given directory.
     * 
     * @param dir Directory to monitor.
     */
    public DirectoryMonitor(File dir) {
        this(dir, false);
    }

    
    /**
     * Creates a DirectoryMonitor for the given directory and all known 
     * subdirectories.
     * 
     * @param dir Directory to monitor.
     * @param subdirs Set to true to also monitor subdirectories.
     */
    public DirectoryMonitor(File dir, boolean subdirs) {
        
        stateMachine_ = ServiceUtil.createStateMachine(this);
        notifier_ = new ServiceNotifier(this);
        directories_ = new ArrayList();
        listeners_ = new ArrayList();
        activities_ = new ArrayList();
        setDelay(5000);
        
        if (subdirs) {
            // Find all subdirs of the starting dir
            FileFinder finder = new FileFinder();
            Map findOptions = new HashMap();
            findOptions.put(Finder.TYPE, "d");
            
            logger_.debug("Finding all subdirs of " + dir + "...");
            
            File[] subdirectories = finder.find(dir, findOptions);
            
            logger_.debug("Found " + subdirectories.length + " subdirs total!");
            logger_.debug(ArrayUtil.toString(subdirectories, true));
            
            for (int i = 0; i < subdirectories.length; i++) 
                addDirectory(subdirectories[i]);
        }
        else {
            addDirectory(dir);
        }
    }
    
    //--------------------------------------------------------------------------
    // Startable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Starts execution of this directory monitor.
     * 
     * @throws IllegalStateException if monitor is already running.
     */
    public void start() throws IllegalStateException {
        stateMachine_.checkTransition(ServiceTransition.START);
        
        if (monitor_ != null && monitor_.isAlive())
            throw new IllegalStateException(
                "The directory monitor for " + 
                " ???" + //directory_.getName() +
                "already running.");

        monitor_ = 
            new Thread(new ActivityRunner(),
                "DirectoryMonitor[" 
                + " ??? " //directory_.getName() 
                + "]");
                        
        monitor_.start();
        stateMachine_.transition(ServiceTransition.START);
        notifier_.fireServiceStateChanged();
    }

    
    /**
     * Requests termination of this monitor. Does not block on termination
     * nor does it guarantee termination.
     * 
     * @see toolbox.util.service.Startable#stop()
     */
    public void stop() throws ServiceException {
        stateMachine_.checkTransition(ServiceTransition.STOP);
        
        try {
            logger_.debug("Stopping directory monitor..");
            stateMachine_.transition(ServiceTransition.STOP);

            // wait at most 10 secs for monitor to shutdown
            monitor_.join(1000);
            
            if (monitor_.isAlive()) {
                logger_.debug("Monitor did not die gracefully. Interrupting...");
                monitor_.interrupt();
            }
            
            logger_.debug("Waiting for monitor to die...");
            monitor_.join();
            
            logger_.debug("Monitor stopped!");
            monitor_ = null;
            
            notifier_.fireServiceStateChanged();
        }
        catch (InterruptedException e) {
            throw new ServiceException(e);
        }
    }

    
    /*
     * @see toolbox.util.service.Startable#isRunning()
     */
    public boolean isRunning() {
        return getState() == ServiceState.RUNNING;
    }
    
    // -------------------------------------------------------------------------
    // Suspendable Interface
    // -------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Suspendable#suspend()
     */
    public void suspend() throws IllegalStateException, ServiceException {
        stateMachine_.checkTransition(ServiceTransition.SUSPEND);
        logger_.debug("Suspending directory monitor..");
        stateMachine_.transition(ServiceTransition.SUSPEND);
        notifier_.fireServiceStateChanged();
    }
    
    /*
     * @see toolbox.util.service.Suspendable#resume()
     */
    public void resume() throws IllegalStateException, ServiceException {
        stateMachine_.checkTransition(ServiceTransition.RESUME);
        logger_.debug("Resuming directory monitor..");
        stateMachine_.transition(ServiceTransition.RESUME);
        synchronized(monitor_) {
            monitor_.notify();
        }
        notifier_.fireServiceStateChanged();
    }
    
    /*
     * @see toolbox.util.service.Suspendable#isSuspended()
     */
    public boolean isSuspended() {
        return getState() == ServiceState.SUSPENDED;
    }
    
    // --------------------------------------------------------------------------
    // ObservableService Interface
    // --------------------------------------------------------------------------

    /*
     * @see toolbox.util.service.ObservableService#addServiceListener(toolbox.util.service.ServiceListener)
     */
    public void addServiceListener(ServiceListener listener){
        notifier_.addServiceListener(listener);
    }


    /*
     * @see toolbox.util.service.ObservableService#removeServiceListener(toolbox.util.service.ServiceListener)
     */
    public void removeServiceListener(ServiceListener listener){
        notifier_.removeServiceListener(listener);
    }

    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState() {
        return (ServiceState) stateMachine_.getState();
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * @param directory
     */
    public void addDirectory(File directory) {
        directories_.add(directory);
    }
    
    
    /**
     * @param directory
     */
    public void removeDirectory(File directory) {
        directories_.remove(directory);
    }

    
    /**
     * Returns the polling delay in milliseconds.
     *
     * @return int
     */
    public int getDelay() {
        return delay_;
    }

    
    /**
     * Sets the polling delay in milliseconds.
     *
     * @param newDelay Delay
     */
    public void setDelay(int newDelay) {
        delay_ = newDelay;
    }
   
    
    /**
     * Adds an activity to monitor.
     * 
     * @param activity Activity to monitor
     */
    public void addFileActivity(IFileActivity activity) {
        activities_.add(activity);
    }

    
    /**
     * Removes an activity from the list of monitored activities.
     * 
     * @param activity Activity to remove
     */
    public void removeFileActivity(IFileActivity activity) {
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
    protected void fireFileActivity(
        IFileActivity activity, 
        List affectedFileSnapshots) 
        throws Exception {
        
        // Iterator through listeners and file event
        for (Iterator i = listeners_.iterator(); i.hasNext();) {
            IDirectoryListener dirListener = (IDirectoryListener) i.next();
            dirListener.fileActivity(activity, affectedFileSnapshots);
        }
    }

    /**
     * Removes a listener from the list that is notified each time a file
     * becomes available.
     * 
     * @param listener Listener to remove from the notification list.
     */
    public void removeDirectoryListener(IDirectoryListener listener) {
        listeners_.remove(listener);
    }

    
    /**
     * Adds a listener to the list that's notified each time a new file is 
     * available.
     *
     * @param listener Listener to add to notification list.
     */
    public void addDirectoryListener(IDirectoryListener listener) {
        listeners_.add(listener);
    }

    //--------------------------------------------------------------------------
    // ActivityRunner
    //--------------------------------------------------------------------------
    
    /**
     * Runnable object that encapsulates the monitoring activity.
     */
    class ActivityRunner implements Runnable {
        
        /*
         * @see java.lang.Runnable#run()
         */
        public void run() {

            for (Iterator i = activities_.iterator(); i.hasNext();)
                logger_.debug("Checking activity: " + i.next());
            
            boolean first = true;
            
            // Check termination flag
            while (isRunning()) {
                
                logger_.debug(
                    (first ? "First" : "Update") 
                    + " scan started for " 
                    + directories_.size()
                    + " directories.");
                
                for (Iterator di = directories_.iterator(); 
                    di.hasNext() && isRunning();) {
                    
                    File dir = (File) di.next();

                    // DEBUG 
                    //logger_.debug("Scanning " + dir);
                        
                    for (Iterator i = activities_.iterator(); 
                         i.hasNext() && isRunning();) {
    
                        IFileActivity activity = (IFileActivity) i.next();
                 
                        List affectedFiles = activity.getAffectedFiles(dir);
                        
                        //logger_.debug(
                        //    "Active files in monitored dir "
                        //    + dir.getName()
                        //    + " = "
                        //    + ArrayUtil.toString(activeFiles));
        
                        // Eat exceptions so rest of listeners get serviced
                        try {
                            if (!affectedFiles.isEmpty())
                                fireFileActivity(activity, affectedFiles);
                        }
                        catch (Exception e) {
                            logger_.error("ActivityRunner.run", e);
                        }
                        
                        ThreadUtil.sleep(perDirectoryDelay_);
                    }
                }
            
                if (!first) {
                    synchronized (monitor_) {
                        try {
                            if (isSuspended()) {
                                // Wait indefinitely until resumed
                                monitor_.wait();
                            }
                            else {
                                logger_.trace("Waiting "
                                    + getDelay()
                                    + "ms until next scan...");
                                
                                // Wait until delay expires
                                monitor_.wait(getDelay()); 
                            }
                        }
                        catch (InterruptedException e) {
                            logger_.debug("Monitor thread interrupted!");
                        }
                    }
                }
                else {
                    first = false;
                }
            }
        }
    }
}