package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.etherium;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.config.EtheriumConfigHandler;
import com.aizistral.enigmaticlegacy.helpers.AOEMiningHelper;
import com.aizistral.enigmaticlegacy.packets.clients.PacketFlameParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.PacketDistributor;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static com.aizistral.enigmaticlegacy.items.AstralBreaker.miningDepth;
import static com.aizistral.enigmaticlegacy.items.AstralBreaker.miningRadius;

public class astral_break extends BattleModifier {
    @Override
    public void afterBlockBreak(IToolStackView var1, ModifierEntry var2, ToolHarvestContext var3) {
        if (var3.isAOE())
            return;
        Level world = var3.getWorld();
        if (!world.isClientSide)
            this.spawnFlameParticles(world, var3.getTargetedPos());

        if (var3.getLiving() instanceof Player player && !world.isClientSide && miningRadius.getValue() != -1){
            HitResult trace = AOEMiningHelper.calcRayTrace(world, player, ClipContext.Fluid.ANY);

            if (trace.getType() == HitResult.Type.BLOCK){
                BlockHitResult blockTrace = (BlockHitResult) trace;
                Direction face = blockTrace.getDirection();
                ItemStack stack = player.getMainHandItem();

                AOEMiningHelper.harvestCube(world, player, face, var3.getTargetedPos(), (s) -> s.canHarvestBlock(world, var3.getTargetedPos(), player),
                                            miningRadius.getValue() + EtheriumConfigHandler.instance().getAOEBoost(player), miningDepth.getValue(), true,
                                            var3.getTargetedPos(),
                                            stack, (objPos, objState) -> {
                            stack.hurtAndBreak(1, var3.getLiving(), p -> p.broadcastBreakEvent(Mob.getEquipmentSlotForItem(stack)));
                            this.spawnFlameParticles(world, objPos);
                        });
            }
        }
    }

    public void spawnFlameParticles(Level world, BlockPos pos) {
        EnigmaticLegacy.packetInstance.send(
                PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 128, world.dimension())),
                new PacketFlameParticles(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 18, true));
    }
}
