package cn.tesseract.dragonfly.lua;

import cn.tesseract.dragonfly.DragonflyCoreMod;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.luaj.vm2.Globals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class LuaCoreMod implements IFMLLoadingPlugin {
    private final Globals globals;

    public LuaCoreMod(String modid) {
        globals = DragonflyCoreMod.getLuaGlobals(modid);
        String assetsPath = "/assets/" + modid + "/";
        String[] mainScripts = getTextFromAssets(assetsPath + "scriptList.txt").split("\n");
        for (String script : mainScripts)
            globals.loadfile(script).call();
    }

    private String getTextFromAssets(String path) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path), StandardCharsets.UTF_8))) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (sb.length() != 0) sb.append('\n');
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
