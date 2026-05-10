package org.dreamtinker.dreamtinker.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.network.DNetwork;
import org.dreamtinker.dreamtinker.network.S2CVibeBarFx;

import java.util.List;
import java.util.function.Predicate;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.ProjLimit;

public class DTHelper {
    public static void clearProjectile(ServerLevel level, double px, double pz) {
        int viewDist = level.getServer().getPlayerList().getViewDistance();
        double radius = viewDist * 16.0;
        AABB box = new AABB(px - radius, level.getMinBuildHeight(), pz - radius, px + radius, level.getMaxBuildHeight(), pz + radius);
        Predicate<Projectile> all = p -> true;
        List<Projectile> list = level.getEntitiesOfClass(Projectile.class, box, all);
        if (ProjLimit.get() <= list.size())
            for (Projectile old : list)
                old.remove(Entity.RemovalReason.DISCARDED);
    }

    public static void debugEffects(List<MobEffect> effects) {
        for (MobEffect effect : effects) {
            ResourceLocation id = ForgeRegistries.MOB_EFFECTS.getKey(effect);
            String key = effect.getDescriptionId();
            System.out.println("Random effect → {" + id + "} ({" + key + "})");
        }
    }


    public static void sendVibeBarFx(
            ServerLevel level, LivingEntity attacker, LivingEntity target,
            int argb /*0xAARRGGBB*/) {
        Vec3 d = target.position().subtract(attacker.position());
        Vec3 flat = new Vec3(d.x, 0, d.z);
        if (flat.lengthSqr() < 1.0e-6)
            return;

        Vec3 attackDir = flat.normalize();
        Vec3 barDir = new Vec3(-attackDir.z, 0, attackDir.x); // 水平且垂直于 attacker->target

        int life = 8;          // 0.4s
        float amp = 0.05f;      // 抖幅
        float hz = 26.0f;       // 频率
        float yFrac = 0.60f;    // 位置（胸口）

        DNetwork.CHANNEL.send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> target),
                new S2CVibeBarFx(target.getId(), (float) barDir.x, (float) barDir.z, argb, life, amp, hz, yFrac)
        );
    }

    public static boolean containsTranslationKey(Component root, String targetKey) {
        if (root == null || targetKey == null){
            return false;
        }

        if (hasTranslationKey(root, targetKey)){
            return true;
        }

        ComponentContents contents = root.getContents();
        if (contents instanceof TranslatableContents translatable){
            for (Object arg : translatable.getArgs()) {
                if (arg instanceof Component argComponent && containsTranslationKey(argComponent, targetKey)){
                    return true;
                }
            }
        }

        for (Component sibling : root.getSiblings()) {
            if (containsTranslationKey(sibling, targetKey)){
                return true;
            }
        }

        return false;
    }

    private static boolean hasTranslationKey(Component component, String targetKey) {
        return component.getContents() instanceof TranslatableContents translatable
               && targetKey.equals(translatable.getKey());
    }

    public static double distanceToAABBSqr(AABB a, AABB b) {
        double dx = 0.0D;
        if (a.maxX < b.minX)
            dx = b.minX - a.maxX;
        else if (b.maxX < a.minX)
            dx = a.minX - b.maxX;

        double dy = 0.0D;
        if (a.maxY < b.minY)
            dy = b.minY - a.maxY;
        else if (b.maxY < a.minY)
            dy = a.minY - b.maxY;

        double dz = 0.0D;
        if (a.maxZ < b.minZ)
            dz = b.minZ - a.maxZ;
        else if (b.maxZ < a.minZ)
            dz = a.minZ - b.maxZ;

        return dx * dx + dy * dy + dz * dz;
    }

    public static float getPositiveAttributeBonus(LivingEntity entity, Attribute attribute) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null){
            return 0.0f;
        }

        double base = instance.getBaseValue();

        double addition = 0.0D;
        double multiplyBase = 0.0D;
        double multiplyTotalFactor = 1.0D;

        for (AttributeModifier modifier : instance.getModifiers()) {
            double amount = modifier.getAmount();

            // 只统计正向 modifier，忽略 debuff / 负数 modifier
            if (amount <= 0.0D){
                continue;
            }

            switch (modifier.getOperation()) {
                case ADDITION -> {
                    addition += amount;
                }
                case MULTIPLY_BASE -> {
                    multiplyBase += base * amount;
                }
                case MULTIPLY_TOTAL -> {
                    multiplyTotalFactor *= 1.0D + amount;
                }
            }
        }

        double valueBeforeTotalMultiplier = base + addition + multiplyBase;
        double positiveOnlyValue = valueBeforeTotalMultiplier * multiplyTotalFactor;

        return (float) Math.max(0.0D, positiveOnlyValue);
    }

    public static double getMultipartVolume(Entity target) {
        Entity root = target instanceof PartEntity<?> part ? part.getParent() : target;

        PartEntity<?>[] parts = root.getParts();
        if (parts == null){
            return getBoxVolume(root.getBoundingBox());
        }

        double volume = 0.0D;

        for (PartEntity<?> part : parts) {
            if (part != null){
                volume += getBoxVolume(part.getBoundingBox());
            }
        }

        return volume;
    }

    public static double getBoxVolume(AABB box) {
        return box.getXsize() * box.getYsize() * box.getZsize();
    }
}
