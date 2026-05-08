package org.dreamtinker.dreamtinker.tools.modifiers.events;

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
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.CompactUtils.CuriosCompact;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class OathGuardPaleSteelEvents {
    private static final ThreadLocal<Boolean> GUARDIAN_TRANSFER =
            ThreadLocal.withInitial(() -> false);
    public static ResourceLocation oathPaleSteelId = new ResourceLocation("oath_holder");

    private static final String PALE_OATH_EVIL = "dreamtinker_pale_oath_evil";
    private static final float MAX_PALE_OATH_EVIL = 100.0F;

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

    private static List<ServerPlayer> findWhiteOathGuardians(ServerLevel level, LivingEntity target) {
        return level.getEntitiesOfClass(
                ServerPlayer.class,
                target.getBoundingBox().inflate(16.0D),
                player -> player.isAlive()
                          && !player.isSpectator()
                          && !player.isCreative()
                          && player != target
                          && DTModifierCheck.haveModifierIn(player, DreamtinkerModifiers.pale_oath.getId())
                          && isGuardianProtectedTarget(player, target)
        );
    }

    private static void changeFracture(List<ServerPlayer> guardians, int amount) {
        for (ServerPlayer guardian : guardians) {
            applyPaleOathValue(guardian.getArmorSlots(), amount);
            applyPaleOathValue(guardian.getHandSlots(), amount);
            List<ItemStack> extraStacks = CuriosCompact.getCurioStacks(guardian);
            if (extraStacks != null){
                applyPaleOathValue(extraStacks, amount);
            }
        }
    }

    private static void applyPaleOathValue(Iterable<ItemStack> stacks, int amount) {
        for (ItemStack stack : stacks) {
            applyPaleOathValue(stack, amount);
        }
    }

    private static void applyPaleOathValue(ItemStack stack, int amount) {
        if (stack.isEmpty()){
            return;
        }

        if (!stack.is(TinkerTags.Items.MODIFIABLE)){
            return;
        }

        ToolStack tool = ToolStack.from(stack);

        if (tool.getModifierLevel(DreamtinkerModifiers.pale_oath.getId()) <= 0){
            return;
        }

        int oldValue = tool.getPersistentData().getInt(oathPaleSteelId);
        tool.getPersistentData().putInt(oathPaleSteelId, oldValue + amount);
    }

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

        List<ServerPlayer> guardians = findWhiteOathGuardians(level, target);

        if (guardians.isEmpty()){
            return;
        }
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

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onProtectedTargetDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();

        if (target.level().isClientSide()){
            return;
        }

        ServerLevel level = (ServerLevel) target.level();

        List<ServerPlayer> guardians = findWhiteOathGuardians(level, target);

        // 死亡时附近也没有白誓钢守护者，那就没人承担“失败”
        if (guardians.isEmpty()){
            return;
        }
        addEvilToOffenderIfPresent(target, event.getSource(), guardians, 25);

        float amount = 25.0F;

        if (target instanceof Villager){
            amount += 15.0F;
        }

        changeFracture(guardians, (int) amount);
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

}
