package org.dreamtinker.dreamtinker.Entity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PlayMessages;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WiserLightBolt extends LightningBolt {
    private static final double CHAIN_RADIUS = 8.0D;
    private static final Map<Class<?>, Boolean> CUSTOM_THUNDER_HIT_CACHE = new ConcurrentHashMap<>();
    private final Set<UUID> chainHitEntities = new HashSet<>();
    @Nullable
    private UUID ownerUUID;
    private int chainCount = 0;
    private boolean chainDone = false;


    public WiserLightBolt(PlayMessages.SpawnEntity packet, Level world) {
        super(DreamtinkerEntityTypes.LIGHTNING_ENTITY.get(), world);
    }

    public WiserLightBolt(EntityType<? extends LightningBolt> type, Level level) {
        super(type, level);
    }

    private static boolean declaresThunderHitAboveEntity(Class<?> clazz) {
        for (Class<?> current = clazz; current != null && current != Entity.class; current = current.getSuperclass()) {
            for (Method method : current.getDeclaredMethods()) {
                Class<?>[] params = method.getParameterTypes();
                if (method.getReturnType() == Void.TYPE && params.length == 2 && params[0] == ServerLevel.class && params[1] == LightningBolt.class)
                    return true;
            }
        }
        return false;
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.ownerUUID = owner == null ? null : owner.getUUID();
        if (owner instanceof ServerPlayer player)
            this.setCause(player);
    }

    @Nullable
    public LivingEntity getOwnerLiving() {
        if (!(this.level() instanceof ServerLevel level) || this.ownerUUID == null)
            return null;
        Entity entity = level.getEntity(this.ownerUUID);
        return entity instanceof LivingEntity living ? living : null;
    }

    public void setChainCount(int chainCount) {
        this.chainCount = Math.max(0, chainCount);
    }

    private void realThunderHitAsOwner(Entity target, ServerLevel level, LightningBolt bolt, @Nullable LivingEntity owner) {
        if (target instanceof ItemEntity)
            return;

        if (target instanceof LivingEntity living && owner != null){
            living.setLastHurtByMob(owner);
            if (owner instanceof Player player)
                living.setLastHurtByPlayer(player);
        }

        if (this.hasCustomThunderHit(target)){
            target.thunderHit(level, bolt);
        }else {
            this.defaultThunderDamageAsOwner(target, level, bolt, owner);
        }
    }

    private boolean hasCustomThunderHit(Entity target) {
        return CUSTOM_THUNDER_HIT_CACHE.computeIfAbsent(target.getClass(), WiserLightBolt::declaresThunderHitAboveEntity);
    }

    private void defaultThunderDamageAsOwner(Entity target, ServerLevel level, LightningBolt bolt, @Nullable LivingEntity owner) {
        target.setRemainingFireTicks(target.getRemainingFireTicks() + 1);
        if (target.getRemainingFireTicks() == 0)
            target.setSecondsOnFire(8);

        Entity causingEntity = owner != null ? owner : bolt.getCause();
        target.hurt(DreamtinkerDamageTypes.source(level.registryAccess(), DamageTypes.LIGHTNING_BOLT, bolt, causingEntity), bolt.getDamage());
    }

    @Override
    public void tick() {
        baseTick();

        if (this.life == 2){
            if (this.level().isClientSide()){
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0F,
                                            0.8F + this.random.nextFloat() * 0.2F, false);
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F,
                                            0.5F + this.random.nextFloat() * 0.2F, false);
            }else {
                this.powerLightningRod();
                clearCopperOnLightningStrike(this.level(), this.getStrikePosition());
                this.gameEvent(GameEvent.LIGHTNING_STRIKE);
            }
        }

        --this.life;

        if (this.life < 0){
            if (this.flashes == 0){
                if (this.level() instanceof ServerLevel serverLevel){
                    List<Entity> list = this.level().getEntities(this,
                                                                 new AABB(this.getX() - 15.0D, this.getY() - 15.0D, this.getZ() - 15.0D, this.getX() + 15.0D,
                                                                          this.getY() + 6.0D + 15.0D, this.getZ() + 15.0D),
                                                                 entity -> entity.isAlive() && !(entity instanceof ItemEntity) &&
                                                                           !this.hitEntities.contains(entity));

                    for (ServerPlayer serverPlayer : serverLevel.getPlayers(player -> player.distanceTo(this) < 256.0F))
                        CriteriaTriggers.LIGHTNING_STRIKE.trigger(serverPlayer, this, list);
                }

                this.discard();
            }else if (this.life < -this.random.nextInt(10)){
                --this.flashes;
                this.life = 1;
                this.seed = this.random.nextLong();
            }
        }

        if (this.life >= 0){
            if (!(this.level() instanceof ServerLevel level)){
                this.level().setSkyFlashTime(2);
            }else if (!this.visualOnly){
                List<Entity> list1 = this.level().getEntities(this, new AABB(this.getX() - 3.0D, this.getY() - 3.0D, this.getZ() - 3.0D, this.getX() + 3.0D,
                                                                             this.getY() + 6.0D + 3.0D, this.getZ() + 3.0D),
                                                              entity -> entity.isAlive() && !(entity instanceof ItemEntity) &&
                                                                        !this.hitEntities.contains(entity));

                LivingEntity owner = this.getOwnerLiving();

                for (Entity entity : list1) {
                    if (!ForgeEventFactory.onEntityStruckByLightning(entity, this))
                        this.realThunderHitAsOwner(entity, level, this, owner);
                }

                List<Entity> chainVictims = this.tryChainLightning(list1);
                this.hitEntities.addAll(list1);
                this.hitEntities.addAll(chainVictims);

                if (this.getCause() != null){
                    List<Entity> triggerList = new ArrayList<>(list1);
                    triggerList.addAll(chainVictims);
                    CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.getCause(), triggerList);
                }
            }
        }
    }

    private static final EntityDataAccessor<CompoundTag> CHAIN_ARCS = SynchedEntityData.defineId(WiserLightBolt.class, EntityDataSerializers.COMPOUND_TAG);

    @Nullable
    private Entity findNextChainTarget(ServerLevel level, Entity from, @Nullable LivingEntity owner) {
        List<Entity> targets = level.getEntities(this, from.getBoundingBox().inflate(CHAIN_RADIUS),
                                                 target -> target.isAlive() && target != from && !(target instanceof ItemEntity) &&
                                                           !this.hitEntities.contains(target) && !this.chainHitEntities.contains(target.getUUID()) &&
                                                           this.isChainTarget(owner, target));
        targets.sort(Comparator.comparingDouble(target -> target.distanceToSqr(from)));
        return targets.isEmpty() ? null : targets.get(0);
    }

    private boolean isChainTarget(@Nullable LivingEntity owner, Entity target) {
        if (target instanceof ItemEntity)
            return false;

        if (!(target instanceof LivingEntity living))
            return true;

        if (living.isSpectator())
            return false;

        if (owner == null)
            return living instanceof Enemy;

        if (living == owner || owner.isAlliedTo(living) || living.isAlliedTo(owner))
            return false;

        if (owner instanceof Player ownerPlayer && living instanceof Player targetPlayer && !ownerPlayer.canHarmPlayer(targetPlayer))
            return false;

        return owner.canAttack(living);
    }

    private List<Entity> tryChainLightning(List<Entity> roots) {
        if (this.chainDone || this.chainCount <= 0 || !(this.level() instanceof ServerLevel level) || roots.isEmpty())
            return List.of();

        this.chainDone = true;
        this.chainHitEntities.clear();

        ArrayDeque<ChainNode> queue = new ArrayDeque<>();
        List<Entity> victims = new ArrayList<>();

        for (Entity root : roots) {
            if (root.isAlive() && !(root instanceof ItemEntity)){
                this.chainHitEntities.add(root.getUUID());
                queue.addLast(new ChainNode(root, 0));
            }
        }

        int remaining = this.chainCount;
        LivingEntity owner = this.getOwnerLiving();

        while (!queue.isEmpty() && remaining > 0) {
            ChainNode node = queue.removeFirst();

            if (node.depth >= this.chainCount)
                continue;

            Entity target = this.findNextChainTarget(level, node.from, owner);
            if (target == null)
                continue;

            this.chainHitEntities.add(target.getUUID());
            this.addChainArc(node.from, target);

            if (!ForgeEventFactory.onEntityStruckByLightning(target, this)){
                this.realThunderHitAsOwner(target, level, this, owner);
                victims.add(target);
                remaining--;

                if (target.isAlive())
                    queue.addLast(new ChainNode(target, node.depth + 1));
            }
        }

        return victims;
    }

    private void addChainArc(Entity from, Entity to) {
        CompoundTag tag = this.entityData.get(CHAIN_ARCS).copy();
        int count = tag.getInt("Count");
        Vec3 origin = this.position();
        Vec3 start = from.getBoundingBox().getCenter().subtract(origin);
        Vec3 end = to.getBoundingBox().getCenter().subtract(origin);

        CompoundTag arc = new CompoundTag();
        arc.putDouble("FX", start.x);
        arc.putDouble("FY", start.y);
        arc.putDouble("FZ", start.z);
        arc.putDouble("TX", end.x);
        arc.putDouble("TY", end.y);
        arc.putDouble("TZ", end.z);

        tag.put("Arc" + count, arc);
        tag.putInt("Count", count + 1);
        this.entityData.set(CHAIN_ARCS, tag);
    }

    public List<ChainArc> getChainArcs() {
        CompoundTag tag = this.entityData.get(CHAIN_ARCS);
        List<ChainArc> arcs = new ArrayList<>();
        int count = tag.getInt("Count");

        for (int i = 0; i < count; i++) {
            CompoundTag arc = tag.getCompound("Arc" + i);
            Vec3 from = new Vec3(arc.getDouble("FX"), arc.getDouble("FY"), arc.getDouble("FZ"));
            Vec3 to = new Vec3(arc.getDouble("TX"), arc.getDouble("TY"), arc.getDouble("TZ"));
            arcs.add(new ChainArc(from, to));
        }

        return arcs;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHAIN_ARCS, new CompoundTag());
    }

    public record ChainArc(Vec3 from, Vec3 to) {}

    private record ChainNode(Entity from, int depth) {
    }
}