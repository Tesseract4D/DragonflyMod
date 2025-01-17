package cn.tesseract.dragonfly;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import cpw.mods.fml.common.asm.transformers.AccessTransformer;

import java.io.File;
import java.io.IOException;

public class DragonflyAccessTransformer extends AccessTransformer {
    public DragonflyAccessTransformer() throws IOException {
        File[] files = DragonflyCoreMod.scriptDir.listFiles();
        if (files != null)
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith("_at.cfg")) {
                    processATFile(Resources.asCharSource(file.toURI().toURL(), Charsets.UTF_8));
                    Dragonfly.logger.info("Loaded rules from AccessTransformer config file \"" + file.getName() + "\"");
                }
            }
    }
}
