package toolbox.tivo;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import toolbox.util.ElapsedTime;
import toolbox.util.StringUtil;
import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.dirmon.IDirectoryMonitorListener;
import toolbox.util.dirmon.event.FileEvent;
import toolbox.util.dirmon.event.StatusEvent;
import toolbox.util.dirmon.recognizer.FileCreatedRecognizer;
import toolbox.util.dirmon.recognizer.FileCreationFinishedRecognizer;

/**
 * Simple utlity application to converts movies from any format to a format that
 * can be displayed on a Tivo.
 */
public class TivoConverter {

    public static final Logger logger_ = Logger.getLogger(TivoConverter.class);
    
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    /**
     * Root directory that contains all files produced and consumed by this
     * application.
     */
    private String rootDir_ = "c:\\tivo";
    
    /**
     * Movies to be converted to a Tivo compatible format are  placed in the 
     * incoming directory.
     */
    private String incomingDir_;
    
    /**
     * During the transcoding process, the newly trancoded movie is written to
     * this directory temporarily.
     */
    private String workingDir_;
    
    /**
     * Movies that fail transcoding are placed in the error directory.
     */
    private String errorDir_;
    
    /**
     * The originals are always preserved and moved from the incoming directory
     * to the originals directory after being processed regardless of success
     * or failure.
     */
    private String originalsDir_;
    
    /**
     * One a movie has been transcoded successfully, it is moved from the 
     * working directory to the goBack directory. Your Tivo should be set
     * to search this directory for movies that you want to watch on TV.
     */
    private String goBackDir_;
    
    /**
     * All log files are dumped to the log directory. This includes stdout and
     * stderr output from the FFMpeg executable.
     */
    private String logDir_;

    /**
     * Monitors the incoming directory for new movies to transcode.
     */
    private DirectoryMonitor monitor_; 
    
    /**
     * Transcoding occurs one movie at a time so additional movies are queued
     * up in this work queue.
     */
    private BlockingQueue workQueue_;
    
    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------
    
    /**
     * The only supported command line argument is the name of the root 
     * directory {@link #rootDir_}. If one is not passed, it defaults to 
     * z:\tivo.
     */
    public static void main(String args[]) {

        configLogger();

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
        incomingDir_ = rootDir_ + File.separator + "incoming";
        workingDir_ = rootDir_ + File.separator + "working";
        errorDir_ = rootDir_ + File.separator + "error";
        originalsDir_ = rootDir_ + File.separator + "originals"; 
        goBackDir_ = rootDir_ + File.separator + "goback";
        logDir_ = rootDir_ + File.separator + "logs";
    }

    public static void configLogger() {
        Logger appLogger = Logger.getRootLogger();
        appLogger.setLevel(Level.INFO);
        appLogger.setAdditivity(true);
        
//        for (Enumeration e = appLogger.getAllAppenders(); e.hasMoreElements(); ) {
//            Appender a = (Appender) e.nextElement();
//            System.out.println(a);
//        }
        
        Appender appAppender = appLogger.getAppender("console");
        appAppender.setLayout(new PatternLayout("%m%n"));
    }
    
    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------

    /**
     * Lets get things rolling...
     */
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

    /**
     * Creates the directory structure under the root directory if it does not
     * already exist.
     */
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
    
    
    /**
     * Sets up a blocking queue that contains the names of movie files to
     * transcode to a Tivo compatible format.
     */
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
    
    
    /**
     * Sets up a directory monitor for the incoming directory which adds files
     * to the work queue as file creation events are received (when a user 
     * copies or renames a file in the directory).
     */
    private void setupDirMon() {
        monitor_ = new DirectoryMonitor(new File(incomingDir_), false);
        
        // Scan for new files in 'incoming' directory every 10 seconds
        monitor_.setDelay(10000);
        
        //monitor_.setName("incoming"); screws things up
        
        monitor_.addRecognizer(new FileCreatedRecognizer(monitor_));
        
        // Check file is created and stabilized 10 seconds after initial event
        // of creation.
        monitor_.addRecognizer(new FileCreationFinishedRecognizer(monitor_, 10));
        
        monitor_.addDirectoryMonitorListener(new IncomingDirListener());
        monitor_.start();
    }
    

    /**
     * Once a movie file name is pulled from the work queue, its time to convert
     * it to Tivo format.
     * 
     * @param sourceFilename Absolute path and name of the file to transcode.
     * @throws Exception on error.
     */
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
            
            // Check that the result file exists and is is not zero bytes 
            // otherwise fail
            
            File f = new File(destFilename);
            if (!f.exists())
                throw new Exception(
                    "Transcoded file " + destFilename + " does not exist");

            if (f.length() == 0) {
                f.delete();
                throw new Exception(
                    "Transcoded file " + destFilename + " is ZERO bytes");
            }
            
            moveDestFileToGoBackDir(destFilename); 
            moveSourceFileToOriginalsDir(sourceFilename, sourceFile); 
        }
        catch (Exception e) {
            handleFailure(sourceFilename, sourceFile, e);
        }
        
    }

    
    /**
     * Common error handler. Copies the failed transcoded source file to the 
     * error directory and logs errors appropriately.
     * 
     * @param sourceFilename
     * @param sourceFile
     * @param e
     * @throws IOException
     */
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

    
    public void printQueue() {
    
        StringBuffer sb = new StringBuffer();
        
        int cnt = 1;
        for (Iterator i = workQueue_.iterator(); i.hasNext(); ) {
            sb.append(cnt + " " + i.next() + "\n");
            ++cnt;
        }
        
        if (sb.length() > 0)
            logger_.info(StringUtil.banner(sb.toString()));
        else
        	logger_.info("Work queue is empty!");
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
                    printQueue();
                    break;
            }
        }
        
        public void statusChanged(StatusEvent statusEvent) throws Exception{
        }
    }
}