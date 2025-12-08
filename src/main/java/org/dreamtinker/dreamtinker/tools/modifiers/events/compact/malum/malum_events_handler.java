package org.dreamtinker.dreamtinker.tools.modifiers.events.compact.malum;

import com.sammy.malum.common.capability.MalumLivingEntityDataCapability;
import com.sammy.malum.common.entity.activator.SpiritCollectionActivatorEntity;
import com.sammy.malum.common.item.curiosities.weapons.TyrvingItem;
import com.sammy.malum.common.item.curiosities.weapons.WeightOfWorldsItem;
import com.sammy.malum.common.item.curiosities.weapons.scythe.EdgeOfDeliveranceItem;
import com.sammy.malum.common.item.curiosities.weapons.scythe.MalumScytheItem;
import com.sammy.malum.common.item.curiosities.weapons.staff.ErosionScepterItem;
import com.sammy.malum.registry.common.item.ItemRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import team.lodestar.lodestone.helpers.RandomHelper;

public class malum_events_handler {
    private static boolean reEnter = false;

    public static void MalumLivingHurtEvent(LivingHurtEvent event) {
        if (event.isCanceled())
            return;
        LivingEntity target = event.getEntity();
        DamageSource dmg = event.getSource();

        if (null != dmg.getEntity() && dmg.getEntity() instanceof Player player && !reEnter){
            reEnter = true;
            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof IModifiable))
                return;
            if (0 < ToolStack.from(stack).getModifierLevel(DreamtinkerModifiers.malum_base.getId()))
                ((MalumScytheItem) ItemRegistry.CRUDE_SCYTHE.get()).hurtEvent(event, player, event.getEntity(), stack);
            if (0 < ToolStack.from(stack).getModifierLevel(DreamtinkerModifiers.Ids.malum_tyrving))
                ((TyrvingItem) ItemRegistry.TYRVING.get()).hurtEvent(event, player, event.getEntity(), stack);
            if (0 < ToolStack.from(stack).getModifierLevel(DreamtinkerModifiers.Ids.malum_world_of_weight))
                ((WeightOfWorldsItem) ItemRegistry.WEIGHT_OF_WORLDS.get()).hurtEvent(event, player, event.getEntity(), stack);
            if (0 < ToolStack.from(stack).getModifierLevel(DreamtinkerModifiers.Ids.malum_edge_of_deliverance))
                ((EdgeOfDeliveranceItem) ItemRegistry.EDGE_OF_DELIVERANCE.get()).hurtEvent(event, player, event.getEntity(), stack);
            if (0 < ToolStack.from(stack).getModifierLevel(DreamtinkerModifiers.malum_erosion.getId()))
                ((ErosionScepterItem) ItemRegistry.EROSION_SCEPTER.get()).hurtEvent(event, player, event.getEntity(), stack);
            if (!(event.getSource().getEntity() instanceof LivingEntity attacker))
                return;
            int levels = DTModifierCheck.getMainhandModifierLevel(attacker, DreamtinkerModifiers.malum_sol_tiferet.getId());
            if (0 < levels && null != target && !target.level().isClientSide &&
                crossedWhichN(target, event.getAmount() - target.getAbsorptionAmount(), levels + 1))
                MalumLivingEntityDataCapability.getCapabilityOptional(target).ifPresent(c -> {
                    if (c.watcherNecklaceCooldown == 0){
                        float speed = 0.4f;
                        final Level level = attacker.level();
                        var random = level.getRandom();
                        Vec3 position = target.position().add(0, target.getBbHeight() / 2f, 0);
                        int amount = target instanceof Player ? 5 : 2;
                        for (int i = 0; i < amount; i++) {
                            SpiritCollectionActivatorEntity entity = new SpiritCollectionActivatorEntity(level, attacker.getUUID(),
                                                                                                         position.x,
                                                                                                         position.y,
                                                                                                         position.z,
                                                                                                         RandomHelper.randomBetween(random, -speed, speed),
                                                                                                         RandomHelper.randomBetween(random, 0.05f, 0.06f),
                                                                                                         RandomHelper.randomBetween(random, -speed, speed));
                            level.addFreshEntity(entity);
                        }
                        c.watcherNecklaceCooldown = 100;
                    }
                });
            reEnter = false;
        }
    }

    public static void MalumLivingDeathEvent(LivingDeathEvent event) {
        DamageSource dmg = event.getSource();
        if (null != dmg.getEntity() && dmg.getEntity() instanceof Player player){
            ItemStack stack = player.getMainHandItem();
            if (0 < ToolStack.from(stack).getModifierLevel(DreamtinkerModifiers.Ids.malum_world_of_weight))
                ((WeightOfWorldsItem) ItemRegistry.WEIGHT_OF_WORLDS.get()).killEvent(event, player, event.getEntity(), stack);
        }
    }

    public static boolean crossedAfterHit(LivingEntity e, float dealtToHealth, int n, int x) {
        if (n <= 0 || n >= x || dealtToHealth <= 0f)
            return false;
        //System.out.println(e + "" + dealtToHealth);

        final float max = e.getMaxHealth();
        final float threshold = max * ((float) n / (float) x);

        float after = e.getHealth();            // 已经扣完后的血量
        float before = after + dealtToHealth;    // 反推受击前血量
        return (before > threshold) && (after <= threshold + 1e-6f);
    }

    private static boolean crossedWhichN(LivingEntity e, float damage, int x) {
        for (int n = 1; n < x; n++) {
            if (crossedAfterHit(e, damage, n, x))
                return true;
        }
        return false; // 没有跨过
    }
}
