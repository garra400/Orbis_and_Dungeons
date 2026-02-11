package com.garra400.racas;

import com.garra400.racas.commands.RaceCommands;
import com.garra400.racas.commands.ClassCommands;
import com.garra400.racas.commands.BuildCommands;
import com.garra400.racas.commands.LanguageCommands;
import com.garra400.racas.components.RaceData;
import com.garra400.racas.i18n.TranslationManager;
import com.garra400.racas.races.RaceRegistry;
import com.garra400.racas.storage.loader.ClassConfigLoader;
import com.garra400.racas.storage.loader.RaceConfigLoader;
import com.garra400.racas.storage.loader.ModConfigLoader;
import com.garra400.racas.storage.RaceStorage;
import com.garra400.racas.systems.RaceDamageBoostSystem;
import com.garra400.racas.systems.RaceDamageResistanceSystem;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Entry point for race mod: registers listener to open UI when player is ready.
 * Opens UI only once - first time player enters the world.
 * 
 * Now uses persistent component system for reliable race tracking across sessions.
 * Supports compatibility mode with other stat mods (RPGLeveling, MMO Skilltree, etc.)
 */
public class RaceMod extends JavaPlugin {

    /**
     * Component type for accessing player race data.
     * Initialized in start() method.
     */
    private static ComponentType<EntityStore, RaceData> raceDataType;
    
    /**
     * Scheduler for delayed stat application (compatibility mode)
     */
    private static ScheduledExecutorService scheduler;

    public RaceMod(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void start() {
        System.out.println("[Orbis] Starting Orbis and Dungeons mod...");
        
        // Init translation system first
        TranslationManager.initialize(getDataDirectory().toFile());

        // Init mod configuration - must be first to check compatibility settings
        ModConfigLoader.init(getDataDirectory());
        
        // Init race and class configuration
        RaceConfigLoader.init(getDataDirectory());
        ClassConfigLoader.init(getDataDirectory());

        // Load races from JSON config
        RaceRegistry.loadFromConfig();

        // Init storage for race cache
        RaceStorage.init(getDataDirectory());

        // Initialize mod integrations (RPGLeveling, HardcoreMode)
        com.garra400.racas.integration.ModIntegration.initialize();
        
        // Initialize scheduler for delayed stat application
        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Register the RaceData component with Hytale's persistence system
        raceDataType = getEntityStoreRegistry().registerComponent(
            RaceData.class,      // Component class
            "RaceData",          // Unique identifier for persistence
            RaceData.CODEC       // Serialization codec
        );

        // Pass component type to RaceManager so it can access/modify race data
        RaceManager.setRaceDataType(raceDataType);

        // Register damage systems
        getEntityStoreRegistry().registerSystem(new RaceDamageBoostSystem());
        getEntityStoreRegistry().registerSystem(new RaceDamageResistanceSystem());

        // ==================== UNIFIED COMMAND SYSTEM ====================
        // Clean, intuitive command structure:
        //
        // /race select              - Open race selection UI
        // /race change <race>       - Change race directly
        // /race reset               - Reset race to none
        // /race info                - Show race information
        // /race reload              - Reload race configs (admin)
        //
        // /class select             - Open class selection UI
        // /class change <class>     - Change class directly
        // /class reset              - Reset class to none
        // /class info               - Show class information
        //
        // /build select             - Open full build selection UI
        // /build change             - Change race and/or class
        // /build info               - Show race + class information
        //
        // /language set <code>      - Set server language
        // /language list            - List available languages
        // /language current         - Show current language
        // =================================================================
        
        CommandRegistry commands = getCommandRegistry();
        commands.registerCommand(new RaceCommands());      // /race
        commands.registerCommand(new ClassCommands());     // /class
        commands.registerCommand(new BuildCommands());     // /build
        commands.registerCommand(new LanguageCommands());  // /language

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
    
    /**
     * Gets the scheduler for delayed operations.
     * Used for compatibility mode stat application.
     */
    public static ScheduledExecutorService getScheduler() {
        return scheduler;
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
            // Player already has race - reapply stats with delay for compatibility
            int delay = ModConfigLoader.getStatApplicationDelay();
            if (delay > 0 && ModConfigLoader.isCompatibilityMode()) {
                String raceId = RaceManager.getPlayerRace(player);
                String classId = RaceManager.getPlayerClass(player);
                if (raceId != null) {
                    scheduler.schedule(() -> {
                        try {
                            RaceManager.applyRaceAndClass(player, raceId, classId != null ? classId : "none");
                            if (ModConfigLoader.isDebugMode()) {
                                System.out.println("[Orbis] Reapplied race stats after " + delay + "ms delay for: " + raceId);
                            }
                        } catch (Exception e) {
                            System.err.println("[Orbis] Failed to reapply race stats: " + e.getMessage());
                        }
                    }, delay, TimeUnit.MILLISECONDS);
                }
            }
            return; // Already chose race, don't open UI again
        }

        // Check if race UI should show on first join
        if (!ModConfigLoader.shouldShowRaceUiOnFirstJoin()) {
            return;
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
