package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.crying_obsidian;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.network.DNetwork;
import org.dreamtinker.dreamtinker.network.PerfectOverlayMsg;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

import static net.minecraft.nbt.Tag.TAG_INT;
import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.IsoLdeEaseTime;

public class isolde extends BattleModifier {
    private static final ResourceLocation TAG_ISOLDE_TIME = Dreamtinker.getLocation("isolde_time");
    private static final ResourceLocation TAG_ISOLDE = Dreamtinker.getLocation("isolde");

    @Override
    public Component onModifierRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().remove(TAG_ISOLDE);
        tool.getPersistentData().remove(TAG_ISOLDE_TIME);
        return null;
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide || !isSelected)
            return;
        if (holder.isUsingItem() && holder.getUseItem() == stack){
            ModDataNBT nbt = tool.getPersistentData();
            if (!nbt.contains(TAG_ISOLDE_TIME, TAG_INT)){
                int draw_time = ModifierUtil.getPersistentInt(stack, GeneralInteractionModifierHook.KEY_DRAWTIME, -1);
                if (-1 == draw_time)
                    return;
                double g = Mth.clamp(world.random.nextGaussian(), -1.0, 1.0); // ~N(0,0.35^2)
                double t01 = (g + 1.0) * 0.5;                                  // 映射到 [0,1]
                int target = IsoLdeEaseTime.get() + 1 + (int) Math.round(t01 * (2 * draw_time + IsoLdeEaseTime.get() - 1));
                nbt.putInt(TAG_ISOLDE_TIME, target);
            }
        }
    }

    public void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
        ModDataNBT nbt = tool.getPersistentData();
        if (nbt.contains(TAG_ISOLDE_TIME, TAG_INT)){
            int target = nbt.getInt(TAG_ISOLDE_TIME);
            if (IsoLdeEaseTime.get() == target - useDuration + timeLeft){
                if (entity instanceof ServerPlayer sp){
                    DNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                                          new PerfectOverlayMsg(new ResourceLocation(MODID, "textures/gui/perfect_release.png"), 2 * IsoLdeEaseTime.get()));
                    sp.playNotifySound(
                            SoundEvents.EXPERIENCE_ORB_PICKUP,
                            SoundSource.PLAYERS, 1.0f, 1.15f
                    );

                }
            }
        }

    }

    @Override
    public void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
        ModDataNBT nbt = tool.getPersistentData();
        if (nbt.contains(TAG_ISOLDE_TIME, TAG_INT)){
            int target = nbt.getInt(TAG_ISOLDE_TIME);
            nbt.putInt(TAG_ISOLDE, Math.abs(useDuration - timeLeft - target) <= IsoLdeEaseTime.get() ? 1 : 0);
            nbt.remove(TAG_ISOLDE_TIME);//If its too short to shoot, user can refresh this
        }
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (shooter.level().isClientSide)
            return;
        if (null == arrow)
            return;
        ModDataNBT nbt = tool.getPersistentData();
        if (nbt.contains(TAG_ISOLDE, TAG_INT)){
            int enhance = nbt.getInt(TAG_ISOLDE);//I suppose delta movement is not necessary.
            arrow.setBaseDamage(arrow.getBaseDamage() * (enhance + .5));
            arrow.setPierceLevel((byte) (arrow.getPierceLevel() * (enhance + .5)));
            arrow.setCritArrow(1 == enhance);
            arrow.setKnockback((int) (arrow.getKnockback() * (enhance + .5)));
            nbt.remove(TAG_ISOLDE);//I dont think there is way to freeze this, but why not
        }
    }

}
