package org.brahypno.dreamtinker.library.modifiers.modules.harvest;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.json.RandomLevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public record BlockLootMultiplierModule(IJsonPredicate<BlockState> block, IJsonPredicate<Item> items, IJsonPredicate<LivingEntity> holder,
                                        RandomLevelingValue times,
                                        LevelingValue chance,
                                        ModifierCondition<IToolStackView> condition) implements ProcessLootModifierHook, ModifierModule, ModifierCondition.ConditionalModule<IToolStackView> {

    public static final RecordLoadable<BlockLootMultiplierModule> LOADER = RecordLoadable.create(
            BlockPredicate.LOADER.defaultField("blocks", BlockLootMultiplierModule::block),
            ItemPredicate.LOADER.defaultField("items", BlockLootMultiplierModule::items),
            LivingEntityPredicate.LOADER.defaultField("holder", BlockLootMultiplierModule::holder),
            RandomLevelingValue.LOADABLE.requiredField("times", BlockLootMultiplierModule::times),
            LevelingValue.LOADABLE.defaultField("chance", LevelingValue.eachLevel(0.25f), false, BlockLootMultiplierModule::chance),
            ModifierCondition.TOOL_FIELD,
            BlockLootMultiplierModule::new);

    private static final ThreadLocal<Boolean> IN_PROGRESS = ThreadLocal.withInitial(() -> false);
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<BlockLootMultiplierModule>defaultHooks(ModifierHooks.PROCESS_LOOT);

    @ApiStatus.Internal
    public BlockLootMultiplierModule {
    }

    public static BlockLootMultiplierModule.Builder builder() {
        return new BlockLootMultiplierModule.Builder();
    }

    public @NotNull Integer getPriority() {
        return -1000;
    }

    @Override
    public void processLoot(
            IToolStackView tool, ModifierEntry modifier,
            List<ItemStack> generatedLoot, LootContext context) {

        // 1) 只处理 BLOCK loot，且防递归
        if (!context.hasParam(LootContextParams.BLOCK_STATE) || !this.condition.matches(tool, modifier))
            return;
        if (Boolean.TRUE.equals(IN_PROGRESS.get()))
            return;

        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (state == null || !this.block.matches(state))
            return;

        ServerLevel level = context.getLevel();

        // 3) 概率判定前置
        float scaledLevel = modifier.getEffectiveLevel();
        float chance = this.chance.compute(scaledLevel);
        if (chance <= 0f)
            return;
        if (chance < 1f && level.random.nextFloat() >= chance)
            return;

        // 4) rolls 计算前置
        int rolls = Math.round(this.times.computeValue(scaledLevel));
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
                    if (!this.items.matches(drop.getItem())){
                        generatedLoot.add(drop);
                    }
                }
            }
        }
        finally {
            IN_PROGRESS.set(false);
        }
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<BlockLootMultiplierModule> getLoader() {
        return LOADER;
    }

    /**
     * Builder for this modifier in datagen
     */
    public static class Builder extends ModuleBuilder.Stack<BlockLootMultiplierModule.Builder> {
        private IJsonPredicate<BlockState> blocks = BlockPredicate.ANY;
        private IJsonPredicate<Item> items = ItemPredicate.ANY;
        private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;
        private RandomLevelingValue times = RandomLevelingValue.flat(1);
        private LevelingValue chance = LevelingValue.eachLevel(0.25f);

        public BlockLootMultiplierModule.Builder blocks(IJsonPredicate<BlockState> blocks) {
            this.blocks = blocks;
            return this;
        }

        public BlockLootMultiplierModule.Builder items(IJsonPredicate<Item> items) {
            this.items = items;//This is used to filer out what we dont want duplicate!
            return this;
        }

        public BlockLootMultiplierModule.Builder holder(IJsonPredicate<LivingEntity> holder) {
            this.holder = holder;
            return this;
        }

        public BlockLootMultiplierModule.Builder times(RandomLevelingValue times) {
            this.times = times;
            return this;
        }

        public BlockLootMultiplierModule.Builder chance(LevelingValue chance) {
            this.chance = chance;
            return this;
        }


        /**
         * Builds the finished modifier
         */
        public BlockLootMultiplierModule build() {
            return new BlockLootMultiplierModule(blocks, items, holder, times, chance, condition);
        }
    }
}
