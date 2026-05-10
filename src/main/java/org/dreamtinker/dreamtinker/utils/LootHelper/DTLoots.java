package org.dreamtinker.dreamtinker.utils.LootHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.dreamtinker.dreamtinker.utils.DTMethodHandler.findMethod;
import static org.dreamtinker.dreamtinker.utils.DTMethodHandler.findSpecial;

public class DTLoots {

    private static volatile Method DROP_ALL_DEATH_LOOT;
    private static MethodHandle DROP_FROM_LOOT_TABLE;
    private static MethodHandle DROP_CUSTOM_DEATH_LOOT;
    private static MethodHandle DROP_EQUIPMENT;
    private static MethodHandle DROP_EXPERIENCE;

    public static ItemStack tryExtractRareLoot(ServerLevel level, LivingEntity target, float triggerRate, int lootinglevel) {
        List<LootTableItemScanner.LootCandidate> candidates =
                LootTableItemScanner.collect(
                        level,
                        target.getLootTable(),
                        LootTableItemScanner.LootScanOptions.looting(lootinglevel),
                        LootTableItemScanner.CandidateFilter
                                .rareByItemOrDropRate()
                                .and(LootTableItemScanner.CandidateFilter.estimatedRateBelow(0.25D))
                );
        candidates.removeIf(candidate -> candidate.item() == Items.AIR);
        if (candidates.isEmpty())
            return ItemStack.EMPTY;

        if (level.random.nextFloat() >= triggerRate)
            return ItemStack.EMPTY;
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < candidates.size() * 2 && stack.isEmpty(); i++) {
            LootTableItemScanner.LootCandidate candidate = pickByInverseRate(candidates, level.random);
            stack = candidate.getRandomCountStack(level.random);
        }
        return stack;
    }

    private static LootTableItemScanner.LootCandidate pickByInverseRate(List<LootTableItemScanner.LootCandidate> candidates, RandomSource random) {
        double totalWeight = 0.0D;

        for (LootTableItemScanner.LootCandidate candidate : candidates) {
            double rate = Math.max(0.001D, candidate.estimatedRate());
            totalWeight += 1.0D / rate;
        }

        double roll = random.nextDouble() * totalWeight;

        for (LootTableItemScanner.LootCandidate candidate : candidates) {
            double rate = Math.max(0.001D, candidate.estimatedRate());
            roll -= 1.0D / rate;

            if (roll <= 0.0D)
                return candidate;
        }

        return candidates.get(candidates.size() - 1);
    }

    public static void dropAllDeathLootVanilla(LivingEntity victim, DamageSource source) {
        Entity attacker = source.getEntity();
        int looting = net.minecraftforge.common.ForgeHooks.getLootingLevel(victim, attacker, source);
        if (attacker instanceof Player player)
            victim.setLastHurtByPlayer(player);
        else if (attacker != null)
            victim.setLastHurtMob(attacker);

        Collection<ItemEntity> drops = null;
        List<Throwable> errors = new ArrayList<>();

        victim.captureDrops(new ArrayList<>());

        try {
            if (victim.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)){
                try {
                    invokeLivingDropFromLootTable(victim, source, true);
                }
                catch (Throwable t) {
                    errors.add(new RuntimeException("dropFromLootTable failed", t));
                }

                try {
                    invokeLivingDropCustomDeathLoot(victim, source, looting, true);
                }
                catch (Throwable t) {
                    errors.add(new RuntimeException("dropCustomDeathLoot failed", t));
                }
            }

            try {
                invokeLivingDropEquipment(victim);
            }
            catch (Throwable t) {
                errors.add(new RuntimeException("dropEquipment failed", t));
            }

            try {
                invokeLivingDropExperience(victim);
            }
            catch (Throwable t) {
                errors.add(new RuntimeException("dropExperience failed", t));
            }
        }
        finally {
            try {
                drops = victim.captureDrops(null);
            }
            catch (Throwable t) {
                errors.add(new RuntimeException("captureDrops(null) failed", t));
            }
        }

        if (drops != null){
            boolean cancelled = false;

            try {
                cancelled = net.minecraftforge.common.ForgeHooks.onLivingDrops(victim, source, drops, looting, true);
            }
            catch (Throwable t) {
                errors.add(new RuntimeException("onLivingDrops failed", t));
            }

            if (!cancelled){
                for (ItemEntity drop : drops) {
                    try {
                        victim.level().addFreshEntity(drop);
                    }
                    catch (Throwable t) {
                        errors.add(new RuntimeException("addFreshEntity failed for " + drop.getItem(), t));
                    }
                }
            }
        }

        if (!errors.isEmpty()){
            RuntimeException ex = new RuntimeException("dropAllDeathLootVanillaSafe had " + errors.size() + " error(s)");
            errors.forEach(ex::addSuppressed);
            throw ex;
        }
    }

    public static void invokeDropAllDeathLoot(LivingEntity entity, DamageSource source) {
        try {
            Method m = DROP_ALL_DEATH_LOOT;
            if (m == null){
                m = findMethod(
                        LivingEntity.class,
                        // 1) 首选：你当前映射下的名字（official/parchment）
                        "dropAllDeathLoot",
                        // 2) 备用：如果你确实在旧版，可把 SRG/obf 名字放这里
                        "m_6668_",  // 示例：自己填你查到的别名
                        new Class<?>[]{DamageSource.class},
                        void.class,
                        /* mustBeInstance */ true
                );
                m.setAccessible(true); // 允许访问 protected/private
                DROP_ALL_DEATH_LOOT = m;
            }
            m.invoke(entity, source);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to invoke dropAllDeathLoot via reflection", e);
        }
    }

    public static void invokeLivingDropFromLootTable(LivingEntity entity, DamageSource source, boolean causedByPlayer) {
        try {
            MethodHandle mh = DROP_FROM_LOOT_TABLE;
            if (mh == null){
                mh = findSpecial(
                        LivingEntity.class,
                        new String[]{"dropFromLootTable", "m_7625_"},
                        MethodType.methodType(void.class, DamageSource.class, boolean.class)
                );
                DROP_FROM_LOOT_TABLE = mh;
            }

            mh.invokeExact(entity, source, causedByPlayer);
        }
        catch (Throwable e) {
            throw new RuntimeException("Failed to invokespecial LivingEntity#dropFromLootTable", e);
        }
    }

    public static void invokeLivingDropCustomDeathLoot(LivingEntity entity, DamageSource source, int lootingLevel, boolean causedByPlayer) {
        try {
            MethodHandle mh = DROP_CUSTOM_DEATH_LOOT;
            if (mh == null){
                mh = findSpecial(
                        LivingEntity.class,
                        new String[]{"dropCustomDeathLoot", "m_7472_"},
                        MethodType.methodType(void.class, DamageSource.class, int.class, boolean.class)
                );
                DROP_CUSTOM_DEATH_LOOT = mh;
            }

            mh.invokeExact(entity, source, lootingLevel, causedByPlayer);
        }
        catch (Throwable e) {
            throw new RuntimeException("Failed to invokespecial LivingEntity#dropCustomDeathLoot", e);
        }
    }

    public static void invokeLivingDropEquipment(LivingEntity entity) {
        try {
            MethodHandle mh = DROP_EQUIPMENT;
            if (mh == null){
                mh = findSpecial(
                        LivingEntity.class,
                        new String[]{"dropEquipment", "m_5907_"},
                        MethodType.methodType(void.class)
                );
                DROP_EQUIPMENT = mh;
            }

            mh.invokeExact(entity);
        }
        catch (Throwable e) {
            throw new RuntimeException("Failed to invokespecial LivingEntity#dropEquipment", e);
        }
    }

    public static void invokeLivingDropExperience(LivingEntity entity) {
        try {
            MethodHandle mh = DROP_EXPERIENCE;
            if (mh == null){
                mh = findSpecial(
                        LivingEntity.class,
                        new String[]{"dropExperience", "m_21226_"},
                        MethodType.methodType(void.class)
                );
                DROP_EXPERIENCE = mh;
            }

            mh.invokeExact(entity);
        }
        catch (Throwable e) {
            throw new RuntimeException("Failed to invokespecial LivingEntity#dropExperience", e);
        }
    }

}
