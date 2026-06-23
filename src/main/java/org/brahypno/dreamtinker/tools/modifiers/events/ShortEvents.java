package org.brahypno.dreamtinker.tools.modifiers.events;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerDamageTypes;
import org.brahypno.dreamtinker.common.DreamtinkerEffects;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.dreamtinker.tools.items.TNTArrow;
import org.brahypno.dreamtinker.utils.DTMessages;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.homunculusGiftDiscount;
import static org.brahypno.dreamtinker.tools.modifiers.traits.Combat.SignalAxe.TAG_RIGHT_TIME;
import static org.brahypno.esotericismtinker.utils.ETModifierCheck.ModifierInHand;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class ShortEvents {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void ExplosionEvent(ExplosionEvent.Detonate event) {
        if (event.isCanceled())
            return;
        Explosion exp = event.getExplosion();
        if (exp.getDamageSource().is(DreamtinkerDamageTypes.force_to_explosion) && null != exp.getDamageSource().getDirectEntity() &&
            exp.getDamageSource().getDirectEntity() instanceof TNTArrow.TNTArrowEntity){
            event.getAffectedEntities().removeIf(Entity::isAlive);
        }
        if (null != exp.getDamageSource().getEntity())
            event.getAffectedEntities()
                 .removeIf(entity -> entity instanceof LivingEntity victim && victim.is(exp.getDamageSource().getEntity()) &&
                                     ModifierInHand(victim, DreamtinkerModifiers.ewige_widerkunft.getId()));

    }

    @SubscribeEvent
    public static void LivingVisibilityEvent(LivingEvent.LivingVisibilityEvent event) {
        if (event.isCanceled())
            return;
        int multi = revealedLevel(event.getEntity(), 2);
        if (0 < multi)
            event.modifyVisibility(Mth.clamp(0.5 * multi + 1.5, 1.0, 4.0));
        int hidden_multi = ETModifierCheck.getEntityModifierNum(event.getEntity(), DreamtinkerModifiers.Ids.hidden_shape);
        if (0 < hidden_multi)
            event.modifyVisibility(0.25 + 0.75 * Math.pow(0.60, hidden_multi));
    }

    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Mob mob))
            return;
        if (event.isCanceled())
            return;

        if (mob.hasEffect(DreamtinkerEffects.RealDarkness.get())){
            if (event.getNewTarget() != null){
                event.setCanceled(true);
                mob.setTarget(null);
            }
        }
        boolean is_otto = null != event.getNewTarget() && 0 < revealedLevel(event.getNewTarget(), 1);
        if (!is_otto){
            LivingEntity best = findBestRevealedTarget(mob);
            if (best == null)
                return;

            if (best.equals(event.getNewTarget()))
                return;
            event.setNewTarget(best);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void ShieldBlockEventEvent(ShieldBlockEvent event) {
        LivingEntity blocker = event.getEntity();
        if (event.isCanceled() || blocker.level().isClientSide)
            return;
        DamageSource source = event.getDamageSource();
        float damage = event.getBlockedDamage();
        float originalDamage = event.getOriginalBlockedDamage();
        if (0 < damage && source.getEntity() instanceof LivingEntity attacker){
            ItemStack activeStack = blocker.getUseItem();
            if (!activeStack.isEmpty() && activeStack.is(TinkerTags.Items.MODIFIABLE)){//Block amount already handled
                int sweet = ModifierUtil.getModifierLevel(activeStack, DreamtinkerModifiers.Ids.sweet_death);
                if (0 < sweet && (/*originalDamage <= damage ||*/ activeStack.getUseDuration() - blocker.getUseItemRemainingTicks() <= 20 * 3 * sweet)){
                    // 伤害被完全格挡，或格挡持续时间不足2秒，则触发反伤
                    attacker.hurt(DreamtinkerDamageTypes.source(attacker.level().registryAccess(), DamageTypes.INDIRECT_MAGIC, null, blocker),
                                  damage * (0.25F * sweet));
                }
                int kiss = ModifierUtil.getModifierLevel(activeStack, DreamtinkerModifiers.Ids.last_kiss);
                if (0 < kiss && (activeStack.getUseDuration() - blocker.getUseItemRemainingTicks() <= 20 * 3 * kiss)){
                    AtomicInteger i = new AtomicInteger();
                    blocker.getActiveEffects().removeIf(effect -> effect.getEffect().getCategory() == MobEffectCategory.HARMFUL &&
                                                                  effect.isCurativeItem(new ItemStack(Items.MILK_BUCKET)) && i.getAndIncrement() < 2);
                    blocker.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 2 * kiss, kiss - 1, false, false));
                }
            }
        }

    }

    @SubscribeEvent
    public static void onMerchantMenuOpened(PlayerContainerEvent.Open e) {
        if (!(e.getContainer() instanceof MerchantMenu menu))
            return;

        // 你的判定：比如玩家/村民有某标签、状态或物品
        int homunculusGift = ETModifierCheck.getEntityModifierNum(e.getEntity(), DreamtinkerModifiers.Ids.homunculusGift);
        if (homunculusGift <= 0)
            return;

        // 获取并修改当次会话的报价列表
        MerchantOffers offers = menu.getOffers();

        for (MerchantOffer o : offers) {
            int dec = Mth.floor(o.getBaseCostA().getCount() * homunculusGift * homunculusGiftDiscount.get());
            o.addToSpecialPriceDiff(-dec);
        }

        // 通知客户端刷新（必要时）
        menu.broadcastChanges();
    }

    @SubscribeEvent
    static void onCritical(CriticalHitEvent event) {
        if (event.getResult() != Event.Result.DENY){
            LivingEntity living = event.getEntity();
            if (0 < ModifierUtil.getPersistentInt(living.getItemBySlot(EquipmentSlot.MAINHAND), TAG_RIGHT_TIME, 0)){
                if (event.getResult() != Event.Result.ALLOW){
                    DTMessages.clientChat(Component.translatable("modifier.dreamtinker.signal_axe.critical")
                                                   .withStyle(DreamtinkerModifiers.signal_axe.get().getDisplayName().getStyle()), false);
                    event.setResult(Event.Result.ALLOW);
                }
                event.setDamageModifier(event.getDamageModifier() + 0.4f);
            }
        }
    }

    private static double getDetectionRadius(Mob mob) {
        // “发现/追踪范围”最贴近 follow range
        return mob.getAttributeValue(Attributes.FOLLOW_RANGE);
    }

    private static boolean preferDistanceFirst(Mob mob) {
        // 近战优先：没有远程接口就按距离优先
        return !(mob instanceof RangedAttackMob);
    }

    private static LivingEntity findBestRevealedTarget(Mob mob) {
        double radius = getDetectionRadius(mob);
        if (!(radius > 0))
            return null;

        AABB box = mob.getBoundingBox().inflate(radius);

        List<LivingEntity> candidates = mob.level().getEntitiesOfClass(
                LivingEntity.class,
                box,
                e -> e.isAlive()
                     && e != mob
                     && 0 < revealedLevel(e, 1)
                     && mob.canAttack(e)
                     && mob.hasLineOfSight(e) // 仍建议保留：避免隔墙点名
        );

        if (candidates.isEmpty())
            return null;

        boolean distanceFirst = preferDistanceFirst(mob);

        LivingEntity best = null;
        int bestAmp = Integer.MIN_VALUE;
        double bestDist2 = Double.MAX_VALUE;

        for (LivingEntity e : candidates) {
            int amp = revealedLevel(e, 1);
            double dist2 = mob.distanceToSqr(e);

            if (best == null){
                best = e;
                bestAmp = amp;
                bestDist2 = dist2;
                continue;
            }

            if (distanceFirst){
                // 近战：距离优先，同距离再比等级
                if (dist2 < bestDist2 || (dist2 == bestDist2 && amp > bestAmp)){
                    best = e;
                    bestAmp = amp;
                    bestDist2 = dist2;
                }
            }else {
                // 远程：等级优先，同等级再比距离
                if (amp > bestAmp || (amp == bestAmp && dist2 < bestDist2)){
                    best = e;
                    bestAmp = amp;
                    bestDist2 = dist2;
                }
            }
        }

        return best;
    }

    private static int revealedLevel(LivingEntity entity, int whimsyWeight) {
        return ETModifierCheck.getEntityModifierNum(entity, DreamtinkerModifiers.Ids.golden_face)
               + whimsyWeight * ETModifierCheck.getEntityModifierNum(entity, DreamtinkerModifiers.Ids.whimsy_face);
    }
}

