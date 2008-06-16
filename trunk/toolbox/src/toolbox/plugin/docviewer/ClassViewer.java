package toolbox.plugin.docviewer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.decompiler.Decompiler;
import toolbox.util.decompiler.DecompilerFactory;
import toolbox.util.io.StringInputStream;

/**
 * Java class file viewer that decompiles a <code>class</code> file and
 * displays the resulting source code in a syntax highlighting editor.
 */
public class ClassViewer extends JavaViewer {

    private static final Logger logger_ = Logger.getLogger(ClassViewer.class);

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ClassViewer() {
        super("Class Viewer");
    }

    // -------------------------------------------------------------------------
    // Overrides JavaViewer
    // -------------------------------------------------------------------------

    public void view(InputStream is) throws DocumentViewerException {
        try {
            byte[] bytecode = IOUtils.toByteArray(is);
            String name = FileUtil.createTempFilename();
            FileUtil.setFileContents(name, bytecode, false);
            File f = new File(name);
            view(f);
        }
        catch (IOException e) {
            throw new DocumentViewerException(e);
        }
    }


    public void view(File file) throws DocumentViewerException {
        Decompiler d = DecompilerFactory.createPreferred();
        String javacode = d.decompile(file);
        super.view(new StringInputStream(javacode));
    }


    public String[] getViewableFileTypes() {
        return new String[] { "class" };
    }
}