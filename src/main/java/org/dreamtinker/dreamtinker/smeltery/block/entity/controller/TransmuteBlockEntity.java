package org.dreamtinker.dreamtinker.smeltery.block.entity.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.smeltery.DreamTinkerSmeltery;
import org.dreamtinker.dreamtinker.smeltery.block.entity.module.ReverseByproductMeltingModuleInventory;
import org.dreamtinker.dreamtinker.smeltery.block.entity.multiblock.TransmuteMultiblock;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.HeatingStructureMultiblock;

import javax.annotation.Nullable;

public class TransmuteBlockEntity extends HeatingStructureBlockEntity {
    /**
     * Fluid capacity per internal block
     */
    private static final int CAPACITY_PER_BLOCK = FluidValues.INGOT * 12;
    /**
     * GUI name
     */
    private static final Component NAME = Dreamtinker.makeTranslation("gui", "transmute");
    /**
     * Number of wall blocks needed to increase the fuel cost by 1
     * this is a bit higher than the smeltery as the structure uses more blocks, balances out in larger structures
     */
    private static final int BLOCKS_PER_FUEL = 18;

    public TransmuteBlockEntity(BlockPos pos, BlockState state) {
        super(DreamTinkerSmeltery.Transmute.get(), pos, state, NAME);
    }

    @Override
    protected @NotNull HeatingStructureMultiblock<?> createMultiblock() {
        return new TransmuteMultiblock(this);
    }

    @Override
    protected @NotNull MeltingModuleInventory createMeltingInventory() {
        return new ReverseByproductMeltingModuleInventory(this, tank, Config.COMMON.foundryOreRate);
    }

    @Override
    protected void heat() {

        if (structure == null || level == null){
            return;
        }

        // the next set of behaviors all require fuel, skip if no tanks
        if (structure.hasTanks()){
            // every second, interact with entities, will consume fuel if needed
            boolean entityMelted = false;
            if (tick == 12){
                entityMelted = entityModule.interactWithEntities();
            }

            // run in four phases alternating each tick, so each thing runs once every 4 ticks
            switch (tick % 4) {
                // first tick, find fuel if needed
                case 0:
                    if (!fuelModule.hasFuel()){
                        // if we melted something already, we need fuel
                        if (entityMelted){
                            fuelModule.findFuel(true);
                        }else {
                            // both alloying and melting need to know the temperature
                            if (meltingInventory.canHeat(fuelModule.findFuel(false) + getHeatTemperatureBuff())){
                                fuelModule.findFuel(true);
                            }
                        }
                    }
                    break;
                // second tick: melt items
                case 1:
                    if (fuelModule.hasFuel()){
                        meltingInventory.heatItems(fuelModule.getTemperature(), fuelModule.getRate() * getHeatTimeMultiplier());
                    }else {
                        meltingInventory.coolItems();
                    }
                    break;
                // fourth tick: consume fuel, update fluids
                case 3: {
                    // update the active state
                    boolean hasFuel = fuelModule.hasFuel();
                    BlockState state = getBlockState();
                    if (state.getValue(ControllerBlock.ACTIVE) != hasFuel){
                        level.setBlockAndUpdate(worldPosition, state.setValue(ControllerBlock.ACTIVE, hasFuel));
                    }
                    fuelModule.decreaseFuel(fuelRate);
                    break;
                }
            }
        }else {
            cool();
        }
    }

    @Override
    protected void setStructure(@Nullable HeatingStructureMultiblock.StructureData structure) {
        super.setStructure(structure);
        if (structure != null){
            int dx = structure.getInnerX(), dy = structure.getInnerY(), dz = structure.getInnerZ();
            // tank capacity includes walls and floor
            tank.setCapacity(CAPACITY_PER_BLOCK * (dx + 2) * (dy + 1) * (dz + 2));
            // item capacity uses just inner space
            meltingInventory.resize(dx * dy * dz, dropItem);
            // fuel rate: every 20 blocks in the wall makes the fuel cost 1 more
            // perimeter: to prevent double counting, frame just added on X and floor
            fuelRate = 1 + (2 * ((dx + 2) * dy) + 2 * (dy * dz) + ((dx + 2) * (dz + 2))) / BLOCKS_PER_FUEL;
        }
    }

    @Override
    protected boolean isDebugItem(ItemStack stack) {
        return stack.is(TinkerTags.Items.FOUNDRY_DEBUG);
    }

    protected int getHeatTimeMultiplier() {
        return 1;
    }

    protected int getHeatTemperatureBuff() {
        return 0;
    }
}
