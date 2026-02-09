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
 * Unified command to change both race and class at once: /changebuild [--race <race>] [--class <class>] [--player <name>]
 * 
 * Usage:
 * - /changebuild                         - Opens race selection UI (for self)
 * - /changebuild --race orc              - Changes race only (keeps current class)
 * - /changebuild --class berserker       - Changes class only (keeps current race)
 * - /changebuild --race orc --class berserker - Changes both race and class
 * - /changebuild --player Steve --race elf   - Changes another player's race
 * 
 * If no race/class arguments provided, opens the selection UI instead.
 */
public class TradeAllCommand extends AbstractPlayerCommand {
    
    private final OptionalArg<String> raceArg;
    private final OptionalArg<String> classArg;
    private final OptionalArg<String> playerArg;

    public TradeAllCommand() {
        super("changebuild", "Change race and/or class (or open UI if no args)", false);
        this.raceArg = withOptionalArg("race", "New race (" + listValidRaces() + ")", ArgTypes.STRING);
        this.classArg = withOptionalArg("class", "New class (" + listValidClasses() + ")", ArgTypes.STRING);
        this.playerArg = withOptionalArg("player", "Target player (self if omitted)", ArgTypes.STRING);
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
        String targetPlayerName = playerArg.get(ctx);

        // Determine target player
        PlayerRef targetRef;
        Player targetPlayer;
        Ref<EntityStore> targetEntityRef;
        Store<EntityStore> targetStore;

        if (targetPlayerName == null || targetPlayerName.isEmpty()) {
            // Self-targeting
            targetRef = playerRef;
            targetPlayer = store.getComponent(ref, Player.getComponentType());
            targetEntityRef = ref;
            targetStore = store;
            
            if (targetPlayer == null) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.changebuild.error_player_data")));
                return;
            }
        } else {
            // Target another player
            targetRef = Universe.get().getPlayerByUsername(targetPlayerName, NameMatching.EXACT_IGNORE_CASE);
            if (targetRef == null) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.changebuild.player_not_found", targetPlayerName)));
                return;
            }
            
            UUID worldUuid = targetRef.getWorldUuid();
            if (worldUuid == null) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.changebuild.not_in_world")));
                return;
            }
            
            UUID uuid = targetRef.getUuid();
            targetPlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(uuid);
            if (targetPlayer == null) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.changebuild.not_online")));
                return;
            }
            
            // Get store for target player
            targetEntityRef = null;
            targetStore = null;
        }

        // If no race/class specified, open UI
        if ((raceName == null || raceName.isEmpty()) && (className == null || className.isEmpty())) {
            // Can only open UI for self
            if (targetPlayerName != null && !targetPlayerName.isEmpty()) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.changebuild.ui_self_only")));
                return;
            }
            
            PageManager pages = targetPlayer.getPageManager();
            
            if (pages.getCustomPage() instanceof RaceSelectionPage) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.changebuild.ui_already_open")));
                return;
            }
            
            try {
                String currentRace = RaceManager.getPlayerRace(targetPlayer);
                if (currentRace != null && !currentRace.equals("none")) {
                    pages.openCustomPage(targetEntityRef, targetStore, new RaceSelectionPage(targetRef, currentRace, 0));
                } else {
                    pages.openCustomPage(targetEntityRef, targetStore, new RaceSelectionPage(targetRef));
                }
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.changebuild.opening_ui")));
            } catch (Exception e) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.changebuild.failed", e.getMessage())));
                e.printStackTrace();
            }
            return;
        }

        // Validate race if provided
        String raceId = null;
        if (raceName != null && !raceName.isEmpty()) {
            raceId = raceName.toLowerCase();
            if (!RaceRegistry.exists(raceId)) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.changebuild.invalid_race", raceName, listValidRaces())));
                return;
            }
        }

        // Validate class if provided
        String classId = null;
        if (className != null && !className.isEmpty()) {
            classId = className.toLowerCase();
            if (!ClassConfigLoader.hasConfig(classId)) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.changebuild.invalid_class", className, listValidClasses())));
                return;
            }
        }

        // Get current race/class
        String currentRace = RaceManager.getPlayerRace(targetPlayer);
        String currentClass = RaceManager.getPlayerClass(targetPlayer);

        // Determine final race and class
        String finalRace = raceId != null ? raceId : currentRace;
        String finalClass = classId != null ? classId : currentClass;

        // Need at least a race to apply
        if (finalRace == null || finalRace.equals("none")) {
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.changebuild.need_race")));
            return;
        }

        // Apply the changes
        if (finalClass != null && !finalClass.equals("none")) {
            RaceManager.applyRaceAndClass(targetPlayer, finalRace, finalClass);
        } else {
            RaceManager.applyRace(targetPlayer, finalRace, targetRef);
        }

        // Build confirmation message
        RaceDefinition race = RaceRegistry.get(finalRace);
        String raceDisplayName = race != null ? race.displayName() : finalRace;
        
        String classDisplayName = "None";
        if (finalClass != null && !finalClass.equals("none")) {
            ClassConfig classConfig = ClassConfigLoader.getClass(finalClass);
            classDisplayName = classConfig != null ? classConfig.displayName : finalClass;
        }

        // Send confirmation
        if (targetPlayerName == null || targetPlayerName.isEmpty()) {
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.changebuild.changed_self", raceDisplayName, classDisplayName)));
        } else {
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.changebuild.changed_other", 
                    targetRef.getUsername(), raceDisplayName, classDisplayName)));
            targetPlayer.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.changebuild.changed_by_admin", raceDisplayName, classDisplayName)));
        }
    }

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
