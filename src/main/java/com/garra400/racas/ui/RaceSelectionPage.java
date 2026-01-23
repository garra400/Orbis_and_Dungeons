package com.garra400.racas.ui;

import com.garra400.racas.RaceManager;
import com.garra400.racas.races.RaceDefinition;
import com.garra400.racas.races.RaceRegistry;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Race Selection Page - Interactive UI for choosing player race
 * 
 * Uses InteractiveCustomUIPage pattern (like Tutorial2/3 examples):
 * - EventData class with BuilderCodec for type-safe event handling
 * - No sendUpdate() calls - UI is set once in build()
 * - Events trigger handleDataEvent() which processes and closes page
 */
public class RaceSelectionPage extends InteractiveCustomUIPage<RaceSelectionPage.RaceEventData> {

    /**
     * EventData class - receives button click data
     * 
     * Fields:
     * - action: Which button was clicked ("select" or "confirm")
     * - race: Which race was selected ("elf", "orc", "human")
     */
    public static class RaceEventData {
        public String action;
        public String race;

        /**
         * Codec for serializing/deserializing event data
         * Pattern from Tutorial2Page.java - defines how to map JSON to fields
         */
        public static final BuilderCodec<RaceEventData> CODEC = 
            BuilderCodec.builder(RaceEventData.class, RaceEventData::new)
                .append(
                    new KeyedCodec<>("Action", Codec.STRING),
                    (RaceEventData o, String v) -> o.action = v,
                    (RaceEventData o) -> o.action
                )
                .add()
                .append(
                    new KeyedCodec<>("Race", Codec.STRING),
                    (RaceEventData o, String v) -> o.race = v,
                    (RaceEventData o) -> o.race
                )
                .add()
                .build();
    }

    private static final Map<String, RaceDetails> RACES = buildRaceDetails();
    private final String selectedRace;

    public RaceSelectionPage(@Nonnull PlayerRef playerRef) {
        this(playerRef, "elf");
    }

    public RaceSelectionPage(@Nonnull PlayerRef playerRef, String selectedRace) {
        // Pass CODEC to parent - this enables typed event handling
        super(playerRef, CustomPageLifetime.CantClose, RaceEventData.CODEC);
        this.selectedRace = selectedRace;
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder cmd,
            @Nonnull UIEventBuilder evt,
            @Nonnull Store<EntityStore> store
    ) {
        // Load UI layout
        cmd.append("Pages/race_selection.ui");

        // Set initial values - Tutorial3Page pattern
        applyRaceToUI(cmd, selectedRace);

        // Bind race selection buttons - Tutorial2Page pattern
        // Each button sends action="select" + race="racename"
        evt.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#RaceButtonElf",
                new EventData().append("Action", "select").append("Race", "elf")
        );
        evt.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#RaceButtonOrc",
                new EventData().append("Action", "select").append("Race", "orc")
        );
        evt.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#RaceButtonBerserker",
                new EventData().append("Action", "select").append("Race", "berserker")
        );
        evt.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#RaceButtonHuman",
                new EventData().append("Action", "select").append("Race", "human")
        );

        // Bind confirm button
        evt.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#ConfirmSelection",
                new EventData().append("Action", "confirm")
        );
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull RaceEventData data
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());
        
        // Handle button clicks - FormPage pattern
        if ("select".equals(data.action)) {
            // Race selection button clicked
            if (data.race != null && RaceRegistry.exists(data.race)) {
                // Reopen directly with new selection (without closing first)
                player.getPageManager().openCustomPage(ref, store, new RaceSelectionPage(playerRef, data.race));
            }
            return;
        }

        if ("confirm".equals(data.action)) {
            // Apply the selected race
            try {
                RaceManager.applyRace(player, selectedRace, playerRef);
            } catch (Exception e) {
                // Silently fail
            }
            
            // Close the page - Tutorial2/3 pattern
            player.getPageManager().setPage(ref, store, Page.None);
        }
    }

    /**
     * Apply race details to UI elements using cmd.set()
     * Pattern from Tutorial3Page - set values dynamically
     */
    private void applyRaceToUI(UICommandBuilder cmd, String raceKey) {
        RaceDetails details = RACES.getOrDefault(raceKey, RACES.get("human"));
        if (details == null) {
            details = RACES.get("human");
        }
        
        cmd.set("#SelectedRaceName.Text", details.title);
        cmd.set("#SelectedRaceTagline.Text", details.tagline);

        setListLine(cmd, "#PositiveLine1", details.positives, 0);
        setListLine(cmd, "#PositiveLine2", details.positives, 1);
        setListLine(cmd, "#PositiveLine3", details.positives, 2);

        setListLine(cmd, "#NegativeLine1", details.negatives, 0);
        setListLine(cmd, "#NegativeLine2", details.negatives, 1);
    }

    private void setListLine(UICommandBuilder cmd, String elementId, List<String> lines, int index) {
        String value = index < lines.size() ? lines.get(index) : "";
        cmd.set(elementId + ".Text", value);
    }

    private record RaceDetails(String title, String tagline, List<String> positives, List<String> negatives) {}

    private static Map<String, RaceDetails> buildRaceDetails() {
        Map<String, RaceDetails> map = new LinkedHashMap<>();
        for (RaceDefinition def : RaceRegistry.all()) {
            map.put(def.id(), new RaceDetails(
                    def.displayName(),
                    def.tagline(),
                    def.strengths(),
                    def.weaknesses()
            ));
        }
        return Collections.unmodifiableMap(map);
    }
}
