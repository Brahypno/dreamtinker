package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.malum;

import com.sammy.malum.registry.common.DamageTypeRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.ArrowInterface;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.util.CombatHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.gadgets.entity.EFLNExplosion;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.utils.CustomExplosion;

import javax.annotation.Nullable;

import static slimeknights.tconstruct.library.modifiers.modules.combat.ProjectileExplosionModule.EFLN;

public class MalumMagicHit extends Modifier implements ArrowInterface {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.ArrowInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    @Override
    public boolean onProjectileHitsBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity owner) {
        return explode(modifier, projectile, persistentData, hit.getLocation());
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        return explode(modifier, projectile, persistentData, hit.getLocation());
    }

    @Override
    public void onProjectileFuseFinish(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow) {
        explode(modifier, projectile, persistentData, projectile.position());
    }

    private boolean explode(ModifierEntry modifier, Projectile projectile, ModDataNBT persistentData, Vec3 location) {
        float level = modifier.getEffectiveLevel();
        float radius = modifier.getLevel() * 2;
        if (radius > 0.5f && !projectile.getType().is(TinkerTags.EntityTypes.REUSABLE_AMMO)){
            Level world = projectile.level();
            if (!world.isClientSide){
                float power = ProjectileWithPower.getDamage(projectile);
                // figure out who to blame for the damage
                Entity cause = projectile.getOwner();
                DamageSource damageSource = CombatHelper.damageSource(DamageTypeRegistry.VOODOO, projectile, cause);

                // damage fishing rods, since they are supposed to damage on retrieve
                // if you need this for your custom projectile, let us know and we can dehardcode it
                ModifierUtil.updateFishingRod(projectile, 2 + 3 * modifier.getLevel(), true);

                // discard projectile so it doesn't explode again
                projectile.discard();

                // if marked, use EFLN style explosion
                // controlled by persistent data so another modifier can set this, we use fins
                CustomExplosion explosion;
                if (persistentData.getBoolean(EFLN)){
                    explosion = new EFLNExplosion(
                            world, location, radius, projectile,
                            power, damageSource, 1,
                            false, Explosion.BlockInteraction.KEEP
                    );
                }else {
                    explosion = new CustomExplosion(
                            world, location, radius, projectile, null,
                            power, damageSource, 1, null,
                            false, Explosion.BlockInteraction.KEEP
                    );
                }
                // cause the explosion
                explosion.handleServer();
            }
            return true;
        }
        return false;
    }
}
