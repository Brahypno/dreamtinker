package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.desire_gem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.MeleeInterface;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.dreamtinker.dreamtinker.utils.DTMethodHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;

public class VisionaryWishes extends Modifier implements MeleeInterface, TooltipModifierHook, InventoryTickModifierHook {
    private static final int BASE_HIT_GAIN = 6;
    private static final int BOOSTED_HIT_GAIN = 10;

    private static final int BASE_KILL_GAIN = 12;
    private static final int BOOSTED_KILL_GAIN = 20;

    public static final int COOLDOWN_DURATION = 20 * 3;

    public static void updateStack(ItemStack stack, boolean on) {
        MaterialVariantId replace = on ? DreamtinkerMaterialIds.musou : DreamtinkerMaterialIds.desire_gem;
        MaterialVariantId target = on ? DreamtinkerMaterialIds.desire_gem : DreamtinkerMaterialIds.musou;
        ToolStack tool = ToolStack.from(stack);
        MaterialNBT mats = tool.getMaterials();
        for (int i = 0; i < mats.size(); i++) {
            if (mats.get(i).sameVariant(target))
                mats = mats.replaceMaterial(i, replace);
        }
        tool.setMaterials(mats);
        tool.updateStack(stack);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (!(context.getAttacker() instanceof ServerPlayer player)){
            return;
        }

        LivingEntity target = context.getLivingTarget();
        if (target == null){
            return;
        }


        boolean boosted = WishPowerData.boosted(tool, player.level());
        int gain = boosted ? BOOSTED_HIT_GAIN : BASE_HIT_GAIN;

        if (!target.isAlive() || target.isDeadOrDying()){
            gain += boosted ? BOOSTED_KILL_GAIN : BASE_KILL_GAIN;
        }

        WishPowerData.add(tool, gain * modifier.getLevel());
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.MeleeInterfaceInit(hookBuilder);
        hookBuilder.addHook(this, ModifierHooks.TOOLTIP, ModifierHooks.INVENTORY_TICK);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        if (WishPowerData.boosted(tool, context.getLevel())){
            damage *= 1.935F + 0.5f * (modifier.getLevel() - 1);
        }

        return damage;
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        if (null == context.getLivingTarget() || !context.getLivingTarget().isAlive())
            return;
        if (WishPowerData.boosted(tool, context.getLevel())){
            DamageSource dmg =
                    DreamtinkerDamageTypes.source(context.getLevel().registryAccess(), DreamtinkerDamageTypes.many_wishes, context.makeDamageSource());
            DTMethodHandler.invokeLivingHurt(context.getLivingTarget(), dmg, damageAttempted);
        }
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        int wish = WishPowerData.get(tool);

        tooltip.add(Component.translatable(modifier.getModifier().getTranslationKey() + ".tooltip", wish, WishPowerData.MAX_WISH)
                             .withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!world.isClientSide && world.getGameTime() % 20 == 1){
            if (WishPowerData.updateState(tool, world, COOLDOWN_DURATION)){
                updateStack(stack, false);
            }
        }
    }
}
