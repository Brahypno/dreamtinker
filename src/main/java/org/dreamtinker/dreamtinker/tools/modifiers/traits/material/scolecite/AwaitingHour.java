package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.scolecite;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.dreamtinker.dreamtinker.utils.DTMessages;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.*;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.util.Mth.clamp;

public class AwaitingHour extends BattleModifier {
    public static final ResourceLocation TAG_SCALE = Dreamtinker.getLocation("scale_worm_tool");
    public static final ResourceLocation TAG_MOTH = Dreamtinker.getLocation("moth_wing_tool");
    private final int OmenInSight = 120;

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        int scale = context.getPersistentData().getInt(TAG_SCALE);
        int moth = context.getPersistentData().getInt(TAG_MOTH);
        if (moth < scale){
            if (ToolStats.PROJECTILE_DAMAGE.supports(context.getItem()))
                ToolStats.PROJECTILE_DAMAGE.add(builder, (scale - moth) * 0.03);
            ToolStats.ATTACK_DAMAGE.multiplyAll(builder, 1 + (scale - moth) * 0.0025);
        }else if (moth > scale){
            if (ToolStats.DRAW_SPEED.supports(context.getItem()))
                ToolStats.DRAW_SPEED.multiplyAll(builder, 1 + (moth - scale) * 0.003);
            if (ToolStats.VELOCITY.supports(context.getItem()))
                ToolStats.VELOCITY.multiplyAll(builder, 1 + (moth - scale) * 0.003);
            ToolStats.ATTACK_SPEED.add(builder, (moth - scale) * 0.015);
        }
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        int scale = tool.getPersistentData().getInt(TAG_SCALE);
        int moth = tool.getPersistentData().getInt(TAG_MOTH);
        if (scale < moth){
            float speed = tool.getStats().get(ToolStats.ATTACK_SPEED);
            damage *= 1 + 0.002f * (moth - scale) * speed / (speed + 4.0f);
        }
        return damage;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (context.getLevel().isClientSide)
            return;
        ModDataNBT data = tool.getPersistentData();
        ResourceLocation resourceLocation;
        if (context.isFullyCharged() && context.isCritical()){
            resourceLocation = TAG_SCALE;
        }else {
            resourceLocation = TAG_MOTH;
        }

        int evolution = (int) (data.getInt(resourceLocation) + clamp(3 + Math.ceil(damageDealt), 3f, 12f));
        data.putInt(resourceLocation, evolution);
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (shooter.level().isClientSide)
            return;
        ModDataNBT data = tool.getPersistentData();
        ResourceLocation resourceLocation;
        if (null != arrow && arrow.isCritArrow()){
            resourceLocation = TAG_SCALE;
        }else {
            resourceLocation = TAG_MOTH;
        }

        int evolution = (int) (data.getInt(resourceLocation) + clamp(3 + Math.ceil(DTModifierCheck.getDamage(projectile)), 3f, 12f));
        data.putInt(resourceLocation, evolution);
    }

    @Override
    public Component validate(IToolStackView tool, ModifierEntry modifier) {
        if (1 < modifier.getLevel() && 1 < tool.getModifier(this).getLevel())
            return Component.translatable("modifier.dreamtinker.pupal_omen.validate");
        return null;
    }

    //I Know its not DRY. BUT　ＷＨＯ CARES!
    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;

        ModDataNBT data = tool.getPersistentData();
        int scale = data.getInt(TAG_SCALE);
        int moth = data.getInt(TAG_MOTH);
        MaterialId id;
        if (scale < OmenInSight && moth < OmenInSight)
            return;
        else if (OmenInSight <= scale){
            id = DreamtinkerMaterialIds.PermanenceScale;
        }else {
            id = DreamtinkerMaterialIds.PermanenceWing;
        }
        MaterialNBT mats = tool.getMaterials();
        int index = -1;
        for (int i = 0; i < mats.size(); i++) {
            if (mats.get(i).sameVariant(DreamtinkerMaterialIds.scolecite)){
                index = i;
                break;
            }
        }
        if (index == -1){
            if (holder instanceof Player player){
                player.sendSystemMessage(Component.translatable("modifier.dreamtinker.pupal_omen.failure")
                                                  .withStyle(this.getDisplayName().getStyle()));
            }
            return;
        }
        mats = mats.replaceMaterial(index, id);
        ToolStack toolStack = ToolStack.from(stack);
        toolStack.setMaterials(mats);
        toolStack.updateStack(stack);
        if (holder instanceof Player player){
            DTMessages.clientChat(
                    Component.translatable(
                                     OmenInSight <= scale ? "modifier.dreamtinker.pupal_omen.success_scale" : "modifier.dreamtinker.pupal_omen.success_wing")
                             .withStyle(this.getDisplayName().getStyle()), false);
        }
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tooltipKey.isShiftOrUnknown()){
            int scale = tool.getPersistentData().getInt(TAG_SCALE);
            int moth = tool.getPersistentData().getInt(TAG_MOTH);
            if (0 < moth)
                tooltip.add(Component.translatable("modifier.dreamtinker.pupal_omen_moth.tooltip", moth, OmenInSight));
            if (0 < scale)
                tooltip.add(Component.translatable("modifier.dreamtinker.pupal_omen_scale.tooltip", scale, OmenInSight));
        }
    }
}
