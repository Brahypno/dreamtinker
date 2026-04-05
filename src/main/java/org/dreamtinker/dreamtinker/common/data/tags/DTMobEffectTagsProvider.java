package org.dreamtinker.dreamtinker.common.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerEffects;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class DTMobEffectTagsProvider extends TagsProvider<MobEffect> {
    public DTMobEffectTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.MOB_EFFECT, lookupProvider, Dreamtinker.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DreamtinkerTagKeys.MobEffects.EDICTS)
                .add(DreamtinkerEffects.Ahimsa.getKey())
                .add(DreamtinkerEffects.EdictOfStillness.getKey())
                .add(DreamtinkerEffects.LawOfTheSilentStep.getKey())
                .add(DreamtinkerEffects.InterdictOfAscent.getKey())
                .add(DreamtinkerEffects.InterdictOfGuard.getKey())
                .add(DreamtinkerEffects.InterdictOfRestoration.getKey())
                .add(DreamtinkerEffects.EdictOfUntouched.getKey());
    }
}
