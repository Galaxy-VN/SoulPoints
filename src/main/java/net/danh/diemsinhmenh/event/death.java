package net.danh.diemsinhmenh.event;

import net.danh.diemsinhmenh.Main;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class death implements Listener {

    private Main main;


    public death(Main main) {
        this.main = main;
    }


    public int getRandomNumber(int min, int max) {
        Random r = new Random();
        int randomNumber = r.nextInt(max - min) + min;
        return randomNumber;
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        LivingEntity mob = e.getEntity();
        Player player = e.getEntity().getKiller();
        if (!main.getConfig().getBoolean("Mobs.Enable")) {
            return;
        }
        if (main.getConfig().getBoolean("Mobs.Enable")) {
            if (mob instanceof Animals) {
                return;
            }
            if (mob instanceof Monster) {
                if (player instanceof Player) {
                    double chance = Math.random() * 100.0D;
                    if (chance <= (main.getConfig().getInt("Mobs.Percent"))) {
                        main.addLives(player, main.getConfig().getInt("Mobs.Amount"));
                        player.sendMessage(main.convert(main.getlang().getString("lang." + main.getConfig().getString("language") + "." + "Kill-mobs-message")).replace("%souls%", main.getConfig().getString("Mobs.Amount")).replace("%mob%", mob.getName()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Player k = p.getKiller();
        List<String> w = main.getConfig().getStringList("available-worlds");
        if (w.contains(p.getWorld().getName())) {
            if (p.hasPermission("souls.use")) {
                main.removeLives(p, main.getConfig().getInt("General.Death-souls"));
                if (!main.getConfig().getBoolean("PVP.Enable")) {
                    return;
                }
                if (main.getConfig().getBoolean("PVP.Enable")) {
                    if (k instanceof Player) {
                        main.addLives(k, main.getConfig().getInt("PVP.Kill-souls"));
                        k.sendMessage(main.convert(main.getlang().getString("lang." + main.getConfig().getString("language") + "." + "Kill-message")).replace("%souls%", main.getConfig().getString("PVP.Kill-souls")).replace("%player%", p.getDisplayName()));
                    }
                }
                if (main.getLives(p) >= 1) {
                    (new BukkitRunnable() {
                        public void run() {
                            if (p.isOnline() && p != null) {
                                p.sendMessage(main.convert(main.getlang().getString("lang." + main.getConfig().getString("language") + "." + "Death-message")).replace("%souls%", String.valueOf(main.getLives(p))).replace("%lost%", main.getConfig().getString("General.Death-souls")));
                            }

                        }
                    }).runTaskLater(main, (long) (20 * main.getConfig().getInt("General.Lose-soul-message-after")));
                }

                if (main.getLives(p) > main.getConfig().getInt("General.Minimum_souls")) {
                    e.setKeepInventory(true);
                }

                if (main.getLives(p) < main.getConfig().getInt("General.Minimum_souls")) {
                    main.addLives(p, main.getConfig().getInt("General.Respawn_souls"));
                    if (main.getConfig().getBoolean("DropRandomItems")) {
                        List<Integer> fullSlots = new ArrayList<Integer>();
                        PlayerInventory playerInventory = p.getInventory();
                        for (int i = 0; i <= playerInventory.getSize(); i++) {
                            if (playerInventory.getItem(i) != null)
                                fullSlots.add(Integer.valueOf(i));
                        }
                        if (fullSlots.size() == 0)
                            return;
                        int theSlot = getRandomNumber(0, fullSlots.size());
                        ItemStack itemStack = new ItemStack(playerInventory.getItem(((Integer) fullSlots.get(theSlot)).intValue()));
                        playerInventory.setItem(((Integer) fullSlots.get(theSlot)).intValue(), null);
                    }
                    if (!main.getConfig().getBoolean("DropRandomItems")) {
                        p.getInventory().clear();
                    }
                    (new BukkitRunnable() {
                        public void run() {
                            if (p.isOnline() && p != null) {
                                p.sendMessage(main.convert(main.getlang().getString("lang." + main.getConfig().getString("language") + "." + "Death-message-inventory")).replaceAll("%souls%", String.valueOf(main.getLives(p))));
                            }

                        }
                    }).runTaskLater(main, (long) (20 * main.getConfig().getInt("General.Lose-soul-message-after")));
                }

            }
        }
    }


}
