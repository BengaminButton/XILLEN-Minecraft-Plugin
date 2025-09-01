package com.xillen.security;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SecurityPlugin extends JavaPlugin implements Listener {
    
    private FileConfiguration config;
    private SecurityManager securityManager;
    private AntiCheat antiCheat;
    private Logger logger;
    private Map<UUID, PlayerData> playerData;
    private Map<UUID, Long> lastMoveTime;
    private Map<UUID, Vector> lastLocation;
    private Map<UUID, Integer> violationLevels;
    
    @Override
    public void onEnable() {
        getLogger().info("XILLEN Security Plugin v2.0 by @Bengamin_Button is starting...");
        
        saveDefaultConfig();
        config = getConfig();
        
        playerData = new ConcurrentHashMap<>();
        lastMoveTime = new ConcurrentHashMap<>();
        lastLocation = new ConcurrentHashMap<>();
        violationLevels = new ConcurrentHashMap<>();
        
        securityManager = new SecurityManager(this);
        antiCheat = new AntiCheat(this);
        logger = new Logger(this);
        
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(securityManager, this);
        getServer().getPluginManager().registerEvents(antiCheat, this);
        
        startMonitoringTask();
        
        getLogger().info("XILLEN Security Plugin has been enabled successfully!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("XILLEN Security Plugin is shutting down...");
        
        if (logger != null) {
            logger.saveAllLogs();
        }
        
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
                    
                case "logs":
                    if (args.length > 1) {
                        showLogs(sender, args[1]);
                    } else {
                        showLogs(sender, "recent");
                    }
                    break;
                    
                case "ban":
                    if (args.length > 1) {
                        String playerName = args[1];
                        String reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "No reason specified";
                        securityManager.banPlayer(playerName, reason, sender.getName());
                        sender.sendMessage(ChatColor.GREEN + "Player " + playerName + " has been banned!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /xillen ban <player> [reason]");
                    }
                    break;
                    
                case "unban":
                    if (args.length > 1) {
                        String playerName = args[1];
                        securityManager.unbanPlayer(playerName);
                        sender.sendMessage(ChatColor.GREEN + "Player " + playerName + " has been unbanned!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /xillen unban <player>");
                    }
                    break;
                    
                case "kick":
                    if (args.length > 1) {
                        String playerName = args[1];
                        String reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "No reason specified";
                        securityManager.kickPlayer(playerName, reason);
                        sender.sendMessage(ChatColor.GREEN + "Player " + playerName + " has been kicked!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /xillen kick <player> [reason]");
                    }
                    break;
                    
                case "scan":
                    if (args.length > 1) {
                        String playerName = args[1];
                        antiCheat.scanPlayer(playerName);
                        sender.sendMessage(ChatColor.GREEN + "Scanning player " + playerName + "...");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /xillen scan <player>");
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
        sender.sendMessage(ChatColor.WHITE + "/xillen logs [type]" + ChatColor.GRAY + " - Show logs");
        sender.sendMessage(ChatColor.WHITE + "/xillen ban <player> [reason]" + ChatColor.GRAY + " - Ban player");
        sender.sendMessage(ChatColor.WHITE + "/xillen unban <player>" + ChatColor.GRAY + " - Unban player");
        sender.sendMessage(ChatColor.WHITE + "/xillen kick <player> [reason]" + ChatColor.GRAY + " - Kick player");
        sender.sendMessage(ChatColor.WHITE + "/xillen scan <player>" + ChatColor.GRAY + " - Scan player for cheats");
    }
    
    private void showStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "╔══════════════════════════════════════════════════════════════╗");
        sender.sendMessage(ChatColor.GOLD + "║                      Plugin Status                          ║");
        sender.sendMessage(ChatColor.GOLD + "╚══════════════════════════════════════════════════════════════╝");
        sender.sendMessage(ChatColor.WHITE + "Online players: " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size());
        sender.sendMessage(ChatColor.WHITE + "Total violations: " + ChatColor.RED + violationLevels.size());
        sender.sendMessage(ChatColor.WHITE + "Banned players: " + ChatColor.RED + securityManager.getBannedPlayersCount());
        sender.sendMessage(ChatColor.WHITE + "Plugin version: " + ChatColor.BLUE + "2.0");
        sender.sendMessage(ChatColor.WHITE + "Author: " + ChatColor.GOLD + "@Bengamin_Button");
    }
    
    private void showLogs(CommandSender sender, String type) {
        List<String> logs = logger.getLogs(type);
        if (logs.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No logs found for type: " + type);
            return;
        }
        
        sender.sendMessage(ChatColor.GOLD + "╔══════════════════════════════════════════════════════════════╗");
        sender.sendMessage(ChatColor.GOLD + "║                        Security Logs                         ║");
        sender.sendMessage(ChatColor.GOLD + "╚══════════════════════════════════════════════════════════════╝");
        
        for (String log : logs.subList(0, Math.min(20, logs.size()))) {
            sender.sendMessage(ChatColor.GRAY + log);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        playerData.put(playerId, new PlayerData(player));
        lastMoveTime.put(playerId, System.currentTimeMillis());
        lastLocation.put(playerId, player.getLocation().toVector());
        violationLevels.put(playerId, 0);
        
        logger.log("JOIN", player.getName(), "Player joined the server");
        
        if (securityManager.isPlayerBanned(player.getName())) {
            player.kickPlayer(ChatColor.RED + "You are banned from this server!");
            return;
        }
        
        securityManager.checkPlayer(player);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        logger.log("QUIT", player.getName(), "Player left the server");
        
        playerData.remove(playerId);
        lastMoveTime.remove(playerId);
        lastLocation.remove(playerId);
        violationLevels.remove(playerId);
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        long lastTime = lastMoveTime.getOrDefault(playerId, currentTime);
        Vector lastLoc = lastLocation.getOrDefault(playerId, event.getFrom().toVector());
        
        if (lastTime > 0) {
            long timeDiff = currentTime - lastTime;
            double distance = event.getTo().toVector().distance(lastLoc);
            double speed = distance / (timeDiff / 1000.0);
            
            if (speed > config.getDouble("anticheat.max_speed", 20.0)) {
                antiCheat.handleSpeedHack(player, speed);
            }
        }
        
        lastMoveTime.put(playerId, currentTime);
        lastLocation.put(playerId, event.getTo().toVector());
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
        
        if (securityManager.isCommandBlocked(command)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "This command is blocked!");
            logger.log("BLOCKED_CMD", player.getName(), "Blocked command: " + command);
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if (!securityManager.canPlayerBreakBlock(player, event.getBlock())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot break this block!");
            logger.log("BLOCK_BREAK", player.getName(), "Blocked block break at " + event.getBlock().getLocation());
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (!securityManager.canPlayerPlaceBlock(player, event.getBlock())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot place this block here!");
            logger.log("BLOCK_PLACE", player.getName(), "Blocked block place at " + event.getBlock().getLocation());
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            
            if (!securityManager.canPlayerDamage(player, event.getEntity())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot damage this entity!");
                logger.log("ENTITY_DAMAGE", player.getName(), "Blocked damage to " + event.getEntity().getType());
            }
        }
    }
    
    private void startMonitoringTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID playerId = player.getUniqueId();
                    
                    if (playerData.containsKey(playerId)) {
                        PlayerData data = playerData.get(playerId);
                        data.updateActivity();
                        
                        if (data.isInactive()) {
                            player.kickPlayer(ChatColor.YELLOW + "You were kicked for inactivity!");
                            logger.log("INACTIVITY", player.getName(), "Kicked for inactivity");
                        }
                    }
                }
            }
        }.runTaskTimer(this, 20L * 60, 20L * 60);
    }
    
    public FileConfiguration getPluginConfig() {
        return config;
    }
    
    public SecurityManager getSecurityManager() {
        return securityManager;
    }
    
    public AntiCheat getAntiCheat() {
        return antiCheat;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public Map<UUID, PlayerData> getPlayerData() {
        return playerData;
    }
    
    public Map<UUID, Integer> getViolationLevels() {
        return violationLevels;
    }
    
    public void addViolation(UUID playerId) {
        int currentLevel = violationLevels.getOrDefault(playerId, 0);
        violationLevels.put(playerId, currentLevel + 1);
        
        if (currentLevel + 1 >= config.getInt("anticheat.max_violations", 5)) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                securityManager.kickPlayer(player.getName(), "Too many violations");
                logger.log("VIOLATIONS", player.getName(), "Kicked for too many violations: " + (currentLevel + 1));
            }
        }
    }
}

