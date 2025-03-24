package org.dreamtinker.dreamtinker.register;


import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DreamtinkerTab {

    public static final CreativeModeTab TOOL = new CreativeModeTab("Dreamtinker.tool") {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(DreamtinkerItem.tntarrow.get());
        }
    };

}