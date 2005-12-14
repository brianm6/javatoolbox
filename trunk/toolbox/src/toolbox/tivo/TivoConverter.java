package toolbox.tivo;

import java.io.File;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.dirmon.IDirectoryMonitorListener;
import toolbox.util.dirmon.event.FileEvent;
import toolbox.util.dirmon.event.StatusEvent;
import toolbox.util.dirmon.recognizer.FileCreatedRecognizer;

public class TivoConverter{

    static public final Logger logger_ = 
        Logger.getLogger(TivoConverter.class);
    
    private String rootDir = "z:\\tivo";
    private String incomingDir = rootDir + "\\incoming";
    private String workingDir = rootDir + "\\working";
    private String errorDir = rootDir + "\\error";
    private String completedDir = rootDir + "\\completed"; 
    private String goBackDir = rootDir + "\\goback";

    private DirectoryMonitor monitor_; 
    
    private BlockingQueue workQueue_;
    
    public static void main(String args[]) {
        TivoConverter converter = new TivoConverter();
        converter.start();
    }
    
    
    public TivoConverter() {
    }
    
    
    public void start() {
        makeDirStructure();
        setupDirMon();
        setupWorkQueue();
    }

    private void convert(String filename) throws Exception {
        MovieInfoParser parser = new MovieInfoParser();
        MovieInfo movieInfo = parser.parse(filename);
        ITranscoder transcoder = new FFMpegTranscoder();

        logger_.debug("\n\n" + movieInfo);
        
        String destFilename = 
            workingDir 
            + File.separator 
            + FilenameUtils.removeExtension(
                FilenameUtils.getName(movieInfo.getFilename()))
            + ".mpg";
        
        File sourceFile = new File(movieInfo.getFilename());
        
        try {
            transcoder.transcode(movieInfo, destFilename);
            FileUtils.copyFileToDirectory(sourceFile, new File(completedDir));
            File destFile = new File(destFilename);
            FileUtils.copyFileToDirectory(destFile, new File(goBackDir));
            sourceFile.delete();
            destFile.delete();
        }
        catch (Exception e) {
            
            logger_.error("Transcoding failed!");
            // move original to error directory
            FileUtils.copyFileToDirectory(sourceFile, new File(errorDir));
            sourceFile.delete();
            logger_.info(movieInfo.getFilename() + " moved to " + errorDir);
        }
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
        monitor_.addDirectoryMonitorListener(new IncomingListener());
        monitor_.start();
    }
    
    private void makeDirStructure(){
        
        File f = new File(rootDir);
        f.mkdir();
        
        new File(incomingDir).mkdir();
        new File(workingDir).mkdir();
        new File(errorDir).mkdir();
        new File(completedDir).mkdir();
        new File(goBackDir).mkdir();
    }
    
    class IncomingListener implements IDirectoryMonitorListener {
        
        public void directoryActivity(FileEvent changeEvent) throws Exception{
            
            switch (changeEvent.getEventType()) {
                
                case FileEvent.TYPE_FILE_CREATED:
                    String newFile = changeEvent.getAfterSnapshot().getAbsolutePath(); 
                    logger_.debug("Adding " + newFile +" to work queue...");
                    workQueue_.add(newFile);
                    break;
            }
        }
        
        public void statusChanged(StatusEvent statusEvent) throws Exception{
            logger_.debug(statusEvent);
        }
    }
}
