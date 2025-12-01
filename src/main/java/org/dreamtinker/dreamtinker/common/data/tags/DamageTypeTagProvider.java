package org.dreamtinker.dreamtinker.common.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.DamageTypeTags.*;

public class DamageTypeTagProvider extends DamageTypeTagsProvider {
    public DamageTypeTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, lookup, Dreamtinker.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider Provider) {
        tag(DAMAGES_HELMET).add(DreamtinkerDamageTypes.NULL_VOID);
        tag(BYPASSES_ARMOR).add(DreamtinkerDamageTypes.NULL_VOID, DreamtinkerDamageTypes.rain_bow);
        tag(BYPASSES_SHIELD).add(DreamtinkerDamageTypes.NULL_VOID);
        tag(BYPASSES_INVULNERABILITY).add(DreamtinkerDamageTypes.NULL_VOID);
        tag(BYPASSES_COOLDOWN).add(DreamtinkerDamageTypes.NULL_VOID);
        tag(BYPASSES_EFFECTS).add(DreamtinkerDamageTypes.NULL_VOID);
        tag(BYPASSES_RESISTANCE).add(DreamtinkerDamageTypes.NULL_VOID);
        tag(BYPASSES_ENCHANTMENTS).add(DreamtinkerDamageTypes.NULL_VOID, DreamtinkerDamageTypes.rain_bow);
        tag(AVOIDS_GUARDIAN_THORNS).add(DreamtinkerDamageTypes.NULL_VOID);
        tag(ALWAYS_HURTS_ENDER_DRAGONS).add(DreamtinkerDamageTypes.NULL_VOID);

    }
}
