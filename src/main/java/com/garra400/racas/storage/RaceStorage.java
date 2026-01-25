package com.garra400.racas.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * File-backed cache of player races and classes.
 * Format: one entry per line -> uuid|username|raceId|classId
 */
public final class RaceStorage {

    private static final Map<UUID, Entry> CACHE = new ConcurrentHashMap<>();
    private static Path storageFile;

    private RaceStorage() {
    }

    public static void init(Path dataDir) {
        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            storageFile = dataDir.resolve("race_cache.txt");
            load();
        } catch (IOException e) {
            // ignore; will rebuild on next save
        }
    }

    private static void load() {
        if (storageFile == null || !Files.exists(storageFile)) {
            return;
        }
        try {
            for (String line : Files.readAllLines(storageFile, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\\|", 4);
                if (parts.length < 3) {
                    continue;
                }
                UUID uuid = UUID.fromString(parts[0]);
                String name = parts[1];
                String race = parts[2];
                String classId = parts.length >= 4 ? parts[3] : "none";
                CACHE.put(uuid, new Entry(uuid, name, race, classId));
            }
        } catch (Exception e) {
            // ignore corrupt file
        }
    }

    public static void save() {
        if (storageFile == null) {
            return;
        }
        try {
            StringBuilder sb = new StringBuilder();
            for (Entry entry : CACHE.values()) {
                if (entry.raceId == null) {
                    continue;
                }
                sb.append(entry.uuid.toString())
                  .append("|").append(entry.username == null ? "" : entry.username)
                  .append("|").append(entry.raceId)
                  .append("|").append(entry.classId == null ? "none" : entry.classId)
                  .append("\n");
            }
            Files.writeString(storageFile, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            // ignore write errors
        }
    }

    public static void put(UUID uuid, String username, String raceId) {
        if (uuid == null || raceId == null) {
            return;
        }
        Entry existing = CACHE.get(uuid);
        String classId = (existing != null && existing.classId != null) ? existing.classId : "none";
        CACHE.put(uuid, new Entry(uuid, username, raceId, classId));
        save();
    }

    public static void putRaceAndClass(UUID uuid, String username, String raceId, String classId) {
        if (uuid == null || raceId == null || classId == null) {
            return;
        }
        CACHE.put(uuid, new Entry(uuid, username, raceId, classId));
        save();
    }

    public static String get(UUID uuid) {
        Entry e = CACHE.get(uuid);
        return e != null ? e.raceId : null;
    }

    public static String getPlayerClass(UUID uuid) {
        Entry e = CACHE.get(uuid);
        return e != null ? e.classId : "none";
    }

    public static String getByName(String username) {
        if (username == null) {
            return null;
        }
        for (Entry e : CACHE.values()) {
            if (e.username != null && e.username.equalsIgnoreCase(username)) {
                return e.raceId;
            }
        }
        return null;
    }

    public static void remove(UUID uuid) {
        if (uuid == null) {
            return;
        }
        CACHE.remove(uuid);
        save();
    }

    public record Entry(UUID uuid, String username, String raceId, String classId) {}
}
