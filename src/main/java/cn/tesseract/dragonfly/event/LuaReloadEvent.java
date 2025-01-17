package cn.tesseract.dragonfly.event;

public class LuaReloadEvent {
    public final boolean isStartup;

    public LuaReloadEvent(boolean isStartup) {
        this.isStartup = isStartup;
    }
}
