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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;
import org.brahypno.dreamtinker.fluids.DreamtinkerFluids;
import org.brahypno.dreamtinker.tools.DreamtinkerTools;
import org.brahypno.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.brahypno.esotericismtinker.common.EsotericismTinkerCommon;
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

    /**
     * Advancment consumer instance
     */
    protected Consumer<Advancement> advancementConsumer;
    /**
     * Advancment consumer instance
     */
    protected BiConsumer<ResourceLocation, ConditionalAdvancement.Builder> conditionalConsumer;

    public AdvancementsProvider(PackOutput output) {
        super(output, PackOutput.Target.DATA_PACK, "advancements");
    }

    protected void generate() {
        Advancement dreamtinkerRoot = builderSilentRoot(
                EsotericismTinkerCommon.hypnagogic_transmute.get(),
                resource("root"),
                resource("textures/block/transmute/ashen/ashen_road_back.png"),
                FrameType.TASK,
                builder -> builder.addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
        );


        //tool
        Advancement getSilenceGlove =
                builder(DreamtinkerTools.silence_glove.get().getRenderTool(),
                        resource("tools/silence_glove"),
                        dreamtinkerRoot,
                        FrameType.TASK,
                        builder -> builder.addCriterion(
                                "crafted_silence_glove",
                                hasItem(DreamtinkerTools.silence_glove)
                        ));

        IJsonPredicate<IToolStackView> sixHandPredicate = ToolStackPredicate.and(
                StatInRangePredicate.min(ToolStats.ATTACK_DAMAGE, 9f),
                ToolStackPredicate.set(DreamtinkerTools.silence_glove.get())
        );

        MaterialNBT nbt = MaterialNBT.of(MaterialVariant.of(MaterialIds.iron, ""), MaterialVariant.of(DreamtinkerMaterialIds.black_sapphire, ""),
                                         MaterialVariant.of(DreamtinkerMaterialIds.shadowskin, ""));
        ItemStack silence_glove = ToolBuildHandler.buildItemFromMaterials(DreamtinkerTools.silence_glove.get(), nbt);
        builder(silence_glove,
                resource("tools/six_finger"),
                getSilenceGlove,
                FrameType.GOAL,
                builder -> builder.addCriterion(
                        "damage",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                ToolStackItemPredicate.ofTool(sixHandPredicate)
                        )
                ));

        /*
         * Materials line:
         * raw_stibnite -> valentinite -> nigrescence_antimony
         * -> metallivorous_stibium_lupus -> star_regulus
         */

        Advancement rawStibnite =
                builder(DreamtinkerCommon.raw_stibnite.get(),
                        resource("materials/raw_stibnite"),
                        dreamtinkerRoot,
                        FrameType.TASK,
                        builder -> builder.addCriterion(
                                "has_raw_stibnite",
                                hasItem(DreamtinkerCommon.raw_stibnite.get())
                        ));

        Advancement valentinite =
                builder(DreamtinkerCommon.valentinite.get(),
                        resource("materials/valentinite"),
                        rawStibnite,
                        FrameType.TASK,
                        builder -> builder.addCriterion(
                                "has_valentinite",
                                hasItem(DreamtinkerCommon.valentinite.get())
                        ));

        Advancement nigrescenceAntimony =
                builder(DreamtinkerCommon.nigrescence_antimony.get(),
                        resource("materials/nigrescence_antimony"),
                        valentinite,
                        FrameType.GOAL,
                        builder -> builder.addCriterion(
                                "has_nigrescence_antimony",
                                hasItem(DreamtinkerCommon.nigrescence_antimony.get())
                        ));
        Advancement moltenAlbedoStibium =
                builder(DreamtinkerFluids.molten_albedo_stibium.getBucket(),
                        resource("materials/molten_albedo_stibium"),
                        nigrescenceAntimony,
                        FrameType.GOAL,
                        builder -> builder.addCriterion(
                                "has_molten_albedo_stibium",
                                hasItem(DreamtinkerFluids.molten_albedo_stibium.getBucket())
                        ));

        Advancement metallivorousStibiumLupus =
                builder(DreamtinkerCommon.metallivorous_stibium_lupus.get(),
                        resource("materials/metallivorous_stibium_lupus"),
                        moltenAlbedoStibium,
                        FrameType.GOAL,
                        builder -> builder.addCriterion(
                                "has_metallivorous_stibium_lupus",
                                hasItem(DreamtinkerCommon.metallivorous_stibium_lupus.get())
                        ));
        Advancement moltenAscendingAntimony =
                builder(DreamtinkerFluids.molten_ascending_antimony.getBucket(),
                        resource("materials/molten_ascending_antimony"),
                        metallivorousStibiumLupus,
                        FrameType.GOAL,
                        builder -> builder.addCriterion(
                                "has_molten_ascending_antimony",
                                hasItem(DreamtinkerFluids.molten_ascending_antimony.getBucket())
                        ));

        Advancement liquidSmokyAntimony =
                builder(DreamtinkerFluids.liquid_smoky_antimony.getBucket(),
                        resource("materials/liquid_smoky_antimony"),
                        moltenAscendingAntimony,
                        FrameType.GOAL,
                        builder -> builder.addCriterion(
                                "has_liquid_smoky_antimony",
                                hasItem(DreamtinkerFluids.liquid_smoky_antimony.getBucket())
                        ));

        Advancement starRegulus =
                builder(DreamtinkerCommon.regulus.get(),
                        resource("materials/star_regulus"),
                        liquidSmokyAntimony,
                        FrameType.CHALLENGE,
                        builder -> builder.addCriterion(
                                "has_star_regulus",
                                hasItem(DreamtinkerCommon.regulus.get())
                        ));
        builderHidden(
                new ItemStack(DreamtinkerCommon.regulus.get()),
                resource("materials/magnum_opus"),
                starRegulus,
                FrameType.TASK,
                builder -> builder.addCriterion(
                        "star_regulus",
                        new UsingItemTrigger.TriggerInstance(
                                ContextAwarePredicate.ANY,
                                ItemPredicate.Builder.item()
                                                     .of(DreamtinkerCommon.regulus.get())
                                                     .build()
                        )
                )
        );
        Advancement orichalcumIngot =
                builder(DreamtinkerCommon.orichalcum.get(),
                        resource("materials/orichalcum_ingot"),
                        dreamtinkerRoot,
                        FrameType.TASK,
                        builder -> builder.addCriterion(
                                "has_orichalcum_ingot",
                                hasItem(DreamtinkerCommon.orichalcum.get())
                        ));

        Advancement coldIronIngot =
                builder(DreamtinkerCommon.cold_iron_ingot.get(),
                        resource("materials/cold_iron_ingot"),
                        dreamtinkerRoot,
                        FrameType.TASK,
                        builder -> builder.addCriterion(
                                "has_cold_iron_ingot",
                                hasItem(DreamtinkerCommon.cold_iron_ingot.get())
                        ));

        Advancement transmutationGoldIngot =
                builder(DreamtinkerCommon.transmutation_gold_ingot.get(),
                        resource("materials/transmutation_gold_ingot"),
                        dreamtinkerRoot,
                        FrameType.TASK,
                        builder -> builder.addCriterion(
                                "has_transmutation_gold_ingot",
                                hasItem(DreamtinkerCommon.transmutation_gold_ingot.get())
                        ));

        builderHidden(
                new ItemStack(DreamtinkerCommon.shadow_silver_ingot.get()),
                resource("materials/shadow_silver_ingot"),
                dreamtinkerRoot,
                FrameType.TASK,
                builder -> builder.addCriterion(
                        "has_shadow_silver_ingot",
                        hasItem(DreamtinkerCommon.shadow_silver_ingot.get())
                )
        );

        Advancement snakeFang =
                builder(DreamtinkerCommon.snake_fang.get(),
                        resource("materials/snake_fang"),
                        dreamtinkerRoot,
                        FrameType.TASK,
                        builder -> builder.addCriterion(
                                "has_snake_fang",
                                hasItem(DreamtinkerCommon.snake_fang.get())
                        ));

        builder(DreamtinkerCommon.eden_fruit.get(),
                resource("materials/eden_fruit"),
                snakeFang,
                FrameType.TASK,
                builder -> builder.addCriterion(
                        "has_eden_fruit",
                        hasItem(DreamtinkerCommon.eden_fruit.get())
                ));

        /*
         * Ruin Wheel Steel material icon
         */

        CompoundTag ruinWheelSteelNbt = new CompoundTag();
        ruinWheelSteelNbt.putString("Material", DreamtinkerMaterialIds.RuinWheelSteel.toString());

        ItemStack ruinSteel = new ItemStack(TinkerToolParts.fakeIngot.get());
        ruinSteel.setTag(ruinWheelSteelNbt);

        builderHidden(
                ruinSteel,
                resource("materials/ruin_wheel"),
                dreamtinkerRoot,
                FrameType.CHALLENGE,
                builder -> builder.addCriterion(
                        "has_ruin_wheel_steel_material",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                ToolStackItemPredicate.ofContext(
                                        new HasMaterialPredicate(DreamtinkerMaterialIds.RuinWheelSteel)
                                )
                        )
                )
        );
    }

    private Advancement builderSilentRoot(
            ItemLike icon,
            ResourceLocation id,
            ResourceLocation background,
            FrameType frame,
            Consumer<Advancement.Builder> consumer
    ) {
        Advancement.Builder builder = Advancement.Builder.advancement();

        builder.display(
                new ItemStack(icon),
                Component.translatable("advancements." + id.getNamespace() + "." + id.getPath().replace('/', '.') + ".title"),
                Component.translatable("advancements." + id.getNamespace() + "." + id.getPath().replace('/', '.') + ".description"),
                background,
                frame,
                false, // showToast
                false, // announceToChat
                false  // hidden
        );

        consumer.accept(builder);

        return builder.save(advancementConsumer, id.toString());
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
            }else {
                advancements.add(advancement);
            }
        };
        this.conditionalConsumer = (id, advancement) -> {
            if (!set.add(id)){
                throw new IllegalStateException("Duplicate advancement " + id);
            }else {
                conditionals.add(new Conditional(id, advancement));
            }
        };
        generate();
        return allOf(Stream.concat(
                advancements.stream().map(advancement -> saveJson(cache, advancement.getId(), advancement.deconstruct().serializeToJson())),
                conditionals.stream().map(conditional -> saveJson(cache, conditional.id, conditional.builder.write()))
        ));
    }

    private CriterionTriggerInstance hasItem(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(new ItemLike[]{item}).build());
    }

    protected Advancement builder(ItemLike display, ResourceLocation name, @Nullable ResourceLocation background, FrameType frame, Consumer<Advancement.Builder> consumer) {
        return this.builder(new ItemStack(display), name, background, frame, consumer);
    }

    protected Advancement builder(ItemLike display, ResourceLocation name, Advancement parent, FrameType frame, Consumer<Advancement.Builder> consumer) {
        return this.builder(new ItemStack(display), name, parent, frame, consumer);
    }

    protected Advancement builder(ItemStack display, ResourceLocation name, Advancement parent, FrameType frame, Consumer<Advancement.Builder> consumer) {
        return this.builder(display, name, (ResourceLocation) null, frame, (builder) -> {
            builder.parent(parent);
            consumer.accept(builder);
        });
    }

    protected Advancement builder(ItemStack display, ResourceLocation name, @Nullable ResourceLocation background, FrameType frame, Consumer<Advancement.Builder> consumer) {
        Advancement.Builder var10000 = net.minecraft.advancements.Advancement.Builder.advancement();
        String var10002 = makeTranslationKey(name);
        MutableComponent var7 = Component.translatable(var10002 + ".title");
        String var10003 = makeTranslationKey(name);
        Advancement.Builder builder =
                var10000.display(display, var7, Component.translatable(var10003 + ".description"), background, frame, true, frame != FrameType.TASK, false);
        consumer.accept(builder);
        return builder.save(this.advancementConsumer, name.toString());
    }

    private static String makeTranslationKey(ResourceLocation advancement) {
        String var10000 = advancement.getNamespace();
        return "advancements." + var10000 + "." + advancement.getPath().replace('/', '.');
    }

    protected ResourceLocation resource(String name) {
        return Dreamtinker.getLocation(name);
    }

    private Advancement builderHidden(
            ItemStack icon,
            ResourceLocation id,
            Advancement parent,
            FrameType frame,
            Consumer<Advancement.Builder> consumer
    ) {
        Advancement.Builder builder = Advancement.Builder.advancement();

        builder.parent(parent);

        builder.display(
                icon,
                Component.translatable("advancements." + id.getNamespace() + "." + id.getPath().replace('/', '.') + ".title"),
                Component.translatable("advancements." + id.getNamespace() + "." + id.getPath().replace('/', '.') + ".description"),
                null,
                frame,
                true,   // showToast
                true,   // announceToChat
                true    // hidden
        );

        consumer.accept(builder);

        return builder.save(advancementConsumer, id.toString());
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Advancements";
    }
}
