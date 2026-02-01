package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

import static org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat.SignalAxe.TAG_RIGHT_TIME;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class ModifierEvents {
    @SubscribeEvent
    static void onCritical(CriticalHitEvent event) {
        if (event.getResult() != Event.Result.DENY){
            LivingEntity living = event.getEntity();
            if (0 < ModifierUtil.getPersistentInt(living.getItemBySlot(EquipmentSlot.MAINHAND), TAG_RIGHT_TIME, 0)){
                if (event.getResult() != Event.Result.ALLOW){
                    living.sendSystemMessage(Component.translatable("modifier.dreamtinker.signal_axe.critical")
                                                      .withStyle(DreamtinkerModifiers.signal_axe.get().getDisplayName().getStyle()));
                    event.setResult(Event.Result.ALLOW);
                }
                event.setDamageModifier(event.getDamageModifier() + 0.4f);
            }
        }
    }
}
