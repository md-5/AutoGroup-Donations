package com.md_5.autogroup.donations;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Promote {

    public static void promotePlayer(String player, String group) {
        if (Bukkit.getServer().getPlayer(player).hasPermission("autogroup.norank")) {
            return;
        }
        AutoGroup.logger.info(String.format("AutoGroup: Trying to promote %1$s to group " + group, player));
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.sendMessage(ChatColor.LIGHT_PURPLE + "AutoGroup: Please welcome " + player + " to group " + group);
        }
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format(Config.command, player, group));
        return;
    }
}
