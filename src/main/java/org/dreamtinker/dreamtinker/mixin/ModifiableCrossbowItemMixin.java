package org.dreamtinker.dreamtinker.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.tools.items.TNTArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableCrossbowItem;

@Mixin(ModifiableCrossbowItem.class)
public class ModifiableCrossbowItemMixin {
    @Redirect(method = "fireCrossbow(Lslimeknights/tconstruct/library/tools/nbt/IToolStackView;Lnet/minecraft/world/entity/LivingEntity;ZLnet/minecraft/world/InteractionHand;Lnet/minecraft/nbt/CompoundTag;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ArrowItem;createArrow(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/entity/projectile/AbstractArrow;"))
    private static AbstractArrow redirectCreateArrow(ArrowItem instance, Level p_40513_, ItemStack p_40514_, LivingEntity p_40515_) {
        if (p_40514_.getItem() instanceof TNTArrow)
            return ((TNTArrow) (p_40514_.getItem())).createArrow(p_40513_, p_40514_, p_40515_);
        return instance.createArrow(p_40513_, p_40514_, p_40515_);
    }
}
