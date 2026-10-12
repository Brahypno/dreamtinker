package org.brahypno.dreamtinker.library.modifiers.modules.armor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.variable.VariableFormula;
import slimeknights.tconstruct.library.json.variable.protection.ProtectionFormula;
import slimeknights.tconstruct.library.json.variable.protection.ProtectionVariable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.BlockDamageSourceModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Extends Tinkers' source/tool filtering with a context formula: a positive result blocks the damage.
 */
public record ConditionalDamageBlockModule(BlockDamageSourceModule block, ProtectionFormula formula)
        implements ModifierModule, DamageBlockModifierHook {
    public static final RecordLoadable<ConditionalDamageBlockModule> LOADER = RecordLoadable.create(
            BlockDamageSourceModule.LOADER.directField(ConditionalDamageBlockModule::block),
            ProtectionFormula.DAMAGE_LOADER.directField(ConditionalDamageBlockModule::formula),
            ConditionalDamageBlockModule::new);
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.defaultHooks(ModifierHooks.DAMAGE_BLOCK);

    @Override
    public boolean isDamageBlocked(
            IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slot,
            DamageSource source, float amount) {
        return block.isDamageBlocked(tool, modifier, context, slot, source, amount)
               && formula.apply(tool, modifier, context, context.getEntity(), slot, source, amount) > 0;
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<ConditionalDamageBlockModule> getLoader() {
        return LOADER;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends VariableFormula.Builder<Builder, ConditionalDamageBlockModule, ProtectionVariable> {
        private IJsonPredicate<DamageSource> source = DamageSourcePredicate.ANY;

        private Builder() {
            super(ProtectionFormula.VARIABLES);
        }

        public Builder source(IJsonPredicate<DamageSource> source) {
            this.source = source;
            return this;
        }

        @Override
        protected ConditionalDamageBlockModule build(ModifierFormula formula) {
            return new ConditionalDamageBlockModule(new BlockDamageSourceModule(source, condition),
                                                    new ProtectionFormula(formula, variables, percent));
        }
    }
}
