package com.garra400.racas;

import com.garra400.racas.components.RaceData;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.Locale;
import java.util.Map;

/**
 * Aplica bonus de stats por raca usando os stats nativos do Hytale.
 * Agora usa Components para persistência confiável de dados.
 */
public final class RaceManager {

    public enum Race {
        ELF,
        ORC,
        HUMAN,
        BERSERKER
    }

    private static final String MOD_PREFIX = "race_mod_";

    /**
     * Component type for accessing race data.
     * Set by RaceMod during initialization.
     */
    private static ComponentType<EntityStore, RaceData> raceDataType;

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
            ),
            Race.BERSERKER, Map.of(
                    "Health", 0f,
                    "Stamina", 0f    // foco em dano, sem bônus base
            )
    );

    // Regras de multiplicador de dano por arma para cada raça.
    private static final Map<Race, WeaponRule[]> RACE_WEAPON_RULES = Map.of(
            Race.BERSERKER, new WeaponRule[]{
                    new WeaponRule(1.5f, "Weapon_Battleaxe", "weapon_battleaxe", "Weapon_Axe", "weapon_axe"),
                    new WeaponRule(1.5f, "battleaxe", "axe") // fallback amplo
            }
    );

    private RaceManager() {
    }

    /**
     * Sets the component type for race data access.
     * Called by RaceMod during initialization.
     * 
     * @param type The registered RaceData component type
     */
    public static void setRaceDataType(ComponentType<EntityStore, RaceData> type) {
        raceDataType = type;
    }

    public static Race fromKey(String key) {
        if (key == null) {
            return Race.HUMAN;
        }
        return switch (key.toLowerCase(Locale.ROOT)) {
            case "elf" -> Race.ELF;
            case "orc" -> Race.ORC;
            case "berserker" -> Race.BERSERKER;
            default -> Race.HUMAN;
        };
    }

    public static void applyRace(Player player, Race race, PlayerRef playerRef) {
        if (player == null || race == null) {
            return;
        }

        // Apply stat bonuses
        EntityStatMap stats = EntityStatsModule.get(player); // deprecated in API, mas funcional
        if (stats == null) {
            return;
        }

        Map<String, Float> bonuses = RACE_BONUSES.getOrDefault(race, Map.of());
        applyBonus(stats, "Health", bonuses.getOrDefault("Health", 0f));
        applyBonus(stats, "Stamina", bonuses.getOrDefault("Stamina", 0f));

        stats.update();

        // Save race selection to persistent component
        if (playerRef != null) {
            saveRaceSelection(playerRef, race);
        }
    }

    /**
     * Resolve a player's race enum using persistent data (and fallback).
     */
    public static Race resolveRace(Player player) {
        return fromKey(getPlayerRace(player));
    }

    /**
     * Retorna o multiplicador de dano por arma para a raça atual do jogador.
     * Se não houver regra, retorna 1.0f.
     */
    public static float getWeaponDamageMultiplier(Player player, ItemStack weapon) {
        if (player == null || weapon == null || weapon.isEmpty()) {
            return 1.0f;
        }
        Race race = resolveRace(player);
        WeaponRule[] rules = RACE_WEAPON_RULES.get(race);
        if (rules == null || rules.length == 0) {
            return 1.0f;
        }
        for (WeaponRule rule : rules) {
            if (rule.matches(weapon)) {
                return rule.multiplier();
            }
        }
        return 1.0f;
    }

    /**
     * Saves the player's race selection to the persistent RaceData component.
     * This data will automatically persist across reconnections and server restarts.
     * 
     * @param playerRef The player reference
     * @param race The selected race
     */
    private static void saveRaceSelection(PlayerRef playerRef, Race race) {
        if (playerRef == null || race == null || raceDataType == null) {
            return;
        }

        try {
            Holder holder = playerRef.getHolder();
            if (holder == null) {
                return;
            }

            // Get or create the race data component
            RaceData raceData = (RaceData) holder.ensureAndGetComponent(raceDataType);
            
            // Set race and timestamp
            raceData.setSelectedRace(race.name());
            raceData.setSelectionTimestampLong(System.currentTimeMillis());
            
            // Save back to holder (clone for thread safety)
            holder.putComponent(raceDataType, (RaceData) raceData.clone());
            
        } catch (Exception e) {
            // If component save fails, stats are still applied
            // Player can continue playing but selection might not persist
            e.printStackTrace();
        }
    }

    /**
     * Checks if player already has a race selected.
     * 
     * New method: Checks persistent RaceData component first (most reliable).
     * Fallback: If component data doesn't exist but stats are modified,
     * infers race from stats and migrates to component system.
     * 
     * This provides backward compatibility for existing players while
     * transitioning to the new persistent component system.
     * 
     * @param player Player to check
     * @return true if race is selected, false otherwise
     */
    public static boolean hasRaceApplied(Player player) {
        if (player == null || raceDataType == null) {
            return false;
        }
        
        PlayerRef playerRef = player.getPlayerRef();
        if (playerRef == null) {
            return inferRaceFromStats(player) != null;
        }
        
        try {
            Holder holder = playerRef.getHolder();
            if (holder == null) {
                return inferRaceFromStats(player) != null;
            }

            // Check persistent component first (new method)
            RaceData raceData = (RaceData) holder.getComponent(raceDataType);
            if (raceData != null && raceData.hasSelectedRace()) {
                return true; // Race explicitly saved in component
            }

            // Fallback: Check if stats are modified (old method)
            // This handles existing players who selected race before component system
            Race inferredRace = inferRaceFromStats(player);
            if (inferredRace != null) {
                // Migrate: Save inferred race to component for future checks
                migrateToComponentSystem(playerRef, inferredRace);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            // On error, fall back to stat-based detection only
            return inferRaceFromStats(player) != null;
        }
    }

    /**
     * Attempts to infer which race was selected based on current stat values.
     * Used for migrating existing players to the new component system.
     * 
     * Detection logic:
     * - Health = 175, Stamina = 10 → Orc
     * - Health = 100, Stamina = 25 → Elf
     * - Health = 135, Stamina = 15 → Human
     * - Health = 100, Stamina = 10 → No race (base values)
     * 
     * @param player Player to check
     * @return Inferred race, or null if no race detected
     */
    private static Race inferRaceFromStats(Player player) {
        if (player == null) {
            return null;
        }
        
        try {
            EntityStatMap stats = EntityStatsModule.get(player);
            if (stats == null) {
                return null;
            }

            var healthStat = stats.get("Health");
            var staminaStat = stats.get("Stamina");
            
            if (healthStat == null || staminaStat == null) {
                return null;
            }

            float health = healthStat.getMax();
            float stamina = staminaStat.getMax();
            
            // Base values: Health=100, Stamina=10
            final float BASE_HEALTH = 100f;
            final float BASE_STAMINA = 10f;
            
            // Check exact race stat combinations
            if (Math.abs(health - 175f) < 0.1f && Math.abs(stamina - BASE_STAMINA) < 0.1f) {
                return Race.ORC; // +75 Health, +0 Stamina
            }
            
            if (Math.abs(health - BASE_HEALTH) < 0.1f && Math.abs(stamina - 25f) < 0.1f) {
                return Race.ELF; // +0 Health, +15 Stamina
            }
            
            if (Math.abs(health - 135f) < 0.1f && Math.abs(stamina - 15f) < 0.1f) {
                return Race.HUMAN; // +35 Health, +5 Stamina
            }
            
            // No race detected (base stats)
            return null;
            
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Migrates an existing player to the new component system.
     * Saves the inferred race to the persistent RaceData component.
     * 
     * @param playerRef The player reference to migrate
     * @param race The inferred race
     */
    private static void migrateToComponentSystem(PlayerRef playerRef, Race race) {
        if (playerRef == null || race == null || raceDataType == null) {
            return;
        }

        try {
            Holder holder = playerRef.getHolder();
            if (holder == null) {
                return;
            }

            // Create or update race data
            RaceData raceData = (RaceData) holder.ensureAndGetComponent(raceDataType);
            raceData.setSelectedRace(race.name());
            
            // Use current time as selection timestamp
            // (actual selection was in the past, but we don't have that data)
            raceData.setSelectionTimestampLong(System.currentTimeMillis());
            
            // Save to persistent storage
            holder.putComponent(raceDataType, (RaceData) raceData.clone());
            
        } catch (Exception e) {
            // Migration failed, but player can still play
            // Will retry on next login
            e.printStackTrace();
        }
    }

    /**
     * Gets the player's selected race from the persistent component.
     * 
     * @param player The player
     * @return The race name ("ELF", "ORC", "HUMAN"), or null if not selected
     */
    public static String getPlayerRace(Player player) {
        if (player == null || raceDataType == null) {
            return null;
        }

        PlayerRef playerRef = player.getPlayerRef();
        if (playerRef == null) {
            Race inferredRace = inferRaceFromStats(player);
            return inferredRace != null ? inferredRace.name() : null;
        }

        try {
            Holder holder = playerRef.getHolder();
            if (holder == null) {
                return null;
            }

            RaceData raceData = (RaceData) holder.getComponent(raceDataType);
            if (raceData != null) {
                return raceData.getSelectedRace();
            }

            // Fallback: Try to infer from stats
            Race inferredRace = inferRaceFromStats(player);
            return inferredRace != null ? inferredRace.name() : null;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets the player's race data component with full information.
     * Useful for accessing timestamp and other metadata.
     * 
     * @param player The player
     * @return The RaceData component, or null if not available
     */
    public static RaceData getPlayerRaceData(Player player) {
        if (player == null || raceDataType == null) {
            return null;
        }

        PlayerRef playerRef = player.getPlayerRef();
        if (playerRef == null) {
            return null;
        }

        try {
            Holder holder = playerRef.getHolder();
            if (holder == null) {
                return null;
            }

            return (RaceData) holder.getComponent(raceDataType);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets a formatted info string about the player's race selection.
     * 
     * Example: "ORC (selected 3 days ago on 2026-01-18 14:30:22)"
     * 
     * @param player The player
     * @return Formatted info string, or "No race selected" if none
     */
    public static String getPlayerRaceInfo(Player player) {
        RaceData data = getPlayerRaceData(player);
        if (data == null || !data.hasSelectedRace()) {
            return "No race selected";
        }

        String race = data.getSelectedRace();
        String date = data.getSelectionDateFormatted();
        long days = data.getDaysSinceSelection();

        if (days == 0) {
            return race + " (selected today at " + date + ")";
        } else if (days == 1) {
            return race + " (selected yesterday at " + date + ")";
        } else if (days > 0) {
            return race + " (selected " + days + " days ago on " + date + ")";
        } else {
            return race + " (selected on " + date + ")";
        }
    }

    /**
     * Resets a player's race, removing all bonuses and clearing the race data.
     * Used by the /race reset command.
     * 
     * @param player The player whose race to reset
     * @param playerRef The player reference
     * @return true if reset was successful, false otherwise
     */
    public static boolean resetRace(Player player, PlayerRef playerRef) {
        if (player == null || playerRef == null || raceDataType == null) {
            return false;
        }

        try {
            // Remove all stat bonuses
            EntityStatMap stats = EntityStatsModule.get(player);
            if (stats != null) {
                // Remove Health bonus
                var healthStat = stats.get("Health");
                if (healthStat != null) {
                    stats.removeModifier(healthStat.getIndex(), MOD_PREFIX + "Health");
                }
                
                // Remove Stamina bonus
                var staminaStat = stats.get("Stamina");
                if (staminaStat != null) {
                    stats.removeModifier(staminaStat.getIndex(), MOD_PREFIX + "Stamina");
                }
                
                stats.update();
            }

            // Clear race data component
            Holder holder = playerRef.getHolder();
            if (holder != null) {
                RaceData emptyData = new RaceData();
                holder.putComponent(raceDataType, (RaceData) emptyData.clone());
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
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

    private record WeaponRule(float multiplier, String... idFragments) {
        boolean matches(ItemStack stack) {
            String itemId = stack.getItemId();
            if (itemId != null && containsAny(itemId)) {
                return true;
            }
            var item = stack.getItem();
            if (item != null) {
                String configId = item.getId();
                if (configId != null && containsAny(configId)) {
                    return true;
                }
                String anim = item.getPlayerAnimationsId();
                if (anim != null && containsAny(anim)) {
                    return true;
                }
            }
            return false;
        }

        private boolean containsAny(String value) {
            String lower = value.toLowerCase(Locale.ROOT);
            for (String frag : idFragments) {
                if (frag == null || frag.isEmpty()) {
                    continue;
                }
                if (value.contains(frag) || lower.contains(frag.toLowerCase(Locale.ROOT))) {
                    return true;
                }
            }
            return false;
        }
    }
}
