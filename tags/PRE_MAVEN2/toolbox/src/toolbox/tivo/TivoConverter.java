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
    
    private String rootDir_ = "c:\\tivo";
    private String incomingDir_;
    private String workingDir_;
    private String errorDir_;
    private String originalsDir_; 
    private String goBackDir_;
    private String logDir_;

    private DirectoryMonitor monitor_; 
    private BlockingQueue workQueue_;
    
    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------
    
    public static void main(String args[]) {
        Logger.getRootLogger().setLevel(Level.INFO);
        TivoConverter converter = null;
        
        if (args.length == 0)
            converter = new TivoConverter();
        else
            converter = new TivoConverter(args[0]);
        
        converter.start();
    }
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public TivoConverter() {
        this("z:\\tivo");
    }
    
    
    public TivoConverter(String rootDir) {
        rootDir_ = rootDir;
        incomingDir_ = rootDir_ + "\\incoming";
        workingDir_ = rootDir_ + "\\working";
        errorDir_ = rootDir_ + "\\error";
        originalsDir_ = rootDir_ + "\\originals"; 
        goBackDir_ = rootDir_ + "\\goback";
        logDir_ = rootDir_ + "\\logs";
    }
    
    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------
    
    public void start() {
        logger_.info("Starting TivoConverter...");
        logger_.info("Incoming  -> " + incomingDir_);
        logger_.info("Working   -> " + workingDir_);
        logger_.info("Error     -> " + errorDir_);
        logger_.info("Originals -> " + originalsDir_);
        
        makeDirStructure();
        setupDirMon();
        setupWorkQueue();
    }

    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------

    private void makeDirStructure(){
        
        File f = new File(rootDir_);
        f.mkdir();
        
        new File(incomingDir_).mkdir();
        new File(workingDir_).mkdir();
        new File(errorDir_).mkdir();
        new File(originalsDir_).mkdir();
        new File(goBackDir_).mkdir();
        new File(logDir_).mkdir();
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
        monitor_ = new DirectoryMonitor(new File(incomingDir_), false);
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
        ITranscoder transcoder = new FFMpegTranscoder(logDir_);
        MovieInfo movieInfo = null;
        
        try {
            logger_.info(shorten(sourceFilename) + " : Querying info ...");
            
            movieInfo = parser.parse(sourceFilename);
            
            logger_.info("\n\n" + movieInfo);
            
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
        FileUtils.copyFileToDirectory(sourceFile, new File(errorDir_));
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
        FileUtils.copyFileToDirectory(sourceFile, new File(originalsDir_));
        sourceFile.delete();
        t3.setEndTime();
        logger_.info(shorten(sourceFilename) + " : Move completed in " + t3);
    }

    
    private void moveDestFileToGoBackDir(String destFilename) 
        throws IOException{
        
        logger_.info(shorten(destFilename) + " : Moving to goBack dir ...");
        ElapsedTime t2 = new ElapsedTime();
        File destFile = new File(destFilename);
        FileUtils.copyFileToDirectory(destFile, new File(goBackDir_));
        destFile.delete();
        t2.setEndTime();
        logger_.info(shorten(destFilename) + " : Move completed in " + t2);
    }


    private String buildDestFilename(MovieInfo movieInfo) {
        String destFilename =  
            workingDir_ 
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