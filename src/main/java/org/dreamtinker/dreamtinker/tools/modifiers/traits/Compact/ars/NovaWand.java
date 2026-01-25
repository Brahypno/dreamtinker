package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.ars;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.CasterCapability;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.ArrayList;

public class NovaWand extends NoLevelsModifier implements GeneralInteractionModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT);
        hookBuilder.addModule(CasterCapability.CAST_HANDLER);
        super.registerHooks(hookBuilder);
    }

    @Override
    public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        ISpellCaster caster = CasterCapability.getSpellCaster(tool);
        Spell spell = caster.getSpell();
        if (spell.isEmpty())
            return InteractionResult.PASS;
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(MethodProjectile.INSTANCE);
        recipe.add(AugmentAccelerate.INSTANCE);
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;
        return caster.castSpell(player.level(), player, hand, Component.translatable("ars_nouveau.wand.invalid"), spell).getResult();
    }
}
