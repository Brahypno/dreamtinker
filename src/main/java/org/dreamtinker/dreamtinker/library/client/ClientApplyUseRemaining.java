package org.dreamtinker.dreamtinker.library.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientApplyUseRemaining {
    public static void apply(int entityId, int hand, int remaining, boolean active) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null)
            return;
        Entity e = mc.level.getEntity(entityId);
        if (!(e instanceof LivingEntity le))
            return;

        InteractionHand h = hand == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

        if (!active){
            le.stopUsingItem();
            return;
        }

        if (!le.isUsingItem() || le.getUsedItemHand() != h){
            le.startUsingItem(h); // 进入使用状态（不重新触发右键逻辑/音效）
        }
        le.useItemRemaining = remaining;
    }
}
