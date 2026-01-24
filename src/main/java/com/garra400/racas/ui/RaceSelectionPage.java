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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Race Selection Page - Interactive UI for choosing player race
 * Uses pagination with 4 races per page
 */
public class RaceSelectionPage extends InteractiveCustomUIPage<RaceSelectionPage.RaceEventData> {

    public static class RaceEventData {
        public String action;
        public String race;

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
    private static final int RACES_PER_PAGE = 4;
    
    private final String selectedRace;
    private final int currentPage;
    private final List<String> allRaceIds;

    public RaceSelectionPage(@Nonnull PlayerRef playerRef) {
        this(playerRef, "elf", 0);
    }

    public RaceSelectionPage(@Nonnull PlayerRef playerRef, String selectedRace, int page) {
        super(playerRef, CustomPageLifetime.CantClose, RaceEventData.CODEC);
        this.selectedRace = selectedRace;
        this.currentPage = page;
        // Build race list dynamically from registry
        this.allRaceIds = new ArrayList<>();
        for (RaceDefinition race : RaceRegistry.all()) {
            allRaceIds.add(race.id());
        }
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder cmd,
            @Nonnull UIEventBuilder evt,
            @Nonnull Store<EntityStore> store
    ) {
        cmd.append("Pages/race_selection.ui");
        applyRaceToUI(cmd, selectedRace);
        buildRaceButtons(cmd, evt);
        
        // Page navigation
        int totalPages = (allRaceIds.size() + RACES_PER_PAGE - 1) / RACES_PER_PAGE;
        cmd.set("#PageInfo.Text", "Page " + (currentPage + 1) + " / " + totalPages);
        cmd.set("#PrevPageButton.Visible", currentPage > 0);
        cmd.set("#NextPageButton.Visible", currentPage < totalPages - 1);
        
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#PrevPageButton", 
                new EventData().append("Action", "prevpage"));
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#NextPageButton", 
                new EventData().append("Action", "nextpage"));
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ConfirmSelection", 
                new EventData().append("Action", "confirm"));
    }
    
    private void buildRaceButtons(UICommandBuilder cmd, UIEventBuilder evt) {
        cmd.clear("#RaceListPanel");
        cmd.appendInline("#RaceListPanel", "Group #RaceButtons { LayoutMode: Top; }");
        
        int start = currentPage * RACES_PER_PAGE;
        int end = Math.min(start + RACES_PER_PAGE, allRaceIds.size());
        
        for (int i = start; i < end; i++) {
            String raceId = allRaceIds.get(i);
            RaceDetails details = RACES.get(raceId);
            if (details == null) continue;
            
            int btnIndex = i - start;
            String buttonId = "#RaceButton" + btnIndex;
            
            cmd.appendInline("#RaceButtons", String.format("""
                Button %s {
                  Anchor: (Width: 280, Height: 80);
                  LayoutMode: Top;
                  Padding: (Full: 10);
                  Background: #0f0f0f(0.9);
                  Label {
                    Text: "%s";
                    Anchor: (Height: 22);
                    Style: (FontSize: 16, RenderBold: true, TextColor: #ffffff);
                  }
                  Label {
                    Text: "%s";
                    Anchor: (Height: 18);
                    Style: (FontSize: 12, TextColor: #c0c0c0);
                  }
                }
                """, buttonId, details.title.toUpperCase(), details.tagline));
            
            if (i < end - 1) {
                cmd.appendInline("#RaceButtons", "Group { Anchor: (Height: 10); }");
            }
            
            evt.addEventBinding(CustomUIEventBindingType.Activating, 
                    buttonId, 
                    new EventData().append("Action", "select").append("Race", raceId));
        }
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull RaceEventData data
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());
        
        if ("select".equals(data.action)) {
            if (data.race != null && RaceRegistry.exists(data.race)) {
                player.getPageManager().openCustomPage(ref, store, new RaceSelectionPage(playerRef, data.race, currentPage));
            }
            return;
        }
        
        if ("prevpage".equals(data.action)) {
            player.getPageManager().openCustomPage(ref, store, new RaceSelectionPage(playerRef, selectedRace, currentPage - 1));
            return;
        }
        
        if ("nextpage".equals(data.action)) {
            player.getPageManager().openCustomPage(ref, store, new RaceSelectionPage(playerRef, selectedRace, currentPage + 1));
            return;
        }

        if ("confirm".equals(data.action)) {
            try {
                RaceManager.applyRace(player, selectedRace, playerRef);
            } catch (Exception e) {
                // Silently fail
            }
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
