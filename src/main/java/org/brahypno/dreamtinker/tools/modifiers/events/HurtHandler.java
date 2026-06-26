package org.brahypno.dreamtinker.tools.modifiers.events;

import net.minecraft.world.damagesource.DamageSource;
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
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import static net.minecraft.tags.DamageTypeTags.IS_PROJECTILE;
import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.homunculusLifeCurseMaxEffectLevel;
import static org.brahypno.dreamtinker.tools.modifiers.tools.underPlate.WeaponTransformation.valueExpSoftCap;


@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class HurtHandler {

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
        Entity direct = dmg.getDirectEntity();

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
            }
            if (dmg.is(TinkerTags.DamageTypes.MAGIC_PROTECTION)){
                int drink_magic = ETModifierCheck.getEntityModifierNum(offender, DreamtinkerModifiers.Ids.drinker_magic);
                if (0 < drink_magic){
                    damageAmount *= (1 + drink_magic * 0.05f);
                    offender.heal(damageAmount * drink_magic * 0.05f);
                }
            }
            int homunculus_life_curse = ETModifierCheck.getEntityModifierNum(offender, DreamtinkerModifiers.Ids.homunculus_life_curse);
            if (0 < homunculus_life_curse){
                damageAmount = homunculusLifeDamage(offender, damageAmount, homunculus_life_curse);
            }
            event.setAmount(damageAmount);
        }
    }

    private static float homunculusLifeDamage(LivingEntity attacker, float amount, int level) {
        float multiplier = (1 - attacker.getHealth() / attacker.getMaxHealth())
                           * Math.min(homunculusLifeCurseMaxEffectLevel.get() + 1, level + 1);
        return amount * multiplier;
    }
}
