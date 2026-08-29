package org.brahypno.dreamtinker.plugin.JEI.goety;

import com.Polarice3.Goety.api.ritual.RitualType;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.research.ResearchScroll;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

/**
 * JEI view of a dynamic Goety ritual. The center and result always use the same part item.
 */
public final class GoetyTransmutationCategory implements IRecipeCategory<GoetyTransmutationJeiDisplay> {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 140;
    private static final int RITUAL_CENTER_X = 56;
    private static final int RITUAL_CENTER_Y = 72;
    private static final int OUTPUT_OFFSET_X = 75;
    private static final int[][] PEDESTAL_POSITIONS = {
            {56, 42}, {86, 72}, {56, 102}, {26, 72},
            {71, 42}, {86, 42}, {41, 102}, {26, 102},
            {41, 42}, {86, 102}, {71, 102}, {26, 42}
    };
    private static final Map<String, ItemStack> RESEARCH_SCROLL_CACHE = new HashMap<>();
    private static boolean researchScrollCacheBuilt;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable arrow;
    private final ItemStack darkAltar;
    private final ItemStack pedestal;

    public GoetyTransmutationCategory(IGuiHelper gui) {
        background = gui.createBlankDrawable(WIDTH, HEIGHT);
        darkAltar = renderFull(new ItemStack(ModBlocks.DARK_ALTAR.get()));
        pedestal = renderFull(new ItemStack(ModItems.PEDESTAL_DUMMY.get()));
        icon = gui.createDrawableItemStack(darkAltar);
        arrow = gui.createDrawable(new ResourceLocation("goety", "textures/gui/jei/arrow.png"), 0, 0, 64, 46);
    }

    @Override
    public RecipeType<GoetyTransmutationJeiDisplay> getRecipeType() {
        return GoetyJeiCompat.RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.dreamtinker.goety_material_transmutation");
    }

    @Override
    @SuppressWarnings("removal")
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, GoetyTransmutationJeiDisplay display, IFocusGroup focuses) {
        layout.addSlot(RecipeIngredientRole.INPUT, RITUAL_CENTER_X, RITUAL_CENTER_Y - 15)
              .addItemStacks(display.inputs());
        layout.addSlot(RecipeIngredientRole.CATALYST, RITUAL_CENTER_X, RITUAL_CENTER_Y)
              .addItemStack(darkAltar);
        for (int index = 0; index < display.cost(); index++) {
            int[] position = PEDESTAL_POSITIONS[index];
            layout.addSlot(RecipeIngredientRole.INPUT, position[0], position[1] - 5)
                  .addIngredients(display.recipe().unitInput());
            layout.addSlot(RecipeIngredientRole.RENDER_ONLY, position[0], position[1])
                  .addItemStack(pedestal);
        }
        layout.addSlot(RecipeIngredientRole.OUTPUT, RITUAL_CENTER_X + OUTPUT_OFFSET_X, RITUAL_CENTER_Y - 15)
              .addItemStacks(display.outputs());
        layout.addSlot(RecipeIngredientRole.CATALYST, RITUAL_CENTER_X + OUTPUT_OFFSET_X, RITUAL_CENTER_Y)
              .addItemStack(darkAltar);
        layout.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0)
              .addItemStack(craftTypeIcon(display));
        ItemStack researchScroll = researchScroll(display.recipe().getResearch());
        if (!researchScroll.isEmpty()){
            layout.addSlot(RecipeIngredientRole.CATALYST, 0, 16)
                  .addItemStack(researchScroll);
        }
    }

    private static ItemStack renderFull(ItemStack stack) {
        stack.getOrCreateTag().putBoolean("RenderFull", true);
        return stack;
    }

    private static ItemStack craftTypeIcon(GoetyTransmutationJeiDisplay display) {
        for (var ritualType : RitualType.getAllRitualType()) {
            if (display.recipe().getCraftType().equals(ritualType.getName())){
                return ritualType.getJeiIcon();
            }
        }
        return new ItemStack(Items.OBSIDIAN);
    }

    private static ItemStack researchScroll(String researchId) {
        if (researchId == null || researchId.isEmpty()){
            return ItemStack.EMPTY;
        }
        if (!researchScrollCacheBuilt){
            for (var item : ForgeRegistries.ITEMS.getValues()) {
                if (item instanceof ResearchScroll scroll && scroll.research != null){
                    RESEARCH_SCROLL_CACHE.putIfAbsent(scroll.research.getId(), new ItemStack(scroll));
                }
            }
            researchScrollCacheBuilt = true;
        }
        return RESEARCH_SCROLL_CACHE.getOrDefault(researchId, ItemStack.EMPTY);
    }

    @Override
    public void draw(
            GoetyTransmutationJeiDisplay display, IRecipeSlotsView slots, GuiGraphics graphics,
            double mouseX, double mouseY) {
        RenderSystem.enableBlend();
        arrow.draw(graphics, RITUAL_CENTER_X + OUTPUT_OFFSET_X - 20, RITUAL_CENTER_Y);
        RenderSystem.disableBlend();

        var font = Minecraft.getInstance().font;
        Component craftType = Component.literal(Component.translatable("jei.goety.craftType").getString())
                                       .append(Component.translatable("jei.goety.craftType." + display.recipe().getCraftType()));
        drawCentered(graphics, font, craftType, 5);
        drawCentered(graphics, font,
                     Component.translatable("jei.goety.soulCost", display.recipe().getSoulCost()), 120);
        drawCentered(graphics, font,
                     Component.translatable("jei.goety.duration", display.recipe().getDuration()), 130);
    }

    private static void drawCentered(
            GuiGraphics graphics, net.minecraft.client.gui.Font font,
            Component text, int y) {
        graphics.drawString(font, text, (WIDTH - font.width(text)) / 2, y, 0, false);
    }
}
