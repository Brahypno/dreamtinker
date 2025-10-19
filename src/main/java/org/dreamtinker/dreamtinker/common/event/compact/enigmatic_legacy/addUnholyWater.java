package org.dreamtinker.dreamtinker.common.event.compact.enigmatic_legacy;

import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.fluids.DreamtinkerFluids;

import javax.annotation.Nullable;

public class addUnholyWater {
    public static void attachCaps(AttachCapabilitiesEvent<ItemStack> e) {
        ItemStack stack = e.getObject();
        if (stack.getItem().equals(EnigmaticItems.UNHOLY_GRAIL)){
            // 若对方已实现就不要重复挂
            //if (stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent())
            //    return;

            e.addCapability(new ResourceLocation(Dreamtinker.MODID, "infinite_unholy_water"),
                            new ICapabilityProvider() {
                                private final LazyOptional<IFluidHandlerItem> cap =
                                        LazyOptional.of(() -> new InfiniteWaterHandler(stack));

                                @Override
                                public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
                                    return capability == ForgeCapabilities.FLUID_HANDLER_ITEM ? cap.cast() : LazyOptional.empty();
                                }
                            });
        }
    }

    private record InfiniteWaterHandler(ItemStack container) implements IFluidHandlerItem {
        @Override
        public ItemStack getContainer() {return container;}

        @Override
        public int getTanks() {return 1;}

        @Override
        public FluidStack getFluidInTank(int tank) {
            return tank == 0 ? new FluidStack(DreamtinkerFluids.unholy_water.get(), Integer.MAX_VALUE) : FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return tank == 0 && stack.getFluid() == DreamtinkerFluids.unholy_water.get();
        }

        /**
         * 禁止往里装液体（我们是“只倒出”）
         */
        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        /**
         * 允许被抽水——按请求量给出相同体积的水，但不改变自身（无限）
         */
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.isEmpty() || resource.getFluid() != DreamtinkerFluids.unholy_water.get())
                return FluidStack.EMPTY;
            return new FluidStack(DreamtinkerFluids.unholy_water.get(), resource.getAmount());
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (maxDrain <= 0)
                return FluidStack.EMPTY;
            return new FluidStack(DreamtinkerFluids.unholy_water.get(), maxDrain);
        }
    }
}


