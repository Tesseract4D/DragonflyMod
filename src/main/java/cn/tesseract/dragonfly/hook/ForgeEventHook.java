package cn.tesseract.dragonfly.hook;

import cn.tesseract.dragonfly.lua.LuaBridge;
import cn.tesseract.mycelium.asm.Hook;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLEvent;
import cpw.mods.fml.common.event.FMLStateEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventBus;

public class ForgeEventHook {
    @Hook
    public static void post(EventBus c, Event event) {
        LuaBridge.callLuaEvent(event);
    }

    @Hook(injectOnExit = true)
    public static void sendEventToModContainer(LoadController c, FMLEvent stateEvent, ModContainer mc) {
        if (stateEvent instanceof FMLStateEvent event)
            LuaBridge.callLuaEvent(mc.getModId() + ":" + event.getModState().toString(), stateEvent);
    }
}
