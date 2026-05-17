package org.dreamtinker.dreamtinker.tools.modifiers.tools.mashou;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.dreamtinker.dreamtinker.Entity.SlashOrbitEntity;
import org.dreamtinker.dreamtinker.library.modifiers.DreamtinkerHook;
import org.dreamtinker.dreamtinker.library.modifiers.hook.LeftClickHook;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.realSweepRange;
import static org.dreamtinker.dreamtinker.utils.DTHelper.autoEndColor;
import static org.dreamtinker.dreamtinker.utils.DTHelper.materialToRender;

public class RealSweep extends NoLevelsModifier implements LeftClickHook {
    @Override
    public void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, DreamtinkerHook.LEFT_CLICK);
        super.registerHooks(hookBuilder);
    }

    public void superSweep(IToolStackView tool, ModifierEntry entry, Player player, Level level, Entity entity) {
        if (!level.isClientSide && player.getAttackStrengthScale(0) > 0.8 && !tool.isBroken()){
            AttributeInstance reach = player.getAttribute(ForgeMod.ENTITY_REACH.get());
            double range = null != reach ? Math.min(realSweepRange.get(), reach.getValue()) : 1;
            if (range > 0){
                int columnA = materialToRender(0xEE050008, tool.getMaterial(0));
                int colB = materialToRender(autoEndColor(columnA, 2.8f, 1.15f, 1.1f), tool.getMaterial(1));

                SlashOrbitEntity e = new SlashOrbitEntity((ServerLevel) level, player, 6, 10, 0.70f, 6, 0, 1.8f);
                e.setGradient(columnA, colB, SlashOrbitEntity.GradMode.ANGULAR, true)
                 .setHueShift(0.02f);
                level.addFreshEntity(e);
                AABB playerBox = player.getBoundingBox();
                AABB searchBox = playerBox.inflate(range, 0.75D, range);
                Entity rootVehicle = player.getRootVehicle();
                for (LivingEntity aoeTarget : level.getEntitiesOfClass(LivingEntity.class, searchBox,
                                                                       aoeTarget -> aoeTarget != player
                                                                                    && aoeTarget != rootVehicle
                                                                                    && !aoeTarget.isDeadOrDying()
                                                                                    && !player.isAlliedTo(aoeTarget)
                                                                                    && (!(aoeTarget instanceof ArmorStand stand) || !stand.isMarker())
                )) {
                    if (aoeTarget.isAlive() && !aoeTarget.isRemoved())
                        ToolAttackUtil.performAttack(tool, ToolAttackContext.attacker(player).target(aoeTarget).cooldown(1).applyAttributes().build());
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
                //sweepArcRightToLeft((ServerLevel) level, player, range - 3, range, 360.0, 2000);
            }
        }
    }

    @Override
    public void onLeftClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {
        superSweep(tool, entry, player, level, null);
    }

    @Override
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, BlockState state, BlockPos pos) {
        superSweep(tool, entry, player, level, null);
    }

    @Override
    public void onLeftClickEntity(AttackEntityEvent event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, Entity target) {
        superSweep(tool, entry, player, level, target);
    }

}
