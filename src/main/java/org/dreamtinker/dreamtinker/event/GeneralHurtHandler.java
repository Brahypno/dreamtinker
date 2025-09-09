package org.dreamtinker.dreamtinker.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifers;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.UnderPlateBoostMutiply;

public class GeneralHurtHandler {
    public static void LivingHurtEvent(LivingHurtEvent event) {
        DamageSource dmg = event.getSource();
        if (null != dmg.getEntity() && dmg.getDirectEntity() instanceof Projectile)
            for (ItemStack itemStack : dmg.getEntity().getArmorSlots()) {
                if (itemStack.is(Tags.Items.ARMORS_LEGGINGS) && itemStack.is(TinkerTags.Items.LEGGINGS)){
                    ToolStack toolStack = ToolStack.from(itemStack);
                    if (!toolStack.isBroken())
                        if (0 < ModifierUtil.getModifierLevel(itemStack, DreamtinkerModifers.weapon_transformation.getId())){
                            float armor = toolStack.getStats().get(ToolStats.ARMOR);
                            float toughness = toolStack.getStats().get(ToolStats.ARMOR_TOUGHNESS);
                            event.setAmount(event.getAmount() * (1 + armor * toughness * UnderPlateBoostMutiply.get().floatValue()));
                        }
                }
            }
    }
}
