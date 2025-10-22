package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.dreamtinker.dreamtinker.utils.DThelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.*;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.nbt.Tag.TAG_FLOAT;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.*;

public class open_soul extends BattleModifier {
    private static final ResourceLocation TAG_SOUL = new ResourceLocation(Dreamtinker.MODID, "open_soul");

    {
        MinecraftForge.EVENT_BUS.addListener(this::onLivingDeath);
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;
        if (!isSelected && !isCorrectSlot)
            return;
        ModDataNBT nbt = tool.getPersistentData();
        if (tool.isBroken() && OpenSoulRepairCount.get() <= nbt.getFloat(TAG_SOUL)){
            nbt.putFloat(TAG_SOUL, (float) (nbt.getFloat(TAG_SOUL) - OpenSoulRepairCount.get()));
            tool.setDamage(0);

        }
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (!context.getAttacker().level().isClientSide){
            ModDataNBT nbt = tool.getPersistentData();
            nbt.putFloat(TAG_SOUL, nbt.getFloat(TAG_SOUL) + damageDealt);
        }
    }

    public void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        ToolStack tool = DTModifierCheck.getToolWithModifier(entity, this.getId());
        if (null != tool){
            ModDataNBT tooldata = tool.getPersistentData();
            float count = tooldata.getFloat(TAG_SOUL);
            if (OpenSoulDeathCount.get() <= count){
                tooldata.putFloat(TAG_SOUL, (float) (count - OpenSoulDeathCount.get()));
                event.setCanceled(true);
                entity.deathTime = -10;
                entity.setHealth(1);
                entity.invulnerableTime = 1000;
                entity.setAbsorptionAmount(entity.getAbsorptionAmount() + entity.getMaxHealth() * 0.5F);
            }
        }
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tool instanceof ToolStack && tooltipKey.isShiftOrUnknown()){
            ModDataNBT nbt = tool.getPersistentData();
            if (nbt.contains(TAG_SOUL, TAG_FLOAT)){
                float count = nbt.getFloat(TAG_SOUL);
                tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.open_soul").append(String.valueOf(count))
                                     .withStyle(this.getDisplayName().getStyle()));
            }
        }
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (null != attacker){
            ItemStack item = DThelper.findItemByModifierNBT(attacker, modifiers, false);
            if (null != item){
                float data = (float) (projectile.getDeltaMovement().length() * (projectile instanceof AbstractArrow arrow ? arrow.getBaseDamage() : 1));
                ToolStack ts = ToolStack.from(item);
                ToolDataNBT nbt = ts.getPersistentData();
                nbt.putFloat(TAG_SOUL, (float) (nbt.getFloat(TAG_SOUL) + data * OpenSoulRangedGet.get()));
            }
        }
        return false;
    }
}
