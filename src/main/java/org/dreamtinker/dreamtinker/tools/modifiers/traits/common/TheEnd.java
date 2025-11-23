package org.dreamtinker.dreamtinker.tools.modifiers.traits.common;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class TheEnd extends Modifier implements GeneralInteractionModifierHook {
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT);
    }

    public @NotNull Component getDisplayName(int level) {
        return this.isNoLevels() ? super.getDisplayName() : super.getDisplayName(level);
    }

    public boolean isNoLevels() {return true;}

    public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        if (source == InteractionSource.RIGHT_CLICK && !tool.isBroken()){
            GeneralInteractionModifierHook.startUsing(tool, modifier.getId(), player, hand);
            return InteractionResult.CONSUME;
        }else {
            return InteractionResult.PASS;
        }
    }

    public void onFinishUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity) {
        if (!tool.isBroken()){
            if (entity.level() instanceof ServerLevel lev && entity.canChangeDimensions()){
                ServerLevel $$5 = lev.getServer().getLevel(Level.OVERWORLD);
                if ($$5 == null){
                    return;
                }
                if (entity instanceof ServerPlayer player)
                    changeDimension(player, lev);
                else
                    entity.changeDimension($$5);
            }
        }

    }

    public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
        return UseAnim.NONE;
    }

    public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
        return 16;
    }

    public void changeDimension(ServerPlayer player, ServerLevel p_9180_) {
        if (!ForgeHooks.onTravelToDimension(player, p_9180_.dimension())){
            return;
        }else {
            player.isChangingDimension = true;
            player.unRide();
            player.serverLevel().removePlayerImmediately(player, Entity.RemovalReason.CHANGED_DIMENSION);
            if (!player.wonGame){
                player.wonGame = true;
                player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, player.seenCredits ? 0.0F : 1.0F));
                player.seenCredits = true;
            }
        }
    }
}
