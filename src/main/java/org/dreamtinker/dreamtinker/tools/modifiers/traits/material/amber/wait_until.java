package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.amber;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.nbt.Tag.TAG_INT;

public class wait_until extends BattleModifier {
    private static final ResourceLocation TAG_WAIT = new ResourceLocation(Dreamtinker.MODID, "wait_until");

    @Override
    public Component onModifierRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().remove(TAG_WAIT);
        return null;
    }

    @Override
    public boolean isNoLevels() {return false;}

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!world.isClientSide && world.getGameTime() % 20 == 0){
            ModDataNBT data = tool.getPersistentData();
            int wait_time = data.getInt(TAG_WAIT);
            if (isSelected || isCorrectSlot)
                wait_time -= 10;
            else
                wait_time += 1;
            wait_time = Math.min(wait_time, Integer.MAX_VALUE / 1000);
            data.putInt(TAG_WAIT, wait_time);
        }
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        ModDataNBT dataNBT = tool.getPersistentData();
        int wait = dataNBT.getInt(TAG_WAIT);
        damage += wait;
        wait *= (int) ((modifier.getLevel() - 1) * .1);
        dataNBT.putInt(TAG_WAIT, wait);
        return damage;
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        ModDataNBT dataNBT = tool.getPersistentData();
        int wait = dataNBT.getInt(TAG_WAIT);
        double speed = projectile.getDeltaMovement().length() + wait;
        Vec3 v2 = projectile.getDeltaMovement().normalize().scale(speed);
        projectile.setDeltaMovement(v2);
        wait *= (int) ((modifier.getLevel() - 1) * .1);
        dataNBT.putInt(TAG_WAIT, wait);
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tooltipKey.isShiftOrUnknown()){
            ModDataNBT nbt = tool.getPersistentData();
            if (nbt.contains(TAG_WAIT, TAG_INT)){
                int count = nbt.getInt(TAG_WAIT);
                tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.wait_until").append(String.valueOf(count))
                                     .withStyle(this.getDisplayName().getStyle()));
            }
        }
    }
}
