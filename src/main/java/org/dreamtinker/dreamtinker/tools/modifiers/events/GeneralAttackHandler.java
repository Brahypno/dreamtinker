package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class GeneralAttackHandler {
    static boolean damage_source_transmission = false;
    private static final String TAG_DamageSourceTransmission = Dreamtinker.getLocation("damage_source_transmission").toString();
    private static final String TAG_extra_hit = Dreamtinker.getLocation("extra_hit").toString();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void LivingAttackEvent(LivingAttackEvent event) {
        DamageSource dmg = event.getSource();
        Entity dmgEntity = dmg.getEntity();
        float damageAmount = event.getAmount();
        LivingEntity victim = event.getEntity();
        if (0 == damageAmount || event.isCanceled())
            return;
        Level world = victim.level();
        if (world.isClientSide())
            return;
        CompoundTag data = victim.getPersistentData();
        RegistryAccess registryAccess = world.registryAccess();
        RandomSource rds = world.random;

        boolean Not_Tran = !data.contains(TAG_DamageSourceTransmission) || data.getLong(TAG_DamageSourceTransmission) < world.getGameTime();
        if (!damage_source_transmission && Not_Tran){
            damage_source_transmission = true;
            boolean transformed = false;

            int ophelia = DTModifierCheck.getEntityBodyModifierNum(victim, DreamtinkerModifiers.Ids.ophelia);
            if (0 < ophelia && !dmg.is(DreamtinkerDamageTypes.NULL_VOID)){
                float amount = damageAmount * 3;
                event.setCanceled(true);
                int inv = victim.invulnerableTime;
                for (int i = 0; i < 2 * ophelia + 1 && victim.isAlive(); i++) {
                    DamageSource source = DreamtinkerDamageTypes.randomSourceNotSame(registryAccess, dmg, rds);
                    victim.invulnerableTime = 0;
                    victim.hurt(source, amount / (2.0f * ophelia + 1.0f));
                }
                victim.invulnerableTime = inv;
                transformed = true;
            }
            damage_source_transmission = false;
            if (transformed){
                data.putLong(TAG_DamageSourceTransmission, world.getGameTime());
                return;//To avoid below buff multiple times
            }
        }
    }

}
