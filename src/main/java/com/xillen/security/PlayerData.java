package com.xillen.security;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {
    
    private final UUID playerId;
    private final String playerName;
    private Location lastLocation;
    private long lastActivity;
    private int violationLevel;
    private long joinTime;
    private boolean isOnline;
    
    public PlayerData(Player player) {
        this.playerId = player.getUniqueId();
        this.playerName = player.getName();
        this.lastLocation = player.getLocation();
        this.lastActivity = System.currentTimeMillis();
        this.violationLevel = 0;
        this.joinTime = System.currentTimeMillis();
        this.isOnline = true;
    }
    
    public void updateActivity() {
        this.lastActivity = System.currentTimeMillis();
    }
    
    public void updateLocation(Location location) {
        this.lastLocation = location;
        updateActivity();
    }
    
    public void addViolation() {
        this.violationLevel++;
    }
    
    public void resetViolations() {
        this.violationLevel = 0;
    }
    
    public boolean isInactive() {
        long currentTime = System.currentTimeMillis();
        long inactiveTime = currentTime - lastActivity;
        // Consider inactive after 10 minutes
        return inactiveTime > 600000;
    }
    
    public void setOffline() {
        this.isOnline = false;
    }
    
    public void setOnline() {
        this.isOnline = true;
        this.joinTime = System.currentTimeMillis();
    }
    
    // Getters
    public UUID getPlayerId() {
        return playerId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public Location getLastLocation() {
        return lastLocation;
    }
    
    public long getLastActivity() {
        return lastActivity;
    }
    
    public int getViolationLevel() {
        return violationLevel;
    }
    
    public long getJoinTime() {
        return joinTime;
    }
    
    public boolean isOnline() {
        return isOnline;
    }
    
    public long getOnlineTime() {
        if (isOnline) {
            return System.currentTimeMillis() - joinTime;
        }
        return 0;
    }
}
