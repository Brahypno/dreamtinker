package org.dreamtinker.dreamtinker.smeltery.block.entity.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.smeltery.DreamTinkerSmeltery;
import org.dreamtinker.dreamtinker.smeltery.block.component.AshenAlloySwitchBlock;
import org.dreamtinker.dreamtinker.smeltery.block.component.AshenButtonBlock;
import org.dreamtinker.dreamtinker.smeltery.block.entity.module.SuperByproductMeltingModuleInventory;
import org.dreamtinker.dreamtinker.smeltery.block.entity.multiblock.TransmuteMultiblock;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.EntityMeltingModule;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.block.entity.module.alloying.MultiAlloyingModule;
import slimeknights.tconstruct.smeltery.block.entity.module.alloying.SmelteryAlloyTank;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.HeatingStructureMultiblock;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.TransmuteAcceleratorTemperature;
import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.TransmuteHeaterTemperature;

public class TransmuteBlockEntity extends HeatingStructureBlockEntity {
    /**
     * Fluid capacity per internal block
     */
    private static final int CAPACITY_PER_BLOCK = FluidValues.INGOT * 15;
    /**
     * GUI name
     */
    private static final Component NAME = Dreamtinker.makeTranslation("gui", "transmute");
    /**
     * Number of wall blocks needed to increase the fuel cost by 1
     * this is a bit higher than the smeltery as the structure uses more blocks, balances out in larger structures
     */
    private static final int BLOCKS_PER_FUEL = 20;
    private int heater = 0;
    private int accelerator = 1;
    private boolean heaterUpdateQueue = false;
    protected final EntityMeltingModule entityModule =
            new EntityMeltingModule(this, tank, this::canMeltEntities, this::insertIntoInventory, () -> structure == null ? null : structure.getBounds());
    /**
     * Module handling alloys
     */
    private final SmelteryAlloyTank alloyTank = new SmelteryAlloyTank(tank);
    private final MultiAlloyingModule alloyingModule = new MultiAlloyingModule(this, alloyTank);
    private boolean allowAlloying = false;
    private final Set<BlockPos> poweredExternalAlloySwitches = new HashSet<>();
    private boolean internalAlloySwitchOn = false;

    public TransmuteBlockEntity(BlockPos pos, BlockState state) {
        super(DreamTinkerSmeltery.Transmute.get(), pos, state, NAME);
    }

    @Override
    protected @NotNull HeatingStructureMultiblock<?> createMultiblock() {
        return new TransmuteMultiblock(this);
    }

