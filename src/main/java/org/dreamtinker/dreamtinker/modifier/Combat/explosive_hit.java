package org.dreamtinker.dreamtinker.modifier.Combat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeMod;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.ExplodehitFire;

public class explosive_hit extends BattleModifier {
    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity attacker = context.getAttacker();
        if (!attacker.level().isClientSide){
            AttributeInstance reach = attacker.getAttribute(ForgeMod.ENTITY_REACH.get());
            double range = null != reach ? reach.getValue() + 1 : 2;
            explode(context.getTarget(), (float) range);
        }
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        explode(hit.getEntity(), 1);
        return false;
    }

    private void explode(Entity target, float range) {
        target.level().explode(target, target.getX(),
                               target.getY(),
                               target.getZ(),
                               (float) range,
                               ExplodehitFire.get(),
                               Level.ExplosionInteraction.NONE);
    }
}
