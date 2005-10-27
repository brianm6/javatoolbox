package toolbox.util.file.snapshot;

import java.io.File;
import java.util.Date;

/**
 * Equality is based on the absolute path regardless of all the other
 * attributs.
 * 
 */
public class FileSnapshot {

    private boolean readable;

    private boolean writable;

    private boolean exists;

    private String absolutePath;

    //private String canonicalPath;

    private String name;

    private boolean directory;

    private boolean hidden;

    private long lastModified;

    private long length;
    
    private Date snapshotTimestamp;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public FileSnapshot(File file) {
        //setReadable(file.canRead());
        //setWritable(file.canRead());
        //setExists(file.exists());
        setAbsolutePath(file.getAbsolutePath());
        //setCanonicalPath(file.getCanonicalPath());
        //setName(file.getName());
        //setDirectory(file.isDirectory());
        //setHidden(file.isHidden());
        setLastModified(file.lastModified());
        //setLength(file.length());
        //setSnapshotTimestamp(new Date());
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
        return absolutePath;
    }


    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }


//    public String getCanonicalPath() {
//        return canonicalPath;
//    }
//
//
//    public void setCanonicalPath(String canonicalPath) {
//        this.canonicalPath = canonicalPath;
//    }


    public boolean isReadable() {
        return readable;
    }


    public void setReadable(boolean canRead) {
        this.readable = canRead;
    }


    public boolean isWritable() {
        return writable;
    }


    public void setWritable(boolean canWrite) {
        this.writable = canWrite;
    }


    public boolean isDirectory() {
        return directory;
    }


    public void setDirectory(boolean directory) {
        this.directory = directory;
    }


    public boolean isExists() {
        return exists;
    }


    public void setExists(boolean exists) {
        this.exists = exists;
    }


    public boolean isHidden() {
        return hidden;
    }


    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }


    public long getLastModified() {
        return lastModified;
    }


    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }


    public long getLength() {
        return length;
    }


    public void setLength(long length) {
        this.length = length;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    
    public Date getSnapshotTimestamp() {
        return snapshotTimestamp;
    }

    
    public void setSnapshotTimestamp(Date snapshotTimestamp) {
        this.snapshotTimestamp = snapshotTimestamp;
    }
}