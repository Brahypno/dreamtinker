package org.brahypno.dreamtinker.library.modifiers.modules.combat;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.ApiStatus;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.json.RandomLevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.tools.modules.armor.CounterModule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static slimeknights.tconstruct.TConstruct.RANDOM;

public record SelfMobEffectModule(IJsonPredicate<LivingEntity> target, @Nullable MobEffect effect, RandomLevelingValue level, RandomLevelingValue time,
                                  LevelingValue chance, boolean applyBeforeMelee,
                                  ModifierCondition<IToolStackView> condition,
                                  @Nullable TagKey<MobEffect> effectTag) implements OnAttackedModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook, ProjectileHitModifierHook, ModifierModule, ModifierCondition.ConditionalModule<IToolStackView> {
    public static final RecordLoadable<SelfMobEffectModule> LOADER = RecordLoadable.create(
            LivingEntityPredicate.LOADER.defaultField("target", SelfMobEffectModule::target),
            Loadables.MOB_EFFECT.nullableField("effect", SelfMobEffectModule::effect),
            RandomLevelingValue.LOADABLE.requiredField("level", SelfMobEffectModule::level),
            RandomLevelingValue.LOADABLE.requiredField("time", SelfMobEffectModule::time),
            LevelingValue.LOADABLE.defaultField("chance", LevelingValue.eachLevel(0.25f), false, SelfMobEffectModule::chance),
            BooleanLoadable.INSTANCE.defaultField("apply_before_melee", false, false, SelfMobEffectModule::applyBeforeMelee),
            ModifierCondition.TOOL_FIELD,
            Loadables.MOB_EFFECT_TAG.nullableField("effect_tag", SelfMobEffectModule::effectTag),
            SelfMobEffectModule::new);
    private static final List<ModuleHook<?>>
            DEFAULT_HOOKS = HookProvider.<SelfMobEffectModule>defaultHooks(ModifierHooks.ON_ATTACKED, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT,
                                                                           ModifierHooks.PROJECTILE_HIT);

    /**
     * @apiNote Internal constructor, use {@link #builder(MobEffect)}
     */
    @ApiStatus.Internal
    public SelfMobEffectModule {}

    /**
     * Creates a builder instance
     */
    public static SelfMobEffectModule.Builder builder(MobEffect effect) {
        return Builder.effect(effect);
    }

    public static SelfMobEffectModule.Builder builder(TagKey<MobEffect> effectTag) {
        return Builder.effectTag(effectTag);
    }

    /**
     * Creates a builder instance
     */
    public static SelfMobEffectModule.Builder builder(Supplier<? extends MobEffect> effect) {
        return Builder.effect(effect.get());
    }

    private static void addRandomEffectFromTagIfMissing(
            LivingEntity target,
            TagKey<MobEffect> tag,
            int duration,
            int amplifier) {
        Registry<MobEffect> registry = BuiltInRegistries.MOB_EFFECT;

        // 1) 玩家当前是否已经有任意一个属于该 tag 的效果
        boolean hasTaggedEffect = target.getActiveEffects().stream()
                                        .map(MobEffectInstance::getEffect)
                                        .anyMatch(effect -> registry.wrapAsHolder(effect).is(tag));

        if (hasTaggedEffect){
            return;
        }

        // 2) 取出 tag 里的所有效果
        Optional<HolderSet.Named<MobEffect>> opt = registry.getTag(tag);
        if (opt.isEmpty()){
            return;
        }

        List<Holder<MobEffect>> candidates = opt.get().stream().toList();
        if (candidates.isEmpty()){
            return;
        }

        // 3) 随机抽一个
        Holder<MobEffect> chosen = candidates.get(target.getRandom().nextInt(candidates.size()));

        // 4) 给玩家添加
        MobEffectInstance inst = new MobEffectInstance(chosen.get(), duration, amplifier);
        target.addEffect(inst);
    }

    /**
     * Applies the effect for the given level
     */
    private void applyEffect(@Nullable LivingEntity target, float scaledLevel) {
        if (target == null || !this.target.matches(target)){
            return;
        }
        int level = Math.round(this.level.computeValue(scaledLevel)) - 1;
        if (level < 0){
            return;
        }
        float duration = this.time.computeValue(scaledLevel);
        if (duration > 0){
            if (null != this.effect)
                target.addEffect(new MobEffectInstance(effect, (int) duration, level));
            else if (null != this.effectTag){
                addRandomEffectFromTagIfMissing(target, effectTag, (int) duration, level);
            }
        }
    }

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        if (isDirectDamage && tool.hasTag(TinkerTags.Items.ARMOR) && condition.matches(tool, modifier)){
            LivingEntity defender = context.getEntity();
            float scaledLevel = CounterModule.getLevel(tool, modifier, slotType, defender);
            float chance = this.chance.compute(scaledLevel);
            if (chance >= 1 || RANDOM.nextFloat() < chance){
                applyEffect(defender, scaledLevel);
                ToolDamageUtil.damageAnimated(tool, 1, defender, slotType);
            }
        }
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        if (applyBeforeMelee && condition.matches(tool, modifier)){
            applyEffect(context.getAttacker(), modifier.getEffectiveLevel());
        }
        return knockback;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (!applyBeforeMelee && condition.matches(tool, modifier)){
            applyEffect(context.getAttacker(), modifier.getEffectiveLevel());
        }
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        if (condition.matches(tool, modifier)){
            applyEffect(context.getAttacker(), modifier.getEffectiveLevel());
        }
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (condition.modifierLevel().test(modifier.getLevel())){
            applyEffect(attacker, modifier.getEffectiveLevel());
        }
        return false;
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<SelfMobEffectModule> getLoader() {
        return LOADER;
    }

    /**
     * Builder for this modifier in datagen
     */
    public static class Builder extends ModuleBuilder.Stack<SelfMobEffectModule.Builder> {
        private MobEffect effect;
        private TagKey<MobEffect> effectTag;
        private IJsonPredicate<LivingEntity> target = LivingEntityPredicate.ANY;
        private RandomLevelingValue level = RandomLevelingValue.flat(1);
        private RandomLevelingValue time = RandomLevelingValue.flat(0);
        private LevelingValue chance = LevelingValue.eachLevel(0.25f);
        private boolean applyBeforeMelee = false;

        public static Builder effect(MobEffect effect) {
            Builder builder = new Builder();
            builder.effect = Objects.requireNonNull(effect, "effect");
            return builder;
        }

        public static Builder effectTag(TagKey<MobEffect> effectTag) {
            Builder builder = new Builder();
            builder.effectTag = Objects.requireNonNull(effectTag, "effectTag");
            return builder;
        }

        public SelfMobEffectModule.Builder target(IJsonPredicate<LivingEntity> target) {
            this.target = target;
            return this;
        }

        public SelfMobEffectModule.Builder level(RandomLevelingValue level) {
            this.level = level;
            return this;
        }

        public SelfMobEffectModule.Builder chance(LevelingValue chance) {
            this.chance = chance;
            return this;
        }

        public SelfMobEffectModule.Builder time(RandomLevelingValue time) {
            this.time = time;
            return this;
        }

        /**
         * Builds the finished modifier
         */
        public SelfMobEffectModule build() {
            boolean hasEffect = effect != null;
            boolean hasEffectTag = effectTag != null;

            if (hasEffect == hasEffectTag){
                throw new IllegalStateException("Exactly one of effect or effectTag must be set");
            }
            return new SelfMobEffectModule(target, effect, level, time, chance, applyBeforeMelee, condition, effectTag);
        }
    }
}
