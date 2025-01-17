package cn.tesseract.dragonfly;

import cn.tesseract.dragonfly.event.LuaReloadEvent;
import cn.tesseract.dragonfly.hook.DragonflyHook;
import cn.tesseract.dragonfly.hook.ForgeEventHook;
import cn.tesseract.dragonfly.lua.DragonflyBaseLib;
import cn.tesseract.dragonfly.lua.LuaBridge;
import cn.tesseract.dragonfly.lua.LuaHookRegistry;
import cn.tesseract.dragonfly.lua.LuaHookTransformer;
import cn.tesseract.mycelium.asm.minecraft.HookLoader;
import net.minecraft.launchwrapper.Launch;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DragonflyCoreMod extends HookLoader {
    public static Globals globals;
    public static String phase = "coremod";
    public static File scriptDir;

    static {
        scriptDir = new File(Launch.minecraftHome, "lua");
        scriptDir.mkdir();
    }

    public static Globals getLuaGlobals() {
        if (globals == null) {
            globals = new Globals();

            globals.load(new DragonflyBaseLib());
            globals.load(new PackageLib());
            globals.load(new Bit32Lib());
            globals.load(new TableLib());
            globals.load(new JseStringLib());
            globals.load(new CoroutineLib());
            globals.load(new JseMathLib());
            globals.load(new JseIoLib());
            globals.load(new JseOsLib());
            globals.load(new LuajavaLib());
            LoadState.install(globals);
            LuaC.install(globals);

            globals.set("import", new VarArgFunction() {
                @Override
                public Varargs invoke(Varargs args) {
                    String className = args.arg1().tojstring();
                    try {
                        globals.set(className.substring(className.lastIndexOf('.') + 1), CoerceJavaToLua.coerce(Class.forName(className)));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    return NONE;
                }
            });
            globals.set("log", new VarArgFunction() {
                @Override
                public Varargs invoke(Varargs args) {
                    Dragonfly.logger.info(args.arg1().tostring());
                    return NONE;
                }
            });
            globals.set("registerLuaEvent", new VarArgFunction() {
                @Override
                public Varargs invoke(Varargs args) {
                    LuaHookRegistry.registerLuaEvent(args.arg1().tojstring(), args.arg(2));
                    return NONE;
                }
            });
            globals.set("registerLuaHook", new VarArgFunction() {
                @Override
                public Varargs invoke(Varargs args) {
                    LuaHookRegistry.registerLuaHook(args.arg1().tojstring(), args.arg(2), args.arg(3).checktable());
                    return NONE;
                }
            });
        }
        return globals;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{LuaHookTransformer.class.getName()};
    }

    @Override
    public String getAccessTransformerClass() {
        return DragonflyAccessTransformer.class.getName();
    }

    @Override
    protected void registerHooks() {
        phase = "hook";
        try {
            File file = new File(scriptDir, "main.lua");
            if (file.exists()) {
                LuaValue chunk = getLuaGlobals().load(new FileReader(file), file.getName());
                chunk.call();
                LuaBridge.callLuaEvent(new LuaReloadEvent(true));
            }
            Class.forName(LuaHookTransformer.luaHookClass);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (!LuaBridge.eventList.isEmpty())
            registerHookContainer(ForgeEventHook.class.getName());
        registerHookContainer(DragonflyHook.class.getName());
    }
}
