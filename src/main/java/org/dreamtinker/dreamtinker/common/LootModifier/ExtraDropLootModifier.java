package org.dreamtinker.dreamtinker.common.LootModifier;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.AntimonyLootChance;
import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.WhitePeachLootChance;

public class ExtraDropLootModifier extends LootModifier {
    private final Item result;
    private final List<TagKey<Block>> target_tags;
    private final Map<Item, Double> rates = Map.of(
            DreamtinkerCommon.raw_stibnite.get(), AntimonyLootChance.get(),
            DreamtinkerCommon.white_peach.get(), WhitePeachLootChance.get()
    );

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<ExtraDropLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst)
            .and(ForgeRegistries.ITEMS.getCodec().fieldOf("result").forGetter(m -> m.result))
            .and(ResourceLocation.CODEC.listOf().optionalFieldOf("target_tags", List.of())
                                       .forGetter(m -> m.target_tags.stream().map(TagKey::location).toList()))
            .apply(inst, (conditions, item, tagLocs) -> {
                List<TagKey<Block>> tags = tagLocs.stream().map(loc -> TagKey.create(Registries.BLOCK, loc)).toList();
                return new ExtraDropLootModifier(conditions, item, tags);
            })
    );


    public ExtraDropLootModifier(LootItemCondition[] conditions, Item result, List<TagKey<Block>> target_tags) {
        super(conditions);
        this.result = result;
        this.target_tags = List.copyOf(target_tags);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> objectArrayList, LootContext lootContext) {
        BlockState state = lootContext.getParamOrNull(LootContextParams.BLOCK_STATE);

        if (state == null || target_tags.stream().noneMatch(state::is))
            return objectArrayList;

        int fortune = 0;

        if (lootContext.hasParam(LootContextParams.TOOL)){
            ItemStack tool = lootContext.getParam(LootContextParams.TOOL);
            fortune = tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
            // 可选：排除精准采集
            if (tool.getEnchantmentLevel(Enchantments.SILK_TOUCH) > 0){
                return objectArrayList;
            }
        }
        float chance = (float) ((fortune + 1 + lootContext.getLuck()) * rates.getOrDefault(result, 1d));
        if (lootContext.getRandom().nextFloat() < chance){
            int amount = 1 + lootContext.getRandom().nextInt((int) (1 + fortune + lootContext.getLuck()));
            objectArrayList.add(new ItemStack(result, amount));
        }

        return objectArrayList;
    }

    public Codec<ExtraDropLootModifier> codec() {
        return CODEC;
    }

}


