package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.BurnInVainInAccuracy;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.BurnInVainRandomProj;
import static org.dreamtinker.dreamtinker.utils.DTHelper.placeProjectileOutsideShooter;

public class BurningInVain extends Modifier implements ProjectileLaunchModifierHook, InventoryTickModifierHook, ToolStatsModifierHook {
    Double maxInaccuracy = BurnInVainInAccuracy.get();


    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        float current_damage = builder.getStat(ToolStats.PROJECTILE_DAMAGE);
        float current_speed = builder.getStat(ToolStats.VELOCITY);
        float current_accuracy = builder.getStat(ToolStats.ACCURACY);
        float max = Math.max(Math.max(current_accuracy, current_damage), current_speed);
        ToolStats.PROJECTILE_DAMAGE.add(builder, max - current_damage);
        ToolStats.VELOCITY.add(builder, max - current_speed);
        ToolStats.ACCURACY.add(builder, max - current_accuracy);
    }

    private static final List<EntityType<? extends Projectile>> PROJECTILE_TYPES = new ArrayList<>();

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (shooter.level().isClientSide)
            return;

        ServerLevel world = (ServerLevel) shooter.level();
        DTHelper.clearProjectile(world, shooter.getX(), shooter.getZ());

        if (!BurnInVainRandomProj.get())
            return;

        Vec3 motion = projectile.getDeltaMovement();
        Vec3 dir = motion.lengthSqr() > 1.0E-6D ? motion.normalize() : shooter.getLookAngle().normalize();
        Vec3 spawnPos = getSafeProjectileSpawnPos(shooter, dir, projectile.getBbWidth(), projectile.getBbHeight());

        Projectile newProj = createRandomProjectile(world, shooter, spawnPos.x, spawnPos.y, spawnPos.z, motion);
        if (newProj == null)
            return;

        placeProjectileOutsideShooter(newProj, shooter, dir);
        if (newProj.getDeltaMovement().lengthSqr() <= 1.0E-6D)
            newProj.setDeltaMovement(motion);

        world.addFreshEntity(newProj);
    }

    private static Vec3 getSafeProjectileSpawnPos(LivingEntity shooter, Vec3 dir, double projectileWidth, double projectileHeight) {
        double distance = shooter.getBbWidth() * 0.5D + projectileWidth * 0.5D + 0.45D;
        Vec3 eye = shooter.getEyePosition();
        return eye.add(dir.scale(distance)).subtract(0.0D, projectileHeight * 0.5D, 0.0D);
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;
        if (!isCorrectSlot && !isSelected)
            return;
        holder.setSecondsOnFire(20);
    }

    @SuppressWarnings("unchecked")
    private Projectile createRandomProjectile(ServerLevel level, LivingEntity shooter, double px, double py, double pz, Vec3 motion) {
        // 第一次调用时，构建投射物类型列表
        if (PROJECTILE_TYPES.isEmpty()){
            for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES.getValues()) {
                try {
                    Entity test = type.create(level);
                    if (test instanceof Projectile){
                        //noinspection unchecked
                        PROJECTILE_TYPES.add((EntityType<? extends Projectile>) type);
                    }
                }
                catch (Exception e) {
                    // 某些实体类型可能不支持 create(world)，直接跳过
                }
            }
        }
        if (PROJECTILE_TYPES.isEmpty())
            return null;

        // 随机选一个投射物类型
        EntityType<? extends Projectile> chosenType = PROJECTILE_TYPES.get(level.getRandom().nextInt(PROJECTILE_TYPES.size()));
        Projectile newProj = chosenType.create(level);
        if (newProj == null)
            return null;
        newProj.setOwner(shooter);
        newProj.setPos(px, py, pz);

        float speed = (float) Math.sqrt(motion.x * motion.x + motion.y * motion.y + motion.z * motion.z);

        float inaccuracy = (float) (level.random.nextFloat() * maxInaccuracy);
        newProj.shoot(motion.x, motion.y, motion.z, speed, inaccuracy);
        if (newProj instanceof AbstractArrow)
            ((AbstractArrow) newProj).pickup = (AbstractArrow.Pickup.DISALLOWED);
        if (newProj instanceof WitherSkull)
            ((WitherSkull) newProj).setDangerous(level.random.nextFloat() < 0.15);
        return newProj;
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.INVENTORY_TICK, ModifierHooks.TOOL_STATS);
        super.registerHooks(hookBuilder);
    }
}
