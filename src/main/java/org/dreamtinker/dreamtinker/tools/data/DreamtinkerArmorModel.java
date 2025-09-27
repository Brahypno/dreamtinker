package org.dreamtinker.dreamtinker.tools.data;

import net.minecraft.data.PackOutput;
import org.dreamtinker.dreamtinker.tools.items.DTtoolsDefinition;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.MaterialArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.TrimArmorTextureSupplier;
import slimeknights.tconstruct.library.client.data.AbstractArmorModelProvider;

public class DreamtinkerArmorModel extends AbstractArmorModelProvider {
    public DreamtinkerArmorModel(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addModels() {
        addModel(DTtoolsDefinition.UNDER_PLATE, name -> new ArmorTextureSupplier[]{
                new MaterialArmorTextureSupplier.Material(name, "/plating_", 0),
                new MaterialArmorTextureSupplier.Material(name, "/maille_", 1),
                //new MaterialArmorTextureSupplier.Material(name, "/maille1_", 2),
                TrimArmorTextureSupplier.INSTANCE
        });
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Armor Models";
    }
}

