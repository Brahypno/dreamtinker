package org.dreamtinker.dreamtinker.plugin.JEI;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.recipe.virtual.WorldRitualEntry;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.plugin.jei.MantleJEIConstants;
import slimeknights.mantle.plugin.jei.entity.EntityIngredientRenderer;

import java.util.ArrayList;
import java.util.List;

import static org.dreamtinker.dreamtinker.plugin.JEI.WorldRitualCategory.CelestialTypes.CELESTIAL;

public final class WorldRitualCategory implements IRecipeCategory<WorldRitualEntry> {
    public static final ResourceLocation UID = new ResourceLocation(Dreamtinker.MODID, "jei");
    public static final RecipeType<WorldRitualEntry> WORLD_RITUAL =
            RecipeType.create("dreamtinker", "world_ritual", WorldRitualEntry.class);
    private final IDrawable bg, icon;
    private final Component title = Component.translatable("jei.dreamtinker.category");
    private final EntityIngredientRenderer entityRenderer = new EntityIngredientRenderer(32);
    private final CelestialTypes.CelestialRenderer celestialRenderer;

    public WorldRitualCategory(IGuiHelper g) {
        // 空白背景，自己排
        this.bg = g.createBlankDrawable(170, 90);
        this.icon = g.createDrawableItemLike(Items.GRASS_BLOCK);
        this.celestialRenderer = new CelestialTypes.CelestialRenderer();
    }

    @Override
    public @NotNull RecipeType<WorldRitualEntry> getRecipeType() {return WORLD_RITUAL;}

    @Override
    @SuppressWarnings({"removal"})
    public IDrawable getBackground() {return bg;}

    @Override
    public IDrawable getIcon() {return icon;}

