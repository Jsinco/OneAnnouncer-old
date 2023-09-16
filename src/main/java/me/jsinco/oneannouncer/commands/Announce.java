package me.jsinco.oneannouncer.commands;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import me.jsinco.oneannouncer.discord.JDAMethods;
import me.jsinco.oneannouncer.OneAnnouncer;
import me.jsinco.oneannouncer.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Map;

public class Announce implements CommandExecutor  {

    public Announce() {
        PluginCommand announcecmd = OneAnnouncer.plugin().getCommand("announce");
        announcecmd.setExecutor(this);
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            OneAnnouncer.plugin().getConfig().getStringList("announce-cmd.aliases").forEach(alias -> {
                commandMap.register(alias, "oneannouncer", announcecmd);
            });
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        String msg = String.join(" ", strings);
        Player playerSender = sender instanceof Player ? (Player) sender : null;
        msg = Util.executeStringCommands(playerSender, Util.addPlaceholders("announce-cmd.placeholders", msg));

        String channel = OneAnnouncer.plugin().getConfig().getString("announce-cmd.default-channel-id");

        if (Util.checkForChannelInString(msg)) {
            Map<String, String> channelMap = Util.getChannelFromString(msg);
            channel = channelMap.get("channel");
            msg = channelMap.get("msg");
        }

        if (channel == null) {
            sender.sendMessage("Channel is invalid. Check config's default-channel-id or specify a real channel with <CHANNEL:channel-id> in your message.");
        } else {
            JDAMethods.sendMessageDiscordChannel(channel, msg);
            sender.sendMessage(IridiumColorAPI.process(Util.colorcode(OneAnnouncer.plugin().getConfig().getString("announce-cmd.default-prefix") + "Announced message")));
        }

        return true;
    }
}
