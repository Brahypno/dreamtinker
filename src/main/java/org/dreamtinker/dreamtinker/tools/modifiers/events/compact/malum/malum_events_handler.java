package org.dreamtinker.dreamtinker.tools.modifiers.events.compact.malum;

import com.sammy.malum.common.item.curiosities.weapons.TyrvingItem;
import com.sammy.malum.common.item.curiosities.weapons.WeightOfWorldsItem;
import com.sammy.malum.common.item.curiosities.weapons.scythe.EdgeOfDeliveranceItem;
import com.sammy.malum.common.item.curiosities.weapons.scythe.MalumScytheItem;
import com.sammy.malum.registry.common.item.ItemRegistry;
import com.sammy.malum.registry.common.item.ItemTagRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class malum_events_handler {
    public static void MalumLivingHurtEvent(LivingHurtEvent event) {
        DamageSource dmg = event.getSource();
        if (null != dmg.getEntity() && dmg.getEntity() instanceof Player player){
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof ModifiableItem && stack.is(ItemTagRegistry.SCYTHE)){
                ((MalumScytheItem) ItemRegistry.CRUDE_SCYTHE.get()).hurtEvent(event, player, event.getEntity(), stack);
            }
            if (0 < ToolStack.from(stack).getModifierLevel(DreamtinkerModifiers.Ids.malum_tyrving))
                ((TyrvingItem) ItemRegistry.TYRVING.get()).hurtEvent(event, player, event.getEntity(), stack);
            if (0 < ToolStack.from(stack).getModifierLevel(DreamtinkerModifiers.Ids.malum_world_of_weight))
                ((WeightOfWorldsItem) ItemRegistry.WEIGHT_OF_WORLDS.get()).hurtEvent(event, player, event.getEntity(), stack);
            if (0 < ToolStack.from(stack).getModifierLevel(DreamtinkerModifiers.Ids.malum_edge_of_deliverance))
                ((EdgeOfDeliveranceItem) ItemRegistry.EDGE_OF_DELIVERANCE.get()).hurtEvent(event, player, event.getEntity(), stack);
        }
    }

    public static void MalumLivingDeathEvent(LivingDeathEvent event) {
        DamageSource dmg = event.getSource();
        if (null != dmg.getEntity() && dmg.getEntity() instanceof Player player){
            ItemStack stack = player.getMainHandItem();
            if (0 < ToolStack.from(stack).getModifierLevel(DreamtinkerModifiers.Ids.malum_world_of_weight))
                ((WeightOfWorldsItem) ItemRegistry.WEIGHT_OF_WORLDS.get()).killEvent(event, player, event.getEntity(), stack);
        }
    }
}
