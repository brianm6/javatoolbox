package toolbox.util.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;

import org.apache.log4j.Logger;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import toolbox.util.StringUtil;

/**
 * A ListModel that filters the text contents based on matching a regular 
 * expression
 */
public class RegexListModelFilter extends AbstractListModelFilter
{
    /** Logger */
    public static final Logger logger_ =
        Logger.getLogger(RegexListModelFilter.class);

    /**
     * Default regular expression is to match all
     */    
    public static final String MATCH_ALL = ".*";
    
    /**
     * Collection of indices that pass the filtering criteria
     */
    private List indexList_ = new ArrayList();

    /**
     * Regular expression used to filter list contents
     */
    private String regex_;
    
    /** 
     * Flag to make the regular expression matcher case sensetive
     */
    private boolean matchCase_;
    
    /** 
     * Regular expression matcher
     */
    private RE matcher_;


    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a list model
     * 
     * @param  delegate  List model to filter
     */
    public RegexListModelFilter(ListModel delegate)
    {
        this(delegate, MATCH_ALL);
    }

    /**
     * Creates a list model
     * 
     * @param  delegate  List model to filter
     * @param  regex     Regular expression
     */
    public RegexListModelFilter(ListModel delegate, String regex)
    {
        this(delegate, regex, false);
    }

    /**
     * Creates a list model
     * 
     * @param  delegate   List model to filter
     * @param  regex      Regular expression
     * @param  matchCase  Flag to match case
     */
    public RegexListModelFilter(ListModel delegate, String regex, 
        boolean matchCase)
    {
        super(delegate);
        matchCase_ = matchCase;        
        setRegex(regex);
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the regular expression to filter on   
     * 
     * @param  regex  Regular expression
     */
    public synchronized void setRegex(String regex)
    {
        // Assign the property value and clear the collection
        regex_ = regex;

        // Match all on null or empty
        if (StringUtil.isNullOrEmpty(regex_))
            regex_ = MATCH_ALL;
            
        try
        {
        
            matcher_ = new RE(regex_);
        
            if (!matchCase_)
                matcher_.setMatchFlags(RE.MATCH_CASEINDEPENDENT);        
        }
        catch (RESyntaxException e)
        {
            logger_.error("Bad pattern: " + regex_, e);
            return;
        }

        indexList_.clear();

        // Iterate through the model
        for (int i = 0; i < getDelegate().getSize(); i++)
        {
            // If the string matches matches the regular expression,
            // add the index as an element to the index collection
            
            String element = (String) getDelegate().getElementAt(i);
            if (matcher_.match(element))
                indexList_.add(new Integer(i));
        }

        // Tell the component that the data have changed
        fireContentsChanged(this, 0, indexList_.size());
    }

    /**
     * @return  The regular expression used to filter
     */
    public String getRegex()
    {
        return regex_;
    }

    //--------------------------------------------------------------------------
    //  Overridden from AbstractListModelFilter
    //--------------------------------------------------------------------------

    /**
     * @return The size of the filtered model
     */
    public int getSize()
    {
        // Return size of index collection.  If no filter has been set
        return indexList_.size();
    }

    /**
     * Gets an element at a given zero based index
     * 
     * @param   index  Index of element to retrieve
     * @return  Element at specifiec index
     */
    public Object getElementAt(int index)
    {
        // return the filtered data element
        int newIndex = ((Integer) indexList_.get(index)).intValue();
        return getDelegate().getElementAt(newIndex);
    }
}