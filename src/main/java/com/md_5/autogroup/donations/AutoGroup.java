package com.md_5.autogroup.donations;

import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoGroup extends JavaPlugin {

    public static final Logger logger = Bukkit.getServer().getLogger();
    public static HashMap<String, Don> playerDonations = new HashMap<String, Don>();
    static FileConfiguration config;

    @Override
    public void onEnable() {
        config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
        Config.debug = config.getBoolean("debug", Config.debug);
        Config.url = config.getString("mysqlURL", Config.url);
        Config.dbName = config.getString("dbName", Config.dbName);
        Config.userName = config.getString("username", Config.userName);
        Config.password = config.getString("password", Config.password);
        Config.table = config.getString("table", Config.table);
        Config.table2 = config.getString("table2", Config.table2);
        Config.don1Group = config.getString("don1Group", Config.don1Group);
        Config.don2Group = config.getString("don2Group", Config.don2Group);
        Config.don3Group = config.getString("don3Group", Config.don3Group);
        Config.don1Amount = config.getInt("don1Amount", Config.don1Amount);
        Config.don2Amount = config.getInt("don2Amount", Config.don2Amount);
        Config.don3Amount = config.getInt("don3Amount", Config.don3Amount);
        Config.command = config.getString("command", Config.command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            return onPlayerCommand((Player) sender, command, label, args);
        } else {
            return onConsoleCommand(sender, command, label, args);
        }
    }

    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        MYSQL.init();
        String name = player.getName();
        if (args.length == 0) {
            MYSQL.loadAmount(name);
            player.sendMessage(ChatColor.GREEN + "Your donations total $" + playerDonations.get(name).amount);
            playerDonations.remove(name);
            return true;
        }
        MYSQL.load(args[0], name);
        if (playerDonations.get(name).payment_amount == 0) {
            player.sendMessage(ChatColor.RED + "Are you trying to scam us? Invalid transaction id!");
            playerDonations.remove(name);
            return true;
        }
        player.sendMessage(ChatColor.RED + "The following information has been found about your donation:");
        player.sendMessage(ChatColor.GREEN + "First Name: " + playerDonations.get(name).first_name);
        player.sendMessage(ChatColor.GREEN + "Last Name: " + playerDonations.get(name).last_name);
        player.sendMessage(ChatColor.GREEN + "Email Address: " + playerDonations.get(name).payer_email);
        player.sendMessage(ChatColor.GREEN + "Amount: " + playerDonations.get(name).payment_amount);
        player.sendMessage(ChatColor.GREEN + "Claimed: " + playerDonations.get(name).claimed);
        player.sendMessage(ChatColor.GREEN + "Total: " + (playerDonations.get(name).amount + playerDonations.get(name).payment_amount));
        if (playerDonations.get(name).claimed) {
            player.sendMessage(ChatColor.RED + "That donation has been processed and your total will remain at $" + playerDonations.get(name).amount);
            playerDonations.remove(name);
            return true;
        }
        boolean add = false;
        if (playerDonations.get(name).name == null) {
            add = true;
        }
        playerDonations.get(name).name = name;
        playerDonations.get(name).amount += playerDonations.get(name).payment_amount;
        playerDonations.get(name).claimed = true;
        if (add) {
            MYSQL.add(name);
        }
        MYSQL.update(name, args[0]);
        if (playerDonations.get(name).amount <= Config.don1Amount) {
            player.sendMessage("That donation is big enough for " + Config.don1Group + " status. You have now been promoted to that rank!");
            Promote.promotePlayer(name, Config.don1Group);
        } else if (playerDonations.get(name).amount <= Config.don2Amount) {
            player.sendMessage("That donation is big enough for " + Config.don2Group + " status. You have now been promoted to that rank!");
            Promote.promotePlayer(name, Config.don2Group);
        } else if (playerDonations.get(name).amount >= Config.don3Amount) {
            player.sendMessage("That donation is big enough for " + Config.don3Group + " status. You have now been promoted to that rank!");
            Promote.promotePlayer(name, Config.don3Group);
        } else {
            player.sendMessage("That donation is not big enough to increase your rank :(");
        }
        playerDonations.remove(name);
        return true;
    }

    public boolean onConsoleCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(String.format("AutoGroup v%1$s by md_5", this.getDescription().getVersion()));
        sender.sendMessage("AutoGroup: No other console functionality is available at this time");
        return true;
    }
}
