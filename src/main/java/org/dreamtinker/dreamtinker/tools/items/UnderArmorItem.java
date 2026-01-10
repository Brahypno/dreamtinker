package org.dreamtinker.dreamtinker.tools.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.ModList;
import org.dreamtinker.dreamtinker.utils.CompactUtils.arsNovaUtils;
import org.dreamtinker.dreamtinker.utils.model.SideAwareArmorModel;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.helper.ArmorUtil;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

import static org.dreamtinker.dreamtinker.Dreamtinker.configCompactDisabled;

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
