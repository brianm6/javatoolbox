package toolbox.util.file;

import java.util.EventObject;

import toolbox.util.file.snapshot.FileSnapshot;

public class DirectoryMonitorEvent extends EventObject {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------
    
    public static final int CREATED = 0;
    public static final int CHANGED = 1;
    public static final int DELETED = 2;
    
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private int eventType_;
    private FileSnapshot beforeSnapshot_;
    private FileSnapshot afterSnapshot_;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public DirectoryMonitorEvent(
        int eventType,
        DirectoryMonitor source, 
        FileSnapshot before, 
        FileSnapshot after) {
        
        super(source);
        eventType_ = eventType;
        beforeSnapshot_ = before;
        afterSnapshot_ = after;
    }

    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------
    
    public FileSnapshot getBeforeSnapshot() {
        return beforeSnapshot_;
    }

    public FileSnapshot getAfterSnapshot() {
        return afterSnapshot_;
    }

    public int getEventType() {
        return eventType_;
    }
}