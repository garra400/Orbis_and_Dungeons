package com.garra400.racas.commands;

import com.garra400.racas.RaceManager;
import com.garra400.racas.color.ColorConverter;
import com.garra400.racas.i18n.TranslationManager;
import com.garra400.racas.ui.RaceSelectionPage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * Command to manually open race selection UI: /raceselect
 * Only works if player hasn't already selected a race/class
 * Helpful for resolving conflicts with other mods like JoinScreen
 */
public class RaceSelectCommand extends AbstractPlayerCommand {

    public RaceSelectCommand() {
        super("raceselect", "Open race selection UI (if not already selected)", false);
    }

    @Override
    protected boolean canGeneratePermission() {
        // Free command for all players (no permission required)
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
                TranslationManager.translate("command.raceselect.player_not_found")));
            return;
        }

        // Check if player already has race/class selected
        if (RaceManager.hasRaceApplied(player)) {
            String raceId = RaceManager.getPlayerRace(player);
            String classId = RaceManager.getPlayerClass(player);
            
            String raceName = raceId != null ? raceId : "Unknown";
            String className = classId != null && !classId.equals("none") ? classId : "None";
            
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.raceselect.already_selected")));
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.raceselect.current", raceName, className)));
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.raceselect.use_trade")));
            return;
        }

        // Open race selection UI
        PageManager pages = player.getPageManager();
        
        // Check if UI is already open
        if (pages.getCustomPage() instanceof RaceSelectionPage) {
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.raceselect.already_open")));
            return;
        }

        try {
            pages.openCustomPage(ref, store, new RaceSelectionPage(playerRef));
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.raceselect.opening")));
        } catch (Exception e) {
            ctx.sendMessage(ColorConverter.message(
                TranslationManager.translate("command.raceselect.failed", e.getMessage())));
            e.printStackTrace();
        }
    }
}
