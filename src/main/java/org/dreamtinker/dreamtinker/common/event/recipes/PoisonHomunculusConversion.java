package org.dreamtinker.dreamtinker.common.event.recipes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
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
        boolean is_illager = false, is_villager = false;
        if (victim.getType().is(Tags.EntityTypes.BOSSES))
            return;

        if (victim.level().isClientSide || event.isCanceled() || !event.getSource().is(DamageTypes.FALLING_ANVIL))
            return;
        if (victim instanceof AbstractVillager || victim.getType() == EntityType.VILLAGER)
            is_villager = true;
        if (victim instanceof AbstractIllager || victim.getMobType() == MobType.ILLAGER)
            is_illager = true;
        if (!is_illager && !is_villager)
            return;
        Entity direct = event.getSource().getDirectEntity();
        if (!(direct instanceof FallingBlockEntity fb) || !fb.getBlockState().is(Dreamtinker.mcBlockTag("anvil")))
            return;
        if (30 < victim.getHealth())// In case boss hit by this
            return;

        BlockPos under = victim.blockPosition().below();
        if (!victim.getBlockStateOn().is(Tags.Blocks.GLASS))
            return;
        level.destroyBlock(under, /*drop=*/false, victim);

        // 移除落下的铁砧实体，避免落地生成方块
        fb.discard();

        // 清除村民本体
        victim.discard();

        // 生成掉落物---illager first
        ItemStack reward = new ItemStack(is_illager ? DreamtinkerCommon.evilHomunculus.get() : DreamtinkerCommon.poisonousHomunculus.get());
        ItemEntity item = new ItemEntity(level, victim.getX(), victim.getY(), victim.getZ(), reward);
        level.addFreshEntity(item);

        // 取消这次伤害（避免双重掉落/死亡信息）
        event.setCanceled(true);
    }
}
