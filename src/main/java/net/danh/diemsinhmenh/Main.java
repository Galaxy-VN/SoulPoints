package net.danh.diemsinhmenh;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

import net.danh.diemsinhmenh.commands.commands;
import net.danh.diemsinhmenh.event.death;
import net.danh.diemsinhmenh.hook.placeholder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {


    @Override
    public void onEnable() {

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new placeholder(this).register();
        }
        Metrics metrics = new Metrics(this, 12918);
        getCommand("souls").setExecutor(new commands(this));
        getServer().getPluginManager().registerEvents(new death(this), this);
        createConfigs();
        (new BukkitRunnable() {
            public void run() {
                Iterator var2 = Bukkit.getOnlinePlayers().iterator();

                while (var2.hasNext()) {
                    Player p = (Player) var2.next();
                    if (!p.hasPermission("souls.use")) {
                        return;
                    }

                    List<String> w = getConfig().getStringList("available-worlds");
                    if (w.contains(p.getWorld().getName())) {
                        if (!getdata().contains("Lives." + p.getUniqueId())) {
                            addLives(p, getConfig().getInt("General.First_join"));

                        }

                        if (!getConfig().getBoolean("ActionBar.Enable")) {
                            return;
                        }
                        if (getConfig().getBoolean("ActionBar.Enable")) {
                            p.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    new TextComponent(convert(getlang().getString("lang." + getConfig().getString("language") + "." + "Soul-message")).replaceAll("%souls%", String.valueOf(getLives(p)))));
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
                    int sie = getLives(p);
                    if (!p.hasPermission("souls.use")) {
                        return;
                    }

                    if (getConfig().getInt("General.Maximum-souls") <= sie) {
                        return;
                    }

                    List<String> w = getConfig().getStringList("available-worlds");
                    if (w.contains(p.getWorld().getName())) {
                        addLives(p, getConfig().getInt("General.Daily-souls"));
                        p.sendMessage(convert(getConfig().getString("lang." + getConfig().getString("language") + "." + "Soul-earn-message")).replaceAll("%souls%", getConfig().getString("General.Daily-souls")));
                    }
                }

            }
        }).runTaskTimer(this, (long) (this.getConfig().getInt("General.Souls-regenerate-duration") * 20), (long) (this.getConfig().getInt("General.Souls-regenerate-duration") * 20));
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Saving data....");
        save();
    }

    private File configFile, langFile, dataFile;
    private FileConfiguration config, lang, data;


    public void createConfigs() {
        configFile = new File(getDataFolder(), "config.yml");
        langFile = new File(getDataFolder(), "lang.yml");
        dataFile = new File(getDataFolder(), "data.yml");

        if (!configFile.exists()) saveResource("config.yml", false);
        if (!langFile.exists()) saveResource("lang.yml", false);
        if (!dataFile.exists()) saveResource("data.yml", false);

        config = new YamlConfiguration();
        lang = new YamlConfiguration();
        data = new YamlConfiguration();

        try {
            config.load(configFile);
            lang.load(langFile);
            data.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getlang() {
        return lang;
    }

    public FileConfiguration getdata() {
        return data;
    }

    public void reloadConfigs() {
        config = YamlConfiguration.loadConfiguration(configFile);
        lang = YamlConfiguration.loadConfiguration(langFile);
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void save() {
        try {
            data.save(dataFile);
        } catch (IOException ignored) {
        }
    }

    public int getLives(Player p) {
        return getdata().getInt("Lives." + p.getUniqueId() + ".life");
    }

    public void setLives(Player p, int number) {
        getdata().set("Lives." + p.getUniqueId() + ".life", number);
        save();
    }

    public void addLives(Player p, int number) {
        getdata().set("Lives." + p.getUniqueId() + ".life", this.getLives(p) + number);
        save();
    }

    public void removeLives(Player p, int number) {
        getdata().set("Lives." + p.getUniqueId() + ".life", this.getLives(p) - number);
        save();
    }



    public String convert(String s) {
        return s.replaceAll("&", "§");
    }

}
