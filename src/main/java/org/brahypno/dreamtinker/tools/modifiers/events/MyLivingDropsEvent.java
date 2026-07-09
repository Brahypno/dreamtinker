package org.brahypno.dreamtinker.tools.modifiers.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerEffects;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;

import java.util.List;

import static org.brahypno.esotericismtinker.utils.LootHelper.LootTableItemScanner.tryExtractRareLoot;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class MyLivingDropsEvent {

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
              (livingAttacker.hasEffect(DreamtinkerEffects.SilverNameBee.get()) || ETModifierCheck.ModifierInHand(livingAttacker,
                                                                                                                  DreamtinkerModifiers.Ids.silver_name_bee))))
            return;
        List<ItemStack> forcedStacks = tryExtractRareLoot(serverLevel, victim, 0.40f, event.getLootingLevel());
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

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        DamageSource source = event.getSource();
        Entity direct = source.getDirectEntity();
        Entity attacker = source.getEntity();

        Player player = null;
        if (attacker instanceof Player p){
            player = p;
        }else if (direct instanceof Projectile projectile && projectile.getOwner() instanceof Player p){
            player = p;
        }

        if (player == null || player.level().isClientSide)
            return;
        if (ETModifierCheck.ModifierInHand(player, DreamtinkerModifiers.foundation_will.getId()))
            for (ItemEntity drop : event.getDrops()) {
                moveDropNearPlayer(drop, player);
            }
    }

    private static void moveDropNearPlayer(ItemEntity drop, Player player) {
        double angle = player.getRandom().nextDouble() * Math.PI * 2;
        double radius = 0.35 + player.getRandom().nextDouble() * 0.45;
        double x = player.getX() + Math.cos(angle) * radius;
        double z = player.getZ() + Math.sin(angle) * radius;

        drop.setPos(x, player.getY() + 0.25, z);
        drop.setDeltaMovement((player.getX() - x) * 0.05, 0.12, (player.getZ() - z) * 0.05);
        drop.setPickUpDelay(0);
    }

}
