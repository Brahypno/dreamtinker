package org.dreamtinker.dreamtinker.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.tools.items.TNTarrow.TNTArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(BowItem.class)
public class BowItemMixin {

    @Redirect(method = "releaseUsing",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ArrowItem;createArrow(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/entity/projectile/AbstractArrow;"))
    private AbstractArrow redirectCreatArrow(ArrowItem instance, Level p_40513_, ItemStack p_40514_, LivingEntity p_40515_) {
        if (p_40514_.getItem() instanceof TNTArrow)
            return ((TNTArrow) (p_40514_.getItem())).createArrow(p_40513_, p_40514_, p_40515_);
        return instance.createArrow(p_40513_, p_40514_, p_40515_);
    }
}
