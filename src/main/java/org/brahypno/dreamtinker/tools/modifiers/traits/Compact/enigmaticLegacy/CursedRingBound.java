package org.brahypno.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.Entity.AggressiveFox;
import org.brahypno.dreamtinker.utils.CompactUtils.EnigmaticLegacyCompact;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

public class CursedRingBound extends Modifier implements EquipmentChangeModifierHook, GeneralInteractionModifierHook, TooltipModifierHook {
    public static final ResourceLocation TAG_DEEP_CURSE = Dreamtinker.getLocation("deeper_curse");

    private boolean check(IToolStackView tool, ServerPlayer player) {
        boolean worthy_check = 0 < tool.getPersistentData().getInt(TAG_DEEP_CURSE);
        return player.getAbilities().instabuild ||
               (EnigmaticLegacyCompact.isTheCursedOne(player) &&
                (!worthy_check || EnigmaticLegacyCompact.isTheWorthyOne(player)));
    }

    @Override
    public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        if (!player.level().isClientSide && player instanceof ServerPlayer sp && this.check(tool, sp))
            return InteractionResult.PASS;
        return InteractionResult.FAIL;
    }

    @Override
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        LivingEntity entity = context.getEntity();
        if (entity.level().isClientSide)
            return;

        boolean pass = entity instanceof ServerPlayer player && this.check(tool, player) || entity instanceof AggressiveFox;
        if (!pass){
            if (entity instanceof Player p){
                if (!p.getInventory().add(context.getReplacement().copy())){
                    p.drop(context.getReplacement().copy(), true); // 背包满则掉落
                }
            }else {
                entity.spawnAtLocation(context.getReplacement().copy());
            }
            entity.setItemSlot(context.getChangedSlot(), ItemStack.EMPTY);
        }
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (0 < tool.getPersistentData().getInt(TAG_DEEP_CURSE) && tooltipKey.isShiftOrUnknown()){
            EnigmaticLegacyCompact.indicateWorthyOnesOnly(tooltip, player);
        }else {
            EnigmaticLegacyCompact.indicateCursedOnesOnly(tooltip, player);
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOLTIP);
        super.registerHooks(hookBuilder);
    }
}
