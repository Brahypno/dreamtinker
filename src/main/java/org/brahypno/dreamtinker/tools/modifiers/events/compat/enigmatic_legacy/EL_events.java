package org.brahypno.dreamtinker.tools.modifiers.events.compat.enigmatic_legacy;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.common.DreamtinkerTagKeys;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.dreamtinker.utils.CompatUtils.EnigmaticLegacyCompat;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import org.brahypno.esotericismtinker.utils.MessagesUtil;
import org.brahypno.esotericismtinker.utils.damage.DamageProbe;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.brahypno.dreamtinker.tools.modifiers.traits.Compat.enigmaticLegacy.EldritchPan.TAG_PAN;

public class EL_events {

    private static boolean addKillIfNotPresent(ItemStack pan, ResourceLocation mob) {
        List<ResourceLocation> kills = EnigmaticLegacyCompat.eldritchPanUniqueKills(pan);
        if (kills.size() < EnigmaticLegacyCompat.eldritchPanUniqueGainLimit() && !kills.contains(mob)){
            EnigmaticLegacyCompat.addEldritchPanUniqueKill(pan, mob);
            return true;
        }else {
            return false;
        }
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || event.isCanceled())
            return;
        if (entity instanceof Player player && EnigmaticLegacyCompat.isTheWorthyOne(player))
            if (2 <= ETModifierCheck.getMainhandModifierLevel(player, DreamtinkerModifiers.weapon_books.getId()))
                if (Math.random() <= EnigmaticLegacyCompat.infinitumUndeadProbabilityMultiplier()){
                    event.setCanceled(true);
                    player.setHealth(1);
                }
        if (event.getSource().getDirectEntity() instanceof ServerPlayer attacker){
            ItemStack weapon = attacker.getMainHandItem();

            if (0 < ETModifierCheck.getMainhandModifierLevel(attacker, DreamtinkerModifiers.eldritch_pan.getId())){
                ResourceLocation killedType = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType());

                if (addKillIfNotPresent(weapon, killedType)){
                    MessagesUtil.clientChat(Component.translatable("message.enigmaticlegacy.eldritch_pan_buff")
                                                     .withStyle(ChatFormatting.GOLD), false);
                    ToolStack toolstack = ToolStack.from(weapon);
                    ModDataNBT nbt = toolstack.getPersistentData();
                    nbt.putInt(TAG_PAN, EnigmaticLegacyCompat.eldritchPanKillCount(weapon));
                    toolstack.updateStack(weapon);
                }
            }
        }
    }

    private static final double DESOLATION_RANGE = 128.0D;
    private static final double DESOLATION_RANGE_SQ = DESOLATION_RANGE * DESOLATION_RANGE;
    private static final String TAG_DESOLATION_DROP_OWNER = "dreamtinker:desolation_drop_owner";
    private static final String TAG_DESOLATION_KILLING = "dreamtinker:desolation_killing";

    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)){
            return;
        }

        if (event.loadedFromDisk()){
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity entity)){
            return;
        }

        if (!isDesolationTarget(entity)){
            return;
        }

        List<ServerPlayer> attackers = findQualifiedDesolationPlayers(level, entity);

        if (attackers.isEmpty()){
            return;
        }

        for (ServerPlayer attacker : attackers) {
            if (!entity.isAlive() || entity.isRemoved()){
                break;
            }

            if (attacker.level() != level){
                continue;
            }

            if (attacker.distanceToSqr(entity) > DESOLATION_RANGE_SQ){
                continue;
            }

            if (!isQualifiedDesolationPlayer(attacker)){
                continue;
            }

            CompoundTag data = entity.getPersistentData();

            /*
             * 每次攻击前都覆盖掉落归属。
             * 如果这一下杀死实体，LivingDropsEvent 会读到当前 attacker。
             */
            data.putUUID(TAG_DESOLATION_DROP_OWNER, attacker.getUUID());
            data.putBoolean(TAG_DESOLATION_KILLING, true);

            entity.setHealth(1);
            entity.invulnerableTime = 0;

            float damage = 6.0F;
            boolean hurt = DamageProbe.damageHandler(entity, level.damageSources().playerAttack(attacker), damage);

            if (!hurt && entity.isAlive()){
                data.remove(TAG_DESOLATION_DROP_OWNER);
                data.remove(TAG_DESOLATION_KILLING);
            }
        }

        /*
         * 如果没有被这次荒芜伤害杀死，就清掉标记。
         * 如果被杀死，LivingDropsEvent 会在 hurt(...) 内部死亡流程中先触发，
         * 所以掉落事件仍然能读到这个标记。
         */
        if (entity.isAlive()){
            CompoundTag data = entity.getPersistentData();
            data.remove(TAG_DESOLATION_DROP_OWNER);
            data.remove(TAG_DESOLATION_KILLING);
        }
    }

    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.isRecentlyHit() && event.getSource() != null && event.getSource().getEntity() instanceof Player player &&
            EnigmaticLegacyCompat.isTheCursedOne((Player) event.getSource().getEntity())){
            LivingEntity killed = event.getEntity();

            if (killed instanceof EnderDragon && EnigmaticLegacyCompat.isTheWorthyOne(player)){
                setPersistentTag(player, "AbyssalHeartsGained", IntTag.valueOf(0));
            }
        }
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide()){
            return;
        }

        if (!(entity.level() instanceof ServerLevel level)){
            return;
        }

        CompoundTag data = entity.getPersistentData();

        if (!data.getBoolean(TAG_DESOLATION_KILLING)){
            return;
        }

        if (!data.hasUUID(TAG_DESOLATION_DROP_OWNER)){
            return;
        }

        UUID ownerId = data.getUUID(TAG_DESOLATION_DROP_OWNER);
        ServerPlayer owner = level.getServer().getPlayerList().getPlayer(ownerId);

        data.remove(TAG_DESOLATION_DROP_OWNER);
        data.remove(TAG_DESOLATION_KILLING);

        if (owner == null){
            return;
        }

        if (owner.level() != level){
            return;
        }

        RandomSource random = level.getRandom();

        for (ItemEntity drop : event.getDrops()) {
            double dx = (random.nextDouble() - 0.5D) * 1.2D;
            double dz = (random.nextDouble() - 0.5D) * 1.2D;

            drop.moveTo(
                    owner.getX() + dx,
                    owner.getY() + 0.25D,
                    owner.getZ() + dz,
                    drop.getYRot(),
                    drop.getXRot()
            );

            drop.setDeltaMovement(
                    (random.nextDouble() - 0.5D) * 0.05D,
                    0.12D,
                    (random.nextDouble() - 0.5D) * 0.05D
            );

            drop.setPickUpDelay(10);
        }
    }

    private static boolean isDesolationTarget(Entity entity) {
        return entity instanceof Piglin
               || entity instanceof ZombifiedPiglin
               || entity instanceof IronGolem
               || entity.getType().is(DreamtinkerTagKeys.EntityTypes.ENDER_ENTITY) && !entity.getType().is(Tags.EntityTypes.BOSSES);
    }

    private static boolean isQualifiedDesolationPlayer(ServerPlayer player) {
        return ETModifierCheck.haveModifierIn(player, DreamtinkerModifiers.desolation_ring.getId());
    }

    private static List<ServerPlayer> findQualifiedDesolationPlayers(ServerLevel level, Entity entity) {
        List<ServerPlayer> players = new ArrayList<>();

        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()){
                continue;
            }

            if (player.distanceToSqr(entity) > DESOLATION_RANGE_SQ){
                continue;
            }

            if (isQualifiedDesolationPlayer(player)){
                players.add(player);
            }
        }

        players.sort(Comparator.comparingDouble(player -> player.distanceToSqr(entity)));

        return players;
    }

    public static void setPersistentTag(Player player, String tag, Tag value) {
        CompoundTag data = player.getPersistentData();
        CompoundTag persistent;
        if (!data.contains("PlayerPersisted")){
            data.put("PlayerPersisted", persistent = new CompoundTag());
        }else {
            persistent = data.getCompound("PlayerPersisted");
        }

        persistent.put(tag, value);
    }
}
