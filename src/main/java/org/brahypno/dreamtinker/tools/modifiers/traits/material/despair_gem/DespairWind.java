package org.brahypno.dreamtinker.tools.modifiers.traits.material.despair_gem;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.EntityHitResult;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerDamageTypes;
import org.brahypno.dreamtinker.library.client.utils.MaskService;
import org.brahypno.dreamtinker.utils.DTHelper;
import org.brahypno.esotericismtinker.utils.ETHelper;
import org.brahypno.esotericismtinker.utils.damage.DamageProbe;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.combat.MeleeAttributeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.RedShadeEnable;
import static org.brahypno.esotericismtinker.utils.LootHelper.LootResolver.dropAllEquipmentLikeDeath;

public class DespairWind extends Modifier implements ProjectileHitModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook {

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = ETHelper.getLivingTarget(context.getTarget());
        LivingEntity attacker = context.getAttacker();
        if (null != target && !target.level().isClientSide){
            if (context.getPlayerAttacker() instanceof ServerPlayer sp){
                MaskService.colorIsolation(sp, Dreamtinker.getLocation("modifier/despair_wind"), RedShadeEnable.get() ? 0xFF8A221C : 0x6E3D3A3A, 50, 0.55F,
                                           1.28F, 100, -1);
            }
            DTHelper.sendVibeBarFx((ServerLevel) target.level(), attacker, target, 0xFF971E1E);
            target.setAbsorptionAmount(0);
            target.invulnerableTime = 0;
            if (target instanceof ServerPlayer sp){
                if (false){
                    if (sp.isCreative() || sp.isSpectator())
                        return knockback;
                    boolean keepInv = sp.serverLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
                    if (!keepInv){
                        // 尽量贴近死亡掉落
                        sp.getInventory().dropAll();
                        sp.getInventory().clearContent();
                    }
                }else
                    dropAllEquipmentLikeDeath(target);
            }else {
                dropAllEquipmentLikeDeath(target);
            }

        }
        return knockback;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity victim = ETHelper.getLivingTarget(context.getTarget());
        if (null != victim && !victim.level().isClientSide){
            if (context.getPlayerAttacker() instanceof ServerPlayer sp){
                MaskService.remove(sp, Dreamtinker.getLocation("modifier/despair_wind"));
            }
        }
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        DamageProbe.finalDamageMethod(context.getTarget(), DreamtinkerDamageTypes.source(context.getLevel().registryAccess(), DreamtinkerDamageTypes.NULL_VOID,
                                                                                         context.makeDamageSource()), damageAttempted);
        afterMeleeHit(tool, modifier, context, damageAttempted);
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (null != target)
            target.invulnerableTime = 0;
        return false;
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        hookBuilder.addModule(MeleeAttributeModule.builder(Attributes.ARMOR, AttributeModifier.Operation.MULTIPLY_TOTAL).flat(-1));
        hookBuilder.addModule(MeleeAttributeModule.builder(Attributes.ARMOR_TOUGHNESS, AttributeModifier.Operation.MULTIPLY_TOTAL).flat(-1));
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        afterMeleeHit(tool, modifier, context, damage);
    }
}
