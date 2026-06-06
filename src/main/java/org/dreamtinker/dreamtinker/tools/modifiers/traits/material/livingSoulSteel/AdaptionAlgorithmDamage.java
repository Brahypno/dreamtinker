package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.livingSoulSteel;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.fml.ModList;
import org.dreamtinker.dreamtinker.library.modifiers.DreamtinkerHook;
import org.dreamtinker.dreamtinker.library.modifiers.hook.ProjectileHurtHook;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

import static org.dreamtinker.dreamtinker.Dreamtinker.configCompactDisabled;

public class AdaptionAlgorithmDamage extends NoLevelsModifier implements MeleeDamageModifierHook, ProjectileHurtHook {
    public static float killStatGain(ServerPlayer player, EntityType<?> type) {
        int kills = player.getStats().getValue(Stats.ENTITY_KILLED.get(type));
        return killStatGain(kills);
    }

    public static float killStatGain(int kills) {
        double x = Math.max(0, kills);

        double earlyGain = 0.05D;
        double softCap = 5.00D;
        double lateGain = 0.001D;
        double knee = 25D;

        double early = softCap * (1D - Math.exp(-earlyGain * x / softCap));
        double late = lateGain * knee * Math.log1p(x / knee);

        return (float) (early + late);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, DreamtinkerHook.PROJECTILE_HURT);
        if (ModList.get().isLoaded("legendary_monsters") && !configCompactDisabled("legendary_monsters"))
            hookBuilder.addModule(new ModifierTraitModule(DreamtinkerModifiers.soul_rage.getId(), 1, false));

        super.registerHooks(hookBuilder);
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        Entity target = context.getTarget();
        if (!(context.getAttacker() instanceof ServerPlayer player)){
            return damage;
        }
        return damage * (1.0F + killStatGain(player, target.getType()));
    }

    @Override
    public float modifyProjectileHurt(
            ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile,
            DamageSource source, @Nullable LivingEntity attacker, LivingEntity target, float amount) {
        if (!(attacker instanceof ServerPlayer player)){
            return amount;
        }
        return amount * (1.0F + killStatGain(player, target.getType()));
    }
}
