package org.brahypno.dreamtinker.common.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.brahypno.dreamtinker.common.DreamtinkerTagKeys;
import org.brahypno.dreamtinker.fluids.DreamtinkerFluids;
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
        fluidTag(DreamtinkerFluids.molten_ender_ash);
        fluidTag(DreamtinkerFluids.molten_utherium);
        fluidTag(DreamtinkerFluids.molten_forgotten_metal);
        fluidTag(DreamtinkerFluids.molten_cloggrum);
        fluidTag(DreamtinkerFluids.molten_froststeel);
        fluidTag(DreamtinkerFluids.molten_regalium);
        fluidTag(DreamtinkerFluids.gooey_slime);
        fluidTag(DreamtinkerFluids.molten_iesnium);
        fluidTag(DreamtinkerFluids.molten_iron_heart);
        fluidTag(DreamtinkerFluids.molten_atonement_silver);
        fluidTag(DreamtinkerFluids.snake_essence);
        fluidTag(DreamtinkerFluids.unmelting_teardrop);
        fluidTag(DreamtinkerFluids.molten_enderitium);

        tag(TinkerTags.Fluids.GLASS_TOOLTIPS).addTag(DreamtinkerFluids.molten_crying_obsidian.getTag());
        this.tag(TinkerTags.Fluids.METAL_TOOLTIPS)
            .addTags(DreamtinkerFluids.molten_lupi_antimony.getTag(), DreamtinkerFluids.molten_soul_steel.getTag(),
                     DreamtinkerFluids.molten_orichalcum.getTag(), DreamtinkerFluids.molten_cold_iron.getTag(), DreamtinkerFluids.molten_shadow_silver.getTag(),
                     DreamtinkerFluids.molten_transmutation_gold.getTag(), DreamtinkerFluids.molten_iron_heart.getTag(),
                     DreamtinkerFluids.molten_atonement_silver.getTag())
            .addOptionalTags(DreamtinkerFluids.molten_evil.getTag(), DreamtinkerFluids.molten_soul_aether.getTag(),
                             DreamtinkerFluids.molten_soul_stained_steel.getTag(), DreamtinkerFluids.molten_malignant_pewter.getTag(),
                             DreamtinkerFluids.molten_malignant_gluttony.getTag(),
                             DreamtinkerFluids.molten_dark_metal.getTag(), DreamtinkerFluids.molten_arcane_gold.getTag(),
                             DreamtinkerFluids.molten_utherium.getTag(), DreamtinkerFluids.molten_forgotten_metal.getTag(),
                             DreamtinkerFluids.molten_cloggrum.getTag(), DreamtinkerFluids.molten_froststeel.getTag(),
                             DreamtinkerFluids.molten_regalium.getTag(), DreamtinkerFluids.molten_iesnium.getTag(),
                             DreamtinkerFluids.molten_enderitium.getTag());
        this.tag(TinkerTags.Fluids.LARGE_GEM_TOOLTIPS)
            .addTags(DreamtinkerFluids.molten_echo_alloy.getTag(), DreamtinkerFluids.molten_black_sapphire.getTag());
        this.tag(TinkerTags.Fluids.CLAY_TOOLTIPS)
            .addTags(DreamtinkerFluids.molten_ender_ash.getTag());
        this.tag(DreamtinkerTagKeys.Fluids.molten_echo_shard).addTags(DreamtinkerFluids.molten_echo_shard.getTag());
        this.tag(DreamtinkerTagKeys.Fluids.molten_echo).addTags(DreamtinkerFluids.molten_echo_shard.getTag());//Maybe someone like name of molten echo? IDK
        this.tag(DreamtinkerTagKeys.Fluids.molten_crying_obsidian).addTags(DreamtinkerFluids.molten_crying_obsidian.getTag());
        this.tag(DreamtinkerTagKeys.Fluids.molten_orichalcum).addTags(DreamtinkerFluids.molten_orichalcum.getTag());
        this.tag(DreamtinkerTagKeys.Fluids.molten_arcane_gold).addTags(DreamtinkerFluids.molten_arcane_gold.getTag());
        this.tag(TinkerTags.Fluids.SLIME_TOOLTIPS)
            .addTags(DreamtinkerFluids.reversed_shadow.getTag(), DreamtinkerFluids.molten_void.getTag(), DreamtinkerFluids.gooey_slime.getTag());
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
