package org.dreamtinker.dreamtinker.common.event.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyBindings {
    public static final String CAT = "key.categories.dreamtinker";

    public static final KeyMapping TOOL_INTERACT =
            new KeyMapping(Dreamtinker.makeTranslationKey("key", "tool_interact"), KeyConflictContext.IN_GAME, InputConstants.getKey("key.keyboard.g"),
                           CAT);
    public static KeyMapping KEY_MODE = new KeyMapping(
            "key.dreamtinker.mode",
            org.lwjgl.glfw.GLFW.GLFW_KEY_V,
            CAT
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent e) {
        e.register(TOOL_INTERACT);
        //e.register(KEY_MODE);
    }


}
