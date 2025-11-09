package org.dreamtinker.dreamtinker.common;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import org.dreamtinker.dreamtinker.Dreamtinker;

import javax.annotation.Nullable;

public class DreamtinkerDamageTypes {
    private DreamtinkerDamageTypes() {}

    public static final ResourceKey<DamageType> NULL_VOID = create("null_void");
    public static final ResourceKey<DamageType> rain_bow = create("rain_bow");

    private static ResourceKey<DamageType> create(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Dreamtinker.getLocation(name));
    }

    public static DamageSource source(RegistryAccess access, ResourceKey<DamageType> type, @Nullable Entity direct, @Nullable Entity causing) {
        return new DamageSource(access.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), direct, causing);
    }

    public static DamageSource source(RegistryAccess access, ResourceKey<DamageType> type, DamageSource source) {
        return new DamageSource(access.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), source.getDirectEntity(), source.getEntity());
    }

    public static DamageSource randomSourceNotSame(RegistryAccess access, DamageSource original, RandomSource rng) {
        var reg = access.registryOrThrow(Registries.DAMAGE_TYPE);
        Holder<DamageType> orig = original.typeHolder(); // 1.20.1 可拿到原 holder
        Holder<DamageType> chosen = null;

        for (int i = 0; i < 5; i++) {
            var opt = reg.getRandom(rng);
            if (opt.isPresent() && !opt.get().equals(orig)){
                chosen = opt.get();
                break;
            }
        }
        if (chosen == null)
            chosen = reg.getHolderOrThrow(DamageTypes.GENERIC);
        return new DamageSource(chosen, original.getDirectEntity(), original.getEntity());
    }
}
