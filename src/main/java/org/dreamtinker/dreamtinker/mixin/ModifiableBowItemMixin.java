package org.dreamtinker.dreamtinker.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.tools.TNTarrow.TNTarrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem;


@Mixin(ModifiableBowItem.class)
public class ModifiableBowItemMixin {
    @Redirect(method = "releaseUsing",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ArrowItem;createArrow(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/entity/projectile/AbstractArrow;"))
    private AbstractArrow redirectCreateArrow(ArrowItem instance, Level p_40513_, ItemStack p_40514_, LivingEntity p_40515_) {
        if (p_40514_.getItem() instanceof TNTarrow)
            return ((TNTarrow) (p_40514_.getItem())).createArrow(p_40513_, p_40514_, p_40515_);
        return instance.createArrow(p_40513_, p_40514_, p_40515_);
    }
}
