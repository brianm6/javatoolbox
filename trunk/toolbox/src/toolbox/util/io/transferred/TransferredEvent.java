package toolbox.util.io.transferred;

import java.util.Date;

import toolbox.util.io.MonitoredChannel;

public class TransferredEvent 
{
	private MonitoredChannel source_;
    private long delta_;
    private long total_;
    private Date timestamp_;
}
