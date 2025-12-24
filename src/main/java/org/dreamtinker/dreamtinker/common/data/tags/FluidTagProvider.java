package org.dreamtinker.dreamtinker.common.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.fluids.DreamtinkerFluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class FluidTagProvider extends FluidTagsProvider {
    public FluidTagProvider(PackOutput p_255941_, CompletableFuture<HolderLookup.Provider> p_256600_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_255941_, p_256600_, modId, existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.@NotNull Provider Provider) {
        fluidTag(DreamtinkerFluids.molten_echo_shard);
        fluidTag(DreamtinkerFluids.molten_echo_alloy);
        fluidTag(DreamtinkerFluids.molten_nigrescence_antimony);
        fluidTag(DreamtinkerFluids.molten_albedo_stibium);
        fluidTag(DreamtinkerFluids.molten_lupi_antimony);
        fluidTag(DreamtinkerFluids.molten_ascending_antimony);
        fluidTag(DreamtinkerFluids.liquid_smoky_antimony);
        fluidTag(DreamtinkerFluids.molten_crying_obsidian);
        fluidTag(DreamtinkerFluids.liquid_trist);
        fluidTag(DreamtinkerFluids.molten_void);
        fluidTag(DreamtinkerFluids.unstable_liquid_aether);
        fluidTag(DreamtinkerFluids.liquid_pure_soul);
        fluidTag(DreamtinkerFluids.molten_nefariousness);
        fluidTag(DreamtinkerFluids.molten_evil);
        fluidTag(DreamtinkerFluids.molten_soul_aether);
        fluidTag(DreamtinkerFluids.unholy_water);
        fluidTag(DreamtinkerFluids.reversed_shadow);
        fluidTag(DreamtinkerFluids.blood_soul);
        fluidTag(DreamtinkerFluids.molten_soul_stained_steel);
        fluidTag(DreamtinkerFluids.molten_malignant_pewter);
        fluidTag(DreamtinkerFluids.molten_malignant_gluttony);
        fluidTag(DreamtinkerFluids.liquid_concentrated_gluttony);
        fluidTag(DreamtinkerFluids.liquid_arcana_juice);
        fluidTag(DreamtinkerFluids.liquid_amber);
        fluidTag(DreamtinkerFluids.molten_desire);
        fluidTag(DreamtinkerFluids.despair_essence);
        fluidTag(DreamtinkerFluids.molten_soul_steel);
        fluidTag(DreamtinkerFluids.half_festering_blood);
        fluidTag(DreamtinkerFluids.festering_blood);
        fluidTag(DreamtinkerFluids.rainbow_honey);
        fluidTag(DreamtinkerFluids.molten_bee_gem);
        fluidTag(DreamtinkerFluids.molten_black_sapphire);
        fluidTag(DreamtinkerFluids.molten_scolecite);
        fluidTag(DreamtinkerFluids.molten_orichalcum);
        fluidTag(DreamtinkerFluids.molten_cold_iron);
        fluidTag(DreamtinkerFluids.molten_shadow_silver);
        fluidTag(DreamtinkerFluids.molten_transmutation_gold);
        fluidTag(DreamtinkerFluids.mercury);
        fluidTag(DreamtinkerFluids.molten_arcane_gold);
        fluidTag(DreamtinkerFluids.molten_dark_metal);

        this.tag(DreamtinkerTagKeys.Fluids.narcissus_wing_used).addTags(DreamtinkerFluids.blood_soul.getTag())
            .addOptionalTags(DreamtinkerFluids.liquid_arcana_juice.getTag(), DreamtinkerFluids.liquid_concentrated_gluttony.getTag(),
                             DreamtinkerFluids.mercury.getTag(), DreamtinkerFluids.unholy_water.getTag());

        tag(TinkerTags.Fluids.GLASS_TOOLTIPS).addTag(DreamtinkerFluids.molten_crying_obsidian.getTag());
        this.tag(TinkerTags.Fluids.METAL_TOOLTIPS)
            .addTags(DreamtinkerFluids.molten_lupi_antimony.getTag(), DreamtinkerFluids.molten_soul_steel.getTag(),
                     DreamtinkerFluids.molten_orichalcum.getTag(), DreamtinkerFluids.molten_cold_iron.getTag(), DreamtinkerFluids.molten_shadow_silver.getTag(),
                     DreamtinkerFluids.molten_transmutation_gold.getTag(), DreamtinkerFluids.molten_arcane_gold.getTag())
            .addOptionalTags(DreamtinkerFluids.molten_evil.getTag(), DreamtinkerFluids.molten_soul_aether.getTag(),
                             DreamtinkerFluids.molten_soul_stained_steel.getTag(), DreamtinkerFluids.molten_malignant_pewter.getTag(),
                             DreamtinkerFluids.molten_malignant_gluttony.getTag(),
                             DreamtinkerFluids.molten_dark_metal.getTag());
        this.tag(DreamtinkerTagKeys.Fluids.molten_echo_shard).addTags(DreamtinkerFluids.molten_echo_shard.getTag());
        this.tag(DreamtinkerTagKeys.Fluids.molten_crying_obsidian).addTags(DreamtinkerFluids.molten_crying_obsidian.getTag());
        this.tag(DreamtinkerTagKeys.Fluids.molten_orichalcum).addTags(DreamtinkerFluids.molten_orichalcum.getTag());
        this.tag(DreamtinkerTagKeys.Fluids.molten_arcane_gold).addTags(DreamtinkerFluids.molten_arcane_gold.getTag());
        this.tag(TinkerTags.Fluids.SLIME_TOOLTIPS).addTags(DreamtinkerFluids.reversed_shadow.getTag(), DreamtinkerFluids.molten_void.getTag());
    }

    /**
     * Adds tags for an unplacable fluid
     */
    private void fluidTag(FluidObject<?> fluid) {
        tag(Objects.requireNonNull(fluid.getCommonTag())).add(fluid.get());
    }

    /**
     * Adds tags for a placable fluid
     */
    private void fluidTag(FlowingFluidObject<?> fluid) {
        tag(fluid.getLocalTag()).add(fluid.getStill(), fluid.getFlowing());
        TagKey<Fluid> tag = fluid.getCommonTag();
        if (tag != null){
            tag(tag).addTag(fluid.getLocalTag());
        }
    }
}
