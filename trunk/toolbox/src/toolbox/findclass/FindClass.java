package toolbox.findclass;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import toolbox.util.ArrayUtil;
import toolbox.util.ClassUtil;
import toolbox.util.FileUtil;
import toolbox.util.StringUtil;
import toolbox.util.io.filter.DirectoryFilter;
import toolbox.util.io.filter.ExtensionFilter;
import toolbox.util.io.filter.OrFilter;

/**
 * Find class is a utility that can find all occurences of a given class 
 * (can be expressed as a regular expression) in the classpath, one or 
 * more directories (including child directories), and/or any number of
 * arbitrary archives (includes both jars and zips). There is a public
 * API for use as a third library or a command line interface for direct
 * usage.
 */
public class FindClass 
{ 
    private static final Logger logger_ = Logger.getLogger(FindClass.class);
    
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
    private boolean isCancelled_;

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
    private IFindClassListener[] findListeners_;
    
    /** 
     * Default collector of search results. 
     */ 
    private FindClassCollector defaultCollector_;

    /** 
     * Filter for class files.
     */
    private FilenameFilter classFileFilter_;
    
    /** 
     * Filter for archives. 
     */
    private FilenameFilter archiveFilter_; 
    
    /** 
     * Filter for directories. 
     */
    private FilenameFilter directoryFilter_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FindClass.
     */
    public FindClass() 
    {
        classFileFilter_ = new ExtensionFilter(".class");
        directoryFilter_ = new DirectoryFilter();
        
        archiveFilter_ = 
            new OrFilter(
                new ExtensionFilter(".jar"), 
                new ExtensionFilter(".zip"));
        
        findListeners_ = new IFindClassListener[0];
        defaultCollector_ = new FindClassCollector();
        addSearchListener(defaultCollector_);
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    /**
     * Finds a class.
     * 
     * @param classToFind Regular expression for class to find
     * @param ignoreCase Ignores case in search
     * @return Array of FindClassResults
     * @throws IOException on I/O error
     * @throws RESyntaxException on regular expression error
     */
    public FindClassResult[] findClass(String classToFind, boolean ignoreCase) 
        throws RESyntaxException, IOException
    {
        ignoreCase_  = ignoreCase;
        classToFind_ = classToFind;
         
        defaultCollector_.clear();

        // Setup regexp based on case sensetivity flag
        regExp_ = new RE(classToFind_);
        if (ignoreCase_)
            regExp_.setMatchFlags(RE.MATCH_CASEINDEPENDENT);        

        String[] targets = (String[])getSearchTargets().toArray(new String[0]);

        // Search each target
        for (int i=0; i< targets.length; i++) 
        { 
            if (!isCancelled_)
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
                fireSearchCancelled();
                isCancelled_ = false;
                break;                    
            }
        }
        
        fireSearchCompleted();
        
        return defaultCollector_.getResults();
    }

    
    /**
     * Returns a list of target jar, zips, and directories to be searched.
     * 
     * @return List of strings
     */
    public List getSearchTargets()
    {
        // build lazily
        if (searchTargets_ == null)
            buildSearchTargets();    
        
        return searchTargets_;
    }
    
    
    /**
     * Cancels a pending search.
     */
    public void cancelSearch()
    {
        isCancelled_ = true;
    }
    
    
    /**
     * Adds a search target to the front of the search target list.  A search 
     * target is a valid directory or java archive.
     * 
     * @param searchTarget Absolute location of directory or jar/zip file
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
     * @param target File or directory
     */
    public void addSearchTarget(File target)
    {
        if (target.isDirectory())
        {
            searchTargets_.addAll(findFilesRecursively(
                target.getAbsolutePath(), archiveFilter_));
        }
        else
        {
            searchTargets_.add(target.getAbsolutePath());            
        }
    }
    
    
    /**
     * Removes a search target from the list of search targets.
     *
     * @param searchTarget Search Target to remove
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
     * @param dir Directory to find targets in
     * @return List of String filenames
     */
    public List getArchivesInDir(File dir)
    {
        return findFilesRecursively(dir.getAbsolutePath(), archiveFilter_);    
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
        searchTargets_.addAll(getClassPathTargets());
        searchTargets_.addAll(getArchiveTargets());
        
        // print out search targets if debub is on
        //if (logger_.isDebugEnabled())
        //{
        //    logger_.debug("Search targets:");
        //    for (Iterator i = searchTargets_.iterator(); 
        //        i.hasNext(); logger_.debug(i.next()));
        //}
    }
    
    
    /**
     * Returns a list of all search targets (archives and directories) on the 
     * classpath.
     * 
     * @return List of strings
     */
    protected List getClassPathTargets()
    {
        List targets = new ArrayList();
        String cp = ClassUtil.getClasspath();
        StringTokenizer t = new StringTokenizer(cp, File.pathSeparator, false);
        while (t.hasMoreTokens())
            targets.add(t.nextToken());
        return targets;
    }
    
    
    /**
     * Returns a list of all archives that are contained in the current
     * directory and all child directories.
     * 
     * @return List of strings
     */
    protected List getArchiveTargets()
    {
        return findFilesRecursively(".", archiveFilter_);        
    }

    
    /**
     * Finds files recursively from a given starting directory using the
     * passed in filter as selection criteria.
     * 
     * @param startingDir Start directory for the search
     * @param filter Filename filter criteria
     * @return List of files that match the filter from the start dir
     */    
    protected List findFilesRecursively(String startingDir, 
        FilenameFilter filter)
    {
        File f = new File(startingDir);
        ArrayList basket = new ArrayList(20);

        if (f.exists() && f.isDirectory()) 
        { 
            // Smack a trailing / on the start dir
            startingDir = FileUtil.trailWithSeparator(startingDir);
            
            // Process files in the current dir and throw them is the basked
            String[] files = f.list(filter);
            for (int i=0; i<files.length; i++) 
                basket.add(startingDir + files[i]);
            
            // Process immediate child directories
            String[] dirs  = f.list(directoryFilter_);
                        
            for(int i=0; i<dirs.length; i++)
            {
                List subBasket = 
                    findFilesRecursively(startingDir + dirs[i], filter);
                    
                basket.addAll(subBasket);
            }
        }
        
        return basket;
    }
    
    
    /**
     * Finds a class in a given jar file.
     * 
     * @param jarName Name of the jar file to search
     * @throws IOException on I/O error
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
            System.out.println("Error: Could not locate " + jarName + ".");
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
     * @param dirPath Absolute name of the directory to start searching from
     */    
    protected void findInPath(String dirPath) 
    { 
        // Tack a slash on the end
        dirPath = FileUtil.trailWithSeparator(dirPath);
        
        // Regular expression search
        List classFiles = findFilesRecursively(dirPath, classFileFilter_);
        
        for(Iterator i = classFiles.iterator(); i.hasNext(); )
        {
            String fileName = (String)i.next();
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
        for (int i=0; i<findListeners_.length; i++)
            findListeners_[i].classFound(result);
    }

    
    /**
     * Called when a search has been completed.
     */
    protected void fireSearchCompleted()
    {
        for (int i=0; i<findListeners_.length; i++)
            findListeners_[i].searchCompleted(classToFind_);
    }

    
    /**
     * Called when a search target is about to be searched.
     *
     * @param target Target being searched
     */
    protected void fireSearchingTarget(String target)
    {
        for (int i=0; i<findListeners_.length; i++)
            findListeners_[i].searchingTarget(target);
    }
    
    
    /**
     * Called when the search is cancelled.
     */
    protected void fireSearchCancelled()
    {
        for (int i=0; i<findListeners_.length; i++)
            findListeners_[i].searchCancelled();
    }
 
    
    /**
     * Adds a search listener to the list of interested listeners. 
     * 
     * @param listener Listener to add
     */   
    public void addSearchListener(IFindClassListener listener)
    {
        findListeners_ = 
            (IFindClassListener[]) ArrayUtil.add(findListeners_, listener);        
    }
    
    
    /**
     * Removes a search listener from the list of interested listeners. 
     * 
     * @param listener Listener to remove
     */
    public void removeSearchListener(IFindClassListener listener)
    {
        findListeners_ = 
            (IFindClassListener[]) ArrayUtil.remove(findListeners_, listener);        
    }
    
    
    /**
     * Removes all search listeners from the list of interested listeners.
     */
    public void removeSearchListeners()
    {
        findListeners_ = new IFindClassListener[0];
    }
}