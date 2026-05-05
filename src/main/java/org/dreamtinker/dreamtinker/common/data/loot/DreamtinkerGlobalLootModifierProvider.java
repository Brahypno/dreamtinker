package org.dreamtinker.dreamtinker.common.data.loot;

import net.minecraft.advancements.critereon.*;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.library.LootModifier.ExtraDropLootModifier;
import org.dreamtinker.dreamtinker.library.LootModifier.KillerIsEnemyCondition;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;

import java.util.Optional;

public class DreamtinkerGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public DreamtinkerGlobalLootModifierProvider(PackOutput output) {
        super(output, Dreamtinker.MODID);
    }

    private static LootItemCondition noSilkTouch() {
        return InvertedLootItemCondition.invert(
                MatchTool.toolMatches(
                        ItemPredicate.Builder.item()
                                             .hasEnchantment(new EnchantmentPredicate(
                                                     Enchantments.SILK_TOUCH,
                                                     MinMaxBounds.Ints.atLeast(1)
                                             ))
                )
        ).build();
    }

    private static LootItemCondition killerIs(EntityTypePredicate p_36647_) {
        return LootItemEntityPropertyCondition.hasProperties(
                LootContext.EntityTarget.KILLER,
                EntityPredicate.Builder.entity().entityType(p_36647_)
        ).build();
    }

    @Override
    protected void start() {
        addBlockDrops();

        addIronHeartFromIronGolemKilledByHostileMob();
    }

    private void addBlockDrops() {
        add("raw_stibnite_loot",
            ExtraDropBuilder.create(Ingredient.of(org.dreamtinker.dreamtinker.common.DreamtinkerCommon.raw_stibnite.get()))
                            .condition(noSilkTouch())
                            .blocks(BlockPredicate.or(
                                    BlockPredicate.tag(BlockTags.GOLD_ORES),
                                    BlockPredicate.tag(BlockTags.COPPER_ORES),
                                    BlockPredicate.tag(DreamtinkerTagKeys.Blocks.forgeTag("lead")),
                                    BlockPredicate.tag(DreamtinkerTagKeys.Blocks.forgeTag("silver"))
                            ))
                            .drop(0.05D, 1.0D, 16, 1)
                            .build()
        );
        add("white_peach_loot",
            ExtraDropBuilder.create(Ingredient.of(DreamtinkerCommon.white_peach.get()))
                            .condition(noSilkTouch())
                            .blocks(BlockPredicate.tag(DreamtinkerTagKeys.Blocks.drop_peach))
                            .drop(0.2D, 1.0D, 16, 1)
                            .build()
        );
    }

    private void addIronHeartFromIronGolemKilledByHostileMob() {
        ItemStack ironHeart = new ItemStack(Items.IRON_BLOCK);
        ironHeart.setHoverName(Component.translatable("item.dreamtinker.iron_golem_heart").withStyle(style -> style.withItalic(false)));

        add("iron_heart_from_iron_golem",
            ExtraDropBuilder.create(StrictNBTIngredient.of(ironHeart))
                            .condition(KillerIsEnemyCondition.INSTANCE)
                            .entities(LivingEntityPredicate.set(EntityType.IRON_GOLEM))
                            .drop(0.05D, 0.0D, 1, 0)
                            .build()
        );
    }

    private static final class ExtraDropBuilder {
        private final Ingredient result;

        private IJsonPredicate<BlockState> blocks;
        private IJsonPredicate<LivingEntity> entities;

        private double baseChance = 1.0D;
        private double lootBonusChanceBonus = 1.0D;
        private int maxCount = 1;
        private int lootBonusCountBonus = 0;

        private LootItemCondition[] conditions = new LootItemCondition[0];

        private ExtraDropBuilder(Ingredient result) {
            this.result = result;
        }

        public static ExtraDropBuilder create(Ingredient result) {
            return new ExtraDropBuilder(result);
        }

        public ExtraDropBuilder condition(LootItemCondition condition) {
            LootItemCondition[] next = new LootItemCondition[this.conditions.length + 1];
            System.arraycopy(this.conditions, 0, next, 0, this.conditions.length);
            next[this.conditions.length] = condition;
            this.conditions = next;
            return this;
        }

        public ExtraDropBuilder conditions(LootItemCondition... conditions) {
            this.conditions = conditions;
            return this;
        }

        public ExtraDropBuilder blocks(IJsonPredicate<BlockState> blocks) {
            this.blocks = blocks;
            return this;
        }

        public ExtraDropBuilder entities(IJsonPredicate<LivingEntity> entities) {
            this.entities = entities;
            return this;
        }

        public ExtraDropBuilder drop(
                double baseChance,
                double lootBonusChanceBonus,
                int maxCount,
                int lootBonusCountBonus
        ) {
            this.baseChance = baseChance;
            this.lootBonusChanceBonus = lootBonusChanceBonus;
            this.maxCount = maxCount;
            this.lootBonusCountBonus = lootBonusCountBonus;
            return this;
        }

        public ExtraDropLootModifier build() {
            return new ExtraDropLootModifier(
                    conditions,
                    result,
                    new ExtraDropLootModifier.SourceFilter(
                            Optional.ofNullable(blocks),
                            Optional.ofNullable(entities)
                    ),
                    new ExtraDropLootModifier.DropRule(
                            baseChance,
                            lootBonusChanceBonus,
                            maxCount,
                            lootBonusCountBonus
                    )
            );
        }
    }
}
