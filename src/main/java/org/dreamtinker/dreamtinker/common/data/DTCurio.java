package org.dreamtinker.dreamtinker.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;

import java.util.concurrent.CompletableFuture;

public class DTCurio extends top.theillusivec4.curios.api.CuriosDataProvider {

    public DTCurio(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries) {
        super(Dreamtinker.MODID, output, fileHelper, registries);
    }

    @Override
    public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
        createSlot("hands")
                .size(2)
                .addCosmetic(true);
        createEntities("dreamtinker_entities")
                .addPlayer()
                .addSlots("hands", "ring");
    }

}
