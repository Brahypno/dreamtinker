package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.Serpent_Shift;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ReasonBeyondReasonModifier extends Modifier implements VolatileDataModifierHook, TooltipModifierHook, ModifierRemovalHook {
    public static int getCapacity(IToolContext tool) {
        return getCapacity(tool.getModifierLevel(DreamtinkerModifiers.ultra_logic.getId()));
    }

    private static int getCapacity(int level) {
        if (level <= 0)
            return 0;
        return level * 3;
    }

    private static MutableComponent slotText(SlotType type, int amount) {
        return Component.literal(String.valueOf(amount))
                        .withStyle(style -> style.withColor(type.getColor()))
                        .append(Component.literal(" "))
                        .append(type.getDisplayName().copy().withStyle(style -> style.withColor(type.getColor())));
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.VOLATILE_DATA, ModifierHooks.TOOLTIP, ModifierHooks.REMOVE);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
        IModDataView persistentData = context.getPersistentData();
        int capacity = getCapacity(context);
        if (persistentData instanceof ModDataNBT mutableData){
            Data.trimToCapacity(mutableData, capacity);
            persistentData = mutableData;
        }

        Data.forEachEntry(persistentData, capacity, (index, slots) ->
                Data.forEachSlot(slots, volatileData::addSlots));
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        int capacity = getCapacity(tool);
        int used = Math.min(Data.entryCount(tool.getPersistentData()), capacity);
        int remaining = Math.max(0, capacity - used);
        if (capacity <= 0 && used <= 0)
            return;

        MutableComponent title = this.getDisplayName().copy()
                                     .append(modifierText(": "))
                                     .append(Component.translatable("modifier.dreamtinker.ultra_logic.tooltip.title", remaining)
                                                      .withStyle(style -> style.withColor(getTextColor())))
                                     .append(modifierText(":"));
        tooltip.add(title);

        if (used > 0){
            MutableComponent slotsLine = Component.empty();
            Data.forEachEntry(tool.getPersistentData(), capacity, (index, slots) -> appendEntryTooltip(slotsLine, index, slots));
            tooltip.add(slotsLine);
        }
    }

    private void appendEntryTooltip(MutableComponent tooltip, int index, CompoundTag slots) {
        if (index > 0)
            tooltip.append(modifierText("; "));
        tooltip.append(Component.translatable("modifier.dreamtinker.ultra_logic.tooltip.entry", index + 1).withStyle(style -> style.withColor(getTextColor())));
        tooltip.append(modifierText(": "));

        boolean firstSlot = true;
        for (String key : slots.getAllKeys()) {
            int amount = slots.getInt(key);
            if (amount <= 0)
                continue;

            SlotType type = SlotType.getOrCreate(key);
            if (!firstSlot)
                tooltip.append(modifierText(", "));
            tooltip.append(slotText(type, amount));
            firstSlot = false;
        }
    }

    private MutableComponent modifierText(String text) {
        return Component.literal(text).withStyle(style -> style.withColor(getTextColor()));
    }

    @Override
    public @Nullable Component onRemoved(IToolStackView tool, Modifier modifier) {
        Data.clear(tool.getPersistentData());
        return null;
    }

    public static final class Data {
        public static final ResourceLocation ROOT = Dreamtinker.getLocation("ultra_logic");

        private static final String ENTRIES = "entries";
        private static final String SLOTS = "slots";

        private Data() {}

        public static void addEntry(ModDataNBT data, Map<SlotType, Integer> paidSlots) {
            if (paidSlots.isEmpty())
                return;

            CompoundTag slotTag = new CompoundTag();
            for (Map.Entry<SlotType, Integer> slot : paidSlots.entrySet()) {
                if (slot.getValue() > 0)
                    slotTag.putInt(slot.getKey().getName(), slot.getValue());
            }
            if (slotTag.isEmpty())
                return;

            CompoundTag entry = new CompoundTag();
            entry.put(SLOTS, slotTag);

            CompoundTag root = root(data);
            ListTag entries = root.getList(ENTRIES, Tag.TAG_COMPOUND);
            entries.add(entry);
            root.put(ENTRIES, entries);
            data.put(ROOT, root);
        }

        public static int entryCount(IModDataView data) {
            return entries(data).size();
        }

        public static ListTag entries(IModDataView data) {
            return root(data).getList(ENTRIES, Tag.TAG_COMPOUND);
        }

        public static void forEachEntry(IModDataView data, int limit, EntryConsumer consumer) {
            ListTag entries = entries(data);
            for (int i = 0; i < entries.size() && i < limit; i++) {
                CompoundTag entry = entries.getCompound(i);
                consumer.accept(i, entry.getCompound(SLOTS));
            }
        }

        public static void forEachSlot(CompoundTag slots, BiConsumer<SlotType, Integer> consumer) {
            for (String key : slots.getAllKeys()) {
                int amount = slots.getInt(key);
                if (amount > 0)
                    consumer.accept(SlotType.getOrCreate(key), amount);
            }
        }

        public static void clear(ModDataNBT data) {
            data.remove(ROOT);
        }

        public static void trimToCapacity(ModDataNBT data, int capacity) {
            CompoundTag root = root(data);
            ListTag entries = root.getList(ENTRIES, Tag.TAG_COMPOUND);
            while (entries.size() > capacity) {
                entries.remove(entries.size() - 1);
            }

            if (entries.isEmpty()){
                data.remove(ROOT);
            }else {
                root.put(ENTRIES, entries);
                data.put(ROOT, root);
            }
        }

        private static CompoundTag root(IModDataView data) {
            return data.contains(ROOT, Tag.TAG_COMPOUND) ? data.getCompound(ROOT) : new CompoundTag();
        }

        @FunctionalInterface
        public interface EntryConsumer {
            void accept(int index, CompoundTag slots);
        }
    }
}
