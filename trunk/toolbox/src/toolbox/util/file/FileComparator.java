package toolbox.util.file;

import java.io.File;
import java.util.Comparator;
import java.util.Date;

/**
 * A {@java.io.File} comparator that can compare on varios file attributes.
 * <p>
 * <ul>
 *   <li>File name (case insensetive)
 *   <li>File size
 *   <li>File timestamp
 * </ul>
 */
public class FileComparator implements Comparator
{
    public static final int COMPARE_NAME = 1; // File name
    public static final int COMPARE_SIZE = 2; // File size
    public static final int COMPARE_DATE = 3; // File timestamp
    
	/**
	 * Field to compare by. See COMPARE_* constants.
	 */    
    private int compareBy_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a FileComparator.
     * 
     * @param compareBy File attribute to base comparision on. 
     *        Use FileComparator.COMPARE_[NAME|SIZE|DATE]
     */
    public FileComparator(int compareBy)
    {
        // TODO: bounds checking
        compareBy_ = compareBy;
    }
    
    //--------------------------------------------------------------------------
    // Comparator Interface
    //--------------------------------------------------------------------------

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object a, Object b)
    {
        int result;
        
        File fileA = (File) a;
        File fileB = (File) b;
        
        switch (compareBy_)
        {
            case COMPARE_NAME :
             
                result = fileA.getName().compareToIgnoreCase(fileB.getName());
                break;
            
                
            case COMPARE_SIZE :
            
                long sizeA = fileA.length();
                long sizeB = fileB.length();
                
                if (sizeA == sizeB)
                    result = 0;
                else if (sizeA > sizeB)
                    result = 1;
                else
                    result = -1;
                break;

                
            case COMPARE_DATE :
                
                Date dateA = new Date(fileA.lastModified());
                Date dateB = new Date(fileB.lastModified());
                result = dateA.compareTo(dateB);
                break;

                
            default:
            
                throw new IllegalArgumentException(
                    "FileComparator does not support comparisons by " + 
                    compareBy_); 
        }
        
        return result;
    }
}