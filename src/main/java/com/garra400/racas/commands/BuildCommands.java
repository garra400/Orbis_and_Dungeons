package com.garra400.racas.commands;

import com.garra400.racas.RaceManager;
import com.garra400.racas.color.ColorConverter;
import com.garra400.racas.i18n.TranslationManager;
import com.garra400.racas.races.RaceDefinition;
import com.garra400.racas.races.RaceRegistry;
import com.garra400.racas.storage.config.ClassConfig;
import com.garra400.racas.storage.loader.ClassConfigLoader;
import com.garra400.racas.ui.RaceSelectionPage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Unified Build Command Collection: /build
 * 
 * Combines race and class management in one command.
 * 
 * Subcommands:
 * - /build select              - Opens race selection UI (goes to class after)
 * - /build change              - Change race and/or class directly
 * - /build info                - Show current race and class info
 * 
 * All commands support --player <name> for targeting other players (admin feature)
 */
public class BuildCommands extends AbstractCommandCollection {

    public BuildCommands() {
        super("build", "Build management (race + class combined)");
        addSubCommand(new SelectCommand());
        addSubCommand(new ChangeCommand());
        addSubCommand(new InfoCommand());
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    // ==================== /build select ====================
    
    /**
     * /build select [--player <name>]
     * Opens race selection UI (flows to class selection after choosing race)
     */
    private static class SelectCommand extends AbstractPlayerCommand {
        private final OptionalArg<String> playerArg;

        public SelectCommand() {
            super("select", "Open build selection UI", false);
            this.playerArg = withOptionalArg("player", "Target player (admin only)", ArgTypes.STRING);
        }

        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        @Override
        protected void execute(
                @Nonnull CommandContext ctx,
                @Nonnull Store<EntityStore> store,
                @Nonnull Ref<EntityStore> ref,
                @Nonnull PlayerRef playerRef,
                @Nonnull World world
        ) {
            String targetName = playerArg.get(ctx);
            
            PlayerRef targetRef;
            Player targetPlayer;
            Ref<EntityStore> targetEntityRef;
            Store<EntityStore> targetStore;
            
            if (targetName == null || targetName.isEmpty()) {
                targetRef = playerRef;
                targetPlayer = store.getComponent(ref, Player.getComponentType());
                targetEntityRef = ref;
                targetStore = store;
            } else {
                targetRef = Universe.get().getPlayerByUsername(targetName, NameMatching.EXACT_IGNORE_CASE);
                if (targetRef == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.build.player_not_found", targetName)));
                    return;
                }
                
                UUID worldUuid = targetRef.getWorldUuid();
                if (worldUuid == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.build.not_in_world")));
                    return;
                }
                
                targetPlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(targetRef.getUuid());
                if (targetPlayer == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.build.not_online")));
                    return;
                }
                
                targetEntityRef = null;
                targetStore = null;
            }

            if (targetPlayer == null) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.build.error_player_data")));
                return;
            }

            PageManager pages = targetPlayer.getPageManager();
            
            if (pages.getCustomPage() instanceof RaceSelectionPage) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.build.select.already_open")));
                return;
            }

            try {
                String currentRace = RaceManager.getPlayerRace(targetPlayer);
                if (currentRace != null && !currentRace.equals("none")) {
                    pages.openCustomPage(targetEntityRef != null ? targetEntityRef : ref, 
                                        targetStore != null ? targetStore : store, 
                                        new RaceSelectionPage(targetRef, currentRace, 0));
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.build.select.opening_reselect")));
                } else {
                    pages.openCustomPage(targetEntityRef != null ? targetEntityRef : ref, 
                                        targetStore != null ? targetStore : store, 
                                        new RaceSelectionPage(targetRef));
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.build.select.opening")));
                }
            } catch (Exception e) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.build.select.failed", e.getMessage())));
            }
        }
    }

    // ==================== /build change ====================
    
    /**
     * /build change [--race <race>] [--class <class>] [--player <name>]
     * Changes race and/or class directly without UI
     */
    private static class ChangeCommand extends AbstractPlayerCommand {
        private final OptionalArg<String> raceArg;
        private final OptionalArg<String> classArg;
        private final OptionalArg<String> playerArg;

        public ChangeCommand() {
            super("change", "Change race and/or class directly", false);
            this.raceArg = withOptionalArg("race", "Race to change to (" + listValidRaces() + ")", ArgTypes.STRING);
            this.classArg = withOptionalArg("class", "Class to change to (" + listValidClasses() + ")", ArgTypes.STRING);
            this.playerArg = withOptionalArg("player", "Target player (admin only)", ArgTypes.STRING);
        }

        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        @Override
        protected void execute(
                @Nonnull CommandContext ctx,
                @Nonnull Store<EntityStore> store,
                @Nonnull Ref<EntityStore> ref,
                @Nonnull PlayerRef playerRef,
                @Nonnull World world
        ) {
            String raceName = raceArg.get(ctx);
            String className = classArg.get(ctx);
            String targetName = playerArg.get(ctx);
            
            // If neither race nor class specified, show usage
            if ((raceName == null || raceName.isEmpty()) && (className == null || className.isEmpty())) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.build.change.usage")));
                return;
            }

            // Validate race if provided
            String raceId = null;
            if (raceName != null && !raceName.isEmpty()) {
                raceId = raceName.toLowerCase();
                if (!RaceRegistry.exists(raceId)) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.build.change.invalid_race", raceName)));
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.build.change.valid_races", listValidRaces())));
                    return;
                }
            }

            // Validate class if provided
            String classId = null;
            if (className != null && !className.isEmpty()) {
                classId = className.toLowerCase();
                if (!ClassConfigLoader.hasConfig(classId)) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.build.change.invalid_class", className)));
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.build.change.valid_classes", listValidClasses())));
                    return;
                }
            }

            PlayerRef targetRef;
            Player targetPlayer;
            
            if (targetName == null || targetName.isEmpty()) {
                targetRef = playerRef;
                targetPlayer = store.getComponent(ref, Player.getComponentType());
            } else {
                targetRef = Universe.get().getPlayerByUsername(targetName, NameMatching.EXACT_IGNORE_CASE);
                if (targetRef == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.build.player_not_found", targetName)));
                    return;
                }
                
                UUID worldUuid = targetRef.getWorldUuid();
                if (worldUuid == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.build.not_in_world")));
                    return;
                }
                
                targetPlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(targetRef.getUuid());
            }

            if (targetPlayer == null) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.build.not_online")));
                return;
            }

            // Get current values
            String currentRace = RaceManager.getPlayerRace(targetPlayer);
            String currentClass = RaceManager.getPlayerClass(targetPlayer);

            // Determine final values
            String finalRace = raceId != null ? raceId : currentRace;
            String finalClass = classId != null ? classId : currentClass;

            // Need at least a race to apply
            if (finalRace == null || finalRace.equals("none")) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.build.change.need_race")));
                return;
            }

            // Apply changes
            if (finalClass != null && !finalClass.equals("none")) {
                RaceManager.applyRaceAndClass(targetPlayer, finalRace, finalClass);
            } else {
                RaceManager.applyRace(targetPlayer, finalRace, targetRef);
            }

            // Build display names
            RaceDefinition race = RaceRegistry.get(finalRace);
            String raceDisplayName = race != null ? race.displayName() : finalRace;
            
            String classDisplayName = "None";
            if (finalClass != null && !finalClass.equals("none")) {
                ClassConfig classConfig = ClassConfigLoader.getClass(finalClass);
                classDisplayName = classConfig != null ? classConfig.displayName : finalClass;
            }

            // Send confirmation
            if (targetName == null || targetName.isEmpty()) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.build.change.success_self", raceDisplayName, classDisplayName)));
            } else {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.build.change.success_other", 
                        targetRef.getUsername(), raceDisplayName, classDisplayName)));
                targetPlayer.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.build.change.by_admin", raceDisplayName, classDisplayName)));
            }
        }
    }

    // ==================== /build info ====================
    
    /**
     * /build info [--player <name>]
     * Shows race and class information
     */
    private static class InfoCommand extends AbstractPlayerCommand {
        private final OptionalArg<String> playerArg;

        public InfoCommand() {
            super("info", "Show build information (race + class)", false);
            this.playerArg = withOptionalArg("player", "Target player", ArgTypes.STRING);
        }

        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        @Override
        protected void execute(
                @Nonnull CommandContext ctx,
                @Nonnull Store<EntityStore> store,
                @Nonnull Ref<EntityStore> ref,
                @Nonnull PlayerRef playerRef,
                @Nonnull World world
        ) {
            String targetName = playerArg.get(ctx);
            PlayerRef targetRef;
            Player targetPlayer;
            
            if (targetName == null || targetName.isEmpty()) {
                targetRef = playerRef;
                targetPlayer = store.getComponent(ref, Player.getComponentType());
            } else {
                targetRef = Universe.get().getPlayerByUsername(targetName, NameMatching.EXACT_IGNORE_CASE);
                if (targetRef == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.build.player_not_found", targetName)));
                    return;
                }
                
                UUID worldUuid = targetRef.getWorldUuid();
                if (worldUuid == null) {
                    ctx.sendMessage(ColorConverter.message(
                        TranslationManager.translate("command.build.not_in_world")));
                    return;
                }
                
                targetPlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(targetRef.getUuid());
            }

            if (targetPlayer == null) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.build.not_online")));
                return;
            }

            String raceId = RaceManager.getPlayerRace(targetPlayer);
            String classId = RaceManager.getPlayerClass(targetPlayer);
            String displayTargetName = targetRef.getUsername();

            // Get display names
            String raceName = "None";
            if (raceId != null && !raceId.equals("none")) {
                RaceDefinition race = RaceRegistry.get(raceId);
                raceName = race != null ? race.displayName() : raceId;
            }

            String className = "None";
            if (classId != null && !classId.equals("none")) {
                ClassConfig classConfig = ClassConfigLoader.getClass(classId);
                className = classConfig != null ? classConfig.displayName : classId;
            }

            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.build.info.title", displayTargetName)));
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.build.info.race", raceName)));
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.build.info.class", className)));
        }
    }

    // ==================== Helper Methods ====================
    
    private static String listValidRaces() {
        return RaceRegistry.all().stream()
                .map(RaceDefinition::id)
                .collect(Collectors.joining(", "));
    }

    private static String listValidClasses() {
        return ClassConfigLoader.getAllClasses().stream()
                .map(c -> c.id)
                .collect(Collectors.joining(", "));
    }
}
