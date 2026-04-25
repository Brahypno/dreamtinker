package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class GeneralDamageHandler {
    @SubscribeEvent
    static void SecondaryNoneEquipmentLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        // give modifiers a chance to respond to damage happening
        float amount = event.getAmount();
        EquipmentContext context = new EquipmentContext(entity);
        if (entity instanceof Player player){
            for (ItemStack stack : player.getInventory().items) {
                if (null == stack || stack.isEmpty() || !stack.is(TinkerTags.Items.ARMOR) || stack.equals(player.getMainHandItem()))
                    continue;
                if (stack.getItem() instanceof IModifiable){
                    IToolStackView toolStackView = ToolStack.from(stack);
                    amount = DTModifierCheck.modifyDamageTakenInventory(ModifierHooks.MODIFY_DAMAGE, context, source, amount,
                                                                        OnAttackedModifierHook.isDirectDamage(source), DTModifierCheck.toSlot(stack),
                                                                        toolStackView);
                    if (amount <= 0)
                        break;
                }
            }
            event.setAmount(amount);
            if (amount <= 0){
                event.setCanceled(true);
            }
        }
    }
}
