package com.garra400.racas.integration;

import com.garra400.racas.RaceManager;
import com.hypixel.hytale.server.core.entity.entities.Player;

/**
 * Integration layer for third-party mods
 * Handles compatibility with RPGLeveling and HardcoreMode
 */
public class ModIntegration {

    private static boolean rpgLevelingAvailable = false;
    private static boolean hardcoreModeAvailable = false;

    /**
     * Initialize mod detection on startup
     * Called from RaceMod.start()
     */
    public static void initialize() {
        // Try to detect RPGLeveling
        try {
            Class.forName("org.zuxaw.plugin.api.RPGLevelingAPI");
            rpgLevelingAvailable = true;
            System.out.println("[Orbis] RPGLeveling detected - integration enabled");
        } catch (ClassNotFoundException e) {
            rpgLevelingAvailable = false;
            System.out.println("[Orbis] RPGLeveling not found - running standalone");
        }

        // Try to detect HardcoreMode
        try {
            Class.forName("com.example.plugin.HardcoreModePlugin");
            hardcoreModeAvailable = true;
            System.out.println("[Orbis] HardcoreMode detected - integration enabled");
        } catch (ClassNotFoundException e) {
            hardcoreModeAvailable = false;
            System.out.println("[Orbis] HardcoreMode not found - running standalone");
        }

        // Initialize integrations if available
        if (rpgLevelingAvailable) {
            RPGLevelingIntegration.initialize();
        }

        if (hardcoreModeAvailable) {
            HardcoreModeIntegration.initialize();
        }
    }

    /**
     * Check if RPGLeveling is available
     */
    public static boolean isRPGLevelingAvailable() {
        return rpgLevelingAvailable;
    }

    /**
     * Check if HardcoreMode is available
     */
    public static boolean isHardcoreModeAvailable() {
        return hardcoreModeAvailable;
    }

    /**
     * Get player level from RPGLeveling (if available)
     * Returns 0 if RPGLeveling is not available
     */
    public static int getPlayerLevel(Player player) {
        if (!rpgLevelingAvailable) {
            return 0;
        }

        return RPGLevelingIntegration.getPlayerLevel(player);
    }

    /**
     * Get player XP from RPGLeveling (if available)
     * Returns 0 if RPGLeveling is not available
     */
    public static long getPlayerXP(Player player) {
        if (!rpgLevelingAvailable) {
            return 0;
        }

        return RPGLevelingIntegration.getPlayerXP(player);
    }

    /**
     * Apply race and class bonuses with RPGLeveling integration
     * This ensures race bonuses are preserved after level-ups
     */
    public static void applyRaceWithLevelSync(Player player, String raceId, String classId) {
        // Always apply race bonuses first
        RaceManager.applyRaceAndClass(player, raceId, classId);

        // If RPGLeveling is available, ensure stats are synchronized
        if (rpgLevelingAvailable) {
            RPGLevelingIntegration.synchronizeStats(player);
        }
    }

    /**
     * Called when player levels up (from RPGLeveling event)
     * Reapplies race/class bonuses to ensure they persist
     */
    public static void onPlayerLevelUp(Player player) {
        String raceId = RaceManager.getPlayerRace(player);
        String classId = RaceManager.getPlayerClass(player);

        if (raceId != null) {
            System.out.println("[Orbis] Reapplying race bonuses after level-up: " + raceId + " / " + classId);
            RaceManager.applyRaceAndClass(player, raceId, classId);
        }
    }

    /**
     * Get difficulty multiplier based on player race
     * Used by HardcoreMode for dynamic difficulty scaling
     */
    public static float getRaceDifficultyMultiplier(String raceId) {
        if (raceId == null) {
            return 1.0f;
        }

        // Tank races face tougher enemies
        if ("orc".equals(raceId) || "dwarf".equals(raceId)) {
            return 1.15f; // +15% mob stats
        }

        // Glass cannon races face weaker enemies
        if ("elf".equals(raceId) || "tiefling".equals(raceId)) {
            return 0.90f; // -10% mob stats
        }

        // Balanced race
        return 1.0f;
    }

    /**
     * Get difficulty multiplier based on player class
     * Used by HardcoreMode for dynamic difficulty scaling
     */
    public static float getClassDifficultyMultiplier(String classId) {
        if (classId == null || "none".equals(classId)) {
            return 1.0f;
        }

        // High damage classes face tougher enemies
        if ("berserker".equals(classId) || "archer".equals(classId) || "mage".equals(classId)) {
            return 1.10f; // +10% mob stats
        }

        // Balanced classes
        if ("swordsman".equals(classId) || "crusader".equals(classId)) {
            return 1.05f; // +5% mob stats
        }

        // Assassin (glass cannon)
        if ("assassin".equals(classId)) {
            return 0.95f; // -5% mob stats
        }

        return 1.0f;
    }

    /**
     * Get combined difficulty multiplier for a player
     * Combines race and class difficulty multipliers
     */
    public static float getPlayerDifficultyMultiplier(Player player) {
        String raceId = RaceManager.getPlayerRace(player);
        String classId = RaceManager.getPlayerClass(player);

        float raceMult = getRaceDifficultyMultiplier(raceId);
        float classMult = getClassDifficultyMultiplier(classId);

        // Multiplicative combination
        return raceMult * classMult;
    }
}
