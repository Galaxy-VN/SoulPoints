package net.danh.diemsinhmenh;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import net.danh.diemsinhmenh.commands.commands;
import net.danh.diemsinhmenh.event.data;
import net.danh.diemsinhmenh.event.death;
import net.danh.diemsinhmenh.hook.placeholder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {
    public static net.danh.diemsinhmenh.event.data data;

    private void createConfig() {
        try {
            if (!this.getDataFolder().exists()) {
                this.getDataFolder().mkdirs();
            }

            File file = new File(this.getDataFolder(), "config.yml");
            if (!file.exists()) {
                this.getLogger().info("Config.yml not found, creating!");
                this.getConfig().options().copyDefaults(true);
                this.saveDefaultConfig();
            } else {
                this.getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public int getLives(Player p) {
        return data.getConfig().getInt("Lives." + p.getUniqueId() + ".life");
    }

    public void setLives(Player p, int number) {
        data.getConfig().set("Lives." + p.getUniqueId() + ".life", number);
        data.save();
    }

    public void addLives(Player p, int number) {
        data.getConfig().set("Lives." + p.getUniqueId() + ".life", this.getLives(p) + number);
        data.save();
    }

    public void removeLives(Player p, int number) {
        data.getConfig().set("Lives." + p.getUniqueId() + ".life", this.getLives(p) - number);
        data.save();
    }




    public void onLoad() {
        this.saveDefaultConfig();
    }

    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new placeholder(this).register();
        }
        Metrics metrics = new Metrics(this, 12918);
        getCommand("souls").setExecutor(new commands(this));
        Bukkit.getServer().getPluginManager().registerEvents(new death(this), this);
        FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        this.saveConfig();
        data = new data(new File(this.getDataFolder() + "/data.yml"));
        data.getConfig().options().copyDefaults(true);
        data.save();
        int configVersion = this.getConfig().contains("config-version", true) ? this.getConfig().getInt("config-version") : -1;
        int defConfigVersion = this.getConfig().getDefaults().getInt("config-version");
        if (configVersion != defConfigVersion) {
            this.getLogger().warning("You may be using an outdated config.yml!");
            this.getLogger().warning("(Your config version: '" + configVersion + "' | Expected config version: '" + defConfigVersion + "')");
        }
        (new BukkitRunnable() {
            public void run() {
                Iterator var2 = Bukkit.getOnlinePlayers().iterator();

                while (var2.hasNext()) {
                    Player p = (Player) var2.next();
                    if (!p.hasPermission("souls.use")) {
                        return;
                    }

                    List<String> w = Main.this.getConfig().getStringList("available-worlds");
                    if (w.contains(p.getWorld().getName())) {
                        if (!Main.data.getConfig().contains("Lives." + p.getUniqueId())) {
                            Main.this.addLives(p, Main.this.getConfig().getInt("General.First_join"));

                        }

                        if (!getConfig().getBoolean("ActionBar.Enable")) {
                            return;
                        }
                        if (getConfig().getBoolean("ActionBar.Enable")) {
                            p.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    new TextComponent(Main.this.convert(Main.this.getConfig().getString("lang." + getConfig().getString("language") + "." + "Soul-message")).replace("%souls%", String.valueOf(getLives(p)))));
                        }
                    }

                }
            }
        }).runTaskTimer(this, 20L, 20L);
        (new BukkitRunnable() {
            public void run() {
                Iterator var2 = Bukkit.getOnlinePlayers().iterator();

                while (var2.hasNext()) {
                    Player p = (Player) var2.next();
                    int sie = Main.this.getLives(p);
                    if (!p.hasPermission("souls.use")) {
                        return;
                    }

                    if (Main.this.getConfig().getInt("General.Maximum-souls") <= sie) {
                        return;
                    }

                    List<String> w = Main.this.getConfig().getStringList("available-worlds");
                    if (w.contains(p.getWorld().getName())) {
                        Main.this.addLives(p, getConfig().getInt("General.Daily-souls"));
                        p.sendMessage(Main.this.convert(Main.this.getConfig().getString("lang." + getConfig().getString("language") + "." + "Soul-earn-message")).replace("%souls%", getConfig().getString("General.Daily-souls")));
                    }
                }

            }
        }).runTaskTimer(this, (long) (this.getConfig().getInt("General.Souls-regenerate-duration") * 20), (long) (this.getConfig().getInt("General.Souls-regenerate-duration") * 20));
    }

    public String convert(String s) {
        return s.replace("&", "§");
    }



    @Override
    public void onDisable() {
        this.getLogger().info("Saving data....");
        this.saveConfig();
        this.data.save();
    }
}