    @Override
    protected @NotNull MeltingModuleInventory createMeltingInventory() {
        return new SuperByproductMeltingModuleInventory(this, tank, Config.COMMON.foundryOreRate);
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
                        if (entityMelted && getHeatTemperatureBuff() <= 0){
                            fuelModule.findFuel(true);
                        }else {
                            // both alloying and melting need to know the temperature
                            int fuelTemp = fuelModule.findFuel(false);
                            boolean meltingNeedFuel = !meltingInventory.canHeat(getHeatTemperatureBuff()) &&
                                                      meltingInventory.canHeat(fuelTemp + getHeatTemperatureBuff());
                            boolean alloyingNeedFuel = alloyingModuleCanAlloy(fuelTemp);
                            if (meltingNeedFuel || alloyingNeedFuel){
                                fuelModule.findFuel(true);
                            }
                        }
                    }
                    break;
                // second tick: melt items
                case 1:
                    if (fuelModule.hasFuel() || 0 < getHeatTemperatureBuff()){
                        meltingInventory.heatItems(fuelModule.getTemperature() + getHeatTemperatureBuff(),
                                                   getHeatRate() + fuelModule.getRate() + getHeatTimeMultiplier());
                    }else {
                        meltingInventory.coolItems();
                    }
                    break;
                case 2:
                    if (allowAlloying && (fuelModule.hasFuel() || 0 < getHeatTemperatureBuff())){
                        alloyTank.setTemperature(fuelModule.getTemperature() + getHeatTemperatureBuff());
                        alloyingModule.doAlloy();
                    }
                    break;
                // fourth tick: consume fuel, update fluids
                case 3: {
                    // update the active state
                    boolean hasFuel = fuelModule.hasFuel() || 0 < getHeatTemperatureBuff();
                    BlockState state = getBlockState();
                    if (state.getValue(ControllerBlock.ACTIVE) != hasFuel){
                        level.setBlockAndUpdate(worldPosition, state.setValue(ControllerBlock.ACTIVE, hasFuel));
                    }
                    if (fuelModule.hasFuel())
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
            updateHeatBuff();
        }else {
            poweredExternalAlloySwitches.clear();
            internalAlloySwitchOn = false;
            updateAllowAlloyingFromCache();
        }
    }

    @Override
    protected boolean isDebugItem(ItemStack stack) {
        return stack.is(TinkerTags.Items.FOUNDRY_DEBUG);
    }

    protected int getHeatTimeMultiplier() {
        return accelerator * TransmuteAcceleratorTemperature.get();
    }

    protected int getHeatRate() {
        return getHeatTemperatureBuff() / 100;
    }

    @SuppressWarnings("deprecation")
    protected boolean isHeaterBlock(Block block) {
        return block.builtInRegistryHolder().is(DreamtinkerTagKeys.Blocks.TRANSMUTE_HEATER);
    }

    @SuppressWarnings("deprecation")
    protected boolean isAccelBlock(Block block) {
        return block.builtInRegistryHolder().is(DreamtinkerTagKeys.Blocks.TRANSMUTE_ACCEL);
    }

    @SuppressWarnings("deprecation")
    protected boolean isAlloySwitchBlock(Block block) {
        return block.builtInRegistryHolder().is(DreamtinkerTagKeys.Blocks.TRANSMUTE_ALLOY_SWITCH);
    }

    @SuppressWarnings("deprecation")
    protected boolean isMeltSwitchBlock(Block block) {
        return block.builtInRegistryHolder().is(DreamtinkerTagKeys.Blocks.TRANSMUTE_MELTING_SWITCH);
    }

    public int getHeatTemperatureBuff() {
        return heater * TransmuteHeaterTemperature.get();
    }

    protected void updateHeatBuff() {
        heaterUpdateQueue = true;
    }

    @Override
    protected void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        if (heaterUpdateQueue){
            checkHeatBuff();
            heaterUpdateQueue = false;
            if ((level.getGameTime() & 31) == 0){
                validateExternalAlloySwitchCache();
            }
        }
    }

    @Override
    protected void clientTick(Level level, BlockPos pos, BlockState state) {
        super.clientTick(level, pos, state);
        if (state.hasProperty(ControllerBlock.IN_STRUCTURE) && heaterUpdateQueue){
            checkHeatBuff();
            heaterUpdateQueue = false;
        }
    }

    protected void checkHeatBuff() {
        accelerator = 0;
        heater = 0;
        internalAlloySwitchOn = false;

        if (structure != null && level != null){
            BlockPos min = structure.getMinPos();
            BlockPos max = structure.getMaxPos();

            for (int x = min.getX(); x <= max.getX(); x++) {
                for (int y = min.getY(); y <= max.getY(); y++) {
                    for (int z = min.getZ(); z <= max.getZ(); z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = level.getBlockState(pos);

                        if (!state.hasProperty(ControllerBlock.IN_STRUCTURE) || !state.getValue(ControllerBlock.IN_STRUCTURE)){
                            continue;
                        }

                        if (isHeaterBlock(state.getBlock())){
                            heater++;
                        }

                        if (isAccelBlock(state.getBlock())){
                            accelerator++;
                        }

                        if (isAlloySwitchBlock(state.getBlock()) && state.hasProperty(AshenButtonBlock.Function_Set) &&
                            state.getValue(AshenButtonBlock.Function_Set) == 1){
                            internalAlloySwitchOn = true;
                        }

                        if (isMeltSwitchBlock(state.getBlock()) && state.hasProperty(AshenButtonBlock.Function_Set) &&
                            meltingInventory instanceof SuperByproductMeltingModuleInventory inv){
                            inv.updateMeltType(SuperByproductMeltingModuleInventory.MeltType.toMeltType(state.getValue(AshenButtonBlock.Function_Set)));
                        }
                    }
                }
            }
        }

        rebuildExternalAlloySwitchCache();
        updateAllowAlloyingFromCache();
    }

    /**
     * Checks if we can melt entities
     *
     * @return True if we can melt entities
     */
    private boolean canMeltEntities() {
        if (fuelModule.hasFuel() || 0 < getHeatTemperatureBuff()){
            return true;
        }
        return fuelModule.findFuel(false) > 0;
    }

    /**
     * Inserts an item into the inventory
     *
     * @param stack Stack to insert
     */
    private ItemStack insertIntoInventory(ItemStack stack) {
        return ItemHandlerHelper.insertItem(meltingInventory, stack, false);
    }

    private boolean alloyingModuleCanAlloy(int fuelTemp) {
        if (!allowAlloying)
            return false;
        if (0 < getHeatTemperatureBuff()){
            alloyTank.setTemperature(getHeatTemperatureBuff());
            if (!alloyingModule.canAlloy()){
                alloyTank.setTemperature(fuelTemp + getHeatTemperatureBuff());
                return alloyingModule.canAlloy();
            }
        }
        return false;
    }

    @Override
    public void notifyFluidsChanged(FluidChange type, FluidStack fluid) {
        super.notifyFluidsChanged(type, fluid);

        // adding a new fluid means recipes that previously did not match might match now
        // can ignore removing a fluid as that is handled internally by the module
        if (allowAlloying && type == FluidChange.ADDED){
            alloyingModule.clearCachedRecipes();
        }
    }

    public void setExternalAlloySwitch(BlockPos switchPos, boolean powered) {
        if (level == null || level.isClientSide){
            return;
        }

        boolean changed = powered
                          ? poweredExternalAlloySwitches.add(switchPos.immutable())
                          : poweredExternalAlloySwitches.remove(switchPos);

        if (changed){
            updateAllowAlloyingFromCache();
        }
    }

    private void updateAllowAlloyingFromCache() {
        boolean old = allowAlloying;
        allowAlloying = internalAlloySwitchOn || !poweredExternalAlloySwitches.isEmpty();

        if (old != allowAlloying){
            if (allowAlloying){
                alloyingModule.clearCachedRecipes();
            }

            setChanged();

            if (level != null && !level.isClientSide){
                BlockState state = getBlockState();
                level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
            }
        }
    }

    public void rebuildExternalAlloySwitchCache() {
        if (level == null || level.isClientSide || structure == null){
            poweredExternalAlloySwitches.clear();
            return;
        }

        poweredExternalAlloySwitches.clear();

        BlockPos min = structure.getMinPos();
        BlockPos max = structure.getMaxPos();

        for (BlockPos structurePos : BlockPos.betweenClosed(min, max)) {
            BlockState structureState = level.getBlockState(structurePos);

            if (!structureState.hasProperty(ControllerBlock.IN_STRUCTURE) || !structureState.getValue(ControllerBlock.IN_STRUCTURE)){
                continue;
            }

            for (Direction direction : Direction.values()) {
                BlockPos switchPos = structurePos.relative(direction);
                BlockState switchState = level.getBlockState(switchPos);

                if (!isValidPoweredExternalAlloySwitch(structurePos, switchPos, switchState)){
                    continue;
                }

                poweredExternalAlloySwitches.add(switchPos.immutable());
            }
        }
    }

    private boolean isValidPoweredExternalAlloySwitch(BlockPos structurePos, BlockPos switchPos, BlockState switchState) {
        if (!isAlloySwitchBlock(switchState.getBlock())){
            return false;
        }

        if (!switchState.hasProperty(LeverBlock.POWERED) || !switchState.getValue(LeverBlock.POWERED)){
            return false;
        }

        return AshenAlloySwitchBlock.getAttachedPos(switchState, switchPos).equals(structurePos);
    }

    private void validateExternalAlloySwitchCache() {
        if (level == null || level.isClientSide || poweredExternalAlloySwitches.isEmpty()){
            return;
        }

        boolean changed = false;
        Iterator<BlockPos> iterator = poweredExternalAlloySwitches.iterator();

        while (iterator.hasNext()) {
            BlockPos switchPos = iterator.next();
            BlockState state = level.getBlockState(switchPos);

            if (!isAlloySwitchBlock(state.getBlock())
                || !state.hasProperty(LeverBlock.POWERED)
                || !state.getValue(LeverBlock.POWERED)){
                iterator.remove();
                changed = true;
            }
        }

        if (changed){
            updateAllowAlloyingFromCache();
        }
    }
}
