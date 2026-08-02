package org.brahypno.dreamtinker.utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.brahypno.esotericismtinker.utils.CompatUtils.CuriosCompat;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * One-tick view of all modifiers equipped by a living entity.
 *
 * <p>Forge combat, visibility and target-selection events can ask the same question several times in
 * one tick. Calling {@code ETModifierCheck} each time reparses every hand/armor/Curios stack. This
 * cache performs that traversal once and stores only the summed modifier levels. A one-tick lifetime
 * deliberately avoids long-lived equipment invalidation rules; an equipment change becomes visible
 * no later than the next game tick.</p>
 *
 * <p>The map uses weak entity keys and each snapshot contains no entity reference, so unloaded
 * entities are not retained by this optimization.</p>
 */
public final class EquippedModifierSnapshot {
    private static final Map<LivingEntity, Snapshot> CACHE = new WeakHashMap<>();

    private EquippedModifierSnapshot() {}

    public static int getLevel(LivingEntity entity, ModifierId modifierId) {
        return snapshot(entity).levels().getOrDefault(modifierId, 0);
    }

    public static boolean has(LivingEntity entity, ModifierId modifierId) {
        return getLevel(entity, modifierId) > 0;
    }

    public static void clear() {
        synchronized(CACHE) {
            CACHE.clear();
        }
    }

    private static Snapshot snapshot(LivingEntity entity) {
        long gameTime = entity.level().getGameTime();
        synchronized(CACHE) {
            Snapshot cached = CACHE.get(entity);
            if (cached != null && cached.gameTime() == gameTime){
                return cached;
            }

            Snapshot rebuilt = new Snapshot(gameTime, collectLevels(entity));
            CACHE.put(entity, rebuilt);
            return rebuilt;
        }
    }

    private static Map<ModifierId, Integer> collectLevels(LivingEntity entity) {
        Map<ModifierId, Integer> levels = new HashMap<>();
        collect(entity.getHandSlots(), levels);
        collect(entity.getArmorSlots(), levels);

        if (entity instanceof Player player){
            List<ItemStack> curios = CuriosCompat.getCurioStacks(player);
            if (curios != null){
                collect(curios, levels);
            }
        }
        return levels;
    }

    private static void collect(Iterable<ItemStack> stacks, Map<ModifierId, Integer> levels) {
        for (ItemStack stack : stacks) {
            if (stack.isEmpty() || !stack.is(TinkerTags.Items.MODIFIABLE)){
                continue;
            }

            ToolStack tool = ToolStack.from(stack);
            for (ModifierEntry entry : tool.getModifierList()) {
                levels.merge(entry.getModifier().getId(), entry.getLevel(), Integer::sum);
            }
        }
    }

    private record Snapshot(long gameTime, Map<ModifierId, Integer> levels) {}
}
