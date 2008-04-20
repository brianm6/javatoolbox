package toolbox.findclass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import toolbox.util.ArrayUtil;
import toolbox.util.ClassUtil;
import toolbox.util.FileUtil;
import toolbox.util.StringUtil;
import toolbox.util.service.Cancelable;

/**
 * Find class is a utility that can find all occurrences of a given class (can be
 * expressed as a regular expression) in the classpath, one or more directories 
 * (including child directories), and/or any number of arbitrary archives 
 * (includes both jars and zips). There is a public API for use as a third 
 * library or a command line interface for direct usage.
 * <p>
 * Lingo:
 * <ul>
 *   <li>searchTarget - a directory or jar/zip file that is a potential search
 *       candidate.
 *   <li>archive - collectively refers to either a jar file or a zip file.
 * </ul>
 * 
 * @see toolbox.findclass.FindClassResult
 * @see toolbox.findclass.FindClassListener
 */
public class FindClass implements Cancelable 
{ 
    private static final Logger logger_ = Logger.getLogger(FindClass.class);

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /** 
     * Filter that identifies java class files.
     */
    private static final IOFileFilter FILEFILTER_CLASSFILE = new SuffixFileFilter(".class"); 
    
    /** 
     * File filter that identifies the the types of archive files that can 
     * contain java class files.
     */
    private static final IOFileFilter FILEFILTER_ARCHIVE = 
        new SuffixFileFilter(new String[]{".jar", ".zip", ".ear", ".war", ".rar"});
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Full/partial/regular expression of class to find. 
     */
    private String classToFind_;
    
    /** 
     * Ignore case in search criteria? 
     */
    private boolean ignoreCase_;

    /** 
     * Flag to cancel the search. 
     */
    private boolean canceled_;

    /** 
     * Regular expression matcher. 
     */
    private RE regExp_;
    
    /** 
     * Holds ordered list of search targets (important is CLASSPATH). 
     */
    private List searchTargets_;

    /** 
     * Collection of listeners. 
     */ 
    private FindClassListener[] findListeners_;
    
