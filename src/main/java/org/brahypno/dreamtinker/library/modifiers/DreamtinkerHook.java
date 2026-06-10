package org.brahypno.dreamtinker.library.modifiers;

import net.minecraft.resources.ResourceLocation;
import org.brahypno.dreamtinker.library.modifiers.hook.LeftClickHook;
import org.brahypno.dreamtinker.library.modifiers.hook.ProjectileHurtHook;
import org.brahypno.dreamtinker.library.modifiers.hook.RightClickHook;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.ModuleHook;

import static org.brahypno.dreamtinker.Dreamtinker.MODID;

public class DreamtinkerHook {
    public static final ModuleHook<LeftClickHook> LEFT_CLICK =
            ModifierHooks.register(new ResourceLocation(MODID, "left_click"), LeftClickHook.class, LeftClickHook.AllMerger::new, new LeftClickHook() {});
    public static final ModuleHook<RightClickHook> RIGHT_CLICK =
            ModifierHooks.register(new ResourceLocation(MODID, "right_click"), RightClickHook.class, RightClickHook.AllMerger::new, new RightClickHook() {});
    public static final ModuleHook<ProjectileHurtHook> PROJECTILE_HURT =
            ModifierHooks.register(new ResourceLocation(MODID, "projectile_hurt"), ProjectileHurtHook.class, ProjectileHurtHook.AllMerger::new,
                                   new ProjectileHurtHook() {});
}
