package toolbox.tivo;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;
import toolbox.util.ElapsedTime;
import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.dirmon.IDirectoryMonitorListener;
import toolbox.util.dirmon.event.FileEvent;
import toolbox.util.dirmon.event.StatusEvent;
import toolbox.util.dirmon.recognizer.FileCreatedRecognizer;

/**
 * Converts movies to tivo format.
 */
public class TivoConverter{

    // TODO: Don't consume files in incoming directory that are still growing
    //       in size.
    
    public static final Logger logger_ = 
        Logger.getLogger(TivoConverter.class);
    
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private String rootDir = "c:\\tivo";
    private String incomingDir = rootDir + "\\incoming";
    private String workingDir = rootDir + "\\working";
    private String errorDir = rootDir + "\\error";
    private String originalsDir = rootDir + "\\originals"; 
    private String goBackDir = rootDir + "\\goback";

    private DirectoryMonitor monitor_; 
    
    private BlockingQueue workQueue_;
    
    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------
    
    public static void main(String args[]) {
        Logger.getRootLogger().setLevel(Level.DEBUG);
        TivoConverter converter = new TivoConverter();
        converter.start();
    }
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public TivoConverter() {
    }
    
    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------
    
    public void start() {
        logger_.info("Starting TivoConverter...");
        logger_.info("Incoming  -> " + incomingDir);
        logger_.info("Working   -> " + workingDir);
        logger_.info("Error     -> " + errorDir);
        logger_.info("Originals -> " + originalsDir);
        
        makeDirStructure();
        setupDirMon();
        setupWorkQueue();
    }

    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------
    
    private void convert(String filename) throws Exception {

        File sourceFile = new File(filename);
        MovieInfoParser parser = new MovieInfoParser();
        ITranscoder transcoder = new FFMpegTranscoder();
        String shortSourceFilename = FilenameUtils.getName(filename);
        MovieInfo movieInfo = null;
        
        try {
            logger_.info(shorten(filename) + " : Querying info ...");
            
            movieInfo = parser.parse(filename);
            logger_.debug("\n\n" + movieInfo);
            
            String destFilename = buildTargetFilename(movieInfo);
            String shortDestFilename = FilenameUtils.getName(destFilename);
            
            logger_.info(shorten(filename) + " : Transcoding at " + movieInfo.getBitrate() + " kb/s ...");
            ElapsedTime timer = new ElapsedTime(new Date());
            transcoder.transcode(movieInfo, destFilename);
            timer.setEndTime();
            logger_.info(shorten(filename) + " : Transcoded in " + timer);
            
            logger_.info(shorten(filename) + " : Moving to completed dir ...");
            timer = new ElapsedTime(new Date());
            FileUtils.copyFileToDirectory(sourceFile, new File(originalsDir));
            timer.setEndTime();
            logger_.info(shorten(filename) + " : Move completed in " + timer); 
            
            logger_.info(shorten(destFilename) + " : Moving to goBack dir ...");
            timer = new ElapsedTime(new Date());
            File destFile = new File(destFilename);
            FileUtils.copyFileToDirectory(destFile, new File(goBackDir));
            timer.setEndTime();
            logger_.info(shorten(destFilename) + " : Move completed in " + timer); 
            
            sourceFile.delete();
            destFile.delete();
        }
        catch (Exception e) {
            logger_.error(
                shorten(filename) 
                + " : Transcoding failed with error '" 
                + e.getMessage()
                + "'");
            
            // move original to error directory
            ElapsedTime timer = new ElapsedTime(new Date());
            logger_.info(shorten(filename) + " : Moving to error dir ...");
            
            FileUtils.copyFileToDirectory(sourceFile, new File(errorDir));
            
            timer.setEndTime();
            logger_.info(shorten(filename) + " : Move completed in " + timer); 
            
            sourceFile.delete();
        }
    }


    private String buildTargetFilename(MovieInfo movieInfo) {
        String destFilename =  
            workingDir 
            + File.separator 
            + FilenameUtils.removeExtension(
                FilenameUtils.getName(movieInfo.getFilename()))
            + ".mpg";
        return destFilename;
    }

    
    private void setupWorkQueue() {
        workQueue_ = new LinkedBlockingQueue();
        
        while (true) {
        
            try {
                String filename = (String) workQueue_.take();
                convert(filename);
            }
            catch (Exception e) {
                logger_.error("kaboom", e);
            }
            
        }
    }
    
    private void setupDirMon() {
        
        monitor_ = new DirectoryMonitor(new File(incomingDir), false);
        monitor_.setDelay(30000);
        //monitor_.setName("incoming");
        monitor_.addRecognizer(new FileCreatedRecognizer(monitor_));
        monitor_.addDirectoryMonitorListener(new IncomingDirListener());
        monitor_.start();
    }
    
    private void makeDirStructure(){
        
        File f = new File(rootDir);
        f.mkdir();
        
        new File(incomingDir).mkdir();
        new File(workingDir).mkdir();
        new File(errorDir).mkdir();
        new File(originalsDir).mkdir();
        new File(goBackDir).mkdir();
    }
    
    private String shorten(String filename) {
        return FilenameUtils.getName(filename);
    }

    // -------------------------------------------------------------------------
    // IncomingDirListener
    // -------------------------------------------------------------------------
    
    class IncomingDirListener implements IDirectoryMonitorListener {
        
        public void directoryActivity(FileEvent changeEvent) throws Exception{
            
            switch (changeEvent.getEventType()) {
                
                case FileEvent.TYPE_FILE_CREATED:
                    
                    String newFile = 
                        changeEvent.getAfterSnapshot().getAbsolutePath();
                    
                    logger_.info(shorten(newFile) + " : Adding to work queue...");
                    workQueue_.add(newFile);
                    break;
            }
        }
        
        public void statusChanged(StatusEvent statusEvent) throws Exception{
        }
    }
}
