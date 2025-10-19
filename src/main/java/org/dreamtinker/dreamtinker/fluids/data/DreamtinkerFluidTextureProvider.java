package org.dreamtinker.dreamtinker.fluids.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.fluids.DreamtinkerFluids;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.fluid.texture.AbstractFluidTextureProvider;

import java.util.Objects;

public class DreamtinkerFluidTextureProvider extends AbstractFluidTextureProvider {
    public DreamtinkerFluidTextureProvider(PackOutput packOutput) {
        super(packOutput, Dreamtinker.MODID);
    }

    @Override
    public void addTextures() {
        this.commonFluid(DreamtinkerFluids.molten_echo_shard.getType());
        this.commonFluid(DreamtinkerFluids.molten_echo_alloy.getType());
        this.commonFluid(DreamtinkerFluids.molten_nigrescence_antimony.getType());
        this.commonFluid(DreamtinkerFluids.molten_albedo_stibium.getType());
        this.commonFluid(DreamtinkerFluids.molten_lupi_antimony.getType());
        this.commonFluid(DreamtinkerFluids.molten_ascending_antimony.getType());
        this.commonFluid(DreamtinkerFluids.liquid_smoky_antimony.getType());
        this.commonFluid(DreamtinkerFluids.molten_crying_obsidian.getType());
        this.commonFluid(DreamtinkerFluids.molten_void.getType());
        this.commonFluid(DreamtinkerFluids.liquid_trist.getType());


        this.commonFluid(DreamtinkerFluids.unstable_liquid_aether.getType());
        this.commonFluid(DreamtinkerFluids.liquid_pure_soul.getType());
        this.commonFluid(DreamtinkerFluids.molten_nefariousness.getType());
        this.commonFluid(DreamtinkerFluids.molten_evil.getType());
        this.commonFluid(DreamtinkerFluids.molten_soul_aether.getType());
        this.commonFluid(DreamtinkerFluids.unholy_water.getType());
        this.commonFluid(DreamtinkerFluids.reversed_shadow.getType());
        this.commonFluid(DreamtinkerFluids.blood_soul.getType());
        this.commonFluid(DreamtinkerFluids.molten_soul_stained_steel.getType());
        this.commonFluid(DreamtinkerFluids.molten_malignant_pewter.getType());
        this.commonFluid(DreamtinkerFluids.molten_malignant_gluttony.getType());
        this.commonFluid(DreamtinkerFluids.liquid_arcana_juice.getType());
        this.commonFluid(DreamtinkerFluids.liquid_concentrated_gluttony.getType());
    }

    public void commonFluid(FluidType fluid) {
        super.texture(fluid)
             .textures(Dreamtinker.getLocation("fluid/" + Objects.requireNonNull(ForgeRegistries.FLUID_TYPES.get().getKey(fluid)).getPath() + "/"), false,
                       false);
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Fluid Texture Provider";
    }
}
