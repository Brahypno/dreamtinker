package org.dreamtinker.dreamtinker.common.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import team.lodestar.lodestone.helpers.EntityHelper;

public class thirsty extends MobEffect {
    public thirsty() {
        super(MobEffectCategory.HARMFUL, 0x1FA60A);
    }

    private static final ResourceLocation Gluttony =
            new ResourceLocation("malum", "gluttony");
    private static final ResourceLocation Silenced =
            new ResourceLocation("malum", "silenced");
    private static final ResourceLocation Deliverance =
            new ResourceLocation("malum", "imminent_deliverance");

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // 每 tick 调用 applyEffectTick
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (null == Gluttony() || null == Silenced() || null == Deliverance())
            return;
        MobEffectInstance gluttony = entity.getEffect(Gluttony());
        MobEffectInstance silenced = entity.getEffect(Silenced());
        MobEffectInstance deliverance = entity.getEffect(Deliverance());
        if (null != gluttony){
            int gluttony_level = gluttony.getAmplifier();
            if (amplifier < gluttony_level)
                entity.removeEffect(this);
            else {
                MobEffectInstance thirsty = entity.getEffect(this);
                if (null != thirsty){
                    EntityHelper.amplifyEffect(thirsty, entity, -gluttony_level);
                }
            }
            if (null != Silenced() && null != silenced){
                if (gluttony_level < silenced.getAmplifier()){
                    EntityHelper.amplifyEffect(silenced, entity, -gluttony_level);
                }else
                    entity.removeEffect(Silenced());

            }
            if (null != Deliverance() && null != deliverance){
                if (gluttony_level < deliverance.getAmplifier()){
                    EntityHelper.amplifyEffect(deliverance, entity, -gluttony_level);
                }else
                    entity.removeEffect(Deliverance());

            }
        }else {
            if (null == silenced)
                entity.addEffect(new MobEffectInstance(Silenced(), 200, amplifier, true, false));
            else if (silenced.getAmplifier() < amplifier)
                EntityHelper.amplifyEffect(silenced, entity, amplifier, amplifier);
            if (null == deliverance)
                entity.addEffect(new MobEffectInstance(Deliverance(), 200, amplifier, true, false));
            else if (deliverance.getAmplifier() < amplifier)
                EntityHelper.amplifyEffect(deliverance, entity, amplifier, amplifier);
        }
    }

    public static MobEffect Gluttony() {
        if (!ModList.get().isLoaded("malum"))
            return null;
        return ForgeRegistries.MOB_EFFECTS.getValue(Gluttony);
    }

    private static MobEffect Silenced() {
        if (!ModList.get().isLoaded("malum"))
            return null;
        return ForgeRegistries.MOB_EFFECTS.getValue(Silenced);
    }

    private static MobEffect Deliverance() {
        if (!ModList.get().isLoaded("malum"))
            return null;
        return ForgeRegistries.MOB_EFFECTS.getValue(Deliverance);
    }
}
