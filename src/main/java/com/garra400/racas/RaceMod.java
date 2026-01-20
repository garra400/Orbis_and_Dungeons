package com.garra400.racas;

import com.garra400.racas.ui.RaceSelectionPage;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

/**
 * Entry point for race mod: registers listener to open UI when player is ready.
 * Opens UI only once - first time player enters the world.
 */
public class RaceMod extends JavaPlugin {

    public RaceMod(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void start() {
        EventRegistry events = getEventRegistry();
        events.registerGlobal(PlayerReadyEvent.class, this::openRacePageOnJoin);
    }

    private void openRacePageOnJoin(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        PageManager pages = player.getPageManager();

        // Ensure it only opens once and playerRef has associated store
        if (pages.getCustomPage() instanceof RaceSelectionPage) {
            return;
        }

        // Check if player already has race modifiers applied
        // This persists between server sessions and reconnects
        if (RaceManager.hasRaceApplied(player)) {
            return; // Already chose race, don't open again
        }

        var playerRef = player.getPlayerRef();
        var ref = event.getPlayerRef();
        var store = ref != null ? ref.getStore() : null;
        if (store == null) {
            return;
        }

        pages.openCustomPage(ref, store, new RaceSelectionPage(playerRef));
    }
}
