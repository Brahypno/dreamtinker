package org.dreamtinker.dreamtinker.data.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.data.DreamtinkerTagkeys;
import org.dreamtinker.dreamtinker.register.DreamtinkerFluids;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DreamtinkerFluidTagProvider extends FluidTagsProvider {
    public DreamtinkerFluidTagProvider(PackOutput p_255941_, CompletableFuture<HolderLookup.Provider> p_256600_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_255941_, p_256600_, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider Provider) {
        addFullTag(DreamtinkerFluids.molten_echo_shard, DreamtinkerTagkeys.Fluids.molten_echo_shard);
        addFullTag(DreamtinkerFluids.molten_nigrescence_antimony, DreamtinkerTagkeys.Fluids.molten_nigrescence_antimony);
        addFullTag(DreamtinkerFluids.molten_albedo_stibium, DreamtinkerTagkeys.Fluids.molten_albedo_stibium);
        addFullTag(DreamtinkerFluids.molten_lupi_antimony, DreamtinkerTagkeys.Fluids.molten_lupi_antimony);
        addFullTag(DreamtinkerFluids.molten_ascending_antimony, DreamtinkerTagkeys.Fluids.molten_ascending_antimony);
        addFullTag(DreamtinkerFluids.liquid_smoky_antimony, DreamtinkerTagkeys.Fluids.liquid_smoky_antimony);
        addFullTag(DreamtinkerFluids.molten_crying_obsidian, DreamtinkerTagkeys.Fluids.molten_crying_obsidian);
        addFullTag(DreamtinkerFluids.liquid_trist, DreamtinkerTagkeys.Fluids.liquid_trist);
        addFullTag(DreamtinkerFluids.molten_void, DreamtinkerTagkeys.Fluids.molten_void);
        addFullTag(DreamtinkerFluids.unstable_liquid_aether, DreamtinkerTagkeys.Fluids.unstable_liquid_aether);
        addFullTag(DreamtinkerFluids.liquid_pure_soul, DreamtinkerTagkeys.Fluids.liquid_pure_soul);
        addFullTag(DreamtinkerFluids.molten_nefariousness, DreamtinkerTagkeys.Fluids.molten_nefariousness);
        addFullTag(DreamtinkerFluids.molten_evil, DreamtinkerTagkeys.Fluids.molten_evil);
        addFullTag(DreamtinkerFluids.molten_soul_aether, DreamtinkerTagkeys.Fluids.molten_soul_aether);
        addFullTag(DreamtinkerFluids.unholy_water, DreamtinkerTagkeys.Fluids.unholy_water);
        addFullTag(DreamtinkerFluids.reversed_shadow, DreamtinkerTagkeys.Fluids.reversed_shadow);
        tag(TinkerTags.Fluids.GLASS_TOOLTIPS).addTag(DreamtinkerFluids.molten_crying_obsidian.getTag());
        this.tag(TinkerTags.Fluids.METAL_TOOLTIPS)
            .addTags(DreamtinkerFluids.molten_lupi_antimony.getTag(), DreamtinkerFluids.molten_evil.getTag(), DreamtinkerFluids.molten_soul_aether.getTag());
    }

    private void addFullTag(FlowingFluidObject<?> fluid, TagKey<Fluid> fluidTagKey) {
        fluidTag(fluid);
        this.tag(fluidTagKey).add(fluid.getStill(), fluid.getFlowing());
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
