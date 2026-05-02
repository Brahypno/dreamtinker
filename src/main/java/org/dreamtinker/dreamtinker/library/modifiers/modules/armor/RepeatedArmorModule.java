package org.dreamtinker.dreamtinker.library.modifiers.modules.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import org.dreamtinker.dreamtinker.Dreamtinker;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
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
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.world.damagesource.CombatRules.getDamageAfterAbsorb;

public record RepeatedArmorModule(
        LevelingValue percentage,
        ModifierCondition<IToolStackView> condition) implements ModifyDamageModifierHook, TooltipModifierHook, ModifierModule, ModifierCondition.ConditionalModule<IToolStackView> {

    public static final RecordLoadable<RepeatedArmorModule> LOADER = RecordLoadable.create(
            LevelingValue.LOADABLE.defaultField("percentage", LevelingValue.eachLevel(0.25f), false, RepeatedArmorModule::percentage),
            ModifierCondition.TOOL_FIELD,
            RepeatedArmorModule::new);

    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("repeated_armor"));

    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<RepeatedArmorModule>defaultHooks(ModifierHooks.MODIFY_DAMAGE, ModifierHooks.TOOLTIP);

    public static RepeatedArmorModule.Builder builder() {
        return new RepeatedArmorModule.Builder();
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        if (!source.is(DamageTypeTags.BYPASSES_ARMOR) && condition.matches(tool, modifier) &&
            SlotInChargeModule.isInCharge(context.getTinkerData(), SLOT_KEY, slotType)){
            float scaledLevel = modifier.getEffectiveLevel();
            float percentage = this.percentage.compute(scaledLevel);
            float armor = context.getEntity().getArmorValue() * percentage;
            float toughness = (float) context.getEntity().getAttributeValue(Attributes.ARMOR_TOUGHNESS) * percentage;
            return getDamageAfterAbsorb(amount, armor, toughness);

        }
        return amount;
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (condition.matches(tool, modifier) && tooltipKey.isShiftOrUnknown()){
            float scaledLevel = modifier.getEffectiveLevel();
            float percentage = this.percentage.compute(scaledLevel);
            Component.literal(Util.PERCENT_BOOST_FORMAT.format(percentage))
                     .append(" ").append(Component.translatable(modifier.getModifier().getTranslationKey() + ".repeated_armor"));
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
    public RecordLoadable<RepeatedArmorModule> getLoader() {
        return LOADER;
    }

    /**
     * Builder for this modifier in datagen
     */
    public static class Builder extends ModuleBuilder.Stack<RepeatedArmorModule.Builder> {
        private LevelingValue percentage = LevelingValue.eachLevel(0.25f);

        public RepeatedArmorModule.Builder percentage(LevelingValue percentage) {
            this.percentage = percentage;
            return this;
        }

        /**
         * Builds the finished modifier
         */
        public RepeatedArmorModule build() {
            return new RepeatedArmorModule(percentage, condition);
        }
    }
}
