package toolbox.workspace;

import java.util.Map;

import javax.swing.JComponent;

public interface IPluginPresenter
{
    public void startup(Map props);
    public void addPlugin(IPlugin plugin);
    public void removePlugin(IPlugin plugin);
    public void shutdown();
    public JComponent getComponent();
}
