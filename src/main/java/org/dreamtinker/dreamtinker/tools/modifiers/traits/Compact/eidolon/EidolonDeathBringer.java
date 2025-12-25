package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.eidolon;

import elucent.eidolon.network.DeathbringerSlashEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.util.ColorUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EidolonDeathBringer extends BattleModifier {
    private static MobEffect unDeath;
    private static final ResourceLocation unDeathEffect =
            new ResourceLocation("eidolon", "undeath");

    public EidolonDeathBringer() {
        if (null == unDeath)
            unDeath = ForgeRegistries.MOB_EFFECTS.getValue(unDeathEffect);
    }

    private void applyEffect(ToolAttackContext context) {
        LivingEntity target = context.getLivingTarget();
        LivingEntity attacker = context.getAttacker();
        if (null == target)
            return;
        if (target.getMobType() != MobType.UNDEAD && null != unDeath){
            target.addEffect(new MobEffectInstance(unDeath, 900));
        }
        if (!attacker.level().isClientSide)
            Networking.sendToTracking(attacker.level(), attacker.blockPosition(), new DeathbringerSlashEffectPacket(
                    attacker.getX(), attacker.getY() + target.getBbHeight() / 2, attacker.getZ(),
                    target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                    ColorUtil.packColor(255, 33, 26, 23),
                    ColorUtil.packColor(255, 10, 10, 11),
                    ColorUtil.packColor(255, 161, 255, 123),
                    ColorUtil.packColor(255, 194, 171, 70)
            ));
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        applyEffect(context);
        return knockback;
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        applyEffect(context);
    }
}
