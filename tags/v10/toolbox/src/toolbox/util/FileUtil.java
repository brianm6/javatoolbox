package toolbox.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;

import org.apache.log4j.Category;


/**
 * File Utility Class
 */
public final class FileUtil
{
	/** Logger **/
	private static Category logger = Category.getInstance(FileUtil.class);
	
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
            throw new IllegalArgumentException("Directory " + directory + " is not a directory.");
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
     *  Reads in the contents of a text file into a single string
     *
     *  @param        filename    Name of the file
     *  @return     Contents of the file as a string
     */
    public static String readFromFile(String filename) throws FileNotFoundException, IOException
    {
        LineNumberReader lnr = new LineNumberReader(new FileReader(filename));
        StringBuffer text = new StringBuffer();

        try 
        {
            String line = null;

            while((line = lnr.readLine()) != null) 
            {
                text.append(line);
                text.append("\n");
            }
        }
        finally 
        {
            if(lnr!= null)
                lnr.close();
        }

        return text.toString();
    }
    
    /**     
     *  Writes out the contents to a text file from a single string.     
     *     
     *  @param 		filename    Name of the file     
     *  @param   	contents    Contents to store in the file
     *  @param   	append	    Specify if you want to append to the file     
     *  @return     Contents of the file as a string
     */    
    public static String writeToFile(String filename, String contents, boolean append) 
    	throws FileNotFoundException, IOException    
    {   
    	//open the file		
    	FileWriter file = new FileWriter(filename, append);		
    	
    	//make sure we have a file		
    	if (file == null) 		
   	    {		
    		logger.error("File does not exist: " + filename);
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
   			logger.error("Writing to file failed.", e);
   			throw e;        
   		}		
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
     *  Retrieves a suitable temporary file name for arbitrary use
     *  based on the systems temporary directory. The returned
     *  string is absolute in form.
     *
     *  @return    Tempory file name
     */
    public static String getTempFilename() throws IOException
    {
        /* create temp file, delete it, and return the name */
        File tmpDir = getTempDir();
        File tmpFile = File.createTempFile("temp","", tmpDir);
        String filename = tmpFile.getAbsolutePath();
        tmpFile.delete();
        return filename;
    }

    /**
     *  Moves a file to a given directory. The destination
     *  directory must exist and be writable.
     *
     *  @param    file       File to move
     *  @param    destDir    Destination directory
     */
    public static void moveFile(File srcFile, File destDir)
    {
    	/*
    	 * TODO: This is a *simple* implementation. There are a lot more complicated
    	 *       scenarios involving permissions, attributes, existence that need
    	 *       to be accounted for. 
    	 */
    	
    	logger.debug("Moving " + srcFile.getAbsolutePath() + " to " + destDir.getAbsolutePath());
    	
    	try
    	{
			File destFile = new File(destDir, srcFile.getName());
			
			InputStream is = new BufferedInputStream(new FileInputStream(srcFile));
			OutputStream os = new BufferedOutputStream(new FileOutputStream(destFile));
			
			int c;
			
			while(is.available()>0)
				os.write(is.read());
				
			is.close();
			os.close();
			
			srcFile.delete();					
    	}
    	catch(Exception e)
    	{
    		logger.error("move file failed.", e);
    	}
    }
}
