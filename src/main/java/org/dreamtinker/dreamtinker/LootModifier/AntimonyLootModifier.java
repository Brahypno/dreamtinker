package org.dreamtinker.dreamtinker.LootModifier;

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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.AntimonyLootChance;

public class AntimonyLootModifier extends LootModifier {
    private final Item antimony;
    private final List<TagKey<Block>> target_tags;

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<AntimonyLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst)
            .and(ForgeRegistries.ITEMS.getCodec().fieldOf("antimony").forGetter(m -> m.antimony))
            .and(ResourceLocation.CODEC.listOf().optionalFieldOf("target_tags", List.of())
                                       .forGetter(m -> m.target_tags.stream().map(TagKey::location).toList()))
            .apply(inst, (conditions, item, tagLocs) -> {
                List<TagKey<Block>> tags = tagLocs.stream().map(loc -> TagKey.create(Registries.BLOCK, loc)).toList();
                return new AntimonyLootModifier(conditions, item, tags);
            })
    );


    public AntimonyLootModifier(LootItemCondition[] conditions, Item antimony, List<TagKey<Block>> target_tags) {
        super(conditions);
        this.antimony = antimony;
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
        float chance = (float) ((fortune + 1 + lootContext.getLuck()) * AntimonyLootChance.get());
        if (lootContext.getRandom().nextFloat() < chance){
            int amount = 1 + lootContext.getRandom().nextInt((int) (1 + fortune + lootContext.getLuck()));
            objectArrayList.add(new ItemStack(antimony, amount));
        }

        return objectArrayList;
    }

    public Codec<AntimonyLootModifier> codec() {
        return CODEC;
    }

}


