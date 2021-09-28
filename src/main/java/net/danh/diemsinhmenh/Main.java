package net.danh.diemsinhmenh;

import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {
    public static data data;

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

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("souls")) {
            if (args.length == 0) {
                if (sender instanceof ConsoleCommandSender) {

                    for (String helpadmin : getConfig().getStringList("Help-admin")) {
                        sender.sendMessage(helpadmin);
                    }
                }
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (!sender.hasPermission("souls.admin")) {
                        sender.sendMessage(this.convert(this.getConfig().getString("Message.Soul-message")).replaceAll("%souls%", String.valueOf(this.getLives(player))));
                        sender.sendMessage("");
                        sender.sendMessage(this.convert(this.getConfig().getString("Help-player")));
                    }
                    if (sender.hasPermission("souls.admin")) {
                        for (String helpadmin : getConfig().getStringList("Help-admin")) {
                            sender.sendMessage(helpadmin);
                        }
                    }
                }
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof ConsoleCommandSender) {
                    Main.this.reloadConfig();
                    sender.sendMessage(this.convert(this.getConfig().getString("Message.Reload")));
                }
                if (sender instanceof Player) {

                    Player player = (Player) sender;

                    if (!sender.hasPermission("souls.admin")) {
                        sender.sendMessage(this.convert(this.getConfig().getString("Message.Soul-message")).replaceAll("%souls%", String.valueOf(this.getLives(player))));
                    }

                    if (sender.hasPermission("souls.admin")) {
                        Main.this.reloadConfig();
                        sender.sendMessage(this.convert(this.getConfig().getString("Message.Reload")));
                    }
                }
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("check")) {
                if (sender instanceof ConsoleCommandSender) {
                    if (Bukkit.getPlayer(args[1]) == null) {
                        sender.sendMessage(this.convert(this.getConfig().getString("Message.Not-online")));
                        return true;
                    }

                    sender.sendMessage(this.convert(this.getConfig().getString("Message.Check-message")).replaceAll("%souls%", String.valueOf(this.getLives(Bukkit.getPlayer(args[1])))).replaceAll("%player%", Bukkit.getPlayer(args[1]).getName()));
                }
                if (sender instanceof Player) {
                    if (Bukkit.getPlayer(args[1]) == null) {
                        sender.sendMessage(this.convert(this.getConfig().getString("Message.Not-online")));
                        return true;
                    }

                    sender.sendMessage(this.convert(this.getConfig().getString("Message.Check-message")).replaceAll("%souls%", String.valueOf(this.getLives(Bukkit.getPlayer(args[1])))).replaceAll("%player%", Bukkit.getPlayer(args[1]).getName()));
                }
            }

            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("add")) {
                    if (sender instanceof ConsoleCommandSender) {
                        if (Bukkit.getPlayer(args[1]) == null) {
                            sender.sendMessage(this.convert(this.getConfig().getString("Message.Not-online")));
                            return true;
                        }
                        this.addLives(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));
                        sender.sendMessage(this.convert(this.getConfig().getString("Message.Add-message")).replaceAll("%souls%", String.valueOf(Integer.parseInt(args[2]))).replaceAll("%player%", Bukkit.getPlayer(args[1]).getName()));

                    }

                    if (sender instanceof Player) {

                        Player player = (Player) sender;

                        if (!sender.hasPermission("souls.admin")) {
                            sender.sendMessage(this.convert(this.getConfig().getString("Message.Soul-message")).replaceAll("%souls%", String.valueOf(this.getLives(player))));
                        }

                        if (sender.hasPermission("souls.admin")) {
                            this.addLives(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));
                            sender.sendMessage(this.convert(this.getConfig().getString("Message.Add-message")).replaceAll("%souls%", String.valueOf(Integer.parseInt(args[2]))).replaceAll("%player%", Bukkit.getPlayer(args[1]).getName()));
                        }
                    }
                }

                if (args[0].equalsIgnoreCase("remove")) {
                    if (sender instanceof ConsoleCommandSender) {
                        if (Bukkit.getPlayer(args[1]) == null) {
                            sender.sendMessage(this.convert(this.getConfig().getString("Message.Not-online")));
                            return true;
                        }
                        if (this.getLives(Bukkit.getPlayer(args[1])) < Integer.parseInt(args[2])) {
                            sender.sendMessage(this.convert(this.getConfig().getString("Message.Enough")));
                            return true;
                        }

                        this.removeLives(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));
                        sender.sendMessage(this.convert(this.getConfig().getString("Message.Take-message")).replaceAll("%souls%", String.valueOf(Integer.parseInt(args[2]))).replaceAll("%player%", Bukkit.getPlayer(args[1]).getName()));
                    }

                    if (sender instanceof Player) {

                        Player player = (Player) sender;
                        if (!sender.hasPermission("souls.admin")) {
                            sender.sendMessage(this.convert(this.getConfig().getString("Message.Soul-message")).replaceAll("%souls%", String.valueOf(this.getLives(player))));
                        }
                        if (sender.hasPermission("souls.admin")) {
                            if (Bukkit.getPlayer(args[1]) == null) {
                                sender.sendMessage(this.convert(this.getConfig().getString("Message.Not-online")));
                                return true;
                            }
                            if (this.getLives(Bukkit.getPlayer(args[1])) < Integer.parseInt(args[2])) {
                                sender.sendMessage(this.convert(this.getConfig().getString("Message.Enough")));
                                return true;
                            }

                            this.removeLives(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));
                            sender.sendMessage(this.convert(this.getConfig().getString("Message.Take-message")).replaceAll("%souls%", String.valueOf(Integer.parseInt(args[2]))).replaceAll("%player%", Bukkit.getPlayer(args[1]).getName()));
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("set")) {
                    if (sender instanceof ConsoleCommandSender) {
                        if (Bukkit.getPlayer(args[1]) == null) {
                            sender.sendMessage(this.convert(this.getConfig().getString("Message.Not-online")));
                            return true;
                        }
                        this.setLives(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));
                        sender.sendMessage(this.convert(this.getConfig().getString("Message.Set-message")).replaceAll("%souls%", String.valueOf(Integer.parseInt(args[2]))).replaceAll("%player%", Bukkit.getPlayer(args[1]).getName()));

                    }

                    if (sender instanceof Player) {

                        Player player = (Player) sender;

                        if (!sender.hasPermission("souls.admin")) {
                            sender.sendMessage(this.convert(this.getConfig().getString("Message.Soul-message")).replaceAll("%souls%", String.valueOf(this.getLives(player))));
                        }

                        if (sender.hasPermission("souls.admin")) {
                            this.setLives(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));
                            sender.sendMessage(this.convert(this.getConfig().getString("Message.Set-message")).replaceAll("%souls%", String.valueOf(Integer.parseInt(args[2]))).replaceAll("%player%", Bukkit.getPlayer(args[1]).getName()));
                        }
                    }
                }
            }
        }
        return true;
    }


    public void onLoad() {
        this.saveDefaultConfig();
    }

    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new placeholder(this).register();
        }
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
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
                            Main.this.addLives(p, Main.this.getConfig().getInt("General.Default-amount-souls"));

                        }

                        if (!getConfig().getBoolean("ActionBar.Enable")) {
                            return;
                        }
                        if (getConfig().getBoolean("ActionBar.Enable")) {
                            p.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    new TextComponent(Main.this.convert(Main.this.getConfig().getString("Message.Soul-message")).replaceAll("%souls%", String.valueOf(getLives(p)))));
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

                    if (Main.this.getConfig().getInt("General.Default-amount-souls") <= sie) {
                        return;
                    }

                    List<String> w = Main.this.getConfig().getStringList("available-worlds");
                    if (w.contains(p.getWorld().getName())) {
                        Main.this.addLives(p, 1);
                        p.sendMessage(Main.this.convert(Main.this.getConfig().getString("Message.Soul-earn-message")));
                    }
                }

            }
        }).runTaskTimer(this, (long) (this.getConfig().getInt("General.Daily-souls") * 20), (long) (this.getConfig().getInt("General.Daily-souls") * 20));
    }

    public String convert(String s) {
        return s.replaceAll("&", "§");
    }

    @EventHandler
    public void death(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Player k = p.getKiller();
        List<String> w = this.getConfig().getStringList("available-worlds");
        if (w.contains(p.getWorld().getName())) {
            if (p.hasPermission("souls.use")) {
                this.removeLives(p, this.getConfig().getInt("General.Death-souls"));
                if (!this.getConfig().getBoolean("PVP.Enable")) {
                    return;
                }
                if (this.getConfig().getBoolean("PVP.Enable")) {
                    if (k instanceof Player) {
                        this.addLives(k, this.getConfig().getInt("PVP.Kill-souls"));
                        k.sendMessage(Main.this.convert(Main.this.getConfig().getString("PVP.Kill-message")));
                    }
                }
                if (this.getLives(p) >= 1) {
                    (new BukkitRunnable() {
                        public void run() {
                            if (p.isOnline() && p != null) {
                                p.sendMessage(Main.this.convert(Main.this.getConfig().getString("Message.Death-message")).replaceAll("%souls%", String.valueOf(Main.this.getLives(p))));
                            }

                        }
                    }).runTaskLater(this, (long) (20 * this.getConfig().getInt("Message.Lose-soul-message-after")));
                }

                if (this.getLives(p) > 0) {
                    e.setKeepInventory(true);
                }

                if (this.getLives(p) <= 0) {
                    this.addLives(p, this.getConfig().getInt("General.Respawn_souls"));
                    e.setKeepInventory(false);
                    (new BukkitRunnable() {
                        public void run() {
                            if (p.isOnline() && p != null) {
                                p.sendMessage(Main.this.convert(Main.this.getConfig().getString("Message.Death-message-inventory")).replaceAll("%souls%", String.valueOf(Main.this.getLives(p))));
                            }

                        }
                    }).runTaskLater(this, (long) (20 * this.getConfig().getInt("General.Lose-soul-message-after")));
                }

            }
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Saving data....");
        this.saveConfig();
        this.data.save();
    }
}