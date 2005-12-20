package toolbox.dbconsole.util;

import java.sql.SQLException;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;

/**
 * Decorator for a database operation that prints out the amount of time used
 * to execute the operation.
 */
public class TimedOperation extends DatabaseOperation {

    private final Log logger = LogFactory.getLog(TimedOperation.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Operation to decorate
     */
    private DatabaseOperation op_;

    /**
     * Name of this operation. Used to identify the op in logger output.
     */
    private String name_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a TimedOperation.
     *
     * @param name Name of this operation.
     * @param op Operation to time.
     */
    public TimedOperation(String name, DatabaseOperation op) {
        this.name_ = name;
        this.op_ = op;
    }

    //--------------------------------------------------------------------------
    // DatabaseOperation Interface
    //--------------------------------------------------------------------------

    /*
     * Slaps a timer around the execution of the operation and sends output to
     * the debug logger.
     *
     * @see org.dbunit.operation.DatabaseOperation#execute(org.dbunit.database.IDatabaseConnection, org.dbunit.dataset.IDataSet)
     */
    public void execute(IDatabaseConnection connection, IDataSet dataSet)
        throws DatabaseUnitException, SQLException {

        logger.debug("Operation " + name_ + " executing...");

        StopWatch watch = new StopWatch();
        watch.start();

        op_.execute(connection, dataSet);

        watch.stop();
        logger.debug("Operation " + name_ + " executed in " + watch);
    }
}