package org.dreamtinker.dreamtinker.tools.data;

import org.dreamtinker.dreamtinker.fluids.DreamtinkerFluids;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.library.data.recipe.IByproduct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer;

import java.util.Locale;

public enum MyByproduct implements IByproduct {
    // base metals
    CLOGGRUM(false, DreamtinkerFluids.molten_cloggrum);

    private final String name;
    private final boolean alwaysPresent;
    private final FluidObject<?> fluid;
    private final int amount;
    private final int damageUnit;
    private final IMeltingContainer.OreRateType oreRate;

    MyByproduct(boolean alwaysPresent, FluidObject<?> fluid, int amount, int damageUnit, IMeltingContainer.OreRateType oreRate) {
        this.name = name().toLowerCase(Locale.ROOT);
        this.alwaysPresent = alwaysPresent;
        this.fluid = fluid;
        this.amount = amount;
        this.damageUnit = damageUnit;
        this.oreRate = oreRate;
    }

    MyByproduct(boolean alwaysPresent, FluidObject<?> fluid) {
        this(alwaysPresent, fluid, FluidValues.INGOT, FluidValues.NUGGET, IMeltingContainer.OreRateType.METAL);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getDamageUnit() {
        return damageUnit;
    }

    @Override
    public boolean isAlwaysPresent() {
        return alwaysPresent;
    }

    @Override
    public FluidOutput getFluid(float scale) {
        return fluid.result((int) (amount * scale));
    }

    @Override
    public IMeltingContainer.OreRateType getOreRate() {
        return oreRate;
    }
}
