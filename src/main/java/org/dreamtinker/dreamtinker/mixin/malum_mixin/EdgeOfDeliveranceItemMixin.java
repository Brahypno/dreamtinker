package org.dreamtinker.dreamtinker.mixin.malum_mixin;

import com.sammy.malum.common.item.curiosities.weapons.scythe.EdgeOfDeliveranceItem;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagkeys;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EdgeOfDeliveranceItem.class, remap = false)
public class EdgeOfDeliveranceItemMixin {
    @Unique
    private static final ThreadLocal<ItemStack> TL_STACK = new ThreadLocal<>();

    @Inject(method = "hurtEvent(Lnet/minecraftforge/event/entity/living/LivingHurtEvent;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("HEAD"))
    private void dreamtinker$capture(LivingHurtEvent event, LivingEntity attacker, LivingEntity target, ItemStack stack, CallbackInfo ci) {
        TL_STACK.set(stack);
    }

    @Group(name = "scytheCheck", min = 1, max = 1) // 两个redirect里命中一个即可
    @Redirect(
            method = "hurtEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"
            ),
            remap = true,
            require = 0 // 若此调用在发行版里不存在，不要让注入失败
    )
    private boolean dreamtinker$redirectIsOnDamageSource(
            DamageSource instance, TagKey<DamageType> p_270890_) {
        boolean base = instance.is(p_270890_);
        return base || 0 < DTModifierCheck.getItemModifierNum(TL_STACK.get(), DreamtinkerTagkeys.Modifiers.MALUM_EXPOSE_SOUL);
    }

    @Inject(method = "hurtEvent(Lnet/minecraftforge/event/entity/living/LivingHurtEvent;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("RETURN"))
    private void dreamtinker$cleanup(LivingHurtEvent e, LivingEntity a, LivingEntity t, ItemStack s, CallbackInfo ci) {
        TL_STACK.remove();
    }

}
