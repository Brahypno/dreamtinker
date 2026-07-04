package org.brahypno.dreamtinker.tools.modifiers.traits.Compat;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.RawDataModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;


public class NovaReactive extends Modifier implements RawDataModifierHook {
    private final ResourceLocation ReactiveCaster = new ResourceLocation(ArsNouveau.MODID, "reactive_caster");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        super.registerHooks(hookBuilder);
        hookBuilder.addHook(this, ModifierHooks.RAW_DATA);
        hookBuilder.addModule(EnchantmentModule.builder(com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry.REACTIVE_ENCHANTMENT.get()).level(1)
                                               .constant());
    }

    @Override
    public void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag) {

    }

    @Override
    public void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag) {
        tag.remove(ReactiveCaster.toString());
    }


}
