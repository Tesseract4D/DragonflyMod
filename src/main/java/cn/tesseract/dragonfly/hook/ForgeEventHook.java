package cn.tesseract.dragonfly.hook;

import cn.tesseract.dragonfly.lua.LuaBridge;
import cn.tesseract.mycelium.asm.Hook;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventBus;

public class ForgeEventHook {
    @Hook
    public static void post(EventBus c, Event event) {
        LuaBridge.callLuaEvent(event);
    }
}
