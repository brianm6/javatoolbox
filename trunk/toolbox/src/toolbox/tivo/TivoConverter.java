package toolbox.tivo;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import toolbox.util.ElapsedTime;
import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.dirmon.IDirectoryMonitorListener;
import toolbox.util.dirmon.event.FileEvent;
import toolbox.util.dirmon.event.StatusEvent;
import toolbox.util.dirmon.recognizer.FileCreatedRecognizer;
import toolbox.util.dirmon.recognizer.FileCreationFinishedRecognizer;

/**
 * Converts movies to tivo format.
 */
public class TivoConverter{

    public static final Logger logger_ = Logger.getLogger(TivoConverter.class);
    
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private String rootDir = "c:\\tivo";
    private String incomingDir = rootDir + "\\incoming";
    private String workingDir = rootDir + "\\working";
    private String errorDir = rootDir + "\\error";
    private String originalsDir = rootDir + "\\originals"; 
    private String goBackDir = rootDir + "\\goback";
    private String logDir = rootDir + "\\logs";

    private DirectoryMonitor monitor_; 
    
    private BlockingQueue workQueue_;
    
    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------
    
    public static void main(String args[]) {
        Logger.getRootLogger().setLevel(Level.INFO);
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

    private void makeDirStructure(){
        
        File f = new File(rootDir);
        f.mkdir();
        
        new File(incomingDir).mkdir();
        new File(workingDir).mkdir();
        new File(errorDir).mkdir();
        new File(originalsDir).mkdir();
        new File(goBackDir).mkdir();
        new File(logDir).mkdir();
    }
    
    
    private void setupWorkQueue() {
        workQueue_ = new LinkedBlockingQueue();
        
        while (true) {
        
            try {
                String filename = (String) workQueue_.take();
                convert(filename);
            }
            catch (Exception e) {
                logger_.error("taking from workqueue", e);
            }
            
        }
    }
    
    
    private void setupDirMon() {
        monitor_ = new DirectoryMonitor(new File(incomingDir), false);
        monitor_.setDelay(10000);
        //monitor_.setName("incoming"); screws things up
        monitor_.addRecognizer(new FileCreatedRecognizer(monitor_));
        monitor_.addRecognizer(new FileCreationFinishedRecognizer(monitor_, 10));
        monitor_.addDirectoryMonitorListener(new IncomingDirListener());
        monitor_.start();
    }
    

    private void convert(String sourceFilename) throws Exception {

        File sourceFile = new File(sourceFilename);
        MovieInfoParser parser = new MovieInfoParser();
        ITranscoder transcoder = new FFMpegTranscoder(logDir);
        MovieInfo movieInfo = null;
        
        try {
            logger_.info(shorten(sourceFilename) + " : Querying info ...");
            
            movieInfo = parser.parse(sourceFilename);
            
            logger_.debug("\n\n" + movieInfo);
            
            String destFilename = buildDestFilename(movieInfo);
            transcodeMovie(sourceFilename, transcoder, movieInfo, destFilename);
            moveDestFileToGoBackDir(destFilename); 
            moveSourceFileToOriginalsDir(sourceFilename, sourceFile); 
        }
        catch (Exception e) {
            handleFailure(sourceFilename, sourceFile, e);
        }
    }

    private void handleFailure(
        String sourceFilename, 
        File sourceFile,
        Exception e) throws IOException{
        
        logger_.error(
            shorten(sourceFilename) 
            + " : Transcoding failed with error '" 
            + e.getMessage()
            + "'", e);
        
        // move original to error directory
        ElapsedTime timer = new ElapsedTime(new Date());
        logger_.info(shorten(sourceFilename) + " : Moving to error dir ...");
        FileUtils.copyFileToDirectory(sourceFile, new File(errorDir));
        timer.setEndTime();
        logger_.info(shorten(sourceFilename) 
            + " : Move to error dir completed in " + timer); 
        
        sourceFile.delete();
    }

    
    private void transcodeMovie(
        String sourceFilename, 
        ITranscoder transcoder, 
        MovieInfo movieInfo, 
        String destFilename) throws IOException, InterruptedException {
        
        logger_.info(shorten(sourceFilename) 
            + " : Transcoding at " 
            + movieInfo.getBitrate() + " kb/s ...");
        
        ElapsedTime t1 = new ElapsedTime();
        transcoder.transcode(movieInfo, destFilename);
        t1.setEndTime();
        
        logger_.info(shorten(sourceFilename) 
            + " : Transcoded in " + t1 + ", "   
            + FileUtils.byteCountToDisplaySize(
                new File(destFilename).length()));
    }

    
    private void moveSourceFileToOriginalsDir(
        String sourceFilename, 
        File sourceFile) throws IOException{
        
        logger_.info(shorten(sourceFilename) + " : Moving to completed dir ...");
        ElapsedTime t3 = new ElapsedTime();
        FileUtils.copyFileToDirectory(sourceFile, new File(originalsDir));
        sourceFile.delete();
        t3.setEndTime();
        logger_.info(shorten(sourceFilename) + " : Move completed in " + t3);
    }

    
    private void moveDestFileToGoBackDir(String destFilename) 
        throws IOException{
        
        logger_.info(shorten(destFilename) + " : Moving to goBack dir ...");
        ElapsedTime t2 = new ElapsedTime();
        File destFile = new File(destFilename);
        FileUtils.copyFileToDirectory(destFile, new File(goBackDir));
        destFile.delete();
        t2.setEndTime();
        logger_.info(shorten(destFilename) + " : Move completed in " + t2);
    }


    private String buildDestFilename(MovieInfo movieInfo) {
        String destFilename =  
            workingDir 
            + File.separator 
            + FilenameUtils.removeExtension(
                FilenameUtils.getName(movieInfo.getFilename()))
            + ".mpg";
        return destFilename;
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
                    String file = changeEvent.getAfterSnapshot().getAbsolutePath();
                    logger_.info(shorten(file) + " : Recognized creation...");
                    break;
                    
                case FileEvent.TYPE_FILE_CREATION_FINISHED:
                    String newFile = changeEvent.getAfterSnapshot().getAbsolutePath();
                    logger_.info(shorten(newFile) + " : Adding to work queue...");
                    workQueue_.add(newFile);
                    break;
            }
        }
        
        public void statusChanged(StatusEvent statusEvent) throws Exception{
        }
    }
}
