package org.brahypno.dreamtinker.tools.modifiers.traits.harvest;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.brahypno.dreamtinker.utils.DTHelper;
import org.brahypno.dreamtinker.utils.LootHelper.DTLoots;
import org.brahypno.dreamtinker.utils.LootHelper.GlobalLootModifierItemScanner;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ForceDrop extends Modifier implements MeleeHitModifierHook, MonsterMeleeHitModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        failedMeleeHit(tool, modifier, context, damageDealt);
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        LivingEntity target = DTHelper.getLivingTarget(context.getTarget());
        if (null != target){
            DTLoots.dropAllDeathLootVanilla(target, context.makeDamageSource());
            if (context.getLevel() instanceof ServerLevel sl)
                for (ItemStack itemStack : GlobalLootModifierItemScanner.getAllScannedLootStacksMinOne(sl, target)) {
                    ItemEntity entity =
                            new ItemEntity(sl, target.getX(), target.getY(), target.getZ(), itemStack);
                    sl.addFreshEntity(entity);
                }
        }
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        afterMeleeHit(tool, modifier, context, damage);
    }
}
