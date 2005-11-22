package toolbox.dbconsole;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;


import toolbox.dbconsole.command.ColumnsCommand;
import toolbox.dbconsole.command.CopyDatabaseCommand;
import toolbox.dbconsole.command.ExportDatabaseCommand;
import toolbox.dbconsole.command.ImportDatabaseCommand;
import toolbox.dbconsole.command.LoadCommand;
import toolbox.dbconsole.command.LoadDirCommand;
import toolbox.dbconsole.command.PingCommand;
import toolbox.dbconsole.command.SelectCommand;
import toolbox.dbconsole.command.SetEnvCommand;
import toolbox.dbconsole.command.TablesCommand;
import toolbox.dbconsole.command.VersionCommand;
import toolbox.util.ArrayUtil;

/**
 * DbConsole
 *
 * @see toolbox.dbconsole.SwingTestConsole
 */
public class TestConsole extends Console {

    private static final Log logger = LogFactory.getLog(TestConsole.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Maps a command's name to its respective implementing class.
     */
    private Map commandMap;

    /**
     * Reference to the currently active test environment.
     */
    private TestEnvironment testEnv;

    /**
     * The unaltered complete command line for those instances where parsing
     * as tokens is not suitable.
     */
    private String commandLine;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    /**
     * Entrypoint.
     *
     * @param args None
     */
    public static void main(String[] args) {
        new TestConsole().startConsole();
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a TestConsole.
     */
    public TestConsole() {
        buildCommandMap();
    }


    /**
     * Creates a TestConsole.
     *
     * @param in Stream to read commands from.
     * @param out Stream to write command output to.
     */
    public TestConsole(InputStream in, PrintStream out) {
        super(in,out);
        buildCommandMap();
    }

    //--------------------------------------------------------------------------
    // Overrides Console
    //--------------------------------------------------------------------------

    public String getPrompt() {
        return "COMMAND>";
    }


    public void handleCommand(String cmdLine) {

        setCommandLine(cmdLine);

        String[] tokens = StringUtils.split(cmdLine);

        if (tokens.length == 0) {
            super.handleCommand(cmdLine);
        }
        else{

            String cmd = tokens[0];
            Class clazz = (Class) commandMap.get(cmd);

            if (clazz == null) {
                super.handleCommand(cmd);
            }
            else {
                try {
                    Command command = (Command) clazz.newInstance();
                    String[] args = (String[]) ArrayUtil.remove(tokens, cmd);
                    command.execute(this, args);
                }
                catch (Exception e) {
                    logger.error("Command failed", e);
                }
            }
        }
    }


    protected void commandHelp() {
        PrintStream ps = getPrintStream();
        ps.println(StringUtils.repeat("=", 80));
        ps.println(StringUtils.center("TestConsole Help", 80));
        ps.println(StringUtils.repeat("=", 80));

        for (Iterator i = commandMap.values().iterator(); i.hasNext();) {
            Class clazz = (Class) i.next();

            try {
                Command c = (Command) clazz.newInstance();

                ps.println(StringUtils.rightPad(
                    c.getName(), 10) + " - " + c.getDescription());
            }
            catch (Exception e) {
                ps.println(e);
            }

        }

        ps.println(StringUtils.repeat("=", 80));
        ps.println(StringUtils.center("Console Help", 80));
        ps.println(StringUtils.repeat("=", 80));

        super.commandHelp();
    }

    
    /**
     * Prints out version on startup.
     */
    public void startConsole() {
        handleCommand(new VersionCommand().getName());

        // Hook the log4j appender up to console output stream

        Logger.getRootLogger().addAppender(
            new WriterAppender(new SimpleLayout(), getPrintStream()));

        Logger.getRootLogger().addAppender(
            new ConsoleAppender(new SimpleLayout()));

        Logger.getRootLogger().info("Logger initialized.");

        getPrintStream().println("Type 'help' for more info.");
        super.startConsole();
    }    
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Registers commands with the console.
     */
    protected void buildCommandMap() {
        commandMap = new TreeMap(); // Keeps sorted
        
        // Generic commands
        commandMap.put(new SetEnvCommand().getName(), SetEnvCommand.class);
        commandMap.put(new PingCommand().getName(), PingCommand.class);
        commandMap.put(new SelectCommand().getName(), SelectCommand.class);
        commandMap.put(new TablesCommand().getName(), TablesCommand.class);
        commandMap.put(new ColumnsCommand().getName(), ColumnsCommand.class);
        
        commandMap.put(
            new CopyDatabaseCommand().getName(), 
            CopyDatabaseCommand.class);
        
        commandMap.put(
            new LoadDirCommand().getName(), 
            LoadDirCommand.class);
        
        commandMap.put(
            new ExportDatabaseCommand().getName(), 
            ExportDatabaseCommand.class);
        
        commandMap.put(
            new ImportDatabaseCommand().getName(), 
            ImportDatabaseCommand.class);
        
        // Vacancy commands
        commandMap.put(new LoadCommand().getName(), LoadCommand.class);
        commandMap.put(new VersionCommand().getName(), VersionCommand.class);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Returns the currently active TestEnvironment or null if one hasn't been
     * set.
     *
     * @return TestEnvironment
     */
    public TestEnvironment getTestEnv() {
        return testEnv;
    }


    /**
     * Sets the currently active TestEnvironment. Can be null so indicate no
     * TestEnvironment has been set.
     *
     * @param profile Test environment.
     */
    public void setTestEnv(TestEnvironment testEnvironment) {
        testEnv = testEnvironment;
    }


    /**
     * Returns the full text of the entire command as read from the command line.
     *
     * @return String
     */
    public String getCommandLine() {
        return commandLine;
    }


    /**
     * Sets the internal command line.
     *
     * @param string Full command line text.
     */
    public void setCommandLine(String string) {
        commandLine = string;
    }
}