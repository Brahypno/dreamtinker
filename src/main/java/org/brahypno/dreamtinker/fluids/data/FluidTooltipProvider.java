package org.brahypno.dreamtinker.fluids.data;

import net.minecraft.data.PackOutput;
import org.brahypno.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.fluid.tooltip.AbstractFluidTooltipProvider;

public class FluidTooltipProvider extends AbstractFluidTooltipProvider {
    public FluidTooltipProvider(PackOutput packOutput) {
        super(packOutput, Dreamtinker.MODID);
    }

    @Override
    protected void addFluids() {
    }

    @Override
    public @NotNull String getName() {
        return "Dream Tinkers' Construct Fluid Tooltip Provider";
    }
}
