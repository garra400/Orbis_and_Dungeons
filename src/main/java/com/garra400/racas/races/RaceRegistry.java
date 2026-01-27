package com.garra400.racas.races;

import com.garra400.racas.storage.config.RaceConfig;
import com.garra400.racas.storage.loader.RaceConfigLoader;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RaceRegistry {
    private static final Map<String, RaceDefinition> DEFINITIONS = new LinkedHashMap<>();
    private static final String DEFAULT_ID = "human";

    private RaceRegistry() {}

    /**
     * Loads all races from JSON configuration.
     * Should be called after RaceConfigLoader.init()
     */
    public static void loadFromConfig() {
        DEFINITIONS.clear();
        
        for (RaceConfig config : RaceConfigLoader.getAllConfigs()) {
            register(new ConfigurableRace(config));
        }
        
        // Fallback to hardcoded races if config is empty
        if (DEFINITIONS.isEmpty()) {
            System.err.println("No races loaded from config, using fallback");
            register(new ElfRace());
            register(new OrcRace());
            register(new HumanRace());
            register(new DwarfRace());
        }
    }

    public static void register(RaceDefinition definition) {
        if (definition == null || definition.id() == null) {
            return;
        }
        DEFINITIONS.put(definition.id().toLowerCase(), definition);
    }

    public static RaceDefinition get(String id) {
        if (id == null) {
            return getDefault();
        }
        return DEFINITIONS.getOrDefault(id.toLowerCase(), getDefault());
    }

    public static RaceDefinition getDefault() {
        return DEFINITIONS.getOrDefault(DEFAULT_ID, DEFINITIONS.values().stream().findFirst().orElse(null));
    }

    public static Collection<RaceDefinition> all() {
        return Collections.unmodifiableCollection(DEFINITIONS.values());
    }

    public static boolean exists(String id) {
        return id != null && DEFINITIONS.containsKey(id.toLowerCase());
    }
    
    /**
     * Reloads all races from the JSON configuration file.
     * Useful for applying config changes without restart.
     */
    public static void reload() {
        RaceConfigLoader.reload();
        loadFromConfig();
    }
}
