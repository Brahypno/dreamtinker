package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import static net.minecraft.tags.DamageTypeTags.*;
import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.FragileDodge;
import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.homunculusLifeCurseMaxEffectLevel;
import static org.dreamtinker.dreamtinker.tools.modifiers.tools.underPlate.weapon_transformation.valueExpSoftCap;
import static org.dreamtinker.dreamtinker.utils.DTModifierCheck.ModifierInHand;
import static org.dreamtinker.dreamtinker.utils.DTModifierCheck.getPossibleToolWithModifier;


@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class GeneralHurtHandler {
    static boolean why_i_cry_triggered = false;
    static boolean damage_source_transmission = false;
    private static final String TAG_DamageSourceTransmission = Dreamtinker.getLocation("damage_source_transmission").toString();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void LivingHurtEvent(LivingHurtEvent event) {
        DamageSource dmg = event.getSource();
        Entity dmgEntity = event.getEntity();
        float damageAmount = event.getAmount();
        LivingEntity victim = event.getEntity();
        if (0 == damageAmount || event.isCanceled())
            return;
        Level world = victim.level();
        if (world.isClientSide() || event.isCanceled())
            return;
        CompoundTag data = victim.getPersistentData();
        RegistryAccess registryAccess = world.registryAccess();
        RandomSource rds = world.random;

        //System.out.println(dmg + "" + damageAmount);
        //CANCEL DAMAGE--need victim but not offender
        int fragileButBright = DTModifierCheck.getEntityModifierNum(victim, DreamtinkerModifiers.Ids.FragileButBright);
        if (0 < fragileButBright && rds.nextFloat() < FragileDodge.get() * fragileButBright){
            event.setAmount(0);
            event.setCanceled(true);
        }
        if (null != getPossibleToolWithModifier(victim, DreamtinkerModifiers.as_one.getId()))
            if (victim.getMaxHealth() < event.getAmount()){
                victim.setAbsorptionAmount(victim.getAbsorptionAmount() + event.getAmount());
                event.setAmount(0);
                event.setCanceled(true);
            }
        if (event.getSource().is(DamageTypeTags.IS_EXPLOSION))
            if (ModifierInHand(event.getEntity(), DreamtinkerModifiers.ewige_widerkunft.getId()))
                if (event.getEntity().getHealth() <= event.getAmount())
                    event.setCanceled(true);


        boolean Not_Tran = !data.contains(TAG_DamageSourceTransmission) || data.getLong(TAG_DamageSourceTransmission) < world.getGameTime();

        if (!damage_source_transmission && Not_Tran){
            damage_source_transmission = true;
            boolean transformed = false;
            if (dmgEntity instanceof LivingEntity offender)
                if (DTModifierCheck.haveModifierIn(offender, DreamtinkerModifiers.despair_wind.getId()) ||
                    2 < DTModifierCheck.getMainhandModifierLevel(offender, DreamtinkerModifiers.Ids.icy_memory))
                    if (!(dmg.is(BYPASSES_ARMOR) && dmg.is(BYPASSES_SHIELD) && dmg.is(BYPASSES_INVULNERABILITY) && dmg.is(BYPASSES_COOLDOWN) &&
                          dmg.is(BYPASSES_EFFECTS) &&
                          dmg.is(BYPASSES_RESISTANCE) && dmg.is(BYPASSES_ENCHANTMENTS))){

                        event.setCanceled(true);
                        transformed = true;
                        victim.hurt(DreamtinkerDamageTypes.source(registryAccess, DreamtinkerDamageTypes.NULL_VOID, dmg), damageAmount);
                    }

            int ophelia = DTModifierCheck.getEntityBodyModifierNum(victim, DreamtinkerModifiers.Ids.ophelia);
            if (0 < ophelia && !dmg.is(DreamtinkerDamageTypes.NULL_VOID)){
                float amount = damageAmount * 3;
                event.setCanceled(true);
                int inv = victim.invulnerableTime;
                for (int i = 0; i < 2 * ophelia + 1; i++) {
                    DamageSource source = DreamtinkerDamageTypes.randomSourceNotSame(registryAccess, dmg, rds);
                    victim.invulnerableTime = 0;
                    if (victim.isAlive())
                        victim.hurt(source, amount / (2 * ophelia + 1));
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

        if (dmgEntity instanceof LivingEntity offender){
            if (dmg.getDirectEntity() instanceof Projectile || dmg.is(IS_PROJECTILE)){
                ItemStack leg = offender.getItemBySlot(EquipmentSlot.LEGS);
                if (leg.is(TinkerTags.Items.LEGGINGS)){
                    ToolStack toolStack = ToolStack.from(leg);
                    if (!toolStack.isBroken())
                        if (0 < ModifierUtil.getModifierLevel(leg, DreamtinkerModifiers.weapon_transformation.getId())){
                            float armor = toolStack.getStats().get(ToolStats.ARMOR);
                            float toughness = toolStack.getStats().get(ToolStats.ARMOR_TOUGHNESS);
                            event.setAmount(damageAmount * (1 + valueExpSoftCap(armor, toughness)));
                        }
                }
            }

            //DEAL DAMAGE
            if (0 < DTModifierCheck.getMainhandModifierLevel(offender, DreamtinkerModifiers.Ids.why_i_cry) && !why_i_cry_triggered){
                DamageSource source = DreamtinkerDamageTypes.source(registryAccess, DamageTypes.FELL_OUT_OF_WORLD, dmg);
                why_i_cry_triggered = true;
                victim.hurt(source, damageAmount * .1f);
                victim.invulnerableTime = 0;
                if (rds.nextFloat() < 0.1)
                    offender.hurt(source, damageAmount * .1f);
                why_i_cry_triggered = false;
            }

            int sand_level = DTModifierCheck.getMainhandModifierLevel(offender, DreamtinkerModifiers.Ids.AsSand);
            if (0 < sand_level)
                applyWearToTarget(victim, (int) (damageAmount / 10 * sand_level));

            int homunculusLifeCurse = DTModifierCheck.getEntityModifierNum(offender, DreamtinkerModifiers.Ids.homunculusLifeCurse);
            if (0 < homunculusLifeCurse)
                event.setAmount(damageAmount * (1 - offender.getHealth() / offender.getMaxHealth()) *
                                Math.min(homunculusLifeCurseMaxEffectLevel.get() + 1, homunculusLifeCurse + 1));
        }

    }


    private static void applyWearToTarget(LivingEntity target, int level) {
        if (target.isDeadOrDying())
            return;

        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        }) {
            damageStackInSlot(target, slot, level);
        }
        damageStackInSlot(target, EquipmentSlot.MAINHAND, level);
        damageStackInSlot(target, EquipmentSlot.OFFHAND, level);
    }

    private static void damageStackInSlot(LivingEntity target, EquipmentSlot slot, int amount) {
        if (amount <= 0)
            return;
        ItemStack stack = target.getItemBySlot(slot);
        if (stack.isEmpty() || !stack.isDamageableItem())
            return;
        stack.hurtAndBreak(amount, target, e -> e.broadcastBreakEvent(slot));
    }
}
