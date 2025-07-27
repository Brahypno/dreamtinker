package org.dreamtinker.dreamtinker.LootModifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Registry;
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

import java.util.Optional;

public class AntimonyLootModifier extends LootModifier {
    private final Item antimony;
    private final TagKey<Block> target_tag;
    public static final Codec<AntimonyLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst)
            .and(ForgeRegistries.ITEMS.getCodec().fieldOf("antimony").forGetter(mod -> mod.antimony))
            .and(ResourceLocation.CODEC.optionalFieldOf("target_tag").forGetter(m -> {
                if (m.target_tag == null) return Optional.empty();
                return Optional.of(m.target_tag.location());
            }))
            .apply(inst, (conditions, item, tagLocation) -> {
                TagKey<Block> tag = tagLocation.map(loc -> TagKey.create(Registry.BLOCK_REGISTRY, loc)).orElse(null);
                return new AntimonyLootModifier(conditions, item, tag);
            })
    );
    public static final float basechance=0.01f;

    public AntimonyLootModifier(LootItemCondition[] conditions, Item antimony, TagKey<Block> target_tag) {
        super(conditions);
        this.antimony = antimony;
        this.target_tag = target_tag;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> objectArrayList, LootContext lootContext) {
        BlockState state = lootContext.getParamOrNull(LootContextParams.BLOCK_STATE);

        if (state == null|| !state.is(target_tag))
            return objectArrayList;

        int fortune = 0;

        if (lootContext.hasParam(LootContextParams.TOOL)) {
            ItemStack tool = lootContext.getParam(LootContextParams.TOOL);
            fortune = tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
            // 可选：排除精准采集
            if (tool.getEnchantmentLevel(Enchantments.SILK_TOUCH) > 0) {
                return objectArrayList;
            }
        }
        float luckBoost = (float) lootContext.getLuck() * 0.005f;
        float chance = (fortune+1)*basechance+luckBoost;
        if (lootContext.getRandom().nextFloat() < 10) {
            int amount = 1 + lootContext.getRandom().nextInt(1 + fortune);
            objectArrayList.add(new ItemStack(antimony,amount));
        }

        return objectArrayList;
    }

    public Codec<AntimonyLootModifier> codec() {
        return CODEC;
    }

}


