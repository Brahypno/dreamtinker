package org.dreamtinker.dreamtinker.register;


import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class DreamtinkerTab {

public static final CreativeModeTab MATERIALS = new CreativeModeTab("Dreamtinker.materials") {
    @Override
    public ItemStack makeIcon() {
        return new ItemStack(DreamtinkerItem.cruse_ingot.get());
    }
};

}