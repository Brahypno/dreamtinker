package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.lupus_antimony;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.*;

public class TheWolfWonder extends Modifier implements ProjectileHitModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook {
    private static final int minDuration = TheWolfWonderEffectMinTime.get() * 20;
    private static final int maxDuration = TheWolfWonderEffectMaxTime.get() * 20;
    private static final Set<ResourceLocation> BAD_CACHE = new HashSet<>();
    private static final Set<ResourceLocation> CONFIG_BLACKLIST = new HashSet<>();
    private static boolean Blacklist_inited = false;
    private static List<MobEffect> negatives = List.of();


    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        onMonsterMeleeHit(tool, modifier, context, damage);
        return knockback;
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        LivingEntity target = context.getLivingTarget();
        if (target == null)
            return;
        long types = target.getActiveEffects().stream().filter(e -> e.getEffect().getCategory() == MobEffectCategory.HARMFUL).count();
        if (types < (long) TheWolfWonderEffectNum.get() * modifier.getLevel())
            applyRandomEffects(target, context.getAttacker(), 1 < tool.getModifierLevel(DreamtinkerModifiers.despair_mist.getId()), 1);
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (target == null)
            return false;
        long types = target.getActiveEffects().stream().filter(e -> e.getEffect().getCategory() == MobEffectCategory.HARMFUL).count();
        if (types < (long) TheWolfWonderEffectNum.get() * modifier.getLevel())
            applyRandomEffects(target, attacker, 1 < modifiers.getLevel(DreamtinkerModifiers.despair_mist.getId()), 1);
        return false;
    }

    private void applyRandomEffects(LivingEntity target, LivingEntity attacker, boolean no_repeat, int mutiply) {
        RandomSource rand = target.getRandom();
        if (negatives.isEmpty())
            negatives = ForgeRegistries.MOB_EFFECTS.getValues().stream()
                                                   .filter(TheWolfWonder::filterMobEffects)
                                                   .collect(Collectors.toList());
        if (negatives.isEmpty())
            return;

        for (int i = negatives.size() - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            Collections.swap(negatives, i, j);
        }

        List<MobEffectInstance> selected_effects =
                new java.util.ArrayList<>(target.getActiveEffects().stream().filter(e -> e.getEffect().getCategory() == MobEffectCategory.HARMFUL).toList());
        for (MobEffect effect : negatives) {
            if (TheWolfWonderEffectNum.get() * mutiply <= selected_effects.size())
                break;
            if (no_repeat && target.hasEffect(effect))
                continue;
            // 随机持续时长与等级
            int duration = minDuration + rand.nextInt(Math.max(1, maxDuration - minDuration + 1));
            int amplifier = rand.nextInt(TheWolfWonderEffectAmplifier.get());
            try {
                MobEffectInstance inst = new MobEffectInstance(effect, duration, amplifier, false, true);
                DTForcedEffectKeys.forceAddAndRecordKey(
                        target,
                        inst,
                        attacker
                );
                selected_effects.add(inst);
            }
            catch (Throwable t) {
                // 该效果在当前环境不安全：记入 BAD_CACHE，继续尝试下一个
                BAD_CACHE.add(ForgeRegistries.MOB_EFFECTS.getKey(effect));
                LogUtils.getLogger().warn("Random harmful effect {} failed on {} ({}). Blacklisting for this session.",
                                          ForgeRegistries.MOB_EFFECTS.getKey(effect), target.getType(), target.level().dimension().location(), t);
            }


        }
        if (attacker != null && rand.nextInt(66666) < TheWolfWonderSurpriseNumber.get()){
            for (MobEffectInstance inst : selected_effects) {
                attacker.addEffect(new MobEffectInstance(inst.getEffect(), inst.getDuration(), inst.getAmplifier(), inst.isAmbient(), inst.isVisible()));
            }
        }
    }

    public static void loadConfigBlacklist(List<? extends String> ids) {
        CONFIG_BLACKLIST.clear();
        for (String s : ids) {
            try {CONFIG_BLACKLIST.add(new ResourceLocation(s));} catch (Exception ignored) {}
        }
    }

    public static boolean filterMobEffects(MobEffect effect) {
        if (!Blacklist_inited){
            Blacklist_inited = true;
            loadConfigBlacklist(TheWolfBlackList.get());
        }
        if (effect.getCategory() != MobEffectCategory.HARMFUL)
            return false;
        if (TheWolfWonderPotionEffectOnly.get() && !isPotionEffectCached(effect))
            return false;

        ResourceLocation key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        return null != key && !BAD_CACHE.contains(key) &&
               !CONFIG_BLACKLIST.contains(key) && !key.getPath().contains("test") && !key.getPath().contains("ceshi");//exclude testing effect as well
    }

    private static final java.util.Set<MobEffect> BrewAble =
            ForgeRegistries.POTIONS.getValues().stream()
                                   .flatMap(p -> p.getEffects().stream())
                                   .map(MobEffectInstance::getEffect)
                                   .collect(java.util.stream.Collectors.toUnmodifiableSet());

    public static boolean isPotionEffectCached(MobEffect effect) {
        return BrewAble.contains(effect);
    }

    public static class DTForcedEffectKeys {
        private static final String ROOT = "dreamtinker_forced_effect_keys";

        public static boolean forceAddAndRecordKey(LivingEntity target, MobEffectInstance instance, @Nullable Entity source) {
            if (target == null || instance == null)
                return false;

            try {
                target.forceAddEffect(instance, source);
            }
            catch (Throwable ignored) {
                try {
                    target.addEffect(instance, source);
                }
                catch (Throwable ignored2) {}
            }

            return recordKey(target, instance.getEffect());
        }

        public static boolean recordKey(LivingEntity target, MobEffect effect) {
            if (target == null || effect == null)
                return false;

            ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(effect);
            if (id == null)
                return false;

            CompoundTag root = target.getPersistentData();
            CompoundTag keys = root.getCompound(ROOT);

            // 只存 key，值随便给一个 byte，占位即可
            keys.putBoolean(id.toString(), true);

            root.put(ROOT, keys);
            return true;
        }

        public static boolean hasKey(LivingEntity target, MobEffect effect) {
            if (target == null || effect == null)
                return false;

            ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(effect);
            if (id == null)
                return false;

            return target.getPersistentData().getCompound(ROOT).contains(id.toString());
        }

        public static boolean hasKey(LivingEntity target, ResourceLocation id) {
            if (target == null || id == null)
                return false;
            return target.getPersistentData().getCompound(ROOT).contains(id.toString());
        }

        public static boolean removeKey(LivingEntity target, MobEffect effect) {
            if (target == null || effect == null)
                return false;

            ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(effect);
            if (id == null)
                return false;

            return removeKey(target, id);
        }

        public static boolean removeKey(LivingEntity target, ResourceLocation id) {
            if (target == null || id == null)
                return false;

            CompoundTag root = target.getPersistentData();
            CompoundTag keys = root.getCompound(ROOT);
            String key = id.toString();

            if (!keys.contains(key))
                return false;

            keys.remove(key);

            if (keys.isEmpty())
                root.remove(ROOT);
            else
                root.put(ROOT, keys);

            return true;
        }

        public static void clearKeys(LivingEntity target) {
            if (target == null)
                return;
            target.getPersistentData().remove(ROOT);
        }

        public static CompoundTag getKeysTag(LivingEntity target) {
            if (target == null)
                return new CompoundTag();
            return target.getPersistentData().getCompound(ROOT);
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        super.registerHooks(hookBuilder);
    }
}
