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
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.library.compat.goety.GoetyMaterialTransmutationRecipe;
import org.brahypno.dreamtinker.library.compat.goety.GoetyModifierRitualRecipe;
import org.brahypno.dreamtinker.library.compat.goety.GoetyModifierRitualTarget;
import org.brahypno.dreamtinker.library.compat.goety.GoetyTransmutationTarget;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.plugin.jei.modifiers.ModifierIngredientRenderer;
import slimeknights.tconstruct.plugin.jei.modifiers.SlotIngredientRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JEI view of dynamic Goety rituals that transform or modify the center item.
 */
public final class GoetyTransmutationCategory extends AbstractRecipeCategory<GoetyTransmutationJeiDisplay> {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 154;
    private static final int RITUAL_CENTER_X = 56;
    private static final int RITUAL_CENTER_Y = 72;
    private static final int OUTPUT_OFFSET_X = 75;
    private static final int SOUL_COST_Y = 124;
    private static final int DURATION_Y = 134;
    private static final int[][] PEDESTAL_POSITIONS = {
            {56, 42}, {86, 72}, {56, 102}, {26, 72},
            {71, 42}, {86, 42}, {41, 102}, {26, 102},
            {41, 42}, {86, 102}, {71, 102}, {26, 42}
    };
    private static final Map<String, ItemStack> RESEARCH_SCROLL_CACHE = new HashMap<>();
    private static boolean researchScrollCacheBuilt;

    private final IDrawable arrow;
    private final ItemStack darkAltar;
    private final ItemStack pedestal;
    private final ModifierIngredientRenderer modifierRenderer = new ModifierIngredientRenderer(124, 10);

    public GoetyTransmutationCategory(IGuiHelper gui) {
        super(GoetyJeiCompat.RECIPE_TYPE, Component.translatable("jei.dreamtinker.goety_material_transmutation"),
              createIcon(gui), WIDTH, HEIGHT);
        darkAltar = renderFull(new ItemStack(ModBlocks.DARK_ALTAR.get()));
        pedestal = renderFull(new ItemStack(ModItems.PEDESTAL_DUMMY.get()));
        arrow = gui.createDrawable(new ResourceLocation("goety", "textures/gui/jei/arrow.png"), 0, 0, 64, 46);
    }

