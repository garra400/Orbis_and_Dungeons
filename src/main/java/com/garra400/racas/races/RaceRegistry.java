package com.garra400.racas.races;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RaceRegistry {
    private static final Map<String, RaceDefinition> DEFINITIONS = new LinkedHashMap<>();
    private static final String DEFAULT_ID = "human";

    static {
        register(new ElfRace());
        register(new OrcRace());
        register(new HumanRace());
        register(new BerserkerRace());
        register(new SwordsmanRace());
        register(new CrusaderRace());
        register(new AssassinRace());
        register(new ArcherRace());
    }

    private RaceRegistry() {}

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
}
