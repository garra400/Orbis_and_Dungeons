package com.garra400.racas;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

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
    private static final Map<Race, Map<String, Float>> RACE_BONUSES = Map.of(
            Race.ELF, Map.of(
                    "Health", 0f,
                    "Stamina", 0f,
                    "Mana", 100f     // Dobro de mana
            ),
            Race.ORC, Map.of(
                    "Health", 100f,  // Dobro de vida
                    "Stamina", 0f,
                    "Mana", 0f
            ),
            Race.HUMAN, Map.of(
                    "Health", 0f,
                    "Stamina", 100f, // Dobro de stamina
                    "Mana", 0f
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

    public static void applyRace(Player player, Race race) {
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
        applyBonus(stats, "Mana", bonuses.getOrDefault("Mana", 0f));

        stats.update();
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
