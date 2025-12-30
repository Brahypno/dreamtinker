package org.dreamtinker.dreamtinker.tools.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.dreamtinker.dreamtinker.utils.DirectionalResistanceExplosionDamageCalculator;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableArrowItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.entity.ModifiableArrow;

import java.util.List;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.*;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.ContinuousExplodeTimes;

public class TNTArrow extends ModifiableArrowItem {
    public static final ResourceLocation TAG_CONTINUOUS = Dreamtinker.getLocation("continuous_explode");
    public static final String TAG_TRIGGERED_ALREADY = Dreamtinker.getLocation("already_explode").toString();

    public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (TooltipKey.SHIFT == SafeClientAccess.getTooltipKey())
            tooltip = DTHelper.getMeleeStats(ToolStack.from(stack), tooltip);
        super.appendHoverText(stack, level, tooltip, flag);
        if (TooltipKey.SHIFT == SafeClientAccess.getTooltipKey()){
            int timeAllows = ModifierUtil.getModifierLevel(stack, DreamtinkerModifiers.Ids.continuous_explode) * ContinuousExplodeTimes.get();
            int currentTime = ModifierUtil.getPersistentInt(stack, TAG_CONTINUOUS, 0);
            if (currentTime < timeAllows)
                tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.continuous_explode").append(String.valueOf(timeAllows - currentTime))
                                     .withStyle(ChatFormatting.DARK_RED));
        }
    }

    public TNTArrow(Properties properties, ToolDefinition toolDefinition, int maxStackSize) {
        super(properties, toolDefinition);
    }

    public @NotNull AbstractArrow createArrow(Level world, ItemStack stack, LivingEntity shooter) {
        TNTArrowEntity arrow = new TNTArrowEntity(world, shooter);
        arrow.onCreate(stack, shooter);
        return arrow;
    }

    public static class TNTArrowEntity extends ModifiableArrow {
        public TNTArrowEntity(Level world, LivingEntity shooter) {
            super(world, shooter);
        }

        protected void hitEntity(Entity entity) {
            if (null == this.getOwner() || !(this.getOwner() instanceof LivingEntity) || null == entity)
                return;
            entity.setInvulnerable(false);
            entity.invulnerableTime = 0;
            ToolStack toolStack = ToolStack.from(this.getRawPickupItem());
            if (entity.getUUID() != this.getOwner().getUUID()){
                ToolAttackUtil.performAttack(toolStack,
                                             ToolAttackContext.attacker((LivingEntity) this.getOwner()).target(entity).cooldown(1).toolAttributes(toolStack)
                                                              .build());
                //ToolAttackUtil.attackEntity(toolStack, (LivingEntity) this.getOwner(), InteractionHand.OFF_HAND, entity, NO_COOLDOWN, false);

            }else {
                try {
                    ServerLevel serverLevel = (ServerLevel) this.level();
                    FakePlayer fakeAttacker = FakePlayerFactory.getMinecraft(serverLevel);
                    ToolAttackUtil.performAttack(toolStack,
                                                 ToolAttackContext.attacker(fakeAttacker).target(entity).cooldown(1).toolAttributes(toolStack)
                                                                  .build());
                }
                catch (SecurityException e) {
                    // 捕获异常，说明 FakePlayer 被禁用
                    ToolAttackUtil.performAttack(toolStack,
                                                 ToolAttackContext.attacker((LivingEntity) this.getOwner()).target(entity).cooldown(1).toolAttributes(toolStack)
                                                                  .build());
                }
                catch (Exception ignored) {
                }

            }
        }

        @Override
        protected void onHit(@NotNull HitResult result) {
            Vec3 impactVel = this.getDeltaMovement();
            super.onHit(result);
            if (!this.level().isClientSide){
                Vec3 hitPos = result.getLocation();
                if (this.getOwner() instanceof LivingEntity owner){//Nothing happened if no owner or not alive
                    if (this.getPersistentData().contains(TAG_TRIGGERED_ALREADY))
                        return;
                    boolean explosion = 0 < ModifierUtil.getModifierLevel(this.getRawPickupItem(), DreamtinkerModifiers.Ids.force_to_explosion);
                    if (explosion){
                        ToolStack ts = ToolStack.from(this.getRawPickupItem());
                        ToolAttackContext context = ToolAttackContext.attacker(owner).target(owner).cooldown(0).toolAttributes(ts).build();

                        float baseDamage = context.getBaseDamage();
                        float damage = baseDamage;
                        List<ModifierEntry> modifiers = ts.getModifierList();

                        for (ModifierEntry entry : modifiers) {
                            damage = (entry.getHook(ModifierHooks.MELEE_DAMAGE)).getMeleeDamage(ts, entry, context, baseDamage, damage);
                        }
                        double explosionPower =
                                Math.min(Math.sqrt(damage) * 2, ForceExplosionPower.get());
                        ExplosionDamageCalculator calc =
                                new DirectionalResistanceExplosionDamageCalculator(hitPos, impactVel, 35, 0.65f, 2.2f);
                        this.level().explode(
                                this.getOwner(),
                                DreamtinkerDamageTypes.source(this.level().registryAccess(), DreamtinkerDamageTypes.force_to_explosion, this, this.getOwner()),
                                calc,
                                hitPos.x, hitPos.y, hitPos.z,
                                (float) explosionPower, false,
                                Level.ExplosionInteraction.TNT
                        );
                    }else {
                        float sound = 2.0F;
                        // 查找半径内的实体
                        int hitRadius = TNT_ARROW_RADIUS.get();
                        List<Entity> nearbyEntities =
                                this.level()
                                    .getEntities(null, new AABB(hitPos.subtract(hitRadius, hitRadius, hitRadius), hitPos.add(hitRadius, hitRadius, hitRadius)));
                        // 遍历实体列表
                        for (Entity entity : nearbyEntities) {
                            if (entity instanceof LivingEntity livingEntity){
                                hitEntity(livingEntity);
                                sound++;
                            }
                        }
                        if (owner.position().distanceTo(hitPos) <= hitRadius){
                            hitEntity(owner);
                            sound++;
                        }
                        this.playSound(SoundEvents.GENERIC_EXPLODE, sound, (1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F) * 0.7F);
                    }
                    int timeAllows =
                            ModifierUtil.getModifierLevel(this.getRawPickupItem(), DreamtinkerModifiers.Ids.continuous_explode) * ContinuousExplodeTimes.get();
                    int currentTimes = ModifierUtil.getPersistentInt(this.getRawPickupItem(), TAG_CONTINUOUS, 0);
                    if (++currentTimes < timeAllows){
                        this.getPersistentData().putInt(String.valueOf(TAG_CONTINUOUS), currentTimes);
                        this.getPersistentData().putBoolean(TAG_TRIGGERED_ALREADY, true);
                    }else
                        this.discard();
                }
            }
        }

        public @NotNull ItemStack getRawPickupItem() {
            return super.getPickupItem();
        }

        @Override
        public @NotNull ItemStack getPickupItem() {
            ItemStack stack = super.getPickupItem();
            ToolStack ts = ToolStack.from(stack);
            int continous = this.getPersistentData().getInt(String.valueOf(TAG_CONTINUOUS));
            ts.getPersistentData().putInt(TAG_CONTINUOUS, continous);
            ts.updateStack(stack);
            return stack;
        }


        @Override
        public void tick() {
            super.tick();
            if (!this.isNoGravity() && ModifierUtil.getModifierLevel(this.getPickupItem(), DreamtinkerModifiers.Ids.force_to_explosion) <= 0){
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, TNT_ARROW_GRAVITY.get(), 0.0));
            }
        }
    }
}
