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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Category;

/**
 * File Utility Class
 */
public final class FileUtil
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(FileUtil.class);
    
    /**
     * Prevent construction
     */
    private FileUtil()
    {
    }

    /**
     * Deletes the contents of a directory including nested directories
     *
     * @param    directory    Directory to clean
     */
    public static void cleanDir(File directory)
    {
        if(!directory.isDirectory())
        {
            throw new IllegalArgumentException(
                "Directory " + directory + " is not a directory.");
        }
        else
        {
            File[] contents = directory.listFiles();

            for(int i=0; i<contents.length; i++)
            {
                File f = contents[i];

                if(f.isDirectory())
                    cleanDir(directory);

                f.delete();
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
        BufferedReader br = null; 
        StringBuffer text = new StringBuffer();

        try 
        {
            br = new BufferedReader(new FileReader(filename));
            int i;
            while ((i = br.read()) != -1) 
                text.append((char)i);
        }
        finally 
        {
            if (br != null)
                br.close();
        }

        return text.toString();
    }
    
    
    /**     
     * Writes out the contents to a text file from a single string.     
     *     
     * @param   filename    Name of the file     
     * @param   contents    Contents to store in the file
     * @param   append      Specify if you want to append to the file     
     * @return  Contents of the file as a string
     * @throws  FileNotFoundException if file not found
     * @throws  IOException on IO error
     */    
    public static String setFileContents(String filename, String contents, 
        boolean append) throws FileNotFoundException, IOException    
    {   
        //open the file     
        FileWriter file = new FileWriter(filename, append);     
        
        //make sure we have a file      
        if (file == null)       
        {       
            logger_.error("File does not exist: " + filename);
            throw new FileNotFoundException();      
        }       
        
        //write to the file
        try         
        { 
            //write the contents          
            file.write(contents);
            
            //close the file
            file.close();
        }
        catch (IOException e)        
        {           
            logger_.error("Writing to file failed.", e);
            throw e;        
        }       
        
        //return the contents for validity
        return contents;    
    }
    
    
    /**
     * Retrieves the System specific temp file directory
     *
     * @return        Temp file directory
     */
    public static File getTempDir()
    {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Retrieves a suitable temporary file name for arbitrary use
     * based on the systems temporary directory. The returned
     * string is absolute in form.
     *
     * @return    Tempory file name
     * @throws    IOException on IO error
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
        /* create temp file, delete it, and return the name */
        File tmpFile = File.createTempFile("temp","", dir);
        String filename = tmpFile.getAbsolutePath();
        tmpFile.delete();
        return filename;
    }

    /**
     *  Moves a file to a given directory. The destination
     *  directory must exist and be writable.
     *
     *  @param    srcFile    File to move
     *  @param    destDir    Destination directory
     */
    public static void moveFile(File srcFile, File destDir)
    {
        /*
         * TODO: This is a SIMPLE implementation. There are a lot more 
         *       complicated scenarios involving permissions, attributes, 
         *       existence that need to be accounted for. 
         */
        logger_.debug("Moving " + srcFile + " => " +  destDir);
        
        InputStream is = null;
        OutputStream os = null;
        
        try
        {
            File destFile = new File(destDir, srcFile.getName());
            
            is = new BufferedInputStream(new FileInputStream(srcFile));
            os = new BufferedOutputStream(new FileOutputStream(destFile));
            
            int c;
            
            /* copy contents */
            while(is.available()>0)
                os.write(is.read());
                
            /* close streams */
            is.close();
            os.close();           
            
            /* delete original */    
            srcFile.delete();
        }
        catch(IOException e)
        {
            logger_.error("moveFile.", e);
        }
        finally
        {
            /* cleanup */
        }
    }
}