package cn.tesseract.dragonfly.command;

import cn.tesseract.dragonfly.DragonflyCoreMod;
import cn.tesseract.dragonfly.lua.ModPacker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

import java.io.File;

public class CommandPack extends CommandBase {
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return MinecraftServer.getServer().isSinglePlayer() || super.canCommandSenderUseCommand(sender);
    }

    public String getCommandName() {
        return "pack";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    public String getCommandUsage(ICommandSender sender) {
        return "commands.pack.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 2) {
            ModPacker.generateModFile(args[0], args[1], new File(DragonflyCoreMod.scriptDir, "mod.jar"));
            sender.addChatMessage(new ChatComponentTranslation("commands.pack.success"));
        } else {
            sender.addChatMessage(new ChatComponentTranslation("commands.pack.failed"));
        }
    }
}
