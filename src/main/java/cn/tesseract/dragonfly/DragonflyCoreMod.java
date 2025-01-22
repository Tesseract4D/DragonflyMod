package cn.tesseract.dragonfly;

import cn.tesseract.dragonfly.event.LuaReloadEvent;
import cn.tesseract.dragonfly.hook.DragonflyHook;
import cn.tesseract.dragonfly.hook.ForgeEventHook;
import cn.tesseract.dragonfly.lua.*;
import cn.tesseract.mycelium.asm.minecraft.HookLoader;
import cpw.mods.fml.common.LoaderState;
import net.minecraft.launchwrapper.Launch;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DragonflyCoreMod extends HookLoader {
    public static Map<String, Globals> globals = new HashMap<>();
    public static File scriptDir;

    static {
        scriptDir = new File(Launch.minecraftHome, "lua");
        scriptDir.mkdir();
    }

    public static Globals getLuaGlobals(String modid) {
        Globals g;
        if ((g = globals.get(modid)) != null) return g;

        final Globals f = new Globals();
        globals.put(modid, f);

        f.load(modid.equals("dragonfly") ? new JseBaseLib() {
            @Override
            public InputStream findResource(String filename) {
                File f = new File(scriptDir, filename + ".lua");
                if (!f.exists())
                    return super.findResource(filename);
                try {
                    return new BufferedInputStream(new FileInputStream(f));
                } catch (IOException ioe) {
                    return null;
                }
            }
        } : new ModBaseLib("/assets/" + modid + "/scripts/"));
        f.load(new PackageLib());
        f.load(new Bit32Lib());
        f.load(new TableLib());
        f.load(new JseStringLib());
        f.load(new CoroutineLib());
        f.load(new JseMathLib());
        f.load(new JseIoLib());
        f.load(new JseOsLib());
        f.load(new LuajavaLib());
        LoadState.install(f);
        LuaC.install(f);

        f.set("import", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String className = arg.tojstring();
                try {
                    f.set(className.substring(className.lastIndexOf('.') + 1), CoerceJavaToLua.coerce(Class.forName(className)));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                return NONE;
            }
        });
        f.set("importAs", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                try {
                    return CoerceJavaToLua.coerce(Class.forName(arg.tojstring()));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        f.set("log", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Dragonfly.logger.info(arg.tostring());
                return NONE;
            }
        });
        f.set("registerLuaEvent", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                LuaHookRegistry.registerLuaEvent(arg1.tojstring(), arg2);
                return NONE;
            }
        });
        f.set("registerLuaHook", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                LuaHookRegistry.registerLuaHook(modid + ":" + args.arg1().tojstring(), args.arg(2), args.arg(3).checktable());
                return NONE;
            }
        });
        f.set("preInitEvent", LuaString.valueOf(modid + ":" + LoaderState.ModState.PREINITIALIZED));
        f.set("initEvent", LuaString.valueOf(modid + ":" + LoaderState.ModState.INITIALIZED));
        f.set("postInitEvent", LuaString.valueOf(modid + ":" + LoaderState.ModState.POSTINITIALIZED));
        return f;
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
        ModPacker.generateModFile("cn.tesseract.skeleton", "nerf", new File(Launch.minecraftHome, "mod.jar"));
        try {
            File[] files = scriptDir.listFiles();
            if (files != null) for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".lua")) {
                    LuaValue chunk = getLuaGlobals("dragonfly").load(new FileReader(file), file.getName());
                    chunk.call();
                }
            }
            LuaBridge.callLuaEvent("reload", new LuaReloadEvent(true));
            Class.forName(LuaHookTransformer.luaHookClass);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (!LuaBridge.eventList.isEmpty()) registerHookContainer(ForgeEventHook.class.getName());
        registerHookContainer(DragonflyHook.class.getName());
    }
}
