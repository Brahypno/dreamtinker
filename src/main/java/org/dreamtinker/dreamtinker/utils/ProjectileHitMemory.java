package org.dreamtinker.dreamtinker.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public final class ProjectileHitMemory {
    private ProjectileHitMemory() {}

    /**
     * 该 projectile 是否已经对该 target 触发过一次效果
     */
    public static boolean hasTriggered(String KEY, Entity projectile, UUID targetUuid) {
        CompoundTag data = projectile.getPersistentData();
        if (!data.contains(KEY, Tag.TAG_LIST))
            return false;

        ListTag list = data.getList(KEY, Tag.TAG_STRING);
        String u = targetUuid.toString();
        for (int i = 0; i < list.size(); i++) {
            if (u.equals(list.getString(i)))
                return true;
        }
        return false;
    }

    /**
     * 标记：该 projectile 已经对该 target 触发过
     */
    public static void markTriggered(String KEY, Entity projectile, UUID targetUuid) {
        CompoundTag data = projectile.getPersistentData();
        ListTag list = data.contains(KEY, Tag.TAG_LIST) ? data.getList(KEY, Tag.TAG_STRING) : new ListTag();

        String u = targetUuid.toString();
        // 避免重复写入（可选，但建议）
        for (int i = 0; i < list.size(); i++) {
            if (u.equals(list.getString(i)))
                return;
        }

        list.add(StringTag.valueOf(u));
        data.put(KEY, list);
    }
}

