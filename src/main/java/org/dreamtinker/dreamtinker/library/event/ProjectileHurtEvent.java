package org.dreamtinker.dreamtinker.library.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.DreamtinkerHook;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class ProjectileHurtEvent {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onProjectileHurt(LivingHurtEvent event) {
        if (event.isCanceled() || event.getEntity().level().isClientSide){
            return;
        }

        DamageSource source = event.getSource();
        Entity direct = source.getDirectEntity();
        if (!(direct instanceof Projectile projectile)){
            return;
        }

        ModifierNBT modifiers = EntityModifierCapability.getOrEmpty(projectile);
        if (modifiers.isEmpty()){
            return;
        }

        ModDataNBT persistentData = PersistentDataCapability.getOrWarn(projectile);
        Entity owner = source.getEntity();
        LivingEntity attacker = owner instanceof LivingEntity living ? living : null;
        LivingEntity target = event.getEntity();
        float amount = event.getAmount();

        for (ModifierEntry entry : modifiers.getModifiers()) {
            amount = entry.getHook(DreamtinkerHook.PROJECTILE_HURT)
                          .modifyProjectileHurt(modifiers, persistentData, entry, projectile, source, attacker, target, amount);
            if (amount <= 0){
                event.setCanceled(true);
                return;
            }
        }

        event.setAmount(amount);
    }
}
