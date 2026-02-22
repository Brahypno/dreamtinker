package org.dreamtinker.dreamtinker.fluids.data;

import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.data.PackOutput;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.fluids.DreamtinkerFluids;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.fluid.texture.AbstractFluidTextureProvider;
import slimeknights.mantle.fluid.texture.FluidTexture;
import slimeknights.mantle.registration.object.FluidObject;

import java.util.Objects;

import static slimeknights.tconstruct.TConstruct.getResource;

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
        this.commonFluid(DreamtinkerFluids.liquid_amber.getType());
        this.commonFluid(DreamtinkerFluids.molten_desire.getType());
        this.commonFluid(DreamtinkerFluids.despair_essence.getType());
        this.commonFluid(DreamtinkerFluids.molten_soul_steel.getType());
        this.commonFluid(DreamtinkerFluids.half_festering_blood.getType());
        this.commonFluid(DreamtinkerFluids.festering_blood.getType());
        this.commonFluid(DreamtinkerFluids.rainbow_honey.getType());
        this.commonFluid(DreamtinkerFluids.molten_bee_gem.getType());
        this.commonFluid(DreamtinkerFluids.molten_black_sapphire.getType());
        this.commonFluid(DreamtinkerFluids.molten_scolecite.getType());
        this.commonFluid(DreamtinkerFluids.molten_orichalcum.getType());
        this.commonFluid(DreamtinkerFluids.molten_cold_iron.getType());
        this.commonFluid(DreamtinkerFluids.molten_shadow_silver.getType());
        this.commonFluid(DreamtinkerFluids.molten_transmutation_gold.getType());
        this.commonFluid(DreamtinkerFluids.mercury.getType());
        this.commonFluid(DreamtinkerFluids.molten_arcane_gold.getType());
        this.commonFluid(DreamtinkerFluids.molten_dark_metal.getType());
        //this.commonFluid(DreamtinkerFluids.molten_ender_ash.getType());
        tintedStone(DreamtinkerFluids.molten_ender_ash).color(0xFFAA87CD);
    }

    public void commonFluid(FluidType fluid) {
        super.texture(fluid)
             .textures(Dreamtinker.getLocation("fluid/" + Objects.requireNonNull(ForgeRegistries.FLUID_TYPES.get().getKey(fluid)).getPath() + "/"), false,
                       false);
    }

    private FluidTexture.Builder tintedStone(FluidObject<?> fluid) {
        return named(fluid, "molten/stone");
    }

    private FluidTexture.Builder named(FluidObject<?> fluid, String name) {
        return texture(fluid).root(getResource("fluid/" + name + "/"))
                             .still().flowing().camera().calculateFogColor(true).fog(FogShape.SPHERE, 0.25f, 2);
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Fluid Texture Provider";
    }
}
