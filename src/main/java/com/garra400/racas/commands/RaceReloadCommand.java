package com.garra400.racas.commands;

import com.garra400.racas.color.ColorConverter;
import com.garra400.racas.i18n.T;
import com.garra400.racas.i18n.TranslationManager;
import com.garra400.racas.races.RaceRegistry;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * Command to reload race configurations from JSON.
 * Usage: /racereload
 */
public class RaceReloadCommand extends AbstractPlayerCommand {

    public RaceReloadCommand() {
        super("racereload", "Reload race configurations from JSON", true);
    }

    @Override
    protected void execute(
        @Nonnull CommandContext context,
        @Nonnull Store<EntityStore> store,
        @Nonnull Ref<EntityStore> ref,
        @Nonnull PlayerRef playerRef,
        @Nonnull World world
    ) {
        try {
            RaceRegistry.reload();
            TranslationManager.reload();
            context.sendMessage(T.t("command.racereload.success"));
            context.sendMessage(T.t("command.racereload.updated"));
        } catch (Exception e) {
            context.sendMessage(T.t("command.racereload.failed", e.getMessage()));
            e.printStackTrace();
        }
    }
}
