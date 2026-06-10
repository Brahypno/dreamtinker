package org.brahypno.dreamtinker.tools.modifiers.events;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.Serpent_Shift.ReasonBeyondReasonModifier;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public final class ReasonBeyondReasonCraftingEvents {
    private ReasonBeyondReasonCraftingEvents() {}

    @SubscribeEvent
    public static void onCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity().level().isClientSide)
            return;
        ItemStack beforeStack = findBeforeTool(event);
        ItemStack afterStack = event.getCrafting();

        if (!isTool(beforeStack) || !isTool(afterStack))
            return;

        ToolStack before = ToolStack.copyFrom(beforeStack);
        ToolStack after = ToolStack.copyFrom(afterStack);

        int capacity = ReasonBeyondReasonModifier.getCapacity(after);
        if (capacity <= 0)
            return;

        if (ReasonBeyondReasonModifier.Data.entryCount(after.getPersistentData()) >= capacity)
            return;

        Map<SlotType, Integer> paidThisEvent = collectPaidSlots(before, after);
        if (paidThisEvent.isEmpty())
            return;

        ReasonBeyondReasonModifier.Data.addEntry(after.getPersistentData(), paidThisEvent);
        after.rebuildStats();
        after.updateStack(afterStack);
    }

    private static Map<SlotType, Integer> collectPaidSlots(ToolStack before, ToolStack after) {
        Map<SlotType, Integer> paid = new LinkedHashMap<>();

        for (SlotType type : SlotType.getAllSlotTypes()) {
            int beforeSlots = before.getPersistentData().getSlots(type);
            int afterSlots = after.getPersistentData().getSlots(type);
            if (afterSlots >= beforeSlots)
                continue;

            paid.put(type, beforeSlots - afterSlots);
        }

        return paid;
    }

    private static ItemStack findBeforeTool(PlayerEvent.ItemCraftedEvent event) {
        for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
            ItemStack stack = event.getInventory().getItem(i);
            if (isTool(stack))
                return stack;
        }

        return ItemStack.EMPTY;
    }

    private static boolean isTool(ItemStack stack) {
        return !stack.isEmpty() && stack.is(TinkerTags.Items.MODIFIABLE);
    }
}
