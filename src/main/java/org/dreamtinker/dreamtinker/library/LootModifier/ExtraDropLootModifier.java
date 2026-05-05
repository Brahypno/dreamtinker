package org.dreamtinker.dreamtinker.library.LootModifier;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;

import java.util.Optional;

public class ExtraDropLootModifier extends LootModifier {
    private static final Codec<IJsonPredicate<BlockState>> BLOCK_PREDICATE_CODEC =
            loadableCodec(BlockPredicate.LOADER, "blocks");

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Codec<IJsonPredicate<LivingEntity>> ENTITY_PREDICATE_CODEC =
            loadableCodec(LivingEntityPredicate.LOADER, "entities");
    private final Ingredient result;
    private final SourceFilter source;
    private final DropRule drop;

    private static final Codec<Ingredient> INGREDIENT_CODEC = Codec.PASSTHROUGH.xmap(
            dynamic -> Ingredient.fromJson(dynamic.convert(JsonOps.INSTANCE).getValue()),
            ingredient -> new Dynamic<>(JsonOps.INSTANCE, ingredient.toJson())
    );
    public static final Codec<ExtraDropLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst)
            .and(INGREDIENT_CODEC.fieldOf("result")
                                 .forGetter(m -> m.result))

            .and(SourceFilter.CODEC.optionalFieldOf("source", SourceFilter.EMPTY)
                                   .forGetter(m -> m.source))

            .and(DropRule.CODEC.optionalFieldOf("drop", DropRule.DEFAULT)
                               .forGetter(m -> m.drop))

            .apply(inst, ExtraDropLootModifier::new)
    );


    public ExtraDropLootModifier(
            LootItemCondition[] conditions,
            Ingredient result,
            SourceFilter source,
            DropRule drop
    ) {
        super(conditions);
        this.result = result;
        this.source = source;
        this.drop = drop;
    }

    private static <T> Codec<T> loadableCodec(Loadable<T> loadable, String key) {
        return Codec.PASSTHROUGH.xmap(
                dynamic -> loadable.convert(dynamic.convert(JsonOps.INSTANCE).getValue(), key),
                value -> new Dynamic<>(JsonOps.INSTANCE, loadable.serialize(value))
        );
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!matchesSource(context)){
            return generatedLoot;
        }

        int lootBonusLevel = getLootBonusLevel(context);

        double chance = drop.baseChance() * (1.0D + lootBonusLevel * drop.lootBonusChanceBonus() + context.getLuck());

        if (context.getRandom().nextDouble() >= chance){
            return generatedLoot;
        }

        ItemStack stack = pickResult(context);
        if (stack.isEmpty()){
            return generatedLoot;
        }

        int baseCount = Math.max(1, stack.getCount());
        int upperCount = Math.min(drop.maxCount(), baseCount + lootBonusLevel * drop.lootBonusCountBonus());

        int amount = baseCount + context.getRandom().nextInt(upperCount - baseCount + 1);
        stack.setCount(amount);

        generatedLoot.add(stack);
        return generatedLoot;
    }

    private int getLootBonusLevel(LootContext context) {
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (state != null && context.hasParam(LootContextParams.TOOL)){
            ItemStack tool = context.getParam(LootContextParams.TOOL);
            return tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
        }

        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity != null){
            Entity killer = context.getParamOrNull(LootContextParams.KILLER_ENTITY);

            if (killer instanceof LivingEntity livingKiller){
                return EnchantmentHelper.getMobLooting(livingKiller);
            }
        }

        return 0;
    }

    private boolean matchesSource(LootContext context) {
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (state != null && source.hasBlockSelector()){
            return source.blocks().orElseThrow().matches(state);
        }

        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity instanceof LivingEntity living && source.hasEntitySelector()){
            return source.entities().orElseThrow().matches(living);
        }

        /*
         * 没写任何 source 时，允许只靠 conditions 控制。
         * 如果你希望必须写 source，就改成 return false;
         */
        return source.isEmpty();
    }

    private ItemStack pickResult(LootContext context) {
        ItemStack[] stacks = result.getItems();

        if (stacks.length <= 0){
            return ItemStack.EMPTY;
        }

        return stacks[context.getRandom().nextInt(stacks.length)].copy();
    }

    public Codec<ExtraDropLootModifier> codec() {
        return CODEC;
    }

    public record SourceFilter(
            Optional<IJsonPredicate<BlockState>> blocks,
            Optional<IJsonPredicate<LivingEntity>> entities
    ) {
        public static final SourceFilter EMPTY = new SourceFilter(Optional.empty(), Optional.empty());
        public static final Codec<SourceFilter> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                BLOCK_PREDICATE_CODEC.optionalFieldOf("blocks")
                                     .forGetter(SourceFilter::blocks),

                ENTITY_PREDICATE_CODEC.optionalFieldOf("entities")
                                      .forGetter(SourceFilter::entities)
        ).apply(inst, SourceFilter::new));

        public boolean hasBlockSelector() {
            return blocks.isPresent();
        }

        public boolean hasEntitySelector() {
            return entities.isPresent();
        }

        public boolean isEmpty() {
            return blocks.isEmpty() && entities.isEmpty();
        }
    }

    public record DropRule(
            double baseChance,
            double lootBonusChanceBonus,
            int maxCount,
            int lootBonusCountBonus
    ) {
        public static final DropRule DEFAULT = new DropRule(
                1.0D,
                1.0D,
                1,
                0
        );
        public static final Codec<DropRule> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.DOUBLE.optionalFieldOf("base_chance", 1.0D)
                            .forGetter(DropRule::baseChance),

                Codec.DOUBLE.optionalFieldOf("loot_bonus_chance_bonus", 1.0D)
                            .forGetter(DropRule::lootBonusChanceBonus),

                Codec.INT.optionalFieldOf("max_count", 1)
                         .forGetter(DropRule::maxCount),

                Codec.INT.optionalFieldOf("loot_bonus_count_bonus", 0)
                         .forGetter(DropRule::lootBonusCountBonus)
        ).apply(inst, DropRule::new));

        public DropRule {
            baseChance = Math.max(0.0D, baseChance);
            lootBonusChanceBonus = Math.max(0.0D, lootBonusChanceBonus);
            maxCount = Math.max(1, maxCount);
            lootBonusCountBonus = Math.max(0, lootBonusCountBonus);
        }
    }

}