    /** 
     * Default collector of search results. 
     */ 
    private FindClassCollector defaultCollector_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FindClass.
     */
    public FindClass() 
    {
        findListeners_ = new FindClassListener[0];
        defaultCollector_ = new FindClassCollector();
        searchTargets_ = new ArrayList();
        addSearchListener(defaultCollector_);
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    /**
     * Finds a class.
     * 
     * @param classToFind Regular expression for class to find.
     * @param ignoreCase Ignores case in search.
     * @return Array of FindClassResults.
     * @throws IOException on I/O error.
     * @throws RESyntaxException on regular expression error.
     */
    public FindClassResult[] findClass(
        String classToFind, 
        boolean ignoreCase) 
        throws RESyntaxException, IOException
    {
        ignoreCase_  = ignoreCase;
        classToFind_ = classToFind;
        canceled_ = false;
        
        defaultCollector_.clear();

        // Setup regexp based on case sensitivity flag
        regExp_ = new RE(classToFind_);
        
        if (ignoreCase_)
            regExp_.setMatchFlags(RE.MATCH_CASEINDEPENDENT);        

        String[] targets = (String[]) getSearchTargets().toArray(new String[0]);

        // Search each target
        for (int i = 0; i < targets.length; i++) 
        { 
            if (!canceled_)
            {
                String target = targets[i];
                
                fireSearchingTarget(target);
                
                if (ClassUtil.isArchive(target))
                    findInArchive(target);
                else
                    findInPath(target);
            }
            else
            {
                fireSearchCanceled();
                break;                    
            }
        }
        
        if (!canceled_)
            fireSearchCompleted();
        
        return defaultCollector_.getResults();
    }

    
    /**
     * Returns a list of target jar, zips, and directories to be searched.
     * 
     * @return List of strings containing names of directories or jar files.
     */
    public List getSearchTargets()
    {
        // build lazily
        if (searchTargets_.isEmpty())
            buildSearchTargets();    
        
        return searchTargets_;
    }

    
    /**
     * Adds a search target to the front of the search target list.  A search 
     * target is a valid directory or java archive.
     * 
     * @param searchTarget Absolute location of directory or jar/zip file.
     */
    public void addSearchTarget(String searchTarget)
    {
        searchTargets_.add(searchTarget);
    }
    
    
    /**
     * Adds a file or a directory as the search target. If a directory, then
     * it is scanned recursively for archives all of which are added as search
     * targets. If a file, then that single archive file is added as a search
     * target.
     * 
     * @param target File or directory.
     */
    public void addSearchTarget(File target)
    {
        if (target.isDirectory())
        {
            searchTargets_.addAll(FileUtil.find(
                target.getAbsolutePath(), FILEFILTER_ARCHIVE));
        }
        else
        {
            searchTargets_.add(target.getAbsolutePath());            
        }
    }
    
    
    /**
     * Removes a search target from the list of search targets.
     *
     * @param searchTarget Search target to remove.
     */
    public void removeSearchTarget(String searchTarget)
    {
        searchTargets_.remove(searchTarget);
    }
    
    
    /**
     * Removes all search targets.
     */
    public void removeSearchTargets()
    {
        searchTargets_.clear();
    }

    
    /**
     * Returns a list of archives that exist in a given directory. The 
     * directory is searched recursively.
     * 
     * @param dir Directory to find targets in.
     * @return List of String filenames.
     */
    public List getArchivesInDir(File dir)
    {
        return FileUtil.find(dir.getAbsolutePath(), FILEFILTER_ARCHIVE);    
    }

    //--------------------------------------------------------------------------
    // Cancelable Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.service.Cancelable#cancel()
     */
    public synchronized void cancel()
    {
        canceled_ = true;
        
        // since search is async, wait to receive the cancel event.
        try
        {
            defaultCollector_.waitForCancel();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    
    /**
     * Cancels a pending search. The search will stop after processing the
     * current search target.
     *  
     * @see toolbox.util.service.Cancelable#isCanceled()
     */
    public boolean isCanceled()
    {
        return canceled_;
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Builds the list of search targets.
     */
    protected void buildSearchTargets()
    {
        // build list of archives and dirs to search
        searchTargets_ = new ArrayList();
        searchTargets_.addAll(Arrays.asList(ClassUtil.getClassPathElements()));
        searchTargets_.addAll(getArchiveTargets());
        
        // print out search targets if debug is on
        //if (logger_.isDebugEnabled())
        //{
        //    logger_.debug("Search targets:");
        //    for (Iterator i = searchTargets_.iterator(); 
        //        i.hasNext(); logger_.debug(i.next()));
        //}
    }
    
    
    /**
     * Returns a list of all archives that are contained in the current
     * directory and all child directories.
     * 
     * @return List of strings.
     */
    protected List getArchiveTargets()
    {
        return FileUtil.find(".", FILEFILTER_ARCHIVE);        
    }

    
    /**
     * Finds a class in a given jar file.
     * 
     * @param jarName Name of the jar file to search.
     * @throws IOException on I/O error.
     */
    protected void findInArchive(String jarName) throws IOException
    { 
        ZipFile zf = null;

        try 
        { 
            zf = new ZipFile(jarName);
        }
        catch (Exception e) 
        { 
            // Exceptions to the Rule for these two troublesome jar files:
            // Error: Could not locate C:\Program Files\Java\j2re1.4.2_06\lib\i18n.jar.
            // Error: Could not locate C:\Program Files\Java\j2re1.4.2_06\lib\charsets.jar            
            
            if (StringUtils.containsIgnoreCase(jarName, "i18n.jar") ||
                StringUtils.containsIgnoreCase(jarName, "charsets.jar"))
            {
				// Skip
            }
            else
            {
                System.out.println("Error: Could not locate " + jarName + ".");
            }
            return;
        }
        
        for (Enumeration e = zf.entries(); e.hasMoreElements();) 
        { 
            ZipEntry ze = (ZipEntry) e.nextElement();

            if (!ze.isDirectory() &&  ze.getName().endsWith(".class")) 
            { 
                String name = ze.getName().replace('/', '.');
                name = name.substring(0, name.length() - ".class".length());
                
                if (regExp_.match(name))
                {
                    long size = ze.getSize();
                    Date date = new Date(ze.getTime());
                    
                    FindClassResult result = new FindClassResult(
                        classToFind_,
                        jarName,
                        name,
                        size,
                        date,
                        regExp_.getParenStart(0),
                        regExp_.getParenEnd(0));
                    
                    fireClassFound(result);
                }
            }
        }
        
        zf.close();
    }
    
    
    /**
     * Finds a class in a given directory including child directories.
     * 
     * @param dirPath Absolute name of the directory to start searching from.
     */    
    protected void findInPath(String dirPath) 
    { 
        // Tack a slash on the end
        dirPath = FileUtil.trailWithSeparator(dirPath);
        
        // Regular expression search
        List classFiles = FileUtil.find(dirPath, FILEFILTER_CLASSFILE);
        
        for (Iterator i = classFiles.iterator(); i.hasNext();)
        {
            String fileName = (String) i.next();
            String dotted = fileName.replace(File.separatorChar, '.');
            
            String searchTarget = StringUtil.truncate(
                dotted, fileName.length() - ".class".length());
                
            dotted = searchTarget.substring(dirPath.length());
            
            //logger_.debug("file = " + dotted);
            
            if (regExp_.match(dotted))
            {
                File classFile = new File(fileName);
                
                FindClassResult result = 
                    new FindClassResult(
                        classToFind_,
                        dirPath,
                        dotted,
                        classFile.length(),
                        new Date(classFile.lastModified()),
                        regExp_.getParenStart(0),
                        regExp_.getParenEnd(0));
                    
                fireClassFound(result);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // Event Listener Support
    //--------------------------------------------------------------------------

    /**
     * Called when a class is found matching the search criteria.
     *
     * @param result Detailed information on the class including location,
     *        size, name, etc.
     */
    protected void fireClassFound(FindClassResult result)
    {
        for (int i = 0; i < findListeners_.length; i++)
            findListeners_[i].classFound(result);
    }

    
    /**
     * Called when a search has been completed.
     */
    protected void fireSearchCompleted()
    {
        for (int i = 0; i < findListeners_.length; i++)
            findListeners_[i].searchCompleted(classToFind_);
    }

    
    /**
     * Called when a search target is about to be searched.
     *
     * @param target Target being searched.
     */
    protected void fireSearchingTarget(String target)
    {
        for (int i = 0; i < findListeners_.length; i++)
            findListeners_[i].searchingTarget(target);
    }
    
    
    /**
     * Called when the search is canceled.
     */
    protected void fireSearchCanceled()
    {
        for (int i = 0; i < findListeners_.length; i++)
            findListeners_[i].searchCanceled();
    }
 
    
    /**
     * Adds a search listener to the list of interested listeners. 
     * 
     * @param listener Listener to add.
     */   
    public void addSearchListener(FindClassListener listener)
    {
        findListeners_ = (FindClassListener[]) ArrayUtil.add(findListeners_, listener);
    }
    
    
    /**
     * Removes a search listener from the list of interested listeners. 
     * 
     * @param listener Listener to remove.
     */
    public void removeSearchListener(FindClassListener listener)
    {
        findListeners_ = (FindClassListener[]) ArrayUtil.remove(findListeners_, listener);
    }
    
    
    /**
     * Removes all search listeners from the list of interested listeners.
     */
    public void removeSearchListeners()
    {
        findListeners_ = new FindClassListener[0];
    }
}