package net.danh.diemsinhmenh;

import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.GRAY + "/souls " + ChatColor.WHITE + "To view help page for ops.");
                    sender.sendMessage(ChatColor.GRAY + "/souls add <player> <amount> " + ChatColor.WHITE + "Add souls for a player.");
                    sender.sendMessage(ChatColor.GRAY + "/souls remove <player> <amount> " + ChatColor.WHITE + "Remove souls for a player.");
                    sender.sendMessage(ChatColor.GRAY + "/souls check <player> " + ChatColor.WHITE + "Check souls for a player.");
                    sender.sendMessage(ChatColor.GRAY + "/souls reload " + ChatColor.WHITE + "To reload config.");
                }
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (!sender.hasPermission("souls.admin")) {
                        sender.sendMessage(this.convert(this.getConfig().getString("soul-message")).replaceAll("%souls%", String.valueOf(this.getLives(player))));
                        sender.sendMessage("");
                        sender.sendMessage(ChatColor.GRAY + "/souls check <player> " + ChatColor.WHITE + "Check souls for a player.");
                    }
                    if (sender.hasPermission("souls.admin")) {
                        sender.sendMessage("");
                        sender.sendMessage(ChatColor.GRAY + "/souls " + ChatColor.WHITE + "To view help page for ops.");
                        sender.sendMessage(ChatColor.GRAY + "/souls add <player> <amount> " + ChatColor.WHITE + "Add souls for a player.");
                        sender.sendMessage(ChatColor.GRAY + "/souls remove <player> <amount> " + ChatColor.WHITE + "Remove souls for a player.");
                        sender.sendMessage(ChatColor.GRAY + "/souls check <player>" + ChatColor.WHITE + "Check souls for a player.");
                        sender.sendMessage(ChatColor.GRAY + "/souls reload " + ChatColor.WHITE + "To reload config.");
                    }
                }
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof ConsoleCommandSender) {
                    Main.this.reloadConfig();
                    sender.sendMessage(this.convert(this.getConfig().getString("reload")));
                }
                if (sender instanceof Player) {

                    Player player = (Player) sender;

                    if (!sender.hasPermission("souls.admin")) {
                        sender.sendMessage(this.convert(this.getConfig().getString("soul-message")).replaceAll("%souls%", String.valueOf(this.getLives(player))));
                    }

                    if (sender.hasPermission("souls.admin")) {
                        Main.this.reloadConfig();
                        sender.sendMessage(this.convert(this.getConfig().getString("reload")));
                    }
                }
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("check")) {
                if (sender instanceof ConsoleCommandSender) {
                    if (Bukkit.getPlayer(args[1]) == null) {
                        sender.sendMessage(this.convert(this.getConfig().getString("not-online")));
                        return true;
                    }

                    sender.sendMessage(this.convert(this.getConfig().getString("check-message")).replaceAll("%souls%",  String.valueOf(this.getLives(Bukkit.getPlayer(args[1])))).replaceAll("%player%", Bukkit.getPlayer(args[1]).getName()));
                }
                if (sender instanceof Player) {
                    if (Bukkit.getPlayer(args[1]) == null) {
                        sender.sendMessage(this.convert(this.getConfig().getString("not-online")));
                        return true;
                    }

                    sender.sendMessage(this.convert(this.getConfig().getString("check-message")).replaceAll("%souls%",  String.valueOf(this.getLives(Bukkit.getPlayer(args[1])))).replaceAll("%player%", Bukkit.getPlayer(args[1]).getName()));
                }
            }

            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("add")) {
                    if (sender instanceof ConsoleCommandSender) {
                        if (Bukkit.getPlayer(args[1]) == null) {
                            sender.sendMessage(this.convert(this.getConfig().getString("not-online")));
                            return true;
                        }
                        this.addLives(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));
                        sender.sendMessage(this.convert(this.getConfig().getString("add-message")).replaceAll("%souls%",  String.valueOf(Integer.parseInt(args[2]))).replaceAll("%player%", Bukkit.getPlayer(args[1]).getName()));

                    }

                    if (sender instanceof Player) {

                        Player player = (Player) sender;

                        if (!sender.hasPermission("souls.admin")) {
                            sender.sendMessage(this.convert(this.getConfig().getString("soul-message")).replaceAll("%souls%", String.valueOf(this.getLives(player))));
                        }

                        if (sender.hasPermission("souls.admin")) {
                            this.addLives(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));
                            sender.sendMessage(this.convert(this.getConfig().getString("add-message")).replaceAll("%souls%",  String.valueOf(Integer.parseInt(args[2]))).replaceAll("%player%", Bukkit.getPlayer(args[1]).getName()));
                        }
                    }
                }

                if (args[0].equalsIgnoreCase("remove")) {
                    if (sender instanceof ConsoleCommandSender) {
                        if (Bukkit.getPlayer(args[1]) == null) {
                            sender.sendMessage(this.convert(this.getConfig().getString("not-online")));
                            return true;
                        }
                        if (this.getLives(Bukkit.getPlayer(args[1])) < Integer.parseInt(args[2])) {
                            sender.sendMessage(this.convert(this.getConfig().getString("enough")));
                            return true;
                        }

                        this.removeLives(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));
                        sender.sendMessage(ChatColor.GREEN + "[Souls] Đã lấy " + Integer.parseInt(args[2]) + " Souls từ " + Bukkit.getPlayer(args[1]).getName() + "!");
                        sender.sendMessage(this.convert(this.getConfig().getString("take-message")).replaceAll("%souls%",  String.valueOf(Integer.parseInt(args[2]))).replaceAll("%player%", Bukkit.getPlayer(args[1]).getName()));
                    }

                    if (sender instanceof Player) {

                        Player player = (Player) sender;
                        if (!sender.hasPermission("souls.admin")) {
                            sender.sendMessage(this.convert(this.getConfig().getString("soul-message")).replaceAll("%souls%", String.valueOf(this.getLives(player))));
                        }
                        if (sender.hasPermission("souls.admin")) {
                            if (Bukkit.getPlayer(args[1]) == null) {
                                sender.sendMessage(this.convert(this.getConfig().getString("not-online")));
                                return true;
                            }
                            if (this.getLives(Bukkit.getPlayer(args[1])) < Integer.parseInt(args[2])) {
                                sender.sendMessage(this.convert(this.getConfig().getString("enough")));
                                return true;
                            }

                            this.removeLives(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));
                            sender.sendMessage(this.convert(this.getConfig().getString("take-message")).replaceAll("%souls%",  String.valueOf(Integer.parseInt(args[2]))).replaceAll("%player%", Bukkit.getPlayer(args[1]).getName()));
                        }
                    }
                }
            }
        }
        return true;
    }


    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new placeholder(this).register();
        }
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        this.createConfig();
        ArrayList<String> worlds = new ArrayList();
        worlds.add("world");
        worlds.add("world_nether");
        worlds.add("world_the_end");
        this.getConfig().addDefault("available-worlds", worlds);
        this.getConfig().addDefault("default-amount-souls", 5);
        this.getConfig().addDefault("daily-souls", 60);
        this.getConfig().addDefault("lose-soul-message-after", 3);
        this.getConfig().addDefault("soul-earn-message", "&d[Souls] &fYou have earned 1 Soul!");
        this.getConfig().addDefault("death-message", "&d[Souls] &fYou have lost 1 Soul, You have %souls% Souls Now!");
        this.getConfig().addDefault("death-message-inventory", "&4[Souls] &cYou have lost all of your souls, your inventory has been dropped!");
        this.getConfig().addDefault("take-message", "&4[Souls] &cRemove %souls% souls from %player%");
        this.getConfig().addDefault("add-message", "&4[Souls] &cAdd %souls% souls to %player%");
        this.getConfig().addDefault("check-message", "&4[Souls] &c%player% has %souls%");
        this.getConfig().addDefault("soul-message", "&d[Souls] &fYou have %souls% souls!");
        this.getConfig().addDefault("not-online", "&d[Souls] &cThat player is not online!");
        this.getConfig().addDefault("enough", "&d[Souls] &cToo much amount to remove!");
        this.getConfig().addDefault("reload", "&d[Souls] &cReload Config");
        this.getConfig().addDefault("config-version", 1);
        this.saveConfig();
        data = new data(new File(this.getDataFolder() + "/data/data.yml"));
        data.getConfig().options().copyDefaults(true);
        data.save();
        (new BukkitRunnable() {
            public void run() {
                Iterator var2 = Bukkit.getOnlinePlayers().iterator();

                while(var2.hasNext()) {
                    Player p = (Player)var2.next();
                    if (!p.hasPermission("souls.use")) {
                        return;
                    }

                    List<String> w = Main.this.getConfig().getStringList("available-worlds");
                    if (w.contains(p.getWorld().getName())) {
                        if (!Main.data.getConfig().contains("Lives." + p.getUniqueId())) {
                            Main.this.addLives(p, Main.this.getConfig().getInt("default-amount-souls"));
                        }
                    }
                }

            }
        }).runTaskTimer(this, 20L, 20L);
        (new BukkitRunnable() {
            public void run() {
                Iterator var2 = Bukkit.getOnlinePlayers().iterator();

                while(var2.hasNext()) {
                    Player p = (Player)var2.next();
                    int sie = Main.this.getLives(p);
                    if (!p.hasPermission("souls.use")) {
                        return;
                    }

                    if (Main.this.getConfig().getInt("default-amount-souls") <= sie) {
                        return;
                    }

                    List<String> w = Main.this.getConfig().getStringList("available-worlds");
                    if (w.contains(p.getWorld().getName())) {
                        Main.this.addLives(p, 1);
                        p.sendMessage(Main.this.convert(Main.this.getConfig().getString("soul-earn-message")));
                    }
                }

            }
        }).runTaskTimer(this, (long)(this.getConfig().getInt("daily-souls") * 20), (long)(this.getConfig().getInt("daily-souls") * 20));
    }

    public String convert(String s) {
        return s.replaceAll("&", "§");
    }

    @EventHandler
    public void death(PlayerDeathEvent e) {
        final Player p = e.getEntity();
        List<String> w = this.getConfig().getStringList("available-worlds");
        if (w.contains(p.getWorld().getName())) {
            if (p.hasPermission("souls.use")) {
                this.removeLives(p, 1);
                if (this.getLives(p) >= 1) {
                    (new BukkitRunnable() {
                        public void run() {
                            if (p.isOnline() && p != null) {
                                p.sendMessage(Main.this.convert(Main.this.getConfig().getString("death-message")).replaceAll("%souls%", String.valueOf(Main.this.getLives(p))));
                            }

                        }
                    }).runTaskLater(this, (long)(20 * this.getConfig().getInt("lose-soul-message-after")));
                }

                if (this.getLives(p) > 0) {
                    e.setKeepInventory(true);
                }

                if (this.getLives(p) <= 0) {
                    this.addLives(p, this.getConfig().getInt("default-amount-souls"));
                    e.setKeepInventory(false);
                    (new BukkitRunnable() {
                        public void run() {
                            if (p.isOnline() && p != null) {
                                p.sendMessage(Main.this.convert(Main.this.getConfig().getString("death-message-inventory")).replaceAll("%souls%", String.valueOf(Main.this.getLives(p))));
                            }

                        }
                    }).runTaskLater(this, (long)(20 * this.getConfig().getInt("lose-soul-message-after")));
                }

            }
        }
    }
}
