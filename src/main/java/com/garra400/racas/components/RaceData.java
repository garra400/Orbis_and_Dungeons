package com.garra400.racas.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/**
 * Persistent component that stores a player's selected race AND class.
 * This data is automatically saved and loaded by Hytale's persistence system.
 * 
 * Benefits:
 * - Persists across player reconnections
 * - Persists across server restarts
 * - Explicitly tracks which race + class was chosen
 * - Compatible with other mods that modify stats
 */
public class RaceData implements Component<EntityStore> {

    /**
     * CODEC defines how to serialize/deserialize this component.
     * Required for Hytale's persistence system.
     */
    public static final BuilderCodec<RaceData> CODEC;

    /**
     * The selected race: "elf", "orc", "human", etc.
     */
    private String selectedRace;

    /**
     * The selected class: "berserker", "swordsman", "none", etc.
     */
    private String selectedClass;

    /**
     * Timestamp (as string) when the race was selected.
     */
    private String selectionTimestamp;

    /**
     * Default constructor - creates empty race data.
     */
    public RaceData() {
        this.selectedRace = null;
        this.selectedClass = "none"; // Default to no class
        this.selectionTimestamp = "0";
    }

    /**
     * Gets the selected race.
     * 
     * @return Race ID or null if not selected
     */
    public String getSelectedRace() {
        return selectedRace;
    }

    /**
     * Sets the selected race.
     * 
     * @param race Race ID
     */
    public void setSelectedRace(String race) {
        this.selectedRace = race;
    }

    /**
     * Gets the selected class.
     * 
     * @return Class ID (defaults to "none")
     */
    public String getSelectedClass() {
        return selectedClass != null ? selectedClass : "none";
    }

    /**
     * Sets the selected class.
     * 
     * @param classId Class ID
     */
    public void setSelectedClass(String classId) {
        this.selectedClass = classId;
    }

    /**
     * Gets the timestamp when race was selected as a string.
     * 
     * @return Timestamp string, or "0" if never selected
     */
    public String getSelectionTimestamp() {
        return selectionTimestamp != null ? selectionTimestamp : "0";
    }

    /**
     * Sets the timestamp when race was selected.
     * 
     * @param timestamp Timestamp as string
     */
    public void setSelectionTimestamp(String timestamp) {
        this.selectionTimestamp = timestamp;
    }

    /**
     * Sets the timestamp when race was selected using a long value.
     * 
     * @param timestamp Unix timestamp in milliseconds
     */
    public void setSelectionTimestampLong(long timestamp) {
        this.selectionTimestamp = String.valueOf(timestamp);
    }

    /**
     * Gets the timestamp as a long value (Unix milliseconds).
     * 
     * @return Unix timestamp in milliseconds, or 0 if invalid
     */
    public long getSelectionTimestampLong() {
        try {
            return Long.parseLong(selectionTimestamp != null ? selectionTimestamp : "0");
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * Gets a human-readable string of when the race was selected.
     * 
     * @return Formatted date/time string
     */
    public String getSelectionDateFormatted() {
        long timestamp = getSelectionTimestampLong();
        if (timestamp == 0) {
            return "Never";
        }
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            .format(new java.util.Date(timestamp));
    }

    /**
     * Gets how long ago the race was selected (in days).
     * 
     * @return Number of days since selection, or -1 if never selected
     */
    public long getDaysSinceSelection() {
        long timestamp = getSelectionTimestampLong();
        if (timestamp == 0) {
            return -1;
        }
        long now = System.currentTimeMillis();
        return (now - timestamp) / (1000 * 60 * 60 * 24);
    }

    /**
     * Checks if a race has been selected.
     * 
     * @return true if race is selected, false otherwise
     */
    public boolean hasSelectedRace() {
        return selectedRace != null && !selectedRace.isEmpty();
    }

    /**
     * Creates a deep copy of this component.
     * Required by Hytale's component system for safe concurrent access.
     * 
     * @return A new RaceData instance with copied values
     */
    @Override
    public Component<EntityStore> clone() {
        RaceData cloned = new RaceData();
        cloned.selectedRace = this.selectedRace;
        cloned.selectedClass = this.selectedClass;
        cloned.selectionTimestamp = this.selectionTimestamp;
        return cloned;
    }

    /**
     * Static initializer that builds the CODEC for serialization.
     * 
     * This defines:
     * - How to create a new instance (RaceData::new)
     * - Which fields to save and how to serialize them
     * - How to deserialize and set the fields
     */
    static {
        CODEC = BuilderCodec.builder(RaceData.class, RaceData::new)
            // Serialize selectedRace field as a string with key "SelectedRace"
            .append(
                new KeyedCodec<>("SelectedRace", Codec.STRING),
                RaceData::setSelectedRace,    // Setter method reference
                RaceData::getSelectedRace     // Getter method reference
            ).add()
            // Serialize selectedClass field
            .append(
                new KeyedCodec<>("SelectedClass", Codec.STRING),
                RaceData::setSelectedClass,
                RaceData::getSelectedClass
            ).add()
            // Serialize selectionTimestamp as string
            .append(
                new KeyedCodec<>("SelectionTimestamp", Codec.STRING),
                RaceData::setSelectionTimestamp,
                RaceData::getSelectionTimestamp
            ).add()
            .build();
    }

    @Override
    public String toString() {
        return "RaceData{" +
                "selectedRace='" + selectedRace + '\'' +
                ", selectedClass='" + selectedClass + '\'' +
                ", selectionTimestamp=" + selectionTimestamp +
                '}';
    }
}
