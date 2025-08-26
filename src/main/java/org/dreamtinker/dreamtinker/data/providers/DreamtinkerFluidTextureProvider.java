package org.dreamtinker.dreamtinker.data.providers;

import net.minecraft.data.PackOutput;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerFluid;
import slimeknights.mantle.fluid.texture.AbstractFluidTextureProvider;
import slimeknights.mantle.fluid.texture.FluidTexture;

public class DreamtinkerFluidTextureProvider extends AbstractFluidTextureProvider {
    public DreamtinkerFluidTextureProvider(PackOutput packOutput) {
        super(packOutput, Dreamtinker.MODID);
    }

    @Override
    public void addTextures() {
        this.commonFluid(DreamtinkerFluid.molten_echo_shard.getType());
        this.commonFluid(DreamtinkerFluid.molten_nigrescence_antimony.getType());
        this.commonFluid(DreamtinkerFluid.molten_albedo_stibium.getType());
        this.commonFluid(DreamtinkerFluid.molten_lupi_antimony.getType());
        this.commonFluid(DreamtinkerFluid.molten_ascending_antimony.getType());
        this.commonFluid(DreamtinkerFluid.liquid_smoky_antimony.getType());
        this.commonFluid(DreamtinkerFluid.molten_crying_obsidian.getType());
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
