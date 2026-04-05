package org.dreamtinker.dreamtinker.common.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.common.DreamtinkerEffects;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Dreamtinker.MODID)
public class EdictSeriesEvents {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void LivingHurtEvent(LivingHurtEvent event) {
        DamageSource dmg = event.getSource();
        Entity dmgEntity = dmg.getEntity();
        float damageAmount = event.getAmount();
        if (0 == damageAmount || event.isCanceled() || null == dmgEntity)
            return;
        Level world = dmgEntity.level();
        if (world.isClientSide())
            return;
        if (dmgEntity instanceof LivingEntity attacker && !attacker.equals(event.getEntity()) && attacker.hasEffect(DreamtinkerEffects.Ahimsa.get())){
            MobEffectInstance effect = attacker.getEffect(DreamtinkerEffects.Ahimsa.get());
            if (effect != null){
                int level = effect.getAmplifier() + 1;
                float self_damage = 1.0f + 0.5f * level;
                float damage_boost = 1.2f + 0.15f * level;
                event.setAmount(event.getAmount() * damage_boost);
                attacker.hurt(DreamtinkerDamageTypes.source(world.registryAccess(), DreamtinkerDamageTypes.edict_punishments, null, null), self_damage);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void PlayerTickEvent(TickEvent.PlayerTickEvent event) {
        net.minecraft.world.entity.player.Player player = event.player;
        if (event.isCanceled() || null == player)
            return;
        Level world = player.level();
        if (world.isClientSide() || player.isPassenger() || player.isFallFlying() || player.isSleeping() || player.isInWaterOrBubble())
            return;

        CompoundTag data = player.getPersistentData();
        if (player.hasEffect(DreamtinkerEffects.EdictOfStillness.get())){
            MobEffectInstance EdictOfStillness = player.getEffect(DreamtinkerEffects.EdictOfStillness.get());
            if (null != EdictOfStillness){
                boolean moved = player.getDeltaMovement().horizontalDistanceSqr() > 1.0E-4;

                int movingTicks = data.getInt("dreamtinker:stillness_moving_ticks");
                int cooldown = data.getInt("dreamtinker:stillness_cd");

                if (cooldown > 0){
                    data.putInt("dreamtinker:stillness_cd", cooldown - 1);
                }

                if (moved){
                    movingTicks++;
                }else {
                    movingTicks = 0;
                }

                if (cooldown <= 0 && movingTicks >= 15){

                    int level = EdictOfStillness.getAmplifier() + 1;

                    float self_damage = 0.25f + 0.25f * level;
                    player.hurt(DreamtinkerDamageTypes.source(world.registryAccess(), DreamtinkerDamageTypes.edict_punishments, null, null), self_damage);
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 + 10 * level, level - 1));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10 * level, level - 1));

                    movingTicks = 0;
                    data.putInt("dreamtinker:stillness_cd", 40);
                }

                data.putInt("dreamtinker:stillness_moving_ticks", movingTicks);
            }
        }else {
            data.remove("dreamtinker:stillness_cd");
            data.remove("dreamtinker:stillness_moving_ticks");
        }
    }

    private static final String NBT_LAST_SILENT_STEP = "dreamtinker_last_silent_step";
    private static final String TAG_ASCENT_LAST_JUMP = "dreamtinker_ascent_last_jump";

    @SubscribeEvent
    public static void onVanillaGameEvent(VanillaGameEvent event) {
        // 只在服务端处理
        if (event.getLevel().isClientSide())
            return;

        // 事件来源实体
        if (!(event.getCause() instanceof ServerPlayer player))
            return;
        if (player.isSteppingCarefully() || player.isShiftKeyDown())
            return;

        var inst = player.getEffect(DreamtinkerEffects.LawOfTheSilentStep.get());
        if (inst == null)
            return;

        GameEvent gameEvent = event.getVanillaEvent();

        // 先只抓 STEP，最稳
        if (gameEvent != GameEvent.STEP)
            return;

        long now = player.level().getGameTime();
        long last = player.getPersistentData().getLong(NBT_LAST_SILENT_STEP);


        // 节流，防止一小段移动里连触发太多次
        if (now - last < 3)
            return;
        player.getPersistentData().putLong(NBT_LAST_SILENT_STEP, now);

        int amp = inst.getAmplifier();

        float selfDamage = switch (amp) {
            case 0 -> 0.5f;
            case 1 -> 0.75f;
            default -> 1.0f;
        };

        int duration = switch (amp) {
            case 0 -> 30;
            case 1 -> 40;
            default -> 50;
        };

        // 自伤
        player.hurt(
                DreamtinkerDamageTypes.source(player.level().registryAccess(), DreamtinkerDamageTypes.edict_punishments, null, null),
                selfDamage
        );

        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, amp));
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        if (player.level().isClientSide())
            return;

        MobEffectInstance inst = player.getEffect(DreamtinkerEffects.InterdictOfAscent.get());
        if (inst == null)
            return;

        long now = player.level().getGameTime();
        long last = player.getPersistentData().getLong(TAG_ASCENT_LAST_JUMP);

        // 防止极端情况下短时间重复触发
        if (now - last < 2)
            return;
        player.getPersistentData().putLong(TAG_ASCENT_LAST_JUMP, now);

        int amp = inst.getAmplifier();

        float selfDamage = switch (amp) {
            case 0 -> 0.5f;
            case 1 -> 0.75f;
            default -> 1.0f;
        };

        float nextHitMultiplier = switch (amp) {
            case 0 -> 1.15f;
            case 1 -> 1.25f;
            default -> 1.35f;
        };

        // 自伤
        player.hurt(
                DreamtinkerDamageTypes.source(
                        player.level().registryAccess(),
                        DreamtinkerDamageTypes.edict_punishments, null, null
                ),
                selfDamage
        );

        // 记录“下一击增伤”
        NextHitBonusHelper.push(player, nextHitMultiplier, NextHitSource.ASCENT);
    }

    @SubscribeEvent
    public static void onShieldBlock(ShieldBlockEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        if (player.level().isClientSide())
            return;

        var inst = player.getEffect(DreamtinkerEffects.InterdictOfGuard.get());
        if (inst == null)
            return;

        int amp = inst.getAmplifier();

        int extraShieldDamage = switch (amp) {
            case 0 -> 1;
            case 1 -> 2;
            default -> 3;
        };

        float nextHitMul = switch (amp) {
            case 0 -> 1.20f;
            case 1 -> 1.30f;
            default -> 1.40f;
        };

        ItemStack using = player.getUseItem();
        if (!using.isEmpty()){
            using.hurtAndBreak(extraShieldDamage, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
        }

        NextHitBonusHelper.push(player, nextHitMul, NextHitSource.GUARD);
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        if (player.level().isClientSide())
            return;

        var inst = player.getEffect(DreamtinkerEffects.InterdictOfRestoration.get());
        if (inst == null)
            return;

        int amp = inst.getAmplifier();

        float healFactor = switch (amp) {
            case 0 -> 0.65f;
            case 1 -> 0.55f;
            default -> 0.45f;
        };

        int layers = switch (amp) {
            case 0 -> 3;
            case 1 -> 4;
            default -> 5;
        };

        float perLayer = switch (amp) {
            case 0 -> 1.04f;
            case 1 -> 1.15f;
            default -> 1.26f;
        };

        event.setAmount(event.getAmount() * healFactor);

        for (int i = 0; i < layers; i++) {
            NextHitBonusHelper.push(player, perLayer, NextHitSource.RESTORATION);
        }
    }

    @SubscribeEvent
    public static void onLivingHurtTouched(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        if (player.level().isClientSide())
            return;

        var inst = player.getEffect(DreamtinkerEffects.EdictOfUntouched.get());
        if (inst == null)
            return;

        int amp = inst.getAmplifier();

        float takenMul = switch (amp) {
            case 0 -> 1.05f;
            case 1 -> 1.08f;
            default -> 1.11f;
        };

        float nextHitMul = switch (amp) {
            case 0 -> 1.15f;
            case 1 -> 1.23f;
            default -> 1.31f;
        };

        event.setAmount(event.getAmount() * takenMul);
        NextHitBonusHelper.push(player, nextHitMul, NextHitSource.UNTOUCHED);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();

        if (!(source.getEntity() instanceof ServerPlayer player))
            return;
        if (player.level().isClientSide())
            return;

        float mul = NextHitBonusHelper.peekCombinedMultiplier(player);
        if (mul <= 1.0f)
            return;

        event.setAmount(event.getAmount() * mul);
        NextHitBonusHelper.clear(player);
    }

    public enum NextHitSource {
        ASCENT("interdict_of_ascent"),
        GUARD("interdict_of_guard"),
        RESTORATION("interdict_of_restoration"),
        UNTOUCHED("edict_of_the_untouched"),
        LOWERED_EYES("law_of_the_lowered_eyes");

        private final String id;

        NextHitSource(String id) {
            this.id = id;
        }

        public static NextHitSource byId(String id) {
            for (NextHitSource value : values()) {
                if (value.id.equals(id))
                    return value;
            }
            return null;
        }

        public String id() {
            return id;
        }
    }

    public static final class NextHitBonusHelper {
        private static final String ROOT = "dreamtinker_next_hit";
        private static final String LIST = "entries";

        private static final String KEY_MUL = "mul";
        private static final String KEY_SOURCE = "source";

        // 避免每次 switch / hasEffect 写一长串
        private static final Map<NextHitSource, Supplier<MobEffect>> EFFECTS =
                new EnumMap<>(NextHitSource.class);

        static {
            EFFECTS.put(NextHitSource.ASCENT, DreamtinkerEffects.InterdictOfAscent);
            EFFECTS.put(NextHitSource.GUARD, DreamtinkerEffects.InterdictOfGuard);
            EFFECTS.put(NextHitSource.RESTORATION, DreamtinkerEffects.InterdictOfRestoration);
            EFFECTS.put(NextHitSource.UNTOUCHED, DreamtinkerEffects.EdictOfUntouched);
            EFFECTS.put(NextHitSource.LOWERED_EYES, DreamtinkerEffects.LawOfLoweredEyes);
        }

        private NextHitBonusHelper() {}

        public static void push(ServerPlayer player, float multiplier, NextHitSource source) {
            if (multiplier <= 1.0f || source == null)
                return;

            CompoundTag root = getOrCreateRoot(player);
            ListTag list = getOrCreateEntryList(root);

            CompoundTag entry = new CompoundTag();
            entry.putFloat(KEY_MUL, multiplier);
            entry.putString(KEY_SOURCE, source.id());

            list.add(entry);
            root.put(LIST, list);
            player.getPersistentData().put(ROOT, root);
        }

        /**
         * 只查看当前有效加成，不消费。
         * 会顺手清洗已经失效/非法的条目。
         */
        public static float peekCombinedMultiplier(ServerPlayer player) {
            return collect(player, false);
        }

        /**
         * 获取当前有效总倍率，并消费掉所有仍有效条目。
         * 失效条目也会被一起清掉。
         */
        public static float popCombinedMultiplier(ServerPlayer player) {
            return collect(player, true);
        }

        public static boolean hasAnyValidBonus(ServerPlayer player) {
            return peekCombinedMultiplier(player) > 1.0f;
        }

        public static void clear(ServerPlayer player) {
            player.getPersistentData().remove(ROOT);
        }

        private static float collect(ServerPlayer player, boolean consumeValidEntries) {
            CompoundTag data = player.getPersistentData();
            if (!data.contains(ROOT, Tag.TAG_COMPOUND))
                return 1.0f;

            CompoundTag root = data.getCompound(ROOT);
            if (!root.contains(LIST, Tag.TAG_LIST)){
                data.remove(ROOT);
                return 1.0f;
            }

            ListTag oldList = root.getList(LIST, Tag.TAG_COMPOUND);
            if (oldList.isEmpty()){
                data.remove(ROOT);
                return 1.0f;
            }

            float combined = 1.0f;
            ListTag keptList = new ListTag();

            for (int i = 0; i < oldList.size(); i++) {
                CompoundTag entry = oldList.getCompound(i);

                if (!entry.contains(KEY_MUL, Tag.TAG_FLOAT) || !entry.contains(KEY_SOURCE, Tag.TAG_STRING)){
                    continue;
                }

                float mul = entry.getFloat(KEY_MUL);
                if (mul <= 1.0f){
                    continue;
                }

                NextHitSource source = NextHitSource.byId(entry.getString(KEY_SOURCE));
                if (source == null){
                    continue;
                }

                if (!sourceStillActive(player, source)){
                    continue;
                }

                combined *= mul;

                // peek 时保留有效项；pop 时消费有效项
                if (!consumeValidEntries){
                    keptList.add(entry.copy());
                }
            }

            if (keptList.isEmpty()){
                data.remove(ROOT);
            }else {
                root.put(LIST, keptList);
                data.put(ROOT, root);
            }

            return combined;
        }

        private static boolean sourceStillActive(ServerPlayer player, NextHitSource source) {
            Supplier<net.minecraft.world.effect.MobEffect> supplier = EFFECTS.get(source);
            return supplier != null && player.hasEffect(supplier.get());
        }

        private static CompoundTag getOrCreateRoot(ServerPlayer player) {
            CompoundTag data = player.getPersistentData();
            return data.contains(ROOT, Tag.TAG_COMPOUND) ? data.getCompound(ROOT) : new CompoundTag();
        }

        private static ListTag getOrCreateEntryList(CompoundTag root) {
            return root.contains(LIST, Tag.TAG_LIST) ? root.getList(LIST, Tag.TAG_COMPOUND) : new ListTag();
        }
    }
}
