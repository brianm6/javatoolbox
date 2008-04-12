package toolbox.util.dirmon;

import java.io.File;
import java.util.Date;

/**
 * FileSnapshot represents the relevant attributes of a file at a given point
 * in time. A reference to the originating {@link java.io.File} is not retained
 * but can be regenerated via {@link #toFile()}. 
 */
public class FileSnapshot {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    // Left out of snapshot for performance reasons
//    private boolean readable_;
//
//    private boolean writable_;
//
//    private boolean exists_;
//
//    private String name_;
//    
//    private boolean hidden_;

    // Included in snapshot
    private String absolutePath_;

    private boolean directory_;

    private long lastModified_;

    private long length_;
    
    private Date snapshotTimestamp_;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    /**
     * Creates a FileSnapshot for the given file.
     * 
     * @param file File to create a snapshot for.
     */
    public FileSnapshot(File file) {
        
//        setReadable(file.canRead());
//        setWritable(file.canWrite());
//        setExists(file.exists());
//        setName(file.getName());
//        setHidden(file.isHidden());
        
        setDirectory(file.isDirectory());
        setLength(file.length());
        setSnapshotTimestamp(new Date());
        setAbsolutePath(file.getAbsolutePath());  // canonical path is very expensive
        setLastModified(file.lastModified()); 
    }

    // -------------------------------------------------------------------------
    // Public 
    // -------------------------------------------------------------------------
    
    /**
     * Returns a reference to the {@link java.io.File} referenced by this
     * snapshot.
     * 
     * @return File
     */
    public File toFile() {
        return new File(getAbsolutePath());
    }

    
    /**
     * Returns the absolute path of the file.
     * 
     * @return String
     */
    public String getKey() {
        return getAbsolutePath();
    }
    
    // -------------------------------------------------------------------------
    // Overrides java.lang.Object
    // -------------------------------------------------------------------------
    
    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        
        if (obj.getClass() != getClass())
            return false;
        
        FileSnapshot rhs = (FileSnapshot) obj;
        
        return getAbsolutePath().equals(rhs.getAbsolutePath());
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getAbsolutePath().hashCode();
    }
    
    /*
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getKey();
    }
    
    // -------------------------------------------------------------------------
    // JavaBean Methods
    // -------------------------------------------------------------------------
    
    public String getAbsolutePath() {
        return absolutePath_;
    }


    public void setAbsolutePath(String absolutePath) {
        this.absolutePath_ = absolutePath;
    }


//    public boolean isReadable() {
//        return readable_;
//    }
//
//
//    public void setReadable(boolean canRead) {
//        this.readable_ = canRead;
//    }
//
//
//    public boolean isWritable() {
//        return writable_;
//    }
//
//
//    public void setWritable(boolean canWrite) {
//        this.writable_ = canWrite;
//    }


    public boolean isDirectory() {
        return directory_;
    }


    public void setDirectory(boolean directory) {
        this.directory_ = directory;
    }


//    public boolean isExists() {
//        return exists_;
//    }
//
//
//    public void setExists(boolean exists) {
//        this.exists_ = exists;
//    }
//
//
//    public boolean isHidden() {
//        return hidden_;
//    }
//
//
//    public void setHidden(boolean hidden) {
//        this.hidden_ = hidden;
//    }


    public long getLastModified() {
        return lastModified_;
    }


    public void setLastModified(long lastModified) {
        this.lastModified_ = lastModified;
    }


    public long getLength() {
        return length_;
    }


    public void setLength(long length) {
        this.length_ = length;
    }


//    public String getName() {
//        return name_;
//    }
//
//
//    public void setName(String name) {
//        this.name_ = name;
//    }

    
    public Date getSnapshotTimestamp() {
        return snapshotTimestamp_;
    }

    
    public void setSnapshotTimestamp(Date snapshotTimestamp) {
        this.snapshotTimestamp_ = snapshotTimestamp;
    }
}