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

import org.apache.log4j.Category;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import toolbox.util.StringUtil;
import toolbox.util.io.DirectoryFilter;
import toolbox.util.io.ExtensionFilter;
import toolbox.util.io.OrFilter;

/**
 * Utility that finds all occurences of a given class in the 
 * CLASSPATH, current directory, and archives (recursively)
 */
public class FindClass 
{ 
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(Main.class);
    
    /** Class to find **/
    private String classToFind_;
    
    /** Class path to search on **/
    private String[] classpath_;
    
    /** Ignore case in search criteria **/
    private boolean ignoreCase_;

    /** Filter for jar files **/
    private FilenameFilter jarFilter_ = new ExtensionFilter(".jar");
    
    /** Filter for zip files **/
    private FilenameFilter zipFilter_ = new ExtensionFilter(".zip");
    
    /** Filter for class files **/
    private FilenameFilter classFilter_ = new ExtensionFilter(".class");
    
    /** Filter for archives **/
    private FilenameFilter archiveFilter_ = new OrFilter(jarFilter_, zipFilter_);
    
    /** Filter for directories **/
    private FilenameFilter directoryFilter_ = new DirectoryFilter();

    /** 
     * if this system property is set to anything, 
     * then debug output will be generated 
     */    
    private static final String debugProp_ = "findclass.debug";

    /** regular expression matcher **/
    private RE     regExp_;

    /** Collection of listeners **/
    private List findListeners_ = new ArrayList();
    
    /** Default collector **/
    private FindClassCollector defaultCollector_ = new FindClassCollector();

    /** Holds ordered list of search targets **/
    private List searchTargets_ = null;

    /** Flag to cancel the search **/
    private boolean isCancelled_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default Constructor
     */
    public FindClass() 
    {
        // enable debug if findclass.debug found 
        if (System.getProperty(debugProp_) == null)
            Category.getDefaultHierarchy().disableDebug();
        
        addFindClassListener(defaultCollector_);
        //buildSearchTargets();
    }

    //--------------------------------------------------------------------------
    //  Implementation
    //--------------------------------------------------------------------------

    /**
     * Finds a class
     * 
     * @param   classToFind     Regular expression for class to find
     * @param   ignoreCase      Ignores case in search
     * @return  Array of FindClassResults
     * @throws  IOException on IO error
     * @throws  RESyntaxException on regular expression error
     */
    public FindClassResult[] findClass(String classToFind, boolean ignoreCase) 
        throws RESyntaxException, IOException
    { 
        // result collector
        defaultCollector_.clear();
        
        // set arguments
        ignoreCase_  = ignoreCase;
        classToFind_ = classToFind;

        // setup regexp based on flag
        regExp_ = new RE(classToFind_);
        if (ignoreCase)
            regExp_.setMatchFlags(RE.MATCH_CASEINDEPENDENT);        

        // convert search list to an array
        classpath_ = (String[]) getSearchTargets().toArray(new String[0]);

        // for each search target, search it
        for (int i=0; i< classpath_.length; i++) 
        { 
            if (!isCancelled_)
            {
                String target = classpath_[i];
                
                fireSearchingTarget(target);
                
                if (isArchive(target))
                    findInArchive(target);
                else
                    findInPath(target);
            }
            else
            {
                fireSearchCancelled();
                break;                    
            }
        }

        
        return defaultCollector_.getResults();
    }


    /**
     * Builds the list of search targets
     */
    protected void buildSearchTargets()
    {
        // build list of archives and dirs to search
        searchTargets_ = new ArrayList();
        searchTargets_.addAll(getClassPathTargets());
        searchTargets_.addAll(getArchiveTargets());
        
        // print out search targets if debub is on
        if (logger_.isDebugEnabled())
        {
            logger_.debug("Search targets");
            logger_.debug("==============================");
            
            for(Iterator i = searchTargets_.iterator(); 
                i.hasNext(); logger_.debug(i.next()));
                
            logger_.debug("==============================");                
        }
        
    }

    
    /**
     * Retrieves all search targets (archives and directories) on the classpath
     *
     * @return  Array of file/directory strings
     */
    protected List getClassPathTargets()
    {
        List targets = new ArrayList();
        
        // get classpath
        String c = System.getProperty("java.class.path");
        
        // tokenize
        StringTokenizer t = 
            new StringTokenizer(c, System.getProperty("path.separator"), false);
                
        // iterate and add to search list
        while (t.hasMoreTokens())
            targets.add(t.nextToken());
            
        return targets;
    }

    
    /**
     * Retrieves a list of all archive targets to search
     * starting from the current directory and all directories
     * contained with it recursively.
     * 
     * @return Array of strings to archive file locations
     */
    protected List getArchiveTargets()
    {
        return findFilesRecursively(".", archiveFilter_);        
    }


