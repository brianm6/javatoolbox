package toolbox.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import toolbox.util.io.filter.DirectoryFilter;

/**
 * File Utility Class
 */
public final class FileUtil
{
    private static final Logger logger_ = 
        Logger.getLogger(FileUtil.class);

    // Clover private constructor workaround
    static { new FileUtil(); }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Prevent construction
     */
    private FileUtil()
    {
    }

    //--------------------------------------------------------------------------
    //  Public Static
    //--------------------------------------------------------------------------

    /**
     * Deletes the contents of a directory including nested directories. The
     * directory itself is not deleted.
     *
     * @param  dir  Directory to clean
     */
    public static void cleanDir(File dir)
    {
        if (!dir.isDirectory())
        {
            throw new IllegalArgumentException(
                "Directory " + dir + " is not a directory.");
        }
        else
        {
            File[] contents = dir.listFiles();

            for (int i = 0; i < contents.length; i++)
            {
                File sub = contents[i];

                if (sub.isDirectory())
                    cleanDir(sub);

                sub.delete();
            }
        }
    }

    /**
     * Reads in the contents of a text file into a single string
     *
     * @param   filename    Name of the file
     * @return  Contents of the file as a string
     * @throws  FileNotFoundException if file not found
     * @throws  IOException on IO error
     */
    public static String getFileContents(String filename)
        throws FileNotFoundException, IOException
    {
        Reader br = null;
        StringBuffer text = new StringBuffer();

        try
        {
            br = new BufferedReader(new FileReader(filename));
            int i;

            while ((i = br.read()) != -1)
                text.append((char) i);
        }
        finally
        {
            ResourceCloser.close(br);
        }

        return text.toString();
    }

    /**
     * Reads in the contents of a file into byte array
     *
     * @param   filename  Name of the file to read in
     * @return  Files contents as a byte array
     * @throws  FileNotFoundException if file not found
     * @throws  IOException on IO error
     */
    public static byte[] getFileAsBytes(String filename)
        throws FileNotFoundException, IOException
    {
        InputStream is = null;
        List byteBuffer = new ArrayList();

        try
        {
            is = new BufferedInputStream(new FileInputStream(filename));
            int b;

            while ((b = is.read()) != -1)
                byteBuffer.add(new Byte((byte)b));
        }
        finally
        {
            StreamUtil.close(is);
        }

        byte[] buffer = new byte[byteBuffer.size()];
        
        for (int i=0; i<byteBuffer.size(); i++)
            buffer[i] = ((Byte) byteBuffer.get(i)).byteValue();
            
        return buffer;
    }

    /**     
     * Writes out the contents to a text file from a single string.     
     *     
     * @param   filename  Name of the file     
     * @param   contents  Contents to store in the file
     * @param   append    Specify if you want to append to the file     
     * @return  Contents of the file as a string
     * @throws  FileNotFoundException if file not found
     * @throws  IOException on I/O error
     */
    public static String setFileContents(
        String filename,
        String contents,
        boolean append)
        throws FileNotFoundException, IOException
    {
        FileWriter file = new FileWriter(filename, append);

        if (file == null)
        {
            logger_.error("File does not exist: " + filename);
            throw new FileNotFoundException(filename);
        }

        file.write(contents);
        file.close();

        return contents;
    }

    /**     
     * Writes out the contents of a byte array to a file
     *     
     * @param   filename    Name of the file     
     * @param   data        Byte array of data
     * @param   append      True if append if the file already exists
     * 
     * @throws  FileNotFoundException if file not found
     * @throws  IOException on IO error
     */
    public static void setFileContents(
        String filename,
        byte[] data,
        boolean append)
        throws FileNotFoundException, IOException
    {
        FileOutputStream fos = new FileOutputStream(filename, append);

        if (fos == null)
        {
            logger_.error("File does not exist: " + filename);
            throw new FileNotFoundException(filename);
        }

        fos.write(data);
        fos.close();
    }

    /**     
     * Writes a string to a file
     *     
     * @param   file      File to write to
     * @param   contents  Contents to store in the file
     * @param   append    Specify if you want to append to the file     
     * @return  Contents of the file as a string
     * @throws  FileNotFoundException if file not found
     * @throws  IOException on IO error
     */
    public static String setFileContents(
        File file,
        String contents,
        boolean append)
        throws FileNotFoundException, IOException
    {
        return setFileContents(file.getAbsolutePath(), contents, append);
    }

