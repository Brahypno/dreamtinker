package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.desire_gem;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EternityDefense extends Modifier implements ModifyDamageModifierHook {
    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("eternity_defense"));

    private static final ResourceLocation MEMORY_KEY = Dreamtinker.getLocation("eternity_memory");
    private static final String TYPES_KEY = "types";
    private static final String COUNTS_KEY = "counts";

    private static int getStacks(IToolStackView tool, String id) {
        CompoundTag memory = tool.getPersistentData().getCompound(MEMORY_KEY);
        CompoundTag counts = memory.getCompound(COUNTS_KEY);

        return counts.getInt(id);
    }

    private static void remember(IToolStackView tool, int capacity, String id) {
        CompoundTag memory = tool.getPersistentData().getCompound(MEMORY_KEY);

        ListTag types = memory.getList(TYPES_KEY, Tag.TAG_STRING);
        CompoundTag counts = memory.getCompound(COUNTS_KEY);

        // 已记忆：层数+1
        if (contains(types, id)){
            counts.putInt(id, counts.getInt(id) + 1);
        }else {
            // FIFO：先记录的先移除
            while (types.size() >= capacity && !types.isEmpty()) {
                String removed = types.getString(0);

                types.remove(0);
                counts.remove(removed);
            }

            types.add(StringTag.valueOf(id));

            // 第一次仅记录，不减伤
            counts.putInt(id, 1);
        }

        memory.put(TYPES_KEY, types);
        memory.put(COUNTS_KEY, counts);

        tool.getPersistentData().put(MEMORY_KEY, memory);
    }

    private static boolean contains(ListTag types, String id) {
        for (int i = 0; i < types.size(); i++) {
            if (id.equals(types.getString(i))){
                return true;
            }
        }

        return false;
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        hookBuilder.addHook(this, ModifierHooks.MODIFY_HURT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float modifyDamageTaken(
            IToolStackView tool, ModifierEntry modifier, EquipmentContext context,
            EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {

        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
            || source.is(DamageTypeTags.BYPASSES_EFFECTS)){
            return amount;
        }

        String id = source.getMsgId();

        int chargeLevel = SlotInChargeModule.getLevel(
                context.getTinkerData(),
                SLOT_KEY,
                slotType
        );


        float result = amount;

        // 只有负责 slot 才减伤
        if (chargeLevel > 0){
            int stacks = getStacks(tool, id);
            if (stacks > 0)
                result *= 1.0f / (stacks + 1);
        }

        // 所有带 modifier 的护甲都记录
        remember(tool, Math.min(3, chargeLevel), id);

        return result;
    }

}
