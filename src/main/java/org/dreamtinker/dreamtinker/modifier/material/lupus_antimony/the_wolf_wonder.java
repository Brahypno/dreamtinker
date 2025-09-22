package org.dreamtinker.dreamtinker.modifier.material.lupus_antimony;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.*;

public class the_wolf_wonder extends BattleModifier {
    private static final int minDuration = TheWolfWonderEffectMinTime.get() * 20;
    private static final int maxDuration = TheWolfWonderEffectMaxTime.get() * 20;
    private static final Set<ResourceLocation> BAD_CACHE = new HashSet<>();
    private static final Set<ResourceLocation> CONFIG_BLACKLIST = new HashSet<>();
    private boolean Blacklist_inited = false;


    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = context.getLivingTarget();
        if (target == null)
            return knockback;
        applyRandomEffects(target, context.getAttacker());

        return knockback;
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (target == null)
            return false;
        applyRandomEffects(target, attacker);
        return false;
    }

    private void applyRandomEffects(LivingEntity target, LivingEntity attacker) {
        RandomSource rand = target.getRandom();
        List<MobEffect> negatives =
                ForgeRegistries.MOB_EFFECTS.getValues().stream()
                                           .filter(this::filterMobeffects)
                                           .collect(Collectors.toList());
        if (negatives.isEmpty())
            return;

        for (int i = negatives.size() - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            Collections.swap(negatives, i, j);
        }

        // 取前 N 项
        List<MobEffectInstance> selected_effects = new ArrayList<>();
        for (MobEffect effect : negatives) {
            if (TheWolfWonderEffectNum.get() <= selected_effects.size())
                break;
            // 随机持续时长与等级
            int duration = minDuration + rand.nextInt(Math.max(1, maxDuration - minDuration + 1));
            int amplifier = rand.nextInt(TheWolfWonderEffectAmplifier.get());
            try {
                MobEffectInstance inst = new MobEffectInstance(effect, duration, amplifier, false, true);
                target.forceAddEffect(inst, target);
                selected_effects.add(inst);
            }
            catch (Throwable t) {
                // 该效果在当前环境不安全：记入 BAD_CACHE，继续尝试下一个
                BAD_CACHE.add(ForgeRegistries.MOB_EFFECTS.getKey(effect));
                LogUtils.getLogger().warn("Random harmful effect {} failed on {} ({}). Blacklisting for this session.",
                                          ForgeRegistries.MOB_EFFECTS.getKey(effect), target.getType(), target.level().dimension().location(), t);
            }


        }
        if (attacker != null && rand.nextInt(6666) < TheWolfWonderSurpriseNumber.get()){
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

    private boolean filterMobeffects(MobEffect effect) {
        if (!Blacklist_inited){
            Blacklist_inited = true;
            loadConfigBlacklist(TheWolfBlackList.get());
        }
        if (effect.getCategory() != MobEffectCategory.HARMFUL)
            return false;
        ResourceLocation key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        return null != key && BAD_CACHE.contains(key) &&
               CONFIG_BLACKLIST.contains(key) && !key.getPath().contains("test") && !key.getPath().contains("ceshi");
    }
}
