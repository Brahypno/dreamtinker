package org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaBook;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.Color;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.buttons.CraftingButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GlyphButton;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.common.spell.validation.CombinedSpellValidator;
import com.hollingsworth.arsnouveau.common.spell.validation.GlyphMaxTierValidator;
import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ModifiableGuiSpellBook extends GuiSpellBook {
    private boolean twoRows;
    private final int next_row_limit = 4;

    public ModifiableGuiSpellBook(InteractionHand hand) {
        super(hand);
        ItemStack heldStack;
        if (Minecraft.getInstance().player != null){
            heldStack = Minecraft.getInstance().player.getItemInHand(hand);
            if (heldStack.getItem() instanceof ModifiableSpellBook mb){
                this.unlockedSpells =
                        new ArrayList<>(GlyphRegistry.getSpellpartMap().values().stream().filter(AbstractSpellPart::shouldShowInSpellBook).toList());
                int tier = mb.getTier(heldStack).value;
                this.spellValidator = new CombinedSpellValidator(
                        new ISpellValidator[]{ArsNouveauAPI.getInstance().getSpellCraftingSpellValidator(), new GlyphMaxTierValidator(tier)});
                int ui_slots = DTModifierCheck.getItemModifierNum(heldStack, DreamtinkerModifiers.Ids.nova_spell_slots);
                numLinks = Math.min(20, numLinks + ui_slots);
                twoRows = this.numLinks > 10;
            }
        }

    }

    public static void open(InteractionHand hand) {
        Minecraft.getInstance().setScreen(new ModifiableGuiSpellBook(hand));
    }

    public void init() {
        this.bookLeft = this.width / 2 - 145;
        this.bookTop = this.height / 2 - 97;
        int colsPerRow = 10;
        int splitCol = 5;      // 0-4 与 5-9 之间加间隔
        int splitGap = 14;

        int stepX = 24;        // 你原本的 24 * i
        int stepY = 24;        // 行间距：建议与按钮高度接近（按你的按钮尺寸可改 22~26）

        int baseX = this.bookLeft + 19;
        int baseY = this.bookTop + 194 - 47; // 原本那一行的 y（作为第一行）


        // 当需要两行时：把“第一行”上移一行，让“第二行”占用原本 baseY
        int firstRowY = baseY - (twoRows ? stepY : 0);

        for (int i = 0; i < this.numLinks; ++i) {
            int row = i / colsPerRow;   // 0: 第一行(前10个), 1: 第二行(后10个)
            if (row >= 2)
                break;        // 只渲染两行（0..19）

            int col = i % colsPerRow;

            int offset = (col >= splitCol) ? splitGap : 0;

            int x = baseX + stepX * col + offset;
            int y = firstRowY + stepY * row;  // twoRows时：row=0 在 baseY-stepY，row=1 在 baseY

            CraftingButton cell = new CraftingButton(x, y, this::onCraftingSlotClick);
            this.addRenderableWidget(cell);
            this.craftingCells.add(cell);
        }
        int links = numLinks;
        numLinks = 0;
        super.init();
        numLinks = links;
        if (twoRows)
            this.layoutAllGlyphs(0);
    }

    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!twoRows)
            super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        else {
            graphics.blit(background, 0, 0, 0.0F, 0.0F, 290, 194, 290, 194);
            if (this.formTextRow >= 1){
                graphics.drawString(this.font, Component.translatable("ars_nouveau.spell_book_gui.form").getString(),
                                    this.formTextRow > next_row_limit ? 154 : 20,
                                    5 + 18 * (this.formTextRow + (this.formTextRow == 1 ? 0 : 1)), -8355712, false);
            }

            if (this.effectTextRow >= 1){
                graphics.drawString(this.font, Component.translatable("ars_nouveau.spell_book_gui.effect").getString(),
                                    this.effectTextRow > next_row_limit ? 154 : 20,
                                    5 + 18 * (this.effectTextRow + 1 - (this.effectTextRow > next_row_limit ? next_row_limit + 1 : 0)), -8355712, false);
            }

            if (this.augmentTextRow >= 1){
                graphics.drawString(this.font, Component.translatable("ars_nouveau.spell_book_gui.augment").getString(),
                                    this.augmentTextRow > next_row_limit ? 154 : 20,
                                    5 + 18 * (this.augmentTextRow + 1 - (this.augmentTextRow > next_row_limit ? next_row_limit + 1 : 0)), -8355712, false);
            }

            graphics.blit(new ResourceLocation("ars_nouveau", "textures/gui/spell_name_paper.png"), 16, 179, 0.0F, 0.0F, 109, 15, 109, 15);
            graphics.blit(new ResourceLocation("ars_nouveau", "textures/gui/search_paper.png"), 203, 0, 0.0F, 0.0F, 72, 15, 72, 15);
            graphics.blit(new ResourceLocation("ars_nouveau", "textures/gui/clear_paper.png"), 161, 179, 0.0F, 0.0F, 47, 15, 47, 15);
            graphics.blit(new ResourceLocation("ars_nouveau", "textures/gui/create_paper.png"), 216, 179, 0.0F, 0.0F, 56, 15, 56, 15);
            if (this.validationErrors.isEmpty()){
                graphics.drawString(this.font, Component.translatable("ars_nouveau.spell_book_gui.create"), 233, 183, -8355712, false);
            }else {
                Component textComponent = Component.translatable("ars_nouveau.spell_book_gui.create").withStyle((s) -> s.withStrikethrough(true).withColor(
                        TextColor.parseColor("#FFB2B2")));
                graphics.drawString(this.font, textComponent, 233, 183, -8355712, false);
            }

            graphics.drawString(this.font, Component.translatable("ars_nouveau.spell_book_gui.clear").getString(), 177, 183, -8355712, false);
            int manaLength = 96;
            if (this.maxManaCache > 0){
                manaLength =
                        (int) Mth.clamp((float) manaLength * ((float) (this.maxManaCache - getCurrentCostCacheReflect()) / (float) this.maxManaCache), -1.0F,
                                        96.0F);
            }else {
                manaLength = 0;
            }

            int offsetLeft = 89;
            int yOffset = 210;
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.scale(1.2F, 1.2F, 1.2F);
            poseStack.translate(-25.0F, -30.0F, 0.0F);
            graphics.blit(new ResourceLocation("ars_nouveau", "textures/gui/manabar_gui_border.png"), offsetLeft, yOffset - 18, 0.0F, 0.0F, 108, 18, 256, 256);
            int manaOffset = (int) (((float) ClientInfo.ticksInGame + partialTicks) / 3.0F % 33.0F) * 6;
            if (manaLength >= 0){
                graphics.blit(new ResourceLocation("ars_nouveau", "textures/gui/manabar_gui_mana.png"), offsetLeft + 9, yOffset - 9, 0.0F, (float) manaOffset,
                              manaLength, 6, 256, 256);
            }else {
                RenderSystem.setShaderTexture(0, new ResourceLocation("ars_nouveau", "textures/gui/manabar_gui_grayscale.png"));
                RenderUtils.colorBlit(graphics.pose(), offsetLeft + 8, yOffset - 10, 0, manaOffset, 100, 8, 256, 256,
                                      manaLength < 0 ? Color.RED : Color.rainbowColor(ClientInfo.ticksInGame));
            }

            if (ArsNouveauAPI.ENABLE_DEBUG_NUMBERS && this.minecraft != null){
                String text = getCurrentCostCacheReflect() + "  /  " + this.maxManaCache;
                int maxWidth = this.minecraft.font.width(this.maxManaCache + "  /  " + this.maxManaCache);
                int offset = offsetLeft - maxWidth / 2 + (maxWidth - this.minecraft.font.width(text));
                graphics.drawString(this.minecraft.font, text, offset + 55, yOffset - 10, 16777215, false);
            }

            graphics.blit(new ResourceLocation("ars_nouveau", "textures/gui/manabar_gui_border.png"), offsetLeft, yOffset - 17, 0.0F, 18.0F, 108, 20, 256, 256);
            poseStack.popPose();
        }
    }

    private static Field CURRENT_COST_CACHE;

    private static Field costField() {
        if (CURRENT_COST_CACHE == null){
            try {
                Field f = GuiSpellBook.class.getDeclaredField("currentCostCache");
                f.setAccessible(true);
                CURRENT_COST_CACHE = f;
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException("Cannot access GuiSpellBook#currentCostCache", e);
            }
        }
        return CURRENT_COST_CACHE;
    }

    /**
     * 只读获取 currentCostCache
     */
    public int getCurrentCostCacheReflect() {
        try {
            return costField().getInt(this);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void layoutAllGlyphs(int page) {
        this.clearButtons(this.glyphButtons);
        this.formTextRow = 0;
        this.augmentTextRow = 0;
        this.effectTextRow = 0;
        int PER_ROW = 6;
        int MAX_ROWS = 6;
        boolean nextPage = false;
        int xStart = nextPage ? this.bookLeft + 154 : this.bookLeft + 20;
        int adjustedRowsPlaced = 0;
        int yStart = this.bookTop + 20;
        boolean foundForms = false;
        boolean foundAugments = false;
        boolean foundEffects = false;
        List<AbstractSpellPart> sorted = new ArrayList();
        sorted.addAll(this.displayedGlyphs.stream().filter((s) -> s instanceof AbstractCastMethod).toList());
        sorted.addAll(this.displayedGlyphs.stream().filter((s) -> s instanceof AbstractAugment).toList());
        sorted.addAll(this.displayedGlyphs.stream().filter((s) -> s instanceof AbstractEffect).toList());
        sorted.sort(CreativeTabRegistry.COMPARE_TYPE_THEN_NAME);
        sorted = sorted.subList(this.glyphsPerPage * page, Math.min(sorted.size(), this.glyphsPerPage * (page + 1)));
        int adjustedXPlaced = 0;
        int totalRowsPlaced = 0;
        int row_offset = page == 0 ? 2 : 0;

        for (int i = 0; i < sorted.size(); ++i) {
            AbstractSpellPart part = (AbstractSpellPart) sorted.get(i);
            if (!foundForms && part instanceof AbstractCastMethod){
                foundForms = true;
                ++adjustedRowsPlaced;
                ++totalRowsPlaced;
                this.formTextRow = page != 0 ? 0 : totalRowsPlaced;
                adjustedXPlaced = 0;
            }

            if (!foundAugments && part instanceof AbstractAugment){
                foundAugments = true;
                adjustedRowsPlaced += row_offset;
                totalRowsPlaced += row_offset;
                this.augmentTextRow = page != 0 ? 0 : totalRowsPlaced - 1;
                adjustedXPlaced = 0;
            }else if (!foundEffects && part instanceof AbstractEffect){
                foundEffects = true;
                adjustedRowsPlaced += row_offset;
                totalRowsPlaced += row_offset;
                this.effectTextRow = page != 0 ? 0 : totalRowsPlaced - 1;
                adjustedXPlaced = 0;
            }else if (adjustedXPlaced >= 6){
                ++adjustedRowsPlaced;
                ++totalRowsPlaced;
                adjustedXPlaced = 0;
            }

            if (adjustedRowsPlaced > next_row_limit){
                if (nextPage){
                    break;
                }

                nextPage = true;
                adjustedXPlaced = 0;
                adjustedRowsPlaced = 0;
            }

            int xOffset = 20 * (adjustedXPlaced % 6) + (nextPage ? 134 : 0);
            int yPlace = adjustedRowsPlaced * 18 + yStart;
            GlyphButton cell = new GlyphButton(xStart + xOffset, yPlace, part, this::onGlyphClick);
            this.addRenderableWidget(cell);
            this.glyphButtons.add(cell);
            ++adjustedXPlaced;
        }
    }

    public void resetPageState() {
        super.resetPageState();
        if (twoRows)
            this.layoutAllGlyphs(0);
    }

    public void onPageIncrease(Button button) {
        super.onPageIncrease(button);
        if (this.page + 1 < this.getNumPages()){
            if (twoRows)
                this.layoutAllGlyphs(this.page);
        }
    }

    public void onPageDec(Button button) {
        super.onPageDec(button);
        if (twoRows)
            this.layoutAllGlyphs(this.page);
    }
}
