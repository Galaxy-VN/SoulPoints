package net.danh.diemsinhmenh;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class placeholder extends PlaceholderExpansion {

    private final Main plugin;

    public placeholder(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
        return "sp";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }


    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if (p == null)
            return "Player not online";

        switch (identifier) {
            case "live":
                return String.valueOf(plugin.getLives(p));
        }

        return null;
    }
}