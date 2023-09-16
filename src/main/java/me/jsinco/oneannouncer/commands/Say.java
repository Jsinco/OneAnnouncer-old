package me.jsinco.oneannouncer.commands;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import me.jsinco.oneannouncer.OneAnnouncer;
import me.jsinco.oneannouncer.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;

public class Say implements CommandExecutor {

    public Say() {
        PluginCommand saycmd = OneAnnouncer.plugin().getCommand("say");
        saycmd.setExecutor(this);
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            OneAnnouncer.plugin().getConfig().getStringList("say-cmd.aliases").forEach(alias -> {
                commandMap.register(alias, "oneannouncer", saycmd);
            });
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    // TODO: Add PAPI support with <ph>%placeholder%</ph>
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (strings.length < 1) return false;

        String senderName;
        if (sender instanceof ConsoleCommandSender) {
            senderName = OneAnnouncer.plugin().getConfig().getString("say-cmd.senders.console-prefix");
        } else if (sender instanceof BlockCommandSender) {
            senderName = OneAnnouncer.plugin().getConfig().getString("say-cmd.senders.command-block-prefix");
        } else {
            senderName = OneAnnouncer.plugin().getConfig().getString("say-cmd.senders.player-prefix");
            if (senderName != null) senderName = senderName.replace("$player", sender.getName());
        }

        String message = String.join(" ", strings);
        String prefix = OneAnnouncer.plugin().getConfig().getString("say-cmd.default-prefix");
        if (prefix == null) {
            Bukkit.broadcastMessage(IridiumColorAPI.process(Util.colorcode(message)));
            return true;
        }

        if (Util.checkForPrefixInString(message)) {
            Map<String, String> prefixMap = Util.getPrefixFromString(message);
            prefix = prefixMap.get("prefix");
            message = prefixMap.get("msg");
        }

        if (senderName != null) prefix = prefix.replace("$sender", senderName);

        Player playerSender = sender instanceof Player ? (Player) sender : null;
        message = Util.executeStringCommands(playerSender, Util.addPlaceholders("say-cmd.placeholders", message));
        Bukkit.broadcastMessage(IridiumColorAPI.process(Util.colorcode(prefix + message)));
        return true;
    }
}
