package org.dreamtinker.dreamtinker.tools.items.UnderArmor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.dreamtinker.dreamtinker.utils.model.SideAwareArmorModel;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.helper.ArmorUtil;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class UnderArmorItem extends ModifiableArmorItem {
    private final ResourceLocation name;

    public UnderArmorItem(ModifiableArmorMaterial material, ArmorItem.Type type, Properties properties) {
        super(material, type, properties);
        this.name = material.getId();
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return ArmorUtil.getDummyArmorTexture(slot);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new ArmorModelManager.ArmorModelDispatcher() {
            @Override
            protected ResourceLocation getName() {
                return name;
            }

            @Nonnull
            @Override
            public Model getGenericArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                return SideAwareArmorModel.INSTANCE.setup(living, stack, slot, original, getModel(stack));
            }
        });
    }

}
