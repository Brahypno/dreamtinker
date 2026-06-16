package org.brahypno.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerAttributes;
import org.brahypno.esotericismtinker.library.modifiers.EsotericismTinkerHook;
import org.brahypno.esotericismtinker.library.modifiers.hook.LivingHealHealHook;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.UUID;

import static org.brahypno.dreamtinker.config.DreamtinkerConfig.BrokenVesselBoost;

public class broken_vessel extends Modifier implements EquipmentChangeModifierHook, LivingHealHealHook {
    public static final String TAG_BASE_HEALTH = "broken_vessel";
    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("broken_vessel"));
    private static final UUID HEALTH_BOOST_ID = UUID.fromString("c8b28a17-d5ec-4fa4-b555-bb1e8f7de4c8");
    private static final int MAX_HEALTH_MULTIPLIER = BrokenVesselBoost.get();

    @Override
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        if (context.getChangedSlot() == EquipmentSlot.MAINHAND || context.getChangedSlot() == EquipmentSlot.OFFHAND)
            return;
        LivingEntity entity = context.getEntity();
        AttributeInstance attr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (attr == null)
            return;

        CompoundTag data = entity.getPersistentData();
        if (!data.contains(TAG_BASE_HEALTH)){
            data.putFloat(TAG_BASE_HEALTH, (float) attr.getBaseValue());
            attr.removeModifier(HEALTH_BOOST_ID);
            attr.addPermanentModifier(
                    new AttributeModifier(HEALTH_BOOST_ID, this.getTranslationKey(), attr.getBaseValue() * MAX_HEALTH_MULTIPLIER,  // 加 baseValue，即翻倍
                                          AttributeModifier.Operation.ADDITION));

            AttributeInstance attr2 = entity.getAttribute(DreamtinkerAttributes.BLOOD_IN_SHELL.get());
            if (attr2 != null){
                attr2.removeModifier(HEALTH_BOOST_ID);
                attr2.addPermanentModifier(
                        new AttributeModifier(HEALTH_BOOST_ID, this.getTranslationKey(), attr.getBaseValue() * MAX_HEALTH_MULTIPLIER,  // 加 baseValue，即翻倍
                                              AttributeModifier.Operation.ADDITION));
            }
        }
    }

    @Override
    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        LivingEntity entity = context.getEntity();
        if (hasOtherPiece(entity))
            return;

        AttributeInstance attr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (attr == null)
            return;

        CompoundTag data = entity.getPersistentData();
        if (!data.contains(TAG_BASE_HEALTH))
            return;

        // 移除 boost
        if (attr.getModifier(HEALTH_BOOST_ID) != null)
            attr.removeModifier(HEALTH_BOOST_ID);

        AttributeInstance attr2 = entity.getAttribute(DreamtinkerAttributes.BLOOD_IN_SHELL.get());
        if (attr2 != null){
            attr2.removeModifier(HEALTH_BOOST_ID);
        }

        // 恢复原始基础血量
        float original = data.getFloat(TAG_BASE_HEALTH);
        attr.setBaseValue(original);
        data.remove(TAG_BASE_HEALTH);

        if (entity.getHealth() > entity.getMaxHealth())
            entity.setHealth(entity.getMaxHealth());

    }

    private boolean hasOtherPiece(LivingEntity entity) {
        for (ItemStack stack : entity.getArmorSlots()) {
            if (stack.getItem().equals(Items.AIR))
                continue;
            ToolStack tool = ToolStack.from(stack);
            if (tool.getModifierLevel(this) > 0)
                return true;
        }
        return false;
    }

    @Override
    public float onHeal(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, float amount) {
        int chargeLevel = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (chargeLevel <= 0){
            return amount;
        }
        LivingEntity entity = context.getEntity();
        CompoundTag data = entity.getPersistentData();
        if (!data.contains(TAG_BASE_HEALTH)){
            return amount;
        }

        float current = entity.getHealth();
        float cap = data.getFloat(TAG_BASE_HEALTH) / (1 + BrokenVesselBoost.get());
        if (cap <= current){
            entity.setHealth(cap);
            return 0.0F;
        }
        if (cap < current + amount){
            return cap - current;
        }
        return amount;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE, EsotericismTinkerHook.HEAL);
        super.registerHooks(hookBuilder);
    }
}
