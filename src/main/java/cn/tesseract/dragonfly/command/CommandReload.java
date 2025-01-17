package cn.tesseract.dragonfly.command;

import cn.tesseract.dragonfly.event.LuaReloadEvent;
import cn.tesseract.dragonfly.lua.LuaBridge;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

public class CommandReload extends CommandBase {
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return MinecraftServer.getServer().isSinglePlayer() || super.canCommandSenderUseCommand(sender);
    }

    public String getCommandName() {
        return "reload";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    public String getCommandUsage(ICommandSender sender) {
        return "commands.reload.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) {
        LuaBridge.onReload();
        LuaBridge.callLuaEvent(new LuaReloadEvent(false));
        sender.addChatMessage(new ChatComponentTranslation("commands.reload.success"));
    }
}