package cn.tesseract.dragonfly.lua;

import org.luaj.vm2.lib.jse.JseBaseLib;

import java.io.InputStream;

public class ModBaseLib extends JseBaseLib {
    final String path;

    public ModBaseLib(String path) {
        this.path = path;
    }

    @Override
    public InputStream findResource(String filename) {
        InputStream is = getClass().getResourceAsStream(path + filename + ".lua");
        return is == null ? super.findResource(filename) : is;
    }
}
