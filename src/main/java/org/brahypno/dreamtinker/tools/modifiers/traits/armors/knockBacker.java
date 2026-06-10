package org.brahypno.dreamtinker.tools.modifiers.traits.armors;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class knockBacker extends Modifier implements OnAttackedModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
        super.registerHooks(hookBuilder);
    }

    public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        if (source.getEntity() instanceof LivingEntity offender){
            float knockback = (float) context.getEntity().getAttributeValue(Attributes.ATTACK_KNOCKBACK) / 2.0F + 0.4F;
            ItemStack mainhand = context.getEntity().getMainHandItem();
            if (mainhand.getItem() instanceof IModifiable){
                ToolStack ts = ToolStack.from(mainhand);
                float baseKnockback = knockback;
                ToolAttackContext aContext = ToolAttackContext.attacker(context.getEntity()).target(offender).cooldown(1).applyAttributes().build();


                for (ModifierEntry entry : ts.getModifierList()) {
                    knockback = ((MeleeHitModifierHook) entry.getHook(ModifierHooks.MELEE_HIT)).beforeMeleeHit(ts, entry, aContext, amount, baseKnockback,
                                                                                                               knockback);
                }
                if (0 < knockback)
                    offender.knockback((double) knockback, -(double) Mth.sin(offender.getYRot() * ((float) Math.PI / 180F)),
                                       (double) (Mth.cos(offender.getYRot() * ((float) Math.PI / 180F))));
            }
        }
    }

}
