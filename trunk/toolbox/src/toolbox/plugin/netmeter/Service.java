package toolbox.plugin.netmeter;

/**
 * LifeCycle Interface 
 */
public interface Service
{
    public void start() throws ServiceException;
    public void stop() throws ServiceException;
    public void pause() throws ServiceException;
    public void resume() throws ServiceException;
    public boolean isRunning();
    public boolean isPaused();
}
