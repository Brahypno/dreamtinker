package org.brahypno.dreamtinker.common.data;

import com.google.common.collect.Sets;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;
import org.brahypno.dreamtinker.fluids.DreamtinkerFluids;
import org.brahypno.dreamtinker.tools.DreamtinkerToolParts;
import org.brahypno.dreamtinker.tools.DreamtinkerTools;
import org.brahypno.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.brahypno.esotericismtinker.common.EsotericismTinkerCommon;
import org.brahypno.esotericismtinker.tools.EsotericismTinkerTools;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.HasMaterialPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.StatInRangePredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolStackItemPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolStackPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class AdvancementsProvider extends GenericDataProvider {
    protected Consumer<Advancement> advancementConsumer;
    protected BiConsumer<ResourceLocation, ConditionalAdvancement.Builder> conditionalConsumer;

    public AdvancementsProvider(PackOutput output) {
        super(output, PackOutput.Target.DATA_PACK, "advancements");
    }

    protected void generate() {
        Advancement dreamtinkerRoot = builderRoot(
                EsotericismTinkerCommon.hypnagogic_transmute.get().getDefaultInstance(),
                resource("root"),
                resource("textures/gui/advancement_background.png"),
                FrameType.TASK,
                builder -> builder.addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
        );

        Advancement toolsRoot = builderSilent(
                EsotericismTinkerTools.ritual_blade.get().getRenderTool(),
                resource("tools"),
                dreamtinkerRoot,
                FrameType.TASK,
                builder -> builder.addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
        );

        Advancement materialsRoot = builderSilent(
                DreamtinkerCommon.amber.get(),
                resource("materials"),
                dreamtinkerRoot,
                FrameType.TASK,
                builder -> builder.addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
        );

        Advancement getSilenceGlove = builder(
                DreamtinkerTools.silence_glove.get().getRenderTool(),
                resource("tools/silence_glove"),
                toolsRoot,
                FrameType.TASK,
                builder -> builder.addCriterion("crafted_silence_glove", hasItem(DreamtinkerTools.silence_glove.get()))
        );

        IJsonPredicate<IToolStackView> sixHandPredicate = ToolStackPredicate.and(
                StatInRangePredicate.min(ToolStats.ATTACK_DAMAGE, 9f),
                ToolStackPredicate.set(DreamtinkerTools.silence_glove.get())
        );

        MaterialNBT nbt = MaterialNBT.of(
                MaterialVariant.of(MaterialIds.iron, ""),
                MaterialVariant.of(DreamtinkerMaterialIds.black_sapphire, ""),
                MaterialVariant.of(DreamtinkerMaterialIds.shadowskin, "")
        );

        ItemStack silenceGlove = ToolBuildHandler.buildItemFromMaterials(DreamtinkerTools.silence_glove.get(), nbt);
        builderHidden(
                silenceGlove,
                resource("tools/six_finger"),
                getSilenceGlove,
                FrameType.GOAL,
                builder -> builder.addCriterion("damage", InventoryChangeTrigger.TriggerInstance.hasItems(ToolStackItemPredicate.ofTool(sixHandPredicate)))
        );

        ToolPartItem[] treeParts = new ToolPartItem[]{
                DreamtinkerToolParts.memoryOrthant.get(),
                DreamtinkerToolParts.wishOrthant.get(),
                DreamtinkerToolParts.soulOrthant.get(),
                DreamtinkerToolParts.personaOrthant.get(),
                DreamtinkerToolParts.reasonEmanation.get()
        };

        Item[] treeCasts = new Item[]{
                DreamtinkerCommon.memory_cast.get(),
                DreamtinkerCommon.wish_cast.get(),
                DreamtinkerCommon.soul_cast.get(),
                DreamtinkerCommon.persona_cast.get(),
                DreamtinkerCommon.reason_cast.get()
        };

        String[] treeNames = new String[]{
                "memory_orthant",
                "wish_orthant",
                "soul_orthant",
                "persona_orthant",
                "reason_emanation"
        };

        List<MaterialVariant> displayMaterial = List.of(
                MaterialVariant.of(DreamtinkerMaterialIds.black_sapphire, ""),
                MaterialVariant.of(DreamtinkerMaterialIds.whimsyGold, ""),
                MaterialVariant.of(DreamtinkerMaterialIds.RuinWheelSteel, ""),
                MaterialVariant.of(DreamtinkerMaterialIds.deliverance, ""),
                MaterialVariant.of(DreamtinkerMaterialIds.star_regulus, "")
        );

        Advancement[] treePartAdvancements = new Advancement[treeParts.length];
        for (int i = 0; i < treeParts.length; i++) {
            int index = i;
            Advancement castAdvancement = builder(
                    treeCasts[index],
                    resource("tools/" + treeNames[index] + "_cast"),
                    toolsRoot,
                    FrameType.TASK,
                    builder -> builder.addCriterion("has_" + treeNames[index] + "_cast", hasItem(treeCasts[index]))
            );

            treePartAdvancements[index] = builder(
                    treeParts[index].withMaterialForDisplay(displayMaterial.get(index).getVariant()),
                    resource("tools/" + treeNames[index]),
                    castAdvancement,
                    FrameType.TASK,
                    builder -> builder.addCriterion("has_" + treeNames[index], hasItem(treeParts[index]))
            );
        }

        builder(
                DreamtinkerTools.narcissus_wing.get().getRenderTool(),
                resource("tools/narcissus_wing"),
                treePartAdvancements[treePartAdvancements.length - 1],
                FrameType.GOAL,
                builder -> {
                    for (int i = 0; i < treeParts.length; i++) {
                        builder.addCriterion("has_" + treeNames[i], hasItem(treeParts[i]));
                    }
                    builder.addCriterion("has_narcissus_wing", hasItem(DreamtinkerTools.narcissus_wing.get()));
                }
        );

        Advancement rawStibnite = builder(
                DreamtinkerCommon.raw_stibnite.get(),
                resource("materials/raw_stibnite"),
                materialsRoot,
                FrameType.TASK,
                builder -> builder.addCriterion("has_raw_stibnite", hasItem(DreamtinkerCommon.raw_stibnite.get()))
        );

        Advancement valentinite = builder(
                DreamtinkerCommon.valentinite.get(),
                resource("materials/valentinite"),
                rawStibnite,
                FrameType.TASK,
                builder -> builder.addCriterion("has_valentinite", hasItem(DreamtinkerCommon.valentinite.get()))
        );

        Advancement nigrescenceAntimony = builder(
                DreamtinkerCommon.nigrescence_antimony.get(),
                resource("materials/nigrescence_antimony"),
                valentinite,
                FrameType.GOAL,
                builder -> builder.addCriterion("has_nigrescence_antimony", hasItem(DreamtinkerCommon.nigrescence_antimony.get()))
        );

        Advancement moltenAlbedoStibium = builder(
                DreamtinkerFluids.molten_albedo_stibium.getBucket(),
                resource("materials/molten_albedo_stibium"),
                nigrescenceAntimony,
                FrameType.GOAL,
                builder -> builder.addCriterion("has_molten_albedo_stibium", hasItem(DreamtinkerFluids.molten_albedo_stibium.getBucket()))
        );

        Advancement metallivorousStibiumLupus = builder(
                DreamtinkerCommon.metallivorous_stibium_lupus.get(),
                resource("materials/metallivorous_stibium_lupus"),
                moltenAlbedoStibium,
                FrameType.GOAL,
                builder -> builder.addCriterion("has_metallivorous_stibium_lupus", hasItem(DreamtinkerCommon.metallivorous_stibium_lupus.get()))
        );

        Advancement moltenAscendingAntimony = builder(
                DreamtinkerFluids.molten_ascending_antimony.getBucket(),
                resource("materials/molten_ascending_antimony"),
                metallivorousStibiumLupus,
                FrameType.GOAL,
                builder -> builder.addCriterion("has_molten_ascending_antimony", hasItem(DreamtinkerFluids.molten_ascending_antimony.getBucket()))
        );

        Advancement liquidSmokyAntimony = builder(
                DreamtinkerFluids.liquid_smoky_antimony.getBucket(),
                resource("materials/liquid_smoky_antimony"),
                moltenAscendingAntimony,
                FrameType.GOAL,
                builder -> builder.addCriterion("has_liquid_smoky_antimony", hasItem(DreamtinkerFluids.liquid_smoky_antimony.getBucket()))
        );

        Advancement starRegulus = builder(
                DreamtinkerCommon.regulus.get(),
                resource("materials/star_regulus"),
                liquidSmokyAntimony,
                FrameType.CHALLENGE,
                builder -> builder.addCriterion("has_star_regulus", hasItem(DreamtinkerCommon.regulus.get()))
        );

        builderHidden(
                new ItemStack(DreamtinkerCommon.regulus.get()),
                resource("materials/magnum_opus"),
                starRegulus,
                FrameType.TASK,
                builder -> builder.addCriterion(
                        "star_regulus",
                        new UsingItemTrigger.TriggerInstance(ContextAwarePredicate.ANY,
                                                             ItemPredicate.Builder.item().of(DreamtinkerCommon.regulus.get()).build())
                )
        );

        builder(
                DreamtinkerCommon.orichalcum.get(),
                resource("materials/orichalcum_ingot"),
                materialsRoot,
                FrameType.TASK,
                builder -> builder.addCriterion("has_orichalcum_ingot", hasItem(DreamtinkerCommon.orichalcum.get()))
        );

        builder(
                DreamtinkerCommon.cold_iron_ingot.get(),
                resource("materials/cold_iron_ingot"),
                materialsRoot,
                FrameType.TASK,
                builder -> builder.addCriterion("has_cold_iron_ingot", hasItem(DreamtinkerCommon.cold_iron_ingot.get()))
        );

        builder(
                DreamtinkerCommon.transmutation_gold_ingot.get(),
                resource("materials/transmutation_gold_ingot"),
                materialsRoot,
                FrameType.TASK,
                builder -> builder.addCriterion("has_transmutation_gold_ingot", hasItem(DreamtinkerCommon.transmutation_gold_ingot.get()))
        );

        builderHidden(
                new ItemStack(DreamtinkerCommon.shadow_silver_ingot.get()),
                resource("materials/shadow_silver_ingot"),
                materialsRoot,
                FrameType.TASK,
                builder -> builder.addCriterion("has_shadow_silver_ingot", hasItem(DreamtinkerCommon.shadow_silver_ingot.get()))
        );

        Advancement snakeFang = builder(
                DreamtinkerCommon.snake_fang.get(),
                resource("materials/snake_fang"),
                materialsRoot,
                FrameType.TASK,
                builder -> builder.addCriterion("has_snake_fang", hasItem(DreamtinkerCommon.snake_fang.get()))
        );

        builder(
                DreamtinkerCommon.eden_fruit.get(),
                resource("materials/eden_fruit"),
                snakeFang,
                FrameType.TASK,
                builder -> builder.addCriterion("has_eden_fruit", hasItem(DreamtinkerCommon.eden_fruit.get()))
        );

        CompoundTag ruinWheelSteelNbt = new CompoundTag();
        ruinWheelSteelNbt.putString("Material", DreamtinkerMaterialIds.RuinWheelSteel.toString());

        ItemStack ruinSteel = new ItemStack(TinkerToolParts.fakeIngot.get());
        ruinSteel.setTag(ruinWheelSteelNbt);

        builderHidden(
                ruinSteel,
                resource("materials/ruin_wheel"),
                materialsRoot,
                FrameType.CHALLENGE,
                builder -> builder.addCriterion(
                        "has_ruin_wheel_steel_material",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                ToolStackItemPredicate.ofContext(new HasMaterialPredicate(DreamtinkerMaterialIds.RuinWheelSteel)))
                )
        );
    }

    private Advancement builderSilentRoot(ItemLike icon, ResourceLocation id, ResourceLocation background, FrameType frame, Consumer<Advancement.Builder> consumer) {
        Advancement.Builder builder = Advancement.Builder.advancement();
        builder.display(
                new ItemStack(icon),
                Component.translatable(makeTranslationKey(id) + ".title"),
                Component.translatable(makeTranslationKey(id) + ".description"),
                background,
                frame,
                false,
                false,
                false
        );
        consumer.accept(builder);
        return builder.save(advancementConsumer, id.toString());
    }

    private Advancement builderSilent(ItemLike icon, ResourceLocation id, Advancement parent, FrameType frame, Consumer<Advancement.Builder> consumer) {
        return builderSilent(new ItemStack(icon), id, parent, frame, consumer);
    }

    private Advancement builderSilent(ItemStack icon, ResourceLocation id, Advancement parent, FrameType frame, Consumer<Advancement.Builder> consumer) {
        Advancement.Builder builder = Advancement.Builder.advancement();
        builder.parent(parent);
        builder.display(
                icon,
                Component.translatable(makeTranslationKey(id) + ".title"),
                Component.translatable(makeTranslationKey(id) + ".description"),
                null,
                frame,
                false,
                false,
                false
        );
        consumer.accept(builder);
        return builder.save(advancementConsumer, id.toString());
    }

    protected Advancement builder(ItemLike display, ResourceLocation name, Advancement parent, FrameType frame, Consumer<Advancement.Builder> consumer) {
        return builder(new ItemStack(display), name, parent, frame, consumer);
    }

    protected Advancement builder(ItemStack display, ResourceLocation name, Advancement parent, FrameType frame, Consumer<Advancement.Builder> consumer) {
        Advancement.Builder builder = Advancement.Builder.advancement();
        builder.parent(parent);
        builder.display(
                display,
                Component.translatable(makeTranslationKey(name) + ".title"),
                Component.translatable(makeTranslationKey(name) + ".description"),
                null,
                frame,
                true,
                frame != FrameType.TASK,
                false
        );
        consumer.accept(builder);
        return builder.save(advancementConsumer, name.toString());
    }

    protected Advancement builderRoot(ItemStack display, ResourceLocation name, @Nullable ResourceLocation background, FrameType frame, Consumer<Advancement.Builder> consumer) {
        Advancement.Builder builder = Advancement.Builder.advancement();
        builder.display(
                display,
                Component.translatable(makeTranslationKey(name) + ".title"),
                Component.translatable(makeTranslationKey(name) + ".description"),
                background,
                frame,
                true,
                frame != FrameType.TASK,
                false
        );
        consumer.accept(builder);
        return builder.save(advancementConsumer, name.toString());
    }

    private Advancement builderHidden(ItemStack icon, ResourceLocation id, Advancement parent, FrameType frame, Consumer<Advancement.Builder> consumer) {
        Advancement.Builder builder = Advancement.Builder.advancement();
        builder.parent(parent);
        builder.display(
                icon,
                Component.translatable(makeTranslationKey(id) + ".title"),
                Component.translatable(makeTranslationKey(id) + ".description"),
                null,
                frame,
                true,
                true,
                true
        );
        consumer.accept(builder);
        return builder.save(advancementConsumer, id.toString());
    }

    private CriterionTriggerInstance hasItem(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item).build());
    }

    private static String makeTranslationKey(ResourceLocation advancement) {
        return "advancements." + advancement.getNamespace() + "." + advancement.getPath().replace('/', '.');
    }

    protected ResourceLocation resource(String name) {
        return Dreamtinker.getLocation(name);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Set<ResourceLocation> set = Sets.newHashSet();

        record Conditional(ResourceLocation id, ConditionalAdvancement.Builder builder) {}

        List<Advancement> advancements = new ArrayList<>();
        List<Conditional> conditionals = new ArrayList<>();

        this.advancementConsumer = advancement -> {
            if (!set.add(advancement.getId())){
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            }
            advancements.add(advancement);
        };

        this.conditionalConsumer = (id, advancement) -> {
            if (!set.add(id)){
                throw new IllegalStateException("Duplicate advancement " + id);
            }
            conditionals.add(new Conditional(id, advancement));
        };

        generate();

        return allOf(Stream.concat(
                advancements.stream().map(advancement -> saveJson(cache, advancement.getId(), advancement.deconstruct().serializeToJson())),
                conditionals.stream().map(conditional -> saveJson(cache, conditional.id, conditional.builder.write()))
        ));
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Advancements";
    }
}