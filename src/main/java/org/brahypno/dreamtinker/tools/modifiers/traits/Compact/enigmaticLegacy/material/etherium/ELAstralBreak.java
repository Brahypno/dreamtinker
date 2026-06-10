package org.brahypno.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.etherium;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.brahypno.dreamtinker.utils.CompactUtils.EnigmaticLegacyCompact;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ELAstralBreak extends Modifier implements BlockBreakModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.BLOCK_BREAK);
        super.registerHooks(hookBuilder);
    }

    private static final ThreadLocal<Boolean> CALLING_ASTRAL = ThreadLocal.withInitial(() -> false);

    @Override
    public void afterBlockBreak(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
        if (context.isAOE() || CALLING_ASTRAL.get())
            return;

        Level level = context.getWorld();
        if (level.isClientSide || !(context.getLiving() instanceof Player player))
            return;

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty())
            return;

        CALLING_ASTRAL.set(true);
        try {
            EnigmaticLegacyCompact.astralBreakerMineBlock(stack, level, context.getState(), context.getTargetedPos(), player);
        }
        finally {
            CALLING_ASTRAL.set(false);
        }
    }
}
