package cn.tesseract.dragonfly.lua;

import net.minecraft.launchwrapper.IClassTransformer;

public class LuaHookTransformer implements IClassTransformer {
    public static final String luaHookClass = "cn.tesseract.dragonfly.hook.LuaHook";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (name.equals(luaHookClass))
            return LuaHookClassVisitor.visit();
        return basicClass;
    }
}
