package com.garra400.racas.ui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.garra400.racas.RaceManager;
import com.garra400.racas.i18n.TranslationManager;
import com.garra400.racas.races.RaceDefinition;
import com.garra400.racas.races.RaceRegistry;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

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

    private static final int RACES_PER_PAGE = 4;
    
    private final String selectedRace;
    private final int currentPage;
    private final List<String> allRaceIds;
    private final boolean raceOnly;
    private final String existingClass;

    /**
     * Normal constructor - after confirm, chains to ClassSelectionPage.
     * Used by /build reset and first-time race selection.
     */
    public RaceSelectionPage(@Nonnull PlayerRef playerRef) {
        this(playerRef, "elf", 0, false, null);
    }

    /**
     * Race-only constructor - after confirm, applies race directly + keeps existingClass.
     * Used by /race reset so only the race UI is shown.
     */
    public RaceSelectionPage(@Nonnull PlayerRef playerRef, boolean raceOnly, String existingClass) {
        this(playerRef, "elf", 0, raceOnly, existingClass);
    }

    public RaceSelectionPage(@Nonnull PlayerRef playerRef, String selectedRace, int page, boolean raceOnly, String existingClass) {
        super(playerRef, CustomPageLifetime.CantClose, RaceEventData.CODEC);
        this.selectedRace = selectedRace;
        this.currentPage = page;
        this.raceOnly = raceOnly;
        this.existingClass = existingClass;
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
        
        // Apply translations to static UI elements
        // Note: #Title is a Group slot in @DecoratedContainer, title text is set via @Text in the .ui file
        cmd.set("#StrengthsHeader.Text", TranslationManager.translate("ui.race_selection.strengths"));
        cmd.set("#WeaknessesHeader.Text", TranslationManager.translate("ui.race_selection.weaknesses"));
        cmd.set("#ConfirmSelection.Text", TranslationManager.translate("ui.race_selection.confirm"));
        cmd.set("#PrevPageButton.Text", TranslationManager.translate("ui.race_selection.previous"));
        cmd.set("#NextPageButton.Text", TranslationManager.translate("ui.race_selection.next"));
        
        applyRaceToUI(cmd, selectedRace);
        buildRaceButtons(cmd, evt);
        
        // Page navigation
        int totalPages = (allRaceIds.size() + RACES_PER_PAGE - 1) / RACES_PER_PAGE;
        String pageLabel = TranslationManager.translate("ui.page");
        cmd.set("#PageInfo.Text", pageLabel + " " + (currentPage + 1) + " / " + totalPages);
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
            
            int btnIndex = i - start;
            String buttonId = "#RaceButton" + btnIndex;
            
            // Priority: translation first (supports multilingual), config as fallback (new races without translations)
            com.garra400.racas.storage.config.RaceConfig btnConfig = com.garra400.racas.storage.loader.RaceConfigLoader.getConfig(raceId);
            String raceName = TranslationManager.translateOrNull("race." + raceId + ".name");
            if (raceName == null) {
                raceName = (btnConfig != null && btnConfig.displayName != null && !btnConfig.displayName.isEmpty())
                    ? btnConfig.displayName : raceId;
            }
            String raceTagline = TranslationManager.translateOrNull("race." + raceId + ".tagline");
            if (raceTagline == null) {
                raceTagline = (btnConfig != null && btnConfig.tagline != null && !btnConfig.tagline.isEmpty())
                    ? btnConfig.tagline : "";
            }
            
            cmd.appendInline("#RaceButtons", String.format("""
                Button %s {
                  Anchor: (Height: 60);
                  LayoutMode: Top;
                  Padding: (Full: 8);
                  Background: #1a1a2e(0.9);
                  Label {
                    Text: "%s";
                    Anchor: (Height: 20);
                    Style: (FontSize: 14, RenderBold: true, TextColor: #ffffff);
                  }
                  Label {
                    Text: "%s";
                    Anchor: (Height: 16);
                    Style: (FontSize: 11, TextColor: #aaaaaa);
                  }
                }
                """, buttonId, raceName.toUpperCase(), raceTagline));
            
            if (i < end - 1) {
                cmd.appendInline("#RaceButtons", "Group { Anchor: (Height: 6); }");
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
                player.getPageManager().openCustomPage(ref, store, new RaceSelectionPage(playerRef, data.race, currentPage, raceOnly, existingClass));
            }
            return;
        }
        
        if ("prevpage".equals(data.action)) {
            player.getPageManager().openCustomPage(ref, store, new RaceSelectionPage(playerRef, selectedRace, currentPage - 1, raceOnly, existingClass));
            return;
        }
        
        if ("nextpage".equals(data.action)) {
            player.getPageManager().openCustomPage(ref, store, new RaceSelectionPage(playerRef, selectedRace, currentPage + 1, raceOnly, existingClass));
            return;
        }

        if ("confirm".equals(data.action)) {
            if (raceOnly) {
                // Race-only mode: apply race directly with existing class and close
                String classToApply = (existingClass != null && !existingClass.isEmpty()) ? existingClass : "none";
                RaceManager.applyRaceAndClass(ref, store, selectedRace, classToApply);
                this.close();
            } else {
                // Normal mode: move to class selection
                player.getPageManager().openCustomPage(ref, store, new ClassSelectionPage(playerRef, selectedRace));
            }
        }
    }

    /**
     * Apply race details to UI elements using cmd.set()
     * Pattern from Tutorial3Page - set values dynamically
     * Uses TranslationManager for i18n support
     *
     * FIXED: Now loads strengths/weaknesses dynamically from RaceConfig
     * instead of hardcoding 3 strengths and 2 weaknesses
     */
    private void applyRaceToUI(UICommandBuilder cmd, String raceKey) {
        // Get race config for strengths/weaknesses and as description fallback
        com.garra400.racas.storage.config.RaceConfig config =
            com.garra400.racas.storage.loader.RaceConfigLoader.getConfig(raceKey);

        // Priority: translation first (supports multilingual), config as fallback (new races without translations)
        String raceName = TranslationManager.translateOrNull("race." + raceKey + ".name");
        if (raceName == null) {
            raceName = (config != null && config.displayName != null && !config.displayName.isEmpty())
                ? config.displayName : raceKey;
        }
        String raceTagline = TranslationManager.translateOrNull("race." + raceKey + ".tagline");
        if (raceTagline == null) {
            raceTagline = (config != null && config.tagline != null && !config.tagline.isEmpty())
                ? config.tagline : "";
        }

        cmd.set("#SelectedRaceName.Text", raceName);
        cmd.set("#SelectedRaceTagline.Text", raceTagline);

        if (config == null) return;

        // Set strengths dynamically based on config
        java.util.List<String> strengths = config.strengths != null ? config.strengths : java.util.List.of();
        for (int i = 0; i < 3; i++) {
            String text = i < strengths.size() ? "- " + strengths.get(i) : "";
            cmd.set("#PositiveLine" + (i + 1) + ".Text", text);
        }

        // Set weaknesses dynamically based on config
        java.util.List<String> weaknesses = config.weaknesses != null ? config.weaknesses : java.util.List.of();
        for (int i = 0; i < 2; i++) {
            String text = i < weaknesses.size() ? "- " + weaknesses.get(i) : "";
            cmd.set("#NegativeLine" + (i + 1) + ".Text", text);
        }
    }
}
