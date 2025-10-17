package org.dreamtinker.dreamtinker.world.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.world.worldgen.ModBiomeModifiers;
import org.dreamtinker.dreamtinker.world.worldgen.ModWorldGen;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DTDataPackProvider extends DatapackBuiltinEntriesProvider {

    // 把你的 bootstrap 方法串起来（世界生成 + BiomeModifier）
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModWorldGen::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, ModWorldGen::bootstrapPlaced)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap);

    public DTDataPackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Dreamtinker.MODID));
    }
}
