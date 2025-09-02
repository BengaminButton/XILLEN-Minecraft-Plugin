package com.xillen.security;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AntiCheat implements Listener {
    
    private final SecurityPlugin plugin;
    private final Map<UUID, Long> lastMoveTime;
    private final Map<UUID, Location> lastLocation;
    private final Map<UUID, Integer> violationLevels;
    private final Map<UUID, Long> lastAttackTime;
    
    public AntiCheat(SecurityPlugin plugin) {
        this.plugin = plugin;
        this.lastMoveTime = new HashMap<>();
        this.lastLocation = new HashMap<>();
        this.violationLevels = new HashMap<>();
        this.lastAttackTime = new HashMap<>();
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        Location from = event.getFrom();
        Location to = event.getTo();
        
        if (from == null || to == null) return;
        
        long currentTime = System.currentTimeMillis();
        long lastTime = lastMoveTime.getOrDefault(playerId, 0L);
        
        // Check movement frequency
        if (currentTime - lastTime < 50) { // Less than 50ms between moves
            addViolation(playerId, "Movement", "Too fast movement");
        }
        
        // Check distance
        double distance = from.distance(to);
        if (distance > 0.8) { // More than 0.8 blocks per move
            addViolation(playerId, "Movement", "Distance too large: " + String.format("%.2f", distance));
        }
        
        lastMoveTime.put(playerId, currentTime);
        lastLocation.put(playerId, to);
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long lastTime = lastMoveTime.getOrDefault(playerId, 0L);
        
        // Check block break speed
        if (currentTime - lastTime < 100) { // Less than 100ms between breaks
            addViolation(playerId, "Block", "Block break too fast");
        }
        
        lastMoveTime.put(playerId, currentTime);
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long lastTime = lastMoveTime.getOrDefault(playerId, 0L);
        
        // Check block place speed
        if (currentTime - lastTime < 100) { // Less than 100ms between places
            addViolation(playerId, "Block", "Block place too fast");
        }
        
        lastMoveTime.put(playerId, currentTime);
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        
        Player player = (Player) event.getDamager();
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long lastTime = lastAttackTime.getOrDefault(playerId, 0L);
        
        // Check attack speed (CPS)
        if (currentTime - lastTime < 50) { // Less than 50ms between attacks
            addViolation(playerId, "Combat", "Attack too fast");
        }
        
        // Check reach
        double distance = player.getLocation().distance(event.getEntity().getLocation());
        if (distance > 4.5) { // More than 4.5 blocks reach
            addViolation(playerId, "Combat", "Reach too far: " + String.format("%.2f", distance));
        }
        
        lastAttackTime.put(playerId, currentTime);
    }
    
    private void addViolation(UUID playerId, String type, String reason) {
        int currentLevel = violationLevels.getOrDefault(playerId, 0);
        currentLevel++;
        violationLevels.put(playerId, currentLevel);
        
        plugin.getLogger().warning("Anti-cheat violation: " + type + " - " + reason + " (Level: " + currentLevel + ")");
        
        // Take action based on violation level
        if (currentLevel >= 10) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null) {
                player.kickPlayer(ChatColor.RED + "You have been kicked for suspicious activity!");
                plugin.getLogger().severe("Player " + player.getName() + " kicked for multiple violations!");
            }
            violationLevels.remove(playerId);
        }
    }
    
    public void resetViolations(UUID playerId) {
        violationLevels.remove(playerId);
    }
    
    public int getViolationLevel(UUID playerId) {
        return violationLevels.getOrDefault(playerId, 0);
    }
}
