package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.malum;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerEffects;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import team.lodestar.lodestone.helpers.EntityHelper;
import team.lodestar.lodestone.registry.common.LodestoneAttributeRegistry;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.dreamtinker.dreamtinker.common.effect.thirsty.Gluttony;
import static org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers.as_one;
import static org.dreamtinker.dreamtinker.utils.DTModiferCheck.haveModifierIn;

public class malum_thirsty extends BattleModifier {
    private static final ResourceLocation TAG_GLU = new ResourceLocation(Dreamtinker.MODID, "enhanced_glu");
    private static final ResourceLocation iron_spell_power = new ResourceLocation("irons_spellbooks", "spell_power");

    // 是否已注册
    private static final boolean iron_spell_power_exists = ForgeRegistries.ATTRIBUTES.containsKey(iron_spell_power);
    public static final EnumMap<EquipmentSlot, UUID> SLOT_UUIDS;

    static {
        EnumMap<EquipmentSlot, UUID> m = new EnumMap<>(EquipmentSlot.class);
        m.put(EquipmentSlot.MAINHAND, UUID.fromString("8b0a9c2e-1d74-4a33-92d3-14f3cb2c8c10"));
        m.put(EquipmentSlot.OFFHAND, UUID.fromString("1f3a5b7c-2e68-4f9b-a4c1-25d6e7f8a9b0"));
        m.put(EquipmentSlot.HEAD, UUID.fromString("c7e2d1a4-3b5f-4c8e-9a71-36b4d5e6f7a8"));
        m.put(EquipmentSlot.CHEST, UUID.fromString("ad4c2f1e-4a6b-4d9c-8e12-47f5a6b7c8d9"));
        m.put(EquipmentSlot.LEGS, UUID.fromString("2b6e4d1c-5f7a-4c3b-9172-58a6b7c8d9e0"));
        m.put(EquipmentSlot.FEET, UUID.fromString("9d1c3e5a-6b8d-4f0a-a283-69c7d8e9f0a1"));
        SLOT_UUIDS = new EnumMap<>(m);
    }

    @Override
    public void modifierOnInventoryTick(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull Level world, @NotNull LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, @NotNull ItemStack stack) {
        if (world.isClientSide)
            return;
        if ((!isCorrectSlot && !isSelected)){
            tool.getPersistentData().remove(TAG_GLU);
            return;
        }
        if (holder instanceof Player player && isCorrectSlot && world.getGameTime() % 20 == 0){
            if (null != Gluttony() && !player.hasEffect(Gluttony()) && !haveModifierIn(holder, as_one.getId())){
                MobEffectInstance thirsty = player.getEffect(DreamtinkerEffects.thirsty.get());
                if (null != thirsty)
                    EntityHelper.amplifyEffect(thirsty, player, modifier.getLevel());
                else
                    player.addEffect(new MobEffectInstance(DreamtinkerEffects.thirsty.get(), 200, 2, true, false));
            }
        }
        if (null != Gluttony()){
            MobEffectInstance glu = holder.getEffect(Gluttony());
            ModDataNBT tool_data = tool.getPersistentData();
            if (null != glu && tool_data.getInt(TAG_GLU) != glu.getAmplifier() + 1)
                tool_data.putInt(TAG_GLU, glu.getAmplifier() + 1);
            else if (null == glu){
                tool_data.remove(TAG_GLU);
            }
        }
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        int extra_glu = tool.getPersistentData().getInt(TAG_GLU);
        if (0 < extra_glu){
            ArrayList<Attribute> attributes = new ArrayList<>(List.of(LodestoneAttributeRegistry.MAGIC_PROFICIENCY.get()));
            if (iron_spell_power_exists)
                attributes.add(ForgeRegistries.ATTRIBUTES.getValue(iron_spell_power));
            for (int i = 0; i < attributes.size(); i++) {
                consumer.accept(attributes.get(i),
                                new AttributeModifier(SLOT_UUIDS.get(slot), LodestoneAttributeRegistry.MAGIC_PROFICIENCY.get().getDescriptionId(),
                                                      0.2f * extra_glu / (i + 1),
                                                      AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }
    }
}
