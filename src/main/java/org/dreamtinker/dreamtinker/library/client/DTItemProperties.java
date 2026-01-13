package org.dreamtinker.dreamtinker.library.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;

public class DTItemProperties {

    public static void register() {
        Item item = DreamtinkerCommon.fox_fur.get();

        ItemProperties.register(
                item,
                new ResourceLocation("dreamtinker", "red_fur"),
                (ItemStack stack, ClientLevel level, LivingEntity entity, int seed) -> {
                    if (stack.getTag() != null){
                        return stack.getTag().getBoolean("red_fur") ? 1.0F : 0.0F;
                    }else
                        return 0.0F;
                }
        );
    }
}
