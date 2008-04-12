package toolbox.util.dirmon.recognizer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.collections.ListUtils;
import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.dirmon.DirSnapshot;
import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.dirmon.FileSnapshot;
import toolbox.util.dirmon.IDirectoryMonitorListener;
import toolbox.util.dirmon.IFileActivityRecognizer;
import toolbox.util.dirmon.event.FileEvent;
import toolbox.util.dirmon.event.StatusEvent;

/**
 * Recognizes files that have been created and unchanged for a given amount of
 * time. Why is this important? Well, if you're copying a 100MB file into a
 * directory, it will get recognized as created but you can't do much of of
 * anything with it until the entire file has completed being copied which may
 * be 10 seconds or 10 minutes later depending on where the file is coming from
 * and how it is being copied. Whats really important is that you want to know
 * after the creation when you can actually start doing something that requires
 * the existence of the entire file, not just a part of it.
 * <p>
 * The {@link toolbox.util.dirmon.recognizer.FileCreatedRecognizer} is used to
 * identify newly created files so this recognizer only needs to make sure that
 * the file hasn't been mutated for a given number of seconds. As such, the
 * FileCreatedRecognizer must already be registered with the DirectoryMonitor
 * for this recognizer to function correctly.
 * <p>
 * This recognizer is unique that it will not be able to recognize the files
 * when the DirectoryMonitor makes a callback using the
 * {@link #getRecognizedEvents(DirSnapshot, DirSnapshot)} method. Instead, this
 * recognizer registers itself as a IDirectoryMonitorListener and uses the file
 * creation event as a cue to start monitoring a file for inactivity compliments
 * of a {@link java.util.Timer}.
 */
