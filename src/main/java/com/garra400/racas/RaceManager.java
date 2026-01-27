package com.garra400.racas;

import com.garra400.racas.components.RaceData;
import com.garra400.racas.races.RaceDefinition;
import com.garra400.racas.races.RaceRegistry;
import com.garra400.racas.storage.config.ClassConfig;
import com.garra400.racas.storage.loader.ClassConfigLoader;
import com.garra400.racas.storage.config.RaceConfig;
import com.garra400.racas.storage.loader.RaceConfigLoader;
import com.garra400.racas.storage.RaceStorage;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Race management using modular RaceDefinition files.
 * To add a new race:
 * 1) Create a class implementing RaceDefinition (e.g., in com.garra400.racas.races).
 * 2) Register it in RaceRegistry.register(...) (static block).
 */
public final class RaceManager {

    private static final String MOD_PREFIX = "race_mod_";
    private static ComponentType<EntityStore, RaceData> raceDataType;
    private static final Map<UUID, String> LAST_KNOWN_RACE = new ConcurrentHashMap<>();

    private RaceManager() {}

    /**
    * Sets the component type for race data access.
    * Called by RaceMod during initialization.
    */
    public static void setRaceDataType(ComponentType<EntityStore, RaceData> type) {
        raceDataType = type;
    }

    public static RaceDefinition fromKey(String key) {
        return RaceRegistry.get(key);
    }

    public static void applyRace(Player player, String raceId, PlayerRef playerRef) {
        if (player == null || raceId == null) {
            return;
        }
        applyRaceStats(player, raceId);
        if (playerRef != null) {
            saveRaceSelection(playerRef, raceId, true);
        }
        cacheRace(player, raceId);
        String username = null;
        try {
            PlayerRef ref = player.getPlayerRef();
            if (ref != null) {
                username = ref.getUsername();
            }
        } catch (Exception ignored) {
        }
        RaceStorage.put(player.getUuid(), username, raceId);
    }

    /**
     * Reaplica apenas os bônus de stats sem alterar o timestamp salvo.
     */
    public static void applyRaceStats(Player player, String raceId) {
        if (player == null || raceId == null) {
            return;
        }
        RaceDefinition race = RaceRegistry.get(raceId);
        EntityStatMap stats = EntityStatsModule.get(player); // deprecated em API, mas funcional
        if (stats != null) {
            applyBonus(stats, "Health", race.healthBonus());
            applyBonus(stats, "Stamina", race.staminaBonus());
            stats.update();
        }
    }

    /**
     * Aplica raça e classe ao jogador, combinando os bônus de ambos.
     * Versão que usa Ref e Store para salvar componentes (UI context).
     */
    public static void applyRaceAndClass(Ref<EntityStore> ref, Store<EntityStore> store, String raceId, String classId) {
        if (ref == null || store == null || raceId == null || classId == null) {
            System.err.println("applyRaceAndClass: Null parameter - ref=" + ref + ", store=" + store + ", raceId=" + raceId + ", classId=" + classId);
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            System.err.println("applyRaceAndClass: Player component not found");
            return;
        }

        System.out.println("applyRaceAndClass: Applying race=" + raceId + ", class=" + classId);

        RaceDefinition race = RaceRegistry.get(raceId);
        ClassConfig classConfig = ClassConfigLoader.getClass(classId);

        if (classConfig == null) {
            System.err.println("applyRaceAndClass: ClassConfig not found for classId=" + classId);
            return;
        }

        System.out.println("applyRaceAndClass: Found race=" + race.displayName() + ", class=" + classConfig.displayName);

        // Combina os bônus de raça e classe
        int totalHealthBonus = Math.round(race.healthBonus() + classConfig.healthModifier);
        int totalStaminaBonus = Math.round(race.staminaBonus() + classConfig.staminaModifier);
        int totalManaBonus = Math.round(classConfig.manaModifier); // Mana é apenas da classe

        // Aplica os stats combinados
        EntityStatMap stats = EntityStatsModule.get(player);
        if (stats != null) {
            applyBonus(stats, "Health", totalHealthBonus);
            applyBonus(stats, "Stamina", totalStaminaBonus);
            applyBonus(stats, "Mana", totalManaBonus); // Aplica modificador de mana
            stats.update();
        }

        // Salva a seleção de raça e classe usando Store
        saveRaceAndClassSelection(ref, store, raceId, classId, true);

        // Atualiza o cache
        cacheRace(player, raceId);
        String username = null;
        try {
            PlayerRef playerRef = player.getPlayerRef();
            if (playerRef != null) {
                username = playerRef.getUsername();
            }
        } catch (Exception ignored) {
        }
        RaceStorage.putRaceAndClass(player.getUuid(), username, raceId, classId);
    }

