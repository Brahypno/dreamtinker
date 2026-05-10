package org.dreamtinker.dreamtinker.utils.LootHelper;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.GameRules;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.dreamtinker.dreamtinker.utils.DTMethodHandler.findMethod;

public class DTDeathLoots {

    private static volatile Method DROP_ALL_DEATH_LOOT;
    private static volatile Method DROP_FROM_LOOT_TABLE;
    private static volatile Method DROP_CUSTOM_DEATH_LOOT;
    private static volatile Method DROP_EQUIPMENT;
    private static volatile Method DROP_EXPERIENCE;

    public static void dropAllDeathLootVanilla(LivingEntity victim, DamageSource source) {
        Entity attacker = source.getEntity();
        int looting = net.minecraftforge.common.ForgeHooks.getLootingLevel(victim, attacker, source);

        Collection<ItemEntity> drops = null;
        List<Throwable> errors = new ArrayList<>();

        victim.captureDrops(new ArrayList<>());

        try {
            if (victim.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)){
                try {
                    invokeDropFromLootTable(victim, source, true);
                }
                catch (Throwable t) {
                    errors.add(new RuntimeException("dropFromLootTable failed", t));
                }

                try {
                    invokeDropCustomDeathLoot(victim, source, looting, true);
                }
                catch (Throwable t) {
                    errors.add(new RuntimeException("dropCustomDeathLoot failed", t));
                }
            }

            try {
                invokeDropEquipment(victim);
            }
            catch (Throwable t) {
                errors.add(new RuntimeException("dropEquipment failed", t));
            }

            try {
                invokeDropExperience(victim);
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

    public static void invokeDropFromLootTable(LivingEntity entity, DamageSource source, boolean causedByPlayer) {
        try {
            Method m = DROP_FROM_LOOT_TABLE;
            if (m == null){
                m = findMethod(
                        LivingEntity.class,
                        "dropFromLootTable",
                        "m_7625_", // 这里改成你当前版本查到的别名；查不到可先删掉这个备用名
                        new Class<?>[]{DamageSource.class, boolean.class},
                        void.class,
                        true
                );
                m.setAccessible(true);
                DROP_FROM_LOOT_TABLE = m;
            }
            m.invoke(entity, source, causedByPlayer);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to invoke dropFromLootTable via reflection", e);
        }
    }

    public static void invokeDropCustomDeathLoot(LivingEntity entity, DamageSource source, int looting_level, boolean causedByPlayer) {
        try {
            Method m = DROP_CUSTOM_DEATH_LOOT;
            if (m == null){
                m = findMethod(
                        LivingEntity.class,
                        "dropCustomDeathLoot",
                        "m_7472_", // 这里改成你当前版本查到的别名；查不到可先删掉这个备用名
                        new Class<?>[]{DamageSource.class, int.class, boolean.class},
                        void.class,
                        true
                );
                m.setAccessible(true);
                DROP_CUSTOM_DEATH_LOOT = m;
            }
            m.invoke(entity, source, looting_level, causedByPlayer);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to invoke DropCustomDeathLoot via reflection", e);
        }
    }

    public static void invokeDropEquipment(LivingEntity entity) {
        try {
            Method m = DROP_EQUIPMENT;
            if (m == null){
                m = findMethod(
                        LivingEntity.class,
                        "dropEquipment",
                        "m_5907_", // 这里改成你当前版本查到的别名；查不到可先删掉这个备用名
                        new Class<?>[]{},
                        void.class,
                        true
                );
                m.setAccessible(true);
                DROP_EQUIPMENT = m;
            }
            m.invoke(entity);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to invoke DropEquipment via reflection", e);
        }
    }

    public static void invokeDropExperience(LivingEntity entity) {
        try {
            Method m = DROP_EXPERIENCE;
            if (m == null){
                m = findMethod(
                        LivingEntity.class,
                        "dropEquipmen",
                        "m_21226_", // 这里改成你当前版本查到的别名；查不到可先删掉这个备用名
                        new Class<?>[]{},
                        void.class,
                        true
                );
                m.setAccessible(true);
                DROP_EXPERIENCE = m;
            }
            m.invoke(entity);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to invoke DropExperience via reflection", e);
        }
    }
}
