package com.garra400.racas.ui;

import com.garra400.racas.RaceManager;
import com.garra400.racas.i18n.TranslationManager;
import com.garra400.racas.storage.config.ClassConfig;
import com.garra400.racas.storage.loader.ClassConfigLoader;
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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Class Selection Page - Second step after race selection
 * Uses pagination with 4 classes per page
 */
public class ClassSelectionPage extends InteractiveCustomUIPage<ClassSelectionPage.ClassEventData> {

    public static class ClassEventData {
        public String action;
        public String classId;

        public static final BuilderCodec<ClassEventData> CODEC = 
            BuilderCodec.builder(ClassEventData.class, ClassEventData::new)
                .append(
                    new KeyedCodec<>("Action", Codec.STRING),
                    (ClassEventData o, String v) -> o.action = v,
                    (ClassEventData o) -> o.action
                )
                .add()
                .append(
                    new KeyedCodec<>("ClassId", Codec.STRING),
                    (ClassEventData o, String v) -> o.classId = v,
                    (ClassEventData o) -> o.classId
                )
                .add()
                .build();
    }

    private static final int CLASSES_PER_PAGE = 4;
    
    private final String selectedRace;
    private final String selectedClass;
    private final int currentPage;
    private final List<String> allClassIds;

    public ClassSelectionPage(@Nonnull PlayerRef playerRef, String selectedRace) {
        this(playerRef, selectedRace, "none", 0);
    }

    public ClassSelectionPage(@Nonnull PlayerRef playerRef, String selectedRace, String selectedClass, int page) {
        super(playerRef, CustomPageLifetime.CantClose, ClassEventData.CODEC);
        this.selectedRace = selectedRace;
        this.selectedClass = selectedClass;
        this.currentPage = page;
        
        // Build class list dynamically from config
        this.allClassIds = new ArrayList<>();
        for (ClassConfig classConfig : ClassConfigLoader.getAllConfigs()) {
            allClassIds.add(classConfig.id);
        }
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder cmd,
            @Nonnull UIEventBuilder evt,
            @Nonnull Store<EntityStore> store
    ) {
        cmd.append("Pages/class_selection.ui");
        
        // Apply translations to static UI elements
        cmd.set("#Title.Text", TranslationManager.translate("ui.class_selection.title"));
        cmd.set("#Subtitle.Text", TranslationManager.translate("ui.class_selection.subtitle"));
        cmd.set("#StrengthsHeader.Text", TranslationManager.translate("ui.class_selection.strengths"));
        cmd.set("#WeaknessesHeader.Text", TranslationManager.translate("ui.class_selection.weaknesses"));
        cmd.set("#ConfirmSelection.Text", TranslationManager.translate("ui.class_selection.confirm"));
        cmd.set("#BackToRace.Text", TranslationManager.translate("ui.class_selection.back"));
        cmd.set("#PrevPageButton.Text", TranslationManager.translate("ui.class_selection.previous"));
        cmd.set("#NextPageButton.Text", TranslationManager.translate("ui.class_selection.next"));
        
        applyClassToUI(cmd, selectedClass);
        buildClassButtons(cmd, evt);
        
        // Page navigation
        int totalPages = (allClassIds.size() + CLASSES_PER_PAGE - 1) / CLASSES_PER_PAGE;
        cmd.set("#PageInfo.Text", "Page " + (currentPage + 1) + " / " + totalPages);
        cmd.set("#PrevPageButton.Visible", currentPage > 0);
        cmd.set("#NextPageButton.Visible", currentPage < totalPages - 1);
        
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#PrevPageButton", 
                new EventData().append("Action", "prevpage"));
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#NextPageButton", 
                new EventData().append("Action", "nextpage"));
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ConfirmSelection", 
                new EventData().append("Action", "confirm"));
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#BackToRace", 
                new EventData().append("Action", "back"));
    }
    
    private void buildClassButtons(UICommandBuilder cmd, UIEventBuilder evt) {
        cmd.clear("#ClassListPanel");
        cmd.appendInline("#ClassListPanel", "Group #ClassButtons { LayoutMode: Top; }");
        
        int start = currentPage * CLASSES_PER_PAGE;
        int end = Math.min(start + CLASSES_PER_PAGE, allClassIds.size());
        
        for (int i = start; i < end; i++) {
            String classId = allClassIds.get(i);
            
            int btnIndex = i - start;
            String buttonId = "#ClassButton" + btnIndex;
            
            // Use translated class name
            String className = TranslationManager.translate("class." + classId + ".name");
            String classTagline = TranslationManager.translate("class." + classId + ".tagline");
            
            cmd.appendInline("#ClassButtons", String.format("""
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
                """, buttonId, className.toUpperCase(), classTagline));
            
            if (i < end - 1) {
                cmd.appendInline("#ClassButtons", "Group { Anchor: (Height: 10); }");
            }
            
            evt.addEventBinding(CustomUIEventBindingType.Activating, 
                    buttonId, 
                    new EventData().append("Action", "select").append("ClassId", classId));
        }
    }

    private void applyClassToUI(UICommandBuilder cmd, String classId) {
        // Use translations for class name and tagline
        String className = TranslationManager.translate("class." + classId + ".name");
        String classTagline = TranslationManager.translate("class." + classId + ".tagline");
        
        cmd.set("#SelectedClassName.Text", className);
        cmd.set("#SelectedClassTagline.Text", classTagline);

        // Get class config for strengths and weaknesses
        ClassConfig config = ClassConfigLoader.getConfig(classId);
        if (config == null) return;

        // Set strengths (keep from config for now - could be translated later)
        List<String> strengths = config.strengths != null ? config.strengths : List.of();
        for (int i = 0; i < 5; i++) {
            String text = i < strengths.size() ? "- " + strengths.get(i) : "";
            cmd.set("#PositiveLine" + (i + 1) + ".Text", text);
        }

        // Set weaknesses (keep from config for now - could be translated later)
        List<String> weaknesses = config.weaknesses != null ? config.weaknesses : List.of();
        for (int i = 0; i < 5; i++) {
            String text = i < weaknesses.size() ? "- " + weaknesses.get(i) : "";
            cmd.set("#NegativeLine" + (i + 1) + ".Text", text);
        }
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull ClassEventData data
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());
        
        if ("select".equals(data.action)) {
            if (data.classId != null && ClassConfigLoader.hasConfig(data.classId)) {
                player.getPageManager().openCustomPage(ref, store, new ClassSelectionPage(playerRef, selectedRace, data.classId, currentPage));
            }
        } else if ("prevpage".equals(data.action)) {
            if (currentPage > 0) {
                player.getPageManager().openCustomPage(ref, store, new ClassSelectionPage(playerRef, selectedRace, selectedClass, currentPage - 1));
            }
        } else if ("nextpage".equals(data.action)) {
            int totalPages = (allClassIds.size() + CLASSES_PER_PAGE - 1) / CLASSES_PER_PAGE;
            if (currentPage < totalPages - 1) {
                player.getPageManager().openCustomPage(ref, store, new ClassSelectionPage(playerRef, selectedRace, selectedClass, currentPage + 1));
            }
        } else if ("back".equals(data.action)) {
            // Go back to race selection
            player.getPageManager().openCustomPage(ref, store, new RaceSelectionPage(playerRef));
        } else if ("confirm".equals(data.action)) {
            // Apply race + class combination
            System.out.println("ClassSelectionPage.confirm: selectedRace=" + selectedRace + ", selectedClass=" + selectedClass);
            RaceManager.applyRaceAndClass(ref, store, selectedRace, selectedClass);
            // Close the page
            this.close();
        }
    }
}
