package com.xillen.security;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SimpleSecurityPlugin extends JavaPlugin implements Listener {
    
    @Override
    public void onEnable() {
        getLogger().info("XILLEN Security Plugin v2.0 by @Bengamin_Button is starting...");
        
        saveDefaultConfig();
        
        getServer().getPluginManager().registerEvents(this, this);
        
        getLogger().info("XILLEN Security Plugin has been enabled successfully!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("XILLEN Security Plugin is shutting down...");
        getLogger().info("XILLEN Security Plugin has been disabled!");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("xillen")) {
            if (!sender.hasPermission("xillen.admin")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }
            
            if (args.length == 0) {
                showHelp(sender);
                return true;
            }
            
            switch (args[0].toLowerCase()) {
                case "reload":
                    reloadConfig();
                    sender.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
                    break;
                    
                case "status":
                    showStatus(sender);
                    break;
                    
                case "kick":
                    if (args.length > 1) {
                        String playerName = args[1];
                        String reason = args.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)) : "No reason specified";
                        Player targetPlayer = Bukkit.getPlayer(playerName);
                        if (targetPlayer != null) {
                            targetPlayer.kickPlayer(ChatColor.RED + "You have been kicked: " + reason);
                            sender.sendMessage(ChatColor.GREEN + "Player " + playerName + " has been kicked!");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Player " + playerName + " is not online!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /xillen kick <player> [reason]");
                    }
                    break;
                    
                default:
                    sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /xillen for help.");
                    break;
            }
            return true;
        }
        return false;
    }
    
    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "╔══════════════════════════════════════════════════════════════╗");
        sender.sendMessage(ChatColor.GOLD + "║                    XILLEN Security Commands                  ║");
        sender.sendMessage(ChatColor.GOLD + "╚══════════════════════════════════════════════════════════════╝");
        sender.sendMessage(ChatColor.YELLOW + "Available commands:");
        sender.sendMessage(ChatColor.WHITE + "/xillen reload" + ChatColor.GRAY + " - Reload configuration");
        sender.sendMessage(ChatColor.WHITE + "/xillen status" + ChatColor.GRAY + " - Show plugin status");
        sender.sendMessage(ChatColor.WHITE + "/xillen kick <player> [reason]" + ChatColor.GRAY + " - Kick player");
    }
    
    private void showStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "╔══════════════════════════════════════════════════════════════╗");
        sender.sendMessage(ChatColor.GOLD + "║                      Plugin Status                          ║");
        sender.sendMessage(ChatColor.GOLD + "╚══════════════════════════════════════════════════════════════╝");
        sender.sendMessage(ChatColor.WHITE + "Online players: " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size());
        sender.sendMessage(ChatColor.WHITE + "Plugin version: " + ChatColor.BLUE + "2.0");
        sender.sendMessage(ChatColor.WHITE + "Author: " + ChatColor.GOLD + "@Bengamin_Button");
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getLogger().info("Player " + player.getName() + " joined the server");
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        getLogger().info("Player " + player.getName() + " left the server");
    }
    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();
        
        if (command.startsWith("/xillen") && !player.hasPermission("xillen.admin")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return;
        }
        
        // Check for blocked commands
        List<String> blockedCommands = getConfig().getStringList("security.blocked-commands");
        for (String blocked : blockedCommands) {
            if (command.startsWith("/" + blocked.toLowerCase())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "This command is blocked!");
                getLogger().warning("Player " + player.getName() + " tried to use blocked command: " + command);
                return;
            }
        }
    }
}
