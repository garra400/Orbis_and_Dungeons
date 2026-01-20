package com.garra400.racas;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.util.Locale;
import java.util.Map;

/**
 * Aplica bonus de stats por raca usando os stats nativos do Hytale.
 */
public final class RaceManager {

    public enum Race {
        ELF,
        ORC,
        HUMAN
    }

    private static final String MOD_PREFIX = "race_mod_";

    // Valores de bonus (MAX) por statId.
    // Base do jogo: Vida=100, Stamina=10 (vida max com armadura=175)
    private static final Map<Race, Map<String, Float>> RACE_BONUSES = Map.of(
            Race.ELF, Map.of(
                    "Health", 0f,
                    "Stamina", 15f   // 10 -> 25 (2.5x stamina base)
            ),
            Race.ORC, Map.of(
                    "Health", 75f,   // 100 -> 175 (igual armadura melhor)
                    "Stamina", 0f
            ),
            Race.HUMAN, Map.of(
                    "Health", 35f,   // 100 -> 135 (intermediario)
                    "Stamina", 5f    // 10 -> 15 (intermediario)
            )
    );

    private RaceManager() {
    }

    public static Race fromKey(String key) {
        if (key == null) {
            return Race.HUMAN;
        }
        return switch (key.toLowerCase(Locale.ROOT)) {
            case "elf" -> Race.ELF;
            case "orc" -> Race.ORC;
            default -> Race.HUMAN;
        };
    }

    public static void applyRace(Player player, Race race, PlayerRef playerRef) {
        if (player == null || race == null) {
            return;
        }
        EntityStatMap stats = EntityStatsModule.get(player); // deprecated in API, mas funcional
        if (stats == null) {
            return;
        }

        Map<String, Float> bonuses = RACE_BONUSES.getOrDefault(race, Map.of());
        applyBonus(stats, "Health", bonuses.getOrDefault("Health", 0f));
        applyBonus(stats, "Stamina", bonuses.getOrDefault("Stamina", 0f));

        stats.update();

        // No need to mark race as selected - persistence is automatic via stat modifiers
    }

    /**
     * Checks if player already has race modifiers applied.
     * This method checks if the player's stats differ from base values,
     * indicating that race modifiers have been applied.
     * 
     * This persists across server restarts and player reconnections because
     * stat modifiers are saved with the player data.
     * 
     * @param player Player to check
     * @return true if race modifiers are already applied, false otherwise
     */
    public static boolean hasRaceApplied(Player player) {
        if (player == null) {
            return false;
        }
        
        try {
            EntityStatMap stats = EntityStatsModule.get(player);
            if (stats == null) {
                return false;
            }

            // Check if Health or Stamina max values differ from base (100 and 10)
            // If they do, it means race modifiers are applied
            var healthStat = stats.get("Health");
            var staminaStat = stats.get("Stamina");
            
            // Base values: Health=100, Stamina=10
            // If max differs from base, race is applied
            if (healthStat != null && healthStat.getMax() != 100f) {
                return true; // Orc or Human applied
            }
            
            if (staminaStat != null && staminaStat.getMax() != 10f) {
                return true; // Elf or Human applied
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static void applyBonus(EntityStatMap stats, String statId, float amount) {
        var stat = stats.get(statId);
        if (stat == null) {
            return;
        }
        String modKey = MOD_PREFIX + statId;
        stats.removeModifier(stat.getIndex(), modKey);
        if (amount == 0f) {
            return;
        }
        Modifier modifier = new StaticModifier(Modifier.ModifierTarget.MAX, StaticModifier.CalculationType.ADDITIVE, amount);
        stats.putModifier(stat.getIndex(), modKey, modifier);
    }
}
