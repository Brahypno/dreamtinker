package org.dreamtinker.dreamtinker.common.event.compact.malum;

import com.sammy.malum.registry.common.item.ItemRegistry;
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
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.item.ConstantFluidContainerWrapper;
import slimeknights.tconstruct.library.recipe.FluidValues;

import javax.annotation.Nullable;

public class addConcentratedGluttonyBottle {

    public static void attachCaps(AttachCapabilitiesEvent<ItemStack> e) {
        ItemStack stack = e.getObject();
        if (stack.getItem().equals(ItemRegistry.CONCENTRATED_GLUTTONY.get())){
            // 若对方已实现就不要重复挂
            //if (stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent())
            //    return;

            e.addCapability(new ResourceLocation(Dreamtinker.MODID, "limit_liquid_concentrated_gluttony"),
                            new ICapabilityProvider() {
                                private final LazyOptional<IFluidHandlerItem> cap =
                                        LazyOptional.of(
                                                () -> new ConstantFluidContainerWrapper(
                                                        new FluidStack(DreamtinkerFluids.liquid_concentrated_gluttony.get(), FluidValues.BOTTLE), stack));

                                @Override
                                public <T> @NotNull LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
                                    return capability == ForgeCapabilities.FLUID_HANDLER_ITEM ? cap.cast() : LazyOptional.empty();
                                }
                            });
        }
    }
}
