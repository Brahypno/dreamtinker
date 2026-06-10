package org.brahypno.dreamtinker.smeltery.block.entity.module;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.brahypno.dreamtinker.smeltery.block.entity.controller.TransmuteBlockEntity;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModule;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.block.entity.tank.SmelteryTank;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

public class SuperByproductMeltingModuleInventory extends MeltingModuleInventory {
    private final TransmuteBlockEntity parent;
    private MeltingModule[] smeltery_modules;//Just using oreRate for smeltery one
    private MeltType meltType = MeltType.Transmute;

    public SuperByproductMeltingModuleInventory(TransmuteBlockEntity parent, IFluidHandler fluidHandler, IMeltingContainer.IOreRate oreRate, int size) {
        super(parent, fluidHandler, oreRate, size);
        this.parent = parent;
        this.smeltery_modules = new MeltingModule[size];
    }

    public SuperByproductMeltingModuleInventory(TransmuteBlockEntity parent, IFluidHandler fluidHandler, IMeltingContainer.IOreRate oreRate) {
        this(parent, fluidHandler, oreRate, 0);
    }

    public void updateMeltType(MeltType meltType) {
        this.meltType = meltType;
    }

    public void resize(int newSize, Consumer<ItemStack> stackConsumer) {
        super.resize(newSize, stackConsumer);
        smeltery_modules = Arrays.copyOf(smeltery_modules, getSlots());
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (0 == amount || !validSlot(slot))
            return ItemStack.EMPTY;

        ItemStack existing = getStackInSlot(slot);
        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int extractSize = Math.min(amount, existing.getCount());
        if (simulate){
            return ItemHandlerHelper.copyStackWithSize(existing, extractSize);
        }else {
            ItemStack extracted = ItemHandlerHelper.copyStackWithSize(existing, extractSize);
            setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - extractSize));
            return extracted;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 2;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        // actually set the stack
        if (validSlot(slot)){
            if (stack.isEmpty()){
                getModule(slot).setStack(ItemStack.EMPTY);
            }else {
                // validate size
                if (stack.getCount() > getSlotLimit(slot)){
                    stack.setCount(getSlotLimit(slot));
                }
                getModule(slot).setStack(stack);
            }
        }
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()){
            return ItemStack.EMPTY;
        }
        if (slot < 0 || slot >= getSlots()){
            return stack;
        }

        MeltingModule module = getModule(slot);
        ItemStack existing = module.getStack();

        // Check if slot is empty or contains same item with room
        boolean isSameItem = !existing.isEmpty() && ItemStack.isSameItemSameTags(existing, stack);
        boolean canInsert = existing.isEmpty() || (isSameItem && existing.getCount() < getSlotLimit(slot));

        int insertSize = canInsert ? Math.min(stack.getCount(), getSlotLimit(slot) - existing.getCount()) : 0;

        if (!simulate && canInsert && insertSize > 0){
            ItemStack toInsert = ItemHandlerHelper.copyStackWithSize(stack, insertSize);
            if (existing.isEmpty()){
                setStackInSlot(slot, toInsert);
            }else {
                existing.grow(insertSize);
            }
        }
        return insertSize > 0 ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - insertSize) : stack;
    }

    @Override
    protected boolean tryFillTank(int index, IMeltingRecipe recipe) {
        return switch (meltType) {
            case SMELTERY -> tryFillTankSmeltery(index, recipe);
            case Foundry -> tryFillTankFoundry(index, recipe);
            default -> tryFillFluidTransmute(index, recipe);
        };
    }

    private boolean tryFillTankFoundry(int index, IMeltingRecipe recipe) {
        IMeltingContainer inv = getModule(index);
        // 1) 主产物
        FluidStack main = recipe.getOutput(inv);
        if (main.isEmpty() || main.getAmount() <= 0)
            return false;
        int count = 0;

        if (fluidHandler.fill(main.copy(), IFluidHandler.FluidAction.SIMULATE) != main.getAmount())
            return false;

        fluidHandler.fill(main, IFluidHandler.FluidAction.EXECUTE);
        while (count < inv.getStack().getCount() * 2) {
            // 4) 让配方把副产物灌进临时 tank（空间等同于真实罐子在灌完主产物后的剩余空间）
            recipe.handleByproducts(inv, fluidHandler);
            ++count;
        }
        return true;
    }

    private MeltingModule getSmelteryModule(int slot) {
        if (!validSlot(slot)){
            throw new IndexOutOfBoundsException();
        }
        if (smeltery_modules[slot] == null){
            smeltery_modules[slot] = new MeltingModule(parent, recipe -> tryFillTank(slot, recipe), Config.COMMON.smelteryOreRate, slot);
        }
        return smeltery_modules[slot];
    }

    private boolean tryFillTankSmeltery(int index, IMeltingRecipe recipe) {
        FluidStack fluid = recipe.getOutput(getSmelteryModule(index));
        IMeltingContainer inv = getModule(index);
        Random defaultRand = new Random();
        float value = 1.2F + defaultRand.nextFloat() * 0.2F;
        fluid.setAmount((Mth.ceil(fluid.getAmount() * inv.getStack().getCount() * value)));
        if (fluidHandler.fill(fluid.copy(), IFluidHandler.FluidAction.SIMULATE) == fluid.getAmount()){
            fluidHandler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
            return true;
        }
        return false;
    }

    private boolean tryFillFluidTransmute(int index, IMeltingRecipe recipe) {
        IMeltingContainer inv = getModule(index);

        // 1) 主产物
        FluidStack main = recipe.getOutput(inv);
        if (main.isEmpty() || main.getAmount() <= 0)
            return false;
        int count = 0;
        main.setAmount(main.getAmount() * inv.getStack().getCount());

        if (fluidHandler.fill(main.copy(), IFluidHandler.FluidAction.SIMULATE) != main.getAmount())
            return false;

        SmelteryTank<HeatingStructureBlockEntity> tank = new SmelteryTank<>(parent);
        tank.setCapacity(Math.max(0, main.getAmount() * 10));
        while (count < inv.getStack().getCount()) {
            // 4) 让配方把副产物灌进临时 tank（空间等同于真实罐子在灌完主产物后的剩余空间）
            recipe.handleByproducts(inv, tank);
            ++count;
        }
        // 5) 读取副产物列表与总量
        java.util.List<FluidStack> bys = collectNonEmptyFluids(tank);

        long M = main.getAmount();
        long B = 0L;
        FluidStack largest = FluidStack.EMPTY;
        long largestAmt = 0L;

        for (FluidStack by : bys) {
            long amt = by.getAmount();
            if (amt <= 0){
                continue;
            }

            B += amt;
            if (amt > largestAmt){
                largestAmt = amt;
                largest = by;
            }
        }

        if (B <= 0L){
            fluidHandler.fill(main.copy(), IFluidHandler.FluidAction.EXECUTE);
            return true;
        }

        FluidStack swappedMain = main.copy();
        swappedMain.setAmount((int) Math.min(Integer.MAX_VALUE, B));
        fluidHandler.fill(swappedMain, IFluidHandler.FluidAction.EXECUTE);

        long remainder = M;
        for (FluidStack by : bys) {
            long amt = by.getAmount();
            if (amt <= 0){
                continue;
            }

            long share = M * amt / B;
            if (share <= 0L){
                continue;
            }

            remainder -= share;

            FluidStack out = by.copy();
            out.setAmount((int) Math.min(Integer.MAX_VALUE, share));
            fluidHandler.fill(out, IFluidHandler.FluidAction.EXECUTE);
        }

        if (remainder > 0L && !largest.isEmpty()){
            FluidStack extra = largest.copy();
            extra.setAmount((int) Math.min(Integer.MAX_VALUE, remainder));
            fluidHandler.fill(extra, IFluidHandler.FluidAction.EXECUTE);
        }

        return true;
    }

    public enum MeltType {
        Transmute,
        SMELTERY,
        Foundry,
        Transmute_v2;

        public static MeltType toMeltType(int i) {
            return switch (i) {
                case 0 -> Transmute;
                case 1 -> SMELTERY;
                case 2 -> Foundry;
                case 3 -> Transmute_v2;
                default -> throw new IllegalArgumentException("Invalid melt type: " + i);
            };
        }

    }

    private static java.util.List<FluidStack> collectNonEmptyFluids(IFluidHandler handler) {
        java.util.ArrayList<FluidStack> list = new java.util.ArrayList<>();
        for (int i = 0; i < handler.getTanks(); i++) {
            FluidStack fs = handler.getFluidInTank(i);
            if (!fs.isEmpty() && fs.getAmount() > 0)
                list.add(fs.copy());
        }
        return list;
    }
}