    /**
     * Finds files recursively from a given starting directory using the
     * passed in filter as selection criteria.
     * 
     * @param    startingDir Start directory for the search
     * @param    filter      Filename filter criteria
     * @return   List of files that match the filter from the start dir
     */    
    protected List findFilesRecursively(String startingDir, 
        FilenameFilter filter)
    {
        File f = new File(startingDir);
        ArrayList basket = new ArrayList(20);

        if (f.exists() && f.isDirectory()) 
        { 
            // smack a trailing / on the start dir 
            if (!startingDir.endsWith(File.separator))
                startingDir += File.separator;
            
            // process files
            String[] files = f.list(filter);
            
            for (int i=0; i<files.length; i++) 
                basket.add(startingDir + files[i]);
            
            // process directories
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
     * Finds class in a given jar file
     * 
     * @param   jarName     the name of the jar file to search
     * @throws  IOException on error
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
                        date);
                    
                    fireClassFound(result);
                }
            }
        }
        zf.close();
    }

    
    /**
     * Finds class in a given directory and subdirs
     * 
     * @param   pathName    the absolute name of the directory to search
     */    
    protected void findInPath(String pathName) 
    { 
        // tack a slash on the end
        if (!pathName.endsWith( File.separator ))
            pathName += File.separator;

        
        // regular expression search
        List classFiles = findFilesRecursively(pathName, classFilter_);
        
        for(Iterator i = classFiles.iterator(); i.hasNext(); )
        {
            String fileName = (String)i.next();
            String dotted = fileName.replace(File.separatorChar, '.');
            
            String searchTarget = StringUtil.truncate(
                dotted, fileName.length() - ".class".length());
                
            dotted = searchTarget.substring(pathName.length());
            
            logger_.debug("file=" + dotted);
            
            if (regExp_.match(dotted))
            {
                File classFile = new File(fileName);
                
                FindClassResult result = 
                    new FindClassResult( 
                        classToFind_,
                        pathName,
                        dotted,
                        classFile.length(),
                        new Date(classFile.lastModified()));
                    
                fireClassFound(result);
            }
        }
    }

    
    /**
     * Determines whether a given file is a java archive
     * 
     * @param   s   absolute name of the java archive
     * @return      true if a valid archive, false otherwise
     */
    public static boolean isArchive(String s) 
    { 
        s = s.toUpperCase();
        return (s.endsWith(".JAR") || s.endsWith(".ZIP"));
    }


    /**
     * Called when a class is found by the various search methods
     *
     * @param  result   Details of what, what, when the class was found
     */
    protected void fireClassFound(FindClassResult result)
    {
        for(int i=0; i<findListeners_.size(); i++)
        {
            IFindClassListener listener = 
                (IFindClassListener)findListeners_.get(i);
                
            listener.classFound(result);
        }
    }


    /**
     * Called when a class is found by the various search methods
     *
     * @param  target   The target being searched
     */
    protected void fireSearchingTarget(String target)
    {
        for(int i=0; i<findListeners_.size(); i++)
        {
            IFindClassListener listener = 
                (IFindClassListener)findListeners_.get(i);
                
            listener.searchingTarget(target);
        }
    }

    
    /**
     * Called when the search is cancelled
     */
    protected void fireSearchCancelled()
    {
        for(int i=0; i<findListeners_.size(); i++)
        {
            IFindClassListener listener = 
                (IFindClassListener)findListeners_.get(i);
                
            listener.searchCancelled();
        }
    }

 
    /**
     * Adds a listener to the notification list
     * 
     * @param listener Listener to add
     */   
    public void addFindClassListener(IFindClassListener listener)
    {
        findListeners_.add(listener);        
    }

    
    /**
     * Removes a listener from the notification list 
     * 
     * @param listener Listener to remove
     */
    public void removeFindClassListener(IFindClassListener listener)
    {
        findListeners_.remove(listener);
    }

    
    /**
     * Returns list of targets that will be searched
     * 
     * @return  List of targets as strings
     */
    public List getSearchTargets()
    {
        // build lazily
        if (searchTargets_ == null)
        {
            buildSearchTargets();    
        }
        
        return searchTargets_;
    }

    
    /**
     * Cancels a pending search
     */
    public void  cancelSearch()
    {
        isCancelled_ = true;
    }
    
    
    /**
     * Adds a search target to the front of the search target list.
     * A search target is a valid directory or java archive.
     * 
     * @param  target  Absolute location of directory or jar/zip file
     */
    public void addSearchTarget(String searchTarget)
    {
        searchTargets_.add(0, searchTarget);
    }
    
    
    /**
     * Adds a file or a directory as the search target. If a directory, then
     * it is scanned recursively for archives all of which are added as search
     * targets. If a file, then that single archive file is added as a search
     * target.
     * 
     * @param  target  File or directory
     */
    public void addSearchTarget(File target)
    {
        if (target.isDirectory())
        {
            searchTargets_.addAll(0,
                findFilesRecursively(target.getAbsolutePath(), archiveFilter_));
        }
        else
        {
            searchTargets_.add(0, target.getAbsolutePath());            
        }
    }

    
    /**
     * Removes a search target from the list of search targets
     *
     * @param  target  Search Target to remove
     */
    public void removeSearchTarget(String searchTarget)
    {
        searchTargets_.remove(searchTarget);
    }
}     