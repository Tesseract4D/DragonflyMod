package cn.tesseract.dragonfly;

import cn.tesseract.dragonfly.command.CommandPack;
import cn.tesseract.dragonfly.command.CommandReload;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "dragonfly", acceptedMinecraftVersions = "[1.7.10]", version = Tags.VERSION)
public class Dragonfly {
    public static final Logger logger = LogManager.getLogger("Lua");

    @Mod.EventHandler
    public void server(FMLServerStartingEvent e) {
        e.registerServerCommand(new CommandReload());
        e.registerServerCommand(new CommandPack());
    }
}
