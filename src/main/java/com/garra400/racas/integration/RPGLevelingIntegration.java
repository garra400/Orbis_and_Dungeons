package com.garra400.racas.integration;

import com.garra400.racas.RaceManager;
import com.hypixel.hytale.server.core.entity.entities.Player;

/**
 * Integration with RPGLeveling mod
 * Handles event listening and stat synchronization
 */
public class RPGLevelingIntegration {

    private static boolean initialized = false;

    /**
     * Initialize RPGLeveling integration
     * Registers event listeners for level-up events
     */
    public static void initialize() {
        if (initialized) {
            return;
        }

        try {
            // Try to register level-up event listener using reflection
            // This avoids hard dependency on RPGLeveling classes
            Class<?> eventRegistry = Class.forName("com.hypixel.hytale.server.core.event.EventRegistry");
            Class<?> levelUpEvent = Class.forName("org.zuxaw.plugin.event.LevelUpEvent");

            // Register global event listener
            java.lang.reflect.Method registerMethod = eventRegistry.getMethod(
                "registerGlobal",
                Class.class,
                java.util.function.Consumer.class
            );

            registerMethod.invoke(null, levelUpEvent, (java.util.function.Consumer<Object>) event -> {
                try {
                    // Extract player from event using reflection
                    java.lang.reflect.Method getPlayerMethod = event.getClass().getMethod("getPlayer");
                    Player player = (Player) getPlayerMethod.invoke(event);

                    // Reapply race bonuses after level-up
                    ModIntegration.onPlayerLevelUp(player);

                } catch (Exception e) {
                    System.out.println("[Orbis] Error handling RPGLeveling level-up event: " + e.getMessage());
                }
            });

            initialized = true;
            System.out.println("[Orbis] Successfully registered RPGLeveling event listeners");

        } catch (Exception e) {
            System.out.println("[Orbis] RPGLeveling integration not available (non-fatal): " + e.getMessage());
            // Non-fatal - mod works without this integration
        }
    }

    /**
     * Get player level using reflection (avoids hard dependency)
     */
    public static int getPlayerLevel(Player player) {
        try {
            Class<?> apiClass = Class.forName("org.zuxaw.plugin.api.RPGLevelingAPI");
            java.lang.reflect.Method getLevelMethod = apiClass.getMethod("getPlayerLevel", Player.class);
            Object result = getLevelMethod.invoke(null, player);
            return result != null ? (Integer) result : 0;
        } catch (Exception e) {
            System.out.println("[Orbis] RPGLeveling API not available for getPlayerLevel");
            return 0;
        }
    }

    /**
     * Get player XP using reflection (avoids hard dependency)
     */
    public static long getPlayerXP(Player player) {
        try {
            Class<?> apiClass = Class.forName("org.zuxaw.plugin.api.RPGLevelingAPI");
            java.lang.reflect.Method getXPMethod = apiClass.getMethod("getPlayerXP", Player.class);
            Object result = getXPMethod.invoke(null, player);
            return result != null ? (Long) result : 0L;
        } catch (Exception e) {
            System.out.println("[Orbis] RPGLeveling API not available for getPlayerXP");
            return 0L;
        }
    }

    /**
     * Synchronize stats between Orbis and RPGLeveling
     * Ensures race bonuses are not lost when RPGLeveling recalculates stats
     */
    public static void synchronizeStats(Player player) {
        // Get current race/class
        String raceId = RaceManager.getPlayerRace(player);
        String classId = RaceManager.getPlayerClass(player);

        if (raceId == null) {
            return; // No race selected
        }

        // Get player level
        int level = getPlayerLevel(player);

        System.out.println("[Orbis] Synchronizing stats for player" +
                          " (Level " + level + ", Race: " + raceId + ", Class: " + classId + ")");

        // Reapply race stats
        // This runs after RPGLeveling applies its modifiers
        RaceManager.applyRaceAndClass(player, raceId, classId);
    }
}
