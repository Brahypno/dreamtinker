package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.legendary_monsters;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerDamageTypes;
import org.brahypno.dreamtinker.network.DNetwork;
import org.brahypno.dreamtinker.network.S2CDinosaurShockwavePacket;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierRequirementsModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.data.ModifierIds;

import java.util.List;

/**
 * Legendary Monsters item abilities adapted to modifiable tools without loading optional-mod classes.
 */
public final class LegendaryMonstersCompatModifiers {
    private static final String MODID = "legendary_monsters";
    private static final ResourceLocation BLEEDING = new ResourceLocation(MODID, "bleeding");
    private static final ResourceLocation STUN = new ResourceLocation(MODID, "stun");
    private static final ResourceLocation ANNIHILATION = new ResourceLocation(MODID, "annihilation");

    private LegendaryMonstersCompatModifiers() {}

    private static MobEffect effect(ResourceLocation id) {
        return ForgeRegistries.MOB_EFFECTS.getValue(id);
    }

    private static boolean validTarget(Player player, LivingEntity target) {
        return target != player && target.isAlive() && !target.isInvulnerable() && !player.isAlliedTo(target)
               && (!(target instanceof Player other) || (!other.isCreative() && !other.isSpectator() && player.canHarmPlayer(other)));
    }

    private static List<LivingEntity> targets(Player player, double radius) {
        double radiusSqr = radius * radius;
        return player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(radius),
                target -> validTarget(player, target) && player.distanceToSqr(target) <= radiusSqr);
    }

    private static boolean inFront(Player player, LivingEntity target, double minimumDot) {
        Vec3 direction = target.getEyePosition().subtract(player.getEyePosition()).normalize();
        return player.getLookAngle().normalize().dot(direction) >= minimumDot && player.hasLineOfSight(target);
    }

    private static void cooldown(Player player, InteractionHand hand, int ticks) {
        player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), ticks);
    }

    private abstract static class ImmediateActiveModifier extends NoLevelsModifier implements GeneralInteractionModifierHook {
        private final int cooldown;

        protected ImmediateActiveModifier(int cooldown) {
            this.cooldown = cooldown;
        }

        @Override
        protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT);
            super.registerHooks(hookBuilder);
        }

        @Override
        public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
            ItemStack stack = player.getItemInHand(hand);
            if (source != InteractionSource.RIGHT_CLICK || tool.isBroken() || player.getCooldowns().isOnCooldown(stack.getItem())){
                return InteractionResult.PASS;
            }
            if (!player.level().isClientSide && !canActivate(player)){
                return InteractionResult.PASS;
            }
            if (!player.level().isClientSide){
                activate(player, hand);
                cooldown(player, hand, cooldown);
            }
            return InteractionResult.sidedSuccess(player.level().isClientSide);
        }

        protected boolean canActivate(Player player) {
            return true;
        }

        protected abstract void activate(Player player, InteractionHand hand);
    }

    public static class AncientCounterspike extends NoLevelsModifier implements OnAttackedModifierHook {
        private static final ThreadLocal<Boolean> REFLECTING = ThreadLocal.withInitial(() -> false);

        @Override
        protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
            super.registerHooks(hookBuilder);
        }

        @Override
        public void onAttacked(
                IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source,
                float amount, boolean isDirectDamage) {
            LivingEntity holder = context.getEntity();
            if (REFLECTING.get() || !holder.isBlocking() || !(source.getEntity() instanceof LivingEntity attacker))
                return;
            REFLECTING.set(true);
            try {
                attacker.hurt(holder.damageSources().thorns(holder), 5.0f + amount * 0.25f);
                MobEffect bleeding = effect(BLEEDING);
                if (bleeding != null && holder.getRandom().nextFloat() < 0.50f){
                    attacker.addEffect(new MobEffectInstance(bleeding, 100, 0));
                }
            }
            finally {
                REFLECTING.set(false);
            }
        }
    }

    public static class MossySymbiosis extends ImmediateActiveModifier implements OnAttackedModifierHook {
        private static final String COOLDOWN_KEY = "dreamtinker_mossy_symbiosis_until";

        public MossySymbiosis() {
            super(100);
        }

        @Override
        protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
            super.registerHooks(hookBuilder);
            hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
        }

        @Override
        protected void activate(Player player, InteractionHand hand) {
            player.getPersistentData().putLong(COOLDOWN_KEY, player.level().getGameTime() + 100L);
            poisonCloud(player);
        }

        @Override
        protected boolean canActivate(Player player) {
            return player.getPersistentData().getLong(COOLDOWN_KEY) <= player.level().getGameTime();
        }

        @Override
        public void onAttacked(
                IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source,
                float amount, boolean isDirectDamage) {
            LivingEntity holder = context.getEntity();
            if (slotType != EquipmentSlot.CHEST || !(holder instanceof Player player) || player.level().isClientSide
                || !isDirectDamage || source.getEntity() == null)
                return;
            long now = player.level().getGameTime();
            if (player.getPersistentData().getLong(COOLDOWN_KEY) > now)
                return;
            player.getPersistentData().putLong(COOLDOWN_KEY, now + 100);
            poisonCloud(player);
        }

        private static void poisonCloud(Player player) {
            boolean hit = false;
            for (LivingEntity target : targets(player, 4.0)) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
                target.hurt(player.damageSources().playerAttack(player), 4.0f);
                hit = true;
            }
            if (hit)
                player.heal(4.0f);
            if (player.level() instanceof ServerLevel server){
                server.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, player.getX(), player.getY() + 1, player.getZ(), 40, 2, 1, 2, 0.02);
            }
        }
    }

    public static class AtmosphericLeap extends NoLevelsModifier implements ModifyDamageModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
            super.registerHooks(hookBuilder);
        }

        @Override
        public float modifyDamageTaken(
                IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType,
                DamageSource source, float amount, boolean isDirectDamage) {
            return slotType == EquipmentSlot.FEET && source.is(DamageTypes.FALL) ? amount * 0.5f : amount;
        }

        public static boolean tryActivate(ServerPlayer player) {
            if (!player.isShiftKeyDown())
                return false;
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            if (boots.isEmpty() || !boots.is(TinkerTags.Items.BOOTS) || player.getCooldowns().isOnCooldown(boots.getItem()))
                return false;
            ToolStack tool = ToolStack.from(boots);
            if (tool.isBroken() || tool.getModifierLevel(DreamtinkerModifiers.atmospheric_leap.getId()) < 1)
                return false;
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x, Math.max(0.85, motion.y + 0.65), motion.z);
            player.hurtMarked = true;
            player.fallDistance = 0;
            player.getCooldowns().addCooldown(boots.getItem(), 60);
            if (player.level() instanceof ServerLevel server){
                server.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 24, 0.5, 0.15, 0.5, 0.08);
                server.playSound(null, player.blockPosition(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 0.7f, 1.6f);
            }
            return true;
        }
    }

    public static class LightningStrike extends ImmediateActiveModifier {
        public LightningStrike() {
            super(100);
        }

        @Override
        protected void activate(Player player, InteractionHand hand) {
            LivingEntity target = targets(player, 16).stream().filter(entity -> inFront(player, entity, 0.94)).min((a, b) ->
                                                                                                                           Double.compare(
                                                                                                                                   player.distanceToSqr(a),
                                                                                                                                   player.distanceToSqr(b)))
                                                     .orElse(null);
            if (target != null && player.level() instanceof ServerLevel server){
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(server);
                if (bolt != null){
                    bolt.moveTo(target.position());
                    if (player instanceof ServerPlayer serverPlayer)
                        bolt.setCause(serverPlayer);
                    server.addFreshEntity(bolt);
                }
            }
        }
    }

    public static class ChorusBlink extends ImmediateActiveModifier {
        public ChorusBlink() {
            super(60);
        }

        @Override
        protected void activate(Player player, InteractionHand hand) {
            Level level = player.level();
            for (int i = 0; i < 16; i++) {
                double x = player.getX() + (player.getRandom().nextDouble() - 0.5) * 16.0;
                double y = player.getY() + player.getRandom().nextInt(9) - 4;
                double z = player.getZ() + (player.getRandom().nextDouble() - 0.5) * 16.0;
                if (player.randomTeleport(x, y, z, true)){
                    level.playSound(null, player.blockPosition(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1, 1);
                    return;
                }
            }
        }
    }

    public static class FieryBreath extends ImmediateActiveModifier {
        public FieryBreath() {
            super(80);
        }

        @Override
        protected void activate(Player player, InteractionHand hand) {
            Vec3 center = player.position();
            for (LivingEntity target : targets(player, 5.0)) {
                if (!inFront(player, target, 0.55))
                    continue;
                target.setSecondsOnFire(5);
                target.hurt(DreamtinkerDamageTypes.source(
                        player.level().registryAccess(), DamageTypes.ON_FIRE, player, player), 6.0f);
                Vec3 push = target.position().subtract(center).normalize().scale(1.2);
                target.push(push.x, 0.25, push.z);
                target.hurtMarked = true;
            }
            if (player.level() instanceof ServerLevel server){
                Vec3 look = player.getLookAngle();
                for (int i = 1; i <= 5; i++) {
                    Vec3 point = player.getEyePosition().add(look.scale(i));
                    server.sendParticles(ParticleTypes.FLAME, point.x, point.y, point.z, 10, 0.35, 0.35, 0.35, 0.03);
                }
                server.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1, 0.8f);
            }
        }
    }

    /**
     * Restores the Dinosaur Bone Club's block-targeted circular shockwave.
     */
    public static class DinosaurBoneShockwave extends ImmediateActiveModifier {
        public DinosaurBoneShockwave() {
            super(70);
        }

        @Override
        public InteractionResult onToolUse(
                IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand,
                InteractionSource source) {
            HitResult hit = player.pick(6.0, 0, false);
            if (hit.getType() != HitResult.Type.BLOCK)
                return InteractionResult.PASS;
            return super.onToolUse(tool, modifier, player, hand, source);
        }

        @Override
        protected void activate(Player player, InteractionHand hand) {
            HitResult hit = player.pick(6.0, 0, false);
            if (!(hit instanceof BlockHitResult blockHit))
                return;
            BlockPos pos = blockHit.getBlockPos();
            Vec3 center = Vec3.atCenterOf(pos).add(0, 0.6, 0);
            AABB area = new AABB(center, center).inflate(4.5, 2.0, 4.5);
            for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, area,
                                                                         entity -> validTarget(player, entity)
                                                                                   && horizontalDistanceSqr(center, entity.position()) <= 4.5 * 4.5)) {
                target.hurt(player.damageSources().playerAttack(player), 7.0f);
                Vec3 push = target.position().subtract(center);
                if (push.lengthSqr() > 0.001){
                    push = push.normalize().scale(0.65);
                    target.push(push.x, 0.35, push.z);
                    target.hurtMarked = true;
                }
            }
            if (player.level() instanceof ServerLevel server){
                BlockState state = server.getBlockState(pos);
                DNetwork.CHANNEL.send(
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                        new S2CDinosaurShockwavePacket(center.x, center.y, center.z, state, 3, 4.5f));
                server.playSound(null, pos, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.PLAYERS, 1.0f, 0.8f);
            }
        }
    }

    public static class AnnihilationBurst extends ImmediateActiveModifier {
        public AnnihilationBurst() {
            super(120);
        }

        @Override
        protected void activate(Player player, InteractionHand hand) {
            Vec3 eyePosition = player.getEyePosition();
            Vec3 intendedImpact = eyePosition.add(player.getLookAngle().scale(9));
            BlockHitResult blockHit = player.level().clip(new ClipContext(
                    eyePosition, intendedImpact, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            Vec3 impact = blockHit.getType() == HitResult.Type.MISS ? intendedImpact : blockHit.getLocation();
            AABB area = new AABB(impact, impact).inflate(2.5);
            for (LivingEntity target : player.level().getEntitiesOfClass(
                    LivingEntity.class, area,
                    entity -> validTarget(player, entity)
                              && entity.distanceToSqr(impact) <= 2.5 * 2.5
                              && player.hasLineOfSight(entity))) {
                target.hurt(DreamtinkerDamageTypes.source(
                        player.level().registryAccess(), DamageTypes.MAGIC, player, player), 8.0f);
                MobEffect annihilation = effect(ANNIHILATION);
                if (annihilation != null)
                    target.addEffect(new MobEffectInstance(annihilation, 60, 0));
            }
            if (player.level() instanceof ServerLevel server){
                server.sendParticles(ParticleTypes.REVERSE_PORTAL, impact.x, impact.y, impact.z, 80, 1.2, 1.2, 1.2, 0.12);
                server.sendParticles(ParticleTypes.EXPLOSION, impact.x, impact.y, impact.z, 3, 0.4, 0.4, 0.4, 0);
                server.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.9f, 0.6f);
            }
        }
    }

    private abstract static class ChargedActiveModifier extends NoLevelsModifier implements GeneralInteractionModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT);
            super.registerHooks(hookBuilder);
        }

        @Override
        public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
            ItemStack stack = player.getItemInHand(hand);
            if (source != InteractionSource.RIGHT_CLICK || tool.isBroken() || player.getCooldowns().isOnCooldown(stack.getItem())){
                return InteractionResult.PASS;
            }
            GeneralInteractionModifierHook.startUsingWithDrawtime(tool, modifier.getId(), player, hand, 0.75f);
            return InteractionResult.CONSUME;
        }

        @Override
        public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
            return 72000;
        }

        @Override
        public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
            return UseAnim.BOW;
        }
    }

    public static class WitheringCharge extends ChargedActiveModifier {
        @Override
        public void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
            if (!(entity instanceof Player player) || entity.level().isClientSide)
                return;
            int chargeTime = getUseDuration(tool, modifier) - timeLeft;
            if (GeneralInteractionModifierHook.getToolCharge(tool, chargeTime) < 0.60f)
                return;
            Vec3 look = player.getLookAngle().normalize();
            player.setDeltaMovement(player.getDeltaMovement().add(look.scale(1.6)).add(0, 0.15, 0));
            player.hurtMarked = true;
            for (LivingEntity target : targets(player, 4.0)) {
                if (inFront(player, target, 0.35)){
                    target.hurt(player.damageSources().playerAttack(player), 6.0f);
                    target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
                }
            }
            cooldown(player, player.getUsedItemHand(), 80);
        }
    }

    /**
     * Restores the Monstrous Anchor's charged launch/stun and fully charged sweeping hit.
     */
    public static class MonstrousAnchorForce extends ChargedActiveModifier implements MeleeHitModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
            super.registerHooks(hookBuilder);
            hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        }

        @Override
        public void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
            if (!(entity instanceof Player player) || entity.level().isClientSide)
                return;
            int chargeTime = getUseDuration(tool, modifier) - timeLeft;
            if (chargeTime < 15)
                return;
            player.setDeltaMovement(player.getDeltaMovement().x, 0.6, player.getDeltaMovement().z);
            player.hurtMarked = true;
            player.fallDistance = 0;
            MobEffect stun = effect(STUN);
            for (LivingEntity target : targets(player, 3.0)) {
                if (stun != null)
                    target.addEffect(new MobEffectInstance(stun, 60, 0));
                else
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 6));
            }
            cooldown(player, player.getUsedItemHand(), 100);
            if (player.level() instanceof ServerLevel server){
                server.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 48, 2.0, 0.2, 2.0, 0.08);
                server.playSound(null, player.blockPosition(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1.0f, 0.8f);
            }
        }

        @Override
        public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
            if (!(context.getAttacker() instanceof Player player) || context.getLevel().isClientSide
                || !context.isFullyCharged() || player.fallDistance != 0)
                return;
            Vec3 center = context.getTarget().position();
            AABB area = context.getTarget().getBoundingBox().inflate(3.0);
            for (LivingEntity target : context.getLevel().getEntitiesOfClass(LivingEntity.class, area,
                                                                             entity -> validTarget(player, entity)
                                                                                       && entity.distanceToSqr(center) <= 3.0 * 3.0)) {
                target.hurt(player.damageSources().playerAttack(player), 6.0f);
            }
            if (context.getLevel() instanceof ServerLevel server){
                server.sendParticles(ParticleTypes.SWEEP_ATTACK, center.x, center.y + 1, center.z, 12, 1.5, 0.5, 1.5, 0);
            }
        }
    }

    public static class MonstrousShock extends ChargedActiveModifier implements InventoryTickModifierHook {
        private static final ResourceLocation ARMED_AT = Dreamtinker.getLocation("monstrous_shock_armed_at");

        @Override
        protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
            super.registerHooks(hookBuilder);
            hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
            hookBuilder.addModule(ModifierRequirementsModule.builder().requireModifier(ModifierIds.drillAttack, 1)
                                                            .modifierKey(DreamtinkerModifiers.monstrous_shock.getId()).build());
        }

        @Override
        public void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
            if (!(entity instanceof Player player) || entity.level().isClientSide)
                return;
            int chargeTime = getUseDuration(tool, modifier) - timeLeft;
            if (GeneralInteractionModifierHook.getToolCharge(tool, chargeTime) < 0.60f)
                return;
            tool.getPersistentData().put(ARMED_AT, LongTag.valueOf(entity.level().getGameTime() + 1L));
            cooldown(player, player.getUsedItemHand(), 100);
            player.setDeltaMovement(player.getDeltaMovement().x, 1.0, player.getDeltaMovement().z);
            player.hurtMarked = true;
            player.fallDistance = 0;
        }

        @Override
        public void onInventoryTick(
                IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected,
                boolean isCorrectSlot, ItemStack stack) {
            if (!(holder instanceof Player player) || world.isClientSide)
                return;
            Tag armedAtTag = tool.getPersistentData().get(ARMED_AT);
            long armedAt = armedAtTag instanceof LongTag longTag ? longTag.getAsLong() : 0L;
            if (armedAt <= 0)
                return;
            long elapsed = world.getGameTime() - (armedAt - 1L);
            if (elapsed > 100){
                tool.getPersistentData().remove(ARMED_AT);
                return;
            }
            if (elapsed < 5 || !player.onGround())
                return;
            tool.getPersistentData().remove(ARMED_AT);
            MobEffect stun = effect(STUN);
            for (LivingEntity target : targets(player, 4.0)) {
                target.hurt(player.damageSources().playerAttack(player), 4.0f);
                if (stun != null)
                    target.addEffect(new MobEffectInstance(stun, 60, 0));
                else
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 6));
            }
            if (world instanceof ServerLevel server){
                server.sendParticles(ParticleTypes.EXPLOSION, player.getX(), player.getY(), player.getZ(), 5, 2, 0.2, 2, 0);
                server.playSound(null, player.blockPosition(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1, 0.7f);
            }
        }
    }

    private static double horizontalDistanceSqr(Vec3 first, Vec3 second) {
        double x = first.x - second.x;
        double z = first.z - second.z;
        return x * x + z * z;
    }
}
