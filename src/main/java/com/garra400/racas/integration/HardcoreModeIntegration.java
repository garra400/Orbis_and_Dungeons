package com.garra400.racas.integration;

import com.hypixel.hytale.server.core.entity.entities.Player;

/**
 * Integration with HardcoreMode mod
 * Provides race-based difficulty scaling
 */
public class HardcoreModeIntegration {

    private static boolean initialized = false;

    /**
     * Initialize HardcoreMode integration
     * Currently passive - HardcoreMode can query Orbis via ModIntegration
     */
    public static void initialize() {
        if (initialized) {
            return;
        }

        // HardcoreMode can access our API via:
        // - ModIntegration.getPlayerDifficultyMultiplier(player)
        // - ModIntegration.getRaceDifficultyMultiplier(raceId)
        // - ModIntegration.getClassDifficultyMultiplier(classId)

        System.out.println("[Orbis] HardcoreMode integration initialized (passive API mode)");
        initialized = true;
    }

    /**
     * Calculate mob difficulty multiplier for a specific player
     * Takes into account both RPGLeveling level and Orbis race/class
     *
     * @param player The player
     * @param baseDifficulty Base difficulty from HardcoreMode (1.0 = normal)
     * @return Modified difficulty multiplier
     */
    public static float calculateMobDifficulty(Player player, float baseDifficulty) {
        // Get race/class multiplier
        float raceClassMult = ModIntegration.getPlayerDifficultyMultiplier(player);

        // Get level multiplier if RPGLeveling is available
        float levelMult = 1.0f;
        if (ModIntegration.isRPGLevelingAvailable()) {
            int level = ModIntegration.getPlayerLevel(player);
            // Scale difficulty with level: +2% per level
            levelMult = 1.0f + (level * 0.02f);
        }

        // Combine all multipliers
        return baseDifficulty * raceClassMult * levelMult;
    }

    /**
     * Check if a specific race should have reduced mob spawns
     * Tank races might attract more enemies
     */
    public static boolean shouldIncreaseSpawnRate(String raceId) {
        // Tank races attract more enemies
        return "orc".equals(raceId) || "dwarf".equals(raceId);
    }

    /**
     * Check if a specific race should have increased mob spawns
     * Fragile races might attract fewer enemies
     */
    public static boolean shouldDecreaseSpawnRate(String raceId) {
        // Fragile races attract fewer enemies
        return "elf".equals(raceId) || "tiefling".equals(raceId);
    }

    /**
     * Get spawn rate multiplier based on race
     */
    public static float getSpawnRateMultiplier(String raceId) {
        if (shouldIncreaseSpawnRate(raceId)) {
            return 1.2f; // +20% spawn rate
        }
        if (shouldDecreaseSpawnRate(raceId)) {
            return 0.8f; // -20% spawn rate
        }
        return 1.0f;
    }
}
