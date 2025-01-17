package cn.tesseract.dragonfly.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;

public class LuaHookRegistry {
    public static void registerLuaEvent(String eventType, LuaValue fn) {
        ArrayList<LuaValue> list = LuaBridge.eventList.computeIfAbsent(eventType, k -> new ArrayList<>());
        list.add(fn);
    }

    public static void registerLuaHook(String name, LuaValue fn, LuaTable obj) {
        if (name.startsWith("__"))
            throw new IllegalArgumentException();
        LuaBridge.registerLuaHook(name, fn, obj);
    }
}
