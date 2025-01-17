package cn.tesseract.dragonfly.hook;

import cn.tesseract.mycelium.asm.Hook;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ModDiscoverer;

import java.util.List;

public class DragonflyHook {
    @Hook(injectOnExit = true)
    public static void identifyMods(ModDiscoverer c, @Hook.ReturnValue List<ModContainer> mods) {
    }
}