    @Override
    public @NotNull Component getTitle() {return title;}

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder b, WorldRitualEntry r, @NotNull IFocusGroup foci) {
        int x = 8, y = 40;

        // 催化物（物品）
        if (r.catalyst() != null && !r.catalyst().isEmpty()){
            b.addSlot(RecipeIngredientRole.CATALYST, x, y).addIngredients(r.catalyst());
            x += 33;
        }
        // 流体
        if (r.fluid() != null && !r.fluid().isEmpty()){
            b.addSlot(RecipeIngredientRole.INPUT, x, y)
             .addIngredients(ForgeTypes.FLUID_STACK, List.of(r.fluid()))
             .setFluidRenderer(Math.max(1000, r.fluid().getAmount()), false, 16, 16);
            x += 33;
        }
        List<CelestialTypes.CelestialIcon> icons = new ArrayList<>();
        if (Boolean.TRUE.equals(r.daytime())){
            icons.add(CelestialTypes.CelestialIcon.sun());
        }
        if (r.moonPhases() != null && !r.moonPhases().isEmpty()){
            for (int p : r.moonPhases())
                icons.add(CelestialTypes.CelestialIcon.moon(p));
        }
        if (!icons.isEmpty()){
            // RENDER_ONLY：不与物品交互，只负责显示
            b.addSlot(RecipeIngredientRole.RENDER_ONLY, 82, y - 20)
             .setCustomRenderer(CELESTIAL, celestialRenderer)
             .addIngredients(CELESTIAL, icons);
        }
        // 实体（组）——用 Mantle/TCon 的实体成分类型与渲染器
        if (r.entityIngredient() != null){
            IIngredientAcceptor<?> ent = b.addSlot(RecipeIngredientRole.INPUT, x, y - 5)
                                          .setCustomRenderer(MantleJEIConstants.ENTITY_TYPE, entityRenderer)
                                          .addIngredients(MantleJEIConstants.ENTITY_TYPE, r.entityIngredient().getDisplay());
            // 把对应的刷怪蛋作为“隐形输入”，便于 JEI 的聚焦/联动
            IIngredientAcceptor<?> eggs = b.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                                           .addItemStacks(r.entityIngredient().getEggs());
            b.createFocusLink(ent, eggs);
            x += 43;
        }
        // 需要的方块（用物品图标）
        if (r.needBlocksAsItems() != null && !r.needBlocksAsItems().isEmpty()){
            b.addSlot(RecipeIngredientRole.INPUT, x, y)
             .addIngredients(r.needBlocksAsItems());
            x += 33;
        }

        // 输出（物品优先；否则用方块图标）
        if (r.resultItem() != null && !r.resultItem().isEmpty()){
            b.addSlot(RecipeIngredientRole.OUTPUT, 140, y).addItemStack(r.resultItem());
        }else if (r.resultBlockIcon() != null && !r.resultBlockIcon().isEmpty()){
            b.addSlot(RecipeIngredientRole.OUTPUT, 140, y).addItemStack(r.resultBlockIcon());
        }
    }

    @Override
    public void draw(
            WorldRitualEntry recipe,
            @NotNull IRecipeSlotsView slots,
            GuiGraphics graphics,
            double mouseX,
            double mouseY) {
        Font font = Minecraft.getInstance().font;

        // 标题行（字符串版）
        graphics.drawString(font,
                            Component.translatable("jei.dreamtinker.category." + recipe.trigger().name().toLowerCase()),
                            40, 8, 0xFFFFFF, false);
        int y = 70;
        if (recipe.minY() != null){
            graphics.drawString(font, "Min Y: " + recipe.minY(), 8, y, 0xAAFFAA, false);
            y += 10;
        }
        if (recipe.chance() != null){
            graphics.drawString(font,
                                Component.translatable("jei.dreamtinker.chance").append(String.format(": %.1f%%", recipe.chance() * 100)),
                                8, y, 0x00AAFF, false);
            y += 10;
        }
        if (Boolean.TRUE.equals(recipe.drowning())){
            graphics.drawString(font, "Drowning", 8, y, 0xFF6666, false);
        }
    }

    /**
     * 自定义成分类型：太阳 / 月亮相位（用于 JEI 轮播显示）
     */
    public static final class CelestialTypes {
        private CelestialTypes() {}

        /**
         * 成分数据
         */
        public static final class CelestialIcon {
            public enum Kind {sun, moon}

            public final Kind kind;
            public final int phase; // MOON: 0..7; SUN 忽略

            private CelestialIcon(Kind k, int p) {
                this.kind = k;
                this.phase = p;
            }

            public static CelestialIcon sun() {return new CelestialIcon(Kind.sun, 0);}

            public static CelestialIcon moon(int phase) {return new CelestialIcon(Kind.moon, phase);}

            @Override
            public String toString() {return kind == Kind.sun ? "sun" : "moon:" + phase;}
        }

        /**
         * JEI 成分类型声明
         */
        public static final IIngredientType<CelestialIcon> CELESTIAL = new IIngredientType<>() {
            @Override
            public Class<? extends CelestialIcon> getIngredientClass() {return CelestialIcon.class;}
        };

        /**
         * 最小渲染器（直接 blit 原版贴图）
         */
        public static final class CelestialRenderer implements IIngredientRenderer<CelestialIcon> {
            private static final ResourceLocation SUN = new ResourceLocation("minecraft", "textures/environment/sun.png");
            private static final ResourceLocation MOON = new ResourceLocation("minecraft", "textures/environment/moon_phases.png");

            @Override
            public void render(GuiGraphics g, CelestialIcon icon) {
                render(g, icon, 0, 0);
            }

            private static final int TEX_CELL = 16;
            private static final int CROP = 1; // 14×14
            private static final int SRC = TEX_CELL - CROP * 2; // 14
            private static final float SCALE = 1.5f;
            private static final int DRAW = Math.round(SRC * SCALE); // 21

            // 告诉 JEI 这个成分需要多大的可视槽位（给点边距，24×24）
            private static final int SLOT_W = 24;
            private static final int SLOT_H = 24;

            @Override
            public void render(GuiGraphics g, CelestialIcon icon, int x, int y) {
                // 居中偏移（把 21×21 放到 24×24 区域中央）
                int dx = x + (SLOT_W - DRAW) / 2; // 1 或 2
                int dy = y + (SLOT_H - DRAW) / 2;

                // 加法混合：去掉黑底
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

                if (icon.kind == CelestialIcon.Kind.sun){
                    // 从 16×16 里裁 1px 边 -> 绘制 21×21（放大 1.5x）
                    g.blit(SUN, dx, dy, CROP, CROP, DRAW, DRAW, TEX_CELL, TEX_CELL);
                }else {
                    int p = Mth.clamp(icon.phase, 0, 7);
                    int u = (p % 4) * TEX_CELL;
                    int v = (p / 4) * TEX_CELL;
                    // 同样裁 1px，再按 21×21 画
                    g.blit(MOON, dx, dy, u + CROP, v + CROP, DRAW, DRAW, 64, 32);
                }
            }

            @Override
            @SuppressWarnings({"removal"})
            public List<Component> getTooltip(CelestialIcon celestialIcon, TooltipFlag tooltipFlag) {
                return List.of();
            }

            @Override
            public void getTooltip(ITooltipBuilder tooltip, CelestialIcon icon, TooltipFlag flag) {
                if (icon.kind == CelestialIcon.Kind.sun){
                    tooltip.add(Component.translatable("jei.dreamtinker.celestial_type.sun"));
                }else {
                    tooltip.add(Component.translatable("jei.dreamtinker.celestial_type.moon_phase").append(String.valueOf(icon.phase)));
                }
            }
        }

        /**
         * 最小 Helper：提供唯一 ID / 名称 / 复制 等
         */
        public static final class CelestialHelper implements IIngredientHelper<CelestialIcon> {
            private static final String UID_NS = "dreamtinker:celestial";

            @Override
            public IIngredientType<CelestialIcon> getIngredientType() {
                return null;
            }

            @Override
            public @NotNull String getDisplayName(CelestialIcon ingredient) {
                return ingredient.kind == CelestialIcon.Kind.sun ? Component.translatable("jei.dreamtinker.celestial_type.sun").toString() :
                       Component.translatable("jei.dreamtinker.celestial_type.moon_phase").toString() + ingredient.phase;
            }

            @Override
            public @NotNull String getUniqueId(@NotNull CelestialIcon celestialIcon, UidContext uidContext) {
                return UID_NS + "_" + uidContext.toString().toLowerCase(); // dreamtinker:celestial/sun 或 /moon:4
            }

            @Override
            public @NotNull CelestialIcon copyIngredient(CelestialIcon ingredient) {
                return ingredient.kind == CelestialIcon.Kind.sun ? CelestialIcon.sun() : CelestialIcon.moon(ingredient.phase);
            }

            @Override
            public @NotNull String getErrorInfo(CelestialIcon ingredient) {
                return ingredient == null ? "null celestial icon" : ingredient.toString();
            }

            // 下面这些可返回 null/默认：
            @Override
            public ResourceLocation getResourceLocation(CelestialIcon ingredient) {
                return Dreamtinker.getLocation(ingredient.kind.name());
            }


            @Override
            public String getDisplayModId(CelestialIcon ingredient) {return Dreamtinker.MODID;}

            @Override
            public @NotNull Iterable<Integer> getColors(@NotNull CelestialIcon ingredient) {return List.of();}

            @Override
            public boolean isValidIngredient(CelestialIcon ingredient) {return true;}

            @Override
            public boolean isIngredientOnServer(CelestialIcon ingredient) {return true;}
        }
    }
    
}
