package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.ars;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.IWrappedCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.CasterCapability;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaCast.ModifiableSpellResolver;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.MeleeInterface;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.ArrayList;

public class NovaEnchanterSword extends NoLevelsModifier implements MeleeInterface {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.MeleeInterfaceInit(hookBuilder);
        hookBuilder.addModule(CasterCapability.CAST_HANDLER);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        onMonsterMeleeHit(tool, modifier, context, damage);
        return knockback;
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        ISpellCaster caster = CasterCapability.getSpellCaster(tool);
        LivingEntity attacker = context.getAttacker();
        ItemStack stack = attacker.getItemInHand(context.getHand());
        Spell spell = caster.getSpell();
        if (spell.isEmpty())
            return;
        IWrappedCaster wrappedCaster = attacker instanceof Player player ? new PlayerCaster(player) : new LivingCaster(attacker);
        SpellContext spellContext =
                new SpellContext(attacker.level(),
                                 caster.modifySpellBeforeCasting(context.getTarget().level(), attacker, InteractionHand.MAIN_HAND, caster.getSpell()),
                                 attacker, wrappedCaster, stack);
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(MethodTouch.INSTANCE);
        recipe.addAll(caster.getSpell().recipe);
        recipe.add(AugmentAmplify.INSTANCE);
        spell.recipe = recipe;
        spellContext.withSpell(spell);
        SpellResolver resolver = attacker instanceof Player ? new ModifiableSpellResolver(spellContext) : new EntitySpellResolver(spellContext);
        EntityHitResult entityRes = new EntityHitResult(context.getTarget());
        resolver.onCastOnEntity(stack, entityRes.getEntity(), InteractionHand.MAIN_HAND);
    }

}
