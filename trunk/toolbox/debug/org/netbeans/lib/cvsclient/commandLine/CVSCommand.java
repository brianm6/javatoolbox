/*****************************************************************************
 * Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the CVS Client Library.
 * The Initial Developer of the Original Code is Robert Greig.
 * Portions created by Robert Greig are Copyright (C) 2000.
 * All Rights Reserved.
 *
 * Contributor(s): Robert Greig.
 *****************************************************************************/
package org.netbeans.lib.cvsclient.commandLine;

import java.io.*;

import org.netbeans.lib.cvsclient.*;
import org.netbeans.lib.cvsclient.admin.*;
import org.netbeans.lib.cvsclient.command.*;
import org.netbeans.lib.cvsclient.connection.*;

/**
 * An implementation of the standard CVS client utility (command line tool)
 * in Java
 * @author  Robert Greig
 */
public class CVSCommand {
    /**
     * The path to the repository on the server
     */
    private String repository;

    /**
     * The local path to use to perform operations (the top level)
     */
    private String localPath;

    /**
     * The connection to the server
     */
    private Connection connection;

    /**
     * The client that manages interactions with the server
     */
    private Client client;

    /**
     * The global options being used. GlobalOptions are only global for a
     * particular command.
     */
    private GlobalOptions globalOptions;

    /**
     * A struct containing the various bits of information in a CVS root
     * string, allowing easy retrieval of individual items of information
     */
    private static class CVSRoot {
        public String connectionType;
        public String user;
        public String host;
        public String repository;
        public int port = 2401;

        public CVSRoot(String root) throws IllegalArgumentException {
            if (!root.startsWith(":"))
                throw new IllegalArgumentException();

            int oldColonPosition = 0;
            int colonPosition = root.indexOf(':', 1);
            if (colonPosition == -1)
                throw new IllegalArgumentException();
            connectionType = root.substring(oldColonPosition + 1, colonPosition);
            oldColonPosition = colonPosition;
            colonPosition = root.indexOf('@', colonPosition + 1);
            if (colonPosition == -1)
                throw new IllegalArgumentException();
            user = root.substring(oldColonPosition + 1, colonPosition);
            oldColonPosition = colonPosition;
            colonPosition = root.indexOf(':', colonPosition + 1);
            if (colonPosition == -1)
                throw new IllegalArgumentException();
            host = root.substring(oldColonPosition + 1, colonPosition);
            repository = root.substring(colonPosition + 1);
            boolean isNumber = true;
            int index = 0;
            String numString = "";
            while (isNumber) {
                try {
                    int num = Integer.parseInt(repository.substring(index, index + 1));
                    numString = numString + Integer.toString(num);
                    index = index + 1;
                }
                catch (NumberFormatException exc) {
                    isNumber = false;
                }
            }
            if (numString.length() > 0) {
                try {
                    port = Integer.parseInt(numString);
                    repository = root.substring(numString.length());
                }
                catch (NumberFormatException exc) {
                }
            }

            if (connectionType == null || user == null || host == null ||
                    repository == null)
                throw new IllegalArgumentException();
        }
    }

