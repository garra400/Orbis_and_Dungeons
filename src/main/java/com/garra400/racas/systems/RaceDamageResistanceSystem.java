package com.garra400.racas.systems;

import com.garra400.racas.RaceManager;
import com.garra400.racas.storage.config.ClassConfig;
import com.garra400.racas.storage.loader.ClassConfigLoader;
import com.garra400.racas.storage.config.RaceConfig;
import com.garra400.racas.storage.loader.RaceConfigLoader;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Map;
import java.util.Set;

/**
 * System that applies race and class-specific damage resistances to players.
 * Based on OrbisOrigins SpeciesDamageResistanceSystem.
 * 
 * Runs in the FilterDamageGroup after damage gathering and before inspection.
 * Combines resistances from both race and class - the strongest resistance applies.
 */
public class RaceDamageResistanceSystem extends DamageEventSystem {

    private static final Query<EntityStore> QUERY = AllLegacyLivingEntityTypesQuery.INSTANCE;

    @Override
    public SystemGroup<EntityStore> getGroup() {
        // Run in filter group to modify damage before armor/other reductions
        return DamageModule.get().getFilterDamageGroup();
    }

    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(
                new SystemGroupDependency<>(Order.AFTER, DamageModule.get().getGatherDamageGroup()),
                new SystemGroupDependency<>(Order.BEFORE, DamageModule.get().getInspectDamageGroup())
        );
    }

    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }

    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store,
                       CommandBuffer<EntityStore> commandBuffer, Damage damage) {
        
        // Get the entity reference
        Ref<EntityStore> targetRef = chunk.getReferenceTo(index);
        if (targetRef == null || !targetRef.isValid()) {
            return;
        }

        // Get player component
        Player player = chunk.getComponent(index, Player.getComponentType());
        if (player == null) {
            return;
        }

        // Get player's race and class
        String raceId = RaceManager.getPlayerRace(player);
        String classId = RaceManager.getPlayerClass(player);
        
        if (raceId == null) {
            return; // Player has no race selected
        }

        // Get damage cause
        DamageCause damageCause = damage.getCause();
        if (damageCause == null) {
            return;
        }

        String damageType = damageCause.getId();
        if (damageType == null || damageType.isEmpty()) {
            return;
        }

        // Calculate combined resistance from race and class
        float finalResistance = 1.0f; // Default: no resistance

        // Get race resistance
        RaceConfig raceConfig = RaceConfigLoader.getConfig(raceId);
        if (raceConfig != null && raceConfig.damageResistances != null) {
            float raceResistance = raceConfig.damageResistances.getOrDefault(damageType, 1.0f);
            finalResistance = Math.min(finalResistance, raceResistance); // Use the best (lowest) resistance
        }

        // Get class resistance
        if (classId != null && !classId.equals("none")) {
            ClassConfig classConfig = ClassConfigLoader.getClass(classId);
            if (classConfig != null && classConfig.damageResistances != null) {
                float classResistance = classConfig.damageResistances.getOrDefault(damageType, 1.0f);
                finalResistance = Math.min(finalResistance, classResistance); // Use the best (lowest) resistance
            }
        }

        // Apply resistance if it's not the default (1.0)
        if (finalResistance != 1.0f) {
            float currentAmount = damage.getAmount();
            float newAmount = currentAmount * finalResistance;
            damage.setAmount(newAmount);
        }
    }
}
