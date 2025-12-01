package org.dreamtinker.dreamtinker.library.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// 只在客户端用
@OnlyIn(Dist.CLIENT)
public abstract class EntityLoopSound extends AbstractTickableSoundInstance {

    private final LivingEntity entity;
    private boolean shouldStop = false;

    public EntityLoopSound(LivingEntity entity, SoundEvent soundEvent) {
        super(soundEvent,   // 你的SoundEvent
              SoundSource.PLAYERS,
              SoundInstance.createUnseededRandom());

        this.entity = entity;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
        this.pitch = 1.0F;
        this.attenuation = Attenuation.LINEAR;

        this.x = (float) entity.getX();
        this.y = (float) entity.getY();
        this.z = (float) entity.getZ();
    }

    public void requestStop() {
        this.shouldStop = true;
    }

    public boolean canPlaySound() {
        return !this.entity.isSilent() && this.entity.isUsingItem() &&
               (isValidItem(this.entity.getItemInHand(InteractionHand.MAIN_HAND)) || isValidItem(this.entity.getItemInHand(InteractionHand.OFF_HAND)));
    }

    public boolean isValidItem(ItemStack itemStack) {return true;}

    public boolean isSameEntity(LivingEntity user) {
        return this.entity.isAlive() && this.entity.getId() == user.getId();
    }


    @Override
    public void tick() {
        if (shouldStop || entity == null || !entity.isAlive()){
            this.stop();
            return;
        }

        this.x = (float) entity.getX();
        this.y = (float) entity.getY();
        this.z = (float) entity.getZ();
    }
}