    /**
     * Retrieves the System specific temp file directory
     *
     * @return  Temp file directory
     */
    public static File getTempDir()
    {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Creates a temporary directory in the System temporary directory
     * 
     * @return  Created temporary directory
     * @throws  IOException on IO error
     */
    public static File createTempDir() throws IOException
    {
        File f = new File(getTempFilename(getTempDir()));
        f.mkdir();
        return f;
    }

    /**
     * Retrieves a suitable temporary file name for arbitrary use based on the 
     * system's temporary directory. The returned string is absolute in form.
     *
     * @return  Temporary file name
     * @throws  IOException on IO error
     */
    public static String getTempFilename() throws IOException
    {
        return getTempFilename(getTempDir());
    }

    /**
     * Creates a temporary filename for a file in the given directory
     * 
     * @param   dir    Directory to assume the file will be created in
     * @return  Tempory filename in absolute form
     * @throws  IOException on IO error
     */
    public static String getTempFilename(File dir) throws IOException
    {
        // Create temp file, delete it, and return the name 
        File tmpFile = File.createTempFile("temp", "", dir);
        String filename = tmpFile.getAbsolutePath();
        tmpFile.delete();
        return filename;
    }

    /**
     * Moves a file to a given directory. The destination directory must exist 
     * and be writable.
     *
     * @param  srcFile  File to move
     * @param  destDir  Destination directory
     */
    public static void moveFile(File srcFile, File destDir)
    {
        /*
         * NOTE: This is a SIMPLE implementation. There are a lot more 
         *       complicated scenarios involving permissions, attributes, 
         *       existence that need to be accounted for. 
         */
        logger_.debug("Moving " + srcFile + " => " + destDir);

        InputStream is = null;
        OutputStream os = null;

        try
        {
            File destFile = new File(destDir, srcFile.getName());

            is = new BufferedInputStream(new FileInputStream(srcFile));
            os = new BufferedOutputStream(new FileOutputStream(destFile));

            // copy contents
            while (is.available() > 0)
                os.write(is.read());

            // close streams
            is.close();
            os.close();

            // delete original
            srcFile.delete();
        }
        catch (IOException e)
        {
            logger_.error("moveFile", e);
        }
        finally
        {
            ; // cleanup
        }
    }

    /**
     * Finds files recursively from a given starting directory using the
     * passed in filter as selection criteria.
     * 
     * @param   startingDir  Start directory for the search
     * @param   filter       Filename filter criteria
     * 
     * @return  List of filesnames as strings that match the filter from the 
     *          start dir
     */
    public static List findFilesRecursively(
        String startingDir,
        FilenameFilter filter)
    {
        File f = new File(startingDir);
        List basket = new ArrayList(20);

        if (f.exists() && f.isDirectory())
        {
            // smack a trailing / on the start dir 
            if (!startingDir.endsWith(File.separator))
                startingDir += File.separator;

            // process files
            String[] files = f.list(filter);

            for (int i = 0; i < files.length; i++)
                basket.add(startingDir + files[i]);

            // process directories
            String[] dirs = f.list(new DirectoryFilter());

            for (int i = 0; i < dirs.length; i++)
            {
                List subBasket =
                    findFilesRecursively(startingDir + dirs[i], filter);

                basket.addAll(subBasket);
            }
        }

        return basket;
    }

    /**
     * Appends the file separator char to the end of a path if it already
     * doesn't exist.
     * 
     * @param   path    Path to append file separator
     * @return  Path with suffixed file separator
     */
    public static String trailWithSeparator(String path)
    {
        if (!path.endsWith(File.separator))
            path = path + File.separator;

        return path;
    }

    /**
     * For a given file path, the file separator characters are changed to
     * match the File.separator for the current platform
     * 
     * @param   path  Path to change
     * @return  Changed path
     */
    public static String matchPlatformSeparator(String path)
    {
        String newPath = path.replace('\\', File.separatorChar);
        newPath = newPath.replace('/', File.separatorChar);
        return newPath;
    }

    /**
     * Chops the extension off of a file's name
     * 
     * @param  file  File name
     * @return File name without the extension
     */
    public static String dropExtension(String file)
    {
        int dot = file.lastIndexOf(".");

        if (dot == -1)
            return file;
        else
            return file.substring(0, dot);
    }
    
    /**
     * Strips the path portion away from the relative or absolulte file path
     * leaving only the filename.
     * <pre>
     * 
     * Examples:
     * 
     * c:\data\work\tmp\orders.txt  => orders.txt
     * /usr/home/user/orders.xml    => orders.xml
     * 
     * </pre>
     * 
     * @param   file  Relative or absolute path reference to a file
     * @return  Just the name of the file
     */
    public static String stripPath(String file)
    {
        int i = file.lastIndexOf(File.separatorChar);
        return (i >= 0 ? file.substring(i+1) : file); 
    }
    
    /**
     * Strips the file portion away from an absolute or relative file path
     * leaving only the path. The resulting path does not have a trailing
     * file separator.
     * <pre>
     * 
     * Examples:
     * 
     * c:\data\work\tmp\orders.txt  => c:\data\work\tmp
     * /usr/home/user/orders.xml    => /usr/home/user
     * 
     * </pre>
     *
     * @param   filepath  Path including filename
     * @return  Just the path portion of the filepath
     */
    public static String stripFile(String filepath)
    {
        int i = filepath.lastIndexOf(File.separatorChar);
        return (i >= 0 ? filepath.substring(0, i) : "");
    }    

    /**
     * Deletes a file quietly. If the file can be deleted, ok. If not, 
     * does not cause a fuss.
     * 
     * @param  file  File to delete
     */    
    public static void delete(String file)
    {
        if (file != null && !StringUtil.isNullEmptyOrBlank(file))
            new File(file).delete();
    }
}