    private static IDrawable createIcon(IGuiHelper gui) {
        return gui.createDrawableItemStack(renderFull(new ItemStack(ModBlocks.DARK_ALTAR.get())));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, GoetyTransmutationJeiDisplay display, IFocusGroup focuses) {
        List<ItemStack> inputs = new ArrayList<>(display.inputs());
        List<ItemStack> outputs = new ArrayList<>(display.outputs());
        applyFocusedTool(display, focuses, inputs, outputs);
        layout.addSlot(RecipeIngredientRole.RENDER_ONLY, RITUAL_CENTER_X, RITUAL_CENTER_Y - 15)
              .addItemStacks(inputs);
        layout.addSlot(RecipeIngredientRole.CATALYST, RITUAL_CENTER_X, RITUAL_CENTER_Y)
              .addItemStack(darkAltar);
        for (int index = 0; index < display.pedestalInputs().size(); index++) {
            int[] position = PEDESTAL_POSITIONS[index];
            layout.addSlot(RecipeIngredientRole.INPUT, position[0], position[1] - 5)
                  .addIngredients(display.pedestalInputs().get(index));
            layout.addSlot(RecipeIngredientRole.RENDER_ONLY, position[0], position[1])
                  .addItemStack(pedestal);
        }
        layout.addSlot(RecipeIngredientRole.OUTPUT, RITUAL_CENTER_X + OUTPUT_OFFSET_X, RITUAL_CENTER_Y - 15)
              .addItemStacks(outputs);
        layout.addSlot(RecipeIngredientRole.CATALYST, RITUAL_CENTER_X + OUTPUT_OFFSET_X, RITUAL_CENTER_Y)
              .addItemStack(darkAltar);
        layout.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0)
              .addItemStack(craftTypeIcon(display));
        ItemStack researchScroll = researchScroll(display.recipe().getResearch());
        if (!researchScroll.isEmpty()){
            layout.addSlot(RecipeIngredientRole.CATALYST, 0, 16)
                  .addItemStack(researchScroll);
        }
        if (display.modifier() != null){
            layout.addSlot(RecipeIngredientRole.OUTPUT, (WIDTH - 124) / 2, 15)
                  .setCustomRenderer(TConstructJEIConstants.MODIFIER_TYPE, modifierRenderer)
                  .addIngredient(TConstructJEIConstants.MODIFIER_TYPE,
                                 new ModifierEntry(ModifierManager.getValue(display.modifier()), display.level().min()));
        }
        if (display.slots() != null){
            layout.addSlot(RecipeIngredientRole.INPUT, WIDTH - 34, DURATION_Y - 8)
                  .setCustomRenderer(TConstructJEIConstants.SLOT_TYPE, SlotIngredientRenderer.INPUT)
                  .addIngredient(TConstructJEIConstants.SLOT_TYPE, display.slots());
        }
    }

    private static void applyFocusedTool(
            GoetyTransmutationJeiDisplay display, IFocusGroup focuses,
            List<ItemStack> inputs, List<ItemStack> outputs) {
        ItemStack focused = focuses.getItemStackFocuses(RecipeIngredientRole.INPUT)
                                   .map(focus -> focus.getTypedValue().getIngredient())
                                   .filter(stack -> stack.getItem() instanceof IModifiable
                                                    || stack.getItem() instanceof ToolPartItem)
                                   .findFirst()
                                   .map(stack -> stack.copyWithCount(1))
                                   .orElse(ItemStack.EMPTY);
        if (focused.isEmpty())
            return;

        ItemStack result = ItemStack.EMPTY;
        if (display.recipe() instanceof GoetyModifierRitualRecipe modifierRecipe){
            GoetyModifierRitualTarget target = GoetyModifierRitualTarget.findForDisplay(focused, modifierRecipe);
            if (target != null){
                result = target.result();
            }
        }else if (display.recipe() instanceof GoetyMaterialTransmutationRecipe transmutation){
            var level = Minecraft.getInstance().level;
            if (level != null){
                GoetyTransmutationTarget target = GoetyTransmutationTarget.find(level, focused, transmutation);
                if (target != null && target.cost() == display.pedestalInputs().size()){
                    result = target.result();
                }
            }
        }
        if (!result.isEmpty()){
            inputs.clear();
            inputs.add(focused);
            outputs.clear();
            outputs.add(result);
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
        Component level = levelText(display.level());
        if (level != null){
            graphics.drawString(font, level, (WIDTH - font.width(level)) / 2, 25, 0xFF808080, false);
        }
        drawCentered(graphics, font,
                     Component.translatable("jei.goety.soulCost", display.recipe().getSoulCost()), SOUL_COST_Y);
        drawCentered(graphics, font,
                     Component.translatable("jei.goety.duration", display.recipe().getDuration()), DURATION_Y);
    }

    private static Component levelText(IntRange level) {
        if (level == null)
            return null;
        if (level.min() == 1 && level.max() < ModifierEntry.VALID_LEVEL.max()){
            return Component.translatable("jei.tconstruct.modifiers.level.max", level.max());
        }
        if (level.min() == level.max()){
            return Component.translatable("jei.tconstruct.modifiers.level.exact", level.min());
        }
        if (level.max() == ModifierEntry.VALID_LEVEL.max()){
            return Component.translatable("jei.tconstruct.modifiers.level.min", level.min());
        }
        return Component.translatable("jei.tconstruct.modifiers.level.range", level.min(), level.max());
    }

    private static void drawCentered(
            GuiGraphics graphics, net.minecraft.client.gui.Font font,
            Component text, int y) {
        graphics.drawString(font, text, (WIDTH - font.width(text)) / 2, y, 0, false);
    }
}
