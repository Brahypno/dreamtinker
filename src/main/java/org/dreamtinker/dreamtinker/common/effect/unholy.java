package org.dreamtinker.dreamtinker.common.effect;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.enigmaticlegacy.triggers.UseUnholyGrailTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class unholy extends MobEffect {

    public unholy() {
        super(MobEffectCategory.NEUTRAL, 0x2F4F4F);
    }

    @Override
    public void applyEffectTick(LivingEntity p_19467_, int p_19468_) {
        if (p_19467_.level().isClientSide)
            return;
        // 维持：每秒把隐藏效果续命一点，防止被耗尽
        int refresh = 40; // 2秒
        Player player = (Player) p_19467_;
        boolean isTheWorthyOne = SuperpositionHandler.isTheCursedOne(player) && EnigmaticItems.FORBIDDEN_FRUIT.haveConsumedFruit(player);
        if (!isTheWorthyOne){
            p_19467_.addEffect(new MobEffectInstance(MobEffects.WITHER, refresh, (p_19468_ + 1) * 3 - 1, true, false, false));
            p_19467_.addEffect(new MobEffectInstance(MobEffects.POISON, refresh, (p_19468_ + 1) * 2 - 1, true, false, false));
            p_19467_.addEffect(new MobEffectInstance(MobEffects.HUNGER, refresh, (p_19468_ + 1) * 3 - 1, true, false, false));
            p_19467_.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, refresh, (p_19468_ + 1) * 2 - 1, true, false, false));
            p_19467_.addEffect(new MobEffectInstance(MobEffects.CONFUSION, refresh, p_19468_, true, false, false));
            p_19467_.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, refresh, p_19468_, true, false, false));
        }else {
            p_19467_.addEffect(new MobEffectInstance(MobEffects.REGENERATION, refresh, (p_19468_ + 1) * 3 - 1, true, false, false));
            p_19467_.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, refresh, (p_19468_ + 1) * 2 - 1, true, false, false));
            p_19467_.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, refresh, (p_19468_ + 1) * 2 - 1, true, false, false));
            p_19467_.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, refresh, (p_19468_ + 1) * 2 - 1, true, false, false));
            p_19467_.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, refresh, p_19468_, true, false, false));
            //player.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 0, false, true));
        }
        UseUnholyGrailTrigger.INSTANCE.trigger((ServerPlayer) player, isTheWorthyOne);

    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 40 == 0; // 每2秒续一次
    }
}
