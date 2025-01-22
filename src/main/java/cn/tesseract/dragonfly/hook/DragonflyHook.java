package cn.tesseract.dragonfly.hook;

import cn.tesseract.mycelium.asm.Hook;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.asm.transformers.ModAccessTransformer;
import cpw.mods.fml.common.discovery.ModCandidate;

import java.util.Map;
import java.util.jar.JarFile;

public class DragonflyHook {
    @Hook(targetMethod = "<init>", injectOnExit = true)
    public static void init(FMLModContainer c, String className, ModCandidate container, Map<String, Object> modDescriptor) {
    }

    @Hook
    public static void addJar(ModAccessTransformer c, JarFile jar) {
        System.out.println("&" + jar);
    }
}
