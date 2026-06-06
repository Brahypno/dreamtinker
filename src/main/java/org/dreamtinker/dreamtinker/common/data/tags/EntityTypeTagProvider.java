package org.dreamtinker.dreamtinker.common.data.tags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagProvider extends EntityTypeTagsProvider {

    public EntityTypeTagProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, Dreamtinker.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull Provider provider) {
        this.tag(DreamtinkerTagKeys.EntityTypes.ENDER_ENTITY)
            .add(TinkerWorld.enderSlimeEntity.get(), EntityType.ENDER_DRAGON, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.SHULKER)
            .addOptional(new ResourceLocation("tconstruct", "end_slime"))
            .addOptional(new ResourceLocation("cataclysm", "ender_guardian"))
            .addOptional(new ResourceLocation("goety", "ender_keeper"))
            .addOptional(new ResourceLocation("grimoireofgaia", "ender_dragon_girl"))
            .addOptional(new ResourceLocation("betterendforge", "shadow_walker"))
            .addOptional(new ResourceLocation("betterendforge", "end_slime"))
            .addOptional(new ResourceLocation("stalwart_dungeons", "propulk"))
            .addOptional(new ResourceLocation("stalwart_dungeons", "shelterer"))
            .addOptionalTag(DreamtinkerTagKeys.EntityTypes.common("farlanders", "endermen"))
            .addOptional(new ResourceLocation("hmag", "entities/ender_executor"))
            .addOptional(new ResourceLocation("legendary_monsters", "shulker_mimic"))
            .addOptional(new ResourceLocation("legendary_monsters", "endersent"))
            .addOptional(new ResourceLocation("legendary_monsters", "flame_drifter"))
            .addOptional(new ResourceLocation("legendary_monsters", "annihilation_pursuer"))
            .addOptional(new ResourceLocation("legendary_monsters", "chorusling"))
            .addOptional(new ResourceLocation("legendary_monsters", "flameborn_warrior"))
            .addOptional(new ResourceLocation("legendary_monsters", "flameborn_guard"))
            .addOptional(new ResourceLocation("legendary_monsters", "flameborn_warrior"))
            .addOptional(new ResourceLocation("legendary_monsters", "the_obliterator"));
        this.tag(DreamtinkerTagKeys.EntityTypes.CHAOS_ELITE)
            .addOptional(new ResourceLocation("born_in_chaos_v1", "fallen_chaos_knight"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "nightmare_stalker"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "skeleton_thrasher"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "diamond_termite"));

        this.tag(DreamtinkerTagKeys.EntityTypes.CHAOS_BOSS)
            .addOptional(new ResourceLocation("born_in_chaos_v1", "missioner"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "lifestealer"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "krampus"));
        this.tag(DreamtinkerTagKeys.EntityTypes.CHAOS_MINOR)
            .addOptional(new ResourceLocation("born_in_chaos_v1", "controlled_baby_skeleton"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "mr_pumpkin_controlled"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "controlled_spiritual_assistant"));
        this.tag(DreamtinkerTagKeys.EntityTypes.CHAOS_HEAD)
            .addOptional(new ResourceLocation("born_in_chaos_v1", "lord_pumpkinhead"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "pumpkinhead"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "sir_pumpkinhead"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "sir_pumpkinhead_without_horse"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "sir_the_headless"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "felsteed"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "lord_pumpkinhead"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "lord_pumpkinhead_withouta_horse"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "lord_the_headless"))
            .addOptional(new ResourceLocation("born_in_chaos_v1", "lords_felsteed"));

    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Entity Type Tags";
    }
}
