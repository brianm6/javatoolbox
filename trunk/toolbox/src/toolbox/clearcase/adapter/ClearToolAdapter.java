package toolbox.clearcase.adapter;

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
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

import toolbox.clearcase.IClearCaseAdapter;
import toolbox.clearcase.domain.Revision;
import toolbox.clearcase.domain.VersionedFile;
import toolbox.util.FileUtil;
import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;

/**
 * ClearToolAdapter is an implemenation of 
 * {@link toolbox.clearcase.IClearCaseAdapter} this uses the cleartool command
 * to communicate with a clearcase repository.
 */
public class ClearToolAdapter implements IClearCaseAdapter
{
    private static final Logger logger_ = 
        Logger.getLogger(ClearToolAdapter.class);

    //--------------------------------------------------------------------------
    // ClearTool Output XML Constants
    //--------------------------------------------------------------------------
    
    private static final String NODE_HISTORY = "History";
    private static final String NODE_USER    = "User";
    private static final String NODE_FILE    = "File";
    private static final String NODE_COMMENT = "Comment";
    private static final String NODE_ACTION  = "Operation";
    private static final String NODE_DATE    = "TimeStamp";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Path to the clearcase view.
     */
    private File viewPath_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ClearToolAdapter.
     */
    public ClearToolAdapter()
    {
    }

    //--------------------------------------------------------------------------
    // IClearCaseAdapter Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.clearcase.IClearCaseAdapter#setViewPath(java.io.File)
     */
    public void setViewPath(File path)
    {
        viewPath_ = path;
    }


    /**
     * @see toolbox.clearcase.IClearCaseAdapter#getViewPath()
     */
    public File getViewPath()
    {
        return viewPath_;
    }


    /**
     * @see toolbox.clearcase.IClearCaseAdapter#findChangedFiles(java.util.Date, 
     *      java.util.Date, java.io.FilenameFilter)
     */
    public List findChangedFiles(Date start, Date end, FilenameFilter filter)
        throws IOException
    {
        // -fmt '%t'
        //"cleartool lshistory -recurse -since 21-Feb-05.23:59 -fmt \"%u\n %o  %n %e %d %c\"",
        
        String dateStr = DateFormatUtils.format(start, "dd-MMM-yy.HH:mm");
        
        
        String command = 
            //"cleartool lshistory -recurse -since 21-Feb-05.23:59 -fmt \"" 
            "cleartool lshistory -recurse -since " + dateStr + " -fmt \""
            + "<History>[NL]" 
            + "  <User>%Fu</User>[NL]"
            + "  <Operation>%o</Operation>[NL]"
            + "  <File>%En</File>[NL]"
            + "  <OperationDetail>%e</OperationDetail>[NL]"
            + "  <TimeStamp>%d</TimeStamp>[NL]"
            + "  <Comment>%Nc</Comment>[NL]"
            + "</History>[NL]\"";
        
        logger_.debug("Command: \n " + command);
        
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
                String date = history.getFirstChildElement(NODE_DATE).getValue();
                
                File f = new File(file);
                
                if (action.equals("checkin") && f.isFile())
                {
                    VersionedFile vf = new VersionedFile();
                    vf.setName(file);
                    
                    Revision rev = new Revision();
                    rev.setAction(action);
                    rev.setComment(comment);
                    rev.setUser(user);
                    rev.setDate(date);
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