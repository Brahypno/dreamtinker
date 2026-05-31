package org.dreamtinker.dreamtinker.tools.modifiers.traits.armors;

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
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.nbt.Tag.TAG_INT;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.SoulBoundCoolDown;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.SoulBoundRange;

public class SoulBlessing extends Modifier implements InventoryTickModifierHook, TooltipModifierHook {
    public static final ResourceLocation TAG_SOUL_BOUND = new ResourceLocation(Dreamtinker.MODID, "soul_blessing");

    {
        MinecraftForge.EVENT_BUS.addListener(this::onLivingDeath);
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK, ModifierHooks.TOOLTIP);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
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
        if (entity.level().isClientSide || event.isCanceled())
            return;
        for (EquipmentSlot slot : DTModifierCheck.slots) {
            if (0 < DTModifierCheck.getModifierLevel(entity, DreamtinkerModifiers.soul_blessing.getId(), slot)){
                ItemStack stack = entity.getItemBySlot(slot);
                ToolStack ts = ToolStack.from(stack);
                if (0 == ts.getPersistentData().getInt(TAG_SOUL_BOUND)){
                    Entity killer = event.getSource().getDirectEntity();
                    int hitRadius = SoulBoundRange.get();
                    List<LivingEntity> nearbyEntities =
                            entity.level().getEntitiesOfClass(LivingEntity.class, new AABB(entity.position().subtract(hitRadius, hitRadius, hitRadius),
                                                                                           entity.position().add(hitRadius, hitRadius, hitRadius)));

                    // 遍历实体列表
                    for (LivingEntity scapegoat : nearbyEntities) {
                        if (!scapegoat.is(entity) && (!scapegoat.getType().is(Tags.EntityTypes.BOSSES) || null == killer || !scapegoat.is(killer))){
                            ts.getPersistentData().putInt(TAG_SOUL_BOUND, SoulBoundCoolDown.get());
                            scapegoat.invulnerableTime = 0;
                            scapegoat.setHealth(0.0F);
                            scapegoat.die(event.getSource());
                            event.setCanceled(true);
                            entity.deathTime = 0;
                            entity.setHealth(Math.max(entity.getMaxHealth() * 0.10F, entity.getHealth()));
                            entity.invulnerableTime = 40;
                            entity.setRemainingFireTicks(0);     // 清火
                            entity.fallDistance = 0.0F;          // 清坠落
                            entity.setLastHurtByMob(null);       // 清仇恨，防止立刻被同一来源补刀
                            entity.hurtMarked = true;            // 强制一次位置/生命同步
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
