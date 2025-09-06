package org.dreamtinker.dreamtinker.modifier.armors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.ArmorModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.EnderDodgeChance;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.EnderDodgetimes;

public class ender_dodge extends ArmorModifier {
    private boolean teleport(LivingEntity entity) {
        if (!entity.level().isClientSide() && entity.isAlive()){
            double d0 = entity.getX() + (entity.level().random.nextDouble() - (double) 0.5F) * (double) 64.0F;
            double d1 = entity.getY() + (double) (entity.level().random.nextInt(64) - 32);
            double d2 = entity.getZ() + (entity.level().random.nextDouble() - (double) 0.5F) * (double) 64.0F;
            return teleport(entity, d0, d1, d2);
        }else {
            return false;
        }
    }

    private boolean teleport(LivingEntity entity, double p_32544_, double p_32545_, double p_32546_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_32544_, p_32545_, p_32546_);

        while (blockpos$mutableblockpos.getY() > entity.level().getMinBuildHeight() && !entity.level().getBlockState(blockpos$mutableblockpos).blocksMotion()) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }

        BlockState blockstate = entity.level().getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1){
            EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(entity, p_32544_, p_32545_, p_32546_);
            if (event.isCanceled()){
                return false;
            }else {
                Vec3 vec3 = entity.position();
                boolean flag2 = entity.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
                if (flag2){
                    entity.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(entity));
                    if (!entity.isSilent()){
                        entity.level()
                              .playSound((Player) null, entity.xo, entity.yo, entity.zo, SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 1.0F, 1.0F);
                        entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                    }
                }

                return flag2;
            }
        }else {
            return false;
        }
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
        LivingEntity holder = context.getEntity();
        Level level = holder.level();
        if (!level.isClientSide() && !(source.getEntity() instanceof LivingEntity) && holder.level().random.nextFloat() < EnderDodgeChance.get()){
            for (int i = 0; i < EnderDodgetimes.get(); ++i) {
                if (teleport(holder)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!isCorrectSlot)
            return;
        if (world.isClientSide)
            return;
        if (holder.isInWater()){
            holder.hurt(new DamageSource(world.damageSources().drown().typeHolder()), 5);
        }
    }
}
