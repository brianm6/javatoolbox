package toolbox.util.ui.plaf;

/**
 * LookAndFeelActivator
 */
public interface LookAndFeelActivator
{
    void setLookAndFeelInfo(LAFInfo lookAndFeelInfo);
    LAFInfo getLookAndFeelInfo();
    void activate() throws Exception;
}
