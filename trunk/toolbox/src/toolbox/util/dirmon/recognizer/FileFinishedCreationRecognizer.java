package toolbox.util.dirmon.recognizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import toolbox.util.CollectionUtil;
import toolbox.util.dirmon.DirSnapshot;
import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.dirmon.FileSnapshot;
import toolbox.util.dirmon.IFileActivityRecognizer;
import toolbox.util.dirmon.event.FileEvent;

/**
 * Recognizes when new files are added to a directory.
 */
public class FileFinishedCreationRecognizer implements IFileActivityRecognizer {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private DirectoryMonitor monitor_;

    private Machine machine1_;
    private Machine machine2_;
    private Machine machine3_;
    
    private List machines_;
    
    private String STAGE1 = "stage1";
    private String STAGE2 = "stage2";
    private String STAGE3 = "stage3";
    
    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    public FileFinishedCreationRecognizer(DirectoryMonitor monitor) {
        monitor_ = monitor;
        machine1_ = new Machine(STAGE1);
        machine2_ = new Machine(STAGE2);
        machine3_ = new Machine(STAGE3);
        
        machines_ = new ArrayList();
        machines_.add(machine1_);
        machines_.add(machine2_);
        machines_.add(machine3_);
    }

    class Machine {
        
        private String currentStage_;

        private List stage1Events_;
        private List stage2Events_;
        private List stage3Events_;
        
        
        public Machine(String stage) {
            currentStage_ = stage;
            stage1Events_ = new ArrayList();
            stage2Events_ = new ArrayList();
            stage3Events_ = new ArrayList();
        }
       
        
        public String getStage() {
            return currentStage_;
        }

        
        public void setStage(String stage_) {
            this.currentStage_ = stage_;
        }


        public void setStage1Events(List list) {
            stage1Events_ = list;
        }
    }
    
    // --------------------------------------------------------------------------
    // IFileActivityRecognizer Interface
    // --------------------------------------------------------------------------

    /*
     * @see toolbox.util.file.activity.IFileActivityRecognizer#getRecognizedEvents(toolbox.util.file.snapshot.DirSnapshot, toolbox.util.file.snapshot.DirSnapshot)
     */
    public List getRecognizedEvents(DirSnapshot before, DirSnapshot after) {
        
        for (Iterator i = machines_.iterator(); i.hasNext(); ) {
            Machine m = (Machine) i.next();
            
            if (m.getStage().equals(STAGE1)) {
                m.setStage1Events(recognizeStage1(before, after));
                m.setStage(STAGE2);
            }
            else if (m.getStage().equals(STAGE2)) {
             
                m.setStage(STAGE3);
            }
            else if (m.getStage().equals(STAGE3)) {
                
                m.setStage(STAGE1);
            }
        }
        
        
        // 1 file created
        // 2 file changed by at least timestamp
        // 3 file not changed in timestamp only
        
        // 1 file created
        // 2 file changed in timestamp only
        // 2 file changed in timestamp only
        // 3 file not changed in timestamp only

        // 1 file created
        // 2 file not changed
        
        return null;
    }

    private List recognizeStage1(DirSnapshot before, DirSnapshot after) {

        List createdFileEvents = new ArrayList();
        
        Map beforeFileSnapshots = before.getFileSnapshots();
        Map afterFileSnapshots = after.getFileSnapshots();
        
        Set beforeFileKeys = beforeFileSnapshots.keySet();
        Set afterFileKeys = afterFileSnapshots.keySet();
        
        Collection allFileKeys = 
            CollectionUtil.union(beforeFileKeys, afterFileKeys);
        
        Collection createdFileKeys =
            CollectionUtil.difference(allFileKeys, beforeFileKeys);
        
        for (Iterator i = createdFileKeys.iterator(); i.hasNext();) {
            String fileKey = (String) i.next();

            FileEvent event = 
                new FileEvent(
                    FileEvent.TYPE_FILE_CREATED,
                    monitor_, 
                    (FileSnapshot) null, 
                    (FileSnapshot) after.getFileSnapshots().get(fileKey));

            createdFileEvents.add(event);
        }
        
        return createdFileEvents;
    }
}