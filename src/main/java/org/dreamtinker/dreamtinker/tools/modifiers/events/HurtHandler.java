package org.dreamtinker.dreamtinker.tools.modifiers.events;

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
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import static net.minecraft.tags.DamageTypeTags.IS_PROJECTILE;
import static org.dreamtinker.dreamtinker.tools.modifiers.tools.underPlate.WeaponTransformation.valueExpSoftCap;


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
                int drink_magic = DTModifierCheck.getEntityModifierNum(offender, DreamtinkerModifiers.Ids.drinker_magic);
                if (0 < drink_magic){
                    damageAmount *= (1 + drink_magic * 0.05f);
                    offender.heal(damageAmount * drink_magic * 0.05f);
                }
            }
            event.setAmount(damageAmount);
        }
    }
}
