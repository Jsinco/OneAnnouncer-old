package me.jsinco.oneannouncer.discord;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import me.jsinco.oneannouncer.OneAnnouncer;
import me.jsinco.oneannouncer.Util;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class JDAListeners extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMember() == null) return;
        else if (!event.getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) return;

        List<String> listenFor = OneAnnouncer.plugin().getConfig().getStringList("relay.listen-for");
        String message = event.getMessage().getContentRaw();
        for (String word : listenFor) {
            if (!message.contains(word)) continue;

            String prefix = OneAnnouncer.plugin().getConfig().getString("relay.default-prefix");
            if (Util.checkForPrefixInString(message)) {
                Map<String, String> prefixMap = Util.getPrefixFromString(message);
                prefix = prefixMap.get("prefix");
                message = prefixMap.get("msg");
            }

            String[] splitMsg = message.split(" ");
            if (splitMsg.length > OneAnnouncer.plugin().getConfig().getInt("relay.max-relay-length")) {
                Bukkit.broadcastMessage(IridiumColorAPI.process(Util.colorcode(prefix + OneAnnouncer.plugin().getConfig().getString("relay.replacement-message"))));
            } else {
                Bukkit.broadcastMessage(IridiumColorAPI.process(Util.colorcode(prefix + message)));
            }
            break;
        }
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) return;

        if (event.getName().equals("announce")) {
            OptionMapping option = event.getOption("msg");
            if (option == null) {
                event.reply("Provide a message to send").setEphemeral(true).queue();
                return;
            }
            String msg = option.getAsString();
            msg = Util.addPlaceholders("announce-cmd.placeholders", msg);

            String prefix = OneAnnouncer.plugin().getConfig().getString("announce-cmd.default-prefix");
            if (Util.checkForPrefixInString(msg)) {
                Map<String, String> prefixMap = Util.getPrefixFromString(msg);
                prefix = prefixMap.get("prefix");
                msg = prefixMap.get("msg");
            }

            Bukkit.broadcastMessage(IridiumColorAPI.process(Util.colorcode(prefix + msg)));
            event.reply("Announced message").setEphemeral(true).queue();
        }
    }
}