    /**
     * Aplica raça e classe ao jogador, combinando os bônus de ambos.
     * Versão legada que usa Player (comandos).
     */
    public static void applyRaceAndClass(Player player, String raceId, String classId) {
        if (player == null || raceId == null || classId == null) {
            System.err.println("applyRaceAndClass: Null parameter - player=" + player + ", raceId=" + raceId + ", classId=" + classId);
            return;
        }

        System.out.println("applyRaceAndClass (command): Applying race=" + raceId + ", class=" + classId);

        RaceDefinition race = RaceRegistry.get(raceId);
        ClassConfig classConfig = ClassConfigLoader.getClass(classId);

        if (classConfig == null) {
            System.err.println("applyRaceAndClass: ClassConfig not found for classId=" + classId);
            return;
        }

        System.out.println("applyRaceAndClass (command): Found race=" + race.displayName() + ", class=" + classConfig.displayName);

        // Combina os bônus de raça e classe
        int totalHealthBonus = Math.round(race.healthBonus() + classConfig.healthModifier);
        int totalStaminaBonus = Math.round(race.staminaBonus() + classConfig.staminaModifier);

        // Aplica os stats combinados
        EntityStatMap stats = EntityStatsModule.get(player);
        if (stats != null) {
            applyBonus(stats, "Health", totalHealthBonus);
            applyBonus(stats, "Stamina", totalStaminaBonus);
            stats.update();
        }

        // Atualiza o cache e storage
        cacheRace(player, raceId);
        String username = null;
        try {
            PlayerRef playerRefComponent = player.getPlayerRef();
            if (playerRefComponent != null) {
                username = playerRefComponent.getUsername();
                
                // Tenta salvar em componentes usando o World
                World world = player.getWorld();
                if (world != null) {
                    world.execute(() -> {
                        var store = world.getEntityStore().getStore();
                        var ref = playerRefComponent.getReference();
                        if (ref != null && ref.isValid()) {
                            saveRaceAndClassSelection(ref, store, raceId, classId, true);
                        }
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("applyRaceAndClass (command): Failed to save to component - " + e.getMessage());
        }
        RaceStorage.putRaceAndClass(player.getUuid(), username, raceId, classId);
    }

    private static void saveRaceSelection(PlayerRef playerRef, String raceId, boolean updateTimestamp) {
        if (playerRef == null || raceId == null || raceDataType == null) {
            return;
        }

        try {
            Holder holder = playerRef.getHolder();
            if (holder == null) {
                return;
            }

            RaceData raceData = (RaceData) holder.ensureAndGetComponent(raceDataType);
            raceData.setSelectedRace(raceId);
            if (updateTimestamp) {
                raceData.setSelectionTimestampLong(System.currentTimeMillis());
            }
            holder.putComponent(raceDataType, (RaceData) raceData.clone());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveRaceAndClassSelection(Ref<EntityStore> ref, Store<EntityStore> store, String raceId, String classId, boolean updateTimestamp) {
        if (ref == null || store == null || raceId == null || classId == null || raceDataType == null) {
            System.err.println("saveRaceAndClassSelection: Null parameter - ref=" + ref + ", store=" + store + ", raceId=" + raceId + ", classId=" + classId);
            return;
        }

        System.out.println("saveRaceAndClassSelection: Saving race=" + raceId + ", class=" + classId);

        try {
            RaceData raceData = store.getComponent(ref, raceDataType);
            if (raceData == null) {
                raceData = new RaceData();
            }
            raceData.setSelectedRace(raceId);
            raceData.setSelectedClass(classId);
            System.out.println("saveRaceAndClassSelection: Set race=" + raceData.getSelectedRace() + ", class=" + raceData.getSelectedClass());
            if (updateTimestamp) {
                raceData.setSelectionTimestampLong(System.currentTimeMillis());
            }
            store.putComponent(ref, raceDataType, raceData);
            System.out.println("saveRaceAndClassSelection: Component saved successfully");
        } catch (Exception e) {
            System.err.println("saveRaceAndClassSelection: Exception - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean hasRaceApplied(Player player) {
        return getPlayerRace(player) != null;
    }

    public static String getPlayerRace(Player player) {
        if (player == null || raceDataType == null) {
            return null;
        }

        PlayerRef playerRef = player.getPlayerRef();
        if (playerRef == null) {
            // fallback para cache em memória
            String cached = cacheLookup(player);
            if (cached != null) {
                return cached;
            }
            return inferRaceFromStats(player);
        }

        try {
            Holder holder = playerRef.getHolder();
            if (holder != null) {
                RaceData raceData = (RaceData) holder.ensureAndGetComponent(raceDataType);
                if (raceData != null && raceData.hasSelectedRace()) {
                    return raceData.getSelectedRace();
                }
            }
            String cached = cacheLookup(playerRef);
            if (cached != null) {
                saveRaceSelection(playerRef, cached, false);
                return cached;
            }
            String storageRace = RaceStorage.get(playerRef.getUuid());
            if (storageRace != null) {
                cacheRace(playerRef, storageRace);
                saveRaceSelection(playerRef, storageRace, false);
                return storageRace;
            }
            // Não inferir automaticamente se não há seleção persistida.
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getPlayerClass(Player player) {
        if (player == null) {
            System.err.println("getPlayerClass: player is null");
            return "none";
        }

        try {
            // Usa RaceStorage como fonte primária (foi salvo via store.putComponent)
            String classId = RaceStorage.getPlayerClass(player.getUuid());
            if (classId != null && !classId.equals("none")) {
                System.out.println("getPlayerClass: Retrieved class=" + classId + " from storage");
                return classId;
            }
            return "none";
        } catch (Exception e) {
            System.err.println("getPlayerClass: Exception - " + e.getMessage());
            e.printStackTrace();
            return "none";
        }
    }

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

            return (RaceData) holder.ensureAndGetComponent(raceDataType);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getPlayerRaceInfo(Player player) {
        String raceId = getPlayerRace(player);
        String classId = getPlayerClass(player);
        RaceData data = getPlayerRaceData(player);
        return formatRaceInfo(raceId, classId, data);
    }

    public static String formatRaceInfo(String raceId, RaceData data) {
        String classId = (data != null) ? data.getSelectedClass() : null;
        return formatRaceInfo(raceId, classId, data);
    }

    public static String formatRaceInfo(String raceId, String classId, RaceData data) {
        if (raceId == null) {
            return "No race selected";
        }

        RaceDefinition def = RaceRegistry.get(raceId);
        String raceName = def.displayName();
        
        String className = "None";
        if (classId != null && !classId.equals("none")) {
            ClassConfig classConfig = ClassConfigLoader.getClass(classId);
            if (classConfig != null) {
                className = classConfig.displayName;
            }
        }

        String raceAndClass = raceName + " - " + className;

        if (data == null || !data.hasSelectedRace() || data.getSelectionTimestampLong() == 0L) {
            return raceAndClass;
        }

        String date = data.getSelectionDateFormatted();
        long days = data.getDaysSinceSelection();

        if (days == 0) {
            return raceAndClass + " (selected today at " + date + ")";
        } else if (days == 1) {
            return raceAndClass + " (selected yesterday at " + date + ")";
        } else if (days > 0) {
            return raceAndClass + " (selected " + days + " days ago on " + date + ")";
        } else {
            return raceAndClass + " (selected on " + date + ")";
        }
    }

    public static String getStoredRace(PlayerRef playerRef) {
        if (playerRef == null) {
            return null;
        }
        String cached = cacheLookup(playerRef);
        if (cached != null) {
            return cached;
        }
        String storageRace = RaceStorage.get(playerRef.getUuid());
        if (storageRace == null && playerRef.getUsername() != null) {
            storageRace = RaceStorage.getByName(playerRef.getUsername());
        }
        if (storageRace != null) {
            cacheRace(playerRef, storageRace);
        }
        return storageRace;
    }

    public static String getStoredRaceByName(String username) {
        if (username == null) {
            return null;
        }
        return RaceStorage.getByName(username);
    }

    public static boolean resetRace(Player player, PlayerRef playerRef) {
        if (player == null || playerRef == null || raceDataType == null) {
            return false;
        }

        try {
            EntityStatMap stats = EntityStatsModule.get(player);
            if (stats != null) {
                var healthStat = stats.get("Health");
                if (healthStat != null) {
                    stats.removeModifier(healthStat.getIndex(), MOD_PREFIX + "Health");
                }

                var staminaStat = stats.get("Stamina");
                if (staminaStat != null) {
                    stats.removeModifier(staminaStat.getIndex(), MOD_PREFIX + "Stamina");
                }

                stats.update();
            }

            Holder holder = playerRef.getHolder();
            if (holder != null) {
                RaceData emptyData = new RaceData();
                holder.putComponent(raceDataType, (RaceData) emptyData.clone());
            }
            try {
                UUID uuid = player.getUuid();
                if (uuid != null) {
                    LAST_KNOWN_RACE.remove(uuid);
                    RaceStorage.remove(uuid);
                }
            } catch (Exception ignored) {
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static float getWeaponDamageMultiplier(Player player, ItemStack weapon) {
        String classId = getPlayerClass(player);
        if (classId == null || classId.equals("none")) {
            return 1.0f;
        }
        
        ClassConfig classConfig = ClassConfigLoader.getClass(classId);
        if (classConfig == null || classConfig.weapons == null) {
            return 1.0f;
        }

        String weaponId = weapon.getItem().getId().toString().toLowerCase();
        for (ClassConfig.WeaponConfig weaponConfig : classConfig.weapons) {
            if (weaponConfig.types != null) {
                for (String type : weaponConfig.types) {
                    if (weaponId.contains(type.toLowerCase())) {
                        return weaponConfig.damageMultiplier;
                    }
                }
            }
        }
        return 1.0f;
    }

    public static float getWeaponDamageMultiplier(String raceId, ItemStack weapon) {
        // Deprecated: Use getWeaponDamageMultiplier(Player, ItemStack) instead
        RaceDefinition race = RaceRegistry.get(raceId);
        return race.resolveWeaponMultiplier(weapon);
    }

    private static void cacheRace(Player player, String raceId) {
        try {
            if (player != null && raceId != null) {
                UUID uuid = player.getUuid();
                if (uuid != null) {
                    LAST_KNOWN_RACE.put(uuid, raceId);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static void cacheRace(PlayerRef ref, String raceId) {
        try {
            if (ref != null && raceId != null) {
                UUID uuid = ref.getUuid();
                if (uuid != null) {
                    LAST_KNOWN_RACE.put(uuid, raceId);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static String cacheLookup(Player player) {
        if (player == null) {
            return null;
        }
        try {
            UUID uuid = player.getUuid();
            if (uuid != null) {
                return LAST_KNOWN_RACE.get(uuid);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static String cacheLookup(PlayerRef ref) {
        if (ref == null) {
            return null;
        }
        try {
            UUID uuid = ref.getUuid();
            if (uuid != null) {
                return LAST_KNOWN_RACE.get(uuid);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    // --- Helpers ---

    /**
     * Gets the damage resistance multiplier for a specific damage type.
     * Combines resistances from both race and class - the best (lowest) resistance applies.
     * 
     * @param player The player
     * @param damageType The damage type ID (e.g., "Fire", "Physical", "Magic")
     * @return Resistance multiplier (0.0 = immune, 0.5 = 50% reduction, 1.0 = normal, 1.5 = 50% weakness)
     */
    public static float getDamageResistance(Player player, String damageType) {
        if (player == null || damageType == null) {
            return 1.0f; // No resistance
        }

        String raceId = getPlayerRace(player);
        String classId = getPlayerClass(player);

        float finalResistance = 1.0f; // Default: no resistance

        // Get race resistance
        if (raceId != null) {
            RaceConfig raceConfig = RaceConfigLoader.getConfig(raceId);
            if (raceConfig != null && raceConfig.damageResistances != null) {
                float raceResistance = raceConfig.damageResistances.getOrDefault(damageType, 1.0f);
                finalResistance = Math.min(finalResistance, raceResistance); // Use best resistance
            }
        }

        // Get class resistance
        if (classId != null && !classId.equals("none")) {
            ClassConfig classConfig = ClassConfigLoader.getClass(classId);
            if (classConfig != null && classConfig.damageResistances != null) {
                float classResistance = classConfig.damageResistances.getOrDefault(damageType, 1.0f);
                finalResistance = Math.min(finalResistance, classResistance); // Use best resistance
            }
        }

        return finalResistance;
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

    private static String inferRaceFromStats(Player player) {
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

            if (Math.abs(health - 175f) < 0.1f && Math.abs(stamina - BASE_STAMINA) < 0.1f) {
                return "orc";
            }

            if (Math.abs(health - BASE_HEALTH) < 0.1f && Math.abs(stamina - 25f) < 0.1f) {
                return "elf";
            }

            if (Math.abs(health - 135f) < 0.1f && Math.abs(stamina - 15f) < 0.1f) {
                return "human";
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static void migrateToComponentSystem(PlayerRef playerRef, String raceId) {
        if (playerRef == null || raceId == null || raceDataType == null) {
            return;
        }

        try {
            Holder holder = playerRef.getHolder();
            if (holder == null) {
                return;
            }

            RaceData raceData = (RaceData) holder.ensureAndGetComponent(raceDataType);
            raceData.setSelectedRace(raceId);
            raceData.setSelectionTimestampLong(System.currentTimeMillis());
            holder.putComponent(raceDataType, (RaceData) raceData.clone());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
