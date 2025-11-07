package org.dreamtinker.dreamtinker.common.data;

import com.google.common.collect.Sets;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.library.json.predicate.tool.StatInRangePredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolStackItemPredicate;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

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

        Advancement getSilenceGlove =
                builder(DreamtinkerTools.silence_glove, resource("tools/silence_glove"), resource("textures/gui/advancement_background.png"), FrameType.TASK,
                        builder ->
                                builder.addCriterion("crafted_book", hasItem(DreamtinkerTools.silence_glove)));

        builder(Items.ZOMBIE_HEAD, resource("tools/six_finger"), getSilenceGlove, FrameType.GOAL, builder ->
                builder.addCriterion("damage", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ToolStackItemPredicate.ofTool(StatInRangePredicate.min(ToolStats.ATTACK_DAMAGE, 10)))));
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
        return InventoryChangeTrigger.TriggerInstance.hasItems(new ItemPredicate[]{ItemPredicate.Builder.item().of(new ItemLike[]{item}).build()});
    }

    protected Advancement builder(ItemLike display, ResourceLocation name, @Nullable ResourceLocation background, FrameType frame, Consumer<Advancement.Builder> consumer) {
        return this.builder(new ItemStack(display), name, background, frame, consumer);
    }

    protected Advancement builder(ItemLike display, ResourceLocation name, Advancement parent, FrameType frame, Consumer<Advancement.Builder> consumer) {
        return this.builder(new ItemStack(display), name, parent, frame, consumer);
    }

    protected Advancement builder(ItemStack display, ResourceLocation name, Advancement parent, FrameType frame, Consumer<Advancement.Builder> consumer) {
        return this.builder((ItemStack) display, name, (ResourceLocation) ((ResourceLocation) null), frame, (builder) -> {
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

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Advancements";
    }
}