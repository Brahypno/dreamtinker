package org.brahypno.dreamtinker.tools.modifiers.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;

import java.util.List;

import static org.brahypno.esotericismtinker.utils.LootHelper.LootTableItemScanner.tryExtractSomeLoot;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class VisionaryDrops {

    public static String Visionary = "dreamtinker:visionary_wishes";

    @SubscribeEvent
    public static void onVisionaryDrops(LivingDropsEvent event) {
        LivingEntity victim = event.getEntity();
        Level level = victim.level();

        // 仅在服务端执行
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel) || !victim.getPersistentData().getBoolean(VisionaryDrops.Visionary))
            return;

        List<ItemStack> forcedStacks = tryExtractSomeLoot(serverLevel, victim, 0.6f, event.getLootingLevel() * 2);
        for (ItemStack stack : forcedStacks) {
            if (stack.isEmpty())
                continue;

            event.getDrops().add(new ItemEntity(
                    level,
                    victim.getX(),
                    victim.getY(),
                    victim.getZ(),
                    stack
            ));
        }
    }
}
