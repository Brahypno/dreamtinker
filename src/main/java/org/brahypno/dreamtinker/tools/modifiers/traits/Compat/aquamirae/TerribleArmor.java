package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.aquamirae;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Set bonuses modeled after Aquamirae's terrible armor.
 */
public class TerribleArmor extends Modifier implements OnAttackedModifierHook {
    private static final ResourceLocation DEPTHS_FURY = new ResourceLocation("aquamirae", "depths_fury");
    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("terrible_armor"));

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
        super.registerHooks(hookBuilder);
    }

    /**
     * Returns the total terrible armor level across equipped armor pieces.
     */
    public static int getEquippedLevel(LivingEntity entity) {
        TinkerDataCapability.Holder data = TinkerDataCapability.getData(entity);
        if (data == null){
            return 0;
        }
        SlotInChargeModule.SlotInCharge tracker = data.get(SLOT_KEY);
        return tracker == null ? 0 : tracker.getTotalLevel();
    }

    private static int depthsFuryAmplifier(LivingEntity entity, float baseValue, float maxBonus) {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(DEPTHS_FURY);
        double depthsFury = attribute == null ? 1.0D : entity.getAttributeValue(attribute);
        return Math.max(0, Math.round(baseValue + maxBonus * ((float) depthsFury - 1.0F)));
    }

    @Override
    public void onAttacked(
            IToolStackView tool, ModifierEntry modifier, EquipmentContext context,
            EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        LivingEntity wearer = context.getEntity();
        if (wearer.level().isClientSide || amount <= 0){
            return;
        }

        // Only the slot selected by SlotInChargeModule receives the total level, preventing duplicate triggers.
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (level < 2){
            return;
        }

        if (source.getDirectEntity() instanceof LivingEntity attacker){
            attacker.addEffect(new MobEffectInstance(MobEffects.POISON, 80,
                                                     depthsFuryAmplifier(wearer, 0.0F, 2.0F)));
        }
        if (level >= 4 && wearer.isInWaterOrBubble()){
            wearer.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 120,
                                                   depthsFuryAmplifier(wearer, 1.0F, 3.0F)));
        }
    }
}
