package me.jsinco.oneannouncer;

import me.jsinco.oneannouncer.commands.Announce;
import me.jsinco.oneannouncer.commands.Say;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class OneAnnouncer extends JavaPlugin implements CommandExecutor {

    private static OneAnnouncer instance;
    private static JDA jda;
    public boolean setupJDA() throws InterruptedException {

        String botToken = OneAnnouncer.plugin().getConfig().getString("bot-token");
        if (botToken == null || botToken.equals("YOUR_BOT_TOKEN")) {
            this.getLogger().info("Bot token is invalid or not set. Not enabling discord features.");
            return false;
        }

        jda = JDABuilder.createDefault(botToken)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new JDAListeners())
                .build().awaitReady();

        TextChannel channel = jda.getTextChannelById(getConfig().getString("announce-cmd.default-channel-id"));
        if (channel == null) {
            this.getLogger().warning("Default channel is invalid. Cannot invoke /announce command.");
            return true;
        }

        channel.getGuild().upsertCommand("announce", "Announce a message to Minecraft")
                .addOption(OptionType.STRING, "msg", "The message to announce", true)
                .queue();
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        reloadConfig();
        if (jda != null) {
            jda.shutdownNow();
        }
        try {
            if (setupJDA()) {
                this.getLogger().info("Discord features enabled.");
            } else {
                this.getLogger().info("Discord features not enabled.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        instance = this;

        try {
            if (setupJDA()) {
                this.getLogger().info("Discord features enabled.");
            } else {
                this.getLogger().info("Discord features not enabled.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Say();
        new AutoAnnouncer();
        new Announce();
        getCommand("onereload").setExecutor(this);
    }

    @Override
    public void onDisable() {
        if (jda != null) {
            jda.shutdownNow();
        }
    }

    public static OneAnnouncer plugin() {
        return instance;
    }

    public static JDA getJDA() {
        return jda;
    }
}
