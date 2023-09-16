package me.jsinco.oneannouncer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Util {

    static public final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";

    /**
     * @param text The string of text to apply color/effects to
     * @return Returns a string of text with color/effects applied
     */
    public static String colorcode(String text) {

        String[] texts = text.split(String.format(WITH_DELIMITER, "&"));

        StringBuilder finalText = new StringBuilder();

        for (int i = 0; i < texts.length; i++) {
            if (texts[i].equalsIgnoreCase("&")) {
                //get the next string
                i++;
                if (texts[i].charAt(0) == '#') {
                    finalText.append(net.md_5.bungee.api.ChatColor.of(texts[i].substring(0, 7)) + texts[i].substring(7));
                } else {
                    finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]));
                }
            } else {
                finalText.append(texts[i]);
            }
        }

        return finalText.toString();
    }


    /**
     *
     * @param configSection The config section to get the placeholders from
     * @param msg The message to add the placeholders to
     * @return Returns a string of text with placeholders applied
     */

    public static String addPlaceholders(String configSection, String msg) {
        List<String> placeholders = List.copyOf(OneAnnouncer.plugin().getConfig().getConfigurationSection(configSection).getKeys(false));
        for (String placeholder : placeholders) {
            if (msg.contains("$" + placeholder)) {
                msg = msg.replace("$" + placeholder, OneAnnouncer.plugin().getConfig().getString(configSection + "." + placeholder));
            }
        }
        return msg;
    }

    /**
     * @param dispatcher If not null, the player to execute commands from
     * @param msg The message to execute commands from
     * @return Returns a string of text with commands removed and executed
     */

    public static String executeStringCommands(@Nullable Player dispatcher, @NotNull String msg) {
        if (msg.contains("<CMD>")) {
            String command = msg.substring(msg.indexOf("<CMD>") + 5, msg.indexOf("</CMD>"));
            msg = msg.substring(0, msg.indexOf("<CMD>")) + msg.substring(msg.indexOf("</CMD>") + 6);
            Bukkit.dispatchCommand(Objects.requireNonNullElseGet(dispatcher, Bukkit::getConsoleSender), command);
        }
        return msg;
    }

    /**
     * Helper method to check if a message contains a channel
     * @param msg Check to see if the message contains a channel
     * @return Returns true if the message contains a channel
     */

    public static boolean checkForChannelInString(String msg) {
        return msg.contains("<CHANNEL:");
    }

    /**
     *
     * @param msg The message to get the channel from
     * @return Returns a map of the channel and the message
     */

    public static Map<String, String> getChannelFromString(String msg) {
        if (msg.contains("<CHANNEL:")) {
            String channel = msg.substring(msg.indexOf("<CHANNEL:") + 9, msg.indexOf(">"));
            msg = msg.substring(0, msg.indexOf("<CHANNEL:")) + msg.substring(msg.indexOf(">") + 1);
            return Map.of("channel", channel, "msg", msg);
        }
        return null;
    }

    /**
     * Helper method to check if a message contains a customizable default prefix
     * @param msg Check to see if the message contains a customizable default prefix
     * @return Returns true if the message contains a customizable default prefix
     */

    public static boolean checkForPrefixInString(String msg) {
        return msg.contains("<P>");
    }

    /**
     *
     * @param msg The message to get the prefix from
     * @return Returns a map of the prefix and the message
     */

    public static Map<String, String> getPrefixFromString(String msg) {
        if (msg.contains("<P>")) {
            String prefix = msg.substring(msg.indexOf("<P>") + 3, msg.indexOf("</P>"));
            msg = msg.substring(0, msg.indexOf("<P>")) + msg.substring(msg.indexOf("</P>") + 4);
            return Map.of("prefix", prefix, "msg", msg);
        }
        return null;
    }

}
