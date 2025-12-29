package org.dreamtinker.dreamtinker.tools.modifiers.events.compact.ars_nouveau;

import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaBook.ModifiableSpellBook;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.Spell.AugmentTinker;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import static org.dreamtinker.dreamtinker.utils.CompactUtils.arsNova.*;

public class SpellDamageEvents {
    public static void PreSpellDamageEvent(SpellDamageEvent.Pre event) {
        SpellContext context = event.context;
        if (null != context && context.getCasterTool().getItem() instanceof ModifiableSpellBook &&
            context.getSpell().recipe.contains(AugmentTinker.INSTANCE)){
            LivingEntity shooter = event.caster;
            ItemStack casterTool = context.getCasterTool();
            ToolStack tool = ToolStack.from(casterTool);
            Entity target = event.target;
            InteractionHand hand = shooter.getMainHandItem().equals(casterTool) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (isMelee(context)){
                MeleeSpellDamagePre(shooter, target, hand, tool, event.damage);
            }else if (context.getSpell().recipe.contains(MethodProjectile.INSTANCE)){

            }
        }
    }

    public static void PostSpellDamageEvent(SpellDamageEvent.Post event) {
        SpellContext context = event.context;
        if (null != context && context.getCasterTool().getItem() instanceof ModifiableSpellBook &&
            context.getSpell().recipe.contains(AugmentTinker.INSTANCE)){
            LivingEntity shooter = event.caster;
            ItemStack casterTool = context.getCasterTool();
            ToolStack tool = ToolStack.from(casterTool);
            Entity target = event.target;
            InteractionHand hand = shooter.getMainHandItem().equals(casterTool) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (isMelee(context)){
                MeleeSpellDamagePost(shooter, target, hand, tool, event.damage);
            }else if (context.getSpell().recipe.contains(MethodProjectile.INSTANCE)){

            }
        }
    }
}
