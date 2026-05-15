package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerEffects;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;

import java.util.List;

import static org.dreamtinker.dreamtinker.utils.LootHelper.LootTableItemScanner.tryExtractRareLoot;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class SilverNameBeeDrop {

    @SubscribeEvent
    public static void onSilverNameBeeDrops(LivingDropsEvent event) {
        LivingEntity victim = event.getEntity();
        Level level = victim.level();
        DamageSource source = event.getSource();

        // 仅在服务端执行
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel))
            return;

        Entity attacker = source.getEntity();  // 尝试先获取伤害归属者

        // 如果攻击者为空，则尝试从弹射物中还原
        if (attacker == null && source.getDirectEntity() instanceof Projectile projectile)
            attacker = projectile.getOwner();  // 可为玩家、骷髅、猪灵等


        // 如果攻击者是生物，则可以检查药水效果
        if (!(attacker instanceof LivingEntity livingAttacker &&
              (livingAttacker.hasEffect(DreamtinkerEffects.SilverNameBee.get()) || DTModifierCheck.ModifierInHand(livingAttacker,
                                                                                                                  DreamtinkerModifiers.Ids.silver_name_bee))))
            return;
        List<ItemStack> forcedStacks = tryExtractRareLoot(serverLevel, victim, 0.35f, event.getLootingLevel());
        for (ItemStack stack : forcedStacks) {
            if (stack.isEmpty())
                continue;

            event.getDrops().add(new ItemEntity(
                    level,
                    victim.getX(),
                    victim.getY(),
                    victim.getZ(),
                    stack
            ));
        }
    }

}
