package toolbox.clearcase;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;

/**
 * ClearToolBridge.
 */
public class ClearToolBridge implements ClearCaseBridge
{
    private static final Logger logger_ = 
        Logger.getLogger(ClearToolBridge.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private File viewPath_;

    private static final String NODE_HISTORY    = "History";
    private static final String NODE_USER       = "User";
    private static final String NODE_FILE       = "File";
    private static final String NODE_COMMENT    = "Comment";
    private static final String NODE_ACTION     = "Operation";

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ClearToolBridge.
     * 
     */
    public ClearToolBridge()
    {
    }

    //--------------------------------------------------------------------------
    // ClearCaseBridge Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.clearcase.ClearCaseBridge#setViewPath(java.io.File)
     */
    public void setViewPath(File path)
    {
        viewPath_ = path;
    }


    /**
     * @see toolbox.clearcase.ClearCaseBridge#getViewPath()
     */
    public File getViewPath()
    {
        return viewPath_;
    }


    /**
     * @see toolbox.clearcase.ClearCaseBridge#findChangedFiles(java.util.Date, 
     *      java.util.Date, java.io.FilenameFilter)
     */
    public List findChangedFiles(Date start, Date end, FilenameFilter filter)
        throws IOException
    {
        // -fmt '%t'
        //"cleartool lshistory -recurse -since 21-Feb-05.23:59 -fmt \"%u\n %o  %n %e %d %c\"",
        
        String command = 
            "cleartool lshistory -recurse -since 21-Feb-05.23:59 -fmt \"" 
            + "<History>[NL]" 
            + "  <User>%u</User>[NL]"
            + "  <Operation>%o</Operation>[NL]"
            + "  <File>%En</File>[NL]"
            + "  <OperationDetail>%e</OperationDetail>[NL]"
            + "  <TimeStamp>%d</TimeStamp>[NL]"
            + "  <Comment>%Nc</Comment>[NL]"
            + "</History>[NL]\"";
        
        Process p = Runtime.getRuntime().exec(command, null, getViewPath());
        InputStream is = p.getInputStream();
        InputStream es = p.getErrorStream();
        
        //IOUtils.copy(is, System.out);
        byte[] b = IOUtils.toByteArray(is);
        
        String in = new String(b);
        String lf = new String("[NL]");
        String out = StringUtils.replace(in, lf, "\n");
        out = StringUtils.replace(out, "&", "&amp;");
        String xml = "<HistoryList>\n" + out + "</HistoryList>";

        List result = new ArrayList();
        
        try
        {
            //System.out.println(xml);
            Element root = XOMUtil.toElement(xml);
            //System.out.println(XOMUtil.format(root));
            Elements histories = root.getChildElements(NODE_HISTORY);
            
            for (int i = 0, n = histories.size(); i < n; i++) 
            {
                Element history = histories.get(i);
                String user = history.getFirstChildElement(NODE_USER).getValue();
                
                String file = 
                    FileUtil.trailWithSeparator(
                        getViewPath().getAbsolutePath()) 
                        + history.getFirstChildElement(NODE_FILE).getValue();
                
                String comment = history.getFirstChildElement(NODE_COMMENT).getValue();
                String action = history.getFirstChildElement(NODE_ACTION).getValue();
                
                File f = new File(file);
                
                if (action.equals("checkin") && f.isFile())
                {
                    VersionedFile vf = new VersionedFile();
                    vf.setName(file);
                    
                    Revision rev = new Revision();
                    rev.setAction(action);
                    rev.setComment(comment);
                    rev.setUser(user);
                    
                    vf.addRevision(rev);
                    result.add(vf);
                }
                else
                {
                    //System.err.println("Invalid: " + file);
                }
            }
        }
        catch (ParsingException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        //String output = IOUtils.toString(is);
        //System.out.println(output);
        
        System.out.println(
            StringUtil.banner(
                "Errors: \n" + IOUtils.toString(es)));

        return result;
    }
}