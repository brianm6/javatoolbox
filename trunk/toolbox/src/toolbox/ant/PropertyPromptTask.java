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
    private String propertyname;    // required
    private String defaultvalue;
    private String proposedValue;   // required
    private String prompttext;      // required
    private String promptcharacter;
    private int timeout;

    private boolean useExistingValue;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
        
    /**
     * PropertyPromptTask default constructor.
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
     * Run the PropertyPromptTask task.
     * 
     * @throws BuildException on build error
     */
    public void execute() throws BuildException
    {
        initTimeout();
        proposedValue = project.getProperty(propertyname);
        String currentValue = defaultvalue;
        
        if (currentValue == "" && proposedValue != null)
        {
            currentValue = proposedValue;
        }
        
        if (!((useExistingValue == true) && (proposedValue != null)))
        {
            if (timeout > -1)
            {

                log("Prompting user for " + propertyname + ". " + 
                    getDefaultMessage(), Project.MSG_VERBOSE);
                    
                StringBuffer prompt = new StringBuffer();
                //prompt.append("\n");
                prompt.append(prompttext);
                prompt.append(" [");
                prompt.append(currentValue);
                prompt.append("] ");
                prompt.append(promptcharacter);
                prompt.append(" ");
                System.out.print(prompt.toString());
                System.out.flush();

                // future version should have hooks for validation of user input
                TimedBufferedReader reader =
                    new TimedBufferedReader(new InputStreamReader(System.in));
                    
                reader.setTimeout(timeout);
                reader.setDefaultString(defaultvalue);
                
                try
                {
                    proposedValue = reader.readLine();
                }
                catch (IOException e)
                {
                    log("Prompt failed. Using default.");
                    proposedValue = defaultvalue;
                }

                if (proposedValue.equals("") && !defaultvalue.equals(""))
                    proposedValue = defaultvalue;

                if (!proposedValue.equals(""))
                {
                    /*
                     * According to the mailing list, properties are API mutable
                     * (as opposed to user-properties and the use of multiple
                     * <property> tags to 'mutate' property values).
                     */
                    project.setProperty(propertyname, proposedValue);
                }
            }
        }
    }
    
    /**
     * Returns a string to be inserted in the log message
     * indicating whether a default response was specified
     * in the build file.
     */
    private String getDefaultMessage()
    {
        if (defaultvalue == "")
        {
            return "No default response specified.";
        }
        else
            return "Default response is " + defaultvalue + ".";
    }
    
    /**
     * Returns defaultValue specified in this task for the Property being set.
     * 
     * @return String
     */
    public String getDefaultvalue()
    {
        return defaultvalue;
    }
    
    /**
     * Returns the terminating character used to 
     * punctuate the prompt text.
     */
    public String getPromptcharacter()
    {
        return promptcharacter;
    }
    
    /**
     * Returns text of the prompt.
     * 
     * @return String
     */
    public String getPrompttext()
    {
        return prompttext;
    }
    
    /**
     * Returns name of the Ant Project Property
     * being set by this task.
     * 
     * @return String
     */
    public String getPropertyname()
    {
        return propertyname;
    }
    
    /**
     * Initializes this task.
     */
    public void init()
    {
        super.init();
        initTimeout();
        defaultvalue = "";
        promptcharacter = "?";
        useExistingValue = false;
    }
    
    /**
     * Insert the method's description here.
     */
    private void initTimeout()
    {
        String timeoutProperty = project.getProperty("promptTimeout");

        if (timeoutProperty == null)
        {
            timeout = 0;
        }
        else
        {
            try
            {
                timeout = Integer.parseInt(timeoutProperty);
            }
            catch (NumberFormatException e)
            {
                log(
                    "Invalid promptTimeout value: "
                        + timeoutProperty
                        + ". Using default (wait indefinitely).");
                timeout = 0;
            }
        }
    }
    
    /**
     * Insert the method's description here.
     *
     * @return boolean
     */
    public boolean isUseExistingValue()
    {
        return useExistingValue;
    }
    
    /**
     * Sets defaultValue for the Property being set by this task.
     * 
     * @param newDefaultvalue String
     */
    public void setDefaultvalue(String newDefaultvalue)
    {
        defaultvalue = newDefaultvalue;
    }
    
    /**
     * Sets the terminating character used to punctuate the prompt text 
     * (default is "?").
     * 
     * @param newPromptcharacter String
     */
    public void setPromptcharacter(String newPromptcharacter)
    {
        promptcharacter = newPromptcharacter;
    }
    
    /**
     * Sets text of the prompt.
     * 
     * @param newPrompttext String
     */
    public void setPrompttext(String newPrompttext)
    {
        prompttext = newPrompttext;
    }
    
    /**
     * Specifies the Ant Project Property being set by this task.
     * 
     * @param newPropertyname String
     */
    public void setPropertyname(String newPropertyname)
    {
        propertyname = newPropertyname;
    }
    
    /**
     * Insert the method's description here.
     *
     * @param newUseExistingValue boolean
     */
    public void setUseExistingValue(boolean newUseExistingValue)
    {
        useExistingValue = newUseExistingValue;
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

        /**
         * TimedBufferedReader constructor.
         * 
         * @param in Reader
         */
        TimedBufferedReader(Reader in)
        {
            super(in);
        }

        /**
         * TimedBufferedReader constructor.
         * 
         * @param in Reader
         * @param sz int Size of the input buffer.
         */
        TimedBufferedReader(Reader in, int sz)
        {
            super(in, sz);
        }

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