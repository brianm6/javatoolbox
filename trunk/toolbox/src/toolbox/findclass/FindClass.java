package toolbox.findclass;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
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
import toolbox.util.io.CompositeFilter;
import toolbox.util.io.DirectoryFilter;
import toolbox.util.io.ExtensionFilter;

/**
 * Utility that finds all occurences of a given class in the 
 * CLASSPATH, current directory, and archives (recursively)
 */
public class FindClass 
{ 
    /** Logger **/
    private static final Category logger_ = Category.getInstance(Main.class);
    
    private String      classToFind_;
    private String[]    classpath_;
    private boolean     ignoreCase_;
    private String      fileSeparator = System.getProperty("file.separator");

    /** File filters **/
    private FilenameFilter jarFilter_       = new ExtensionFilter(".jar");
    private FilenameFilter zipFilter_       = new ExtensionFilter(".zip");
    private FilenameFilter classFilter_     = new ExtensionFilter(".class");
    private FilenameFilter archiveFilter_   = new CompositeFilter(jarFilter_, zipFilter_);
    private FilenameFilter directoryFilter_ = new DirectoryFilter();

    /** if this system property is set to anything, then debug output will be generated **/    
    private static final String debugProp_ = "findclass.debug";

    /** regular expression matcher **/
    private RE     regExp_;

    /** Collection of listeners **/
    private List findListeners_ = new ArrayList();
    
    /** Default collector **/
    private FindClassCollector defaultCollector_ = new FindClassCollector();

    /**
     * Constructor
     */
    public FindClass() 
    {
        addFindClassListener(defaultCollector_);
    }

    /**
     * Finds a class
     * 
     * @param   classToFind     Regular expression for class to find
     * @param   ignoreCase      Ignores case in search
     */
    public FindClassResult[] findClass(String classToFind, boolean ignoreCase) 
    { 
        /* result collector */
        defaultCollector_.clear();
        
        /* enable debug if findclass.debug found */
        if (System.getProperty(debugProp_) == null)
            Category.getDefaultHierarchy().disableDebug();
        
        
        /* set arguments */
        ignoreCase_  = ignoreCase;
        classToFind_ = classToFind;

        try
        {
            /* setup regexp based on flag */                    
            regExp_ = new RE(classToFind_);
            if (ignoreCase)
                regExp_.setMatchFlags(RE.MATCH_CASEINDEPENDENT);        
        }
        catch (RESyntaxException e)
        {
            logger_.error("constructor", e);
        }

        /* build list of archives and dirs to search */        
        List searchList = new ArrayList();
        searchList.addAll(getClassPathTargets());
        searchList.addAll(getArchiveTargets());

        /* convert search list to an array */
        classpath_ = (String[])searchList.toArray(new String[0]);
        
        if (logger_.isDebugEnabled())
        {
            logger_.debug("Search targets");
            logger_.debug("==============================");
            
            for(int i=0; i < classpath_.length; i++)
                logger_.debug(classpath_[i]);

            logger_.debug("==============================");                
        }

        findClass();
        
        return defaultCollector_.getResults();
    }

    /**
     * Finds given class in archives/paths
     */
    protected void findClass() 
    { 
        try 
        { 
            for (int i=0; i< classpath_.length; i++) 
            { 
                if (isArchive(classpath_[i]))
                    findInArchive(classpath_[i]);
                else
                    findInPath(classpath_[i]);
            }
        }
        catch (Exception e) 
        { 
            logger_.error("findclass", e);
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
        
        /* get classpath */
        String c = System.getProperty("java.class.path");
        
        /* tokenize */  
        StringTokenizer t = 
            new StringTokenizer(c, System.getProperty("path.separator"), false);
                
        /* iterate and add to search list */    
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
     * @param    startDir    Start directory for the search
     * @param    filter      Filename filter criteria
     * @return   List of files that match the filter from the start dir
     */    
    protected List findFilesRecursively(String startingDir, FilenameFilter filter)
    {
        File f = new File(startingDir);
        ArrayList basket = new ArrayList(20);

        if (f.exists() && f.isDirectory()) 
        { 
            /* smack a trailing / on the start dir */
            if (!startingDir.endsWith(fileSeparator))
                startingDir += fileSeparator;
            
            /* process files */
            String[] files = f.list(filter);
            
            for (int i=0; i<files.length; i++) 
            { 
                File current = new File(f, files[i]);
                basket.add(startingDir + files[i]);
            }
            
            /* process directories */
            String[] dirs  = f.list(directoryFilter_);
                        
            for(int i=0; i<dirs.length; i++)
            {
                List subBasket = findFilesRecursively(startingDir + dirs[i], filter);
                basket.addAll(subBasket);
            }
        }
        
        return basket;
    }
    
    /**
     * Finds class in a given jar file
     * 
     * @param   jarName     the name of the jar file to search
     */
    protected void findInArchive(String jarName) throws Exception 
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

            if (!ze.isDirectory() &&  ze.getName().endsWith(".class" )) 
            { 
                String name = ze.getName().replace('/', '.');
                name = name.substring(0, name.length() - ".class".length());

                if (regExp_.match(name))
                    fireClassFound(jarName, name);
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
        /* tack a slash on the end */
        if (!pathName.endsWith( fileSeparator ))
            pathName += fileSeparator;

        
        /* regular expression search */
        List classFiles = findFilesRecursively(pathName, classFilter_);
        
        for(Iterator i = classFiles.iterator(); i.hasNext(); )
        {
            String fileName = (String)i.next();
            String dotted = fileName.replace(File.separatorChar, '.');
            dotted = StringUtil.truncate(dotted, fileName.length() - ".class".length());
            
            logger_.debug("file=" + dotted);
            
            if (regExp_.match(dotted))
                fireClassFound(pathName, dotted);
        }
    }
    
    /**
     * Determines whether a given file is a java archive
     * 
     * @param   s   absolute name of the java archive
     * @return      true if a valid archive, false otherwise
     */
    protected boolean isArchive(String s) 
    { 
        s = s.toUpperCase();
        if (s.endsWith(".JAR") || s.endsWith(".ZIP"))
            return true;
        else
            return false;
    }


    /**
     * Called when a class is found by the various search methods
     *
     * @param  clazz        Class that was found
     * @param  clazzSource  Where the class was found (dir, zip, etc)
     */
    protected void fireClassFound(String whereFound, String whatFound)
    {
        FindClassResult result = 
            new FindClassResult(classToFind_, whereFound, whatFound);

        for(int i=0; i<findListeners_.size(); i++)
        {
            IFindClassListener listener = 
                (IFindClassListener)findListeners_.get(i);
                
            listener.classFound(result);
        }
        
        //System.out.println(clazzSource + " => " + clazz);   
    }
    
 
    /**
     * Adds a listener to the notification list
     */   
    public void addFindClassListener(IFindClassListener listener)
    {
        findListeners_.add(listener);        
    }
    
    /**
     * Removes a listener from the notification list 
     */
    public void removeFindClassListener(IFindClassListener listener)
    {
        findListeners_.remove(listener);
    }
}
        