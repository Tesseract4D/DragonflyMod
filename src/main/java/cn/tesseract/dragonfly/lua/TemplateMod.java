package cn.tesseract.dragonfly.lua;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "template")
public class TemplateMod {
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        LuaBridge.callLuaEvent(e);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        LuaBridge.callLuaEvent(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        LuaBridge.callLuaEvent(e);
    }
}
