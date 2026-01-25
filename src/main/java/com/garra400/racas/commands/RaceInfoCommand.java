package com.garra400.racas.commands;

import com.garra400.racas.RaceManager;
import com.garra400.racas.components.RaceData;
import com.garra400.racas.races.RaceRegistry;
import com.garra400.racas.storage.config.ClassConfig;
import com.garra400.racas.storage.loader.ClassConfigLoader;
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
 * Command to view race info: /raceinfo [--player <name>]
 * Uses AbstractPlayerCommand for reliability (like Basic UIs)
 * Supports optional --player argument to target other players
 */
public class RaceInfoCommand extends AbstractPlayerCommand {
    
    private final OptionalArg<String> playerArg;
    
    public RaceInfoCommand() {
        super("raceinfo", "Show race information", false);
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
        String targetNameArg = playerArg.get(ctx);
        PlayerRef targetRef = null;
        Player targetPlayer = null;
        String displayName;
        RaceData raceData = null;

        if (targetNameArg == null || targetNameArg.isEmpty()) {
            targetRef = playerRef;
            targetPlayer = store.getComponent(ref, Player.getComponentType());
            displayName = targetRef != null ? targetRef.getUsername() : "you";
        } else {
            targetRef = Universe.get().getPlayerByUsername(targetNameArg, NameMatching.EXACT_IGNORE_CASE);
            displayName = targetRef != null ? targetRef.getUsername() : targetNameArg;
            if (targetRef != null) {
                UUID worldUuid = targetRef.getWorldUuid();
                if (worldUuid != null) {
                    UUID uuid = targetRef.getUuid();
                    targetPlayer = (Player) Universe.get().getWorld(worldUuid).getEntity(uuid);
                }
            }
        }

        try {
            String raceId = null;
            String classId = null;
            if (targetPlayer != null) {
                raceId = RaceManager.getPlayerRace(targetPlayer);
                classId = RaceManager.getPlayerClass(targetPlayer);
                raceData = RaceManager.getPlayerRaceData(targetPlayer);
            }
            if (raceId == null && targetRef != null) {
                raceId = RaceManager.getStoredRace(targetRef);
            }
            if (raceId == null) {
                raceId = RaceManager.getStoredRaceByName(displayName);
            }

            if (raceId == null) {
                ctx.sendMessage(Message.raw("No race recorded for " + displayName + "."));
                return;
            }

            // Format race and class info
            String raceName = raceId;
            if (RaceRegistry.exists(raceId)) {
                raceName = RaceRegistry.get(raceId).displayName();
            }

            String className = "None";
            if (classId != null && !classId.equals("none")) {
                ClassConfig classConfig = ClassConfigLoader.getClass(classId);
                if (classConfig != null) {
                    className = classConfig.displayName;
                }
            }

            ctx.sendMessage(Message.raw("=== Race Info for " + displayName + " ==="));
            ctx.sendMessage(Message.raw("Race: " + raceName));
            ctx.sendMessage(Message.raw("Class: " + className));

            if (raceData != null && raceData.getSelectionTimestamp() != null && !raceData.getSelectionTimestamp().isEmpty()) {
                ctx.sendMessage(Message.raw("Selected: " + raceData.getSelectionDateFormatted()));
                long days = raceData.getDaysSinceSelection();
                if (days >= 0) {
                    ctx.sendMessage(Message.raw("Days ago: " + days));
                }
            }

            ctx.sendMessage(Message.raw("=============================="));
        } catch (Exception e) {
            ctx.sendMessage(Message.raw("Error getting race info: " + e.getMessage()));
        }
    }
}
