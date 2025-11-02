package org.dreamtinker.dreamtinker.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.ProjLimit;

public class DThelper {
    public static void clearProjectile(ServerLevel level, double px, double pz) {
        int viewDist = level.getServer().getPlayerList().getViewDistance();
        double radius = viewDist * 16.0;
        AABB box = new AABB(px - radius, level.getMinBuildHeight(), pz - radius, px + radius, level.getMaxBuildHeight(), pz + radius);
        Predicate<Projectile> all = p -> true;
        List<Projectile> list = level.getEntitiesOfClass(Projectile.class, box, all);
        if (ProjLimit.get() <= list.size())
            for (Projectile old : list)
                old.remove(Entity.RemovalReason.DISCARDED);
    }

    private static boolean isMatch(ItemStack stack, ModifierNBT target, boolean compareUpgrades) {
        if (stack.isEmpty() || !stack.is(TinkerTags.Items.MODIFIABLE))
            return false;
        ToolStack tool = ToolStack.from(stack);
        ModifierNBT mine = compareUpgrades ? tool.getUpgrades() : tool.getModifiers();
        return mine.equals(target);
    }

    public static ItemStack findItemByModifierNBT(LivingEntity entity, ModifierNBT target, boolean compareUpgrades) {
        ItemStack main = entity.getMainHandItem();
        if (isMatch(main, target, compareUpgrades)){
            return main;
        }
        ItemStack off = entity.getOffhandItem();
        if (isMatch(off, target, compareUpgrades)){
            return off;
        }
        if (entity instanceof Player player)
            for (int i = 0; i < 9; i++) {
                ItemStack hot = player.getInventory().getItem(i);
                if (isMatch(hot, target, compareUpgrades)){
                    return hot;
                }
            }
        return null;
    }

    public static void debugEffects(List<MobEffect> effects) {
        for (MobEffect effect : effects) {
            ResourceLocation id = ForgeRegistries.MOB_EFFECTS.getKey(effect);
            String key = effect.getDescriptionId();
            System.out.println("Random effect → {" + id + "} ({" + key + "})");
        }
    }

    public static boolean teleport(LivingEntity entity) {
        if (!entity.level().isClientSide() && entity.isAlive()){
            double d0 = entity.getX() + (entity.level().random.nextDouble() - (double) 0.5F) * (double) 64.0F;
            double d1 = entity.getY() + (double) (entity.level().random.nextInt(64) - 32);
            double d2 = entity.getZ() + (entity.level().random.nextDouble() - (double) 0.5F) * (double) 64.0F;
            return teleport(entity, d0, d1, d2);
        }else {
            return false;
        }
    }

    private static boolean teleport(LivingEntity entity, double p_32544_, double p_32545_, double p_32546_) {
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

    public static List<ToolPartItem> getPartList(MaterialStatsId statsId) {
        return ForgeRegistries.ITEMS.getValues().stream().
                                    filter(item -> item instanceof ToolPartItem part && part.getStatType() == statsId)
                                    .map(item -> (ToolPartItem) item).toList();

    }

    public static ItemStack getPart(MaterialId id, MaterialStatsId statsId, @Nullable RandomSource rdm) {
        MaterialVariantId mli = MaterialRegistry.getMaterial(id).getIdentifier();

        List<ToolPartItem> Parts = getPartList(statsId);
        if (Parts.isEmpty())
            return ItemStack.EMPTY;
        ToolPartItem part = Parts.get(0);
        if (rdm != null)
            part = Parts.get(rdm.nextInt(Parts.size()));
        return part.withMaterial(mli);
    }

    public static boolean startToolInteract(Player player, EquipmentSlot slotType, TooltipKey modifierKey) {
        if (!player.isSpectator()){
            ItemStack helmet = player.getItemBySlot(slotType);
            if (!helmet.is(TinkerTags.Items.ARMOR) && helmet.is(TinkerTags.Items.MODIFIABLE)){
                ToolStack tool = ToolStack.from(helmet);
                for (ModifierEntry entry : tool.getModifierList()) {
                    if (entry.getHook(ModifierHooks.ARMOR_INTERACT).startInteract(tool, entry, player, slotType, modifierKey)){
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
