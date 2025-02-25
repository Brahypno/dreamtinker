package org.dreamtinker.dreamtinker.register;

import net.minecraft.resources.ResourceLocation;
import org.dreamtinker.dreamtinker.hook.LeftClickHook;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.ModuleHook;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;

public class DreamtinkerHook {
    public static final ModuleHook<LeftClickHook> LEFT_CLICK = ModifierHooks.register(new ResourceLocation(MODID,"left_click"), LeftClickHook.class, LeftClickHook.AllMerger::new, new LeftClickHook() {});
}
