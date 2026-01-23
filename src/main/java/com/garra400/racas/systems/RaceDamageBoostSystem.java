package com.garra400.racas.systems;

import com.garra400.racas.RaceManager;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Set;

/**
 * Aplica multiplicadores de dano configurados por raça (ex.: Berserker com axes/battleaxes).
 * - Grupo: FilterDamageGroup (entre gather e aplicação final), com dependências explícitas.
 * - Não acessa o store diretamente para evitar "Store is processing" durante ticks.
 */
public class RaceDamageBoostSystem extends DamageEventSystem {

    private static final Query<EntityStore> QUERY = AllLegacyLivingEntityTypesQuery.INSTANCE;

    @Override
    public SystemGroup<EntityStore> getGroup() {
        // Aplicar antes de reduções/armor (pós-gather).
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
        if (damage == null || damage.isCancelled()) {
            return;
        }

        if (!(damage.getSource() instanceof Damage.EntitySource entitySource)) {
            return;
        }

        Ref<EntityStore> attackerRef = entitySource.getRef();
        if (attackerRef == null || !attackerRef.isValid()) {
            return;
        }

        var attacker = EntityUtils.getEntity(attackerRef, commandBuffer);
        if (!(attacker instanceof Player player)) {
            return;
        }

        String raceId = RaceManager.getPlayerRace(player);
        if (raceId == null) {
            return;
        }

        var inventory = player.getInventory();
        if (inventory == null) {
            return;
        }

        ItemStack weapon = inventory.getItemInHand();
        if (weapon == null || weapon.isEmpty()) {
            return;
        }

        float multiplier = RaceManager.getWeaponDamageMultiplier(raceId, weapon);
        if (multiplier > 1.0f) {
            damage.setAmount(damage.getAmount() * multiplier);
        }
    }
}
