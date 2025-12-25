package org.dreamtinker.dreamtinker.tools.modifiers.tools.mashou;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.InteractionInterface;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Map;
import java.util.UUID;

import static org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers.as_one;
import static org.dreamtinker.dreamtinker.utils.DTModifierCheck.haveModifierIn;

public class StrongHeavy extends Modifier implements InteractionInterface {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.InteractionInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    private static final double SPEED_THRESHOLD = 0.3; // 速度阈值，大于此值时不触发
    private static final Map<UUID, Snapshot> SNAP = new java.util.WeakHashMap<>();

    private record Snapshot(double x, double y, double z, long tick) {}

    @Override
    public void onInventoryTick(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull Level world, @NotNull LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, @NotNull ItemStack stack) {
        if (holder instanceof Player player && isCorrectSlot && !player.level().isClientSide){
            Snapshot last = SNAP.get(player.getUUID());
            SNAP.put(player.getUUID(),
                     new Snapshot(player.getRootVehicle().getX(), player.getRootVehicle().getY(), player.getRootVehicle().getZ(), world.getGameTime()));
            if (world.getGameTime() % 20 != 0)
                return;
            // 处理虚弱效果
            if (player.getMainHandItem().equals(stack) && player.getOffhandItem().isEmpty() &&
                (player.hasEffect(MobEffects.DAMAGE_BOOST) || player.hasEffect(MobEffects.MOVEMENT_SPEED) ||
                 isAllowedVehicle(player, last) || haveModifierIn(holder, as_one.getId()))){
                player.removeEffect(MobEffects.WEAKNESS);
            }else
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2, true, false));
        }
    }

    private boolean isAllowedVehicle(Player player, Snapshot last) {
        Entity carrier = player.getRootVehicle();
        if (player.getRootVehicle().isAlive() && !carrier.equals(player)){
            long dt = Math.max(1, player.level().getGameTime() - last.tick());
            double dx = carrier.getX() - last.x();
            double dy = carrier.getY() - last.y();
            double dz = carrier.getZ() - last.z();

            // 速度=位移/时间（单位：方块/刻）
            double speedPerTick = Math.sqrt(dx * dx + dy * dy + dz * dz) / dt;
            //double horizPerTick = Math.hypot(dx, dz) / dt;
            return SPEED_THRESHOLD < speedPerTick;
        }
        return false;
    }
}
