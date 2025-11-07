package org.dreamtinker.dreamtinker.common.event.recipes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class PoisonHomunculusConversion {
    @SubscribeEvent
    public static void LivingHurtEvent(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        ServerLevel level = (ServerLevel) victim.level();

        if (victim.level().isClientSide || event.isCanceled() || !event.getSource().is(DamageTypes.FALLING_ANVIL) ||
            !(victim instanceof Villager || victim.getType() == EntityType.VILLAGER))
            return;
        Entity direct = event.getSource().getDirectEntity();
        if (!(direct instanceof FallingBlockEntity fb) || !fb.getBlockState().is(Dreamtinker.mcBlockTag("anvil")))
            return;
        if (30 < victim.getHealth())
            return;

        BlockPos under = victim.blockPosition().below();
        if (!victim.getBlockStateOn().is(Tags.Blocks.GLASS))
            return;
        level.destroyBlock(under, /*drop=*/false, victim);

        // 移除落下的铁砧实体，避免落地生成方块
        fb.discard();

        // 清除村民本体
        victim.discard();

        // 生成掉落物A
        ItemStack reward = new ItemStack(DreamtinkerCommon.poisonousHomunculus.get());
        ItemEntity item = new ItemEntity(level, victim.getX(), victim.getY(), victim.getZ(), reward);
        level.addFreshEntity(item);

        // 取消这次伤害（避免双重掉落/死亡信息）
        event.setCanceled(true);
    }
}
