package org.dreamtinker.dreamtinker.mixin.malum_mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import static org.dreamtinker.dreamtinker.Dreamtinker.configCompactDisabled;

@Mixin(value = ToolStack.class, remap = false)
public abstract class ExtraMalumModifier {


    // 重入保护，防止 addModifier -> rebuildStats -> 再次注入导致死循环
    private static final ThreadLocal<Boolean> AVOID_RECURSION = ThreadLocal.withInitial(() -> false);

    // 直接拿 ToolStack 的 this
    @Unique
    private ToolStack dreamtinker$self() {
        return (ToolStack) (Object) this;
    }

    @Inject(method = "rebuildStats", at = @At("TAIL"))
    private void dreamtinker$appendExtraModifierAtTail(CallbackInfo ci) {
        // 若已在递归中或 A 不存在，直接退出
        if (AVOID_RECURSION.get() || !ModList.get().isLoaded("malum") || configCompactDisabled("malum") ||
            !dreamtinker$self().hasTag(dreamtinker$malumTag("scythe"))){
            return;
        }

        ToolStack tool = dreamtinker$self();

        // 已有该 modifier 就不重复添加（不改动原有 traits / 既有 modifiers）
        if (tool.getModifierLevel(DreamtinkerModifiers.malum_base.getId()) > 0)
            return;


        try {
            AVOID_RECURSION.set(true);
            tool.addModifier(DreamtinkerModifiers.malum_base.getId(), 1);
            // addModifier 通常会触发一次 rebuild；有了 AVOID_RECURSION，不会再次进入
        }
        finally {
            AVOID_RECURSION.set(false);
        }
    }

    @Unique
    private static TagKey<Item> dreamtinker$malumTag(String name) {
        return ItemTags.create(new ResourceLocation("malum", name));
    }
}

