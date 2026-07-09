package org.brahypno.dreamtinker.plugin.JEI.narcissus;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.brahypno.dreamtinker.plugin.JEI.DTJeiPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class NarcissusFluidFeedbackCategory implements IRecipeCategory<NarcissusFluidFeedbackCache.Page> {
    private static final int X = 6;
    private static final int Y = 5;
    private static final int ROW_H = 18;

    private final IDrawable bg;
    private final IDrawable icon;
    private final Component title = Component.translatable("jei.dreamtinker.narcissus_feedback.title");

    public NarcissusFluidFeedbackCategory(IGuiHelper helper) {
        this.bg = helper.createBlankDrawable(170, 150);
        this.icon = helper.createDrawableIngredient(ForgeTypes.FLUID_STACK, new FluidStack(Fluids.WATER, 1000));
    }

    @Override
    public @NotNull RecipeType<NarcissusFluidFeedbackCache.Page> getRecipeType() {
        return DTJeiPlugin.NARCISSUS_FEEDBACK;
    }

    @Override
    public @NotNull Component getTitle() {
        return title;
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() {
        return bg;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, NarcissusFluidFeedbackCache.Page recipe, @NotNull IFocusGroup focuses) {
        List<NarcissusFluidFeedbackCache.Entry> entries = recipe.entries();
        for (int i = 0; i < entries.size(); i++) {
            NarcissusFluidFeedbackCache.Entry entry = entries.get(i);
            builder.addSlot(RecipeIngredientRole.INPUT, X, Y + i * ROW_H)
                   .addIngredient(ForgeTypes.FLUID_STACK, entry.fluid())
                   .addRichTooltipCallback((recipeSlotView, tooltip) -> addEntryTooltip(tooltip, entry));
        }
    }


    @Override
    public void draw(NarcissusFluidFeedbackCache.Page recipe, @NotNull IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        List<NarcissusFluidFeedbackCache.Entry> entries = recipe.entries();

        for (int i = 0; i < entries.size(); i++) {
            NarcissusFluidFeedbackCache.Entry entry = entries.get(i);
            int y = Y + i * ROW_H;

            drawTextTrimmed(graphics, minecraft, entry.fluid().getDisplayName(), 28, y + 4, 60, 0x404040);
            graphics.drawString(minecraft.font, Component.translatable(categoryShortKey(entry)).withStyle(ChatFormatting.GRAY), 94, y + 4, 0x606060, false);
            drawFeedback(graphics, minecraft, entry, 132, y);
        }

        drawEffectHoverTooltip(recipe, graphics, mouseX, mouseY);
    }

    private void drawFeedback(GuiGraphics graphics, Minecraft minecraft, NarcissusFluidFeedbackCache.Entry entry, int x, int y) {
        List<MobEffectInstance> effects = entry.display().effects();
        if (!effects.isEmpty()){
            TextureAtlasSprite sprite = minecraft.getMobEffectTextures().get(effects.get(0).getEffect());
            graphics.blit(x, y, 0, 18, 18, sprite);

            if (effects.size() > 1){
                graphics.drawString(minecraft.font, Component.literal("+" + (effects.size() - 1)), x + 9, y + 9, 0xFFFFFF, true);
            }
            return;
        }

        graphics.drawString(minecraft.font, Component.translatable(modeShortKey(entry)), x, y + 5, 0x404040, false);
    }

    private static void drawTextTrimmed(GuiGraphics graphics, Minecraft minecraft, Component text, int x, int y, int width, int color) {
        if (minecraft.font.width(text) <= width){
            graphics.drawString(minecraft.font, text, x, y, color, false);
            return;
        }

        String raw = text.getString();
        String trimmed = minecraft.font.plainSubstrByWidth(raw, width - minecraft.font.width("…")) + "…";
        graphics.drawString(minecraft.font, Component.literal(trimmed), x, y, color, false);
    }

    private void drawEffectHoverTooltip(NarcissusFluidFeedbackCache.Page recipe, GuiGraphics graphics, double mouseX, double mouseY) {
        int row = (int) ((mouseY - Y) / ROW_H);
        if (row < 0 || row >= recipe.entries().size()){
            return;
        }

        int iconX = 132;
        int iconY = Y + row * ROW_H;
        if (mouseX < iconX || mouseX >= iconX + 18 || mouseY < iconY || mouseY >= iconY + 18){
            return;
        }

        NarcissusFluidFeedbackCache.Entry entry = recipe.entries().get(row);
        List<MobEffectInstance> effects = entry.display().effects();
        if (effects.isEmpty()){
            return;
        }

        List<Component> tooltip = new ArrayList<>();
        PotionUtils.addPotionTooltip(effects, tooltip, 1.0F);
        graphics.renderTooltip(Minecraft.getInstance().font, tooltip, Optional.empty(), (int) mouseX, (int) mouseY);
    }

    private static void addEntryTooltip(ITooltipBuilder tooltip, NarcissusFluidFeedbackCache.Entry entry) {
        tooltip.add(Component.translatable("jei.dreamtinker.narcissus_feedback.category",
                                           Component.translatable(categoryKey(entry))).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(modeKey(entry)).withStyle(ChatFormatting.YELLOW));

        List<Component> effects = new ArrayList<>();
        PotionUtils.addPotionTooltip(entry.display().effects(), effects, 1.0F);
        for (Component effectLine : effects) {
            tooltip.add(effectLine);
        }
    }


    private static String modeShortKey(NarcissusFluidFeedbackCache.Entry entry) {
        return modeKey(entry) + ".short";
    }


    private static String categoryKey(NarcissusFluidFeedbackCache.Entry entry) {
        return "jei.dreamtinker.narcissus_feedback.category." + entry.feedback().category().toLowerCase(Locale.ROOT);
    }

    private static String categoryShortKey(NarcissusFluidFeedbackCache.Entry entry) {
        return categoryKey(entry) + ".short";
    }

    private static String modeKey(NarcissusFluidFeedbackCache.Entry entry) {
        return "jei.dreamtinker.narcissus_feedback.mode." + entry.feedback().mode().name().toLowerCase(Locale.ROOT);
    }

}