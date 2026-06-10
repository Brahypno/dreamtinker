package org.brahypno.dreamtinker.library.modifiers.fluid.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Chooses one non-ignored block tag from the hit block, then transforms the block
 * into another block from that same tag.
 * ignoredTags means:
 * "do not use these tags as transform pools"
 * It does NOT mean:
 * "blocks in these tags cannot transform"
 */
public record AutoTagCycleBlockFluidEffect(
        List<ResourceLocation> ignoredTags,
        Mode mode,
        boolean skipSelf,
        float chance,
        int minPoolSize,
        int maxPoolSize
) implements FluidEffect<FluidEffectContext.Block> {

    public static final RecordLoadable<AutoTagCycleBlockFluidEffect> LOADER = RecordLoadable.create(
            // 如果你的 Mantle 版本不是 .list()，这里按本地 Loadable API 改成对应 list loadable。
            Loadables.RESOURCE_LOCATION.list().defaultField(
                    "ignored_tags",
                    defaultIgnoredTags(),
                    AutoTagCycleBlockFluidEffect::ignoredTags
            ),
            Mode.LOADABLE.defaultField("mode", Mode.RANDOM, AutoTagCycleBlockFluidEffect::mode),
            BooleanLoadable.INSTANCE.defaultField("skip_self", true, AutoTagCycleBlockFluidEffect::skipSelf),
            FloatLoadable.FROM_ZERO.defaultField("chance", 1.0f, AutoTagCycleBlockFluidEffect::chance),
            IntLoadable.FROM_ONE.defaultField("min_pool_size", 2, AutoTagCycleBlockFluidEffect::minPoolSize),
            IntLoadable.FROM_ONE.defaultField("max_pool_size", 64, AutoTagCycleBlockFluidEffect::maxPoolSize),
            AutoTagCycleBlockFluidEffect::new
    );

    public AutoTagCycleBlockFluidEffect() {
        this(defaultIgnoredTags(), Mode.RANDOM, true, 1.0f, 2, 64);
    }

    public AutoTagCycleBlockFluidEffect(float chance) {
        this(defaultIgnoredTags(), Mode.RANDOM, true, chance, 2, 64);
    }

    public AutoTagCycleBlockFluidEffect(List<ResourceLocation> ignoredTags, float chance) {
        this(ignoredTags, Mode.RANDOM, true, chance, 2, 64);
    }

    private static Block nextBlock(Block originalBlock, List<Block> candidates) {
        int originalId = BuiltInRegistries.BLOCK.getId(originalBlock);

        Block best = candidates.get(0);
        int bestId = Integer.MAX_VALUE;

        for (Block block : candidates) {
            int id = BuiltInRegistries.BLOCK.getId(block);

            if (id > originalId && id < bestId){
                best = block;
                bestId = id;
            }
        }

        return bestId == Integer.MAX_VALUE ? candidates.get(0) : best;
    }

    public static List<ResourceLocation> defaultIgnoredTags() {
        return List.of(
                // mining/tool tags
                new ResourceLocation("minecraft", "mineable/pickaxe"),
                new ResourceLocation("minecraft", "mineable/axe"),
                new ResourceLocation("minecraft", "mineable/shovel"),
                new ResourceLocation("minecraft", "mineable/hoe"),
                new ResourceLocation("minecraft", "needs_stone_tool"),
                new ResourceLocation("minecraft", "needs_iron_tool"),
                new ResourceLocation("minecraft", "needs_diamond_tool"),

                // broad vanilla behavior tags
                new ResourceLocation("minecraft", "replaceable"),
                new ResourceLocation("minecraft", "sword_efficient"),
                new ResourceLocation("minecraft", "features_cannot_replace"),
                new ResourceLocation("minecraft", "lava_pool_stone_cannot_replace"),
                new ResourceLocation("minecraft", "geode_invalid_blocks"),

                // overly broad families
                new ResourceLocation("minecraft", "logs"),
                new ResourceLocation("minecraft", "leaves"),
                new ResourceLocation("minecraft", "planks"),
                new ResourceLocation("minecraft", "stairs"),
                new ResourceLocation("minecraft", "slabs"),
                new ResourceLocation("minecraft", "walls"),
                new ResourceLocation("minecraft", "doors"),
                new ResourceLocation("minecraft", "trapdoors"),
                new ResourceLocation("minecraft", "fences"),

                // overly broad forge tags
                new ResourceLocation("forge", "ores"),
                new ResourceLocation("forge", "storage_blocks"),
                new ResourceLocation("forge", "stone"),
                new ResourceLocation("forge", "cobblestone"),
                new ResourceLocation("forge", "ores_in_ground/stone"),
                Tags.Blocks.ORE_RATES_DENSE.location(),
                Tags.Blocks.ORE_RATES_SINGULAR.location(),
                Tags.Blocks.ORE_RATES_SPARSE.location()
        );
    }

    @Override
    public RecordLoadable<AutoTagCycleBlockFluidEffect> getLoader() {
        return LOADER;
    }

    @Override
    public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext.Block context, FluidAction action) {
        float value = level.value();
        if (value <= 0){
            return 0;
        }

        BlockState originalState = context.getBlockState();
        if (originalState.isAir()){
            return 0;
        }

        Level world = context.getLevel();
        BlockPos pos = context.getBlockPos();
        Block originalBlock = originalState.getBlock();

        List<TagKey<Block>> usableTags = getUsableTags(originalState, originalBlock);
        if (usableTags.isEmpty()){
            return 0;
        }

        RandomSource random = world.getRandom();

        if (chance < 1.0f && random.nextFloat() > chance){
            return 0;
        }

        TagKey<Block> chosenTag = usableTags.get(random.nextInt(usableTags.size()));
        List<Block> candidates = getCandidates(chosenTag, originalBlock, world, pos);

        if (candidates.isEmpty()){
            return 0;
        }

        Block chosen = switch (mode) {
            case RANDOM -> candidates.get(random.nextInt(candidates.size()));
            case NEXT -> nextBlock(originalBlock, candidates);
        };

        if (chosen == originalBlock){
            return 0;
        }

        BlockState newState = chosen.defaultBlockState();
        if (!newState.canSurvive(world, pos)){
            return 0;
        }

        if (action.execute()){
            world.setBlock(pos, newState, Block.UPDATE_ALL);
            world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(originalState));
        }

        return value;
    }

    /**
     * Gets all tags on the hit block, then removes tags that should not be used as transform pools.
     */
    private List<TagKey<Block>> getUsableTags(BlockState state, Block originalBlock) {
        return state.getTags()
                    .filter(tag -> !ignoredTags.contains(tag.location()))
                    .filter(tag -> {
                        int size = candidateCount(tag, originalBlock);
                        return size >= minPoolSize && size <= maxPoolSize;
                    })
                    .toList();
    }

    private int candidateCount(TagKey<Block> tag, Block originalBlock) {
        return BuiltInRegistries.BLOCK.getTag(tag)
                                      .map(named -> {
                                          int count = 0;

                                          for (Holder<Block> holder : named) {
                                              Block block = holder.value();

                                              if (skipSelf && block == originalBlock){
                                                  continue;
                                              }

                                              count++;
                                          }

                                          return count;
                                      })
                                      .orElse(0);
    }

    private List<Block> getCandidates(TagKey<Block> tag, Block originalBlock, Level world, BlockPos pos) {
        List<Block> candidates = new ArrayList<>();

        BuiltInRegistries.BLOCK.getTag(tag).ifPresent(named -> {
            for (Holder<Block> holder : named) {
                Block block = holder.value();

                if (skipSelf && block == originalBlock){
                    continue;
                }

                BlockState state = block.defaultBlockState();
                if (!state.canSurvive(world, pos)){
                    continue;
                }

                candidates.add(block);
            }
        });

        return candidates;
    }

    @Override
    public Component getDescription(RegistryAccess registryAccess) {
        return Component.translatable(FluidEffect.getTranslationKey(getLoader()));
    }

    public enum Mode {
        RANDOM,
        NEXT;

        public static final EnumLoadable<Mode> LOADABLE = new EnumLoadable<>(Mode.class);
    }
}