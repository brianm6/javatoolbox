package toolbox.util.ui.plugin;

import java.net.URL;

import com.werken.classworlds.ClassRealm;
import com.werken.classworlds.ClassWorld;

public class ClassWorldsWorkspace
{
    public static void main(String[] args)
    {
        new ClassWorldsWorkspace().launch();
    }
    
    public void launch()
    {
        try
        {
            ClassWorld world = new ClassWorld();
            ClassRealm toolbox = world.newRealm("toolbox");
            toolbox.addConstituent(new URL("file:../lib/fop.jar"));
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
