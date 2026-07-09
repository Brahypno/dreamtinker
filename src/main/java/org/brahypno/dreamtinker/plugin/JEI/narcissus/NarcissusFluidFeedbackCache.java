package org.brahypno.dreamtinker.plugin.JEI.narcissus;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.library.modifiers.modules.combat.NarcissusFluidFeedbacks;

import java.util.*;

public final class NarcissusFluidFeedbackCache {
    public static final int ROWS = 8;

    private static final Map<ResourceLocation, Entry> ENTRIES = new LinkedHashMap<>();

    private NarcissusFluidFeedbackCache() {}

    public static void rebuild(Collection<ItemStack> stacks) {
        ENTRIES.clear();
        for (ItemStack stack : stacks) {
            scanStack(stack);
        }
    }

    public static List<Entry> entries() {
        return List.copyOf(ENTRIES.values());
    }

    public static List<Page> pages() {
        List<Entry> entries = entries();
        List<Page> pages = new ArrayList<>();
        for (int i = 0; i < entries.size(); i += ROWS) {
            pages.add(new Page(entries.subList(i, Math.min(i + ROWS, entries.size()))));
        }
        return pages;
    }

    private static void scanStack(ItemStack stack) {
        if (stack.isEmpty()){
            return;
        }
        ItemStack probe = stack.copyWithCount(1);
        probe.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> scanHandler(probe, handler));
    }

    private static void scanHandler(ItemStack source, IFluidHandler handler) {
        FluidStack fluid = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (fluid.isEmpty()){
            return;
        }

        ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fluid.getFluid());
        if (id == null || ENTRIES.containsKey(id)){
            return;
        }

        NarcissusFluidFeedbacks.ResolvedFluidFeedback feedback = NarcissusFluidFeedbacks.resolveFeedback(fluid);
        if (feedback == null){
            return;
        }

        FluidStack displayFluid = new FluidStack(fluid.getFluid(), 1000, fluid.getTag());
        ENTRIES.put(id, new Entry(displayFluid, source.copy(), feedback, NarcissusFluidFeedbacks.displayFor(feedback.mode())));
    }

    public record Entry(FluidStack fluid, ItemStack source, NarcissusFluidFeedbacks.ResolvedFluidFeedback feedback,
                        NarcissusFluidFeedbacks.FeedbackDisplay display) {}

    public record Page(List<Entry> entries) {}
}