package org.brahypno.dreamtinker.library.modifiers.modules.harvest;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Honey drops unique to Honey Tasty; eating is handled by Tinkers' Construct's edible modules.
 */
public enum HoneyLootModule implements ModifierModule, ProcessLootModifierHook {
    INSTANCE;

    public static final SingletonLoader<HoneyLootModule> LOADER = new SingletonLoader<>(INSTANCE);
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.defaultHooks(ModifierHooks.PROCESS_LOOT);

    @Override
    public SingletonLoader<HoneyLootModule> getLoader() {
        return LOADER;
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }

    @Override
    public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context) {
        if (context.hasParam(LootContextParams.DAMAGE_SOURCE)){
            Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
            if (entity != null && (entity.getType().is(TinkerTags.EntityTypes.SLIMES) || entity.getType() == EntityType.BEE)){
                int looting = context.getLootingModifier();
                if (Modifier.RANDOM.nextInt(24 / modifier.intEffectiveLevel()) <= looting){
                    generatedLoot.add(new ItemStack(Items.HONEY_BLOCK));
                }else if (Modifier.RANDOM.nextInt(36 / modifier.intEffectiveLevel()) <= looting){
                    generatedLoot.add(new ItemStack(Items.HONEYCOMB_BLOCK));
                }else if (Modifier.RANDOM.nextInt(48 / modifier.intEffectiveLevel()) <= looting){
                    generatedLoot.add(new ItemStack(Items.HONEY_BOTTLE));
                }
            }
        }
    }
}
