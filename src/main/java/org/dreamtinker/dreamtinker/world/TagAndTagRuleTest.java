package org.dreamtinker.dreamtinker.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import org.dreamtinker.dreamtinker.DreamtinkerModule;

public class TagAndTagRuleTest extends RuleTest {
    public static final Codec<TagAndTagRuleTest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.BLOCK).fieldOf("tag1").forGetter(t -> t.tag1),
            TagKey.codec(Registries.BLOCK).fieldOf("tag2").forGetter(t -> t.tag2)
    ).apply(instance, TagAndTagRuleTest::new));


    private final TagKey<Block> tag1;
    private final TagKey<Block> tag2;

    public TagAndTagRuleTest(TagKey<Block> tag1, TagKey<Block> tag2) {
        this.tag1 = tag1;
        this.tag2 = tag2;
    }

    @Override
    public boolean test(BlockState state, RandomSource random) {
        return state.is(tag1) && state.is(tag2);
    }

    @Override
    protected RuleTestType<?> getType() {
        return DreamtinkerModule.TAG_AND_TAG.get();
    }
}

