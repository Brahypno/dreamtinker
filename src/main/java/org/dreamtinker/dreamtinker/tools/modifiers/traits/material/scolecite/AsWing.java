package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.scolecite;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.dreamtinker.dreamtinker.Entity.WingSlashProjectile;
import org.dreamtinker.dreamtinker.library.modifiers.DreamtinkerHook;
import org.dreamtinker.dreamtinker.library.modifiers.hook.LeftClickHook;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class AsWing extends Modifier implements LeftClickHook {
    @Override
    public void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, DreamtinkerHook.LEFT_CLICK);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onLeftClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {
        ShootWing(tool, player, level, entry.getLevel());
    }

    @Override
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, BlockState state, BlockPos pos) {
        ShootWing(tool, player, level, entry.getLevel());
    }

    @Override
    public void onLeftClickEntity(AttackEntityEvent event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, Entity target) {
        ShootWing(tool, player, level, entry.getLevel());
    }

    private void ShootWing(IToolStackView tool, Player player, Level world, int modifierLevel) {
        if (!world.isClientSide && 0.6 < player.getAttackStrengthScale(0)){
            float damageAmount = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);//TODO!
            float power = damageAmount * (0.6F + 0.15F * modifierLevel);
            int maxLife = 40 + 10 * modifierLevel;
            byte pierce = (byte) modifierLevel;
            double speed = 1.25D + 0.15D * modifierLevel;

            WingSlashProjectile.shootFrom(
                    world,
                    player,
                    power,
                    maxLife,
                    pierce,
                    speed,
                    0xEDE9DD, // 玉白
                    210,      // 半透明但核心清楚
                    1.0F,     // 标准长度
                    1.0F,     // 标准宽度
                    14.0F     // 轻微旋转
            );
        }
    }

}
