package org.brahypno.dreamtinker.tools.modifiers.traits.armors;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.library.modifiers.variable.SlotInChargeReductionVariable;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.armor.AdjustDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.List;

import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.AbsorptionDefenseRate;

public class AbsorptionDefense extends Modifier implements ModifyDamageModifierHook {
    private static final net.minecraft.resources.ResourceLocation TRACKER = Dreamtinker.getLocation("absorption_defense");
    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(TRACKER);

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        hookBuilder.addHook(AdjustDamageModule.builder()
                                              .holder(LivingEntityPredicate.simple(entity -> entity.getAbsorptionAmount() > 0))
                                              .customVariable("reduction", new SlotInChargeReductionVariable(TRACKER))
                                              .formula()
                                              .variable(slimeknights.tconstruct.library.json.math.ModifierFormula.VALUE)
                                              .constant(1.0f)
                                              .customVariable("reduction").subtract()
                                              .constant(0.1f).max()
                                              .multiply()
                                              .build(),
                            ModifierHooks.MODIFY_HURT);
        hookBuilder.addHook(this, ModifierHooks.MODIFY_HURT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (0 < level){
            float absorption = context.getEntity().getAbsorptionAmount();
            if (0 < absorption){
                if (absorption <= amount && source.getEntity() instanceof LivingEntity entity)
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, level * 20, level));
            }
        }
        return amount;
    }

    @Override
    public @NotNull List<Component> getDescriptionList() {
        return Arrays.asList(Component.translatable(this.getTranslationKey() + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(this.getTranslationKey() + ".description",
                                                    String.format("%.0f%%", AbsorptionDefenseRate.get() * 100))
                                      .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public int getPriority() {
        return -DEFAULT_PRIORITY;
    }
}
