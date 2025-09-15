package org.dreamtinker.dreamtinker.event.recipes;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerItems;
import slimeknights.tconstruct.tools.TinkerTools;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class memoryCastConversion {
    @SubscribeEvent
    public static void onRightClickKelp(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide)
            return;

        Player player = event.getEntity();
        ItemStack held = player.getItemInHand(event.getHand());
        if (!(held.getItem() instanceof FlintAndSteelItem || held.getItem().equals(TinkerTools.flintAndBrick.asItem())) || held.is(ItemTags.CREEPER_IGNITERS))
            return;

        BlockPos clicked = event.getPos();
        BlockState state = level.getBlockState(clicked);
        if (!isKelp(state))
            return;

        boolean inWater = player.isUnderWater();
        boolean drowning = player.getAirSupply() <= 0; // 正在掉血的真·窒息
        if (!(inWater && drowning))
            return;

        // 找到整株海带的最顶端
        BlockPos top = clicked;
        while (isKelp(level.getBlockState(top.above()))) {
            top = top.above();
        }

        // 从上到下移除海带，计数
        int removed = 0;
        BlockPos cur = top;
        while (isKelp(level.getBlockState(cur))) {
            level.setBlock(cur, Blocks.WATER.defaultBlockState(), 3);
            removed++;
            cur = cur.below();
        }

        if (removed > 0){
            Vec3 spawnAt = Vec3.atCenterOf(clicked).add(0, 0.1, 0);
            int left = removed;
            while (left > 0) {
                int n = Math.min(64, left);
                ItemEntity drop = new ItemEntity(level, spawnAt.x, spawnAt.y, spawnAt.z, new ItemStack(DreamtinkerItems.memory_cast.get(), n));
                level.addFreshEntity(drop);
                left -= n;
            }
        }
        held.hurtAndBreak(removed, player, p -> p.broadcastBreakEvent(event.getHand()));

        // 取消默认交互（不损耐久、不点燃）
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static boolean isKelp(BlockState s) {
        return s.is(Blocks.KELP) || s.is(Blocks.KELP_PLANT);
    }
}
