package org.dreamtinker.dreamtinker.modifier.material.nigrescence_antimony;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.ArmorModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.UUID;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.BrokenVesselBoost;

public class broken_vessel extends ArmorModifier {
    private static final String TAG_BASE_HEALTH = "broken_vessel";
    private static final UUID HEALTH_BOOST_ID = UUID.fromString("c8b28a17-d5ec-4fa4-b555-bb1e8f7de4c8");
    private static final int MAX_HEALTH_MULTIPLIER = BrokenVesselBoost.get();

    {
        MinecraftForge.EVENT_BUS.addListener(this::LivingHealEvent);
    }

    @Override
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        LivingEntity entity = context.getEntity();
        AttributeInstance attr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (attr == null) return;

        CompoundTag data = entity.getPersistentData();
        if (!data.contains(TAG_BASE_HEALTH)) {
            data.putFloat(TAG_BASE_HEALTH, (float)attr.getBaseValue());
            attr.removeModifier(HEALTH_BOOST_ID);
            attr.addPermanentModifier(new AttributeModifier(
                    HEALTH_BOOST_ID,
                    TAG_BASE_HEALTH,
                    attr.getBaseValue()*MAX_HEALTH_MULTIPLIER,  // 加 baseValue，即翻倍
                    AttributeModifier.Operation.ADDITION
            ));
        }
    }

    @Override
    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        LivingEntity entity = context.getEntity();
        if (hasOtherPiece(entity))
            return;

        AttributeInstance attr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (attr == null) return;

        CompoundTag data = entity.getPersistentData();
        if (!data.contains(TAG_BASE_HEALTH)) return;

        // 移除 boost
        if (attr.getModifier(HEALTH_BOOST_ID) != null)
            attr.removeModifier(HEALTH_BOOST_ID);

        // 恢复原始基础血量
        float original = data.getFloat(TAG_BASE_HEALTH);
        attr.setBaseValue(original);
        data.remove(TAG_BASE_HEALTH);

        if (entity.getHealth() > entity.getMaxHealth())
            entity.setHealth(entity.getMaxHealth());

    }

    private boolean hasOtherPiece(LivingEntity entity) {
        for (ItemStack stack : entity.getArmorSlots()) {
            if(stack.getItem().equals(Items.AIR)) continue;
            ToolStack tool=ToolStack.from(stack);
            if (tool.getModifierLevel(this) > 0)
                return true;
        }
        return false;
    }
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent evt) {
        LivingEntity entity = evt.getEntity();
        CompoundTag data = entity.getPersistentData();
        if (!data.contains(TAG_BASE_HEALTH)) return;
        AttributeInstance attr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (attr == null) return;
        if (attr.getModifier(HEALTH_BOOST_ID) == null) return;
    }


    private void LivingHealEvent(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag data = entity.getPersistentData();

        if (!data.contains(TAG_BASE_HEALTH))
            return;

        // 读取记录的基础血量上限
        float baseHealth = data.getFloat(TAG_BASE_HEALTH);
        // 计算血量允许恢复到的一半
        float cap = baseHealth / (1+MAX_HEALTH_MULTIPLIER);

        float current = entity.getHealth();
        float heal = event.getAmount();

        if (cap<=current) {
            event.setAmount(0f);
            entity.setHealth(cap);
        }else if (cap<current + heal ) {
            event.setAmount(cap - current);
            entity.setHealth(cap);
        }
        float currentAbs = entity.getAbsorptionAmount();
        if(currentAbs < cap){
            float toAbsorb = Math.min(cap-currentAbs, heal);
            entity.setAbsorptionAmount(currentAbs + toAbsorb);
        }

    }
    @Override
    public boolean isNoLevels(){return true;}
}
