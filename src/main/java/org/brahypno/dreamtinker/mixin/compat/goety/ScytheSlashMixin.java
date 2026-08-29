package org.brahypno.dreamtinker.mixin.compat.goety;

import com.Polarice3.Goety.common.entities.projectiles.ScytheSlash;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compat.goety.GoetyDeathScytheModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

/**
 * Adds Sapped only after this projectile's own damage call succeeds.
 */
@Mixin(value = ScytheSlash.class, remap = false)
public abstract class ScytheSlashMixin {
    @Shadow
    private ItemStack weapon;

    @Redirect(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
            remap = true)
    private boolean dreamtinker$applySappedOnHit(Entity target, DamageSource source, float damage) {
        boolean hurt = target.hurt(source, damage);
        if (hurt && target instanceof LivingEntity living && weapon.getItem() instanceof IModifiable
            && ToolStack.from(weapon).getModifierLevel(DreamtinkerModifiers.Ids.goety_death_scythe) > 0){
            GoetyDeathScytheModifier.applySapped(living);
        }
        return hurt;
    }
}
