package org.brahypno.dreamtinker.mixin.compact.legendary_monsters;


import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import slimeknights.tconstruct.common.TinkerTags;

import static org.brahypno.dreamtinker.tools.DreamtinkerModifiers.annihilator_armor_power;

@Pseudo
@Mixin(targets = "net.miauczel.legendary_monsters.event.ForgeEvents", remap = false)
public class LMannLeggingsMixin {
    @Redirect(
            method = "onLivingHurt(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z",
                    ordinal = 1
            ),
            remap = false
    )
    private static boolean dreamtinker$bypassAnnihilatorLeggings(ItemStack stack, Item p_150931_) {
        return stack.is(p_150931_) || (stack.is(TinkerTags.Items.LEGGINGS) || stack.is(TinkerTags.Items.HELMETS)) &&
                                      0 < ETModifierCheck.getItemModifierNum(stack, annihilator_armor_power.getId());
    }
}
