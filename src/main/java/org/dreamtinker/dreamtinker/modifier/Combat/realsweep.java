package org.dreamtinker.dreamtinker.modifier.Combat;


import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;

import static slimeknights.tconstruct.TConstruct.getResource;

public class realsweep extends BattleModifier {
    public realsweep(){}

    public static float getSweepRange(IToolStackView toolData) {
        System.out.println(" event triggered! Modifier level: " + toolData.getPersistentData());

        if (toolData.getPersistentData().contains(getResource("sweep_range"),99)) {
            return toolData.getPersistentData().getFloat(getResource("sweep_melee"));
        }
        if (toolData.getPersistentData().contains(getResource("attack_range"),99)) {
            return toolData.getPersistentData().getFloat(getResource("attack_range"));
        }
        return 0.0f;
    }
    private int getLevel(IToolStackView tool){return tool.getModifierLevel(this);}

    @Override
    public Component onModifierRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().addSlots(ABILITY,1);
        return null;
    }

    public void superSweep(IToolStackView tool, ModifierEntry entry, Player player, Level level, Entity entity){
        if (!level.isClientSide&&player.getAttackStrengthScale(0)>0.8&& !tool.isBroken()){
            // basically sword sweep logic, just deals full damage to all entities
            float diameter=2;//getSweepRange(tool); To improve in 1.20
            double range = diameter + tool.getModifierLevel(TinkerModifiers.expanded.getId());
            float sweepDamage = TinkerModifiers.sweeping.get().getSweepingDamage(tool, tool.getStats().get(ToolStats.ATTACK_DAMAGE));

            if (range > 0) {
                double rangeSq = range * range;
                for (LivingEntity aoeTarget : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(range, 0.25D, range))) {
                    if (aoeTarget != player &&  !player.isAlliedTo(aoeTarget)
                            && !(aoeTarget instanceof ArmorStand stand && stand.isMarker()) && player.distanceToSqr(aoeTarget) < rangeSq) {
                        if (1 < getLevel(tool)) {
                            ToolAttackUtil.attackEntity(tool, player, tool.getItem().equals(player.getMainHandItem().getItem())?InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND,aoeTarget,() -> 10, true);
                        }else{
                            float angle = player.getYRot() * ((float)Math.PI / 180F);
                            aoeTarget.knockback(0.4F, Mth.sin(angle), -Mth.cos(angle));
                            ToolAttackUtil.dealDefaultDamage(player, aoeTarget, sweepDamage);
                        }
                    }
                }

                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
                player.sweepAttack();
            }
        }
    }

    @Override
    public void onLeftClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {
        superSweep(tool, entry, player, level,null);
    }

    @Override
    public void onLeftClickBlock(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, BlockState state, BlockPos pos) {
        superSweep(tool, entry, player, level,null);
    }

    @Override
    public void onLeftClickEntity(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot,Entity target) {
        superSweep(tool, entry, player, level,target);
    }
}
