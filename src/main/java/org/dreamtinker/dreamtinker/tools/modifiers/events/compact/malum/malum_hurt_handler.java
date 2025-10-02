package org.dreamtinker.dreamtinker.tools.modifiers.events.compact.malum;

import com.sammy.malum.common.item.curiosities.weapons.scythe.MalumScytheItem;
import com.sammy.malum.registry.common.item.ItemRegistry;
import com.sammy.malum.registry.common.item.ItemTagRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;

public class malum_hurt_handler {
    public static void MalumLivingHurtEvent(LivingHurtEvent event) {
        DamageSource dmg = event.getSource();
        if (null != dmg.getEntity() && dmg.getEntity() instanceof Player player){
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof ModifiableItem && stack.is(ItemTagRegistry.SCYTHE)){
                ((MalumScytheItem) ItemRegistry.CRUDE_SCYTHE.get()).hurtEvent(event, player, event.getEntity(), stack);
            }
        }
    }
}
