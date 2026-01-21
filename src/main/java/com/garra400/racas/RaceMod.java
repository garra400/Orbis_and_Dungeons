package com.garra400.racas;

import com.garra400.racas.commands.RaceInfoCommand;
import com.garra400.racas.commands.RaceResetCommand;
import com.garra400.racas.commands.RaceTradeCommand;
import com.garra400.racas.components.RaceData;
import com.garra400.racas.ui.RaceSelectionPage;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/**
 * Entry point for race mod: registers listener to open UI when player is ready.
 * Opens UI only once - first time player enters the world.
 * 
 * Now uses persistent component system for reliable race tracking across sessions.
 */
public class RaceMod extends JavaPlugin {

    /**
     * Component type for accessing player race data.
     * Initialized in start() method.
     */
    private static ComponentType<EntityStore, RaceData> raceDataType;

    public RaceMod(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void start() {
        // Register the RaceData component with Hytale's persistence system
        raceDataType = getEntityStoreRegistry().registerComponent(
            RaceData.class,      // Component class
            "RaceData",          // Unique identifier for persistence
            RaceData.CODEC       // Serialization codec
        );

        // Pass component type to RaceManager so it can access/modify race data
        RaceManager.setRaceDataType(raceDataType);

        // Register simple player commands (like Basic UIs approach)
        CommandRegistry commands = getCommandRegistry();
        commands.registerCommand(new RaceTradeCommand());
        commands.registerCommand(new RaceResetCommand());
        commands.registerCommand(new RaceInfoCommand());

        // Register event listener
        EventRegistry events = getEventRegistry();
        events.registerGlobal(PlayerReadyEvent.class, this::openRacePageOnJoin);
    }

    /**
     * Gets the registered component type.
     * Can be used by other parts of the mod to access race data.
     * 
     * @return The RaceData component type, or null if not yet initialized
     */
    public static ComponentType<EntityStore, RaceData> getRaceDataType() {
        return raceDataType;
    }

    private void openRacePageOnJoin(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        PageManager pages = player.getPageManager();

        // Ensure it only opens once and playerRef has associated store
        if (pages.getCustomPage() instanceof RaceSelectionPage) {
            return;
        }

        // Check if player already has race selected (using persistent component)
        // This reliably persists between server sessions and reconnects
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