    /**
     * Execute a configured CVS command
     * @param command the command to execute
     * @throws CommandException if there is an error running the command
     */
    public void executeCommand(Command command)
            throws CommandException, AuthenticationException {
        client.executeCommand(command, globalOptions);
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public void setGlobalOptions(GlobalOptions globalOptions) {
        this.globalOptions = globalOptions;
    }

    /**
     * This handles the "server" connection
     */
    private void connectToServer(PrintStream stdout, PrintStream stderr)
            throws AuthenticationException {
        ServerConnection c = new ServerConnection();
        connection = c;
        c.setRepository(repository);
        c.open();

        client = new Client(c, new StandardAdminHandler());

        client.setLocalPath(localPath);

        // add a listener to the client
        client.getEventManager().addCVSListener(new BasicListener(stdout, stderr));
    }

    /**
     * This handles "pserver" connection
     */
    private void connectToServer(String userName, String encodedPassword,
                                 String hostName, int port,
                                 PrintStream stdout, PrintStream stderr)
            throws AuthenticationException {
                
        //System.out.println("connectToServer encoded pw: " + encodedPassword);
               
        PServerConnection c = new PServerConnection();
        connection = c;
        c.setUserName(userName);
        c.setEncodedPassword(encodedPassword);
        c.setHostName(hostName);
        c.setRepository(repository);
        c.setPort(port);
        c.open();

        client = new Client(c, new StandardAdminHandler());

        client.setLocalPath(localPath);

        // add a listener to the client
        client.getEventManager().addCVSListener(new BasicListener(stdout, stderr));
    }

    private void close(PrintStream stderr) {
        try {
            connection.close();
        }
        catch (IOException e) {
            stderr.println("Unable to close connection: " + e);
            //e.printStackTrace();
        }
    }

    /**
     * Obtain the CVS root, either from the -D option cvs.root or from
     * the CVS directory
     * @return the CVSRoot string
     */
    private static String getCVSRoot() {
        String root = null;
        BufferedReader r = null;
        try {
            File f = new File(System.getProperty("user.dir"));
            File rootFile = new File(f, "CVS/Root");
            if (rootFile.exists()) {
                r = new BufferedReader(new FileReader(rootFile));
                root = r.readLine();
            }
        }
        catch (IOException e) {
            // ignore
        }
        finally {
            try {
                if (r != null)
                    r.close();
            }
            catch (IOException e) {
                System.err.println("Warning: could not close CVS/Root file!");
            }
        }
        if (root == null) {
            root = System.getProperty("cvs.root");
        }
        return root;
    }

    /**
     * Process global options passed into the application
     * @param args the argument list, complete
     * @param globalOptions the global options structure that will be passed
     * to the command
     */
    private static int processGlobalOptions(String[] args,
                                            GlobalOptions globalOptions,
                                            PrintStream stderr) {
        final String getOptString = "Hrwd:z:";
        GetOpt go = new GetOpt(args, getOptString);
        int ch = -1;
        boolean usagePrint = false;
        while ((ch = go.getopt()) != go.optEOF) {
            if ((char)ch == 'H')
                usagePrint = true;
            else if ((char)ch == 'r')
                globalOptions.setCheckedOutFilesReadOnly(true);
            else if ((char)ch == 'w')
                globalOptions.setCheckedOutFilesReadOnly(false);
            else if ((char)ch == 'd')
                globalOptions.setCVSRoot(go.optArgGet());
            else if ((char)ch == 'z') {
                globalOptions.setUseGzip(true);
                // we just ignore the zip level but include it for
                // completeness
                /*Object zipLevel =*/ go.optArgGet();
            }
            else {
                usagePrint = true;
            }
        }
        if (usagePrint) {
            showUsage(stderr);
            return -10;
        }
        return go.optIndexGet();
    }

    private static void showUsage(PrintStream stderr) {
        stderr.println("Usage: cvs [global options] command [options]");
    }

    /**
     * Perform the 'login' command, asking the user for a password. If the
     * login is successful, the password is written to a file. The file's
     * location is user.home, unless the cvs.passfile option is set.
     * @param userName the userName
     * @param hostName the host
     */
    private static boolean performLogin(String userName, String hostName,
                                     String repository, int port,
                                     GlobalOptions globalOptions) {
        PServerConnection c = new PServerConnection();
        c.setUserName(userName);
        String password = null;
        
        String overridePassword = System.getProperty("cvs.password");
       
        // HACK : Override getting of password via command line if one is
        //        specified in the property cvs.password.
         
        if (overridePassword != null)
        {
            System.out.println("Using cvs.password...");
            password = overridePassword;
        }
        else
        {
            try {
                BufferedReader in = new BufferedReader(new
                        InputStreamReader(System.in));
                System.out.print("Enter password: ");
                password = in.readLine();
            }
            catch (IOException e) {
                System.err.println("Could not read password: " + e);
                return false;
            }
        }

        String encodedPassword = StandardScrambler.getInstance().scramble(
                password);
        c.setEncodedPassword(encodedPassword);
        c.setHostName(hostName);
        c.setRepository(repository);
        c.setPort(port);
        try {
            c.verify();
        }
        catch (AuthenticationException e) {
            System.err.println("Could not login to host " + hostName);
            System.err.println(e.getMessage());
            //e.printStackTrace();
            return false;
        }
        // was successful, so write the appropriate file out
        // we look for cvs.passfile being set, but if not use user.dir
        // as the default
        File passFile = new File(System.getProperty("cvs.passfile",
                                                    System.getProperty("user.home") +
                                                    "/.cvspass"));
        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {
            if (passFile.createNewFile()) {
                writer = new BufferedWriter(new FileWriter(passFile));
                writer.write(globalOptions.getCVSRoot() + " " +
                             encodedPassword);
                writer.close();
            }
            else {
                File tempFile = File.createTempFile("cvs", "tmp");
                reader = new BufferedReader(new FileReader(passFile));
                writer = new BufferedWriter(new FileWriter(tempFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(globalOptions.getCVSRoot())) {
                        writer.write(globalOptions.getCVSRoot() + " " +
                                     encodedPassword);
                    }
                    else {
                        writer.write(line);
                    }
                }
                reader.close();
                writer.close();
                File temp2File = File.createTempFile("cvs", "tmp");
                passFile.renameTo(temp2File);
                tempFile.renameTo(passFile);
                temp2File.delete();
                tempFile.delete();
            }
        }
        catch (IOException e) {
            System.err.println("Error: could not write password file to " +
                               passFile);
            return false;
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
            catch (Exception e) {
                // ignore
            }
        }

        System.err.println("Logged in successfully to repository " +
                           repository + " on host " + hostName);
        return true;
    }

    /**
     * Lookup the password in the .cvspass file. This file is looked for
     * in the user.home directory if the option cvs.passfile is not set
     * @param CVSRoot the CVS root for which the password is being searched
     * @return the password, scrambled
     */
    private static String lookupPassword(String CVSRoot, PrintStream stderr) {
        File passFile = new File(System.getProperty("cvs.passfile",
                                                    System.getProperty("user.home") +
                                                    "/.cvspass"));

        BufferedReader reader = null;
        String password = null;

        try {
            reader = new BufferedReader(new FileReader(passFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(CVSRoot)) {
                    password = line.substring(CVSRoot.length() + 1);
                    break;
                }
            }
        }
        catch (IOException e) {
            stderr.println("Could not read password for host: " + e);
            return null;
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    stderr.println("Warning: could not close password file.");
                }
            }
        }
        return password;
    }

    /**
     * Execute the CVS command and exit JVM.
     */
    public static void main(String[] args) {
        if (processCommand(args, null, System.getProperty("user.dir"),
                           System.out, System.err)) {
            //System.exit(0);
        } else {
            //System.exit(1);
        }
    }
    
    /**
     * Process the CVS command passed in args[] array with all necessary options.
     * The only difference from main() method is, that this method does not exit
     * the JVM and provides command output.
     * @param args The command with options
     * @param files The files to execute the command on.
     * @param stdout The standard output of the command
     * @param stderr The error output of the command.
     */
    public static boolean processCommand(String[] args, File[] files, String localPath,
                                         PrintStream stdout, PrintStream stderr) {
        // Set up the CVSRoot. Note that it might still be null after this
        // call if the user has decided to set it with the -d command line
        // global option
        GlobalOptions globalOptions = new GlobalOptions();
        globalOptions.setCVSRoot(getCVSRoot());

        // Set up any global options specified. These occur before the
        // name of the command to run
        int commandIndex = -1;
        try {
            commandIndex = processGlobalOptions(args, globalOptions, stderr);
            if (commandIndex == -10) return true;
        }
        catch (IllegalArgumentException e) {
            stderr.println("Invalid argument: " + e);
            return false;
        }

        // if we don't have a CVS root by now, the user has messed up
        if (globalOptions.getCVSRoot() == null) {
            stderr.println("No CVS root is set. Use the cvs.root " +
                           "property, e.g. java -Dcvs.root=\":pserver:user@host:/usr/cvs\"" +
                           " or start the application in a directory containing a CVS subdirectory" +
                           " or use the -d command switch.");
            return false;
        }

        // parse the CVS root into its constituent parts
        CVSRoot root = null;
        final String cvsRoot = globalOptions.getCVSRoot();
        try {
            root = new CVSRoot(cvsRoot);
        }
        catch (IllegalArgumentException e) {
            stderr.println("Incorrect format for CVSRoot: " + cvsRoot +
                           "\nThe correct format is: :<connection-type>:user@host:<repository path>" +
                           "\nwhere <connection-type> is pserver and <repository path> is the " +
                           "path to the cvs repository on the server.");
            return false;
        }

        // if we had some options without any command, then the user messed up
        if (commandIndex >= args.length) {
            showUsage(stderr);
            return false;
        }

        final String command = args[commandIndex];
        if (command.equals("login")) {
            if (root.connectionType.equals("pserver")) {
                return performLogin(root.user, root.host, root.repository, root.port,
                                    globalOptions);
            }
            else {
                stderr.println("login does not apply for connection type " +
                               "\'" + root.connectionType + "\'");
                return false;
            }
        }

        // this is not login, but a 'real' cvs command, so construct it,
        // set the options, and then connect to the server and execute it

        Command c = null;
        try {
            c = CommandFactory.getCommand(command, args, ++commandIndex);
        }
        catch (IllegalArgumentException e) {
            stderr.println("Illegal argument: " + e.getMessage());
            return false;
        }
        
        if (files != null && c instanceof BasicCommand) {
            ((BasicCommand) c).setFiles(files);
        }

        String password = null;

        if (root.connectionType.equals("pserver"))
            password = lookupPassword(cvsRoot, stderr);
        CVSCommand cvsCommand = new CVSCommand();
        cvsCommand.setGlobalOptions(globalOptions);
        cvsCommand.setRepository(root.repository);
        // the local path is just the path where we executed the
        // command. This is the case for command-line CVS but not
        // usually for GUI front-ends
        cvsCommand.setLocalPath(localPath);
        try {
            if (root.connectionType.equals("pserver"))
                cvsCommand.connectToServer(root.user, password, root.host, root.port,
                                           stdout, stderr);
            else
                cvsCommand.connectToServer(stdout, stderr);
            cvsCommand.executeCommand(c);
        }
        catch (Exception t) {
            stderr.println("Error: " + t);
            t.printStackTrace(stderr);
            return false;
        }
        finally {
            if (cvsCommand != null) {
                cvsCommand.close(stderr);
            }
        }
        return true;
    }
}
