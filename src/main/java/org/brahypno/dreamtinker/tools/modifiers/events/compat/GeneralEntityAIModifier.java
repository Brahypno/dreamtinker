package org.brahypno.dreamtinker.tools.modifiers.events.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.Entity.AggressiveFox;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class GeneralEntityAIModifier {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity().level().isClientSide)
            return;

        // 防止重复添加（读档 / 重新进入区块时事件会再触发）
        CompoundTag data = event.getEntity().getPersistentData();
        String key = "dreamtinker_add_wolf_battle";
        if (event.getEntity() instanceof Wolf wolf && data.getBoolean(key)){
            data.putBoolean(key, true);
            wolf.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(wolf, AggressiveFox.class, 10, true, true, null));
        }
        if (event.getEntity() instanceof PathfinderMob mob && mob.getHealth() < 30){
            mob.targetSelector.addGoal(3, new AvoidEntityGoal<>(
                    mob,
                    LivingEntity.class,
                    // avoidPredicate：只有满足条件的玩家才会被当成“需要躲避的目标”
                    living -> isScaryLivingEntity(living)
                              && (!(living instanceof Player p) || (!p.isCreative() && !p.isSpectator())),
                    6.0F,   // 参考原版苦力怕避猫的触发半径
                    1.0D,   // walk speed
                    1.2D,   // sprint speed
                    // second predicate（若你的映射/构造器需要）：通常可与上面相同或更宽松
                    living -> true
            ));
        }

    }

    private static boolean isScaryLivingEntity(LivingEntity e) {
        return ETModifierCheck.haveModifierIn(e, DreamtinkerModifiers.Ids.monster_blood);
    }
}
