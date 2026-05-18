package org.dreamtinker.dreamtinker.tools.modifiers.traits.harvest;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.utils.LootHelper.DTLoots;
import org.dreamtinker.dreamtinker.utils.LootHelper.GlobalLootModifierItemScanner;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ForceDrop extends Modifier implements MeleeHitModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        failedMeleeHit(tool, modifier, context, damageDealt);
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        if (null != context.getLivingTarget()){
            DTLoots.dropAllDeathLootVanilla(context.getLivingTarget(), context.makeDamageSource());
            if (context.getLevel() instanceof ServerLevel sl)
                for (ItemStack itemStack : GlobalLootModifierItemScanner.getAllScannedLootStacksMinOne(sl, context.getLivingTarget())) {
                    ItemEntity entity =
                            new ItemEntity(sl, context.getLivingTarget().getX(), context.getLivingTarget().getY(), context.getLivingTarget().getZ(), itemStack);
                    sl.addFreshEntity(entity);
                }
        }
    }


}
