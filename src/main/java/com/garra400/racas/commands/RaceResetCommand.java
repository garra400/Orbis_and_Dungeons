package com.garra400.racas.commands;

import com.garra400.racas.RaceManager;
import com.garra400.racas.color.ColorConverter;
import com.garra400.racas.i18n.T;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Command to reset race: /racereset [--player <name>]
 * Uses AbstractPlayerCommand for reliability (like Basic UIs)
 * Supports optional --player argument to target other players
 */
public class RaceResetCommand extends AbstractPlayerCommand {
    
    private final OptionalArg<String> playerArg;
    
    public RaceResetCommand() {
        super("racereset", "Reset race selection", false);
        this.playerArg = withOptionalArg("player", "Target player (self if omitted)", ArgTypes.STRING);
    }

    @Override
    protected boolean canGeneratePermission() {
        // Libera o comando para todos (sem perm node/OP)
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
        // Check if targeting another player
        String targetPlayerName = playerArg.get(ctx);
        PlayerRef targetRef;
        Player targetPlayer;
        
        if (targetPlayerName == null || targetPlayerName.isEmpty()) {
            // Self-targeting
            targetRef = playerRef;
            targetPlayer = store.getComponent(ref, Player.getComponentType());
            if (targetPlayer == null) {
                ctx.sendMessage(T.t("command.racereset.error_player_data"));
                return;
            }
        } else {
            // Target another player
            targetRef = Universe.get().getPlayerByUsername(targetPlayerName, NameMatching.EXACT_IGNORE_CASE);
            if (targetRef == null) {
                ctx.sendMessage(T.t("command.racereset.player_not_found", targetPlayerName));
                return;
            }
            
            // Get target player entity
            UUID worldUuid = targetRef.getWorldUuid();
            if (worldUuid == null) {
                ctx.sendMessage(T.t("command.racereset.not_in_world"));
                return;
            }
            
            UUID uuid = targetRef.getUuid();
            targetPlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(uuid);
            if (targetPlayer == null) {
                ctx.sendMessage(T.t("command.racereset.not_online"));
                return;
            }
        }
        
        // Reset race
        try {
            boolean success = RaceManager.resetRace(targetPlayer, targetRef);
            if (success) {
                if (targetPlayerName == null || targetPlayerName.isEmpty()) {
                    ctx.sendMessage(T.t("command.racereset.success_self"));
                    ctx.sendMessage(T.t("command.racereset.reconnect"));
                } else {
                    ctx.sendMessage(T.t("command.racereset.success_other", targetRef.getUsername()));
                    targetPlayer.sendMessage(T.t("command.racereset.reset_by_admin"));
                }
            } else {
                ctx.sendMessage(T.t("command.racereset.failed"));
            }
        } catch (Exception e) {
            ctx.sendMessage(T.t("command.racereset.error", e.getMessage()));
        }
    }
}
