package toolbox.plugin.jdbc;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.RandomUtil;

/**
 * This is a sample implementation of the Transaction Processing Performance
 * Council Benchmark B coded in Java and ANSI SQL2. This version is using one
 * connection per thread to parallellize server operations.
 */
public class DBBenchmark 
{
    private static final Logger logger_ = Logger.getLogger(DBBenchmark.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    public final static int TELLER = 0;
    public final static int BRANCH = 1;
    public final static int ACCOUNT = 2;
    
    //--------------------------------------------------------------------------
    // Static
    //--------------------------------------------------------------------------
    
    // tpc bm b scaling rules
    
    /**
     * the tps scaling factor: here it is 1 
     */
    private static int tps_ = 1;      
    
    /**
     * number of branches in 1 tps db       
     */
    private static int numBranches_ = 1;
    
    /** 
     * number of tellers in  1 tps db       
     */
    private static int numTellers_ = 10;
    
    /**
     * number of accounts in 1 tps db       
     */
    private static int numAccounts_ = 100000;
    
    /** 
     * number of history recs in 1 tps db   
     */
    private static int numHistory_  = 864000; 
    
    /**
     * Use transactions.
     */
    private static boolean transactions_ = true;
    
    /**
     * Use prepared statements.
     */
    private static boolean preparedStmt_ = false;
    
    /**
     * Table extension.
     */
    private static String tableExtension_ = "";
    
    /**
     * Create extension.
     */
    private static String createExtension_ = "";
    
    /**
     * Shutdown command.
     */
    private static String shutdownCommand_ = "";
    
    /**
     * Tab file.
     */
    private static PrintStream tabFile_ = 
        new PrintStream(new NullOutputStream());
    
    /**
     * Verbose flag.
     */
    private static boolean verbose_ = false;
    
    /**
     * Default number of clients.
     */
    private static int numClients_ = 10;
    
    /**
     * Default number of transactions per client.
     */
    private static int numTxPerClient_ = 10;
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Number of failed transactions.
     */
    private int failedTx_ = 0;
    
    /**
     * Transaction count.
     */
    private int txCount_ = 0;
    
    /**
     * Start time.
     */
    private long startTime_ = 0;
    
    /**
     * Tracks memory usage while the test is running.
     */
    private MemoryWatcherThread memoryWatcher_;
    
    /**
     * Reports on the results are sent here.
     */
    private PrintWriter writer_;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Creates a 1-tps database (1 branch, 10 tellers) and runs one TPC BM B 
     * transaction. Example command line:
     * <pre>
     * java DBBench -driver org.hsqldb.jdbcDriver -url jdbc:hsqldb:/hsql/test33 -user sa -clients 20
     * </pre>
     * 
     * @param args See above. 
     */
    public static void main(String[] args) 
    {
        //DriverManager.setLogWriter(new PrintWriter(new OutputStreamWriter(System.out)));
        
        String  driver   = "";
        String  url      = "";
        String  user     = "";
        String  password = "";
        boolean initDB   = false;

        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-clients"))
            {
                if (i + 1 < args.length)
                {
                    i++;
                    numClients_ = Integer.parseInt(args[i]);
                }
            }
            else if (args[i].equals("-driver"))
            {
                if (i + 1 < args.length)
                {
                    i++;
                    driver = args[i];

                    if (driver.equals(
                        "org.enhydra.instantdb.jdbc.idbDriver"))
                        shutdownCommand_ = "SHUTDOWN";

                    if (driver.equals(
                        "com.borland.datastore.jdbc.DataStoreDriver"))
                    {
                    }

                    if (driver.equals("com.mckoi.JDBCDriver"))
                        shutdownCommand_ = "SHUTDOWN";

                    if (driver.equals("org.hsqldb.jdbcDriver"))
                    {
                        tableExtension_ = "CREATE CACHED TABLE ";
                        shutdownCommand_ = "SHUTDOWN COMPACT";
                    }
                }
            }
            else if (args[i].equals("-url"))
            {
                if (i + 1 < args.length)
                {
                    i++;
                    url = args[i];
                }
            }
            else if (args[i].equals("-user"))
            {
                if (i + 1 < args.length)
                {
                    i++;
                    user = args[i];
                }
            }
            else if (args[i].equals("-tabfile"))
            {
                if (i + 1 < args.length)
                {
                    i++;

                    try
                    {
                        tabFile_ = new PrintStream(
                            new FileOutputStream(args[i]));
                    }
                    catch (Exception e)
                    {
                        tabFile_ = new PrintStream(new NullOutputStream());
                    }
                }
            }
            else if (args[i].equals("-password"))
            {
                if (i + 1 < args.length)
                {
                    i++;
                    password = args[i];
                }
            }
            else if (args[i].equals("-tpc"))
            {
                if (i + 1 < args.length)
                {
                    i++;
                    numTxPerClient_ = Integer.parseInt(args[i]);
                }
            }
            else if (args[i].equals("-init"))
                initDB = true;
            else if (args[i].equals("-tps"))
            {
                if (i + 1 < args.length)
                {
                    i++;
                    tps_ = Integer.parseInt(args[i]);
                }
            }
            else if (args[i].equals("-v"))
                verbose_ = true;
        }

        if (StringUtils.isBlank(driver) || 
            StringUtils.isBlank(url)) 
        {
            System.out.println(
                "Usage: java DBBenchmark -driver [driver_class_name] " +
                "-url [url_to_db] -user [username] -password [password] [-v] " +
                "[-init] [-tpc n] [-clients]");
            
            System.out.println();
            System.out.println("-v       Verbose error messages");
            System.out.println("-init    Initialize the tables");
            System.out.println("-tpc     Transactions per client");
            System.out.println("-clients Number of simultaneous clients");
            System.exit(-1);
        }

        System.out.println(
            "*********************************************************");
        System.out.println(
            "* DBBenchmark v1.1                                        *");
        System.out.println(
            "*********************************************************");
        System.out.println();
        System.out.println("Driver      : " + driver);
        System.out.println("URL         : " + url);
        System.out.println();
        System.out.println("Scale factor: " + tps_);
        System.out.println("# Clients   : " + numClients_);
        System.out.println("# Txn/Client: " + numTxPerClient_);
        System.out.println();

        try
        {
            Class.forName(driver);

            DBBenchmark bench = 
                new DBBenchmark(url, user, password, initDB);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a DBBenchmark and sends output to System.out by default.
     * 
     * @param url JDBC url.
     * @param user Database username.
     * @param password Database password.
     * @param init True to init the tables, false otherwise.
     */
    public DBBenchmark(String url, String user, String password, boolean init)
    {
        this(url, user, password, init, 
            new PrintWriter(new OutputStreamWriter(System.out)));
    }   

    
    /**
     * Creates a DBBenchmark.
     * 
     * @param url JDBC url.
     * @param user Database username.
     * @param password Database password.
     * @param init True to init the tables, false otherwise.
     * @param writer Destination of benchmark output.
     */
    public DBBenchmark(
        String url, 
        String user, 
        String password, 
        boolean init,
        PrintWriter writer)
    {
        writer_ = writer;
        List clients = new ArrayList();
        Thread client = null;
        Iterator it = null;

        try
        {
            if (init)
            {
                writer_.println("Start: " + new java.util.Date());
                writer_.print("Initializing dataset...");
                createDatabase(url, user, password);
                writer_.println("done.\n");
                writer_.println("Complete: " + new java.util.Date());
                writer_.flush();
            }

            writer_.println("* Starting Benchmark Run *");
            memoryWatcher_ = new MemoryWatcherThread();
            memoryWatcher_.start();

            transactions_ = false;
            preparedStmt_ = false;
            startTime_ = System.currentTimeMillis();

            for (int i = 0; i < numClients_; i++)
            {
                client = new ClientThread(numTxPerClient_, url, user, password);
                client.start();
                clients.add(client);
            }
            
            // Barrier to complete this test session
            
            it = clients.iterator();

            while (it.hasNext())
            {
                client = (Thread) it.next();
                client.join();
            }

            clients.clear();
            reportDone();

            transactions_ = true;
            preparedStmt_ = false;
            startTime_ = System.currentTimeMillis();

            for (int i = 0; i < numClients_; i++)
            {
                client = new ClientThread(numTxPerClient_, url, user, password);
                client.start();
                clients.add(client);
            }

            // Barrier to complete this test session
            
            it = clients.iterator();

            while (it.hasNext())
            {
                client = (Thread) it.next();
                client.join();
            }

            clients.clear();
            reportDone();

            transactions_ = false;
            preparedStmt_ = true;
            startTime_ = System.currentTimeMillis();

            for (int i = 0; i < numClients_; i++)
            {
                client = new ClientThread(numTxPerClient_, url, user, password);
                client.start();
                clients.add(client);
            }

            // Barrier to complete this test session
            
            it = clients.iterator();

            while (it.hasNext())
            {
                client = (Thread) it.next();
                client.join();
            }

            clients.clear();
            reportDone();

            transactions_ = true;
            preparedStmt_ = true;
            startTime_ = System.currentTimeMillis();

            for (int i = 0; i < numClients_; i++)
            {
                client = new ClientThread(numTxPerClient_, url, user, password);
                client.start();
                clients.add(client);
            }

            // Barrier to complete this test session
            
            it = clients.iterator();

            while (it.hasNext())
            {
                client = (Thread) it.next();
                client.join();
            }

            clients.clear();
            reportDone();
        }
        catch (Exception ex)
        {
            writer_.println(ex.getMessage());
            ex.printStackTrace(writer_);
        }
        finally
        {
            memoryWatcher_.end();

            try
            {
                memoryWatcher_.join();

                if (shutdownCommand_.length() > 0)
                {
                    Connection conn = connect(url, user, password);
                    Statement stmt = conn.createStatement();
                    stmt.execute(shutdownCommand_);
                    stmt.close();
                    connectClose(conn);
                }

                if (tabFile_ != null)
                    tabFile_.close();
            }
            catch (Exception ex2)
            {
            }

            writer_.flush();
        }
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Report done. 
     */
    protected void reportDone()
    {
        long endTime = System.currentTimeMillis();
        double completionTime = ((double) endTime - (double) startTime_) / 1000;

        tabFile_.print(tps_ + ";" + numClients_ + ";" + numTxPerClient_ + ";");

        //writer_.println("\n* Benchmark Report *");
        //writer_.print("* Featuring ");

        writer_.println();
        writer_.print("Benchmark: ");
        
        if (preparedStmt_)
        {
            writer_.print("Prepared Statements ");
            tabFile_.print("<prepared statements>;");
        }
        else
        {
            writer_.print("Direct Queries ");
            tabFile_.print("<direct queries>;");
        }

        if (transactions_)
        {
            writer_.print("+ Transactions");
            tabFile_.print("<transactions>;");
        }
        else
        {
            writer_.print("+ Auto-commit");
            tabFile_.print("<auto-commit>;");
        }

        writer_.println("\n================================================");
        
        writer_.println(
            //"Time for " + txCount_ + " transactions: " +
            "Time        " +
            DecimalFormat.getNumberInstance().format(completionTime) + 
            " sec(s)");

        writer_.println(
            "Passed      " +
            (txCount_ - failedTx_) + "/" + txCount_);

        double rate = (txCount_ - failedTx_) / completionTime;
        
        writer_.println(
            "Throughput  " + 
            DecimalFormat.getNumberInstance().format(rate) + 
            " transactions/sec.");
        
        writer_.println(
            "Min memory  " + 
            DecimalFormat.getIntegerInstance().format(memoryWatcher_.min_) +
            " KB");
        
        writer_.println(
            "Max memory  " + 
            DecimalFormat.getIntegerInstance().format(memoryWatcher_.max_) +
            " KB");
        
        //    + " / " + memoryWatcher_.min + " kb");
        

        tabFile_.print(memoryWatcher_.max_ + ";" + memoryWatcher_.min_ + ";" + 
            failedTx_ + ";" + rate + "\n");

        txCount_ = 0;
        failedTx_ = 0;
        memoryWatcher_.reset();
        writer_.flush();
    }

    
    /**
     * Bump up tx count.
     */
    protected synchronized void incrementTransactionCount()
    {
        txCount_++;
    }


    /**
     * Bump up failed tx count.
     */
    protected synchronized void incrementFailedTransactionCount()
    {
        failedTx_++;
    }

    
    /**
     * Creates the test database.
     * 
     * @param url JDBC url.
     * @param user Username.
     * @param password Password.
     * @throws Exception on error.
     */
    protected void createDatabase(String url, String user, String password)
        throws Exception
    {
        Connection conn = connect(url, user, password);
        writer_.println(conn.getMetaData().getDatabaseProductName());
        transactions_ = true;

        if (transactions_)
        {
            try
            {
                conn.setAutoCommit(false);
                writer_.println("[In transaction mode]");
            }
            catch (SQLException se)
            {
                transactions_ = false;
            }
        }
        
        writer_.flush();
        
        try
        {
            int accountsnb = 0;
            Statement stmt = conn.createStatement();
            String query;

            query = "SELECT count(*) FROM accounts";
            ResultSet rs = stmt.executeQuery(query);
            stmt.clearWarnings();

            while (rs.next())
                accountsnb = rs.getInt(1);

            if (transactions_)
                conn.commit();

            stmt.close();

            if (accountsnb == (numAccounts_ * tps_))
            {
                writer_.println("Tables already initialized");
                connectClose(conn);
                return;
            }
        }
        catch (Exception ex)
        {
        }

        writer_.println("Dropping old tables if they exist...");
        writer_.flush();
        
        try
        {
            Statement stmt = conn.createStatement();
            String query;

            query = "DROP TABLE history";
            stmt.execute(query);
            stmt.clearWarnings();

            query = "DROP TABLE accounts";
            stmt.execute(query);
            stmt.clearWarnings();

            query = "DROP TABLE tellers";
            stmt.execute(query);
            stmt.clearWarnings();

            query = "DROP TABLE branches";
            stmt.execute(query);
            stmt.clearWarnings();

            if (transactions_)
                conn.commit();

            stmt.close();
        }
        catch (Exception ex2)
        {
        }

        writer_.println("Creating tables...");
        writer_.flush();
        
        try
        {
            Statement stmt = conn.createStatement();
            String query;

            if (tableExtension_.length() > 0)
                query = tableExtension_ + " branches (";
            else
                query = "CREATE TABLE branches (";

            query += "Bid         INTEGER NOT NULL PRIMARY KEY, ";
            query += "Bbalance    INTEGER,";
            query += "filler      CHAR(88))"; // pad to 100 bytes

            if (createExtension_.length() > 0)
                query += createExtension_;

            stmt.execute(query);
            stmt.clearWarnings();

            if (tableExtension_.length() > 0)
                query = tableExtension_ + " tellers (";
            else
                query = "CREATE TABLE tellers (";

            query += "Tid         INTEGER NOT NULL PRIMARY KEY,";
            query += "Bid         INTEGER,";
            query += "Tbalance    INTEGER,";
            query += "filler      CHAR(84))"; // pad to 100 bytes 

            if (createExtension_.length() > 0)
                query += createExtension_;

            stmt.execute(query);
            stmt.clearWarnings();

            if (tableExtension_.length() > 0)
                query = tableExtension_ + " accounts (";
            else
                query = "CREATE TABLE accounts (";

            query += "Aid         INTEGER NOT NULL PRIMARY KEY, ";
            query += "Bid         INTEGER, ";
            query += "Abalance    INTEGER, ";
            query += "filler      CHAR(84))"; // pad to 100 bytes 

            if (createExtension_.length() > 0)
                query += createExtension_;

            stmt.execute(query);
            stmt.clearWarnings();

            if (tableExtension_.length() > 0)
                query = tableExtension_ + " history (";
            else
                query = "CREATE TABLE history (";

            query += "Tid         INTEGER, ";
            query += "Bid         INTEGER, ";
            query += "Aid         INTEGER, ";
            query += "delta       INTEGER, ";
            query += "tstime        TIMESTAMP, ";
            query += "filler      CHAR(22))"; // pad to 50 bytes 

            if (createExtension_.length() > 0)
                query += createExtension_;

            stmt.execute(query);
            stmt.clearWarnings();

            if (transactions_)
                conn.commit();

            stmt.close();
        }
        catch (Exception ex2)
        {
        }

        writer_.println("Deleting table contents in case Drop didn't work...");
        writer_.flush();
        
        try
        {
            Statement stmt = conn.createStatement();
            String query;

            query = "DELETE FROM history";
            stmt.execute(query);
            stmt.clearWarnings();

            query = "DELETE FROM accounts";
            stmt.execute(query);
            stmt.clearWarnings();

            query = "DELETE FROM tellers";
            stmt.execute(query);
            stmt.clearWarnings();

            query = "DELETE FROM branches";
            stmt.execute(query);
            stmt.clearWarnings();

            if (transactions_)
                conn.commit();

            /*
             * prime database using TPC BM B scaling rules. Note that for each
             * branch and teller: 
             * branch_id = teller_id/ntellers 
             * branch_id = account_id/naccounts
             */
            PreparedStatement pstmt = null;

            preparedStmt_ = true;

            if (preparedStmt_)
            {
                try
                {
                    query = "INSERT INTO branches(Bid,Bbalance) VALUES (?,0)";
                    pstmt = conn.prepareStatement(query);
                    writer_.println("[Using prepared statements]");
                }
                catch (SQLException se)
                {
                    pstmt = null;
                    preparedStmt_ = false;
                }
            }

            writer_.println("Populating branches table...");
            writer_.flush();
            
            for (int i = 0; i < numBranches_ * tps_; i++)
            {
                if (preparedStmt_)
                {
                    pstmt.setInt(1, i);
                    pstmt.executeUpdate();
                    pstmt.clearWarnings();
                }
                else
                {
                    query = "INSERT INTO branches (Bid,Bbalance) " +
                            "VALUES (" + i + ",0)";

                    stmt.executeUpdate(query);
                }

                if ((i % 100 == 0) && (transactions_))
                    conn.commit();
            }

            if (preparedStmt_)
                pstmt.close();

            if (transactions_)
                conn.commit();

            if (preparedStmt_)
            {
                query = "INSERT INTO tellers(Tid,Bid,Tbalance) VALUES (?,?,0)";
                pstmt = conn.prepareStatement(query);
            }

            writer_.println("Populating tellers table...");
            writer_.flush();
            
            for (int i = 0; i < numTellers_ * tps_; i++)
            {
                if (preparedStmt_)
                {
                    pstmt.setInt(1, i);
                    pstmt.setInt(2, i / numTellers_);
                    pstmt.executeUpdate();
                    pstmt.clearWarnings();
                }
                else
                {
                    query = "INSERT INTO tellers(Tid,Bid,Tbalance) VALUES ("
                        + i + "," + i / numTellers_ + ",0)";

                    stmt.executeUpdate(query);
                }

                if ((i % 100 == 0) && (transactions_))
                    conn.commit();
            }

            if (preparedStmt_)
                pstmt.close();

            if (transactions_)
                conn.commit();

            if (preparedStmt_)
            {
                query = "INSERT INTO accounts(Aid,Bid,Abalance) VALUES (?,?,0)";
                pstmt = conn.prepareStatement(query);
            }

            writer_.println("Populating accounts table...");
            writer_.flush();
            
            for (int i = 0; i < numAccounts_ * tps_; i++)
            {
                if (preparedStmt_)
                {
                    pstmt.setInt(1, i);
                    pstmt.setInt(2, i / numAccounts_);
                    pstmt.executeUpdate();
                    pstmt.clearWarnings();
                }
                else
                {
                    query = "INSERT INTO accounts(Aid,Bid,Abalance) VALUES ("
                        + i + "," + i / numAccounts_ + ",0)";

                    stmt.executeUpdate(query);
                }

                if ((i % 10000 == 0) && (transactions_))
                    conn.commit();

                if ((i > 0) && ((i % 10000) == 0))
                    writer_.println("\t" + i + "\t records inserted");
            }

            if (preparedStmt_)
                pstmt.close();

            if (transactions_)
                conn.commit();

            writer_.println(
                "\t" + (numAccounts_ * tps_) + "\t records inserted");
            
            stmt.close();
        }
        catch (Exception ex)
        {
            writer_.println(ex.getMessage());
            ex.printStackTrace(writer_);
        }

        writer_.flush();
        connectClose(conn);
    }
 
    
    /**
     * Returns a random ID.
     * 
     * @param type ACCOUNT, TELLER, or BRANCH
     * @return int
     */
    protected static int getRandomID(int type)
    {
        int min;
        int max;
        int num;

        max = min = 0;
        num = numAccounts_;

        switch (type)
        {
            case TELLER:
                min += numBranches_;
                num = numTellers_;

            // FALLTHROUGH
            case BRANCH:
                if (type == BRANCH)
                {
                    num = numBranches_;
                }

                min += numAccounts_;

            // FALLTHROUGH
            case ACCOUNT:
                max = min + num - 1;
        }

        return (RandomUtil.nextInt(min, max));
    }

    
    /**
     * Gets a connection.
     *  
     * @param url JDBC url
     * @param user Username
     * @param password Password
     * @return Connection
     */
    protected static Connection connect(String url, String user, String password)
    {
        Connection conn = null;
        
        try
        {
            conn = DriverManager.getConnection(url, user, password);
        }
        catch (Exception ex)
        {
            logger_.error(ex);
        }

        return conn;
    }

    
    /**
     * Closes a connection.
     * 
     * @param c Connection to close
     */
    protected static void connectClose(Connection c)
    {
        if (c == null)
            return;

        try
        {
            c.close();
        }
        catch (Exception ex)
        {
            logger_.error(ex);
        }
    }

    //--------------------------------------------------------------------------
    // ClientThread
    //--------------------------------------------------------------------------
    
    class ClientThread extends Thread
    {
        //----------------------------------------------------------------------
        // Fields
        //----------------------------------------------------------------------
        
        private int numTx_ = 0;
        private Connection conn_;
        private PreparedStatement pstmt1_ = null;
        private PreparedStatement pstmt2_ = null;
        private PreparedStatement pstmt3_ = null;
        private PreparedStatement pstmt4_ = null;
        private PreparedStatement pstmt5_ = null;

        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------
        
        /**
         * Creates a ClientThread.
         * 
         * @param numOfTx Number of transactions.
         * @param url JDBC url.
         * @param user Username.
         * @param password Password.
         */
        public ClientThread(
            int numOfTx, 
            String url, 
            String user,
            String password)
        {
            numTx_ = numOfTx;
            conn_ = connect(url, user, password);

            if (conn_ == null)
                return;

            try
            {
                if (transactions_)
                    conn_.setAutoCommit(false);

                if (preparedStmt_)
                {
                    String Query;

                    Query = "UPDATE accounts ";
                    Query += "SET     Abalance = Abalance + ? ";
                    Query += "WHERE   Aid = ?";
                    pstmt1_ = conn_.prepareStatement(Query);
                    Query = "SELECT Abalance ";
                    Query += "FROM   accounts ";
                    Query += "WHERE  Aid = ?";
                    pstmt2_ = conn_.prepareStatement(Query);
                    Query = "UPDATE tellers ";
                    Query += "SET    Tbalance = Tbalance + ? ";
                    Query += "WHERE  Tid = ?";
                    pstmt3_ = conn_.prepareStatement(Query);
                    Query = "UPDATE branches ";
                    Query += "SET    Bbalance = Bbalance + ? ";
                    Query += "WHERE  Bid = ?";
                    pstmt4_ = conn_.prepareStatement(Query);
                    Query = "INSERT INTO history(Tid, Bid, Aid, delta) ";
                    Query += "VALUES (?,?,?,?)";
                    pstmt5_ = conn_.prepareStatement(Query);
                }
            }
            catch (Exception ex)
            {
                writer_.println(ex.getMessage());
                ex.printStackTrace(writer_);
            }
        }

        //----------------------------------------------------------------------
        // Runnable Interface
        //----------------------------------------------------------------------
        
        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            while (numTx_-- > 0)
            {
                int account = getRandomID(ACCOUNT);
                int branch = getRandomID(BRANCH);
                int teller = getRandomID(TELLER);
                int delta = RandomUtil.nextInt(0, 1000);

                doOne(branch, teller, account, delta);
                incrementTransactionCount();
            }

            if (preparedStmt_)
            {
                try
                {
                    if (pstmt1_ != null)
                        pstmt1_.close();

                    if (pstmt2_ != null)
                        pstmt2_.close();

                    if (pstmt3_ != null)
                        pstmt3_.close();

                    if (pstmt4_ != null)
                        pstmt4_.close();

                    if (pstmt5_ != null)
                        pstmt5_.close();
                }
                catch (Exception ex)
                {
                    writer_.println(ex.getMessage());
                    ex.printStackTrace(writer_);
                }
            }

            connectClose(conn_);
            conn_ = null;
        }

        //----------------------------------------------------------------------
        // Protected
        //----------------------------------------------------------------------
        
        /**
         * Executes a single TPC BM B transaction.
         *
         * @param bid B id
         * @param tid t id
         * @param aid A id
         * @param delta Delta 
         * @return int
         */
        protected int doOne(int bid, int tid, int aid, int delta)
        {
            int balance = 0;

            if (conn_ == null)
            {
                incrementFailedTransactionCount();
                return 0;
            }

            try
            {
                if (preparedStmt_)
                {
                    pstmt1_.setInt(1, delta);
                    pstmt1_.setInt(2, aid);
                    pstmt1_.executeUpdate();
                    pstmt1_.clearWarnings();
                    pstmt2_.setInt(1, aid);

                    ResultSet rs = pstmt2_.executeQuery();

                    pstmt2_.clearWarnings();

                    while (rs.next())
                        balance = rs.getInt(1);

                    pstmt3_.setInt(1, delta);
                    pstmt3_.setInt(2, tid);
                    pstmt3_.executeUpdate();
                    pstmt3_.clearWarnings();
                    pstmt4_.setInt(1, delta);
                    pstmt4_.setInt(2, bid);
                    pstmt4_.executeUpdate();
                    pstmt4_.clearWarnings();
                    pstmt5_.setInt(1, tid);
                    pstmt5_.setInt(2, bid);
                    pstmt5_.setInt(3, aid);
                    pstmt5_.setInt(4, delta);
                    pstmt5_.executeUpdate();
                    pstmt5_.clearWarnings();
                }
                else
                {
                    Statement stmt = conn_.createStatement();
                    String query = "UPDATE accounts ";

                    query += "SET     Abalance = Abalance + " + delta + " ";
                    query += "WHERE   Aid = " + aid;

                    int res = stmt.executeUpdate(query);

                    stmt.clearWarnings();

                    query = "SELECT Abalance ";
                    query += "FROM   accounts ";
                    query += "WHERE  Aid = " + aid;

                    ResultSet rs = stmt.executeQuery(query);

                    stmt.clearWarnings();

                    while (rs.next())
                        balance = rs.getInt(1);

                    query = "UPDATE tellers ";
                    query += "SET    Tbalance = Tbalance + " + delta + " ";
                    query += "WHERE  Tid = " + tid;

                    stmt.executeUpdate(query);
                    stmt.clearWarnings();

                    query = "UPDATE branches ";
                    query += "SET    Bbalance = Bbalance + " + delta + " ";
                    query += "WHERE  Bid = " + bid;

                    stmt.executeUpdate(query);
                    stmt.clearWarnings();

                    query = "INSERT INTO history(Tid, Bid, Aid, delta) ";
                    query += "VALUES (";
                    query += tid + ",";
                    query += bid + ",";
                    query += aid + ",";
                    query += delta + ")";

                    stmt.executeUpdate(query);
                    stmt.clearWarnings();
                    stmt.close();
                }

                if (transactions_)
                    conn_.commit();

                return balance;
            }
            catch (Exception ex)
            {
                if (verbose_)
                {
                    writer_.println("Transaction failed: " + ex.getMessage());
                    ex.printStackTrace(writer_);
                }

                incrementFailedTransactionCount();

                if (transactions_)
                {
                    try
                    {
                        conn_.rollback();
                    }
                    catch (SQLException E1)
                    {
                    }
                }
            }

            return 0;
        }
    }
    
    //--------------------------------------------------------------------------
    // MemoryWatcherThread
    //--------------------------------------------------------------------------
    
    class MemoryWatcherThread extends Thread
    {
        private long min_ = 0;
        private long max_ = 0;
        private boolean keepRunning_ = true;


        public MemoryWatcherThread()
        {
            reset();
            keepRunning_ = true;
        }


        public void reset()
        {
            System.gc();
            long currentFree = Runtime.getRuntime().freeMemory();
            long currentAlloc = Runtime.getRuntime().totalMemory();
            min_ = max_ = (currentAlloc - currentFree);
        }


        public void end()
        {
            keepRunning_ = false;
        }


        public void run()
        {
            while (keepRunning_)
            {
                long currentFree = Runtime.getRuntime().freeMemory();
                long currentAlloc = Runtime.getRuntime().totalMemory();
                long used = currentAlloc - currentFree;

                if (used < min_)
                    min_ = used;

                if (used > max_)
                    max_ = used;

                try
                {
                    sleep(100);
                }
                catch (InterruptedException E)
                {
                }
            }
        }
    }
}

/* Copyright (c) 2001-2002, The HSQL Development Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the HSQL Development Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL HSQL DEVELOPMENT GROUP, HSQLDB.ORG, 
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
