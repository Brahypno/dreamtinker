package org.brahypno.dreamtinker.library.modifiers.modules.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reduces matching damage once using the sum of the armor stat on every equipped piece carrying this modifier.
 * A tracker key derived from the owning modifier ensures only one equipped copy performs the combined calculation.
 */
public record ArmorValueReductionModule(IJsonPredicate<DamageSource> damageSource, float ratio)
        implements ModifierModule, EquipmentChangeModifierHook, ModifyDamageModifierHook, TooltipModifierHook {
    private static final Map<slimeknights.tconstruct.library.modifiers.ModifierId,
            TinkerDataKey<SlotInChargeModule.SlotInCharge>> TRACKERS =
            new ConcurrentHashMap<>();
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.defaultHooks(
            ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.MODIFY_DAMAGE, ModifierHooks.TOOLTIP);

    public static final RecordLoadable<ArmorValueReductionModule> LOADER = RecordLoadable.create(
            DamageSourcePredicate.LOADER.requiredField("damage_source", ArmorValueReductionModule::damageSource),
            FloatLoadable.FROM_ZERO.requiredField("ratio", ArmorValueReductionModule::ratio),
            ArmorValueReductionModule::new);

    private static TinkerDataKey<SlotInChargeModule.SlotInCharge> trackerKey(ModifierEntry modifier) {
        return TRACKERS.computeIfAbsent(modifier.getId(), TinkerDataKey::of);
    }

    private static SlotInChargeModule slotTracker(ModifierEntry modifier) {
        return new SlotInChargeModule(trackerKey(modifier));
    }

    @Override
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        slotTracker(modifier).onEquip(tool, modifier, context);
    }

    @Override
    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        slotTracker(modifier).onUnequip(tool, modifier, context);
    }

    @Override
    public float modifyDamageTaken(
            IToolStackView tool, ModifierEntry modifier, EquipmentContext context,
            EquipmentSlot slotType, DamageSource source, float amount,
            boolean isDirectDamage) {
        if (amount <= 0 || !damageSource.matches(source)
            || SlotInChargeModule.getLevel(context.getTinkerData(), trackerKey(modifier), slotType) <= 0){
            return amount;
        }

        float armor = 0;
        for (EquipmentSlot armorSlot : ModifiableArmorMaterial.ARMOR_SLOTS) {
            IToolStackView equipped = context.getValidTool(armorSlot);
            if (equipped != null && !equipped.isBroken() && equipped.getModifierLevel(modifier.getId()) > 0){
                armor += equipped.getStats().get(ToolStats.ARMOR);
            }
        }
        return Math.max(0, amount * (1 - armor * ratio));
    }

    @Override
    public void addTooltip(
            IToolStackView tool, ModifierEntry modifier, @Nullable Player player,
            List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        float armor = tool.getStats().get(ToolStats.ARMOR);
        if (armor > 0){
            TooltipModifierHook.addPercentBoost(
                    modifier.getModifier(),
                    Component.translatable(modifier.getModifier().getTranslationKey() + ".resistance"),
                    armor * ratio, tooltip);
        }
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<ArmorValueReductionModule> getLoader() {
        return LOADER;
    }
}
