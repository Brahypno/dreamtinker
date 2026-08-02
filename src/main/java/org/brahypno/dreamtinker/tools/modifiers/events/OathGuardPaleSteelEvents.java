package org.brahypno.dreamtinker.tools.modifiers.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.dreamtinker.utils.EquippedModifierSnapshot;
import org.brahypno.esotericismtinker.utils.CompatUtils.CuriosCompat;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class OathGuardPaleSteelEvents {
    private static final ThreadLocal<Boolean> GUARDIAN_TRANSFER =
            ThreadLocal.withInitial(() -> false);
    public static ResourceLocation oathPaleSteelId = new ResourceLocation("oath_holder");
    public static final ResourceLocation oathBrokenSteelId = Dreamtinker.getLocation("broken_oath_despair");

    private static final String PALE_OATH_EVIL = "dreamtinker_pale_oath_evil";
    private static final float MAX_PALE_OATH_EVIL = 100.0F;
    private static final Map<ServerPlayer, ProtectedTargets> PROTECTED_TARGET_CACHE = new WeakHashMap<>();


    @SubscribeEvent
    public static void onProtectedTargetHurt(LivingHurtEvent event) {
        if (GUARDIAN_TRANSFER.get()){
            return;
        }

        LivingEntity target = event.getEntity();

        if (target.level().isClientSide()){
            return;
        }

        ServerLevel level = (ServerLevel) target.level();

        float damage = event.getAmount();
        if (damage <= 0.0F){
            return;
        }

        OathParticipants participants = findOathParticipants(level, target);
        List<ServerPlayer> guardians = participants.guardians();
        List<ServerPlayer> des = participants.brokenGuardians();
        if (!guardians.isEmpty()){
            addEvilToOffenderIfPresent(target, event.getSource(), guardians, event.getAmount());

            guardians.sort(Comparator.comparingDouble(player -> player.distanceToSqr(target)));

            float remaining = damage;
            float totalTransferred = 0.0F;

            GUARDIAN_TRANSFER.set(true);
            try {
                for (ServerPlayer guardian : guardians) {
                    if (remaining <= 0.0F){
                        break;
                    }

                    float transfer = remaining * 0.10F;
                    remaining -= transfer;
                    totalTransferred += transfer;

                    guardian.hurt(event.getSource(), transfer);
                }
            }
            finally {
                GUARDIAN_TRANSFER.set(false);
            }

            event.setAmount(remaining);

            if (totalTransferred > 0.0F){
                changeFracture(guardians, (int) (-Math.max(1, totalTransferred * 0.5F))); // 守护成功
            }
        }

        if (!des.isEmpty()){
            addEvilToOffenderIfPresent(target, event.getSource(), des, event.getAmount());
            changeDespair(des, (int) (Math.max(1, damage * 0.5F)));
        }

    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onProtectedTargetDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();

        if (target.level().isClientSide()){
            return;
        }

        ServerLevel level = (ServerLevel) target.level();
        float amount = 25.0F;

        if (target instanceof Villager){
            amount += 15.0F;
        }
        OathParticipants participants = findOathParticipants(level, target);
        List<ServerPlayer> guardians = participants.guardians();

        // 死亡时附近也没有白誓钢守护者，那就没人承担“失败”
        if (!guardians.isEmpty()){
            addEvilToOffenderIfPresent(target, event.getSource(), guardians, 25);
            changeFracture(guardians, (int) amount);
        }
        List<ServerPlayer> des = participants.brokenGuardians();

        // 死亡时附近也没有白誓钢守护者，那就没人承担“失败”
        if (!des.isEmpty()){
            addEvilToOffenderIfPresent(target, event.getSource(), des, 25);
            changeDespair(des, (int) amount);
        }

    }

    public static boolean isGuardianProtectedTarget(ServerPlayer holder, LivingEntity target) {
        if (target == holder){
            return false;
        }

        // 同队玩家视为守护对象
        if (target instanceof ServerPlayer otherPlayer){
            return holder.isAlliedTo(otherPlayer);
        }

        // 村民视为守护对象
        if (target instanceof Villager){
            return true;
        }

        // 悦灵视为守护对象
        if (target instanceof Allay){
            return true;
        }

        // 已驯服动物，且主人与守护者同队
        if (target instanceof TamableAnimal tameable){
            if (!tameable.isTame()){
                return false;
            }

            LivingEntity owner = tameable.getOwner();
            return owner != null && (owner == holder || holder.isAlliedTo(owner));
        }

        return false;
    }

    /**
     * Returns the living entities protected by this player, reusing the same spatial query for every
     * armor piece/hook invoked during the current tick.
     */
    public static List<LivingEntity> findProtectedTargets(ServerPlayer player) {
        long gameTime = player.level().getGameTime();
        synchronized (PROTECTED_TARGET_CACHE) {
            ProtectedTargets cached = PROTECTED_TARGET_CACHE.get(player);
            if (cached != null && cached.gameTime() == gameTime) {
                return cached.targets();
            }
            List<LivingEntity> targets = player.level().getEntitiesOfClass(
                    LivingEntity.class,
                    player.getBoundingBox().inflate(16.0D),
                    target -> target != player
                              && target.isAlive()
                              && isGuardianProtectedTarget(player, target));
            List<LivingEntity> immutableTargets = List.copyOf(targets);
            PROTECTED_TARGET_CACHE.put(player, new ProtectedTargets(gameTime, immutableTargets));
            return immutableTargets;
        }
    }

    /** One player query, followed by cheap classification from the shared one-tick equipment snapshot. */
    private static OathParticipants findOathParticipants(ServerLevel level, LivingEntity target) {
        List<ServerPlayer> nearby = level.getEntitiesOfClass(
                ServerPlayer.class,
                target.getBoundingBox().inflate(16.0D),
                player -> player.isAlive()
                          && !player.isSpectator()
                          && !player.isCreative()
                          && player != target
                          && isGuardianProtectedTarget(player, target)
        );

        List<ServerPlayer> guardians = new ArrayList<>();
        List<ServerPlayer> brokenGuardians = new ArrayList<>();
        for (ServerPlayer player : nearby) {
            if (EquippedModifierSnapshot.has(player, DreamtinkerModifiers.pale_oath.getId())){
                guardians.add(player);
            }
            if (EquippedModifierSnapshot.has(player, DreamtinkerModifiers.broken_oath.getId())){
                brokenGuardians.add(player);
            }
        }
        return new OathParticipants(guardians, brokenGuardians);
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        synchronized (PROTECTED_TARGET_CACHE) {
            PROTECTED_TARGET_CACHE.clear();
        }
        EquippedModifierSnapshot.clear();
        GUARDIAN_TRANSFER.remove();
    }

    private static void changeFracture(List<ServerPlayer> guardians, int amount) {
        changeOathValue(guardians, amount, DreamtinkerModifiers.pale_oath.getId(), oathPaleSteelId);
    }

    private static void changeDespair(List<ServerPlayer> despairPlayers, int amount) {
        changeOathValue(despairPlayers, amount, DreamtinkerModifiers.broken_oath.getId(), oathBrokenSteelId);
    }

    private static void changeOathValue(List<ServerPlayer> players, int amount, ModifierId modifierId, ResourceLocation dataKey) {
        for (ServerPlayer player : players) {
            applyOathValue(player.getArmorSlots(), amount, modifierId, dataKey);
            applyOathValue(player.getHandSlots(), amount, modifierId, dataKey);

            List<ItemStack> extraStacks = CuriosCompat.getCurioStacks(player);
            if (extraStacks != null){
                applyOathValue(extraStacks, amount, modifierId, dataKey);
            }
        }
    }

    private static void applyOathValue(Iterable<ItemStack> stacks, int amount, ModifierId modifierId, ResourceLocation dataKey) {
        for (ItemStack stack : stacks) {
            applyOathValue(stack, amount, modifierId, dataKey);
        }
    }

    private static void applyOathValue(ItemStack stack, int amount, ModifierId modifierId, ResourceLocation dataKey) {
        if (stack.isEmpty()){
            return;
        }

        if (!stack.is(TinkerTags.Items.MODIFIABLE)){
            return;
        }

        ToolStack tool = ToolStack.from(stack);
        if (tool.getModifierLevel(modifierId) <= 0){
            return;
        }

        int value = tool.getPersistentData().getInt(dataKey) + amount;
        tool.getPersistentData().putInt(dataKey, value);
    }

    private static String oathKey(ServerPlayer guardian) {
        return guardian.getUUID().toString();
    }

    private static CompoundTag getOrCreateOathEvilMap(LivingEntity entity) {
        CompoundTag data = entity.getPersistentData();

        if (!data.contains(PALE_OATH_EVIL, Tag.TAG_COMPOUND)){
            data.put(PALE_OATH_EVIL, new CompoundTag());
        }

        return data.getCompound(PALE_OATH_EVIL);
    }

    public static float getOathEvil(LivingEntity offender, ServerPlayer guardian) {
        CompoundTag map = offender.getPersistentData().getCompound(PALE_OATH_EVIL);
        return map.getFloat(oathKey(guardian));
    }

    private static void addOathEvil(LivingEntity offender, ServerPlayer guardian, float amount) {
        if (amount <= 0.0F){
            return;
        }

        CompoundTag map = getOrCreateOathEvilMap(offender);
        String key = oathKey(guardian);

        float oldValue = map.getFloat(key);
        float newValue = Mth.clamp(oldValue + amount, 0.0F, MAX_PALE_OATH_EVIL);

        map.putFloat(key, newValue);
    }

    public static void reduceOathEvil(LivingEntity offender, ServerPlayer guardian, float amount) {
        if (amount <= 0.0F){
            return;
        }

        CompoundTag data = offender.getPersistentData();

        if (!data.contains(PALE_OATH_EVIL, Tag.TAG_COMPOUND)){
            return;
        }

        CompoundTag map = data.getCompound(PALE_OATH_EVIL);
        String key = oathKey(guardian);

        float oldValue = map.getFloat(key);
        float newValue = Math.max(0.0F, oldValue - amount);

        if (newValue <= 0.0F){
            map.remove(key);
        }else {
            map.putFloat(key, newValue);
        }

        if (map.isEmpty()){
            data.remove(PALE_OATH_EVIL);
        }
    }

    private static void addEvilToOffenderIfPresent(LivingEntity protectedTarget, DamageSource source, List<ServerPlayer> guardians, float damage) {
        Entity responsible = source.getEntity();

        if (!(responsible instanceof LivingEntity offender)){
            return;
        }

        if (damage <= 0.0F){
            return;
        }

        for (ServerPlayer guardian : guardians) {
            if (!guardian.isAlive()){
                continue;
            }

            if (!isGuardianProtectedTarget(guardian, protectedTarget)){
                continue;
            }

            addOathEvil(offender, guardian, damage);
        }
    }

    private record OathParticipants(List<ServerPlayer> guardians, List<ServerPlayer> brokenGuardians) {}

    private record ProtectedTargets(long gameTime, List<LivingEntity> targets) {}

}
