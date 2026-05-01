package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat.GoliathDamage;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import static net.minecraft.tags.DamageTypeTags.BYPASSES_ENCHANTMENTS;
import static net.minecraft.tags.DamageTypeTags.IS_PROJECTILE;
import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.FirthMark;
import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.homunculusLifeCurseMaxEffectLevel;
import static org.dreamtinker.dreamtinker.tools.modifiers.tools.underPlate.WeaponTransformation.valueExpSoftCap;
import static org.dreamtinker.dreamtinker.tools.modifiers.traits.armors.knockArts.TAG_KNOCK;
import static org.dreamtinker.dreamtinker.tools.modifiers.traits.material.ruin_wheel.DoomTrack.proofByResistanceMultiplier;


@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class GeneralHurtHandler {
    private static final int allowed_extra_times = 1;
    private static final ThreadLocal<Integer> cry_extra_attack_depth = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Integer> arcane_extra_attack_depth = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Integer> ruin_extra_attack_depth = ThreadLocal.withInitial(() -> 0);

    @SubscribeEvent(priority = EventPriority.LOW)
    static void SecondaryNoneEquipmentHurtHandler(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        EquipmentContext context = new EquipmentContext(entity);
        float originalDamage = event.getAmount();

        // for our own armor, we have boosts from modifiers to consider
        if (entity instanceof Player player){
            for (ItemStack stack : player.getInventory().items) {
                if (null == stack || stack.isEmpty() || !stack.is(TinkerTags.Items.ARMOR) || stack.equals(player.getMainHandItem()))
                    continue;
                if (stack.getItem() instanceof IModifiable){
                    IToolStackView toolStackView = ToolStack.from(stack);
                    originalDamage = DTModifierCheck.modifyDamageTakenInventory(ModifierHooks.MODIFY_HURT, context, source, originalDamage,
                                                                                OnAttackedModifierHook.isDirectDamage(source), DTModifierCheck.toSlot(stack),
                                                                                toolStackView);
                    if (originalDamage <= 0)
                        break;
                }
            }
            event.setAmount(originalDamage);
            if (originalDamage <= 0){
                event.setCanceled(true);
            }
        }
    }


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
        Entity direct = dmg.getDirectEntity();
        ModifierNBT modifiers = direct instanceof Projectile ? EntityModifierCapability.getOrEmpty(direct) : null;

        if (dmgEntity instanceof LivingEntity offender){
            if (direct instanceof Projectile || dmg.is(IS_PROJECTILE)){
                ItemStack leg = offender.getItemBySlot(EquipmentSlot.LEGS);
                if (leg.is(TinkerTags.Items.LEGGINGS)){
                    ToolStack toolStack = ToolStack.from(leg);
                    if (!toolStack.isBroken())
                        if (0 < ModifierUtil.getModifierLevel(leg, DreamtinkerModifiers.weapon_transformation.getId())){
                            float armor = toolStack.getStats().get(ToolStats.ARMOR);
                            float toughness = toolStack.getStats().get(ToolStats.ARMOR_TOUGHNESS);
                            damageAmount *= (1 + valueExpSoftCap(armor, toughness));
                        }
                }

                int goliath = Math.max(DTModifierCheck.getMainhandModifierLevel(offender, DreamtinkerModifiers.goliath_damage.getId()),
                                       null != modifiers ? modifiers.getLevel(DreamtinkerModifiers.goliath_damage.getId()) : 0);
                if (0 < goliath)
                    damageAmount *= GoliathDamage.goliathPercentage(offender, victim) * goliath;
                int fifth_mark = Math.max(DTModifierCheck.getMainhandModifierLevel(offender, DreamtinkerModifiers.Ids.four_warning),
                                          null != modifiers ? modifiers.getLevel(DreamtinkerModifiers.Ids.four_warning) : 0);
                if (0 < fifth_mark){
                    final String tag = "dt_fifth_mark";
                    int times = (data.getInt(tag) + 1) % 5;
                    if (0 != times){
                        damageAmount *= (1 - FirthMark.get().floatValue());
                    }else {
                        damageAmount *= (1 + FirthMark.get().floatValue() * 5);
                    }
                    data.putInt(tag, times);
                }

                int doom = Math.max(DTModifierCheck.getMainhandModifierLevel(offender, DreamtinkerModifiers.doom_track.getId()),
                                    null != modifiers ? modifiers.getLevel(DreamtinkerModifiers.doom_track.getId()) : 0);
                if (0 < doom){
                    int depth = ruin_extra_attack_depth.get();
                    if (depth < allowed_extra_times){
                        try {
                            ruin_extra_attack_depth.set(depth + 1);
                            DamageSource ruin_dmg =
                                    DreamtinkerDamageTypes.source(victim.level().registryAccess(), DreamtinkerDamageTypes.ruin_wheel, dmg);
                            float damage = damageAmount *
                                           proofByResistanceMultiplier(offender, victim, ruin_dmg, doom, true);
                            victim.hurt(ruin_dmg, damage);
                        }
                        finally {
                            ruin_extra_attack_depth.set(depth);
                        }
                    }
                }
            }

            //DEAL DAMAGE
            int drown_level = DTModifierCheck.getMainhandModifierLevel(offender, DreamtinkerModifiers.Ids.why_i_cry);
            if (0 < drown_level){
                int depth = cry_extra_attack_depth.get();
                if (depth < allowed_extra_times){
                    try {
                        DamageSource source = DreamtinkerDamageTypes.source(registryAccess, DreamtinkerDamageTypes.NULL_VOID, dmg);
                        float extra = damageAmount * .05f * drown_level;
                        cry_extra_attack_depth.set(depth + 1);
                        if (0.1f <= extra){
                            victim.hurt(source, extra);
                            if (rds.nextFloat() < 0.1)
                                offender.hurt(source, extra);
                        }
                    }
                    finally {
                        cry_extra_attack_depth.set(depth);
                    }
                }
            }
            float del = DTModifierCheck.getPersistentTagValue(offender, DreamtinkerModifiers.knockArts.getId(), TAG_KNOCK);
            if (0 < del)
                damageAmount += del;

            int sand_level = DTModifierCheck.getMainhandModifierLevel(offender, DreamtinkerModifiers.Ids.AsSand);
            if (0 < sand_level)
                applyWearToTarget(victim, (int) (damageAmount / 10 * sand_level));

            int homunculusLifeCurse = DTModifierCheck.getEntityModifierNum(offender, DreamtinkerModifiers.Ids.homunculusLifeCurse);
            if (0 < homunculusLifeCurse)
                damageAmount *= (1 - offender.getHealth() / offender.getMaxHealth()) *
                                Math.min(homunculusLifeCurseMaxEffectLevel.get() + 1, homunculusLifeCurse + 1);

            if (!dmg.is(BYPASSES_ENCHANTMENTS)){
                int arcane_hit_level = DTModifierCheck.getMainhandModifierLevel(offender, DreamtinkerModifiers.Ids.arcane_hit);
                int depth = arcane_extra_attack_depth.get();
                if (0 < arcane_hit_level && depth < allowed_extra_times){
                    try {
                        DamageSource source = DreamtinkerDamageTypes.source(registryAccess, DreamtinkerDamageTypes.arcane_damage, dmg);
                        float extra = damageAmount * .1f * arcane_hit_level;
                        if (0.1f <= extra){
                            victim.hurt(source, extra);
                            damageAmount = Math.max(0f, damageAmount - extra);
                        }
                    }
                    finally {
                        arcane_extra_attack_depth.set(depth);
                    }
                }
            }
            if (dmg.is(TinkerTags.DamageTypes.MAGIC_PROTECTION)){
                int drink_magic = DTModifierCheck.getEntityModifierNum(offender, DreamtinkerModifiers.Ids.drinker_magic);
                if (0 < drink_magic){
                    damageAmount *= (1 + drink_magic * 0.05f);
                    offender.heal(damageAmount * drink_magic * 0.05f);
                }
            }
            event.setAmount(damageAmount);
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
