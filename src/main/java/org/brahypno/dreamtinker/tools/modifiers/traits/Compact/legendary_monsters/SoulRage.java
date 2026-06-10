package org.brahypno.dreamtinker.tools.modifiers.traits.Compact.legendary_monsters;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class SoulRage extends NoLevelsModifier implements GeneralInteractionModifierHook {
    private static final ResourceLocation SOUL_RAGE = new ResourceLocation("legendary_monsters", "soul_rage");
    private static final int DURATION = 20;

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public @NotNull InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        MobEffect soulRage = ForgeRegistries.MOB_EFFECTS.getValue(SOUL_RAGE);
        if (source != InteractionSource.RIGHT_CLICK || tool.isBroken() || soulRage == null){
            return InteractionResult.PASS;
        }
        if (!player.level().isClientSide){
            player.addEffect(new MobEffectInstance(soulRage, DURATION));
            player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), DURATION);
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }
}
