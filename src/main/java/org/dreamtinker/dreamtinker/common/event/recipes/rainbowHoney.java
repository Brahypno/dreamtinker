package org.dreamtinker.dreamtinker.common.event.recipes;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.rainbowHoneyRate;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class rainbowHoney {
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock e) {
        Level level = e.getLevel();
        if (level.isClientSide)
            return;
        if (!level.isRaining() || !level.canSeeSky(e.getPos().above()))
            return;

        ItemStack hand = e.getItemStack();
        BlockState state = level.getBlockState(e.getPos());
        BlockPos pos = e.getPos();
        if (!state.is(BlockTags.BEEHIVES))
            return;
        if (state.hasProperty(BeehiveBlock.HONEY_LEVEL)
            && state.getValue(BeehiveBlock.HONEY_LEVEL) >= 5 && level.random.nextFloat() < rainbowHoneyRate.get()){

            //——到这里就判定成功——//

            // 你的产物：彩虹蜂蜜瓶
            ItemStack out = new ItemStack(DreamtinkerCommon.rainbow_honey.get());

            // 消耗瓶子 & 给产物
            hand.shrink(1);
            if (!e.getEntity().addItem(out)){
                e.getEntity().drop(out, false);
            }

            // 重置蜂蜜等级为 0（等同于原版装瓶后的状态）
            level.setBlock(pos, state.setValue(BeehiveBlock.HONEY_LEVEL, 0), 3);

            // 可选：像原版一样触发音效/游戏事件
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(e.getEntity(), GameEvent.FLUID_PICKUP, pos);

            // 可选：根据是否有营火决定是否激怒蜜蜂（与原版一致）
            if (level.getBlockEntity(pos) instanceof BeehiveBlockEntity hive){
                boolean hasCampfire = hive.isSedated();
                if (!hasCampfire){
                    hive.emptyAllLivingFromHive(e.getEntity(), state, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
                }
            }

            // 取消原版交互，避免再走普通蜂蜜逻辑
            e.setCanceled(true);
            e.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
