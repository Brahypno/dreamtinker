package org.dreamtinker.dreamtinker.data.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.data.DreamtinkerTagkeys;
import org.dreamtinker.dreamtinker.register.DreamtinkerFluid;
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
        tag(DreamtinkerTagkeys.Fluids.molten_crying_obsidian)
                .add(DreamtinkerFluid.molten_crying_obsidian.getStill(), DreamtinkerFluid.molten_crying_obsidian.getFlowing());

        fluidTag(DreamtinkerFluid.molten_echo_shard);
        fluidTag(DreamtinkerFluid.molten_nigrescence_antimony);
        fluidTag(DreamtinkerFluid.molten_albedo_stibium);
        fluidTag(DreamtinkerFluid.molten_lupi_antimony);
        fluidTag(DreamtinkerFluid.molten_ascending_antimony);
        fluidTag(DreamtinkerFluid.liquid_smoky_antimony);
        fluidTag(DreamtinkerFluid.molten_crying_obsidian);
        tag(TinkerTags.Fluids.GLASS_TOOLTIPS).addTag(DreamtinkerFluid.molten_crying_obsidian.getTag());
        this.tag(TinkerTags.Fluids.METAL_TOOLTIPS).addTag(DreamtinkerFluid.molten_lupi_antimony.getTag());

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
