package cn.tesseract.dragonfly.lua;

import cn.tesseract.dragonfly.DragonflyCoreMod;
import org.luaj.vm2.lib.jse.JseBaseLib;

import java.io.*;

public class DragonflyBaseLib extends JseBaseLib {
    @Override
    public InputStream findResource(String filename) {
        File f = new File(DragonflyCoreMod.scriptDir, filename + ".lua");
        if (!f.exists())
            return super.findResource(filename);
        try {
            return new BufferedInputStream(new FileInputStream(f));
        } catch (IOException ioe) {
            return null;
        }
    }
}
