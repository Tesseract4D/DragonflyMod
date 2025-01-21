package cn.tesseract.dragonfly;

import cn.tesseract.dragonfly.command.CommandReload;
import cn.tesseract.dragonfly.lua.LuaBridge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "dragonfly", acceptedMinecraftVersions = "[1.7.10]", version = Tags.VERSION)
public class Dragonfly {
    public static final Logger logger = LogManager.getLogger("Lua");

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        LuaBridge.callLuaEvent(e);
    }

    @Mod.EventHandler
    public void server(FMLServerStartingEvent e) {
        e.registerServerCommand(new CommandReload());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        LuaBridge.callLuaEvent(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        LuaBridge.callLuaEvent( e);
    }
}
