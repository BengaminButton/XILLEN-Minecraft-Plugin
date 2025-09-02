package com.xillen.security;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;

public class SecurityLogger {
    
    private final SecurityPlugin plugin;
    private final File logFile;
    private final SimpleDateFormat dateFormat;
    private final Map<UUID, StringBuilder> playerLogs;
    
    public SecurityLogger(SecurityPlugin plugin) {
        this.plugin = plugin;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.playerLogs = new ConcurrentHashMap<>();
        
        // Create logs directory if it doesn't exist
        File logsDir = new File(plugin.getDataFolder(), "logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }
        
        this.logFile = new File(logsDir, "xillen-security.log");
        
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("[XILLEN] Could not create log file: " + e.getMessage());
        }
    }
    
    public void logViolation(UUID playerId, String playerName, String type, String reason, int level) {
        String timestamp = dateFormat.format(new Date());
        String logEntry = String.format("[%s] VIOLATION - Player: %s, Type: %s, Reason: %s, Level: %d", 
                                      timestamp, playerName, type, reason, level);
        
        // Log to console
        System.out.println("[XILLEN] " + logEntry);
        
        // Log to file
        logToFile(logEntry);
        
        // Add to player-specific logs
        addToPlayerLog(playerId, logEntry);
    }
    
    public void logCommand(UUID playerId, String playerName, String command) {
        String timestamp = dateFormat.format(new Date());
        String logEntry = String.format("[%s] COMMAND - Player: %s, Command: %s", 
                                      timestamp, playerName, command);
        
        logToFile(logEntry);
        addToPlayerLog(playerId, logEntry);
    }
    
    public void logConnection(UUID playerId, String playerName, String action) {
        String timestamp = dateFormat.format(new Date());
        String logEntry = String.format("[%s] CONNECTION - Player: %s, Action: %s", 
                                      timestamp, playerName, action);
        
        logToFile(logEntry);
        addToPlayerLog(playerId, logEntry);
    }
    
    public void logSecurity(String message) {
        String timestamp = dateFormat.format(new Date());
        String logEntry = String.format("[%s] SECURITY - %s", timestamp, message);
        
        System.out.println("[XILLEN] " + logEntry);
        logToFile(logEntry);
    }
    
    private void logToFile(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            writer.println(message);
        } catch (IOException e) {
            System.out.println("[XILLEN] Could not write to log file: " + e.getMessage());
        }
    }
    
    private void addToPlayerLog(UUID playerId, String logEntry) {
        playerLogs.computeIfAbsent(playerId, k -> new StringBuilder());
        playerLogs.get(playerId).append(logEntry).append("\n");
    }
    
    public String getPlayerLogs(UUID playerId) {
        StringBuilder logs = playerLogs.get(playerId);
        return logs != null ? logs.toString() : "No logs found for this player.";
    }
    
    public void clearPlayerLogs(UUID playerId) {
        playerLogs.remove(playerId);
    }
    
    public void saveAllLogs() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            writer.println("=== SESSION END ===");
            writer.println("Timestamp: " + dateFormat.format(new Date()));
            writer.println("Total players logged: " + playerLogs.size());
            writer.println("==================");
        } catch (IOException e) {
            System.out.println("[XILLEN] Could not write session end to log file: " + e.getMessage());
        }
    }
    
    public File getLogFile() {
        return logFile;
    }
    
    public int getTotalLoggedPlayers() {
        return playerLogs.size();
    }
}
