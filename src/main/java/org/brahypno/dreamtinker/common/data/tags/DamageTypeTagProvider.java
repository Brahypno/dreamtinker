package org.brahypno.dreamtinker.common.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.brahypno.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.DamageTypeTags.*;
import static org.brahypno.dreamtinker.common.DreamtinkerDamageTypes.*;
import static slimeknights.tconstruct.common.TinkerTags.DamageTypes.MELEE_PROTECTION;

public class DamageTypeTagProvider extends DamageTypeTagsProvider {
    public DamageTypeTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, lookup, Dreamtinker.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider Provider) {
        tag(DAMAGES_HELMET).add(NULL_VOID, many_wishes);
        tag(BYPASSES_ARMOR).add(NULL_VOID, rain_bow, edict_punishments, many_wishes);
        tag(BYPASSES_SHIELD).add(NULL_VOID, edict_punishments, many_wishes);
        tag(BYPASSES_INVULNERABILITY).add(NULL_VOID, arcane_damage, solar_judgment/*, edict_punishments*/);
        tag(BYPASSES_COOLDOWN).add(NULL_VOID, arcane_damage, edict_punishments, solar_judgment);
        tag(BYPASSES_EFFECTS).add(NULL_VOID, ruin_wheel, many_wishes, solar_judgment);
        tag(BYPASSES_RESISTANCE).add(NULL_VOID, ruin_wheel, solar_judgment);
        tag(BYPASSES_ENCHANTMENTS).add(NULL_VOID, rain_bow, arcane_damage, many_wishes, solar_judgment);
        tag(AVOIDS_GUARDIAN_THORNS).add(NULL_VOID);
        tag(ALWAYS_HURTS_ENDER_DRAGONS).add(NULL_VOID, arcane_damage, edict_punishments, ruin_wheel, many_wishes);
        tag(WITCH_RESISTANT_TO).add(arcane_damage);
        tag(MELEE_PROTECTION).add(tnt_arrow_force);

    }
}
