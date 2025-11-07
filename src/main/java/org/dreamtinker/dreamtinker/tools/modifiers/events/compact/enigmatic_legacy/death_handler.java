package org.dreamtinker.dreamtinker.tools.modifiers.events.compact.enigmatic_legacy;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.EldritchPan;
import com.aizistral.enigmaticlegacy.items.TheInfinitum;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import static org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.eldritch_pan.TAG_PAN;

public class death_handler {
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || event.isCanceled())
            return;
        if (entity instanceof Player player && SuperpositionHandler.isTheWorthyOne(player))
            if (3 <= DTModifierCheck.getMainhandModifierLevel(player, DreamtinkerModifiers.weapon_books.getId()))
                if (Math.random() <= TheInfinitum.undeadProbability.getValue().asMultiplier(false)){
                    event.setCanceled(true);
                    player.setHealth(1);
                }
        if (event.getSource().getDirectEntity() instanceof ServerPlayer attacker){
            ItemStack weapon = attacker.getMainHandItem();

            if (0 < DTModifierCheck.getMainhandModifierLevel(attacker, DreamtinkerModifiers.eldritch_pan.getId())){
                ResourceLocation killedType = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType());

                if (EldritchPan.addKillIfNotPresent(weapon, killedType)){
                    attacker.sendSystemMessage(Component.translatable("message.enigmaticlegacy.eldritch_pan_buff")
                                                        .withStyle(ChatFormatting.GOLD));
                    ToolStack toolstack = ToolStack.from(weapon);
                    ModDataNBT nbt = toolstack.getPersistentData();
                    nbt.putInt(TAG_PAN, EldritchPan.getKillCount(weapon));
                    toolstack.updateStack(weapon);
                }
            }
        }
    }
}
