package org.dreamtinker.dreamtinker.modifier.armors;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.ArmorModifier;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifers;
import org.dreamtinker.dreamtinker.utils.DTModiferCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.nbt.Tag.TAG_INT;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.SoulBoundCoolDown;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.SoulBoundRange;

public class soul_blessing extends ArmorModifier {
    public static final ResourceLocation TAG_SOUL_BOUND = new ResourceLocation(Dreamtinker.MODID, "soul_blessing");

    {
        MinecraftForge.EVENT_BUS.addListener(this::onLivingDeath);
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;
        if (0 == world.getGameTime() % 20)
            if (isSelected || isCorrectSlot){
                int currentSecond = tool.getPersistentData().getInt(TAG_SOUL_BOUND);
                if (0 < currentSecond)
                    tool.getPersistentData().putInt(TAG_SOUL_BOUND, --currentSecond);
            }
    }

    public void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        for (EquipmentSlot slot : DTModiferCheck.slots) {
            if (0 < DTModiferCheck.getModifierlevel(entity, DreamtinkerModifers.soul_blessing.getId(), slot)){
                ItemStack stack = entity.getItemBySlot(slot);
                ToolStack ts = ToolStack.from(stack);
                if (0 == ts.getPersistentData().getInt(TAG_SOUL_BOUND)){
                    Entity killer = event.getSource().getDirectEntity();
                    int hitRadius = SoulBoundRange.get();
                    List<Entity> nearbyEntities =
                            entity.level().getEntities(null, new AABB(entity.position().subtract(hitRadius, hitRadius, hitRadius),
                                                                      entity.position().add(hitRadius, hitRadius, hitRadius)));

                    // 遍历实体列表
                    for (Entity scapegoat : nearbyEntities) {
                        if (scapegoat instanceof LivingEntity livingEntity && !scapegoat.is(entity) && (null == killer || !scapegoat.is(killer))){
                            ts.getPersistentData().putInt(TAG_SOUL_BOUND, SoulBoundCoolDown.get());
                            livingEntity.invulnerableTime = 0;
                            livingEntity.setHealth(0.0F);
                            livingEntity.die(event.getSource());
                            event.setCanceled(true);
                            entity.deathTime = -10;
                            entity.setHealth(Math.max(entity.getMaxHealth() * 0.10F, entity.getHealth()));
                            entity.invulnerableTime = 40;
                            return;
                        }
                    }
                }

            }
        }
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tool instanceof ToolStack && tooltipKey.isShiftOrUnknown()){
            ModDataNBT nbt = tool.getPersistentData();
            if (nbt.contains(TAG_SOUL_BOUND, TAG_INT)){
                int count = nbt.getInt(TAG_SOUL_BOUND);
                tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.soul_blessing").append(String.valueOf(count))
                                     .withStyle(this.getDisplayName().getStyle()));
            }
        }
    }
}
