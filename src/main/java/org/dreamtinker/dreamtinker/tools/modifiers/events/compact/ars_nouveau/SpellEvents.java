package org.dreamtinker.dreamtinker.tools.modifiers.events.compact.ars_nouveau;

import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.event.SpellProjectileHitEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaBook.ModifiableSpellBook;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaCast.ModifiableSpellResolver;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import static org.dreamtinker.dreamtinker.utils.CompactUtils.arsNovaUtils.*;

public class SpellEvents {
    public static void PreSpellDamageEvent(SpellDamageEvent.Pre event) {
        SpellContext context = event.context;
        if (null != context && context.getCasterTool().getItem() instanceof ModifiableSpellBook && isTinker(context)){
            LivingEntity shooter = event.caster;
            ItemStack casterTool = context.getCasterTool();
            ToolStack tool = ToolStack.from(casterTool);
            Entity target = event.target;
            InteractionHand hand = shooter.getMainHandItem().equals(casterTool) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            MeleeSpellDamagePre(shooter, target, hand, tool, event.damage);
        }
    }

    public static void PostSpellDamageEvent(SpellDamageEvent.Post event) {
        SpellContext context = event.context;
        if (null != context && context.getCasterTool().getItem() instanceof ModifiableSpellBook && isTinker(context)){
            LivingEntity shooter = event.caster;
            ItemStack casterTool = context.getCasterTool();
            ToolStack tool = ToolStack.from(casterTool);
            Entity target = event.target;
            InteractionHand hand = shooter.getMainHandItem().equals(casterTool) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            MeleeSpellDamagePost(shooter, target, hand, tool, event.damage);
        }
    }

    public static void SpellProjectileHitEvent(SpellProjectileHitEvent event) {
        EntityProjectileSpell projectile = event.projectile;
        if (projectile.spellResolver instanceof ModifiableSpellResolver resolver &&
            resolver.spellContext.getCasterTool().getItem() instanceof ModifiableSpellBook && isTinker(resolver.spellContext)){
            ModifierNBT modifiers = EntityModifierCapability.getOrEmpty(projectile);
            if (modifiers.isEmpty()){//if its not empty then it could be tracked by tinker events
                System.out.println("happened?");
                ToolStack tool = ToolStack.from(resolver.spellContext.getCasterTool());
                ModifierNBT new_modifiers = tool.getModifiers();
                EntityModifierCapability.getCapability(projectile).addModifiers(new_modifiers);
                // fetch the persistent data for the arrow as modifiers may want to store data
                ModDataNBT nbt = PersistentDataCapability.getOrWarn(projectile);
                HitResult hit = event.hit;
                HitResult.Type type = hit.getType();
                // extract a firing entity as that is a common need
                LivingEntity attacker = projectile.getOwner() instanceof LivingEntity l ? l : null;
                ModuleHook<ProjectileHitModifierHook> hook =
                        projectile.level().isClientSide ? ModifierHooks.PROJECTILE_HIT_CLIENT : ModifierHooks.PROJECTILE_HIT;
                switch (type) {
                    case ENTITY -> {
                        EntityHitResult entityHit = (EntityHitResult) hit;
                        Entity entity = entityHit.getEntity();
                        LivingEntity target = ToolAttackUtil.getLivingEntity(entity);

                        // ensure we are not blocking, that means projectile shouldn't hit
                        boolean notBlocked = true;
                        if (target != null && target.isBlocking()){
                            Vec3 direction = projectile.position().vectorTo(target.position()).normalize();
                            direction = new Vec3(direction.x, 0.0D, direction.z);
                            if (direction.dot(target.getViewVector(1.0F)) < 0.0D){
                                notBlocked = false;
                            }
                        }
                        for (ModifierEntry entry : new_modifiers.getModifiers()) {
                            entry.getHook(hook).onProjectileHitEntity(new_modifiers, nbt, entry, projectile, entityHit, attacker, target,
                                                                      notBlocked);//hit happened already, so I don't care
                        }
                    }
                    case BLOCK -> {
                        BlockHitResult blockHit = (BlockHitResult) hit;
                        for (ModifierEntry entry : new_modifiers.getModifiers()) {
                            if (entry.getHook(hook).onProjectileHitsBlock(new_modifiers, nbt, entry, projectile, blockHit, attacker)){
                                event.setCanceled(true);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void SpellCostCalcEvent(SpellCostCalcEvent event) {
        if (event.isCanceled())
            return;
        LivingEntity caster = event.context.getUnwrappedCaster();
        event.currentCost -= 10 * DTModifierCheck.getEntityModifierNum(caster, DreamtinkerModifiers.Ids.nova_mana_reduce);
        event.currentCost -= 0 < DTModifierCheck.getItemModifierNum(event.context.getCasterTool(), DreamtinkerModifiers.nova_enchanter_sword.getId()) ?
                             AugmentAmplify.INSTANCE.getCastingCost() : 0;
        event.currentCost -= 0 < DTModifierCheck.getItemModifierNum(event.context.getCasterTool(), DreamtinkerModifiers.nova_spell_bow.getId()) ?
                             MethodProjectile.INSTANCE.getCastingCost() : 0;

    }
}
