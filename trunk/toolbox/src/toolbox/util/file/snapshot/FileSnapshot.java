package toolbox.util.file.snapshot;

import java.io.File;
import java.util.Date;

/**
 * Equality is based on the absolute path regardless of all the other
 * attributs.
 * 
 */
public class FileSnapshot {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private boolean readable_;

    private boolean writable_;

    private boolean exists_;

    private String absolutePath_;

    private String name_;

    private boolean directory_;

    private boolean hidden_;

    private long lastModified_;

    private long length_;
    
    private Date snapshotTimestamp_;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public FileSnapshot(File file) {
        //setReadable(file.canRead());
        //setWritable(file.canRead());
        //setExists(file.exists());
        //setName(file.getName());
        //setDirectory(file.isDirectory());
        //setHidden(file.isHidden());
        setLength(file.length());
        //setSnapshotTimestamp(new Date());
        
        setAbsolutePath(file.getAbsolutePath());
        setLastModified(file.lastModified());
    }

    // -------------------------------------------------------------------------
    // Public 
    // -------------------------------------------------------------------------
    
    public File toFile() {
        return new File(getAbsolutePath());
    }

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


    public boolean isReadable_() {
        return readable_;
    }


    public void setReadable_(boolean canRead) {
        this.readable_ = canRead;
    }


    public boolean isWritable() {
        return writable_;
    }


    public void setWritable(boolean canWrite) {
        this.writable_ = canWrite;
    }


    public boolean isDirectory() {
        return directory_;
    }


    public void setDirectory(boolean directory) {
        this.directory_ = directory;
    }


    public boolean isExists() {
        return exists_;
    }


    public void setExists(boolean exists) {
        this.exists_ = exists;
    }


    public boolean isHidden() {
        return hidden_;
    }


    public void setHidden(boolean hidden) {
        this.hidden_ = hidden;
    }


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


    public String getName() {
        return name_;
    }


    public void setName(String name) {
        this.name_ = name;
    }

    
    public Date getSnapshotTimestamp() {
        return snapshotTimestamp_;
    }

    
    public void setSnapshotTimestamp(Date snapshotTimestamp) {
        this.snapshotTimestamp_ = snapshotTimestamp;
    }
}