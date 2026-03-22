package org.dreamtinker.dreamtinker.smeltery.block.entity.module;

import net.minecraft.core.BlockPos;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.MultitankFuelModule;

import java.util.List;
import java.util.function.Supplier;

public class TransmuteMultitankFuelModule extends MultitankFuelModule {
    public TransmuteMultitankFuelModule(MantleBlockEntity parent, Supplier<List<BlockPos>> tankSupplier) {
        super(parent, tankSupplier);
    }

    protected int extraTempBuff;

    public void setExtraTempBuff(int extraTempBuff) {
        this.extraTempBuff = extraTempBuff;
    }
}
