package com.xillen.security;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class SecurityManager implements Listener {
    
    private final SecurityPlugin plugin;
    
    public SecurityManager(SecurityPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getLogger().info("Player " + player.getName() + " joined the server");
        
        // Check for suspicious names
        if (isSuspiciousName(player.getName())) {
            plugin.getLogger().warning("Suspicious player name detected: " + player.getName());
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getLogger().info("Player " + player.getName() + " left the server");
    }
    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();
        
        // Check for blocked commands
        if (isBlockedCommand(command) && !player.hasPermission("xillen.bypass")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "This command is blocked for security reasons!");
            plugin.getLogger().warning("Player " + player.getName() + " tried to use blocked command: " + command);
        }
    }
    
    private boolean isSuspiciousName(String name) {
        // Check for suspicious patterns in player names
        String lowerName = name.toLowerCase();
        return lowerName.contains("admin") || 
               lowerName.contains("moderator") || 
               lowerName.contains("staff") ||
               lowerName.contains("op") ||
               lowerName.contains("console");
    }
    
    private boolean isBlockedCommand(String command) {
        List<String> blockedCommands = plugin.getConfig().getStringList("security.blocked-commands");
        for (String blocked : blockedCommands) {
            if (command.startsWith("/" + blocked.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
