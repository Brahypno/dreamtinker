package org.brahypno.dreamtinker.mixin.compat.goety;

import com.Polarice3.Goety.common.entities.projectiles.SwordProjectile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

/**
 * Uses Tinkers' Construct's thrown-tool attack pipeline for modifiable swords fired by Sword Focus.
 */
@Mixin(value = SwordProjectile.class, remap = false)
public abstract class SwordProjectileMixin {
    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true, remap = true)
    private void dreamtinker$attackWithModifiableSword(EntityHitResult result, CallbackInfo ci) {
        SwordProjectile projectile = (SwordProjectile) (Object) this;
        ItemStack sword = projectile.getItem();
        if (!sword.is(TinkerTags.Items.SWORD) || !(sword.getItem() instanceof IModifiable)){
            return;
        }

        Entity target = result.getEntity();
        Entity ownerEntity = projectile.getOwner();
        ToolStack tool = ToolStack.from(sword);
        if (!(ownerEntity instanceof LivingEntity owner)
            || !ToolAttackUtil.canPerformAttack(tool)
            || !ToolAttackUtil.isAttackable(owner, target)){
            ci.cancel();
            return;
        }

        ItemStack previousOffhand = owner.getOffhandItem();
        boolean swapOffhand = owner != target;
        try {
            if (swapOffhand){
                owner.setItemInHand(InteractionHand.OFF_HAND, sword);
            }
            boolean hit = ToolAttackUtil.performAttack(
                    tool,
                    ToolAttackContext.attacker(owner)
                                     .target(target)
                                     .hand(InteractionHand.OFF_HAND)
                                     .baseDamage(tool.getStats().get(ToolStats.ATTACK_DAMAGE))
                                     .cooldown(1.0F)
                                     .projectile(projectile)
                                     .build());
            if (hit){
                projectile.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
            }
        }
        finally {
            if (swapOffhand){
                owner.setItemInHand(InteractionHand.OFF_HAND, previousOffhand);
            }
        }
        ci.cancel();
    }
}
