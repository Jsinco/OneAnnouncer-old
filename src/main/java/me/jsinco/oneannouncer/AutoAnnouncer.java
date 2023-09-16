package me.jsinco.oneannouncer;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class AutoAnnouncer {

    private static int announcerTask;

    public AutoAnnouncer() {
        loadAnnouncer();
    }
    // TODO: Add indexing of messages or allow them to go in specific orders

    private boolean loadAnnouncer() {
        if (!OneAnnouncer.plugin().getConfig().getBoolean("auto-announcer.enabled")){
            Bukkit.getScheduler().cancelTask(announcerTask);
            return false;
        }

        announcerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(OneAnnouncer.plugin(), () -> {
            List<String> msgs = OneAnnouncer.plugin().getConfig().getStringList("auto-announcer.messages");
            String msg = msgs.get(new Random().nextInt(msgs.size()));
            msg = Util.executeStringCommands(null, Util.addPlaceholders("auto-announcer.placeholders", msg));

            Bukkit.broadcastMessage(IridiumColorAPI.process(Util.colorcode(msg)));
        }, 0L, OneAnnouncer.plugin().getConfig().getLong("auto-announcer.interval") * 20L);
        return true;
    }
}
