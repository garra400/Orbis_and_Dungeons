package com.garra400.racas.commands;

import com.garra400.racas.RaceManager;
import com.garra400.racas.color.ColorConverter;
import com.garra400.racas.i18n.TranslationManager;
import com.garra400.racas.ui.ClassSelectionPage;
import com.garra400.racas.ui.RaceSelectionPage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * Unified selection command: /race select [--race] [--class]
 * Opens the UI for selecting race and/or class (for reselection without reset)
 * 
 * Usage:
 * - /race select           - Opens race UI, then class UI after confirmation
 * - /race select --race    - Opens only race UI for reselection
 * - /race select --class   - Opens only class UI for reselection
 * 
 * This allows players to change their selection via UI without using text commands.
 */
public class SelectCommand extends AbstractPlayerCommand {
    
    private final FlagArg raceOnlyArg;
    private final FlagArg classOnlyArg;

    public SelectCommand() {
        super("select", "Open race/class selection UI", false);
        this.raceOnlyArg = withFlagArg("race", "Open only race selection UI");
        this.classOnlyArg = withFlagArg("class", "Open only class selection UI");
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
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.select.player_not_found")));
            return;
        }

        PageManager pages = player.getPageManager();
        boolean raceOnly = raceOnlyArg.get(ctx);
        boolean classOnly = classOnlyArg.get(ctx);

        // If only class selection requested
        if (classOnly && !raceOnly) {
            // Need to have a race selected first
            String currentRace = RaceManager.getPlayerRace(player);
            if (currentRace == null || currentRace.equals("none")) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.select.need_race_first")));
                return;
            }

            // Check if class UI is already open
            if (pages.getCustomPage() instanceof ClassSelectionPage) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.select.class_already_open")));
                return;
            }

            try {
                pages.openCustomPage(ref, store, new ClassSelectionPage(playerRef, currentRace));
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.select.opening_class")));
            } catch (Exception e) {
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.select.failed", e.getMessage())));
                e.printStackTrace();
            }
            return;
        }

        // If race only or default (opens race UI, which leads to class UI)
        // Check if race UI is already open
        if (pages.getCustomPage() instanceof RaceSelectionPage) {
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.select.race_already_open")));
            return;
        }

        try {
            // Get current race for pre-selection in UI if reselecting
            String currentRace = RaceManager.getPlayerRace(player);
            
            if (currentRace != null && !currentRace.equals("none")) {
                // Reselection mode - pre-select current race
                pages.openCustomPage(ref, store, new RaceSelectionPage(playerRef, currentRace, 0));
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.select.opening_reselect")));
            } else {
                // First selection mode
                pages.openCustomPage(ref, store, new RaceSelectionPage(playerRef));
                ctx.sendMessage(ColorConverter.message(
                    TranslationManager.translate("command.select.opening_race")));
            }
        } catch (Exception e) {
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.select.failed", e.getMessage())));
            e.printStackTrace();
        }
    }
}
