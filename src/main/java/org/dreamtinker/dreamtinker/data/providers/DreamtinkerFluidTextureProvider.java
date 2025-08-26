package org.dreamtinker.dreamtinker.data.providers;

import net.minecraft.data.PackOutput;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerFluids;
import slimeknights.mantle.fluid.texture.AbstractFluidTextureProvider;
import slimeknights.mantle.fluid.texture.FluidTexture;

public class DreamtinkerFluidTextureProvider extends AbstractFluidTextureProvider {
    public DreamtinkerFluidTextureProvider(PackOutput packOutput) {
        super(packOutput, Dreamtinker.MODID);
    }

    @Override
    public void addTextures() {
        this.commonFluid(DreamtinkerFluids.molten_echo_shard.getType());
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
    }

    public FluidTexture.Builder commonFluid(FluidType fluid) {
        return super.texture(fluid)
                    .textures(Dreamtinker.getLocation("fluid/" + ForgeRegistries.FLUID_TYPES.get().getKey(fluid).getPath() + "/"), false, false);
    }

    @Override
    public String getName() {
        return "Dreamtinker Fluid Texture Provider";
    }
}
