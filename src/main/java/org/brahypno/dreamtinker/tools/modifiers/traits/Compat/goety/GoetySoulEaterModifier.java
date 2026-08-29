package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.goety;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;

/**
 * Exposes Goety's Soul Eater enchantment level through a normal Tinkers' modifier.
 */
public class GoetySoulEaterModifier extends Modifier {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(EnchantmentModule.builder(ModEnchantments.SOUL_EATER.get()).level(1).constant());
        super.registerHooks(hookBuilder);
    }
}
