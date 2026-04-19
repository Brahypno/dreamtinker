package org.dreamtinker.dreamtinker.tools.modifiers.traits.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

import static slimeknights.tconstruct.common.TinkerTags.Items.HARVEST;
import static slimeknights.tconstruct.common.TinkerTags.Items.WORN_ARMOR;
import static slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial.ARMOR_SLOTS;

public class SilkyCrystal extends NoLevelsModifier implements ProcessLootModifierHook {
    private static final IJsonPredicate<Item> harvest = ItemPredicate.tag(HARVEST);
    private static final IJsonPredicate<Item> armor = ItemPredicate.tag(WORN_ARMOR);
    private static final ThreadLocal<Boolean> IN_PROGRESS = ThreadLocal.withInitial(() -> false);

    private static void removeMatchingItemTypes(List<ItemStack> generatedLoot, List<ItemStack> targets) {
        generatedLoot.removeIf(existing -> {
            if (existing.isEmpty()){
                return true;
            }

            for (ItemStack target : targets) {
                if (target.isEmpty()){
                    continue;
                }
                if (ItemStack.isSameItemSameTags(existing, target)){
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(EnchantmentModule.builder(Enchantments.SILK_TOUCH).toolItem(harvest).constant());
        hookBuilder.addModule(EnchantmentModule.builder(Enchantments.SILK_TOUCH).toolItem(armor).armorHarvest(ARMOR_SLOTS));
        hookBuilder.addHook(this, ModifierHooks.PROCESS_LOOT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public int getPriority() {
        return 10000;
    }

    @Override
    public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context) {
        if (Boolean.TRUE.equals(IN_PROGRESS.get()))
            return;
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (state == null)
            return;
        if (state.getBlock().asItem() == Items.AIR)
            return;

        ServerLevel level = context.getLevel();

        ItemStack originalTool = context.getParamOrNull(LootContextParams.TOOL);
        if (originalTool == null || originalTool.isEmpty()){
            return;
        }

        ItemStack silkTool = originalTool.copy();
        silkTool.enchant(Enchantments.SILK_TOUCH, 1);
        IN_PROGRESS.set(true);
        try {
            BlockEntity blockEntity = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
            Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
            Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
            BlockPos pos = null != origin ? BlockPos.containing(origin) : null;
            List<ItemStack> silkDrops = null != pos ? Block.getDrops(state, level, pos, blockEntity, entity, silkTool) : List.of();

            // 只删掉与 silk 结果重复的项
            removeMatchingItemTypes(generatedLoot, silkDrops);

            // 再加入原方块
            generatedLoot.add(new ItemStack(state.getBlock()));
        }
        finally {
            IN_PROGRESS.set(false);
        }
    }
}
