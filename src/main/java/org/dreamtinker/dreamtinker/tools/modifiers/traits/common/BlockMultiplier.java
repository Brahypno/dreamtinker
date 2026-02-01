package org.dreamtinker.dreamtinker.tools.modifiers.traits.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public class BlockMultiplier extends Modifier implements ProcessLootModifierHook {
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROCESS_LOOT);
    }

    private final ResourceLocation location;
    private final float rate;
    private final int times;
    private final TagKey<Block> blockTag;
    private final TagKey<Item> itemTag;
    private static final ThreadLocal<Boolean> IN_PROGRESS = ThreadLocal.withInitial(() -> false);

    public BlockMultiplier(ResourceLocation location, float rate, int time) {
        this.location = location;
        this.rate = rate;
        this.times = time;
        blockTag = TagKey.create(Registries.BLOCK, location);
        itemTag = TagKey.create(Registries.ITEM, location);
    }

    @Override
    public void processLoot(
            IToolStackView tool, ModifierEntry modifier,
            List<ItemStack> generatedLoot, LootContext context) {

        // 1) 只处理 BLOCK loot，且防递归
        if (!context.hasParam(LootContextParams.BLOCK_STATE))
            return;
        if (Boolean.TRUE.equals(IN_PROGRESS.get()))
            return;

        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (state == null)
            return;

        // 2) tag 判定前置
        if (!state.is(blockTag))
            return;

        ServerLevel level = context.getLevel();

        // 3) 概率判定前置（避免后面一堆对象构造）
        float chance = rate * modifier.getLevel();
        if (chance <= 0f)
            return;
        if (chance < 1f && level.random.nextFloat() >= chance)
            return;

        // 4) rolls 计算前置
        int rolls = Math.min(times, (modifier.getLevel() + 1) / 2);
        if (rolls <= 0)
            return;

        // 5) 拿 lootTable
        ResourceLocation lootId = state.getBlock().getLootTable();
        if (lootId == BuiltInLootTables.EMPTY)
            return;

        LootTable lootTable = level.getServer().getLootData().getLootTable(lootId);

        IN_PROGRESS.set(true);
        try {
            for (int i = 0; i < rolls; i++) {
                LootParams.Builder builder = new LootParams.Builder(level)
                        .withParameter(LootContextParams.ORIGIN, context.getParam(LootContextParams.ORIGIN))
                        .withParameter(LootContextParams.BLOCK_STATE, state);

                // 尽量携带原 context 的参数（有就带）
                if (context.hasParam(LootContextParams.TOOL)){
                    builder.withParameter(LootContextParams.TOOL, context.getParam(LootContextParams.TOOL));
                }
                if (context.hasParam(LootContextParams.THIS_ENTITY)){
                    builder.withParameter(LootContextParams.THIS_ENTITY, context.getParam(LootContextParams.THIS_ENTITY));
                }
                if (context.hasParam(LootContextParams.BLOCK_ENTITY)){
                    builder.withParameter(LootContextParams.BLOCK_ENTITY, context.getParam(LootContextParams.BLOCK_ENTITY));
                }
                if (context.hasParam(LootContextParams.EXPLOSION_RADIUS)){
                    builder.withParameter(LootContextParams.EXPLOSION_RADIUS, context.getParam(LootContextParams.EXPLOSION_RADIUS));
                }

                LootParams params = builder.create(LootContextParamSets.BLOCK);

                // 直接遍历，避免中间 list
                for (ItemStack drop : lootTable.getRandomItems(params)) {
                    if (!drop.isEmpty() && !drop.is(itemTag)){
                        generatedLoot.add(drop);
                    }
                }
            }
        }
        finally {
            IN_PROGRESS.set(false);
        }
    }
}
