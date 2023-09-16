package me.jsinco.oneannouncer.discord;

import me.jsinco.oneannouncer.OneAnnouncer;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class JDAMethods {
    public static void sendMessageDiscordUser(String userID, String msg) {
        OneAnnouncer.getJDA().retrieveUserById(userID).queue(user -> {
            user.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage(msg))
                    .queue();
        });
    }

    public static void sendMessageDiscordChannel(String channelID, String msg) {
        TextChannel channel = OneAnnouncer.getJDA().getTextChannelById(channelID);
        if (channel != null) {
            channel.sendMessage(msg).queue();
        }
    }
}
