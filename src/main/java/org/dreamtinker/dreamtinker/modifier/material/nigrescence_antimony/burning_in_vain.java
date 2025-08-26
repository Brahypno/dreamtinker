package org.dreamtinker.dreamtinker.modifier.material.nigrescence_antimony;

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
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.utils.DThelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.BurninVainInaccuracy;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.BurninVainRandomProj;

public class burning_in_vain extends BattleModifier {
    Double maxInaccuracy = BurninVainInaccuracy.get();
    Integer SummonRandomProj = BurninVainRandomProj.get();


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

        double px = shooter.getX(), pz = shooter.getZ();
        DThelper.clearProjectile(world, px, pz);
        if (!SummonRandomProj.equals(1))
            return;
        Vec3 motion = projectile.getDeltaMovement();
        Projectile newProj = createRandomProjectile(world, shooter, projectile.getX(), projectile.getY(), projectile.getZ(), motion);
        // 将新实体加入世界，移除原箭
        if (newProj != null)
            world.addFreshEntity(newProj);
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;
        if (!isCorrectSlot || !isSelected)
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

}
