package toolbox.ant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * Task that prompts user for property values to allow interactive builds.
 * Admittedly, this task definitely falls way outside the bounds of
 * Ant's core functionality but is useful enough to warrant
 * inclusion amongst the optional tasks.
 * <p>
 * Set project property "promptTimeout" to control behavior.
 * <ul>
 * <li>timeout = -1 --> Cancel prompting. Use default property values.
 * <li>timeout =  0 --> Wait indefinitely for user response (default).
 * <li>timeout =  x --> Wait x seconds for user reponse before using default
 *                      property values (for x > 0).
 * </ul>
 * 
 * @author <a href=mailto:ajyoung@alum.mit.edu>Anthony J. Young-Garner</a>
 */
public class PropertyPromptTask extends Task
{
    private String propertyName_;    // required
    private String defaultValue_;
    private String proposedValue_;   // required
    private String promptText_;      // required
    private String promptCharacter_;
    private int timeout_;
    private boolean useExistingValue_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
        
    /**
     * Creates a ProprertyPromptTask.
     */
    public PropertyPromptTask()
    {
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the prompt text that will be presented to the user.
     * 
     * @param prompt String
     */
    public void addText(String prompt)
    {
        setPrompttext(prompt);
    }
    
    /**
     * Returns defaultValue specified in this task for the Property being set.
     * 
     * @return String
     */
    public String getDefaultvalue()
    {
        return defaultValue_;
    }
    
    /**
     * Returns the terminating character used to punctuate the prompt text.
     * 
     * @return String
     */
    public String getPromptcharacter()
    {
        return promptCharacter_;
    }
    
    /**
     * Returns text of the prompt.
     * 
     * @return String
     */
    public String getPrompttext()
    {
        return promptText_;
    }
    
    /**
     * Returns name of the Ant Project Property being set by this task.
     * 
     * @return String
     */
    public String getPropertyname()
    {
        return propertyName_;
    }
    
    /**
     * Insert the method's description here.
     *
     * @return boolean
     */
    public boolean isUseExistingValue()
    {
        return useExistingValue_;
    }
    
    /**
     * Sets defaultValue for the Property being set by this task.
     * 
     * @param newDefaultvalue Default value
     */
    public void setDefaultvalue(String newDefaultvalue)
    {
        defaultValue_ = newDefaultvalue;
    }
    
    /**
     * Sets the terminating character used to punctuate the prompt text 
     * (default is "?").
     * 
     * @param newPromptcharacter Prompt character
     */
    public void setPromptcharacter(String newPromptcharacter)
    {
        promptCharacter_ = newPromptcharacter;
    }
    
    /**
     * Sets text of the prompt.
     * 
     * @param newPrompttext Prompt
     */
    public void setPrompttext(String newPrompttext)
    {
        promptText_ = newPrompttext;
    }
    
    /**
     * Specifies the Ant Project Property being set by this task.
     * 
     * @param newPropertyname Property name
     */
    public void setPropertyname(String newPropertyname)
    {
        propertyName_ = newPropertyname;
    }
    
    /**
     * Insert the method's description here.
     *
     * @param newUseExistingValue boolean
     */
    public void setUseExistingValue(boolean newUseExistingValue)
    {
        useExistingValue_ = newUseExistingValue;
    }

    //--------------------------------------------------------------------------
    // Overrides org.apache.tools.ant.Task
    //--------------------------------------------------------------------------

    /**
     * Initializes this task.
     */
    public void init()
    {
        super.init();
        initTimeout();
        defaultValue_ = "";
        promptCharacter_ = "?";
        useExistingValue_ = false;
    }
    
    /**
     * Run the PropertyPromptTask task.
     * 
     * @throws BuildException on build error
     */
    public void execute() throws BuildException
    {
        initTimeout();
        proposedValue_ = project.getProperty(propertyName_);
        String currentValue = defaultValue_;
        
        if (currentValue == "" && proposedValue_ != null)
        {
            currentValue = proposedValue_;
        }
        
        if (!((useExistingValue_ == true) && (proposedValue_ != null)))
        {
            if (timeout_ > -1)
            {

                log("Prompting user for " + propertyName_ + ". " + 
                    getDefaultMessage(), Project.MSG_VERBOSE);
                    
                StringBuffer prompt = new StringBuffer();
                //prompt.append("\n");
                prompt.append(promptText_);
                prompt.append(" [");
                prompt.append(currentValue);
                prompt.append("] ");
                prompt.append(promptCharacter_);
                prompt.append(" ");
                System.out.print(prompt.toString());
                System.out.flush();

                // future version should have hooks for validation of user input
                TimedBufferedReader reader =
                    new TimedBufferedReader(new InputStreamReader(System.in));
                    
                reader.setTimeout(timeout_);
                reader.setDefaultString(defaultValue_);
                
                try
                {
                    proposedValue_ = reader.readLine();
                }
                catch (IOException e)
                {
                    log("Prompt failed. Using default.");
                    proposedValue_ = defaultValue_;
                }

                if (proposedValue_.equals("") && !defaultValue_.equals(""))
                    proposedValue_ = defaultValue_;

                if (!proposedValue_.equals(""))
                {
                    /*
                     * According to the mailing list, properties are API mutable
                     * (as opposed to user-properties and the use of multiple
                     * <property> tags to 'mutate' property values).
                     */
                    project.setProperty(propertyName_, proposedValue_);
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Initializes the timeout.
     */
    protected void initTimeout()
    {
        String timeoutProperty = project.getProperty("promptTimeout");

        if (timeoutProperty == null)
        {
            timeout_ = 0;
        }
        else
        {
            try
            {
                timeout_ = Integer.parseInt(timeoutProperty);
            }
            catch (NumberFormatException e)
            {
                log(
                    "Invalid promptTimeout value: "
                        + timeoutProperty
                        + ". Using default (wait indefinitely).");
                timeout_ = 0;
            }
        }
    }

    /**
     * Returns a string to be inserted in the log message indicating whether a 
     * default response was specified in the build file.
     * 
     * @return Message
     */
    private String getDefaultMessage()
    {
        if (defaultValue_ == "")
            return "No default response specified.";
        else
            return "Default response is " + defaultValue_ + ".";
    }

    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
        
    /**
     * Provides a BufferedReader with a readLine method that blocks for only a 
     * specified number of seconds. If no input is read in that time, a 
     * specified default string is returned. Otherwise, the input read is 
     * returned. Thanks to <a href=mailto:doc@drjava.de>Stefan Reich </a>
     * for suggesting this implementation.
     * 
     * @author <a href=mailto:ajyoung@alum.mit.edu>Anthony J. Young-Garner</a>
     */
    private class TimedBufferedReader extends BufferedReader
    {
        private boolean linefeed = true;
        private int timeout = 0;
        private String defaultString = "";

        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------
        
        /**
         * TimedBufferedReader constructor.
         * 
         * @param in Reader to chain
         */
        TimedBufferedReader(Reader in)
        {
            super(in);
        }

        /**
         * TimedBufferedReader constructor.
         * 
         * @param in Reader to chain
         * @param sz int Size of the input buffer.
         */
        TimedBufferedReader(Reader in, int sz)
        {
            super(in, sz);
        }

        //----------------------------------------------------------------------
        // Public
        //----------------------------------------------------------------------
        
        /**
         * Sets number of seconds to block for input.
         * 
         * @param seconds int
         */
        public void setTimeout(int seconds)
        {
            timeout = seconds;
        }

        /**
         * Sets defaultString to use if no input is read.
         * 
         * @param str String
         */
        public void setDefaultString(String str)
        {
            defaultString = str;
        }

        //----------------------------------------------------------------------
        // Overrides java.io.BufferedReader
        //----------------------------------------------------------------------
        
        /**
         * @see java.io.BufferedReader#readLine()
         */
        public String readLine() throws IOException
        {
            int msec = 0;
            int sec = 0;
            
            while (!this.ready())
            {
                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException e)
                {
                    break;
                }
                
                if (msec > 99)
                {
                    sec++;
                    msec = 0;
                }
                else
                    msec++;
                    
                if (timeout != 0 && sec >= timeout)
                {
                    if (linefeed)
                    {
                        System.out.print("\n");
                    }
                    return defaultString;
                }
            }
            
            return super.readLine();
        }
    }
}