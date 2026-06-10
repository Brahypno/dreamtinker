package org.brahypno.dreamtinker.library.modifiers.modules.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.brahypno.dreamtinker.Dreamtinker;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.library.tools.helper.ArmorUtil.getDamageAfterMagicAbsorb;

public record RepriseProtectionModule(
        LevelingValue percentage,
        ModifierCondition<IToolStackView> condition) implements ModifyDamageModifierHook, TooltipModifierHook, ModifierModule, ModifierCondition.ConditionalModule<IToolStackView> {

    public static final RecordLoadable<RepriseProtectionModule> LOADER = RecordLoadable.create(
            LevelingValue.LOADABLE.defaultField("percentage", LevelingValue.eachLevel(0.25f), false, RepriseProtectionModule::percentage),
            ModifierCondition.TOOL_FIELD,
            RepriseProtectionModule::new);

    private static final List<ModuleHook<?>> DEFAULT_HOOKS =
            HookProvider.<RepriseProtectionModule>defaultHooks(ModifierHooks.TOOLTIP);

    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("reprise_protection"));

    public static RepriseProtectionModule.Builder builder() {
        return new RepriseProtectionModule.Builder();
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        if (condition.matches(tool, modifier) &&
            SlotInChargeModule.isInCharge(context.getTinkerData(), SLOT_KEY, slotType)){
            float modifierValue = 0;
            LivingEntity entity = context.getEntity();
            if (context.hasModifiableArmor()){
                if (DamageSourcePredicate.CAN_PROTECT.matches(source)){
                    modifierValue = EnchantmentHelper.getDamageProtection(entity.getArmorSlots(), source);
                }
                for (EquipmentSlot slotType1 : EquipmentSlot.values()) {
                    if (ModifierUtil.validArmorSlot(entity, slotType1)){
                        IToolStackView tool1 = context.getToolInSlot(slotType1);
                        if (tool1 != null && !tool1.isBroken()){
                            for (ModifierEntry entry : tool1.getModifierList()) {
                                modifierValue =
                                        entry.getHook(ModifierHooks.PROTECTION).getProtectionModifier(tool1, entry, context, slotType1, source, modifierValue);
                            }
                        }
                    }
                }
                if (entity.getType().is(TinkerTags.EntityTypes.SMALL_ARMOR)){
                    modifierValue *= 4;
                }
            }else if (entity.getType().is(TinkerTags.EntityTypes.SMALL_ARMOR)){
                modifierValue = EnchantmentHelper.getDamageProtection(entity.getArmorSlots(), source) * 4;
            }
            float scaledLevel = modifier.getEffectiveLevel();
            float percentage = this.percentage.compute(scaledLevel);
            modifierValue *= percentage;
            float cap = 20f;
            if (modifierValue > 0){
                cap = (float) ProtectionModifierHook.getProtectionCap(entity, context.getTinkerData());
            }
            return getDamageAfterMagicAbsorb(amount, modifierValue, cap);
        }
        return amount;
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (condition.matches(tool, modifier) && tooltipKey.isShiftOrUnknown()){
            float scaledLevel = modifier.getEffectiveLevel();
            float percentage = this.percentage.compute(scaledLevel);
            Component.literal(Util.PERCENT_BOOST_FORMAT.format(percentage))
                     .append(" ").append(Component.translatable(modifier.getModifier().getTranslationKey() + ".reprise_protection"));
        }
    }

    @Override
    public void addModules(ModuleHookMap.Builder builder) {
        builder.addModule(new SlotInChargeModule(SLOT_KEY));
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<RepriseProtectionModule> getLoader() {
        return LOADER;
    }

    /**
     * Builder for this modifier in datagen
     */
    public static class Builder extends ModuleBuilder.Stack<RepriseProtectionModule.Builder> {
        private LevelingValue percentage = LevelingValue.eachLevel(0.25f);

        public RepriseProtectionModule.Builder percentage(LevelingValue percentage) {
            this.percentage = percentage;
            return this;
        }

        /**
         * Builds the finished modifier
         */
        public RepriseProtectionModule build() {
            return new RepriseProtectionModule(percentage, condition);
        }
    }
}
