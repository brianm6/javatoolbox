package toolbox.util.file.snapshot;



public class FileDiff {

    private FileSnapshot beforeSnapshot;
    private FileSnapshot afterSnapshot;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public FileDiff(FileSnapshot before, FileSnapshot after) {
        this.beforeSnapshot = before;
        this.afterSnapshot = after;
    }

    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------

    public boolean isModified() {
        return beforeSnapshot.getLastModified() != afterSnapshot.getLastModified();
    }
    
    // -------------------------------------------------------------------------
    // JavaBean Methods
    // -------------------------------------------------------------------------
    
    public FileSnapshot getAfterSnapshot() {
        return afterSnapshot;
    }

    
    public void setAfterSnapshot(FileSnapshot afterSnapshot) {
        this.afterSnapshot = afterSnapshot;
    }

    
    public FileSnapshot getBeforeSnapshot() {
        return beforeSnapshot;
    }

    
    public void setBeforeSnapshot(FileSnapshot beforeSnapshot) {
        this.beforeSnapshot = beforeSnapshot;
    }
}
