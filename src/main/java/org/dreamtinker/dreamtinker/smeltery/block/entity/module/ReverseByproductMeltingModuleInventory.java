package org.dreamtinker.dreamtinker.smeltery.block.entity.module;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.dreamtinker.dreamtinker.smeltery.block.entity.controller.TransmuteBlockEntity;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.block.entity.tank.SmelteryTank;

public class ReverseByproductMeltingModuleInventory extends MeltingModuleInventory {
    private final TransmuteBlockEntity parent;

    public ReverseByproductMeltingModuleInventory(TransmuteBlockEntity parent, IFluidHandler fluidHandler, IMeltingContainer.IOreRate oreRate, int size) {
        super(parent, fluidHandler, oreRate, size);
        this.parent = parent;
    }

    public ReverseByproductMeltingModuleInventory(TransmuteBlockEntity parent, IFluidHandler fluidHandler, IMeltingContainer.IOreRate oreRate) {
        super(parent, fluidHandler, oreRate);
        this.parent = parent;
    }


    @Override
    protected boolean tryFillTank(int index, IMeltingRecipe recipe) {
        IMeltingContainer inv = getModule(index);

        // 1) 主产物
        FluidStack main = recipe.getOutput(inv);
        if (main.isEmpty() || main.getAmount() <= 0)
            return false;
        // 2) 先判断主产物能否完整灌入（保持 super 的语义）
        int canFillMain = fluidHandler.fill(main.copy(), IFluidHandler.FluidAction.SIMULATE);
        if (canFillMain != main.getAmount()){
            return false;
        }
        int remainingBefore = getRemainingSpace(fluidHandler);
        int remainingAfterMain = Math.max(0, remainingBefore - main.getAmount());

        SmelteryTank<HeatingStructureBlockEntity> tank = new SmelteryTank<>(parent);
        //tank.setCapacity(fluidHandler.getTankCapacity(fluidHandler.getTanks()));
        tank.setCapacity(remainingAfterMain);
        // 4) 让配方把副产物灌进临时 tank（空间等同于真实罐子在灌完主产物后的剩余空间）
        recipe.handleByproducts(inv, tank);

        // 5) 读取副产物列表与总量
        java.util.List<FluidStack> bys = collectNonEmptyFluids(tank);
        if (bys.isEmpty()){
            // 没有副产物：直接正常灌主产物
            fluidHandler.fill(main, IFluidHandler.FluidAction.EXECUTE);
            return true;
        }

        long M = main.getAmount();
        long B = 0;
        for (FluidStack b : bys)
            B += b.getAmount();

        // 副产物总量为 0：不交换
        if (B <= 0){
            fluidHandler.fill(main, IFluidHandler.FluidAction.EXECUTE);
            // byproducts 都是 0，不用灌
            return true;
        }

        // 6) 交换规则：
        // 新主产物量 = B
        // 新副产物总量 = M，按 bys 原比例分配
        FluidStack newMain = main.copy();
        newMain.setAmount((int) Math.min(Integer.MAX_VALUE, B));
        fluidHandler.fill(newMain, IFluidHandler.FluidAction.EXECUTE);

        long remaining = M;

        // 找占比最大的副产物，用来吃掉整数除法的余数
        int maxIdx = 0;
        long maxAmt = bys.get(0).getAmount();
        for (int i = 1; i < bys.size(); i++) {
            long a = bys.get(i).getAmount();
            if (a > maxAmt){
                maxAmt = a;
                maxIdx = i;
            }
        }

        for (FluidStack b : bys) {
            long share = (M * (long) b.getAmount()) / B; // floor
            remaining -= share;

            if (share > 0){
                FluidStack nb = b.copy();
                nb.setAmount((int) Math.min(Integer.MAX_VALUE, share));
                fluidHandler.fill(nb, IFluidHandler.FluidAction.EXECUTE);
            }
        }

        // 余数补给最大项，保证总量尽量守恒（若 fill 装不下则自然丢失）
        if (remaining > 0){
            FluidStack bonus = bys.get(maxIdx).copy();
            bonus.setAmount((int) Math.min(Integer.MAX_VALUE, remaining));
            fluidHandler.fill(bonus, IFluidHandler.FluidAction.EXECUTE);
        }
        return true;
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

    private static int getRemainingSpace(IFluidHandler handler) {
        int remaining = 0;
        int tanks = handler.getTanks();
        for (int i = 0; i < tanks; i++) {
            FluidStack in = handler.getFluidInTank(i);
            int cap = handler.getTankCapacity(i);
            int amt = in.isEmpty() ? 0 : in.getAmount();
            if (cap > amt)
                remaining += (cap - amt);
        }
        return remaining;
    }
}
