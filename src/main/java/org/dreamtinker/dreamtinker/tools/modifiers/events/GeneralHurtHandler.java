package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
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
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import static net.minecraft.tags.DamageTypeTags.IS_PROJECTILE;
import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.FragileDodge;
import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.homunculusLifeCurseMaxEffectLevel;
import static org.dreamtinker.dreamtinker.tools.modifiers.tools.underPlate.weapon_transformation.valueExpSoftCap;
import static org.dreamtinker.dreamtinker.tools.modifiers.traits.armors.knockArts.TAG_KNOCK;
import static org.dreamtinker.dreamtinker.utils.DTModifierCheck.ModifierInHand;
import static org.dreamtinker.dreamtinker.utils.DTModifierCheck.getPossibleToolWithModifier;


@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class GeneralHurtHandler {
    static boolean why_i_cry_triggered = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void LivingHurtEvent(LivingHurtEvent event) {
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

        //System.out.println(dmg + "" + damageAmount);
        //CANCEL DAMAGE--need victim but not offender
        int fragileButBright = DTModifierCheck.getEntityModifierNum(victim, DreamtinkerModifiers.Ids.FragileButBright);
        if (0 < fragileButBright && rds.nextFloat() < FragileDodge.get() * fragileButBright){
            event.setAmount(0);
            event.setCanceled(true);
            return;
        }
        if (null != getPossibleToolWithModifier(victim, DreamtinkerModifiers.as_one.getId()))
            if (victim.getMaxHealth() < event.getAmount()){
                victim.setAbsorptionAmount(Math.min(victim.getAbsorptionAmount() + event.getAmount(), Integer.MAX_VALUE));
                event.setAmount(0);
                event.setCanceled(true);
                return;
            }
        if (event.getSource().is(DamageTypeTags.IS_EXPLOSION)){
            if (ModifierInHand(event.getEntity(), DreamtinkerModifiers.ewige_widerkunft.getId()))
                if (event.getEntity().getHealth() <= event.getAmount()){
                    event.setCanceled(true);
                    return;
                }
        }
        if (null == dmgEntity){
            if (DTModifierCheck.ModifierInBody(victim, DreamtinkerModifiers.Ids.requiem)){
                event.setAmount(0);
                event.setCanceled(true);
                return;
            }
        }
        
        if (dmgEntity instanceof LivingEntity offender){
            int requiem = DTModifierCheck.getEntityBodyModifierNum(offender, DreamtinkerModifiers.Ids.requiem);
            if (0 < requiem){
                float reduced = victim.getActiveEffects().stream()
                                      .filter(e -> e.getEffect().getCategory() == MobEffectCategory.HARMFUL).count() / 4.0f * requiem;
                reduced = event.getAmount() - reduced;
                if (reduced <= 0){
                    event.setAmount(0);
                    event.setCanceled(true);
                    return;
                }else
                    event.setAmount(reduced);
            }

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
                int lunarAttractive = DTModifierCheck.getMainhandModifierLevel(offender, DreamtinkerModifiers.Ids.lunarRejection);
                if (0 < lunarAttractive)
                    event.setAmount(event.getAmount() + (TinkerPredicate.AIRBORNE.matches(victim) ? 2.0f : -2.0f));
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
            float del = DTModifierCheck.getPersistentTagValue(offender, DreamtinkerModifiers.knockArts.getId(), TAG_KNOCK);
            if (0 < del)
                event.setAmount(event.getAmount() + del);

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