public class FileCreationFinishedRecognizer 
    implements IFileActivityRecognizer, IDirectoryMonitorListener {

    private static Logger logger_ =  
        Logger.getLogger(FileCreationFinishedRecognizer.class);

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    /**
     * Directory monitor that this recognizer is associated with.
     */
    private DirectoryMonitor monitor_;
    
    /**
     * The minimum number of seconds the file has been inactive after creation. 
     * Inactive implies that the file's size and timestamp have not changed.
     */
    private int inactiveFor_;
    
    /**
     * Timer on which checks for file inactivity are make after a file's 
     * creation.
     */
    private Timer timer_;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Creates a FileCreationFinishedRecognizer.
     * 
     * @param monitor Directory monitor that this recognizer is associated 
     *        with.
     * @param inactiveFor Number of seconds that a file must be inactive.
     */
    public FileCreationFinishedRecognizer(
        DirectoryMonitor monitor, 
        int inactiveFor) {
        
        monitor_ = monitor;
        monitor_.addDirectoryMonitorListener(this);
        inactiveFor_ = inactiveFor;
        timer_ = new Timer(true);
    }

    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------
    
    private void scheduleCheck(FileEvent creationEvent){
        TimerTask task = new CheckInactivityTask(creationEvent);
        timer_.schedule(task, inactiveFor_ * 1000);
    }

    // -------------------------------------------------------------------------
    // IDirectoryMonitorListener Interface
    // -------------------------------------------------------------------------

    /**
     * Once we get notification that a file has been created, monitor it for
     * inactivity.
     * 
     * @see toolbox.util.dirmon.IDirectoryMonitorListener#directoryActivity(toolbox.util.dirmon.event.FileEvent)
     */
    public void directoryActivity(FileEvent changeEvent) throws Exception {
        
        switch (changeEvent.getEventType()) {
            
            case FileEvent.TYPE_FILE_CREATED:
                scheduleCheck(changeEvent);
                break;
        }
    }

    /**
     * Not intereted in status changed events so this is a NO-OP.
     * 
     * @see toolbox.util.dirmon.IDirectoryMonitorListener#statusChanged(toolbox.util.dirmon.event.StatusEvent)
     */
    public void statusChanged(StatusEvent statusEvent) throws Exception {
        // NOOP
    }
    
    // -------------------------------------------------------------------------
    // IFileActivityRecognizer Interface
    // -------------------------------------------------------------------------

    /** 
     * Files are not recognized on the DirectoryMonitor's time table so we have 
     * nothing to return here but an empty list.
     * 
     * @see IFileActivityRecognizer#getRecognizedEvents(DirSnapshot, DirSnapshot)
     */
    public List getRecognizedEvents(DirSnapshot before, DirSnapshot after) {
        return ListUtils.EMPTY_LIST;
    }
    
    // -------------------------------------------------------------------------
    // CheckInactivityTask
    // -------------------------------------------------------------------------
    
    class CheckInactivityTask extends TimerTask {
        
        private FileEvent creationEvent_;
        
        
        public CheckInactivityTask(FileEvent creationEvent) {
            creationEvent_ = creationEvent;
        }
 
        
        public void run() {
            
            FileSnapshot creationSnapshot = creationEvent_.getAfterSnapshot();
            File currentFile = creationSnapshot.toFile();
            
            if (!currentFile.exists()) {
                // File was apparently deleted, moved, or renamed since the
                // time of creation and no longer exists.
                logger_.debug(
                    "Created file "
                    + currentFile
                    + " no longer exists. Ceasing check for completion of writing.");
                return;
            }
            
            FileSnapshot currentSnapshot = new FileSnapshot(currentFile);
            
            RandomAccessFile raf = null;
            
            //logger_.debug(StringUtil.banner(
            //    "Creation snapshot:\n" + AsMap.of(creationSnapshot)));
            
            //logger_.debug(StringUtil.banner(
            //    "Current  snapshot:\n" + AsMap.of(currentSnapshot)));
            
            try {
                raf = new RandomAccessFile(currentFile, "r");
                
                // If seeking to the end of the file succeeds, the file is
                // apparently all there and can be consumed by other processes.
                raf.seek(currentSnapshot.getLength() - 1);

                logger_.debug(
                    "File " 
                    + creationSnapshot.getAbsolutePath() 
                    + " recognized as finsiehd!");
                
                FileEvent finishedEvent = 
                    new FileEvent(
                        FileEvent.TYPE_FILE_CREATION_FINISHED,
                        monitor_,
                        creationSnapshot,
                        currentSnapshot);
                
                monitor_.fireDirectoryActivity(finishedEvent);
            }
            catch (IOException ioe) {
                //logger_.error("seek error" , ioe);
                
                logger_.debug(
                    "File " 
                    + creationSnapshot.getAbsolutePath() 
                    + " still changing...checking again in "
                    + inactiveFor_
                    + " seconds.");
                
                // Keep waiting by scheduling another check...
                FileEvent event = new FileEvent(
                    FileEvent.TYPE_FILE_CREATED,
                    monitor_,
                    creationSnapshot,
                    currentSnapshot);
                
                scheduleCheck(event);
            }
            catch (Exception e) {
                logger_.error("non-io error on file completed recognization" , e);
            }
            finally {
                FileUtil.closeQuietly(raf);
            }
            

//            if (creationSnapshot.getLastModified() == 
//                currentSnapshot.getLastModified()) {
//                
//                // Criteria met..fire an event!!
//
//                logger_.debug("File " + creationSnapshot.getAbsolutePath() + " recognized as finsiehd!");
//                
//                try {
//                    FileEvent finishedEvent = 
//                        new FileEvent(
//                            FileEvent.TYPE_FILE_CREATION_FINISHED,
//                            monitor_,
//                            creationSnapshot,
//                            currentSnapshot);
//                    
//                    // TODO: Verify this!
//                    monitor_.fireDirectoryActivity(finishedEvent);
//                }
//                catch (Exception e) {
//                    logger_.error(e);
//                }
//            }
//            else {
//                
//                logger_.debug("File " + creationSnapshot.getAbsolutePath() + " still changing...checking again..");
//                // Keep waiting by scheduling another check...
//                FileEvent event = new FileEvent(
//                    FileEvent.TYPE_FILE_CREATED,
//                    monitor_,
//                    creationSnapshot,
//                    currentSnapshot);
//                
//                scheduleCheck(event);
//            }
        }
    }
}