package org.dreamtinker.dreamtinker.mixin.NovaMixin;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaBook.ModifiableSpellBook;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaCast.ModifiableSpellResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.ArrayList;
import java.util.List;

import static org.dreamtinker.dreamtinker.utils.CompactUtils.arsNova.isTinker;

@Mixin(value = MethodProjectile.class, remap = false)
public class MethodProjectileMixin {
    @Inject(method = "summonProjectiles(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lcom/hollingsworth/arsnouveau/api/spell/SpellStats;Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;)V", at = @At("HEAD"), cancellable = true)
    public void dreamtinker$summonProjectiles(Level world, LivingEntity shooter, SpellStats stats, SpellResolver resolver, CallbackInfo ci) {
        if (resolver instanceof ModifiableSpellResolver && resolver.spellContext.getCasterTool().getItem() instanceof ModifiableSpellBook &&
            isTinker(resolver.spellContext)){
            ToolStack toolStack = ToolStack.from(resolver.spellContext.getCasterTool());
            int numSplits = 1 + stats.getBuffCount(AugmentSplit.INSTANCE);
            List<EntityProjectileSpell> projectiles = new ArrayList<>();

            for (int i = 0; i < numSplits; ++i) {
                EntityProjectileSpell spell = new EntityProjectileSpell(world, resolver);
                projectiles.add(spell);
            }
            float tool_velocity = ConditionalStatModifierHook.getModifiedStat(toolStack, shooter, ToolStats.VELOCITY);
            float inaccuracy = ModifierUtil.getInaccuracy(toolStack, shooter);
            float velocity = Math.max(0.1F, tool_velocity * 3 + stats.getAccMultiplier() / 2.0F);
            int opposite = -1;
            int counter = 0;

            for (EntityProjectileSpell proj : projectiles) {
                proj.shoot(shooter, shooter.getXRot(), shooter.getYRot() + (float) (Math.round((double) counter / (double) 2.0F) * 10L * (long) opposite), 0.0F,
                           velocity, inaccuracy);
                ModifierNBT modifiers = toolStack.getModifiers();
                EntityModifierCapability.getCapability(proj).addModifiers(modifiers);
                ModDataNBT arrowData = PersistentDataCapability.getOrWarn(proj);
                opposite *= -1;
                ++counter;
                for (ModifierEntry entry : modifiers.getModifiers()) {
                    entry.getHook(ModifierHooks.PROJECTILE_LAUNCH)
                         .onProjectileLaunch(toolStack, entry, shooter, ItemStack.EMPTY, proj, null, arrowData, counter == projectiles.size() / 2);
                }
                world.addFreshEntity(proj);
            }
            ci.cancel();
        }
    }

    @Inject(method = "summonProjectiles(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;Lcom/hollingsworth/arsnouveau/api/spell/SpellStats;Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;)V", at = @At("HEAD"), cancellable = true)
    public void dreamtinker$summonProjectiles(Level world, BlockPos pos, LivingEntity shooter, SpellStats stats, SpellResolver resolver, CallbackInfo ci) {
        if (resolver instanceof ModifiableSpellResolver && resolver.spellContext.getCasterTool().getItem() instanceof ModifiableSpellBook &&
            isTinker(resolver.spellContext)){
            ToolStack toolStack = ToolStack.from(resolver.spellContext.getCasterTool());
            ArrayList<EntityProjectileSpell> projectiles = new ArrayList<>();
            EntityProjectileSpell projectileSpell = new EntityProjectileSpell(world, resolver);
            projectileSpell.setPos((double) pos.getX(), (double) (pos.getY() + 1), (double) pos.getZ());
            projectiles.add(projectileSpell);
            int numSplits = stats.getBuffCount(AugmentSplit.INSTANCE);

            for (int i = 1; i < numSplits + 1; ++i) {
                Direction offset = shooter.getDirection().getClockWise();
                if (i % 2 == 0){
                    offset = offset.getOpposite();
                }

                BlockPos projPos = pos.relative(offset, i);
                projPos = projPos.offset(0, 2, 0);
                EntityProjectileSpell spell = new EntityProjectileSpell(world, resolver);
                spell.setPos((double) projPos.getX(), (double) projPos.getY(), (double) projPos.getZ());
                projectiles.add(spell);
            }
            int counter = 0;

            for (EntityProjectileSpell proj : projectiles) {
                proj.setDeltaMovement(new Vec3((double) 0.0F, -0.1, (double) 0.0F));
                ModifierNBT modifiers = toolStack.getModifiers();
                EntityModifierCapability.getCapability(proj).addModifiers(modifiers);
                ModDataNBT arrowData = PersistentDataCapability.getOrWarn(proj);
                ++counter;
                for (ModifierEntry entry : modifiers.getModifiers()) {
                    entry.getHook(ModifierHooks.PROJECTILE_LAUNCH)
                         .onProjectileLaunch(toolStack, entry, shooter, ItemStack.EMPTY, proj, null, arrowData, counter == projectiles.size() / 2);
                }
                world.addFreshEntity(proj);
            }
            ci.cancel();
        }
    }
}

