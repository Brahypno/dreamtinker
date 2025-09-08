package org.dreamtinker.dreamtinker.utils.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager.ArmorModel;
import slimeknights.tconstruct.library.client.armor.MultilayerArmorModel;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier;

import java.util.ArrayList;
import java.util.List;

/**
 * 多层护甲模型（支持 plating/maille 分层 & LEGS/FEET 左右侧过滤）
 */
public class SideAwareArmorModel extends MultilayerArmorModel {
    public record SideMaskConfig(
            boolean platingLeftLeg, boolean platingRightLeg,
            boolean platingLeftBoot, boolean platingRightBoot,
            boolean mailleLeftLeg, boolean mailleRightLeg,
            boolean mailleLeftBoot, boolean mailleRightBoot
    ) {
        public static SideMaskConfig defaults() {
            // 示例：plating 只显示右腿/右鞋；maille 两侧都显示
            return new SideMaskConfig(false, true, true, true, false, true, true, false);
        }

        public static SideMaskConfig fromStack(ItemStack stack) {
            // TODO 如需动态开关，改为读 NBT；这里先给默认
            return defaults();
        }
    }

    public static final SideAwareArmorModel INSTANCE = new SideAwareArmorModel();

    private EquipmentSlot currentSlot = EquipmentSlot.CHEST;
    private SideMaskConfig mask = SideMaskConfig.defaults();
    private List<LayerKind> kinds = List.of();

    private enum LayerKind {PLATING, MAILLE, OTHER}

    /**
     * 按你的注册顺序把 layers 分类：0=plating，1=maille，其它=other
     */
    private static List<LayerKind> classify(List<ArmorTextureSupplier> layers) {
        List<LayerKind> out = new ArrayList<>(layers.size());
        for (int i = 0; i < layers.size(); i++) {
            out.add(i == 0 ? LayerKind.PLATING : (i == 1 ? LayerKind.MAILLE : LayerKind.OTHER));
        }
        return out;
    }

    @Override
    public Model setup(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> base, ArmorModel model) {
        this.currentSlot = slot;
        this.mask = SideMaskConfig.fromStack(stack);
        this.kinds = classify(model.layers());     // 也可换成“按 Supplier 类型判断”
        return super.setup(living, stack, slot, base, model);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer vc, int light, int overlay, float r, float g, float b, float a) {
        if (this.base == null || buffer == null)
            return;

        boolean armorGlint = hasGlint;
        boolean wingGlint = hasGlint;

        final boolean isLegSlot = (currentSlot == EquipmentSlot.LEGS && textureType == ArmorTextureSupplier.TextureType.LEGGINGS);
        final boolean isFeetSlot = (currentSlot == EquipmentSlot.FEET && textureType == ArmorTextureSupplier.TextureType.ARMOR);

        // 这里必须是 HumanoidModel —— 不要再换成自定义 Model 了
        final HumanoidModel<?> hmodel = this.base;

        for (int i = 0; i < model.layers().size(); i++) {
            ArmorTextureSupplier sup = model.layers().get(i);
            ArmorTextureSupplier.ArmorTexture tex = sup.getArmorTexture(armorStack, textureType, registryAccess);
            if (tex != ArmorTextureSupplier.ArmorTexture.EMPTY){
                // 默认不改（对头/胸槽位）
                HumanoidModel<?> target = hmodel;

                // 仅在腿/脚槽位做左右过滤
                boolean oldL = hmodel.leftLeg.visible, oldR = hmodel.rightLeg.visible;
                if ((isLegSlot || isFeetSlot)){
                    // 按注册顺序分类：0=plating, 1=maille, 其余=other
                    LayerKind kind = (i < kinds.size() ? kinds.get(i) : LayerKind.OTHER);

                    boolean showL = true, showR = true;
                    if (kind == LayerKind.PLATING){
                        showL = isLegSlot ? mask.platingLeftLeg() : mask.platingLeftBoot();
                        showR = isLegSlot ? mask.platingRightLeg() : mask.platingRightBoot();
                    }else if (kind == LayerKind.MAILLE){
                        showL = isLegSlot ? mask.mailleLeftLeg() : mask.mailleLeftBoot();
                        showR = isLegSlot ? mask.mailleRightLeg() : mask.mailleRightBoot();
                    }

                    // ★ 关键：临时切可见性，渲完恢复
                    hmodel.leftLeg.visible = showL;
                    hmodel.rightLeg.visible = showR;
                    try {
                        tex.renderTexture(target, pose, buffer, light, overlay, r, g, b, a, armorGlint);
                    }
                    finally {
                        hmodel.leftLeg.visible = oldL;
                        hmodel.rightLeg.visible = oldR;
                    }
                }else {
                    // 非腿/脚槽位，直接渲染
                    tex.renderTexture(target, pose, buffer, light, overlay, r, g, b, a, armorGlint);
                }
                armorGlint = false;
            }

            // 翅膀保持原逻辑
            if (hasWings){
                var wing = sup.getArmorTexture(armorStack, ArmorTextureSupplier.TextureType.WINGS, registryAccess);
                if (wing != ArmorTextureSupplier.ArmorTexture.EMPTY){
                    renderWings(pose, light, overlay, wing, r, g, b, a, wingGlint);
                    wingGlint = false;
                }
            }
        }
    }


    /**
     * 代理模型：只把选中的腿渲染出去，其它部位不画
     */
    private static final class LegFilteredModel<T extends LivingEntity> extends Model {
        private final HumanoidModel<T> base;
        private final boolean left, right;

        @SuppressWarnings("unchecked")
        LegFilteredModel(HumanoidModel<?> base, boolean left, boolean right) {
            super(RenderType::entityCutoutNoCull); // 实际不使用此函数，安全占位
            this.base = (HumanoidModel<T>) base;
            this.left = left;
            this.right = right;
        }

        @Override
        public void renderToBuffer(PoseStack pose, VertexConsumer vc, int light, int overlay, float r, float g, float b, float a) {
            if (left)
                base.leftLeg.render(pose, vc, light, overlay, r, g, b, a);
            if (right)
                base.rightLeg.render(pose, vc, light, overlay, r, g, b, a);
        }
    }
}



