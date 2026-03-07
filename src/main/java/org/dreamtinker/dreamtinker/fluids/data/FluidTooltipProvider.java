package org.dreamtinker.dreamtinker.fluids.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.smeltery.DreamTinkerSmeltery;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.fluid.tooltip.AbstractFluidTooltipProvider;
import slimeknights.tconstruct.TConstruct;

public class FluidTooltipProvider extends AbstractFluidTooltipProvider {
    public FluidTooltipProvider(PackOutput packOutput) {
        super(packOutput, Dreamtinker.MODID);
    }

    @Override
    protected void addFluids() {
        addRedirect(DreamTinkerSmeltery.Transmute.getId(), new ResourceLocation(TConstruct.MOD_ID, "ingots"));
    }

    @Override
    public @NotNull String getName() {
        return "Dream Tinkers' Construct Fluid Tooltip Provider";
    }
}
