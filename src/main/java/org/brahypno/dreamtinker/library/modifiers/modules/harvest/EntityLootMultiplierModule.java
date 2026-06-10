package org.brahypno.dreamtinker.library.modifiers.modules.harvest;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
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

public record EntityLootMultiplierModule(IJsonPredicate<LivingEntity> target, IJsonPredicate<LivingEntity> holder, RandomLevelingValue times,
                                         LevelingValue chance,
                                         ModifierCondition<IToolStackView> condition) implements ProcessLootModifierHook, ModifierModule, ModifierCondition.ConditionalModule<IToolStackView> {

    public static final RecordLoadable<EntityLootMultiplierModule> LOADER = RecordLoadable.create(
            LivingEntityPredicate.LOADER.defaultField("target", EntityLootMultiplierModule::target),
            LivingEntityPredicate.LOADER.defaultField("holder", EntityLootMultiplierModule::holder),
            RandomLevelingValue.LOADABLE.requiredField("times", EntityLootMultiplierModule::times),
            LevelingValue.LOADABLE.defaultField("chance", LevelingValue.eachLevel(0.25f), false, EntityLootMultiplierModule::chance),
            ModifierCondition.TOOL_FIELD,
            EntityLootMultiplierModule::new);

    private static final ThreadLocal<Boolean> IN_PROGRESS = ThreadLocal.withInitial(() -> false);
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<EntityLootMultiplierModule>defaultHooks(ModifierHooks.PROCESS_LOOT);

    public static EntityLootMultiplierModule.Builder builder() {
        return new EntityLootMultiplierModule.Builder();
    }

    public @NotNull Integer getPriority() {
        return -1000;
    }

    @Override
    public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context) {

        // 1) 只处理 ENTITY loot，且防递归
        if (!context.hasParam(LootContextParams.THIS_ENTITY))
            return;
        if (Boolean.TRUE.equals(IN_PROGRESS.get()))
            return;

        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof LivingEntity living) || !this.target.matches(living))
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

        // 5) 拿实体 lootTable
        ResourceLocation lootId = living.getType().getDefaultLootTable();
        if (lootId == BuiltInLootTables.EMPTY)
            return;

        LootTable lootTable = level.getServer().getLootData().getLootTable(lootId);

        IN_PROGRESS.set(true);
        try {
            for (int i = 0; i < rolls; i++) {
                LootParams.Builder builder = new LootParams.Builder(level)
                        .withParameter(LootContextParams.THIS_ENTITY, living)
                        .withParameter(LootContextParams.ORIGIN, context.getParam(LootContextParams.ORIGIN))
                        .withParameter(LootContextParams.DAMAGE_SOURCE, context.getParam(LootContextParams.DAMAGE_SOURCE));

                // 尽量携带原 context 的参数（有就带）
                if (context.hasParam(LootContextParams.KILLER_ENTITY)){
                    builder.withParameter(LootContextParams.KILLER_ENTITY, context.getParam(LootContextParams.KILLER_ENTITY));
                }
                if (context.hasParam(LootContextParams.DIRECT_KILLER_ENTITY)){
                    builder.withParameter(LootContextParams.DIRECT_KILLER_ENTITY, context.getParam(LootContextParams.DIRECT_KILLER_ENTITY));
                }
                if (context.hasParam(LootContextParams.LAST_DAMAGE_PLAYER)){
                    builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, context.getParam(LootContextParams.LAST_DAMAGE_PLAYER));
                }

                LootParams params = builder.create(LootContextParamSets.ENTITY);

                generatedLoot.addAll(lootTable.getRandomItems(params));
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
    public RecordLoadable<EntityLootMultiplierModule> getLoader() {
        return LOADER;
    }

    /**
     * Builder for this modifier in datagen
     */
    public static class Builder extends ModuleBuilder.Stack<EntityLootMultiplierModule.Builder> {
        private IJsonPredicate<LivingEntity> target = LivingEntityPredicate.ANY;
        private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;
        private RandomLevelingValue times = RandomLevelingValue.flat(1);
        private LevelingValue chance = LevelingValue.eachLevel(0.25f);

        public EntityLootMultiplierModule.Builder target(IJsonPredicate<LivingEntity> target) {
            this.target = target;
            return this;
        }

        public EntityLootMultiplierModule.Builder holder(IJsonPredicate<LivingEntity> holder) {
            this.holder = holder;
            return this;
        }

        public EntityLootMultiplierModule.Builder times(RandomLevelingValue times) {
            this.times = times;
            return this;
        }

        public EntityLootMultiplierModule.Builder chance(LevelingValue chance) {
            this.chance = chance;
            return this;
        }


        /**
         * Builds the finished modifier
         */
        public EntityLootMultiplierModule build() {
            return new EntityLootMultiplierModule(target, holder, times, chance, condition);
        }
    }
}
