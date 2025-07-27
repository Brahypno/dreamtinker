package org.dreamtinker.dreamtinker.register;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

public class DreamtinkerTab {

    public static final CreativeModeTab TOOL = new CreativeModeTab("Dreamtinker.tool") {
        @Override
        public @NotNull ItemStack makeIcon() {
            ItemStack icon = new ItemStack(DreamtinkerItem.masu.get().getRenderTool().getItem());

            CompoundTag tag = new CompoundTag();
            ListTag materialList = new ListTag();
            materialList.add(StringTag.valueOf("dreamtinker:echo_shard"));
            materialList.add(StringTag.valueOf("tconstruct:cobalt"));
            materialList.add(StringTag.valueOf("tconstruct:blazing_bone"));
            materialList.add(StringTag.valueOf("tconstruct:wood"));
            materialList.add(StringTag.valueOf("tconstruct:stone"));

            tag.put("tic_materials", materialList);
            icon.setTag(tag);

            return icon;
        }
    };
    public static final CreativeModeTab PART = new CreativeModeTab("Dreamtinker.part") {
        @Override
        public @NotNull ItemStack makeIcon() {
            return DreamtinkerItem.explode_core.get().withMaterial(MaterialVariantId.parse("tconstruct:blazing_bone"));
        }
    };
    public static final CreativeModeTab ORE = new CreativeModeTab("Dreamtinker.ore") {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(DreamtinkerItem.stibnite_ore.get());
        }
    };
}