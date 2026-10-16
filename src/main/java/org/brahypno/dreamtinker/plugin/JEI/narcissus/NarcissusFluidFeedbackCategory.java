package org.brahypno.dreamtinker.plugin.JEI.narcissus;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
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

public final class NarcissusFluidFeedbackCategory extends AbstractRecipeCategory<NarcissusFluidFeedbackCache.Page> {
    private static final int X = 6;
    private static final int Y = 5;
    private static final int ROW_H = 18;
    private static final int WIDTH = 170;
    private static final int NAME_X = 28;
    private static final int CATEGORY_X = 94;
    private static final int FEEDBACK_X = 132;

    public NarcissusFluidFeedbackCategory(IGuiHelper helper) {
        super(DTJeiPlugin.NARCISSUS_FEEDBACK, Component.translatable("jei.dreamtinker.narcissus_feedback.title"),
              helper.createDrawableIngredient(ForgeTypes.FLUID_STACK, new FluidStack(Fluids.WATER, 1000)), WIDTH,
              Y + NarcissusFluidFeedbackCache.ROWS * ROW_H + 1);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, NarcissusFluidFeedbackCache.Page recipe, @NotNull IFocusGroup focuses) {
        List<NarcissusFluidFeedbackCache.Entry> entries = recipe.entries();
        for (int i = 0; i < entries.size(); i++) {
            NarcissusFluidFeedbackCache.Entry entry = entries.get(i);
            builder.addSlot(RecipeIngredientRole.INPUT, X, Y + i * ROW_H)
                   .addIngredient(ForgeTypes.FLUID_STACK, entry.fluid())
                   .setFluidRenderer(1000, false, 16, 16)
                   .addRichTooltipCallback((recipeSlotView, tooltip) -> addEntryTooltip(tooltip, entry));
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStack(entry.source());
        }
    }


    @Override
    public void draw(NarcissusFluidFeedbackCache.Page recipe, @NotNull IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        List<NarcissusFluidFeedbackCache.Entry> entries = recipe.entries();

        for (int i = 0; i < entries.size(); i++) {
            NarcissusFluidFeedbackCache.Entry entry = entries.get(i);
            int y = Y + i * ROW_H;

            drawTextTrimmed(graphics, minecraft, entry.fluid().getDisplayName(), NAME_X, y + 4, CATEGORY_X - NAME_X - 6, 0x404040);
            drawTextTrimmed(graphics, minecraft, Component.translatable(categoryShortKey(entry)),
                            CATEGORY_X, y + 4, FEEDBACK_X - CATEGORY_X - 4, 0x606060);
            drawFeedback(graphics, minecraft, entry, FEEDBACK_X, y);
        }
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

        drawTextTrimmed(graphics, minecraft, Component.translatable(modeShortKey(entry)), x, y + 5, WIDTH - x - 4, 0x404040);
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

    @Override
    public void getTooltip(
            ITooltipBuilder tooltip, NarcissusFluidFeedbackCache.Page recipe, @NotNull IRecipeSlotsView slots,
            double mouseX, double mouseY) {
        // Let JEI draw the tooltip after the recipe slots, including text-only feedback.
        if (mouseX < NAME_X || mouseX >= WIDTH || mouseY < Y || mouseY >= Y + recipe.entries().size() * ROW_H){
            return;
        }

        int row = (int) ((mouseY - Y) / ROW_H);
        NarcissusFluidFeedbackCache.Entry entry = recipe.entries().get(row);
        tooltip.add(entry.fluid().getDisplayName());
        addEntryTooltip(tooltip, entry);
    }

    private static void addEntryTooltip(ITooltipBuilder tooltip, NarcissusFluidFeedbackCache.Entry entry) {
        tooltip.add(Component.translatable("jei.dreamtinker.narcissus_feedback.category",
                                           Component.translatable(categoryKey(entry))).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(modeKey(entry)).withStyle(ChatFormatting.YELLOW));

        if (!entry.display().effects().isEmpty()){
            List<Component> effects = new ArrayList<>();
            PotionUtils.addPotionTooltip(entry.display().effects(), effects, 1.0F);
            tooltip.addAll(effects);
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
