package com.garra400.racas.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Manages loading and saving race configurations from/to JSON.
 */
public final class RaceConfigLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path configFile;
    private static Map<String, RaceConfig> configs = new LinkedHashMap<>();

    private RaceConfigLoader() {
    }

    public static void init(Path dataDir) {
        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            configFile = dataDir.resolve("races_config.json");
            
            if (Files.exists(configFile)) {
                load();
            } else {
                generateDefaultConfig();
                save();
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize race config: " + e.getMessage());
            generateDefaultConfig();
        }
    }

    private static void load() throws IOException {
        String json = Files.readString(configFile, StandardCharsets.UTF_8);
        Type listType = new TypeToken<List<RaceConfig>>(){}.getType();
        List<RaceConfig> list = GSON.fromJson(json, listType);
        
        configs.clear();
        if (list != null) {
            for (RaceConfig config : list) {
                configs.put(config.id, config);
            }
        }
    }

    private static void save() throws IOException {
        List<RaceConfig> list = new ArrayList<>(configs.values());
        String json = GSON.toJson(list);
        Files.writeString(configFile, json, StandardCharsets.UTF_8);
    }

    private static void generateDefaultConfig() {
        configs.clear();
        
        // Elf - High stamina, mobility focus
        configs.put("elf", new RaceConfig(
            "elf",
            "Elf",
            "Agile and tireless, moves like the wind.",
            0f,
            15f,
            List.of("100 HP (base)", "25 Stamina (+15)", "Extended mobility"),
            List.of("No health bonus", "Stamina-dependent playstyle"),
            List.of()
        ));

        // Orc - Maximum health, tank
        configs.put("orc", new RaceConfig(
            "orc",
            "Orc",
            "Brutish and relentless, crushes all opposition.",
            75f,
            0f,
            List.of("175 HP (+75)", "Massive health pool", "Tank role"),
            List.of("10 Stamina (base)", "Limited mobility"),
            List.of()
        ));

        // Human - Balanced baseline
        configs.put("human", new RaceConfig(
            "human",
            "Human",
            "Balanced and adaptable, jack of all trades.",
            35f,
            5f,
            List.of("135 HP (+35)", "15 Stamina (+5)", "Well-rounded stats"),
            List.of("No specialization", "Average at everything"),
            List.of()
        ));
    }

    public static RaceConfig getConfig(String raceId) {
        return configs.get(raceId);
    }

    public static Collection<RaceConfig> getAllConfigs() {
        return configs.values();
    }

    public static boolean hasConfig(String raceId) {
        return configs.containsKey(raceId);
    }

    public static void reload() {
        try {
            if (configFile != null && Files.exists(configFile)) {
                load();
            }
        } catch (IOException e) {
            System.err.println("Failed to reload race config: " + e.getMessage());
        }
    }
}
