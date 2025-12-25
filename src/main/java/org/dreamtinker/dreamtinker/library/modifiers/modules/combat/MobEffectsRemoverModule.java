package org.dreamtinker.dreamtinker.library.modifiers.modules.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags.Items;
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
import java.util.Collections;
import java.util.List;

public record MobEffectsRemoverModule(IJsonPredicate<LivingEntity> target, RandomLevelingValue level,
                                      LevelingValue chance,
                                      boolean removeBeforeMelee, ModifierCondition<IToolStackView> condition, boolean removeOwner,
                                      MobEffectCategory category)
        implements OnAttackedModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook, ProjectileHitModifierHook, ModifierModule, ModifierCondition.ConditionalModule<IToolStackView> {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS;
    public static final RecordLoadable<MobEffectsRemoverModule> LOADER;

    @Internal
    public MobEffectsRemoverModule(IJsonPredicate<LivingEntity> target, RandomLevelingValue level, LevelingValue chance, boolean removeBeforeMelee, ModifierCondition<IToolStackView> condition, boolean removeOwner, MobEffectCategory category) {
        this.target = target;
        this.level = level;
        this.chance = chance;
        this.removeBeforeMelee = removeBeforeMelee;
        this.condition = condition;
        this.removeOwner = removeOwner;
        this.category = category;
    }

    public static Builder builder() {
        return new Builder();
    }

    private void removeEffects(@Nullable LivingEntity target, float scaledLevel) {
        if (target != null && this.target.matches(target)){
            int level = Math.round(this.level.computeValue(scaledLevel)) - 1;
            List<MobEffectInstance> all =
                    target.getActiveEffects().stream().filter(e -> e.getEffect().getCategory() == this.category).toList();
            if (all.isEmpty()){
                return;
            }
            Collections.shuffle(all, new java.util.Random(target.level().random.nextLong()));

            int removeCount = Math.min(level, all.size());
            for (int i = 0; i < removeCount; i++) {
                MobEffectInstance inst = all.get(i);
                target.removeEffect(inst.getEffect());
            }
        }
    }

    public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        Entity attacker = source.getEntity();
        if (isDirectDamage && tool.hasTag(Items.ARMOR) && this.condition.matches(tool, modifier) && attacker instanceof LivingEntity living){
            LivingEntity defender = context.getEntity();
            float scaledLevel = CounterModule.getLevel(tool, modifier, slotType, defender);
            float chance = this.chance.compute(scaledLevel);
            if (chance >= 1.0F || TConstruct.RANDOM.nextFloat() < chance){
                this.removeEffects(this.removeOwner ? defender : living, scaledLevel);
                ToolDamageUtil.damageAnimated(tool, 1, defender, slotType);
            }
        }

    }

    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        if (this.removeBeforeMelee && this.condition.matches(tool, modifier)){
            this.removeEffects(this.removeOwner ? context.getAttacker() : context.getLivingTarget(), modifier.getEffectiveLevel());
        }

        return knockback;
    }

    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (!this.removeBeforeMelee && this.condition.matches(tool, modifier)){
            this.removeEffects(this.removeOwner ? context.getAttacker() : context.getLivingTarget(), modifier.getEffectiveLevel());
        }

    }

    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        if (this.condition.matches(tool, modifier)){
            this.removeEffects(this.removeOwner ? context.getAttacker() : context.getLivingTarget(), modifier.getEffectiveLevel());
        }

    }

    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (this.condition.modifierLevel().test(modifier.getLevel())){
            this.removeEffects(this.removeOwner ? attacker : target, modifier.getEffectiveLevel());
        }

        return false;
    }

    public List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }

    public RecordLoadable<MobEffectsRemoverModule> getLoader() {
        return LOADER;
    }

    public IJsonPredicate<LivingEntity> target() {
        return this.target;
    }

    public RandomLevelingValue level() {
        return this.level;
    }

    public LevelingValue chance() {
        return this.chance;
    }

    public boolean removeBeforeMelee() {
        return this.removeBeforeMelee;
    }

    public ModifierCondition<IToolStackView> condition() {
        return this.condition;
    }

    static {
        DEFAULT_HOOKS = HookProvider.defaultHooks(
                new ModuleHook[]{ModifierHooks.ON_ATTACKED, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.PROJECTILE_HIT});
        LOADER = RecordLoadable.create(
                LivingEntityPredicate.LOADER.defaultField("target", MobEffectsRemoverModule::target),
                RandomLevelingValue.LOADABLE.requiredField("level", MobEffectsRemoverModule::level),
                LevelingValue.LOADABLE.defaultField("chance", LevelingValue.eachLevel(0.25F), false,
                                                    MobEffectsRemoverModule::chance),
                BooleanLoadable.INSTANCE.defaultField("apply_before_melee", false, false,
                                                      MobEffectsRemoverModule::removeBeforeMelee),
                ModifierCondition.TOOL_FIELD,
                BooleanLoadable.INSTANCE.defaultField("remove_effect_on_attacker", false, false,
                                                      MobEffectsRemoverModule::removeOwner),
                new EnumLoadable<>(MobEffectCategory.class).requiredField("mob_effect_category", MobEffectsRemoverModule::category),
                MobEffectsRemoverModule::new);
    }

    public static class Builder extends ModuleBuilder.Stack<MobEffectsRemoverModule.Builder> {
        private IJsonPredicate<LivingEntity> target;
        private RandomLevelingValue level;
        private LevelingValue chance;
        private boolean removeBeforeMelee;
        private boolean removeOwner;
        private MobEffectCategory category;

        public MobEffectsRemoverModule build() {
            return new MobEffectsRemoverModule(this.target, this.level, this.chance,
                                               this.removeBeforeMelee, this.condition, this.removeOwner, this.category);
        }

        private Builder() {
            this.target = LivingEntityPredicate.ANY;
            this.level = RandomLevelingValue.flat(1.0F);
            this.chance = LevelingValue.eachLevel(0.25F);
            this.removeBeforeMelee = false;
            this.removeOwner = false;
            this.category = MobEffectCategory.BENEFICIAL;
        }

        public Builder target(IJsonPredicate<LivingEntity> target) {
            this.target = target;
            return this;
        }

        public Builder level(RandomLevelingValue level) {
            this.level = level;
            return this;
        }

        public Builder chance(LevelingValue chance) {
            this.chance = chance;
            return this;
        }

        public Builder attacker() {
            this.removeOwner = true;
            return this;
        }

        public Builder removeBeforeMelee(boolean removeBeforeMelee) {
            this.removeBeforeMelee = removeBeforeMelee;
            return this;
        }

        public Builder category(MobEffectCategory category) {
            this.category = category;
            return this;
        }
    }
}

